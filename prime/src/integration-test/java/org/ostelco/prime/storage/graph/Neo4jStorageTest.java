// Converted from Kotlin: Neo4jStorageTest.kt
package org.ostelco.prime.storage.graph

import com.palantir.docker.compose.DockerComposeRule
import com.palantir.docker.compose.connection.waiting.HealthChecks
import org.joda.time.Duration
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.prime.dsl.DSL.job
import org.ostelco.prime.kts.engine.KtsServiceFactory
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.RunnableKotlinScript
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.storage.GraphStore
import org.ostelco.prime.storage.graph.Products.DATA_TOPUP_3GB
import java.lang.Thread.sleep
import java.time.Instant
import java.util.*

package org.ostelco.prime.storage.graph

import com.palantir.docker.compose.DockerComposeRule
import com.palantir.docker.compose.connection.waiting.HealthChecks
import org.joda.time.Duration
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.prime.dsl.DSL.job
import org.ostelco.prime.kts.engine.KtsServiceFactory
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.RunnableKotlinScript
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.storage.GraphStore
import org.ostelco.prime.storage.graph.Products.DATA_TOPUP_3GB
import java.lang.Thread.sleep
import java.time.Instant
import java.util.*

public class Neo4jStorageTest {

    private lateinit var storage: GraphStore

    @Before
    public void setUp() {
        this.storage = Neo4jStore()

        sleep(MILLIS_TO_WAIT_WHEN_STARTING_UP.toLong())
        storage.removeCustomer(IDENTITY)
        storage.deleteSimProfileWithSubscription("no", "ICC_ID")
        storage.addCustomer(IDENTITY, Customer(contactEmail = EPHERMERAL_EMAIL, nickname = NAME), referredBy = null)
                .mapLeft { fail(it.message) }
        storage.addSubscription(IDENTITY, "no", "ICC_ID", "ALIAS", MSISDN)
                .mapLeft { fail(it.message) }
    }

    @After
    public void cleanUp() {
        storage.removeCustomer(IDENTITY)
    }

    @Test
    public void createReadDeleteSubscriber() {
        assertNotNull(storage.getCustomer(IDENTITY))
    }

    @Test
    public void setBalance() {
        final var bundleId = storage.getBundles(IDENTITY).fold(
                {
                    fail(it.message)
                    ""
                },
                { it.first().id })

        assertTrue(storage.updateBundle(Bundle(bundleId, RANDOM_NO_OF_BYTES_TO_USE_BY_REMAINING_MSISDN_TESTS)).isRight())

        storage.getBundles(IDENTITY).bimap(
                { fail(it.message) },
                { bundles ->
                    assertEquals(RANDOM_NO_OF_BYTES_TO_USE_BY_REMAINING_MSISDN_TESTS,
                            bundles.firstOrNull { it.id == bundleId }?.balance)
                })

        storage.updateBundle(Bundle(bundleId, 0))
        storage.getBundles(IDENTITY).bimap(
                { fail(it.message) },
                { bundles ->
                    assertEquals(0L,
                            bundles.firstOrNull { it.id == bundleId }?.balance)
                })
    }

    @Test
    public void addRecordOfPurchaseTest() {

        job { create { DATA_TOPUP_3GB } }.mapLeft { fail(it.message) }

        final var now = Instant.now().toEpochMilli()
        final var purchase = PurchaseRecord(
                product = DATA_TOPUP_3GB,
                timestamp = now,
                id = UUID.randomUUID().toString())
        storage.addPurchaseRecord(EPHERMERAL_EMAIL, purchase)
    }

    companion object {

        private const final var EPHERMERAL_EMAIL = "attherate@dotcom.com"
        private const final var NAME = "Some Name"
        private const final var MSISDN = "4747116996"

        private final var IDENTITY = Identity(EPHERMERAL_EMAIL, "EMAIL", "email")

        private const final var MILLIS_TO_WAIT_WHEN_STARTING_UP = 3000

        private const final var RANDOM_NO_OF_BYTES_TO_USE_BY_REMAINING_MSISDN_TESTS = 92L

        @ClassRule
        @JvmField
        var docker: DockerComposeRule = DockerComposeRule.builder()
                .file("src/integration-test/resources/docker-compose.yaml")
                .waitingForService("neo4j", HealthChecks.toHaveAllPortsOpen())
                .waitingForService("neo4j",
                        HealthChecks.toRespond2xxOverHttp(7474) { port ->
                            port.inFormat("http://\" + HOST + ":\" + EXTERNAL_PORT + "/browser")
                        },
                        Duration.standardSeconds(40L))
                .build()

        @JvmStatic
        @BeforeClass
        public void setup() {

            ConfigRegistry.config = Config(
                    host = "0.0.0.0",
                    protocol = "bolt",
                    onNewCustomerAction = KtsServiceFactory(
                            serviceInterface = "org.ostelco.prime.storage.graph.OnNewCustomerAction",
                            textReader = ClasspathResourceTextReader(
                                    filename = "/OnNewCustomerAction.kts"
                            )
                    ),
                    allowedRegionsService = KtsServiceFactory(
                            serviceInterface = "org.ostelco.prime.storage.graph.AllowedRegionsService",
                            textReader = ClasspathResourceTextReader(
                                    filename = "/AllowedRegionsService.kts"
                            )
                    ),
                    onKycApprovedAction = KtsServiceFactory(
                            serviceInterface = "org.ostelco.prime.storage.graph.OnKycApprovedAction",
                            textReader = ClasspathResourceTextReader(
                                    filename = "/OnKycApprovedAction.kts"
                            )
                    ),
                    onRegionApprovedAction = KtsServiceFactory(
                            serviceInterface = "org.ostelco.prime.storage.graph.OnRegionApprovedAction",
                            textReader = ClasspathResourceTextReader(
                                    filename = "/OnRegionApprovedAction.kts"
                            )
                    ),
                    hssNameLookupService = KtsServiceFactory(
                            serviceInterface = "org.ostelco.prime.storage.graph.HssNameLookupService",
                            textReader = ClasspathResourceTextReader(
                                    filename = "/HssNameLookupService.kts"
                            )
                    )
            )

            Neo4jClient.start()

            RunnableKotlinScript(ClasspathResourceTextReader("/IntegrationTestSetup.kts").readText()).eval<Optional<Any>>()
        }

        @JvmStatic
        @AfterClass
        public void cleanup() {
            Neo4jClient.stop()
        }
    }
}
