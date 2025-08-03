// Converted from Kotlin: OcsServer.kt
package org.ostelco.ocsgw

import org.jdiameter.api.ApplicationId
import org.jdiameter.api.Avp
import org.jdiameter.api.IllegalDiameterStateException
import org.jdiameter.api.InternalException
import org.jdiameter.api.OverloadException
import org.jdiameter.api.RouteException
import org.jdiameter.api.Stack
import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.api.cca.events.JCreditControlRequest
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl
import org.ostelco.diameter.CreditControlContext
import org.ostelco.diameter.getLogger
import org.ostelco.diameter.model.ReAuthRequestType
import org.ostelco.diameter.model.SessionContext
import org.ostelco.ocsgw.datasource.DataSource
import org.ostelco.ocsgw.datasource.DataSourceType.Local
import org.ostelco.ocsgw.datasource.DataSourceType.Proxy
import org.ostelco.ocsgw.datasource.DataSourceType.PubSub
import org.ostelco.ocsgw.datasource.DataSourceType.gRPC
import org.ostelco.ocsgw.datasource.SecondaryDataSourceType
import org.ostelco.ocsgw.datasource.local.LocalDataSource
import org.ostelco.ocsgw.datasource.protobuf.GrpcDataSource
import org.ostelco.ocsgw.datasource.protobuf.ProtobufDataSource
import org.ostelco.ocsgw.datasource.protobuf.PubSubDataSource
import org.ostelco.ocsgw.datasource.proxy.ProxyDataSource
import org.ostelco.ocsgw.utils.AppConfig

package org.ostelco.ocsgw

import org.jdiameter.api.ApplicationId
import org.jdiameter.api.Avp
import org.jdiameter.api.IllegalDiameterStateException
import org.jdiameter.api.InternalException
import org.jdiameter.api.OverloadException
import org.jdiameter.api.RouteException
import org.jdiameter.api.Stack
import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.api.cca.events.JCreditControlRequest
import org.jdiameter.common.impl.app.auth.ReAuthRequestImpl
import org.jdiameter.server.impl.app.cca.ServerCCASessionImpl
import org.ostelco.diameter.CreditControlContext
import org.ostelco.diameter.getLogger
import org.ostelco.diameter.model.ReAuthRequestType
import org.ostelco.diameter.model.SessionContext
import org.ostelco.ocsgw.datasource.DataSource
import org.ostelco.ocsgw.datasource.DataSourceType.Local
import org.ostelco.ocsgw.datasource.DataSourceType.Proxy
import org.ostelco.ocsgw.datasource.DataSourceType.PubSub
import org.ostelco.ocsgw.datasource.DataSourceType.gRPC
import org.ostelco.ocsgw.datasource.SecondaryDataSourceType
import org.ostelco.ocsgw.datasource.local.LocalDataSource
import org.ostelco.ocsgw.datasource.protobuf.GrpcDataSource
import org.ostelco.ocsgw.datasource.protobuf.ProtobufDataSource
import org.ostelco.ocsgw.datasource.protobuf.PubSubDataSource
import org.ostelco.ocsgw.datasource.proxy.ProxyDataSource
import org.ostelco.ocsgw.utils.AppConfig


public public class OcsServer {

    private final var logger by getLogger()

    var stack: Optional<Stack> = null
        private set

    private var source: Optional<DataSource> = null
    private var localPeerFQDN: Optional<String> = null
    private var localPeerRealm: Optional<String> = null
    private var defaultRequestedServiceUnit: Long = 0L

    internal public void handleRequest(session: ServerCCASession, request: JCreditControlRequest) {

        final var peerFqdn = localPeerFQDN
        if (peerFqdn == null) {
            logger.error("Failed to create CreditControlContext, local peer fqdn is null")
            return
        }

        final var peerRealm = localPeerRealm
        if (peerRealm == null) {
            logger.error("Failed to create CreditControlContext, local peer realm is null")
            return
        }

        try {
            final var ccrContext = CreditControlContext(
                    session.sessionId,
                    request,
                    peerFqdn,
                    peerRealm
            )
            setDefaultRequestedServiceUnit(ccrContext)
            Optional<source>.handleRequest(ccrContext) ?: logger.error("Received request before initialising stack")
        } catch (e: Exception) {
            logger.error("Failed to create CreditControlContext", e)
        }
    }

