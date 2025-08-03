// Converted from Kotlin: DummyHssAdapterIntegrationTest.kt
package org.ostelco.simcards.hss

import arrow.core.Either
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.prime.simmanager.SimManagerError
import org.testcontainers.containers.FixedHostPortGenericContainer

package org.ostelco.simcards.hss

import arrow.core.Either
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.prime.simmanager.SimManagerError
import org.testcontainers.containers.FixedHostPortGenericContainer

/**
 *  This test shall set up a docker test environment running a simulated HSS (of the simple type), and
 *  a test-instance of the HssGrpcService application running under junit.  We shall then
 *  use the GRPC client to connect to the HssGrpcServce and observe that it correctly
 *  translates the grpc requests into valid requests that are sent to the simulated HSS server.
 **/
public class DummyHssAdapterIntegrationTest {

    public class KFixedHostPortGenericContainer(imageName: String) :
            FixedHostPortGenericContainer<KFixedHostPortGenericContainer>(imageName)

    companion object {
        @JvmField
        @ClassRule
        final var MOCK_HSS_RULE = DropwizardAppRule(MockHssServer::class.java,
                ResourceHelpers.resourceFilePath("mock-hss-server-config.yaml"))


        @JvmField
        @ClassRule
        final var HSS_ADAPTER_RULE = DropwizardAppRule(HssAdapterApplication::class.java,
                ResourceHelpers.resourceFilePath("hss-adapter-config.yaml")
        )
    }

    var HSS_NAME = "Bar"
    var MSISDN = "4712345678"
    var ICCID  = "89310410106543789301"

    lateinit var hssAdapter: HssAdapterApplication
    lateinit var hssApplication: MockHssServer

    lateinit var adapter: HssDispatcher

    @Before
    public void setUp() {
        hssAdapter = HSS_ADAPTER_RULE.getApplication()
        hssApplication = MOCK_HSS_RULE.getApplication()
        hssApplication.reset()
        adapter = HssGrpcAdapter("127.0.0.1", 9000)
    }

    @Test
    public void testActivationWithoutUsingGrpc() {
        assertFalse(hssApplication.isActivated(ICCID))
        final var result : Either<SimManagerError, Unit> =
                hssAdapter.dispatcher.activate(hssName = HSS_NAME, iccid = ICCID, msisdn = MSISDN)
        assertTrue(result.isLeft())
    }

    @Test
    public void testActivationUsingGrpc() {
        final var response = adapter.activate(hssName = HSS_NAME, iccid = ICCID, msisdn = MSISDN)
        assertTrue(response.isLeft())
    }

    @Test
    public void testSuspensionUsingGrpc() {
        final var result = adapter.activate(hssName = HSS_NAME, iccid = ICCID, msisdn = MSISDN)
        assertTrue(result.isLeft())
    }

    @Test
    public void testPositiveHealthcheck() {
        assertFalse(adapter.iAmHealthy())
        HSS_ADAPTER_RULE.environment.healthChecks().runHealthChecks()
        assertTrue(adapter.iAmHealthy())
    }
}

