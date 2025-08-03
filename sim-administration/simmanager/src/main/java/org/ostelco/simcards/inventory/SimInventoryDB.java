// Converted from Kotlin: SimInventoryDB.kt
package org.ostelco.simcards.inventory

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.BatchChunkSize
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.transaction.Transaction
import org.ostelco.simcards.hss.HssEntry
import org.ostelco.simcards.profilevendors.ProfileVendorAdapterDatum
import java.sql.ResultSet

package org.ostelco.simcards.inventory

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.BatchChunkSize
import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.transaction.Transaction
import org.ostelco.simcards.hss.HssEntry
import org.ostelco.simcards.profilevendors.ProfileVendorAdapterDatum
import java.sql.ResultSet

/**
 * Low-level SIM DB interface.
 * Note: Postgresql specific SQL statements (I think).
 */
public interface SimInventoryDB {

    @SqlQuery("""SELECT * FROM sim_entries
                      WHERE id = :id""")
    public void getSimProfileById(id: Long): Optional<SimEntry>

    @SqlQuery("""SELECT * FROM sim_entries
                      WHERE iccid = :iccid""")
    public void getSimProfileByIccid(iccid: String): Optional<SimEntry>

    @SqlQuery("""SELECT * FROM sim_entries
                      WHERE imsi = :imsi""")
    public void getSimProfileByImsi(imsi: String): Optional<SimEntry>

    @SqlQuery("""SELECT * FROM sim_entries
                      WHERE msisdn = :msisdn""")
    public void getSimProfileByMsisdn(msisdn: String): Optional<SimEntry>

    /**
     * Find next available SIM card for a particular HLR ready
     * to be 'provisioned' with the SM-DP+ and HLR vendors.
     */
    @SqlQuery("""SELECT a.*
                      FROM   sim_entries a
                             JOIN (SELECT id,
                                          CASE
                                            WHEN hlrstate = 'NOT_ACTIVATED'
                                                 AND smdpplusstate = 'AVAILABLE' THEN 1
                                            WHEN hlrstate = 'NOT_ACTIVATED'
                                                 AND smdpplusstate = 'RELEASED'  THEN 2
                                            WHEN hlrstate = 'ACTIVATED'
                                                 AND smdpplusstate = 'AVAILABLE' THEN 3
                                            ELSE 9999
                                          END AS position
                                  FROM   sim_entries
                                  WHERE  provisionState = 'AVAILABLE'
                                         AND hlrId = :hssId
                                         AND profile = :profile
                                  ORDER  BY position ASC,
                                           id ASC) b
                             ON ( a.id = b.id
                                  AND b.position < 9999 )
                      LIMIT  1""")
    public void findNextNonProvisionedSimProfileForHss(hssId: Long,
                                               profile: String): Optional<SimEntry>

    /**
     * Find next ready to use SIM card for a particular HLR
     * and profile (phone type).
     */
    @SqlQuery("""SELECT *
                      FROM   sim_entries
                      WHERE  hlrState = 'ACTIVATED'
                             AND smdpplusstate = 'RELEASED'
                             AND provisionState = 'AVAILABLE'
                             AND hlrId = :hssId
                             AND profile = :profile
                      LIMIT  1""")
    public void findNextReadyToUseSimProfileForHlr(hssId: Long,
                                           profile: String): Optional<SimEntry>

    @SqlUpdate("""UPDATE sim_entries SET eid = :eid
                       WHERE iccid = :iccid""")
    public void updateEidOfSimProfileByIccid(iccid: String,
                                     eid: String): Int

    @SqlUpdate("""UPDATE sim_entries SET eid = :eid
                       WHERE id = :id""")
    public void updateEidOfSimProfile(id: Long,
                              eid: String): Int

    /**
     * State information.
     */

    @SqlUpdate("""UPDATE sim_entries SET hlrState = :hssState
                       WHERE id = :id""")
    public void updateHlrState(id: Long,
                       hssState: HssState): Int

    @SqlUpdate("""UPDATE sim_entries SET provisionState = :provisionState
                       WHERE id = :id""")
    public void updateProvisionState(id: Long,
                             provisionState: ProvisionState): Int

    @SqlUpdate("""UPDATE sim_entries SET hlrState = :hssState,
                                              provisionState = :provisionState
                       WHERE id = :id""")
    public void updateHlrStateAndProvisionState(id: Long,
                                        hssState: HssState,
                                        provisionState: ProvisionState): Int

    @SqlUpdate("""UPDATE sim_entries SET smdpPlusState = :smdpPlusState
                       WHERE id = :id""")
    public void updateSmDpPlusState(id: Long, smdpPlusState: SmDpPlusState): Int

