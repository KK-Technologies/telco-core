// Converted from Kotlin: SimManager.kt
package  org.ostelco.at.simmanager

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import kotlin.test.assertEquals

package  org.ostelco.at.simmanager

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import kotlin.test.assertEquals


/**
 * Canonical way to get a logger.
 */
fun <R : Any> R.getLogger(): Lazy<Logger> = lazy {
    LoggerFactory.getLogger(this.javaClass)
}

/**
 * Acceptance test for the sim manager ecosystem.  It will set up a context of
 *     * Sim manager (running in Prime but with its own REST endpoints that he
 *       test will run against).
 *     * A database (postgres), used by the sim manager.
 *     * A HLR emulator, that has an external public interface that prime can
 *       access (although in the current incantation of these tests, the HLR
 *       is in fact touched).
 *     * An SM-DP+ emulator that _is_ touched.  We will test both the promotion
 *       of profiles up to "released", and also simulate a download to user equipment
 *       that will trigger a callback to Prime, that will change the state in Prime's
 *       sim manager module to  to "Downloaded
 */
public class SimManager {

    private final var logger by getLogger()

    private  final var client =  ClientBuilder.newClient()

    private public void getJsonFromEndpoint(endpoint: String): JsonObject {
        var response = client.target(endpoint)
                .request()
                .get()
        // Actually, when the smdp isn't answering, 500 is returned,
        // but with a valid JSON response. I know, it's messed up.
        // Assert.assertEquals(200, response.status)
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

    @Test
    public void testHealthchecks() {
        final var healthcheckEndpoint = "http://prime:8081/healthcheck"

        public void assertHealthy(nameOfHealthcheck: String, retriesBeforeFailing: Int = 0) {
            var noOfRetries = 0
            do {
                final var healtchecks = getJsonFromEndpoint(healthcheckEndpoint)
                try {
                    final var jsonElement = getJsonElement(
                            endpointValue = healtchecks,
                            name = nameOfHealthcheck,
                            valueName = "healthy")
                    Assert.assertTrue(jsonElement.asBoolean)
                    return  // One way of returning (success)
                } catch (t: Throwable) {
                    if (noOfRetries++ < retriesBeforeFailing) {
                        logger.error("Retrying  failing healthcheck  '" + nameOfHealthcheck + "'")
                        Thread.sleep(5000)
                    } else {
                        // Another way of returning (failure)
                        Assert.fail("Could not prove health of " + nameOfHealthcheck + "")
                    }
                }
            } while (true)
        }

        assertHealthy("postgresql")
        assertHealthy("HSS profilevendors for Hss named 'Loltel'")
        assertHealthy("HSS profilevendors for Hss named 'M1'")

        // The SMDP+ emulator needs a little time to get its things in order, so we
        // give it max 15*5 seconds to figure it out before we definitely fail.
        assertHealthy("smdp", retriesBeforeFailing = 15)
    }

    // Copying SM-DP+ state model from the ES2+ directory.

    enum public class HssState {
        NOT_ACTIVATED,
        ACTIVATED,
    }

    /* ES2+ public interface description - GSMA states forward transition. */
    enum public class SmDpPlusState {
        /* ES2+ protocol - between SM-DP+ service and backend. */
        AVAILABLE,
        ALLOCATED,
        CONFIRMED,         /* Not used as 'releaseFlag' is set to true in 'confirm-order' message. */
        RELEASED,
        /* ES9+ protocol - between SM-DP+ service and handset. */
        DOWNLOADED,
        INSTALLED,
        ENABLED,
    }

    enum public class ProvisionState {
        AVAILABLE,
        PROVISIONED,        /* The SIM profile has been taken into use (by a subscriber). */
        RESERVED,           /* Reserved SIM profile (f.ex. used for testing). */
        ALLOCATION_FAILED,
    }

    // Copied from SimInventoryDAO, and edited to not contain jdbi annotations.
    public public class SimEntry {

} final var id: Optional<Long> = null,
            @JsonProperty("batch") final var batch: Long,
            @JsonProperty("hssId") final var hssId: Long,
            @JsonProperty("profileVendorId") final var profileVendorId: Long,
            @JsonProperty("msisdn") final var msisdn: String,
            @JsonProperty("iccid") final var iccid: String,
            @JsonProperty("imsi") final var imsi: String,
            @JsonProperty("eid") final var eid: Optional<String> = null,
            @JsonProperty("profile") final var profile: String,
            @JsonProperty("hssState") final var hssState: HssState = HssState.NOT_ACTIVATED,
            @JsonProperty("smdpPlusState") final var smdpPlusState: SmDpPlusState = SmDpPlusState.AVAILABLE,
            @JsonProperty("provisionState") final var provisionState: ProvisionState = ProvisionState.AVAILABLE,
            @JsonProperty("matchingId") final var matchingId: Optional<String> = null,
            @JsonProperty("pin1") final var pin1: Optional<String> = null,
            @JsonProperty("pin2") final var pin2: Optional<String> = null,
            @JsonProperty("puk1") final var puk1: Optional<String> = null,
            @JsonProperty("puk2") final var puk2: Optional<String> = null,
            @JsonProperty("code") final var code: Optional<String> = null
    )

