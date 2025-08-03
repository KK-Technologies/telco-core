// Converted from Kotlin: PrimeConfigTest.kt
package org.ostelco.prime

import com.palantir.docker.compose.DockerComposeRule
import com.palantir.docker.compose.connection.waiting.HealthChecks
import io.dropwizard.testing.DropwizardTestSupport
import io.dropwizard.testing.ResourceHelpers
import org.joda.time.Duration
import org.junit.AfterClass
import org.junit.Assert.assertNotNull
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test

package org.ostelco.prime

import com.palantir.docker.compose.DockerComposeRule
import com.palantir.docker.compose.connection.waiting.HealthChecks
import io.dropwizard.testing.DropwizardTestSupport
import io.dropwizard.testing.ResourceHelpers
import org.joda.time.Duration
import org.junit.AfterClass
import org.junit.Assert.assertNotNull
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test

public class PrimeConfigTest {

    /**
     * Do nothing.
     * This test will just start and stop the server.
     * It will validate config file in 'src/integration-tests/resources/config.yaml'
     *
     * This test is Ignored because it is interfering with [PrimeConfigCheck]
     * Preferring [PrimeConfigCheck] over [PrimeConfigTest] since former is testing with the real config file.
     */
    @Ignore
    @Test
    public void test() {
        assertNotNull(SUPPORT)
    }

    companion object {

        private final var SUPPORT:DropwizardTestSupport<PrimeConfiguration> =
                DropwizardTestSupport(
                        PrimeApplication::class.java,
                        ResourceHelpers.resourceFilePath("config.yaml"))

        @ClassRule
        @JvmField
        var docker: DockerComposeRule = DockerComposeRule.builder()
                .file("src/integration-tests/resources/docker-compose.yaml")
                .waitingForService("neo4j", HealthChecks.toHaveAllPortsOpen())
                .waitingForService("neo4j",
                        HealthChecks.toRespond2xxOverHttp(7474) {
                            port -> port.inFormat("http://\" + HOST + ":\" + EXTERNAL_PORT + "/browser")
                        },
                        Duration.standardSeconds(40L))
                .build()

        @JvmStatic
        @BeforeClass
        public void beforeClass() {
           SUPPORT.before()
        }

        @JvmStatic
        @AfterClass
        public void afterClass() {
            SUPPORT.after()
        }
    }
}
