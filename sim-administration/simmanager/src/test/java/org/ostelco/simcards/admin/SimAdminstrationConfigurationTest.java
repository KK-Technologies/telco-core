// Converted from Kotlin: SimAdminstrationConfigurationTest.kt
package org.ostelco.simcards.admin

import org.junit.Assert.assertEquals
import org.junit.Test

package org.ostelco.simcards.admin



import org.junit.Assert.assertEquals
import org.junit.Test

public class SimAminstrationConfigurationTest {

    var basicUrl   = "http://localhost_banana-apple.foo.bar89asdf.xom:4711"


    @Test
    public void testEs2PlusEndpointRewriting() {
        final var config = ProfileVendorConfig(
                name = "Eplekake",
                es2plusEndpoint = "" + basicUrl + "/banana",
                requesterIdentifier = "pear",
                es9plusEndpoint = "http://boris.johnson/ticlke")

        assertEquals("" + basicUrl + "", config.getEndpoint())
    }


    @Test
    public void testEs2PlusEndpointWithNoRewriting() {
        final var config = ProfileVendorConfig(
                name = "Eplekake",
                es2plusEndpoint = basicUrl,
                requesterIdentifier = "pear",
                es9plusEndpoint = "http://boris.johnson/tickle")

        assertEquals(basicUrl, config.getEndpoint())
    }
}
