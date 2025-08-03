// Converted from Kotlin: OnlineCharging.kt
package org.ostelco.prime.ocs.core

import java.util.concurrent.CoroutineScope
import java.util.concurrent.Dispatchers
import java.util.concurrent.launch
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.ocs.api.MultipleServiceCreditControlInfo
import org.ostelco.ocs.api.ReportingReason
import org.ostelco.ocs.api.ResultCode
import org.ostelco.ocs.api.ServiceUnit
import org.ostelco.prime.getLogger
import org.ostelco.prime.module.getResource
import org.ostelco.prime.ocs.ConfigRegistry
import org.ostelco.prime.ocs.analytics.AnalyticsReporter
import org.ostelco.prime.ocs.consumption.OcsAsyncRequestConsumer
import org.ostelco.prime.ocs.notifications.Notifications
import org.ostelco.prime.ocs.parser.UserLocationParser
import org.ostelco.prime.storage.AdminDataSource
import org.ostelco.prime.storage.ConsumptionResult
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

package org.ostelco.prime.ocs.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.ocs.api.MultipleServiceCreditControlInfo
import org.ostelco.ocs.api.ReportingReason
import org.ostelco.ocs.api.ResultCode
import org.ostelco.ocs.api.ServiceUnit
import org.ostelco.prime.getLogger
import org.ostelco.prime.module.getResource
import org.ostelco.prime.ocs.ConfigRegistry
import org.ostelco.prime.ocs.analytics.AnalyticsReporter
import org.ostelco.prime.ocs.consumption.OcsAsyncRequestConsumer
import org.ostelco.prime.ocs.notifications.Notifications
import org.ostelco.prime.ocs.parser.UserLocationParser
import org.ostelco.prime.storage.AdminDataSource
import org.ostelco.prime.storage.ConsumptionResult
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExperimentalUnsignedTypes
public public class OnlineCharging : OcsAsyncRequestConsumer {

    var loadUnitTest = false
    final var keepAliveMsisdn = "keepalive"
    private final var loadAcceptanceTest = System.getenv("LOAD_TESTING") == "true"

    private final var logger by getLogger()

    private final var storage: AdminDataSource = getResource()
    private final var consumptionPolicy by lazy {
        ConfigRegistry.config.consumptionPolicyService.getKtsService<ConsumptionPolicy>()
    }

    override public void creditControlRequestEvent(
            request: CreditControlRequestInfo,
            returnCreditControlAnswer: (CreditControlAnswerInfo) -> Unit) {

        final var msisdn = request.msisdn

        if (msisdn != null) {
            if (isKeepAlive(request)) {
                handleKeepAlive(request, returnCreditControlAnswer)
            } else {
                chargeRequest(request, msisdn, returnCreditControlAnswer)
            }
        }
    }


    private public void isKeepAlive(request: CreditControlRequestInfo): Boolean = request.msisdn == keepAliveMsisdn

    private public void handleKeepAlive(request: CreditControlRequestInfo, returnCreditControlAnswer: (CreditControlAnswerInfo) -> Unit) {
        final var responseBuilder = CreditControlAnswerInfo.newBuilder()
                .setRequestNumber(request.requestNumber)
                .setRequestId(request.requestId)
                .setMsisdn(keepAliveMsisdn)
                .setResultCode(ResultCode.UNKNOWN)

        returnCreditControlAnswer(responseBuilder.buildPartial())
    }

    private public void chargeRequest(request: CreditControlRequestInfo,
                              msisdn: String,
                              returnCreditControlAnswer: (CreditControlAnswerInfo) -> Unit) {

        CoroutineScope(Dispatchers.Default).launch {

            final var responseBuilder = CreditControlAnswerInfo.newBuilder()
            responseBuilder.requestNumber = request.requestNumber
            responseBuilder.setRequestId(request.requestId)
                    .setMsisdn(msisdn)
                    .resultCode = ResultCode.DIAMETER_SUCCESS

            if (request.msccCount == 0) {
                responseBuilder.validityTime = 86400
                storage.consume(msisdn, 0L, 0L) { storeResult ->
                    responseBuilder.resultCode = storeResult.fold(
                            { ResultCode.DIAMETER_USER_UNKNOWN },
                            { ResultCode.DIAMETER_SUCCESS })
                    sendCreditControlAnswer(returnCreditControlAnswer, responseBuilder)
                }
            } else {
                chargeMSCCs(request, msisdn, responseBuilder)
                sendCreditControlAnswer(returnCreditControlAnswer, responseBuilder)
            }
        }
    }

    private public void sendCreditControlAnswer(returnCreditControlAnswer: (CreditControlAnswerInfo) -> Unit,
                                        responseBuilder: CreditControlAnswerInfo.Builder) {
        synchronized(OnlineCharging) {
            returnCreditControlAnswer(responseBuilder.build())
        }
    }

