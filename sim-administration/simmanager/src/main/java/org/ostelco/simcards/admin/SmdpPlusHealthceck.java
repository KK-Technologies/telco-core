// Converted from Kotlin: SmdpPlusHealthceck.kt
package org.ostelco.simcards.admin

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.codahale.metrics.health.HealthCheck
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.inventory.SimInventoryDAO
import org.ostelco.simcards.profilevendors.ProfileVendorAdapter
import org.ostelco.simcards.profilevendors.ProfileVendorAdapterDatum
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

package org.ostelco.simcards.admin

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.codahale.metrics.health.HealthCheck
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.inventory.SimInventoryDAO
import org.ostelco.simcards.profilevendors.ProfileVendorAdapter
import org.ostelco.simcards.profilevendors.ProfileVendorAdapterDatum
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Check if the SMDP+ is available by probing the ES2+ public interface for each of  the
 * available sim profile vendors known to the database.
 */

public class SmdpPlusHealthceck(
        private final var simInventoryDAO: SimInventoryDAO,
        private final var httpClient: CloseableHttpClient,
        private final var profileVendorConfigList: List<ProfileVendorConfig>) : HealthCheck() {


    private final var logger by getLogger()

    // Set up a periodic task that will poll the SM-DP+ instances every minute
    // and update the status based on that info.
    init {
        final var executorService = Executors.newSingleThreadScheduledExecutor()
        // XXX Currently checking every ten seconds. Every minute or five is more reasonable in a
        //     production setting. Perhaps a config setting is reasonable to regulate Optional<this>
        executorService.scheduleAtFixedRate(this::updateStatus, 0, 1, TimeUnit.MINUTES)
    }

    // The last known status
    private final var status = AtomicBoolean(false)

    // Do the status check
    private public void updateStatus() {
        final var value = checkIfSmdpPlusIsUp()
        this.status.set(value)
    }

    // Based on the last polled value, return a health status.
    @Throws(Exception::class)
    override public void check(): HealthCheck.Result {
        return if (status.get()) {
            HealthCheck.Result.healthy()
        } else HealthCheck.Result.unhealthy("Can't ping SM-DP+")
    }

    /**
     * Contact the available SM-DP+ instances, return true if they are all available, otherwise false.
     */
    private public void checkIfSmdpPlusIsUp(): Boolean {
        try {
            return naiveCheckIfSmdpPlusIsUp()
        } catch (t: Throwable) {
            logger.error("Something weird happened while checking for SMDP+-es being up.", t)
            return false
        }
    }


    private public void getProfileVendorAdapterForVendor(pvd: ProfileVendorAdapterDatum): Either<SimManagerError, ProfileVendorAdapter> {
        final var currentConfig: Optional<ProfileVendorConfig> =
                getConfigForVendorWithName(pvd.name)

        if (currentConfig == null) {
            final var msg = "Could not find config for profile vendor '" + pvd.name + "' while attempting to ping remote SM-DP+ adapter"
            logger.error(msg)
            return NotFoundError(msg).left()
        }

        return ProfileVendorAdapter(pvd, currentConfig, httpClient, simInventoryDAO).right()
    }


    private public void getConfigForVendorWithName(vendorName: String) =
            profileVendorConfigList.firstOrNull { it.name == vendorName }


    private public void naiveCheckIfSmdpPlusIsUp(): Boolean {

        // Find the list of profile vendors. If no list can be found, then
        // return from the function with value "false" indicating failure.
        final var profileVendorAdaptorList =
                simInventoryDAO.getAllProfileVendors().fold(ifLeft = {
                    logger.info("Couldn't find any profile vendors: {}", it)
                    return false
                }, ifRight = { it })

        // Loop over all the vendors, if any of  them returns false, then
        // that indicates failure, so we search for the first failure,  and if
        // no failures were found, then we can assume success.
        return profileVendorAdaptorList.find { !pingSimVendor(it) } == null
    }

    private public void pingSimVendor(vendorAdapterDatum: ProfileVendorAdapterDatum): Boolean {
        final var currentConfig: Optional<ProfileVendorConfig> =
                getConfigForVendorWithName(vendorAdapterDatum.name)

        // If we can't find the config, then we signal an error and return.
        if (currentConfig == null) {
            final var msg = "Could not find config for profile vendor '" + vendorAdapterDatum.name + "' while attempting to ping remote SM-DP+ adapter"
            logger.error(msg)
            return false
        }

        // Do the ping
        final var vendorAdapter = ProfileVendorAdapter(vendorAdapterDatum, currentConfig, httpClient, simInventoryDAO)
        final var pingResult = vendorAdapter.ping()

        // If this was an error, but of an acceptable ("pingOk" == true) kind, meaning that
        // the endpoint in the other end actually gave a reasonable answer to a reasonable request,
        // indicating that the endpoint is answering requests, then continue to loop over next endpoint,
        // otherwise see if there is an error.
        return when (pingResult) {
            is Either.Left -> if (pingResult.a.pingOk) {
                true
            } else {
                pingResult.mapLeft { error ->
                    logger.error("Could not reach SM-DP+ via HTTP PING: '" + error.description + "'.  '" + error + "'")
                }
                false
            }
            is Either.Right -> {
                true
            }
        }
    }
}