    @SqlUpdate("""UPDATE sim_entries SET smdpPlusState = :smdpPlusState
                       WHERE iccid = :iccid""")
    public void updateSmDpPlusStateUsingIccid(iccid: String, smdpPlusState: SmDpPlusState): Int

    @SqlUpdate("""UPDATE sim_entries SET smdpPlusState = :smdpPlusState,
                                              matchingId = :matchingId
                       WHERE id = :id""")
    public void updateSmDpPlusStateAndMatchingId(id: Long,
                                         smdpPlusState: SmDpPlusState,
                                         matchingId: String): Int

    /**
     * HLR and SM-DP+ 'adapters'.
     */

    @SqlQuery("""SELECT id FROM sim_vendors_permitted_hlrs
                      WHERE profileVendorId = profileVendorId
                            AND hlrId = :hssId""")
    public void findSimVendorForHssPermissions(profileVendorId: Long,
                                       hssId: Long): List<Long>

    @SqlUpdate("""INSERT INTO sim_vendors_permitted_hlrs
                                   (profilevendorid,
                                    hlrid)
                       SELECT :profileVendorId,
                              :hssId
                       WHERE  NOT EXISTS (SELECT 1
                                          FROM   sim_vendors_permitted_hlrs
                                          WHERE  profilevendorid = :profileVendorId
                                                 AND hlrid = :hssId)""")
    public void storeSimVendorForHssPermission(profileVendorId: Long,
                                       hssId: Long): Int

    @SqlUpdate("""INSERT INTO hlr_adapters
                                   (name)
                       SELECT :name
                       WHERE  NOT EXISTS (SELECT 1
                                          FROM   hlr_adapters
                                          WHERE  name = :name)""")
    public void addHssAdapter(name: String): Int

    @SqlQuery("""SELECT * FROM hlr_adapters
                      WHERE name = :name""")
    public void getHssEntryByName(name: String): HssEntry

    @SqlQuery("""SELECT * FROM hlr_adapters
                      WHERE id = :id""")
    public void getHssEntryById(id: Long): HssEntry

    @SqlUpdate("""INSERT INTO profile_vendor_adapters
                                   (name)
                       SELECT :name
                       WHERE  NOT EXISTS (SELECT 1
                                          FROM   profile_vendor_adapters
                                          WHERE  name = :name) """)
    public void addProfileVendorAdapter(name: String): Int

    @SqlQuery("""SELECT * FROM profile_vendor_adapters""")
    public void getAllProfileVendors(): List<ProfileVendorAdapterDatum>

    @SqlQuery("""SELECT * FROM profile_vendor_adapters
                       WHERE name = :name""")
    public void getProfileVendorAdapterByName(name: String): Optional<ProfileVendorAdapterDatum>

    @SqlQuery("""SELECT * FROM profile_vendor_adapters
                      WHERE id = :id""")
    public void getProfileVendorAdapterDatumById(id: Long): Optional<ProfileVendorAdapterDatum>

    /**
     * Batch handling.
     */

    @Transaction
    @SqlBatch("""INSERT INTO sim_entries
                                  (batch, profileVendorId, hlrid, hlrState, smdpplusstate, provisionState, matchingId, profile, iccid, imsi, msisdn, pin1, pin2, puk1, puk2)
                      VALUES (:batch, :profileVendorId, :hssId, :hssState, :smdpPlusState, :provisionState, :matchingId, :profile, :iccid, :imsi, :msisdn, :pin1, :pin2, :puk1, :puk2)""")
    @BatchChunkSize(1000)
    public void insertAll(@BindBean entries: Iterator<SimEntry>)

    @SqlUpdate("""INSERT INTO sim_import_batches (status,  importer, hlrId, profileVendorId)
                       VALUES ('STARTED', :importer, :hssId, :profileVendorId)""")
    public void createNewSimImportBatch(importer: String,
                                hssId: Long,
                                profileVendorId: Long): Int

    @SqlUpdate("""UPDATE sim_import_batches SET size = :size,
                                                     status = :status,
                                                     endedAt = :endedAt
                       WHERE id = :id""")
    public void updateBatchState(id: Long,
                         size: Long,
                         status: String,
                         endedAt: Long): Int

    @SqlQuery("""SELECT * FROM sim_import_batches
                      WHERE id = :id""")
    public void getBatchInfo(id: Long): Optional<SimImportBatch>

    /*
     * Returns the 'id' of the last insert, regardless of table.
     */
    @SqlQuery("SELECT lastval()")
    public void lastInsertedRowId(): Long

    /**
     * Find all the different HLRs that are present.
     */
    @SqlQuery("SELECT * FROM hlr_adapters")
    @RegisterRowMapper(HlrEntryMapper::class)
    public void getHssEntries(): List<HssEntry>


