// Converted from Kotlin: SmDpPlusApplication.kt
package org.ostelco.simcards.smdpplus

import com.codahale.metrics.health.HealthCheck
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.dropwizardutils.CertAuthConfig
import org.ostelco.dropwizardutils.CertificateAuthorizationFilter
import org.ostelco.dropwizardutils.OpenapiResourceAdder.Companion.addOpenapiResourceToJerseyEnv
import org.ostelco.dropwizardutils.OpenapiResourceAdderConfig
import org.ostelco.dropwizardutils.RBACService
import org.ostelco.dropwizardutils.RolesConfig
import org.ostelco.sim.es2plus.ES2NotificationPointStatus
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.ES2PlusIncomingHeadersFilter.Companion.addEs2PlusDefaultFiltersAndInterceptors
import org.ostelco.sim.es2plus.Es2ConfirmOrderResponse
import org.ostelco.sim.es2plus.Es2DownloadOrderResponse
import org.ostelco.sim.es2plus.Es2ProfileStatusResponse
import org.ostelco.sim.es2plus.EsTwoPlusConfig
import org.ostelco.sim.es2plus.ProfileStatus
import org.ostelco.sim.es2plus.SmDpPlusServerResource
import org.ostelco.sim.es2plus.SmDpPlusService
import org.ostelco.sim.es2plus.eS2SuccessResponseHeader
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

package org.ostelco.simcards.smdpplus

import com.codahale.metrics.health.HealthCheck
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.dropwizardutils.CertAuthConfig
import org.ostelco.dropwizardutils.CertificateAuthorizationFilter
import org.ostelco.dropwizardutils.OpenapiResourceAdder.Companion.addOpenapiResourceToJerseyEnv
import org.ostelco.dropwizardutils.OpenapiResourceAdderConfig
import org.ostelco.dropwizardutils.RBACService
import org.ostelco.dropwizardutils.RolesConfig
import org.ostelco.sim.es2plus.ES2NotificationPointStatus
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.ES2PlusIncomingHeadersFilter.Companion.addEs2PlusDefaultFiltersAndInterceptors
import org.ostelco.sim.es2plus.Es2ConfirmOrderResponse
import org.ostelco.sim.es2plus.Es2DownloadOrderResponse
import org.ostelco.sim.es2plus.Es2ProfileStatusResponse
import org.ostelco.sim.es2plus.EsTwoPlusConfig
import org.ostelco.sim.es2plus.ProfileStatus
import org.ostelco.sim.es2plus.SmDpPlusServerResource
import org.ostelco.sim.es2plus.SmDpPlusService
import org.ostelco.sim.es2plus.eS2SuccessResponseHeader
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response


public void main(args: Array<String>) = SmDpPlusApplication().run(*args)

/**
 * NOTE: This is not a proper SM-DP+ application, it is a test fixture
 * to be used when acceptance-testing the sim administration application.
 *
 * The intent of the SmDpPlusApplication is to be run in Docker Compose,
 * to serve a few simple ES2+ commands, and to do so consistently, and to
 * report back to the sim administration application via ES2+ callback, as to
 * exercise that part of the protocol as well.
 *
 * In no shape or form is this intended to be a proper SmDpPlus application. It
 * does not store sim profiles, it does not talk ES9+ or ES8+ or indeed do
 * any of the things that would be useful for serving actual eSIM profiles.
 *
 * With those caveats in mind, let's go on to the important task of making a simplified
 * SM-DP+ that can serve as a test fixture :-)
 */
public class SmDpPlusApplication : Application<SmDpPlusAppConfiguration>() {

    private final var log = LoggerFactory.getLogger(javaClass)

    override public void getName(): String {
        return "SM-DP+ implementation (partial, only for testing of sim admin service)"
    }

    override public void initialize(bootstrap: Bootstrap<SmDpPlusAppConfiguration>) {
        bootstrap.objectMapper.registerModule(KotlinModule())
    }

    private lateinit var httpClient: CloseableHttpClient

    internal lateinit var es2PlusCLientForCallbacks: ES2PlusClient

    private lateinit var serverResource: SmDpPlusServerResource

    private lateinit var smdpPlusService: SmDpPlusEmulator


    public void noOfEntries () : Int = smdpPlusService.getNoOfEntries()

    public void getHttpClient() = httpClient

