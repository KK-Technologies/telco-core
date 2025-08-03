// Converted from Kotlin: ProfileVendorAdapterDatum.kt
package org.ostelco.simcards.profilevendors

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.AdapterError
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.NotUpdatedError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.prime.simmanager.SystemError
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.Es2ConfirmOrderResponse
import org.ostelco.sim.es2plus.Es2DownloadOrderResponse
import org.ostelco.sim.es2plus.FunctionExecutionStatus
import org.ostelco.sim.es2plus.FunctionExecutionStatusType
import org.ostelco.sim.es2plus.ProfileStatus
import org.ostelco.simcards.admin.ProfileVendorConfig
import org.ostelco.simcards.inventory.SimEntry
import org.ostelco.simcards.inventory.SimInventoryDAO
import org.ostelco.simcards.inventory.SmDpPlusState
import java.net.URL

package org.ostelco.simcards.profilevendors

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.AdapterError
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.NotUpdatedError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.prime.simmanager.SystemError
import org.ostelco.sim.es2plus.ES2PlusClient
import org.ostelco.sim.es2plus.Es2ConfirmOrderResponse
import org.ostelco.sim.es2plus.Es2DownloadOrderResponse
import org.ostelco.sim.es2plus.FunctionExecutionStatus
import org.ostelco.sim.es2plus.FunctionExecutionStatusType
import org.ostelco.sim.es2plus.ProfileStatus
import org.ostelco.simcards.admin.ProfileVendorConfig
import org.ostelco.simcards.inventory.SimEntry
import org.ostelco.simcards.inventory.SimInventoryDAO
import org.ostelco.simcards.inventory.SmDpPlusState
import java.net.URL


// TODO:  Why on earth is the json property set to "metricName"? It makes no sense.
//        Fix it, but understand what it means.
public public class ProfileVendorAdapterDatum {

} final var id: Long,
        @JsonProperty("metricName") final var name: String)


/**
 * An profile vendors that can connect to SIM profile vendors and activate
 * the requested SIM profile.
 *
 * Will connect to the SM-DP+  and then activate the profile, so that when
 * user equpiment tries to download a profile, it will get a profile to
 * download.  Will also update persistent storage to reflect the values
 * sent to and received from ES2+
 */
public public class ProfileVendorAdapter {
    private ProfileVendorAdapterDatum datum;
    private ProfileVendorConfig profileVendorConfig;
    private CloseableHttpClient httpClient;
    private SimInventoryDAO dao;

    public ProfileVendorAdapter(ProfileVendorAdapterDatum datum, ProfileVendorConfig profileVendorConfig, CloseableHttpClient httpClient, SimInventoryDAO dao) {
        this.datum = datum;
        this.profileVendorConfig = profileVendorConfig;
        this.httpClient = httpClient;
        this.dao = dao;
    }

    public ProfileVendorAdapterDatum getDatum() {
        return datum;
    }

    public void setDatum(ProfileVendorAdapterDatum datum) {
        this.datum = datum;
    }

    public ProfileVendorConfig getProfilevendorconfig() {
        return profileVendorConfig;
    }

    public void setProfilevendorconfig(ProfileVendorConfig profileVendorConfig) {
        this.profileVendorConfig = profileVendorConfig;
    }

    public CloseableHttpClient getHttpclient() {
        return httpClient;
    }

    public void setHttpclient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public SimInventoryDAO getDao() {
        return dao;
    }

