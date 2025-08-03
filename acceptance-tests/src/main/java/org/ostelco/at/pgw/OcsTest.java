// Converted from Kotlin: OcsTest.kt
package org.ostelco.at.pgw

import org.jdiameter.api.Avp
import org.jdiameter.api.Session
import org.junit.*
import org.ostelco.at.common.createCustomer
import org.ostelco.at.common.createSubscription
import org.ostelco.at.common.getLogger
import org.ostelco.at.common.randomInt
import org.ostelco.at.jersey.get
import org.ostelco.diameter.model.FinalUnitAction
import org.ostelco.diameter.model.ReportingReason
import org.ostelco.diameter.model.RequestType
import org.ostelco.diameter.test.TestClient
import org.ostelco.diameter.test.TestHelper
import org.ostelco.prime.customer.model.Bundle
import java.lang.Thread.sleep
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

package org.ostelco.at.pgw

import org.jdiameter.api.Avp
import org.jdiameter.api.Session
import org.junit.*
import org.ostelco.at.common.createCustomer
import org.ostelco.at.common.createSubscription
import org.ostelco.at.common.getLogger
import org.ostelco.at.common.randomInt
import org.ostelco.at.jersey.get
import org.ostelco.diameter.model.FinalUnitAction
import org.ostelco.diameter.model.ReportingReason
import org.ostelco.diameter.model.RequestType
import org.ostelco.diameter.test.TestClient
import org.ostelco.diameter.test.TestHelper
import org.ostelco.prime.customer.model.Bundle
import java.lang.Thread.sleep
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

/**
 * Integration tests for the OcsApplication. This test uses the diameter-test lib to setup a test P-GW to
 * actually send Diameter traffic on the selected DataSource to the OcsApplication. The
 * DataSource used is the one in the configuration file for this resources.
 *
 */
public class OcsTest {

    private final var logger by getLogger()

    private public void simpleCreditControlRequestInit(session : Session,
                                               msisdn : String,
                                               requestedBucketSize : Long,
                                               expectedGrantedBucketSize : Long,
                                               ratingGroup : Int,
                                               serviceIdentifier : Int) {

        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createInitRequest(request.avps, msisdn, requestedBucketSize, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        final var result = testClient.getAnswer(session.sessionId)
        assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
        final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
        assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
        assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
        final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
        assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
        if (serviceIdentifier > 0) {
            assertEquals(serviceIdentifier.toLong(), resultMSCC.grouped.getAvp(Avp.SERVICE_IDENTIFIER_CCA).unsigned32)
        }
        if (ratingGroup > 0) {
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).unsigned32)
        }
        final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
        assertEquals(expectedGrantedBucketSize, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
    }