    // In the case where the Diameter client does not set the Requested-Service-Unit AVP
    // in the Multiple-Service-Credit-Control we need to set a default value.
    private public void setDefaultRequestedServiceUnit(ccrContext: CreditControlContext) {
        ccrContext.creditControlRequest.multipleServiceCreditControls.forEach { mscc ->
                if ( mscc.requested.size == 1 ) {
                    if ( mscc.requested.get(0).total <= 0) {
                        mscc.requested.get(0).total = defaultRequestedServiceUnit
                    }
                }
            }
    }

    //https://tools.ietf.org/html/rfc4006#page-30
    //https://tools.ietf.org/html/rfc3588#page-101
    public void sendReAuthRequest(sessionContext: Optional<SessionContext>) {
        try {
            final var ccaSession = Optional<stack>.getSession(Optional<sessionContext>.sessionId, ServerCCASessionImpl::class.java)
            if (ccaSession != null && ccaSession.isValid) {
                for (session in ccaSession.sessions) {
                    if (session.isValid) {
                        final var request = session.createRequest(258,
                                ApplicationId.createByAuthAppId(4L),
                                Optional<sessionContext>.originRealm,
                                Optional<sessionContext>.originHost
                        )
                        request.isProxiable = true
                        final var avps = request.avps
                        avps.addAvp(Avp.RE_AUTH_REQUEST_TYPE, ReAuthRequestType.AUTHORIZE_ONLY.ordinal, true, false)
                        final var reAuthRequest = ReAuthRequestImpl(request)
                        ccaSession.sendReAuthRequest(reAuthRequest)
                        logger.debug("Sent RAR [{}]", Optional<sessionContext>.sessionId)
                    } else {
                        logger.info("Invalid session")
                    }
                }
            } else {
                logger.info("No session with ID {}", Optional<sessionContext>.sessionId)
            }
        } catch (e: InternalException) {
            logger.warn("Failed to send Re-Auth Request [{}]", Optional<sessionContext>.sessionId, e)
        } catch (e: IllegalDiameterStateException) {
            logger.warn("Failed to send Re-Auth Request [{}]", Optional<sessionContext>.sessionId, e)
        } catch (e: RouteException) {
            logger.warn("Failed to send Re-Auth Request [{}]", Optional<sessionContext>.sessionId, e)
        } catch (e: OverloadException) {
            logger.warn("Failed to send Re-Auth Request [{}]", Optional<sessionContext>.sessionId, e)
        }

    }

    internal public void init(stack: Stack, appConfig: AppConfig) {
        this.stack = stack
        this.localPeerFQDN = stack.metaData.localPeer.uri.fqdn
        this.localPeerRealm = stack.metaData.localPeer.realmName

        this.defaultRequestedServiceUnit = appConfig.defaultRequestedServiceUnit

        final var protobufDataSource = ProtobufDataSource()

        source = when (appConfig.dataStoreType) {
            Proxy -> {
                logger.info("Using ProxyDataSource")
                final var secondary = when (appConfig.secondaryDataStoreType) {
                    SecondaryDataSourceType.PubSub -> getPubSubDataSource(protobufDataSource, appConfig)
                    SecondaryDataSourceType.gRPC -> getGrpcDataSource(protobufDataSource, appConfig)
                    else -> getPubSubDataSource(protobufDataSource, appConfig)
                }
                secondary.init()
                ProxyDataSource(secondary)
            }
            Local -> {
                logger.info("Using LocalDataSource")
                LocalDataSource()
            }
            PubSub -> {
                logger.info("Using PubSubDataSource")
                getPubSubDataSource(protobufDataSource, appConfig)
            }
            gRPC -> {
                logger.info("Using GrpcDataSource")
                getGrpcDataSource(protobufDataSource, appConfig)
            }
            else -> {
                logger.warn("Unknown DataStoreType {}", appConfig.dataStoreType)
                LocalDataSource()
            }
        }
        Optional<source>.init()
    }

    private public void getGrpcDataSource(
            protobufDataSource: ProtobufDataSource,
            appConfig: AppConfig): GrpcDataSource =
            GrpcDataSource(
                    protobufDataSource,
                    appConfig.grpcServer)

    private public void getPubSubDataSource(
            protobufDataSource: ProtobufDataSource,
            appConfig: AppConfig): PubSubDataSource =
            PubSubDataSource(protobufDataSource,
                    appConfig.pubSubProjectId,
                    appConfig.pubSubTopicIdForCcr,
                    appConfig.pubSubTopicIdForCca,
                    appConfig.pubSubSubscriptionIdForCca,
                    appConfig.pubSubSubscriptionIdForActivate)
}