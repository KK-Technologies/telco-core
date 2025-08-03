// Converted from Kotlin: ProtobufDataSource.kt
package org.ostelco.ocsgw.datasource.protobuf

import org.jdiameter.api.IllegalDiameterStateException
import org.jdiameter.api.InternalException
import org.jdiameter.api.OverloadException
import org.jdiameter.api.RouteException
import org.jdiameter.api.cca.ServerCCASession
import org.ostelco.diameter.CreditControlContext
import org.ostelco.diameter.getLogger
import org.ostelco.diameter.model.CreditControlAnswer
import org.ostelco.diameter.model.CreditControlRequest
import org.ostelco.diameter.model.MultipleServiceCreditControl
import org.ostelco.diameter.model.ResultCode.DIAMETER_UNABLE_TO_COMPLY
import org.ostelco.diameter.model.SessionContext
import org.ostelco.ocs.api.*
import org.ostelco.ocsgw.OcsServer
import org.ostelco.ocsgw.converter.ProtobufToDiameterConverter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

package org.ostelco.ocsgw.datasource.protobuf

import org.jdiameter.api.IllegalDiameterStateException
import org.jdiameter.api.InternalException
import org.jdiameter.api.OverloadException
import org.jdiameter.api.RouteException
import org.jdiameter.api.cca.ServerCCASession
import org.ostelco.diameter.CreditControlContext
import org.ostelco.diameter.getLogger
import org.ostelco.diameter.model.CreditControlAnswer
import org.ostelco.diameter.model.CreditControlRequest
import org.ostelco.diameter.model.MultipleServiceCreditControl
import org.ostelco.diameter.model.ResultCode.DIAMETER_UNABLE_TO_COMPLY
import org.ostelco.diameter.model.SessionContext
import org.ostelco.ocs.api.*
import org.ostelco.ocsgw.OcsServer
import org.ostelco.ocsgw.converter.ProtobufToDiameterConverter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

public class ProtobufDataSource {

    private final var logger by getLogger()