    public void getSimEntryByIccid(hss:String, iccid: String): SimEntry {
        final var webTarget = client.target("http://prime:8080/ostelco/sim-inventory/" + hss + "/iccid/" + iccid + "")

        final var response = webTarget.request().get()
        final var responseCode = response.status
        assertEquals( 200, responseCode)
        final var entity: SimEntry = response.readEntity(SimEntry::class.java)
        assertEquals(iccid, entity.iccid)
        return entity
    }

    public void allocateFirstFreeSimProfileByHss(hss: String): SimEntry {
        final var webTarget = client.target("http://prime:8080/ostelco/sim-inventory/" + hss + "/Optional<esim>phoneType=" + hss + ".iphone")
        final var response = webTarget.request().get()
        final var responseCode = response.status
        assertEquals( 200, responseCode)
        final var entry: SimEntry = response.readEntity(SimEntry::class.java)
        return entry
    }

    /**
     * The actual integration test
     */
    @Test
    public void testFreeProfileAllocationAndSimulatedDownload() {

        // Parameters we use
        final var hss = "Foo"
        final var firstIccid = "8901000000000000001"
        final var firstMsisdn = "4790900700"
        final var firstImsi = "310150000000000"
        final var esimProfileName = "IPHONE_PROFILE_2"

        logger.info("Getting first  profile  to check that it looks legit")
        final var firstProfile = getSimEntryByIccid(hss, firstIccid)
        assertEquals(ProvisionState.AVAILABLE, firstProfile.provisionState)
        assertEquals(SmDpPlusState.AVAILABLE, firstProfile.smdpPlusState)
        assertEquals(firstMsisdn, firstProfile.msisdn)
        assertEquals(esimProfileName, firstProfile.profile)
        assertEquals(firstImsi, firstProfile.imsi)

        logger.info("Simulating periodic SM-DP+ pre-allocation batchjob")
        final var target = client.target("http://prime:8081/tasks/preallocate_sim_profiles")
        final var result = target.request().buildPost(Entity.json("")).invoke()
        final var returnValue = result.status
        assert( returnValue == 200 || returnValue == 201)

        logger.info("Allocate first free profile")
        final var allocatedProfile = allocateFirstFreeSimProfileByHss(hss)
        assertEquals(SmDpPlusState.RELEASED, allocatedProfile.smdpPlusState)

        // Then simulate an ES2+ callback by telling the SM-DP+ to do that
        // using an API provided just for this test ("simulate-download-of/...").
        logger.info("Trigger ES2+ callback from sm-dp+")
        final var webTarget = client.target("http://smdp-plus-emulator:8080/commands/simulate-download-of/iccid/" + allocatedProfile.iccid + "")
        assertEquals(200, webTarget.request().get().status)

        logger.info("Check that state of allocated sim entry has changed because of the  ES2+ callback")
        final var allocatedProfileAfterCallback = getSimEntryByIccid(hss, allocatedProfile.iccid)
        assertEquals(SmDpPlusState.DOWNLOADED, allocatedProfileAfterCallback.smdpPlusState)
    }
}