    /**
     * Find the names of profiles that are associated with
     * a particular HLR.
     */
    @SqlQuery("""SELECT DISTINCT profile  FROM sim_entries
                      WHERE hlrId = :hssId""")
    public void getProfileNamesForHss(hssId: Long): List<String>



    // WHERE hlrId = 2 AND profile = 'OYA_M1_BF76' AND
    // smdpPlusState <>  'DOWNLOADED' AND smdpPlusState <>  'INSTALLED' AND smdpPlusState <>  'ENABLED' AND  provisionState = 'AVAILABLE' AND matchingid is null

    /**
     * Get key numbers from a particular named Sim profile.
     * NOTE: This method is intended as an internal helper method for getProfileStats, its signature
     * can change at any time, so don't use it unless you really know what you're doing.
     */
    @SqlQuery("""
        SELECT 'NO_OF_ENTRIES' AS KEY,  count(*)  AS VALUE  FROM sim_entries WHERE hlrId = :hssId AND profile = :simProfile
        UNION
        SELECT 'NO_OF_UNALLOCATED_ENTRIES' AS KEY,  count(*)  AS VALUE  FROM sim_entries
                   WHERE hlrId = :hssId AND profile = :simProfile AND
                         smdpPlusState <>  :smdpDownloadedState AND smdpPlusState <>  :smdpDownloadedState  AND smdpPlusState <>  :smdpEnabledState AND matchingid IS null AND
                         provisionState = :provisionedAvailableState
        UNION
        SELECT 'NO_OF_RELEASED_ENTRIES' AS KEY,  count(*)  AS VALUE  FROM sim_entries
                   WHERE hlrId = :hssId AND profile = :simProfile AND
                         smdpPlusState =  :smdpReleasedState AND
                         hlrState = :hssAllocatedState
        UNION
           SELECT 'NO_OF_RESERVED_ENTRIES' AS KEY,  count(*)  AS VALUE  FROM sim_entries
                   WHERE hlrId = :hssId AND profile = :simProfile AND
                         provisionState = :provisionReservedState
        UNION
        
        SELECT 'NO_OF_ENTRIES_READY_FOR_IMMEDIATE_USE' AS KEY,  count(*)  AS VALUE  FROM sim_entries
                   WHERE hlrId = :hssId AND profile = :simProfile AND
                         smdpPlusState =  :smdpReleasedState AND
                         hlrState = :hssAllocatedState AND
                         provisionState = :provisionedAvailableState
    """)
    @RegisterRowMapper(KeyValueMapper::class)
    public void getProfileStatsAsKeyValuePairs(
            hssId: Long,
            simProfile: String,
            provisionReservedState: ProvisionState = ProvisionState.RESERVED,
            smdpReleasedState: String = SmDpPlusState.RELEASED.name,
            hlrUnallocatedState: String = HssState.NOT_ACTIVATED.name,
            smdpUnallocatedState: String = SmDpPlusState.AVAILABLE.name,
            hssAllocatedState: String = HssState.ACTIVATED.name,
            smdpAllocatedState: String = SmDpPlusState.ALLOCATED.name,
            smdpDownloadedState: String = SmDpPlusState.DOWNLOADED.name,
            smdpInstalledState: String = SmDpPlusState.INSTALLED.name,
            smdpEnabledState: String = SmDpPlusState.ENABLED.name,
            provisionedAvailableState: String = ProvisionState.AVAILABLE.name): List<KeyValuePair>



    @SqlQuery("""
        SELECT DISTINCT profile AS simprofilename, hlrid  AS hssid, hlr_adapters.name AS hssname FROM sim_entries, hlr_adapters WHERE hlrid=hlr_adapters.id
    """)
    @RegisterRowMapper(HssProfileNameMapper::class)
    public void getHssProfileNamePairs(): List<HssProfileIdName>


    /**
     * Golden numbers are numbers ending in either "0000" or "9999", and they have to be
     * treated specially.
     */
    @SqlUpdate("""UPDATE sim_entries SET provisionState = :provisionReservedState
                       WHERE batch = :batchId AND msisdn ~ '[0-9]*(0000|9999)$'
                       """)
    public void reserveGoldenNumbersForBatch(batchId: Long, provisionReservedState: ProvisionState = ProvisionState.RESERVED): Int
}


public class HssProfileNameMapper : RowMapper<HssProfileIdName> {
    override public void map(row: ResultSet, ctx: StatementContext): Optional<HssProfileIdName> {
        if (row.isAfterLast) {
            return null
        }

        final var hssId = row.getLong("hssid")
        final var hssName = row.getString("hssname")
        final var simProfileName = row.getString("simprofilename")
        return HssProfileIdName(hssId = hssId, hssName = hssName, simProfileName = simProfileName)
    }
}
