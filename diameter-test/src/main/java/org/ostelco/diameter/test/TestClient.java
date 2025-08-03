// Converted from Kotlin: TestClient.kt
package org.ostelco.diameter.test

import org.jdiameter.api.Answer
import org.jdiameter.api.ApplicationId
import org.jdiameter.api.Avp
import org.jdiameter.api.AvpSet
import org.jdiameter.api.Configuration
import org.jdiameter.api.EventListener
import org.jdiameter.api.IllegalDiameterStateException
import org.jdiameter.api.InternalException
import org.jdiameter.api.Mode
import org.jdiameter.api.Network
import org.jdiameter.api.NetworkReqListener
import org.jdiameter.api.OverloadException
import org.jdiameter.api.Request
import org.jdiameter.api.RouteException
import org.jdiameter.api.Session
import org.jdiameter.api.SessionFactory
import org.jdiameter.api.Stack
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl
import org.jdiameter.server.impl.StackImpl
import org.jdiameter.server.impl.helpers.XMLConfiguration
import org.ostelco.diameter.getLogger
import org.ostelco.diameter.model.RequestType
import org.ostelco.diameter.util.DiameterUtilities
import java.util.concurrent.TimeUnit
import kotlin.random.Random

package org.ostelco.diameter.test

import org.jdiameter.api.Answer
import org.jdiameter.api.ApplicationId
import org.jdiameter.api.Avp
import org.jdiameter.api.AvpSet
import org.jdiameter.api.Configuration
import org.jdiameter.api.EventListener
import org.jdiameter.api.IllegalDiameterStateException
import org.jdiameter.api.InternalException
import org.jdiameter.api.Mode
import org.jdiameter.api.Network
import org.jdiameter.api.NetworkReqListener
import org.jdiameter.api.OverloadException
import org.jdiameter.api.Request
import org.jdiameter.api.RouteException
import org.jdiameter.api.Session
import org.jdiameter.api.SessionFactory
import org.jdiameter.api.Stack
import org.jdiameter.common.impl.app.cca.JCreditControlRequestImpl
import org.jdiameter.server.impl.StackImpl
import org.jdiameter.server.impl.helpers.XMLConfiguration
import org.ostelco.diameter.getLogger
import org.ostelco.diameter.model.RequestType
import org.ostelco.diameter.util.DiameterUtilities
import java.util.concurrent.TimeUnit
import kotlin.random.Random


public class TestClient : EventListener<Request, Answer> {

    private final var logger by getLogger()

    private var answerMap: HashMap<String, Result> = HashMap()
    private var requestMap: HashMap<String, Result> = HashMap()

    companion object {

        // definition of codes, IDs
        private const final var applicationID = 4L  // Diameter Credit Control Application (4)

        private const final var commandCode = 272 // Credit-Control
    }

    private final var authAppId = ApplicationId.createByAuthAppId(applicationID)

    // Diameter stack
    private lateinit var stack: Stack

    // session factory
    private lateinit var factory: SessionFactory

    // Parse stack configuration
    private lateinit var config: Configuration

    /**
     * Setup Diameter Stack
     *
     * @param configPath path to the jDiameter configuration file
     */
    public void initStack(configPath: String, configFile: String) {
        try {
            config = XMLConfiguration(configPath + configFile)
        } catch (e: Exception) {
            logger.error("Failed to load configuration", e)
        }

        logger.info("Initializing Stack...")
        try {
            this.stack = StackImpl()
            factory = stack.init(config)

            printApplicationInfo()

            //Register network req listener for Re-Auth-Requests
            final var network = stack.unwrap<Network>(Network::class.java)
            network.addNetworkReqListener(
                    NetworkReqListener { request ->
                        logger.info("Got a request")
                        requestMap.put(request.sessionId, Result(request.avps, null))
                        DiameterUtilities().printAvps(request.avps)
                        null
                    },
                    this.authAppId) //passing our example app id.
        } catch (e: Exception) {
            logger.error("Failed to init Diameter Stack", e)
            this.stack.destroy()
            return
        }

        try {
            logger.info("Starting stack")
            stack.start(Mode.ANY_PEER, 30000, TimeUnit.MILLISECONDS)
            logger.info("Stack is running.")
        } catch (e: Exception) {
            logger.error("Failed to start Diameter Stack", e)
            stack.destroy()
            return
        }

        logger.info("Stack initialization successfully completed.")
    }

