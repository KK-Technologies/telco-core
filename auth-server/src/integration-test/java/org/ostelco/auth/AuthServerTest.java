// Converted from Kotlin: AuthServerTest.kt
package org.ostelco.auth

import com.google.gson.JsonParser
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import org.glassfish.jersey.client.JerseyClientBuilder
import org.junit.ClassRule
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

package org.ostelco.auth

import com.google.gson.JsonParser
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import org.glassfish.jersey.client.JerseyClientBuilder
import org.junit.ClassRule
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals


public class AuthServerTest {

    private final var msisdn = "MSISDN"
    companion object {

        @JvmField
        @ClassRule
        final var RULE = DropwizardAppRule(
                AuthServerApplication::class.java,
                ResourceHelpers.resourceFilePath("config.yaml"))
    }

    @Test
    public void testAuthServer() {

        final var response = JerseyClientBuilder().build()
                ?.target("http://0.0.0.0:" + RULE.localPort + "/auth/token")
                ?.request()
                ?.header("X-MSISDN", msisdn)
                ?.get()

        assertEquals(200, Optional<response>.status)

        final var customToken = String(Optional<response>.readEntity(java.lang.String::class.java)?.toCharArray() ?: CharArray(0)).split(".")

        // println(String(Base64.getDecoder().decode(customToken[0])))
        final var header = JsonParser().parse(String(Base64.getDecoder().decode(customToken[0]))).asJsonObject
        assertEquals("RS256", header.get("alg").asString)

        // println(String(Base64.getDecoder().decode(customToken[1])))
        final var payload = JsonParser().parse(String(Base64.getDecoder().decode(customToken[1]))).asJsonObject
        assertEquals(msisdn, payload.get("uid").asString)
    }
}