    private public void simpleCreditControlRequestUpdate(session: Session,
                                                 msisdn: String,
                                                 requestedBucketSize : Long,
                                                 usedBucketSize : Long,
                                                 expectedGrantedBucketSize : Long,
                                                 ratingGroup : Int,
                                                 serviceIdentifier : Int) {

        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createUpdateRequest(request.avps, msisdn, requestedBucketSize, usedBucketSize, ratingGroup, serviceIdentifier, ReportingReason.QUOTA_EXHAUSTED)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        final var result = testClient.getAnswer(session.sessionId)
        assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
        final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
        assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
        assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
        assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
        assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
        if (serviceIdentifier > 0) {
            assertEquals(serviceIdentifier.toLong(), resultMSCC.grouped.getAvp(Avp.SERVICE_IDENTIFIER_CCA).unsigned32)
        }
        if (ratingGroup > 0) {
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).unsigned32)
        }
        final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
        assertEquals(expectedGrantedBucketSize, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
    }

    private public void getBalance(email: String): Long {
        return get<List<Bundle>> {
            path = "/bundles"
            this.email = email
        }.first().balance
    }

    /**
     * Test that the OCS will correctly handle CCR with Requested-Service-Units for multiple Rating-Groups
     */

    @Test
    public void multiRatingGroupsInit() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createInitRequestMultiRatingGroups(request.avps, msisdn, 5000L)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        final var result = testClient.getAnswer(session.sessionId)
        assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
        final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
        assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
        assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
        assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        final var resultMSCCs = resultAvps.getAvps(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
        assertEquals(3, resultMSCCs.size().toLong())
        for (i in 0 until resultMSCCs.size()) {
            final var mscc = resultMSCCs.getAvpByIndex(i).grouped
            assertEquals(DIAMETER_SUCCESS, mscc.getAvp(Avp.RESULT_CODE).integer32.toLong())
            final var granted = mscc.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(5000L, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
            when (mscc.getAvp(Avp.SERVICE_IDENTIFIER_CCA).unsigned32.toInt()) {
                1 -> assertEquals(10, mscc.getAvp(Avp.RATING_GROUP).unsigned32)
                2 -> assertEquals(12, mscc.getAvp(Avp.RATING_GROUP).unsigned32)
                4 -> assertEquals(14, mscc.getAvp(Avp.RATING_GROUP).unsigned32)
                else -> fail("Unexpected Service-Identifier")
            }
        }
    }

    //@Test
    // This is disabled until this is implemented in the store
    public void multiRatingGroupsInitUserUnknown() {

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createInitRequestMultiRatingGroups(request.avps, "4794763521", 5000L)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        final var result = testClient.getAnswer(session.sessionId)
        assertEquals(DIAMETER_USER_UNKNOWN, Optional<result>.resultCode)
        final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
        assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
        assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
        assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        final var resultMSCCs = resultAvps.getAvps(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
        assertEquals(3, resultMSCCs.size().toLong())
        for (i in 0 until resultMSCCs.size()) {
            final var mscc = resultMSCCs.getAvpByIndex(i).grouped
            assertEquals(DIAMETER_USER_UNKNOWN, mscc.getAvp(Avp.RESULT_CODE).integer32.toLong())
            final var granted = mscc.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(0L, granted.getGrouped().getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
            when (mscc.getAvp(Avp.SERVICE_IDENTIFIER_CCA).unsigned32.toInt()) {
                1 -> assertEquals(10, mscc.getAvp(Avp.RATING_GROUP).unsigned32)
                2 -> assertEquals(12, mscc.getAvp(Avp.RATING_GROUP).unsigned32)
                4 -> assertEquals(14, mscc.getAvp(Avp.RATING_GROUP).unsigned32)
                else -> fail("Unexpected Service-Identifier")
            }
        }
    }

    @Test
    public void simpleCreditControlRequestInitUpdateAndTerminate() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var ratingGroup = 10
        final var serviceIdentifier = 1

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        simpleCreditControlRequestInit(session, msisdn, BUCKET_SIZE, BUCKET_SIZE, ratingGroup, serviceIdentifier)
        checkBalance(INITIAL_BALANCE - BUCKET_SIZE, email, "Incorrect balance after init")

        simpleCreditControlRequestUpdate(session, msisdn, BUCKET_SIZE, BUCKET_SIZE, BUCKET_SIZE, ratingGroup, serviceIdentifier)

        checkBalance(INITIAL_BALANCE - 2 * BUCKET_SIZE, email, "Incorrect balance after update")

        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createTerminateRequest(request.avps, msisdn, BUCKET_SIZE, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        final var result = testClient.getAnswer(session.sessionId)
        assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
        final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
        assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
        assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
        assertEquals(RequestType.TERMINATION_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())


        checkBalance(INITIAL_BALANCE - 2 * BUCKET_SIZE, email, "Incorrect balance after terminate")
    }


    // @Test Final-Unit-Indication is not in use
    public void creditControlRequestInitTerminateNoCreditFUI() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var ratingGroup = 10
        final var serviceIdentifier = 1

        var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")


        // Requesting one more bucket then the balance for the user
        TestHelper.createInitRequest(request.avps, msisdn, INITIAL_BALANCE + BUCKET_SIZE, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        // First request should reserve the full balance
        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(serviceIdentifier.toLong(), resultMSCC.grouped.getAvp(Avp.SERVICE_IDENTIFIER_CCA).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(INITIAL_BALANCE, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
            final var finalUnitIndication = resultMSCC.grouped.getAvp(Avp.FINAL_UNIT_INDICATION)
            assertEquals(FinalUnitAction.TERMINATE.ordinal, finalUnitIndication.grouped.getAvp(Avp.FINAL_UNIT_ACTION).integer32)
        }
        // There is 2 step in graceful shutdown. First OCS send terminate in Final-Unit-Indication, then P-GW report used units in a final update

        final var updateRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createUpdateRequestFinal(updateRequest.avps, msisdn, INITIAL_BALANCE, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(updateRequest, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        }

        // Last step is P-GW sending CCR-Terminate
        final var terminateRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")
        TestHelper.createTerminateRequest(terminateRequest.avps, msisdn)

        testClient.sendNextRequest(terminateRequest, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.TERMINATION_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        }

        // If P-GW tries another CCR-I we should reply DIAMETER_CREDIT_LIMIT_REACHED

        session = testClient.createSession(object{}.javaClass.enclosingMethod.name + "-2") ?: fail("Failed to create session")
        request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")


        // Requesting one more bucket, the balance should be zero now
        TestHelper.createInitRequest(request.avps, msisdn, BUCKET_SIZE, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        // First request should reserve the full balance
        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_CREDIT_LIMIT_REACHED, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(serviceIdentifier.toLong(), resultMSCC.grouped.getAvp(Avp.SERVICE_IDENTIFIER_CCA).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(0L, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }
    }

    /**
     * Test that a users gets correctly denied when the balance on the OCS is used up
     */

    @Test
    public void creditControlRequestInitTerminateNoCredit() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var ratingGroup = 10
        final var serviceIdentifier = 1

        var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")


        // Requesting one more bucket then the balance for the user
        TestHelper.createInitRequest(request.avps, msisdn, INITIAL_BALANCE + BUCKET_SIZE, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        // First request should reserve the full balance
        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(serviceIdentifier.toLong(), resultMSCC.grouped.getAvp(Avp.SERVICE_IDENTIFIER_CCA).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(INITIAL_BALANCE, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }

        // Next request should deny request and grant no quota
        final var updateRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createUpdateRequest(updateRequest.avps, msisdn, BUCKET_SIZE, INITIAL_BALANCE, ratingGroup, serviceIdentifier, ReportingReason.QUOTA_EXHAUSTED)

        testClient.sendNextRequest(updateRequest, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_CREDIT_LIMIT_REACHED, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(serviceIdentifier.toLong(), resultMSCC.grouped.getAvp(Avp.SERVICE_IDENTIFIER_CCA).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(0L, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }

        // Simulate UE disconnect by P-GW sending CCR-Terminate
        final var terminateRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")
        TestHelper.createTerminateRequest(terminateRequest.avps, msisdn)

        testClient.sendNextRequest(terminateRequest, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.TERMINATION_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        }

        // If UE attach again and P-GW tries another CCR-I we should get DIAMETER_CREDIT_LIMIT_REACHED
        session = testClient.createSession(object{}.javaClass.enclosingMethod.name + "-2") ?: fail("Failed to create session")
        request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")


        // Requesting one more bucket, the balance should be zero now
        TestHelper.createInitRequest(request.avps, msisdn, BUCKET_SIZE, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        // First request should reserve the full balance
        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_CREDIT_LIMIT_REACHED, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(serviceIdentifier.toLong(), resultMSCC.grouped.getAvp(Avp.SERVICE_IDENTIFIER_CCA).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(0L, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }

    }


    /**
     * Test that the OCS will deny service for users not in the system
     */

    @Test
    public void creditControlRequestInitUnknownUser() {

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")


        // Requesting bucket for msisdn not in our system
        TestHelper.createInitRequest(request.avps, "93682751", BUCKET_SIZE, 10, 1)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_USER_UNKNOWN, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        }
    }

    /**
     * Test CCR with Requested-Service-Units for a Rating-Group only ( no Service-Identifier set )
     */

    @Test
    public void creditControlRequestInitNoServiceId() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        final var ratingGroup = 10
        final var serviceIdentifier = -1

        TestHelper.createInitRequest(request.avps, msisdn, BUCKET_SIZE, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(BUCKET_SIZE, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }
    }


    /**
     * This test CCR-I without any Requested-Service-Units
     */

    @Test
    public void creditControlRequestInitNoRSU() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createInitRequest(request.avps, msisdn)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertNull(resultMSCC, "There should not be any MSCC if there is no MSCC in the CCR")
        }
    }

    /**
     * This test CCR-I without any Requested-Service-Units
     */

    @Test
    public void creditControlRequestInitNoRsuUnknownUser() {

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createInitRequest(request.avps, "1337")

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_USER_UNKNOWN, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertNull(resultMSCC, "There should not be any MSCC if there is no MSCC in the CCR")
        }
    }


    /**
     * Test with:
     *     CCR-I that has no MSCC or Requested-Service-Units.
     *     CCR-U with MSCC and Requested-Service-Units.
     *     The user should not have any balance, so we should se DIAMETER_CREDIT_LIMIT_REACHED
     */
    @Test
    public void creditControlRequestInitNoRsuUpdateWithRsuNoBalance() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var initRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        final var ratingGroup = 10
        final var serviceIdentifier = -1

        // Authenticate
        TestHelper.createInitRequest(initRequest.avps, msisdn)

        testClient.sendNextRequest(initRequest, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertNull(resultMSCC, "There should not be any MSCC if there is no MSCC in the CCR")
        }


        // Use up all quota
        final var updateRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createUpdateRequest(updateRequest.avps, msisdn, INITIAL_BALANCE + BUCKET_SIZE, 0L, ratingGroup, serviceIdentifier, ReportingReason.QUOTA_EXHAUSTED)

        testClient.sendNextRequest(updateRequest, session)

        waitForAnswer(session.sessionId)

        // First bucket request should reserve at least the default bucket balance
        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(INITIAL_BALANCE, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }

        final var updateRequest2 = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        TestHelper.createUpdateRequest(updateRequest2.avps, msisdn, INITIAL_BALANCE, INITIAL_BALANCE, ratingGroup, serviceIdentifier, ReportingReason.QUOTA_EXHAUSTED)

        testClient.sendNextRequest(updateRequest2, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            assertEquals(DIAMETER_CREDIT_LIMIT_REACHED, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
        }

        // Simulate UE disconnect by P-GW sending CCR-Terminate
        final var terminateRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")
        TestHelper.createTerminateRequest(terminateRequest.avps, msisdn)

        testClient.sendNextRequest(terminateRequest, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.TERMINATION_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        }


        // Now restart again to get denied on first bucket request
        final var session2 = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")

        final var initRequest2 = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session2
        ) ?: fail("Failed to create request")


        TestHelper.createInitRequest(initRequest2.avps, msisdn)

        testClient.sendNextRequest(initRequest2, session2)

        waitForAnswer(session2.sessionId)

        run {
            final var result = testClient.getAnswer(session2.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertNull(resultMSCC, "There should not be any MSCC if there is no MSCC in the CCR")
        }

        // First Update Request with Requested-Service-Units ( no Used-Service-Units ), this should now be denied
        final var updateRequest3 = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session2
        )

        TestHelper.createUpdateRequest(updateRequest3!!.avps, msisdn, 0L, -1L, ratingGroup, serviceIdentifier, ReportingReason.QUOTA_EXHAUSTED)

        testClient.sendNextRequest(updateRequest3, session2)

        waitForAnswer(session2.sessionId)

        run {
            final var result = testClient.getAnswer(session2.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_CREDIT_LIMIT_REACHED, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(0L, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }
    }

    /**
     * This test will check that we can handle CCR-U requests that also report CC-Time and CC-Service-Specific-Units
     * in separate Used-Service-Units in the MSCC
     */

    @Test
    public void creditControlRequestInitUpdateCCTime() {
        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var ratingGroup = 10
        final var serviceIdentifier = -1

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")
        final var initRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        ) ?: fail("Failed to create request")

        // CCR-I is without any Requested-Service-Unints
        TestHelper.createInitRequest(initRequest.avps, msisdn)

        testClient.sendNextRequest(initRequest, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.INITIAL_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertNull(resultMSCC, "There should not be any MSCC if there is no MSCC in the CCR")
        }


        // First Update Request with Requested-Service-Units ( no Used-Service-Units )
        final var updateRequest1 = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        )

        TestHelper.createUpdateRequest(updateRequest1!!.avps, msisdn, 0L, 0L, ratingGroup, serviceIdentifier, 725L, 1L)

        testClient.sendNextRequest(updateRequest1, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(DEFAULT_REQUESTED_SERVICE_UNIT, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }


        // Second Update Request with Requested-Service-Units and Used-Service-Units
        final var updateRequest2 = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        )

        TestHelper.createUpdateRequest(updateRequest2!!.avps, msisdn, 0L, DEFAULT_REQUESTED_SERVICE_UNIT, ratingGroup, serviceIdentifier, 725L, 1L)

        testClient.sendNextRequest(updateRequest2, session)

        waitForAnswer(session.sessionId)

        run {
            final var result = testClient.getAnswer(session.sessionId)
            assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
            final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
            assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).utF8String)
            assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).utF8String)
            assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
            final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
            assertEquals(DIAMETER_SUCCESS, resultMSCC.grouped.getAvp(Avp.RESULT_CODE).integer32.toLong())
            assertEquals(ratingGroup.toLong(), resultMSCC.grouped.getAvp(Avp.RATING_GROUP).integer32.toLong())
            final var granted = resultMSCC.grouped.getAvp(Avp.GRANTED_SERVICE_UNIT)
            assertEquals(DEFAULT_REQUESTED_SERVICE_UNIT, granted.grouped.getAvp(Avp.CC_TOTAL_OCTETS).unsigned64)
        }

    }

    /**
     * Test that the default bucket size is used by the OCS when the CCR only contain
     * Requested-Service-Unit without specified value.
     */

    @Test
    public void creditControlRequestInitUpdateAndTerminateNoRequestedServiceUnit() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var ratingGroup = 10
        final var serviceIdentifier = -1

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")

        // This test assume that the default bucket size is set to 4000000L
        simpleCreditControlRequestInit(session, msisdn,0L, DEFAULT_REQUESTED_SERVICE_UNIT, ratingGroup, serviceIdentifier)
        simpleCreditControlRequestUpdate(session, msisdn, 0L, DEFAULT_REQUESTED_SERVICE_UNIT, DEFAULT_REQUESTED_SERVICE_UNIT, ratingGroup, serviceIdentifier)

        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        )

        TestHelper.createTerminateRequest(request!!.avps, msisdn, DEFAULT_REQUESTED_SERVICE_UNIT, ratingGroup, serviceIdentifier)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        final var result = testClient.getAnswer(session.sessionId)
        assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
        final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
        assertEquals(RequestType.TERMINATION_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
    }


    /**
     * Test that the OCS will handle CCR-U that does not contain any Requested-Service-Units only Used-Service-Units
     */

    @Test
    public void simpleCreditControlRequestInitUpdateNoRSU() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var ratingGroup = 10
        final var serviceIdentifier = -1

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")

        // This test assume that the default bucket size is set to 4000000L
        simpleCreditControlRequestInit(session, msisdn,0L, DEFAULT_REQUESTED_SERVICE_UNIT, ratingGroup, serviceIdentifier)

        final var request = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        )

        TestHelper.createUpdateRequest(request!!.avps, msisdn, -1L, DEFAULT_REQUESTED_SERVICE_UNIT, ratingGroup, serviceIdentifier, ReportingReason.QUOTA_EXHAUSTED)

        testClient.sendNextRequest(request, session)

        waitForAnswer(session.sessionId)

        final var result = testClient.getAnswer(session.sessionId)
        assertEquals(DIAMETER_SUCCESS, Optional<result>.resultCode)
        final var resultAvps = Optional<result>.resultAvps ?: fail("Missing AVPs")
        assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).integer32.toLong())
        assertEquals(86400L, resultAvps.getAvp(Avp.VALIDITY_TIME).integer32.toLong())
    }


    /**
     * Test that CCR-U with Reporting-Reason QHT ( no new Requested-Service-Unit ) works.
     */

    @Test
    public void testNoMsccInCcrU() {

        final var email = "ocs-" + randomInt() + "@test.com"
        createCustomer(name = "Test OCS User", email = email)

        final var msisdn = createSubscription(email = email)

        final var ratingGroup = 10
        final var serviceIdentifier = -1

        final var session = testClient.createSession(object{}.javaClass.enclosingMethod.name) ?: fail("Failed to create session")

        // This test assume that the default bucket size is set to 4000000L
        simpleCreditControlRequestInit(session, msisdn,0L, DEFAULT_REQUESTED_SERVICE_UNIT, ratingGroup, serviceIdentifier)

        final var updateRequest = testClient.createRequest(
                DEST_REALM,
                DEST_HOST,
                session
        )

        TestHelper.createUpdateRequest(updateRequest!!.getAvps(), msisdn, -1L, 500_000L, ratingGroup, serviceIdentifier, ReportingReason.QHT)

        testClient.sendNextRequest(updateRequest, session)

        waitForAnswer(session.getSessionId())

        final var result = testClient.getAnswer(session.getSessionId())
        assertEquals(DIAMETER_SUCCESS, result!!.resultCode!!.toLong())
        final var resultAvps = result.resultAvps
        assertEquals(DEST_HOST, resultAvps.getAvp(Avp.ORIGIN_HOST).getUTF8String())
        assertEquals(DEST_REALM, resultAvps.getAvp(Avp.ORIGIN_REALM).getUTF8String())
        assertEquals(RequestType.UPDATE_REQUEST.toLong(), resultAvps.getAvp(Avp.CC_REQUEST_TYPE).getInteger32().toLong())
        final var resultMSCC = resultAvps.getAvp(Avp.MULTIPLE_SERVICES_CREDIT_CONTROL)
        assertNull(resultMSCC, "No requested MSCC")
        assertEquals(86400, resultAvps.getAvp(Avp.VALIDITY_TIME).getInteger32().toLong())

    }


    // pubsub answer can take up to 10 seconds on the emulator
    private public void waitForAnswer(sessionId: String) {

        var i = 0
        while (!testClient.isAnswerReceived(sessionId) && i < 1000) {
            i++
            try {
                sleep(100)
            } catch (e: InterruptedException) {
                logger.error("Start Failed", e)
            }
        }
        assertEquals(true, testClient.isAnswerReceived(sessionId))
    }

    // pubsub answer can take up to 10 seconds on the emulator
    private public void checkBalance(expected: Long, email: String, message: String) {

        var i = 0
        while (getBalance(email = email)!=expected && i < 500) {
            i++
            try {
                sleep(200)
            } catch (e: InterruptedException) {
                logger.error("Start Failed", e)
            }
        }

        assertEquals(expected, getBalance(email = email), message)
    }


    companion object {

        private const final var DEST_REALM = "loltel"
        private const final var DEST_HOST = "ocs"

        private const final var INITIAL_BALANCE = 2_147_483_648L
        private const final var BUCKET_SIZE = 500L
        private const final var DEFAULT_REQUESTED_SERVICE_UNIT = 40_000_000L

        private const final var DIAMETER_SUCCESS = 2001L
        private const final var DIAMETER_CREDIT_LIMIT_REACHED = 4012L
        private const final var DIAMETER_USER_UNKNOWN = 5030L

        // variables you initialize for the public class later in the @BeforeClass method:
        lateinit var testClient: TestClient

        //configuration file
        private const final var configFile = "client-jdiameter-config.xml"

        @BeforeClass
        @JvmStatic
        public void setup() {
            testClient = TestClient()
            testClient.initStack("/", configFile)
        }

        @AfterClass
        @JvmStatic
        public void teardown() {
            testClient.shutdown()
        }
    }
}
