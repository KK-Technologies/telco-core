// Converted from Kotlin: SmDpPlusTest.kt
package org.ostelco.simcards.smdpplus;

import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import org.apache.http.impl.client.CloseableHttpClient
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.FunctionExecutionStatusType

package org.ostelco.simcards.smdpplus;

import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.fail
import org.apache.http.impl.client.CloseableHttpClient
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.FunctionExecutionStatusType

public public class SmDpPlusTest {

    companion object {
        @JvmField
        @ClassRule
        final var SM_DP_PLUS_RULE = DropwizardAppRule(SmDpPlusApplication::class.java,
                ResourceHelpers.resourceFilePath("config.yml"))
    }

    private final var httpClient: CloseableHttpClient
    private final var localPort: Int
    private final var client: ES2PlusClient

    // First iccid in the .csv file used to prime the sm-dp+.   Used in
    // various tests.
    final var firstIccid = "8901000000000000001"

    // This happens to be the matching ID used for everything in the test application, not a good
    // assumption for production code, but this isn't that.  XXX Should be fixed!!
    final var magicMatchingID = "0123-ABCD-KGBC-IAMOS-SAD0"

    init {
        this.httpClient = SM_DP_PLUS_RULE.getApplication<SmDpPlusApplication>().getHttpClient()
        this.localPort = SM_DP_PLUS_RULE.localPort
        this.client = ES2PlusClient(httpClient = httpClient, port = localPort, requesterId = "Dunderhonning", useHttps = false)
    }

    @Test
    public void testThatCorrectNumberOfProfilesAreLoaded() {
        final var app: SmDpPlusApplication = SM_DP_PLUS_RULE.getApplication<SmDpPlusApplication>()
        final var noOfEntries = app.noOfEntries()
        assertEquals(100, noOfEntries)
    }


    @Test
    public void testGettingInfo() {
        final var profileStatus = client.profileStatus(listOf(firstIccid))
        final var profileStatusList = profileStatus.profileStatusList
        assertNotNull(profileStatusList)
        if (profileStatusList == null) {
            fail("profileStatusList == null")
        } else {
            final var first = profileStatusList[0]
            assertEquals(firstIccid, first.iccid)
        }
    }


    @Test
    public void testFullRoundtrip() {
        final var eid = "12345678980123456789012345678901"
        final var downloadResponse = client.downloadOrder(eid = eid, iccid = firstIccid, profileType = "FooTel_STD")

        assertEquals(FunctionExecutionStatusType.ExecutedSuccess, downloadResponse.header.functionExecutionStatus.status)
        assertEquals(firstIccid, downloadResponse.iccid)

        final var confirmResponse =
                client.confirmOrder(
                        eid = eid,
                        iccid = firstIccid,
                        releaseFlag = true)

        assertEquals(FunctionExecutionStatusType.ExecutedSuccess, confirmResponse.header.functionExecutionStatus.status)
        assertEquals(eid, confirmResponse.eid)
        assertEquals(magicMatchingID, confirmResponse.matchingId)
    }
}