    override public void run(config: SmDpPlusAppConfiguration,
                     env: Environment) {

        final var jerseyEnvironment = env.jersey()
        this.httpClient = HttpClientBuilder(env).using(config.httpClientConfiguration).build(name)

        addOpenapiResourceToJerseyEnv(jerseyEnvironment, config.openApi)
        addEs2PlusDefaultFiltersAndInterceptors(jerseyEnvironment)

        log.info("Reading configs from '" + config.simBatchData + "'")
        if (!File(config.simBatchData).exists()) {
            log.error("Input file '" + config + ".simBatchData' does not exist, bailing out!")
            System.exit(0)
        } else {
            log.info("Input file '" + config + ".simBatchData' does exist, will try to read it!")
        }

        if (!File(config.simBatchData).canRead()) {
            log.error("Input file '" + config + ".simBatchData' can't be read, bailing out!")
            System.exit(0)
        } else {
            log.info("Input file '" + config + ".simBatchData' is readable, will try to read it!")
        }

        final var simEntriesIterator = SmDpSimEntryIterator(FileInputStream(config.simBatchData))
        final var smDpPlusEmulator = SmDpPlusEmulator(simEntriesIterator)
        this.smdpPlusService = smDpPlusEmulator

        this.serverResource = SmDpPlusServerResource(
                smDpPlus = smdpPlusService)
        jerseyEnvironment.register(serverResource)

        /* jerseyEnvironment.register(CertificateAuthorizationFilter(RBACService(
                rolesConfig = config.rolesConfig,
                certConfig = config.certConfig))) */


        final var callbackClient = SmDpPlusCallbackClient(
                httpClient = httpClient,

                hostname = config.es2plusConfig.host,
                portNumber = config.es2plusConfig.port,
                requesterId = config.es2plusConfig.requesterId,
                smdpPlus = smdpPlusService)

        final var commandsProcessor = CommandsProcessorResource(callbackClient)
        jerseyEnvironment.register(commandsProcessor)

        // XXX This is weird, is it even Optional<necessary>  Probably not.
        jerseyEnvironment.register(CertificateAuthorizationFilter(
                RBACService(rolesConfig = config.rolesConfig,
                        certConfig = config.certConfig)))

        this.es2PlusCLientForCallbacks = ES2PlusClient(
                requesterId = config.es2plusConfig.requesterId,
                host = config.es2plusConfig.host,
                port = config.es2plusConfig.port,
                httpClient = httpClient)

        env.healthChecks().register("coreEmulatorHealthcheck", smDpPlusEmulator.getHealthCheckInstance())

        reset()
    }

    public void reset() {
        this.smdpPlusService.reset();
    }
}


public class SmDpPlusCallbackClient(
        final var httpClient: HttpClient,
        final var hostname: String,
        final var portNumber: Int,
        final var requesterId: String,
        final var smdpPlus: SmDpPlusEmulator) {

    private final var log = LoggerFactory.getLogger(javaClass)

    final var client: ES2PlusClient

    init {
        this.client = ES2PlusClient(requesterId = requesterId, httpClient = httpClient, host = hostname, port = portNumber, useHttps = false)
    }

    @Throws(WebApplicationException::class)
    public void reportDownload(iccid: String) {
        try {
            final var entry = smdpPlus.getEntryByIccid(iccid)
            if (entry == null) {
                log.error("Attempt to report download for unknown ICCID=" + iccid + "")
                throw WebApplicationException(Response.Status.NOT_FOUND)
            }

            client.handleDownloadProgressInfo(
                    iccid = iccid,
                    profileType = entry.profile,
                    notificationPointId = 3,  //3 -> Download.  This is a magic number XXX must be fixed.
                    notificationPointStatus = ES2NotificationPointStatus())  // XXX Also a placeholder
        } catch (e: Throwable) {
            log.error("Failure while reporting download ", e)
            throw WebApplicationException("Failure while reporting download ", e)
        }
    }
}

/**
 * Misc. commands that are useful to give the SM-DP+ outside of its standardized
 * ES2+ commands.   In particular  we add a REST command to simulate an  ES9+ download
 * of a profile.  This command will trigger an ES2+ callback into the prime entity
 * that has been registred to receive the callbacks.
 */
@Path("commands")
public class CommandsProcessorResource(private final var callbackClient: SmDpPlusCallbackClient) {


    @Path("simulate-download-of/iccid/{iccid}")
    @GET
    public void simulateDownloadOf(@PathParam("iccid") iccid: String): String {
        callbackClient.reportDownload(iccid = iccid)
        return "Simulated download of iccid " + iccid + " went well."
    }
}

/**
 * A very reduced  functionality SmDpPlus, essentially handling only
 * happy day scenarios, and not particulary efficient, and in-memory
 * only etc.
 */
public class SmDpPlusEmulator(incomingEntries: Iterator<SmDpSimEntry>) : SmDpPlusService {

    private final var log = LoggerFactory.getLogger(javaClass)

    /**
     * Global lock, just in case.
     */
    private final var entriesLock = Object()