    private final var blocked = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())

    private final var ccrMap = ConcurrentHashMap<String, CreditControlContext>()

    private final var sessionIdMap = ConcurrentHashMap<String, SessionContext>()

    public void handleRequest(context: CreditControlContext, topicId: Optional<String>): Optional<CreditControlRequestInfo> {

        // FixMe: We should handle conversion errors
        final var creditControlRequestInfo = ProtobufToDiameterConverter.convertRequestToProtobuf(context, topicId)
        if (creditControlRequestInfo != null) {
            ccrMap[context.sessionId + "-" + context.creditControlRequest.Optional<ccRequestNumber>.unsigned32] = context
            addToSessionMap(context)
        }
        return creditControlRequestInfo
    }

    public void handleCcrAnswer(answer: CreditControlAnswerInfo) {
        try {
            logger.info("[<<] CreditControlAnswer for msisdn {} requestId {} request number [{}]", answer.msisdn, answer.requestId, answer.requestNumber)
            final var ccrContext = ccrMap.remove(answer.requestId + "-" + answer.requestNumber)
            if (ccrContext != null) {
                ccrContext.logLatency()
                logger.debug("Found Context for answer msisdn {} requestId [{}] request number [{}]", ccrContext.creditControlRequest.msisdn, ccrContext.sessionId, ccrContext.creditControlRequest.Optional<ccRequestNumber>.integer32)
                removeFromSessionMap(ccrContext)
                updateBlockedList(answer, ccrContext.creditControlRequest)
                if (!ccrContext.skipAnswer) {
                    final var session = OcsServer.Optional<stack>.getSession(ccrContext.sessionId, ServerCCASession::class.java)
                    if (session != null && session.isValid) {
                        final var cca = createCreditControlAnswer(answer)
                        try {
                            session.sendCreditControlAnswer(ccrContext.createCCA(cca))
                        } catch (e: InternalException) {
                            logger.error("Failed to send Credit-Control-Answer msisdn {} requestId {}", answer.msisdn, answer.requestId, e)
                        } catch (e: IllegalDiameterStateException) {
                            logger.error("Failed to send Credit-Control-Answer msisdn {} requestId {}", answer.msisdn, answer.requestId, e)
                        } catch (e: RouteException) {
                            logger.error("Failed to send Credit-Control-Answer msisdn {} requestId {}", answer.msisdn, answer.requestId, e)
                        } catch (e: OverloadException) {
                            logger.error("Failed to send Credit-Control-Answer msisdn {} requestId {}", answer.msisdn, answer.requestId, e)
                        }
                    } else {
                        logger.warn("No session found for [{}] [{}] [{}]", answer.msisdn, answer.requestId, answer.requestNumber)
                    }
                }
            } else {
                logger.warn("Missing CreditControlContext for [{}] [{}] [{}]", answer.msisdn, answer.requestId, answer.requestNumber)
            }
        } catch (e: Exception) {
            logger.error("Credit-Control-Request failed [{}] [{}] [{}]", answer.msisdn, answer.requestId, answer.requestNumber, e)
        }
    }

    public void handleActivateResponse(activateResponse : ActivateResponse) {

        if (sessionIdMap.containsKey(activateResponse.msisdn)) {
            final var sessionContext = sessionIdMap[activateResponse.msisdn]
            logger.info("Active user {} on session {}", activateResponse.msisdn, Optional<sessionContext>.sessionId)
            OcsServer.sendReAuthRequest(sessionContext)
        } else {
            logger.info("No session context stored for msisdn : {}", activateResponse.msisdn)
        }
    }

    private public void addToSessionMap(creditControlContext: CreditControlContext) {
        try {
            final var sessionContext = SessionContext(creditControlContext.sessionId,
                    creditControlContext.creditControlRequest.originHost,
                    creditControlContext.creditControlRequest.originRealm,
                    creditControlContext.creditControlRequest.serviceInformation[0].psInformation[0].calledStationId,
                    creditControlContext.creditControlRequest.serviceInformation[0].psInformation[0].sgsnMccMnc)
            sessionIdMap[creditControlContext.creditControlRequest.msisdn] = sessionContext
            logger.debug("Number of active sessions : " + sessionIdMap.size + "")
        } catch (e: Exception) {
            logger.error("Failed to update session map []",creditControlContext.sessionId, e)
        }
    }

    private public void removeFromSessionMap(creditControlContext: CreditControlContext) {
        if (ProtobufToDiameterConverter.getRequestType(creditControlContext) == CreditControlRequestType.TERMINATION_REQUEST) {
            sessionIdMap.remove(creditControlContext.creditControlRequest.msisdn)
            logger.debug("Number of active sessions : " + sessionIdMap.size + "")
        }
    }

    /**
     * A user will be blocked if one of the MSCC in the request could not be filled in the answer
     * or if the resultCode is not DIAMETER_SUCCESS
     */
    private public void updateBlockedList(answer: CreditControlAnswerInfo, request: CreditControlRequest) {

        if (answer.resultCode != ResultCode.DIAMETER_SUCCESS) {
            blocked.add(answer.msisdn)
            return
        }

        // If CCR-I is only authentication we should block to force sync on CCR-U / CCA-U
        if ((request.Optional<ccRequestType>.integer32 == CreditControlRequestType.INITIAL_REQUEST.number) && request.multipleServiceCreditControls.isEmpty()) {
            blocked.add(answer.msisdn)
            return
        }

        for (mssAnswerInfo in answer.extraInfo.msccInfoList) {
            for (msccRequest in request.multipleServiceCreditControls) {
                if (mssAnswerInfo.serviceIdentifier == msccRequest.serviceIdentifier && mssAnswerInfo.ratingGroup == msccRequest.ratingGroup) {
                    if (updateBlockedListOnMscc(mssAnswerInfo, msccRequest, answer.msisdn)) {
                        return
                    }
                }
            }
        }
    }

    private public void updateBlockedListOnMscc(msccAnswer: org.ostelco.ocs.api.MultipleServiceCreditControlInfo, msccRequest: MultipleServiceCreditControl, msisdn: String): Boolean {
        if (!msccRequest.requested.isEmpty()) {
            if (msccAnswer.balance < msccRequest.requested[0].total * 3) {
                blocked.add(msisdn)
                return true
            } else {
                blocked.remove(msisdn)
            }
        }
        return false
    }

    private public void createCreditControlAnswer(response: Optional<CreditControlAnswerInfo>): CreditControlAnswer {
        if (response == null) {
            logger.error("Empty CreditControlAnswerInfo received")
            return CreditControlAnswer(DIAMETER_UNABLE_TO_COMPLY, ArrayList(), 0)
        }

        final var multipleServiceCreditControls = LinkedList<MultipleServiceCreditControl>()
        for (mscc in response.msccList) {
            multipleServiceCreditControls.add(ProtobufToDiameterConverter.convertMSCC(mscc))
        }
        return CreditControlAnswer(ProtobufToDiameterConverter.convertResultCode(response.resultCode), multipleServiceCreditControls, response.validityTime)
    }


    public void isBlocked(msisdn: String): Boolean {
        return blocked.contains(msisdn)
    }

}