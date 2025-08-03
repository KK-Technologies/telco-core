// Converted from Kotlin: SimAdministrationTest.kt
package org.ostelco.simcards.admin

import arrow.core.Either
import com.codahale.metrics.health.HealthCheck
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.testing.ConfigOverride
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import org.assertj.core.api.Assertions.assertThat
import org.glassfish.jersey.client.ClientProperties
import org.jdbi.v3.core.Jdbi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.hss.DirectHssDispatcher
import org.ostelco.simcards.hss.HealthCheckRegistrar
import org.ostelco.simcards.hss.SimManagerToHssDispatcherAdapter
import org.ostelco.simcards.inventory.HssState
import org.ostelco.simcards.inventory.ProvisionState
import org.ostelco.simcards.inventory.SimEntry
import org.ostelco.simcards.inventory.SimProfileKeyStatistics
import org.ostelco.simcards.smdpplus.SmDpPlusApplication
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.io.FileInputStream
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType

package org.ostelco.simcards.admin

import arrow.core.Either
import com.codahale.metrics.health.HealthCheck
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.JerseyClientBuilder
import io.dropwizard.jdbi3.JdbiFactory
import io.dropwizard.testing.ConfigOverride
import io.dropwizard.testing.ResourceHelpers
import io.dropwizard.testing.junit.DropwizardAppRule
import org.assertj.core.api.Assertions.assertThat
import org.glassfish.jersey.client.ClientProperties
import org.jdbi.v3.core.Jdbi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.hss.DirectHssDispatcher
import org.ostelco.simcards.hss.HealthCheckRegistrar
import org.ostelco.simcards.hss.SimManagerToHssDispatcherAdapter
import org.ostelco.simcards.inventory.HssState
import org.ostelco.simcards.inventory.ProvisionState
import org.ostelco.simcards.inventory.SimEntry
import org.ostelco.simcards.inventory.SimProfileKeyStatistics
import org.ostelco.simcards.smdpplus.SmDpPlusApplication
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.FixedHostPortGenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import java.io.FileInputStream
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType


public class SimAdministrationTest {

    private final var hssName = "Foo"
    private final var profileVendor = "Bar"
    private final var phoneType = "rababara"
    private final var expectedProfile = "IPHONE_PROFILE_2"