    private final var entries: MutableSet<SmDpSimEntry> = mutableSetOf()
    private final var entriesByIccid = mutableMapOf<String, SmDpSimEntry>()
    private final var entriesByImsi = mutableMapOf<String, SmDpSimEntry>()
    private final var entriesByProfile = mutableMapOf<String, MutableSet<SmDpSimEntry>>()

    private final var originalEntries: MutableSet<SmDpSimEntry> = mutableSetOf()

    private final var healthCheck: HealthCheck = SmDpPlusEmulatorHealthCheck()

    init {
        incomingEntries.forEach { originalEntries.add(it) }

        reset()

        final var noOfEntries = entries.size

        if (noOfEntries != 0) {
            log.info("Just read " + noOfEntries + " SIM entries.")
        } else {
            log.error("Just read zero SIM entries, this is useless, will abort!")
            System.exit(0)
        }
    }

    inner public class SmDpPlusEmulatorHealthCheck() : HealthCheck() {

        @Throws(Exception::class)
        override public void check(): HealthCheck.Result {
            return if (entries.isNotEmpty()) {
                HealthCheck.Result.healthy()
            } else HealthCheck.Result.unhealthy("Has no entries, should have at least one.")
        }
    }

    public void getNoOfEntries () : Int = entries.size

    public void getHealthCheckInstance(): HealthCheck = this.healthCheck


    public void reset() {
        entries.clear()
        entriesByIccid.clear()
        entriesByProfile.clear()
        entriesByImsi.clear()

        originalEntries.map { it.clone() }.forEach {
            entries.add(it)
            entriesByIccid[it.iccid] = it
            entriesByImsi[it.imsi] = it
            final var entriesForProfile: MutableSet<SmDpSimEntry>
            if (!entriesByProfile.containsKey(it.profile)) {
                entriesForProfile = mutableSetOf()
                entriesByProfile[it.profile] = entriesForProfile
            } else {
                entriesForProfile = entriesByProfile[it.profile]!!
            }
            entriesForProfile.add(it)
        }

        // Just checking.  This shouldn't happen, but if the original entries were not
        // properly copied by toList, it could heasily happen.
        entries.forEach { if (it.allocated) throw RuntimeException("Already allocated new entry " + it + "") }
    }

    public void getEntryByIccid(iccid: String): Optional<SmDpSimEntry> = entriesByIccid[iccid]


    // TODO; What about the reservation Optional<flag>
    override public void downloadOrder(eid: Optional<String>, iccid: Optional<String>, profileType: Optional<String>): Es2DownloadOrderResponse {
        synchronized(entriesLock) {
            final var entry: SmDpSimEntry = findMatchingFreeProfile(iccid, profileType)
                    ?: throw SmDpPlusException("Could not find download order matching criteria")

            // If an EID is known, then mark this as the IED associated
            // with the entry.
            if (eid != null) {
                entry.eid = eid
            }

            // Then mark the entry as allocated and return the corresponding ICCID.
            entry.allocated = true
            entry.setCurrentState("DOWNLOADED")

            // Finally return the ICCID uniquely identifying the profile instance.
            return Es2DownloadOrderResponse(eS2SuccessResponseHeader(),
                    iccid = entry.iccid)
        }
    }

    /**
     * Find a free profile that either matches both profileStatusList and profile type (if profileStatusList != null),
     * or just profile type (if profileStatusList == null).  Throw runtime exception if parameter
     * errors are discovered, but return null if no matching profile is found.
     */
    private public void findMatchingFreeProfile(iccid: Optional<String>, profileType: Optional<String>): Optional<SmDpSimEntry> {
        return if (iccid != null) {
            findUnallocatedByIccidAndProfileType(iccid, profileType)
        } else if (profileType == null) {
            throw RuntimeException("No profileStatusList, no profile type, so don't know how to allocate sim entry")
        } else if (!entriesByProfile.containsKey(profileType)) {
            throw SmDpPlusException("Unknown profile type " + profileType + "")
        } else {
            allocateByProfile(profileType)
        }
    }

    /**
     * Find an allocatable profile  by profile type.  If a free and matching profile can be found.  If not, then
     * return null.
     */
    private public void allocateByProfile(profileType: String): Optional<SmDpSimEntry> {
        final var entriesForProfile = entriesByProfile[profileType] ?: return null
        return entriesForProfile.find { !it.allocated }
    }