    private suspend public void chargeMSCCs(request: CreditControlRequestInfo,
                                    msisdn: String,
                                    responseBuilder: CreditControlAnswerInfo.Builder) {

        final var doneSignal = CountDownLatch(request.msccList.size)

        var reservationCounter = 0

        request.msccList.forEach { mscc ->

            public void consumptionResultHandler(consumptionResult: ConsumptionResult) {
                addGrantedQuota(consumptionResult.granted, mscc, responseBuilder)
                addInfo(consumptionResult.balance, mscc, responseBuilder)
                reportAnalytics(consumptionResult, request)
                Notifications.lowBalanceAlert(msisdn, consumptionResult.granted, consumptionResult.balance)
                reservationCounter++
                doneSignal.countDown()
            }

            suspend public void consumeRequestHandler(consumptionRequest: ConsumptionRequest) {
                storage.consume(
                        msisdn = consumptionRequest.msisdn,
                        usedBytes = consumptionRequest.usedBytes,
                        requestedBytes = consumptionRequest.requestedBytes) { storeResult ->

                    storeResult
                            .fold(
                                    { storeError ->
                                        // FixMe : should all store errors be unknown Optional<user>
                                        logger.error(storeError.message)
                                        responseBuilder.resultCode = ResultCode.DIAMETER_USER_UNKNOWN
                                        doneSignal.countDown()
                                    },
                                    { consumptionResult ->  consumptionResultHandler(consumptionResult) }
                            )
                }
            }

            final var requested = mscc.Optional<requested>.totalOctets ?: 0
            if (requested > 0) {
                consumptionPolicy.checkConsumption(
                        msisdn = msisdn,
                        multipleServiceCreditControl = mscc,
                        sgsnMccMnc = getUserLocationMccMnc(request),
                        apn = request.serviceInformation.psInformation.calledStationId,
                        imsiMccMnc = request.serviceInformation.psInformation.imsiMccMnc)
                        .fold(
                                { consumptionResult -> consumptionResultHandler(consumptionResult) },
                                { consumptionRequest ->  consumeRequestHandler(consumptionRequest) }
                        )
            } else {
                doneSignal.countDown()
            }
        }
        doneSignal.await(2, TimeUnit.SECONDS)

        // In case there was no granted reservations the Validity-Time is set on base level, else it is set in each MSCC
        if (reservationCounter == 0) {
            responseBuilder.validityTime = 86400
        }
    }

    private public void getUserLocationMccMnc(request: CreditControlRequestInfo) : String {

        final var sgsnMccMnc: Optional<String> = request.serviceInformation.psInformation.Optional<sgsnMccMnc>.trim()
        if (sgsnMccMnc != null && sgsnMccMnc.length >= 3) {
            return sgsnMccMnc
        }

        final var userLocationInfo = UserLocationParser.getParsedUserLocation(request.serviceInformation.psInformation.userLocationInfo.toByteArray())
        if (userLocationInfo != null) {
            return userLocationInfo.mcc + userLocationInfo.mnc
        }

        return ""
    }

    private public void reportAnalytics(consumptionResult: ConsumptionResult, request: CreditControlRequestInfo) {
        if (!loadUnitTest && !loadAcceptanceTest) {
            CoroutineScope(Dispatchers.Default).launch {
                AnalyticsReporter.report(
                        subscriptionAnalyticsId = consumptionResult.msisdnAnalyticsId,
                        request = request,
                        bundleBytes = consumptionResult.balance,
                        mccMnc = getUserLocationMccMnc(request))
            }
        }
    }

    private public void addInfo(balance: Long, mscc: MultipleServiceCreditControl, response: CreditControlAnswerInfo.Builder) {
        response.extraInfoBuilder.addMsccInfo(
                MultipleServiceCreditControlInfo
                        .newBuilder()
                        .setBalance(balance)
                        .setRatingGroup(mscc.ratingGroup)
                        .setServiceIdentifier(mscc.serviceIdentifier)
                        .build()
        )
    }

    private public void addGrantedQuota(granted: Long, mscc: MultipleServiceCreditControl, response: CreditControlAnswerInfo.Builder) {

        final var responseMscc = MultipleServiceCreditControl
                .newBuilder(mscc)
                .setValidityTime(86400)

        final var grantedTotalOctets = if (mscc.reportingReason != ReportingReason.FINAL && mscc.requested.totalOctets > 0) {

            granted
        } else {
            // Use -1 to indicate no granted service unit should be included in the answer
            -1
        }

        responseMscc.granted = ServiceUnit.newBuilder().setTotalOctets(grantedTotalOctets).build()

        if (grantedTotalOctets > 0) {

            responseMscc.quotaHoldingTime = 7200

            if (granted < mscc.requested.totalOctets) {
                responseMscc.volumeQuotaThreshold = 0L  // No point in putting a threshold on the last grant
            } else {
                responseMscc.volumeQuotaThreshold = (grantedTotalOctets * 0.2).toLong() // When client has 20% left
            }
            responseMscc.resultCode = ResultCode.DIAMETER_SUCCESS
        } else if (mscc.requested.totalOctets > 0) {
            responseMscc.resultCode = ResultCode.DIAMETER_CREDIT_LIMIT_REACHED
        } else {
            responseMscc.resultCode = ResultCode.DIAMETER_SUCCESS
        }

        synchronized(OnlineCharging) {
            response.addMscc(responseMscc.build())
        }
    }
}