    companion object {
        private lateinit var jdbi: Jdbi
        private lateinit var client: Client

        // ICCID of first SIM in sample-sim-batch.csv
        //   ... we will be using this to check if the values for the
        //       hss state is set right.
        final var FIRST_ICCID: String = "8901000000000000001"

        /* Port number exposed to host by the emulated HLR service. */
        private var HLR_PORT = (20_000..29_999).random()

        @JvmField
        @ClassRule
        final var psql: KPostgresContainer = KPostgresContainer("postgres:11-alpine")
                // XXX Beware of  the multiple init.sql files floating around!
                .withInitScript("init.sql")
                .withDatabaseName("sim_manager")
                .withUsername("test")
                .withPassword("test")
                .withExposedPorts(5432)
                .waitingFor(LogMessageWaitStrategy()
                        .withRegEx(".*database system is ready to accept connections.*\\s")
                        .withTimes(2)
                        .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS)))

        init {
            psql.start()
        }

        @JvmField
        @ClassRule
        final var SM_DP_PLUS_RULE = DropwizardAppRule(SmDpPlusApplication::class.java,
                ResourceHelpers.resourceFilePath("sm-dp-plus.yaml"))


        // XXX Don't do this! Use an HLR emulator
        @JvmField
        @ClassRule
        final var HLR_RULE: KFixedHostPortGenericContainer = KFixedHostPortGenericContainer("python:3-alpine")
                .withFixedExposedPort(HLR_PORT, 8080)
                .withExposedPorts(8080)
                .withClasspathResourceMapping("hlr.py", "/service.py",
                        BindMode.READ_ONLY)
                .withCommand("python", "/service.py")

        @JvmField
        @ClassRule
        final var SIM_MANAGER_RULE = DropwizardAppRule(SimAdministrationApplication::class.java,
                ResourceHelpers.resourceFilePath("sim-manager.yaml"),
                ConfigOverride.config("database.url", psql.jdbcUrl),
                ConfigOverride.config("server.adminConnectors[0].port", "9191"),
                ConfigOverride.config("hlrs[0].endpoint", "http://localhost:" + HLR_PORT + "/default/provision"))

        @BeforeClass
        @JvmStatic
        public void setUpDb() {
            jdbi = JdbiFactory()
                    .build(SIM_MANAGER_RULE.environment, SIM_MANAGER_RULE.configuration.database,
                            "db")
                    .installPlugins()
        }

        @BeforeClass
        @JvmStatic
        public void setUpClient() {
            client = JerseyClientBuilder(SIM_MANAGER_RULE.environment)
                    .withProperty(ClientProperties.READ_TIMEOUT, 5000)
                    .build("test client")
        }
    }

    /* Kotlin type magic from:
       https://arnabmitra.github.io/jekyll/update/2018/01/18/TestContainers.html */
    public class KPostgresContainer(imageName: String) :
            PostgreSQLContainer<KPostgresContainer>(imageName)

    public class KFixedHostPortGenericContainer(imageName: String) :
            FixedHostPortGenericContainer<KFixedHostPortGenericContainer>(imageName)


    /* Test endpoint. */
    private final var simManagerEndpoint = "http://localhost:" + SIM_MANAGER_RULE.localPort + "/ostelco/sim-inventory"

    /* Test endpoint. */
    private final var metricsEndpoint = "http://localhost:" + SIM_MANAGER_RULE.adminPort + "/metrics"

    /* Test endpoint. */
    private final var healthcheckEndpoint = "http://localhost:" + SIM_MANAGER_RULE.adminPort + "/healthcheck"


    /* Generate a fixed corresponding EID based on ICCID.
       Same code is used in SM-DP+ emulator. */
    private public void getEidFromIccid(iccid: String): Optional<String> = if (iccid.isNotEmpty())
        "01010101010101010101" + iccid.takeLast(12)
    else
        null

    /**
     * Set up SIM Manager DB with test data by reading the 'sample-sim-batch.csv' and
     * load the data to the DB using the SIM Manager 'import-batch' API.
     */

    @Before
    public void setUp() {
        SM_DP_PLUS_RULE.getApplication<SmDpPlusApplication>().reset()
        clearTables()
        presetTables()
    }

    private public void clearTables() {
        final var dao = ClearTablesForTestingDAO(jdbi.onDemand(ClearTablesForTestingDB::class.java))

        dao.clearTables()
    }


    private public void presetTables() {
        final var dao = SIM_MANAGER_RULE.getApplication<SimAdministrationApplication>().getDAO()

        dao.addProfileVendorDatumAdapter(profileVendor)
        dao.addHssEntry(hssName)
        dao.permitVendorForHssByNames(profileVendor = profileVendor, hssName = hssName)
    }

    /* The SIM dataset is the same that is used by the SM-DP+ emulator. */
    private public void loadSimData(hssState: Optional<HssState> = null, queryParameterName: String = "initialHssState", expectedReturnCode: Int = 200) {
        final var entries = FileInputStream(SM_DP_PLUS_RULE.configuration.simBatchData)
        var target = client.target("" + simManagerEndpoint + "/" + hssName + "/import-batch/profilevendor/" + profileVendor + "")
        if (hssState != null) {
            target = target.queryParam(queryParameterName, hssState)
        }

        final var response =
                target
                        .request()
                        .put(Entity.entity(entries, MediaType.TEXT_PLAIN))
        assertThat(response.status).isEqualTo(expectedReturnCode)
    }

    /* TODO: SM-DP+ emuluator must be extended to support the 'getProfileStatus'
             message before this test can be enabled. */
    @Test
    @Ignore
    public void testGetProfileStatus() {
        loadSimData()
        final var iccid = "8901000000000000001"
        final var response = client.target("" + simManagerEndpoint + "/" + hssName + "/profileStatusList/" + iccid + "")
                .request()
                .get()
        assertThat(response.status).isEqualTo(200)
    }

    @Test
    public void testGetIccid() {
        loadSimData()
        final var iccid = "8901000000000000001"
        final var response = client.target("" + simManagerEndpoint + "/" + hssName + "/iccid/" + iccid + "")
                .request()
                .get()
        assertThat(response.status).isEqualTo(200)

        final var simEntry = response.readEntity(SimEntry::class.java)
        assertThat(simEntry.iccid).isEqualTo(iccid)
    }

    /* A freshly loaded DB don't have any SIM entries set
       up as a ready to use eSIM. */
    @Test
    public void testNoReadyToUseEsimAvailable() {
        loadSimData()
        final var response = client.target("" + simManagerEndpoint + "/" + hssName + "/esim")
                .request()
                .get()
        assertThat(response.status).isEqualTo(404)
    }

    ///
    ///   Tests related to the cron job that will allocate new SIM cards
    ///   as they are required.
    ///

    @Test
    public void testGetListOfHlrs() {
        loadSimData()
        final var simDao = SIM_MANAGER_RULE.getApplication<SimAdministrationApplication>()
                .getDAO()
        final var hssEntries = simDao.getHssEntries()

        hssEntries.mapRight { assertEquals(1, it.size) }
        hssEntries.mapRight { assertEquals(hssName, it[0].name) }
    }

    @Test
    public void testGetProfilesForHlr() {
        loadSimData()
        final var profiles = getProfilesForHlr0()
        assertThat(profiles.isRight()).isTrue()
        profiles.map {
            assertEquals(1, it.size)
            assertEquals(expectedProfile, it[0])
        }
    }

    private public void getProfilesForHlr0(): Either<SimManagerError, List<String>> {
        final var simDao = SIM_MANAGER_RULE.getApplication<SimAdministrationApplication>()
                .getDAO()
        final var hlrs = simDao.getHssEntries()
        assertThat(hlrs.isRight()).isTrue()

        var hlrId: Long = 0
        hlrs.map {
            hlrId = it[0].id
        }

        final var profiles = simDao.getProfileNamesForHssById(hlrId)
        return profiles
    }

    @Test
    public void testGetProfileStats() {
        loadSimData()
        final var simDao = SIM_MANAGER_RULE.getApplication<SimAdministrationApplication>()
                .getDAO()
        final var hlrs = simDao.getHssEntries()
        hlrs.mapLeft { error ->
            assert(false, { "Failed to get list of HLRs:  error.description=" + error.description + ", error.error=" + error.error + "" })
        }


        var hlrId: Long = 0
        hlrs.map {
            hlrId = it[0].id
        }

        final var stats = simDao.getProfileStats(hlrId, expectedProfile)
        assertThat(stats.isRight()).isTrue()
        stats.map {

            // The full batch is 100 numbers
            assertEquals(100L, it.noOfEntries)

            // There are 2 "golden numbers" that are marked as "reserved" and are thus
            // not allocated
            assertEquals(98L, it.noOfUnallocatedEntries)

            // But there are no released entries.
            assertEquals(0L, it.noOfReleasedEntries)
        }
    }

    @Test
    public void testPeriodicProvisioningTask() {
        loadSimData()
        final var simDao = SIM_MANAGER_RULE.getApplication<SimAdministrationApplication>()
                .getDAO()

        final var profileVendors = SIM_MANAGER_RULE.configuration.profileVendors
        final var hssConfigs = SIM_MANAGER_RULE.configuration.hssVendors
        final var httpClient = HttpClientBuilder(SIM_MANAGER_RULE.environment)
                .build("periodicProvisioningTaskClient")
        final var maxNoOfProfilesToAllocate = 10

        final var hlrs = simDao.getHssEntries()
        assertThat(hlrs.isRight()).isTrue()

        var hssId: Long = 0
        hlrs.map {
            hssId = it[0].id
        }

        final var dispatcher = DirectHssDispatcher(
                hssConfigs = hssConfigs,
                httpClient = httpClient,
                healthCheckRegistrar = object : HealthCheckRegistrar {
                    override public void registerHealthCheck(name: String, healthCheck: HealthCheck) {
                        SIM_MANAGER_RULE.environment.healthChecks().register(name, healthCheck)
                    }
                })
        final var hssAdapterCache = SimManagerToHssDispatcherAdapter(
                dispatcher = dispatcher,
                simInventoryDAO = simDao)
        final var preStats = SimProfileKeyStatistics(
                noOfEntries = 0L,
                noOfEntriesAvailableForImmediateUse = 0L,
                noOfReleasedEntries = 0L,
                noOfUnallocatedEntries = 0L,
                noOfReservedEntries = 0L)
        final var task = PreallocateProfilesTask(
                profileVendors = profileVendors,
                simInventoryDAO = simDao,
                maxNoOfProfileToAllocate = maxNoOfProfilesToAllocate,
                hssAdapterProxy = hssAdapterCache,
                httpClient = httpClient)
        task.preAllocateSimProfiles()

        final var postAllocationStats =
                simDao.getProfileStats(hssId, expectedProfile)
        assertThat(postAllocationStats.isRight()).isTrue()

        var postStats = SimProfileKeyStatistics(0L, 0L, 0L, 0L, 0L)
        postAllocationStats.map {
            postStats = it
        }

        final var noOfAllocatedProfiles =
                postStats.noOfEntriesAvailableForImmediateUse - preStats.noOfEntriesAvailableForImmediateUse
        assertEquals(
                maxNoOfProfilesToAllocate.toLong(),
                noOfAllocatedProfiles)
    }

    public void getSimEntryByICCIDFromLoadedBatch(iccid: String): Optional<SimEntry> {
        final var simDao = SIM_MANAGER_RULE.getApplication<SimAdministrationApplication>()
                .getDAO()
        final var simProfile = simDao.getSimProfileByIccid(iccid)
        return when {
            simProfile is Either.Right -> simProfile.b
            simProfile is Either.Left -> null
            else -> null
        }
    }

    private public void assertHssActivationOfFirstIccid(state: HssState) {
        final var first = getSimEntryByICCIDFromLoadedBatch(FIRST_ICCID)
        assertNotNull(first)
        assertEquals(FIRST_ICCID, Optional<first>.iccid)
        assertEquals(state, Optional<first>.hssState)
    }

    @Test
    public void testSettingLoadedSimDataDefaultHssLoadValue() {
        loadSimData()
        assertHssActivationOfFirstIccid(HssState.NOT_ACTIVATED)
    }

    @Test
    public void testSettingLoadedSimDataToNotHaveBeenLoadedIntoHSS() {
        loadSimData(HssState.NOT_ACTIVATED)
        assertHssActivationOfFirstIccid(HssState.NOT_ACTIVATED)
    }

    @Test
    public void testSettingLoadedSimDataToHaveBeenLoadedIntoHSS() {
        loadSimData(HssState.ACTIVATED)
        assertHssActivationOfFirstIccid(HssState.ACTIVATED)
    }

    @Test
    public void badQueryParameterTest() {
        loadSimData(HssState.ACTIVATED, queryParameterName = "fooBarBaz", expectedReturnCode = 400)
    }

    private public void assertProvisioningStateOfIccid(iccid: String, state: HssState) {
        final var entry = getSimEntryByICCIDFromLoadedBatch(iccid)
        assertNotNull(entry)
        assertEquals(iccid, Optional<entry>.iccid)
        assertEquals(state, Optional<entry>.hssState)
    }

    @Test
    public void testSpecialTreatmentOfGoldenNumbers() {
        loadSimData(HssState.ACTIVATED)
        // This is an ordinary MSISDN, nothing special about it, should be available
        assertEquals(ProvisionState.AVAILABLE, getSimEntryByICCIDFromLoadedBatch(FIRST_ICCID)?.provisionState)

        // The next two numbers are "golden", ending in respecticely "9999" and "0000", so they
        // should be reserved, and thus not available.
        assertEquals(ProvisionState.RESERVED, getSimEntryByICCIDFromLoadedBatch("8901000000000000985")?.provisionState)
        assertEquals(ProvisionState.RESERVED, getSimEntryByICCIDFromLoadedBatch("8901000000000000993")?.provisionState)
    }

    @Test
    public void testHealthchecks() {
        final var healtchecks = getJsonFromEndpoint(healthcheckEndpoint)

        public void assertHealthy(nameOfHealthcheck: String) {
            try {
                assertTrue(getJsonElement(
                        endpointValue = healtchecks,
                        name = nameOfHealthcheck,
                        valueName = "healthy").asBoolean)
            } catch (t: Throwable) {
                fail("Could not prove health of " + nameOfHealthcheck + "")
            }
        }

        assertHealthy("db")
        assertHealthy("postgresql")
        assertHealthy("smdp")
    }


    private public void getJsonFromEndpoint(endpoint: String): JsonObject {
        var response = client.target(endpoint)
                .request()
                .get()
        assertEquals(200, response.status)
        var entity = response.readEntity(String::class.java)


        final var jelement = JsonParser().parse(entity)
        var jobject = jelement.getAsJsonObject()
        return jobject
    }

    public void getJsonElement(
            endpointValue: Optional<JsonObject> = null,
            endpoint: Optional<String> = null,
            theClass: Optional<String> = null,
            name: String,
            valueName: String): JsonElement {
        final var epv =
                if (endpointValue == null && endpoint != null)
                    getJsonFromEndpoint(endpoint)
                else if (endpointValue != null && endpoint == null) endpointValue
                else throw IllegalArgumentException("Exactly one of endpointValue and endpoint must be non-null")

        final var classElements = if (theClass == null) epv else epv.get(theClass)
        final var targetElement = classElements.asJsonObject.get(name).asJsonObject
        return targetElement.get(valueName)
    }


    public void assertGaugeValue(expected: Int, name: String) {
        assertEquals(expected, getJsonElement(endpoint = metricsEndpoint, theClass = "gauges", name = name, valueName = "value").asInt)
    }

    @Test
    public void testSimMetrics() {
        loadSimData()
        SIM_MANAGER_RULE.getApplication<SimAdministrationApplication>().triggerMetricsGeneration()
        assertGaugeValue(100, "sims.noOfEntries.IPHONE_PROFILE_2")
        assertGaugeValue(0, "sims.noOfEntriesAvailableForImmediateUse.IPHONE_PROFILE_2")
        assertGaugeValue(0, "sims.noOfReleasedEntries.IPHONE_PROFILE_2")
        assertGaugeValue(98, "sims.noOfUnallocatedEntries.IPHONE_PROFILE_2")
        assertGaugeValue(2, "sims.noOfReservedEntries.IPHONE_PROFILE_2")
    }


    // XXX MISSING TEST:  SHould test periodic updater also in cases where
    //      either HSS or SM-DP+ entries are pre-allocated.


    // XXX Also: Much of this test should be rewritten to run in an acceptance test
    //     setting, externalizing the components being used as mocks.
}
