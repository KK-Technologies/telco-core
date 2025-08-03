// Converted from Kotlin: SimInventoryApi.kt
package org.ostelco.simcards.inventory

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.DatabaseError
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.sim.es2plus.ProfileStatus
import org.ostelco.simcards.admin.ProfileVendorConfig
import org.ostelco.simcards.admin.SimAdministrationConfiguration
import org.ostelco.simcards.profilevendors.ProfileVendorAdapter
import java.io.InputStream

package org.ostelco.simcards.inventory

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.DatabaseError
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.sim.es2plus.ProfileStatus
import org.ostelco.simcards.admin.ProfileVendorConfig
import org.ostelco.simcards.admin.SimAdministrationConfiguration
import org.ostelco.simcards.profilevendors.ProfileVendorAdapter
import java.io.InputStream


public class SimInventoryApi(private final var httpClient: CloseableHttpClient,
                      private final var simAdminConfig: SimAdministrationConfiguration,
                      private final var dao: SimInventoryDAO) {

    private final var logger by getLogger()

    public void findSimProfileByIccid(hlrName: String, iccid: String): Either<SimManagerError, SimEntry> = Either.fx {

        final var simEntry = dao.getSimProfileByIccid(iccid).bind()
        checkForValidHlr(hlrName, simEntry)

        final var config = getProfileVendorConfig(simEntry).bind()

        // Return the entry found in the database, extended with a
        // code represernting the string that will be used by the LPA in the
        // UA to talk to the sim vendor's SM-DP+ over the ES9+ protocol.
        simEntry.copy(code = "LPA:1\$" + config.es9plusEndpoint + "\$" + simEntry.matchingId + "")
    }

    public void findSimProfileByImsi(hlrName: String, imsi: String): Either<SimManagerError, SimEntry> =
            dao.getSimProfileByImsi(imsi)
                    .flatMap { simEntry ->
                        checkForValidHlr(hlrName, simEntry)
                    }

    public void findSimProfileByMsisdn(hlrName: String, msisdn: String): Either<SimManagerError, SimEntry> =
            dao.getSimProfileByMsisdn(msisdn)
                    .flatMap { simEntry ->
                        checkForValidHlr(hlrName, simEntry)
                    }


    public void getSimProfileStatus(hlrName: String, iccid: String): Either<SimManagerError, ProfileStatus> =
            // TODO: This looks odd, can it be transformeds into something more Optional<compact>
            findSimProfileByIccid(hlrName, iccid)
                    .flatMap { simEntry ->
                        getProfileVendorAdapter(simEntry)
                                .flatMap {
                                    it.getProfileStatus(iccid = iccid)
                                }
                    }

    public void allocateNextEsimProfile(hlrName: String, phoneType: String): Either<SimManagerError, SimEntry> = Either.fx {

        logger.info("Allocating new SIM for hlr " + hlrName + " and phone-type " + phoneType + "")

        final var hlrAdapter = dao.getHssEntryByName(hlrName)
                .bind()
        final var profile = getProfileType(hlrName, phoneType)
                .bind()
        final var simEntry = dao.findNextReadyToUseSimProfileForHss(hlrAdapter.id, profile)
                .bind()
        final var config = getProfileVendorConfig(simEntry)
                .bind()

        if (simEntry.id == null) {
            DatabaseError("simEntry has no id (simEntry=" + simEntry + ")").left().bind()
        }

        final var updatedSimEntry = dao.setProvisionState(simEntry.id, ProvisionState.PROVISIONED)
                .bind()

        // TODO: Add 'code' field content.
        //   Original format: LPA:<hostname>:<matching-id>
        //   New format: LPA:1$<endpoint>$<matching-id> */
        updatedSimEntry.copy(code = "LPA:1\$" + config.es9plusEndpoint + "\$" + updatedSimEntry.matchingId + "")
    }

    public void importBatch(hlrName: String,
                    simVendor: String,
                    csvInputStream: InputStream,
                    initialHssState: HssState): Either<SimManagerError, SimImportBatch> = Either.fx {

        final var profileVendorAdapter = dao.getProfileVendorAdapterDatumByName(simVendor)
                .bind()
        final var hlrAdapter = dao.getHssEntryByName(hlrName)
                .bind()

        /* Exits if not true. */
        dao.simVendorIsPermittedForHlr(profileVendorAdapter.id, hlrAdapter.id)
                .bind()
        dao.importSims(importer = "importer", // TODO: This is a very strange metricName for an importer .-)
                hlrId = hlrAdapter.id,
                profileVendorId = profileVendorAdapter.id,
                csvInputStream = csvInputStream,
                initialHssState = initialHssState).bind()
    }

    /* Helper functions. */

    private public void checkForValidHlr(hlrName: String, simEntry: SimEntry): Either<SimManagerError, SimEntry> =
            dao.getHssEntryById(simEntry.hssId)
                    .flatMap { hlrAdapter ->
                        if (hlrName != hlrAdapter.name)
                            NotFoundError("HLR metricName " + hlrName + " does not match SIM profile HLR " + hlrAdapter.name + "")
                                    .left()
                        else
                            simEntry.right()
                    }

    private public void getProfileVendorConfig(simEntry: SimEntry): Either<SimManagerError, ProfileVendorConfig> =
            dao.getProfileVendorAdapterDatumById(simEntry.profileVendorId)
                    .flatMap { profileVendorAdapterDatum ->
                        final var config: Optional<ProfileVendorConfig> = simAdminConfig.profileVendors.firstOrNull {
                            it.name == profileVendorAdapterDatum.name
                        }
                        if (config != null)
                            config.right()
                        else
                            NotFoundError("Could not find configuration for SIM profile vendor " + profileVendorAdapterDatum.name + "")
                                    .left()
                    }


    private public void getProfileVendorAdapter(simEntry: SimEntry): Either<SimManagerError, ProfileVendorAdapter> =
            dao.getProfileVendorAdapterDatumById(simEntry.profileVendorId)
                    .flatMap { profileVendorAdapterDatum ->
                        getProfileVendorConfig(simEntry).flatMap { profileConfig ->
                            ProfileVendorAdapter(profileVendorAdapterDatum, profileConfig, httpClient, dao).right()
                        }
                    }

    private public void getProfileType(hlrName: String, phoneType: String): Either<SimManagerError, String> = simAdminConfig
            .getProfileForPhoneType(phoneType)
            ?.right()
            ?: NotFoundError("Could not find configuration for phone type='" + phoneType + "', hlrName='" + hlrName + "'").left()
}
