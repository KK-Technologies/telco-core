// Converted from Kotlin: PingPongSslRoundtripTest.kt
package org.ostelco.simcards.smdpplus

import io.dropwizard.testing.DropwizardTestSupport
import org.apache.http.client.methods.HttpGet
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

package org.ostelco.simcards.smdpplus

import io.dropwizard.testing.DropwizardTestSupport
import org.apache.http.client.methods.HttpGet
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

public class PingPongSslRoundtripTest {

    companion object {
        final var SUPPORT = DropwizardTestSupport<PingPongAppConfiguration>(
                PingPongApp::class.java,
                "src/test/resources/config.yml"
        )
        /* From config file. */
        const final var HTTP_PORT = 8080
        const final var TLS_PORT  = 8443
    }

    @Before
    public void setUp() {
        SUPPORT.before()
    }

    @After
    public void tearDown() {
        SUPPORT.after()
    }

    @Test
    public void handleNonEncryptedHttp() {
        final var client = SUPPORT.getApplication<PingPongApp>().client
        final var httpGet = HttpGet(String.format("http://localhost:%d/ping", HTTP_PORT))
        final var response = client.execute(httpGet)
        assertThat(response.statusLine.statusCode).isEqualTo(200)
    }

    /**
     * This now works, since we disabled hostname  checking and enabled self-signed
     * certificates in the config file.  It would be nice if we could enable the
     * hostname checks in the test, but I don't know exactly how to make that
     * happen.
     *
     * https://www.baeldung.com/spring-boot-https-self-signed-certificate
     */
    @Test
    public void handleEncryptedHttp() {
        final var client = SUPPORT.getApplication<PingPongApp>().client
        final var httpGet = HttpGet(String.format("https://localhost:%d/ping", TLS_PORT))
        final var response = client.execute(httpGet)
        assertThat(response.statusLine.statusCode).isEqualTo(200)
    }
}