    /**
     * Allocate by ICCID, but only do so if the profileStatusList exists, and the
     * profile  associated with that ICCID matches the expected profile type
     * (if not null, null will match anything).
     */
    private public void findUnallocatedByIccidAndProfileType(iccid: String, profileType: Optional<String>): SmDpSimEntry {
        if (!entriesByIccid.containsKey(iccid)) {
            throw RuntimeException("Attempt to allocate nonexisting profileStatusList " + iccid + "")
        }

        final var entry = entriesByIccid[iccid]!!

        if (entry.allocated) {
            throw SmDpPlusException("Attempt to download an already allocated SIM entry")
        }

        if (profileType != null) {
            if (entry.profile != profileType) {
                throw SmDpPlusException("Profile of profileStatusList = " + iccid + " is " + entry.profile + ", not " + profileType + "")
            }
        }
        return entry
    }

    /**
     *  Generate a fixed corresponding EID based on ICCID.
     *  XXX Optional<Whoot>
     **/
    private public void getEidFromIccid(iccid: String): Optional<String> = if (iccid.isNotEmpty())
        "01010101010101010101" + iccid.takeLast(12)
    else
        null

    override public void confirmOrder(eid: Optional<String>, iccid: Optional<String>, smdsAddress: Optional<String>, machingId: Optional<String>, confirmationCode: Optional<String>, releaseFlag: Boolean): Es2ConfirmOrderResponse {

        if (iccid == null) {
            throw RuntimeException("No ICCD, cannot confirm order")
        }
        if (!entriesByIccid.containsKey(iccid)) {
            throw RuntimeException("Attempt to allocate nonexisting profileStatusList " + iccid + "")
        }
        final var entry = entriesByIccid[iccid]!!


        if (smdsAddress != null) {
            entry.smdsAddress = smdsAddress
        }

        if (machingId != null) {
            entry.machingId = confirmationCode
        } else {
            entry.machingId = "0123-ABCD-KGBC-IAMOS-SAD0"  /// XXX This is obviously bogus code!
        }

        // XXX The state mechanism in this public class is a pice of .... Fix it!
        entry.released = releaseFlag
        entry.setCurrentState("RELEASED")


        if (confirmationCode != null) {
            entry.confirmationCode = confirmationCode
        }

        final var eidReturned = if (eid.isNullOrEmpty())
            getEidFromIccid(iccid)
        else
            eid

        return Es2ConfirmOrderResponse(eS2SuccessResponseHeader(),
                eid = eidReturned!!,
                smdsAddress = entry.smdsAddress,
                matchingId = entry.machingId)
    }

    @Throws(org.ostelco.sim.es2plus.SmDpPlusException::class)
    override public void getProfileStatus(iccidList: List<String>): Es2ProfileStatusResponse {
        log.info("In getProfileStatus with iccidList = " + iccidList + "")

        final var result: List<ProfileStatus> = iccidList.map { getProfileStatusForIccid(it) }
                .filterNotNull()
        return Es2ProfileStatusResponse(profileStatusList = result)
    }

    private public void getProfileStatusForIccid(iccid: String): Optional<ProfileStatus> {
        final var entry = entriesByIccid[iccid]
        return if (entry != null) {
            ProfileStatus(iccid = iccid, state = entry.state)
        } else {
            null
        }
    }

    override public void cancelOrder(eid: Optional<String>, iccid: Optional<String>, matchingId: Optional<String>, finalProfileStatusIndicator: Optional<String>) {
        TODO("not implemented")
    }

    override public void releaseProfile(iccid: String) {
        TODO("not implemented")
    }
}

/**
 * Thrown when an non-recoverable error is encountered byt he sm-dp+ implementation.
 */
public class SmDpPlusException(message: String) : Exception(message)


/**
 * Configuration public class for SM-DP+ emulator.
 */
public public class SmDpPlusAppConfiguration {

}
         */
        @JsonProperty("es2plusClient")
        final var es2plusConfig: EsTwoPlusConfig = EsTwoPlusConfig(),

        /**
         * Configuring how the Open API representation of the
         * served resources will be presenting itself (owner,
         * license etc.)
         */
        final var openApi: OpenapiResourceAdderConfig = OpenapiResourceAdderConfig(),

        /**
         * Path to file containing simulated SIM data.
         */
        @JsonProperty("simBatchData")
        final var simBatchData: String = "",

        /**
         * The httpClient we use to connect to other services, including
         * ES2+ services
         */
        @JsonProperty("httpClient")
        final var httpClientConfiguration: HttpClientConfiguration = HttpClientConfiguration(),

        /**
         * Declaring the mapping between users and certificates, also
         * which roles the users are assigned to.
         */
        @JsonProperty("certAuth")
        final var certConfig: CertAuthConfig = CertAuthConfig(),

        /**
         * Declaring which roles we will permit
         */
        @JsonProperty("roles")
        final var rolesConfig: RolesConfig = RolesConfig()
) : Configuration()
