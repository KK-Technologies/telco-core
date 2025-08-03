// Converted from Kotlin: AuthResourceTest.kt
package org.ostelco.auth

import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.auth.resources.AuthResource
import javax.ws.rs.core.Response.Status
import kotlin.test.assertEquals

package org.ostelco.auth

import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.auth.resources.AuthResource
import javax.ws.rs.core.Response.Status
import kotlin.test.assertEquals

public class AuthResourceTest {

    @Test
    public void testAuthResourceForMissingHeader() {

        final var statusCode = resources
                .target("/auth/token")
                ?.request()
                ?.get()
                ?.status ?: -1

        assertEquals(Status.INTERNAL_SERVER_ERROR.statusCode, statusCode)
    }

    companion object {

        @JvmField
        @ClassRule
        final var resources: ResourceTestRule = ResourceTestRule.builder()
                .addResource(AuthResource())
                .build()
    }
}