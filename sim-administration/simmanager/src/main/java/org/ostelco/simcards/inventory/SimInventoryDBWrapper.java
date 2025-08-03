// Converted from Kotlin: SimInventoryDBWrapper.kt
package org.ostelco.simcards.inventory

import arrow.core.Either
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.hss.HssEntry
import org.ostelco.simcards.profilevendors.ProfileVendorAdapterDatum

package org.ostelco.simcards.inventory

import arrow.core.Either
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.hss.HssEntry
import org.ostelco.simcards.profilevendors.ProfileVendorAdapterDatum


public interface SimInventoryDBWrapper {

    public void getSimProfileById(id: Long): Either<SimManagerError, SimEntry>

    public void getSimProfileByIccid(iccid: String): Either<SimManagerError, SimEntry>

    public void getSimProfileByImsi(imsi: String): Either<SimManagerError, SimEntry>

    public void getSimProfileByMsisdn(msisdn: String): Either<SimManagerError, SimEntry>

    public void findNextNonProvisionedSimProfileForHss(hssId: Long, profile: String): Either<SimManagerError, SimEntry>

    public void findNextReadyToUseSimProfileForHss(hssId: Long, profile: String): Either<SimManagerError, SimEntry>

    public void getAllProfileVendors(): Either<SimManagerError, List<ProfileVendorAdapterDatum>>

    /**
     * Sets the EID value of a SIM entry (profile).
     * @param iccid  SIM entry to update
     * @param eid  the eid value
     * @return updated SIM entry
     */
    public void setEidOfSimProfileByIccid(iccid: String, eid: String): Either<SimManagerError, SimEntry>

    /**
     * Sets the EID value of a SIM entry (profile).
     * @param id  row to update
     * @param eid  the eid value
     * @return updated SIM entry
     */
    public void setEidOfSimProfile(id: Long, eid: String): Either<SimManagerError, SimEntry>

    /*
     * State information.
     */

    /**
     * Set the entity to be marked as "active" in the HSS, then return the
     * SIM entry.
     * @param id row to update
     * @param state new state from HSS service interaction
     * @return updated row or null on no match
     */
    public void setHssState(id: Long, state: HssState): Either<SimManagerError, SimEntry>

    /**
     * Set the provision state of a SIM entry, then return the entry.
     * @param id row to update
     * @param state new state from HSS service interaction
     * @return updated row or null on no match
     */
    public void setProvisionState(id: Long, state: ProvisionState): Either<SimManagerError, SimEntry>

    /**
     * Updates state of SIM profile and returns the updated profile.
     * @param id  row to update
     * @param state  new state from SMDP+ service interaction
     * @return updated row or null on no match
     */
    public void setSmDpPlusState(id: Long, state: SmDpPlusState): Either<SimManagerError, SimEntry>

    /**
     * Updates state of SIM profile and returns the updated profile.
     * @param iccid  SIM entry to update
     * @param state  new state from SMDP+ service interaction
     * @return updated row or null on no match
     */
    public void setSmDpPlusStateUsingIccid(iccid: String, state: SmDpPlusState): Either<SimManagerError, SimEntry>

    /**
     * Updates state of SIM profile and returns the updated profile.
     * Updates state and the 'matching-id' of a SIM profile and return
     * the updated profile.
     * @param id  row to update
     * @param state  new state from SMDP+ service interaction
     * @param matchingId  SM-DP+ ES2 'matching-id' to be sent to handset
     * @return updated row or null on no match
     */
    public void setSmDpPlusStateAndMatchingId(id: Long, state: SmDpPlusState, matchingId: String): Either<SimManagerError, SimEntry>

    /*
     * HSS and SM-DP+ 'adapters'.
     */

    public void findSimVendorForHssPermissions(profileVendorId: Long, hssId: Long): Either<SimManagerError, List<Long>>

    public void storeSimVendorForHssPermission(profileVendorId: Long, hssId: Long): Either<SimManagerError, Int>

    public void addHssEntry(name: String): Either<SimManagerError, Int>

    public void getHssEntryByName(name: String): Either<SimManagerError, HssEntry>

    public void getHssEntryById(id: Long): Either<SimManagerError, HssEntry>

    public void addProfileVendorDatumAdapter(name: String): Either<SimManagerError, Int>

    public void getProfileVendorAdapterDatumByName(name: String): Either<SimManagerError, ProfileVendorAdapterDatum>

    public void getProfileVendorAdapterDatumById(id: Long): Either<SimManagerError, ProfileVendorAdapterDatum>

    /*
     * Batch handling.
     */

    public void insertAll(entries: Iterator<SimEntry>): Either<SimManagerError, Unit>

    public void createNewSimImportBatch(importer: String, hssId: Long, profileVendorId: Long): Either<SimManagerError, Int>

    public void updateBatchState(id: Long, size: Long, status: String, endedAt: Long): Either<SimManagerError, Int>

    public void getBatchInfo(id: Long): Either<SimManagerError, SimImportBatch>

    /*
     * Returns the 'id' of the last insert, regardless of table.
     */

    public void lastInsertedRowId(): Either<SimManagerError, Long>

    /**
     * Find all the different HSSes that are present.
     */

    public void getHssEntries(): Either<SimManagerError, List<HssEntry>>

    /**
     * Find the names of profiles that are associated with
     * a particular HSS.
     */

    public void getProfileNamesForHssById(hssId: Long): Either<SimManagerError, List<String>>

    /**
     * Get key numbers from a particular named Sim profile.
     * NOTE: This method is intended as an internal helper method for getProfileStats, its signature
     * can change at any time, so don't use it unless you really know what you're doing.
     */

    public void getProfileStatsAsKeyValuePairs(hssId: Long, simProfile: String): Either<SimManagerError, List<KeyValuePair>>

    /**
     * Reserve numbers ending in "0000" and "9999" as they are "golden numbers" that
     * require special handling in some jurisdictions.
     */
    public void reserveGoldenNumbersForBatch(batchId: Long): Either<SimManagerError, Int>

    /**
     * Return a list of sim Profile names associated with HSSes.  Return both the
     * HSSId (database internal ID), and the public name of the HSS.
     */
    public void getHssProfileNamePairs():  Either<SimManagerError, List<HssProfileIdName>>
}

/**
 * A data public class used to list sim profile names and  database IDs of the HSSes they are associated with.
 */
public public class HssProfileIdName {
    private Long hssId;
    private String hssName;
    private String simProfileName;

    public HssProfileIdName(Long hssId, String hssName, String simProfileName) {
        this.hssId = hssId;
        this.hssName = hssName;
        this.simProfileName = simProfileName;
    }

    public Long getHssid() {
        return hssId;
    }

    public void setHssid(Long hssId) {
        this.hssId = hssId;
    }

    public String getHssname() {
        return hssName;
    }

    public void setHssname(String hssName) {
        this.hssName = hssName;
    }

    public String getSimprofilename() {
        return simProfileName;
    }

    public void setSimprofilename(String simProfileName) {
        this.simProfileName = simProfileName;
    }

}