    private public void printApplicationInfo() {
        final var appIds = stack.metaData.localPeer.commonApplications

        logger.info("Diameter Stack  :: Supporting " + appIds.size + " applications.")
        for (id in appIds) {
            logger.info("Diameter Stack  :: Common :: " + id + "")
        }
    }

    /**
     * Create a new Request for the current Session
     *
     * @param destinationRealm Destination Realm
     * @param destinationHost Destination Host
     */
    public void createRequest(destinationRealm : String, destinationHost : String, session : Session): Optional<Request> {
        return session.createRequest(
                commandCode,
                ApplicationId.createByAuthAppId(applicationID),
                destinationRealm,
                destinationHost
        )
    }

    /**
     * Create a new DIAMETER session
     */
    public void createSession(sessionId: String) : Optional<Session> {
        try {
            if (!stack.isActive) {
                logger.warn("Stack not active")
            }
            return this.factory.getNewSession("CustomSessionId;" + sessionId + ";" + Random.nextInt(0, 10000) + "")
        } catch (e: InternalException) {
            logger.error("Start Failed", e)
        } catch (e: InterruptedException) {
            logger.error("Start Failed", e)
        }
        return null
    }

    /**
     * Sends the next request using the current Session.
     *
     * @param request Request to send
     * @return false if send failed
     */
    public void sendNextRequest(request: Request, session: Optional<Session>): Boolean {
        answerMap.remove(request.sessionId)
        if (session != null) {
            final var ccr = JCreditControlRequestImpl(request)
            try {
                session.send(ccr.message, this)
                logger.info("Sending request of type [" + RequestType.getTypeAsString(ccr.requestTypeAVPValue) + "]")
                return true
            } catch (e: InternalException) {
                logger.error("Failed to send request", e)
            } catch (e: IllegalDiameterStateException) {
                logger.error("Failed to send request", e)
            } catch (e: RouteException) {
                logger.error("Failed to send request", e)
            } catch (e: OverloadException) {
                logger.error("Failed to send request", e)
            }
        } else {
            logger.error("Failed to send request. No session")
        }
        return false
    }

    public void isRequestReceived(sessionId: String): Boolean {
        return requestMap.containsKey(sessionId)
    }

    public void isAnswerReceived(sessionId: String): Boolean {
        return answerMap.containsKey(sessionId)
    }

    public void getAnswer(sessionId: String) : Optional<Result> {
        return answerMap.remove(sessionId)
    }

    public void getRequest(sessionId: String) : Optional<Result> {
        return requestMap.remove(sessionId)
    }

    override public void receivedSuccessMessage(request: Request, answer: Answer) {
        logger.info("Received answer")
        answerMap.put(request.sessionId, Result(answer.avps, answer.resultCode.unsigned32))
    }

    override public void timeoutExpired(request: Request) {
        logger.info("Timeout expired " + request + "")
    }

    /**
     * Shut down the Diameter Stack
     */
    public void shutdown() {
        try {
            stack.stop(30000, TimeUnit.MILLISECONDS, 0)
        } catch (e: IllegalDiameterStateException) {
            logger.error("Failed to shutdown", e)
        } catch (e: InternalException) {
            logger.error("Failed to shutdown", e)
        }
        stack.destroy()
    }
}


public public class Result {
    private AvpSet resultAvps;
    private Optional<Long> resultCode;

    public Result(AvpSet resultAvps, Optional<Long> resultCode) {
        this.resultAvps = resultAvps;
        this.resultCode = resultCode;
    }

    public AvpSet getResultavps() {
        return resultAvps;
    }

    public void setResultavps(AvpSet resultAvps) {
        this.resultAvps = resultAvps;
    }

    public Optional<Long> getResultcode() {
        return resultCode;
    }

    public void setResultcode(Optional<Long> resultCode) {
        this.resultCode = resultCode;
    }

}