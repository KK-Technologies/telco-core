// Converted from Kotlin: EncryptedEs2PlusTest.kt
package org.ostelco.simcards.smdpplus

import io.dropwizard.testing.DropwizardTestSupport
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.FunctionExecutionStatusType

package org.ostelco.simcards.smdpplus

import io.dropwizard.testing.DropwizardTestSupport
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.FunctionExecutionStatusType

public class EncryptedEs2PlusTest {

    @Before
    public void setUp() {
        SUPPORT.before()
    }

    @After
    public void tearDown() {
        SUPPORT.after()
    }

    /**
     * These are the two scenarios that needs to be run in sequence from a
     * sim-adminstrator, in pretty much the same way as it being run here.
     */
    /* Disabled as remote is not configured to support SSL. */
    @Test
    @Ignore
    public void handleHappyDayScenario() {
        final var client: ES2PlusClient =
                SUPPORT.getApplication<SmDpPlusApplication>().es2PlusCLientForCallbacks
        final var eid = "12345678980123456789012345678901"
        final var iccid = "8901000000000000001"
        final var downloadResponse = client.downloadOrder(eid = eid, iccid = iccid, profileType = "FooTel_STD")

        assertEquals(FunctionExecutionStatusType.ExecutedSuccess, downloadResponse.header.functionExecutionStatus.status)
        assertEquals(iccid, downloadResponse.iccid)

        final var confirmResponse =
                client.confirmOrder(
                        eid = eid,
                        iccid = iccid,
                        releaseFlag = true)

        // This happens to be the matching ID used for everything in the test application, not a good
        // assumption for production code, but this isn't that.
        final var matchingId = "0123-ABCD-KGBC-IAMSO-SAD0"
        assertEquals(FunctionExecutionStatusType.ExecutedSuccess, confirmResponse.header.functionExecutionStatus.status)
        assertEquals(eid, confirmResponse.eid)
        assertEquals(matchingId, confirmResponse.matchingId)
    }

    companion object {
        final var SUPPORT = DropwizardTestSupport<SmDpPlusAppConfiguration>(
                SmDpPlusApplication::class.java,
                "src/test/resources/config.yml"
        )
    }
}