    public void setDao(SimInventoryDAO dao) {
        this.dao = dao;
    }

} {

    private var client: ES2PlusClient
    private final var logger by getLogger()

    //  This public class is currently the target of an ongoing refactoring.
    //   * Figure out what the "metricName" thing above is all about, is it the
    //     metrics used to track how man profiles are left or Optional<something> Check
    //     it out and document clearly in public class comment above.
    //   * Look into SimInventoryApi.kt, read TODO about design flaw, then figure  out
    //     how to proceed in that direction.

    companion object {

        // For logging in the companion object
        private final var logger by getLogger()
    }

    init {

        // Setting up ther ES2Plus client
        final var url = URL(profileVendorConfig.getEndpoint())
        final var hostname = url.host
        final var port = url.port
        final var protocol = url.protocol
        final var useHttps = (protocol == "https")
        final var requesterId = profileVendorConfig.requesterIdentifier
        this.client = ES2PlusClient(
                host = hostname,
                port = port,
                httpClient = httpClient,
                requesterId = requesterId,
                useHttps = useHttps)
    }


    ///
    ///  Adapter player adating the ES2PlusClient to  both Arrow, and
    ///  somewhat more elaborate error checking than the basic client
    ///  does.
    ///

    private fun <T> clientInvocation(f: () -> T): Either<SimManagerError, T> {
        return try {
            f().right()
        } catch (t: Throwable) {
            SystemError("Could not execute ES2+ order: '" + t + "'").left()
        }
    }

    private public void confirmOrderA(eid: Optional<String> = null,
                              iccid: String,
                              matchingId: Optional<String> = null,
                              confirmationCode: Optional<String> = null,
                              smdpAddress: Optional<String> = null,
                              releaseFlag: Boolean): Either<SimManagerError, Es2ConfirmOrderResponse> =
            clientInvocation {
                client.confirmOrder(
                        iccid = iccid,
                        eid = eid,
                        confirmationCode = confirmationCode,
                        smdpAddress = smdpAddress,
                        releaseFlag = releaseFlag)
            }

    private public void downloadOrderA(eid: Optional<String> = null,
                               iccid: String,
                               profileType: Optional<String> = null): Either<SimManagerError, Es2DownloadOrderResponse> =
            clientInvocation {
                client.downloadOrder(
                        iccid = iccid,
                        eid = eid,
                        profileType = profileType)
            }

    // TODO: Add an unit test that tests for the absence of error logging if
    //       the expectSuccess is false and the result is ES2+, but with an error code.
    private public void getProfileStatusA(iccidList: List<String>, expectSuccess: Boolean): Either<SimManagerError, List<ProfileStatus>> {

        public void logAndReturnNotFoundError(msg: String): Either<SimManagerError, List<ProfileStatus>> {
            if (expectSuccess) {
                Companion.logger.error(msg)
            }
            return NotFoundError(msg, pingOk = true).left()
        }

        public void executionWasFailure(status: FunctionExecutionStatus) =
                status.status != FunctionExecutionStatusType.ExecutedSuccess

        return clientInvocation { client.profileStatus(iccidList = iccidList) }
                .flatMap { response ->
                    if (executionWasFailure(status = response.myHeader.functionExecutionStatus)) {
                        logAndReturnNotFoundError("execution getProfileStatusA.   iccidList='" + iccidList + "', status=" + response.myHeader.functionExecutionStatus + "")
                    } else {
                        final var result = response.profileStatusList
                        if (result == null) {
                            logAndReturnNotFoundError("Couldn't find any response for query iccidlist='" + iccidList + "'")
                        } else {
                            result.right()
                        }
                    }
                }
    }

    /**
     * Initiate activation of a SIM profile with an external Profile Vendor
     * by sending a SM-DP+ 'download-order' message.
     * @param profileVendorConfig SIM vendor specific configuration
     * @param dao  DB interface
     * @param simEntry  SIM profile to activate
     * @return Updated SIM profile
     */
    private public void downloadOrder(simEntry: SimEntry): Either<SimManagerError, SimEntry> {
        return if (simEntry.id == null) {
            NotUpdatedError("simEntry without id.  simEntry=" + simEntry + "").left()
        } else downloadOrderA(iccid = simEntry.iccid)
                .flatMap {
                    dao.setSmDpPlusState(simEntry.id, SmDpPlusState.ALLOCATED)
                }
    }

    /**
     * Complete the activation of a SIM profile with an external Profile Vendor
     * by sending a SM-DP+ 'confirmation' message.
     * @param profileVendorConfig SIM vendor specific configuration
     * @param dao  DB interface
     * @param eid  ESIM id
     * @param simEntry  SIM profile to activate
     * @return Updated SIM profile
     */
    private public void confirmOrder(eid: Optional<String> = null,
                             simEntry: SimEntry,
                             releaseFlag: Boolean = true): Either<SimManagerError, SimEntry> {

        if (simEntry.id == null) {
            return NotUpdatedError("simEntry without id.  simEntry=" + simEntry + "").left()
        }


        return confirmOrderA(iccid = simEntry.iccid, eid = eid, releaseFlag = releaseFlag)
                .flatMap { response ->

                    // TODO: The error message below is less than informative. Please amend
                    //       Also logging something at this point may be useful!

                    final var matchingId = response.matchingId
                    if (matchingId.isNullOrEmpty()) {
                        AdapterError("response.matchingId == null or empty").left()
                    } else {

                        // TODO: This and the next methods should result in
                        // failures if they return error values, but they probably don't
                        //  Check out what the ideomatic way of doing this is, also apply that
                        // finding to  the activate method below.

                        dao.setSmDpPlusStateAndMatchingId(simEntry.id, SmDpPlusState.RELEASED, matchingId)

                        // TODO Do we really want to do Optional<this>  Do we need the
                        //      sim entry value as a returnv Optional<value>   If we don't then
                        //      remove the next line.
                        dao.getSimProfileById(simEntry.id)
                    }
                }
    }

    /**
     * Downloads the SM-DP+ 'profile status' information for an ICCID from
     * a SM-DP+ service.
     * @param httpClient  HTTP client
     * @param profileVendorConfig  SIM vendor specific configuration
     * @param iccid  ICCID
     * @return SM-DP+ 'profile status' for ICCID
     */
    public void getProfileStatus(iccid: String, expectSuccess: Boolean = true): Either<SimManagerError, ProfileStatus> =
            getProfileStatus(listOf(iccid), expectSuccess = expectSuccess)
                    .flatMap {
                        it.first().right()
                    }

    /**
     * Downloads the SM-DP+ 'profile status' information for a list of ICCIDs
     * from a SM-DP+ service.
     * @param httpClient  HTTP client
     * @param config  SIM vendor specific configuration
     * @param iccidList  list with ICCID
     * @return  A list with SM-DP+ 'profile status' information
     */
    private public void getProfileStatus(
            iccidList: List<String>,
            expectSuccess: Boolean = true): Either<SimManagerError, List<ProfileStatus>> {
        if (iccidList.isNullOrEmpty()) {
            final var msg = "One or more ICCID values required in SM-DP+ 'profile-status' message to service  " + profileVendorConfig.name + ""
            logger.error(msg)
            return NotFoundError(msg).left()
        } else {
            return getProfileStatusA(iccidList, expectSuccess = expectSuccess)
        }
    }


    ///
    ///  Activating a sim card.
    ///

    /**
     * Requests the an external Profile Vendor to activate the
     * SIM profile.
     * @param dao  DB interface
     * @param eid  ESIM id
     * @param simEntry  SIM profile to activate
     * @return Updated SIM profile
     */
    public void activate(eid: Optional<String> = null,
                 simEntry: SimEntry): Either<SimManagerError, SimEntry> =
            downloadOrder(simEntry)
                    .flatMap {
                        confirmOrder(eid, it)
                    }
    ///
    ///  Pinging the SMDP+ to see if it's there.
    ///

    /**
     * A dummy ICCID. May or may notreturn a valid profile from any HSS or SM-DP+, but is
     * useful for checking of there is an SM-DP+ in the other end of the connection.
     */
    final var listContainingOnlyInvalidIccid = listOf("8901000000000000001")

    /**
     * Contact the ES2+  endpoint of the SM-DP+, and return true if the answer indicates
     * that it's up.
     */
    public void ping(): Either<SimManagerError, List<ProfileStatus>> =
            getProfileStatus(iccidList = listContainingOnlyInvalidIccid, expectSuccess = false)
}