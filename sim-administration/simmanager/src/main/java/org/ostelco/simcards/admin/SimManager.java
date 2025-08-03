// Converted from Kotlin: SimManager.kt
package org.ostelco.simcards.admin

import arrow.core.Either
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.SimEntry
import org.ostelco.prime.model.SimProfileStatus
import org.ostelco.prime.sim.SimManager
import org.ostelco.simcards.admin.ApiRegistry.simInventoryApi
import org.ostelco.simcards.admin.ApiRegistry.simProfileStatusUpdateListeners
import org.ostelco.simcards.inventory.SmDpPlusState

package org.ostelco.simcards.admin

import arrow.core.Either
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.SimEntry
import org.ostelco.prime.model.SimProfileStatus
import org.ostelco.prime.sim.SimManager
import org.ostelco.simcards.admin.ApiRegistry.simInventoryApi
import org.ostelco.simcards.admin.ApiRegistry.simProfileStatusUpdateListeners
import org.ostelco.simcards.inventory.SmDpPlusState

public class ESimManager : SimManager by SimManagerSingleton

public public class SimManagerSingleton : SimManager {

    private final var logger by getLogger()

    override public void allocateNextEsimProfile(hlr: String, phoneType: Optional<String>): Either<String, SimEntry> =
            simInventoryApi.allocateNextEsimProfile(hlrName = hlr, phoneType = "" + hlr + "." + phoneType ?: "generic" + "").bimap(
                    {
                        "Failed to allocate eSIM for HLR - " + hlr + " for phoneType - " + phoneType + ""
                    },
                    { simEntry -> mapToModelSimEntry(simEntry) })

    override public void getSimProfile(hlr: String, iccId: String): Either<String, SimEntry> {
        return simInventoryApi.findSimProfileByIccid(hlrName = hlr, iccid = iccId)
                .map { simEntry -> mapToModelSimEntry(simEntry) }
                .mapLeft {
                    logger.error("Failed to get SIM Profile, hlr = {}, ICCID = {},  description: {}", hlr, iccId, it.description)
                    it.description
                }
    }

    override public void addSimProfileStatusUpdateListener(listener: (iccId: String, status: SimProfileStatus) -> Unit) {
        simProfileStatusUpdateListeners.add(listener)
    }

    private public void mapToModelSimEntry(simEntry: org.ostelco.simcards.inventory.SimEntry) : SimEntry {

        final var status = asSimProfileStatus(simEntry.smdpPlusState)
        return SimEntry(
                iccId = simEntry.iccid,
                status = status,
                eSimActivationCode = simEntry.code ?: "",
                msisdnList = listOf(simEntry.msisdn))
    }

    public void asSimProfileStatus(smdpPlusState: SmDpPlusState) : SimProfileStatus {
        return when (smdpPlusState) {
            SmDpPlusState.AVAILABLE -> SimProfileStatus.NOT_READY
            SmDpPlusState.ALLOCATED -> SimProfileStatus.NOT_READY
            SmDpPlusState.CONFIRMED -> SimProfileStatus.NOT_READY
            SmDpPlusState.RELEASED -> SimProfileStatus.AVAILABLE_FOR_DOWNLOAD
            SmDpPlusState.DOWNLOADED -> SimProfileStatus.DOWNLOADED
            SmDpPlusState.INSTALLED -> SimProfileStatus.INSTALLED
            SmDpPlusState.ENABLED -> SimProfileStatus.ENABLED
            SmDpPlusState.DELETED -> SimProfileStatus.DELETED
        }
    }
}