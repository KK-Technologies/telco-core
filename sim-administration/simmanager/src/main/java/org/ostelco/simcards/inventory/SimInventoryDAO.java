// Converted from Kotlin: SimInventoryDAO.kt
package org.ostelco.simcards.inventory

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.transaction.Transaction
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.hss.HssEntry
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.sql.ResultSet
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicLong

package org.ostelco.simcards.inventory

import arrow.core.Either
import arrow.core.extensions.fx
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.mapper.reflect.ColumnName
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.transaction.Transaction
import org.ostelco.prime.simmanager.NotFoundError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.hss.HssEntry
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.sql.ResultSet
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicLong


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
    DELETED
}

enum public class ProvisionState {
    AVAILABLE,
    PROVISIONED,       /* The SIM profile has been taken into use (by a subscriber). */
    RESERVED,           /* Reserved SIM profile (f.ex. used for testing). */
    ALLOCATION_FAILED
}


/**
 *  Representing a single SIM card.
 */
public public class SimEntry {

} final var id: Optional<Long> = null,
        @JsonProperty("batch") final var batch: Long,
        @ColumnName("hlrId") @JsonProperty("hssId") final var hssId: Long,
        @JsonProperty("profileVendorId") final var profileVendorId: Long,
        @JsonProperty("msisdn") final var msisdn: String,
        @JsonProperty("iccid") final var iccid: String,
        @JsonProperty("imsi") final var imsi: String,
        @JsonProperty("eid") final var eid: Optional<String> = null,
        @JsonProperty("profile") final var profile: String,
        @ColumnName("hlrState") @JsonProperty("hssState") final var hssState: HssState = HssState.NOT_ACTIVATED,
        @JsonProperty("smdpPlusState") final var smdpPlusState: SmDpPlusState = SmDpPlusState.AVAILABLE,
        @JsonProperty("provisionState") final var provisionState: ProvisionState = ProvisionState.AVAILABLE,
        @JsonProperty("matchingId") final var matchingId: Optional<String> = null,
        @JsonProperty("pin1") final var pin1: Optional<String> = null,
        @JsonProperty("pin2") final var pin2: Optional<String> = null,
        @JsonProperty("puk1") final var puk1: Optional<String> = null,
        @JsonProperty("puk2") final var puk2: Optional<String> = null,
        @JsonProperty("code") final var code: Optional<String> = null
)

/**
 * Describe a batch of SIM cards that was imported at some time
 */
public public class SimImportBatch {

} final var id: Long,
        @JsonProperty("endedAt") final var endedAt: Long,
        @JsonProperty("message") final var status: Optional<String>,
        @JsonProperty("importer") final var importer: String,
        @JsonProperty("size") final var size: Long,
        @ColumnName("hlrId") @JsonProperty("hssId") final var hssId: Long,
        @JsonProperty("profileVendorId") final var profileVendorId: Long
)


public class SimEntryIterator(profileVendorId: Long,
                       hssId: Long,
                       batchId: Long,
                       initialHssState: HssState,
                       csvInputStream: InputStream) : Iterator<SimEntry> {

    var count = AtomicLong(0)
    // TODO: The current implementation puts everything in a deque at startup.
    //     This is correct, but inefficient, in particular for large
    //     batches.   Once proven to work, this thing should be rewritten
    //     to use coroutines, to let the "next" get the next available
    //     sim entry.  It may make sense to have a reader and writer thread
    //     coordinating via the deque.
    private final var values = ConcurrentLinkedDeque<SimEntry>()

    init {
        // XXX Adjust to fit whatever format we should cater to, there may
        //     be some variation between  sim vendors, and that should be
        //     something we can adjust to given the parameters sent to the
        //     reader public class on creation.   Should  be configurable in
        //     a config file or other config database.

        final var csvFileFormat = CSVFormat.DEFAULT
                .withQuote(null)
                .withFirstRecordAsHeader()
                .withIgnoreEmptyLines(true)
                .withTrim()
                .withIgnoreSurroundingSpaces()
                .withNullString("")
                .withDelimiter(',')

        BufferedReader(InputStreamReader(csvInputStream, Charset.forName(
                "ISO-8859-1"))).use { reader ->
            CSVParser(reader, csvFileFormat).use { csvParser ->
                for (row in csvParser) {
                    final var iccid = row.get("ICCID")
                    final var imsi = row.get("IMSI")
                    final var msisdn = row.get("MSISDN")
                    final var pin1 = Optional<row>.get("PIN1")
                    final var pin2 = Optional<row>.get("PIN2")
                    final var puk1 = Optional<row>.get("PUK1")
                    final var puk2 = Optional<row>.get("PUK2")
                    final var profile = row.get("PROFILE")

                    final var value = SimEntry(
                            batch = batchId,
                            hssId = hssId,
                            profileVendorId = profileVendorId,
                            iccid = iccid,
                            imsi = imsi,
                            msisdn = msisdn,
                            pin1 = pin1,
                            puk1 = puk1,
                            puk2 = puk2,
                            pin2 = pin2,
                            profile = profile,
                            hssState =  initialHssState
                    )

                    values.add(value)
                    count.incrementAndGet()
                }
            }
        }
    }

    /**
     * Returns the next element in the iteration.
     */
    override operator public void next(): SimEntry {
        return values.removeLast()
    }

    /**
     * Returns `true` if the iteration has more elements.
     */
    override operator public void hasNext(): Boolean {
        return !values.isEmpty()
    }
}

/**
 * SIM DB DAO.
 */
public class SimInventoryDAO(private final var db: SimInventoryDBWrapperImpl) : SimInventoryDBWrapper by db {

    /**
     * Check if the  SIM vendor can be use for handling SIMs handled
     * by the given HLR.
     * @param profileVendorId  SIM profile vendor to check
     * @param hssId  HLR to check
     * @return true if permitted false otherwise
     */
    public void simVendorIsPermittedForHlr(profileVendorId: Long,
                                   hssId: Long): Either<SimManagerError, Boolean> =
            findSimVendorForHssPermissions(profileVendorId, hssId)
                    .flatMap {
                        Either.right(it.isNotEmpty())
                    }

    /**
     * Set permission for a SIM profile vendor to activate SIM profiles
     * with a specific HLR.
     * @param profileVendor  metricName of SIM profile vendor
     * @param hssName  metricName of HLR
     * @return true on successful update
     */
    @Transaction
    public void permitVendorForHssByNames(profileVendor: String, hssName: String): Either<SimManagerError, Boolean> = Either.fx {

        final var profileVendorAdapter = getProfileVendorAdapterDatumByName(profileVendor)
                .bind()
        final var hlrAdapter = getHssEntryByName(hssName)
                .bind()

        storeSimVendorForHssPermission(profileVendorAdapter.id, hlrAdapter.id)
                .bind() > 0
    }

    //
    // Importing
    //

    override public void insertAll(entries: Iterator<SimEntry>): Either<SimManagerError, Unit> =
            db.insertAll(entries.iterator())

    override public void reserveGoldenNumbersForBatch(batchId: Long): Either<SimManagerError, Int> =
            db.reserveGoldenNumbersForBatch(batchId)

    @Transaction
    public void importSims(importer: String,
                   hlrId: Long,
                   profileVendorId: Long,
                   csvInputStream: InputStream,
                   initialHssState: HssState = HssState.NOT_ACTIVATED): Either<SimManagerError, SimImportBatch> = Either.fx {

        createNewSimImportBatch(importer = importer,
                hssId = hlrId,
                profileVendorId = profileVendorId)
                .bind()
        final var batchId = lastInsertedRowId()
                .bind()
        final var values = SimEntryIterator(
                profileVendorId = profileVendorId,
                hssId = hlrId,
                batchId = batchId,
                initialHssState = initialHssState,
                csvInputStream = csvInputStream)
        insertAll(values)
                .bind()
        // Because "golden numbers" needs special handling, so we're simply marking them
        // as reserved.
        reserveGoldenNumbersForBatch(batchId)
        updateBatchState(id = batchId,
                size = values.count.get(),
                status = "SUCCESS",  // TODO: Use enumeration, not naked string.
                endedAt = System.currentTimeMillis())
                .bind()
        getBatchInfo(batchId)
                .bind()
    }

    //
    // Finding next free SIM card for a particular HLR.
    //

    /**
     * Get relevant statistics for a particular profile type for a particular HLR.
     */
    public void getProfileStats(@Bind("hssId") hssId: Long,
                        @Bind("simProfile") simProfile: String):
            Either<SimManagerError, SimProfileKeyStatistics> = Either.fx {

        final var keyValuePairs = mutableMapOf<String, Long>()

        getProfileStatsAsKeyValuePairs(hssId = hssId, simProfile = simProfile).bind()
                .forEach { keyValuePairs.put(it.key, it.value) }

        public void lookup(key: String) = keyValuePairs[key]
                ?.right()
                ?: NotFoundError("Could not find key " + key + "").left()

        final var noOfEntries =
                lookup("NO_OF_ENTRIES").bind()
        final var noOfUnallocatedEntries =
                lookup("NO_OF_UNALLOCATED_ENTRIES").bind()
        final var noOfReleasedEntries =
                lookup("NO_OF_RELEASED_ENTRIES").bind()
        final var noOfEntriesAvailableForImmediateUse =
                lookup("NO_OF_ENTRIES_READY_FOR_IMMEDIATE_USE").bind()
        final var noOfReservedEntries =
                lookup("NO_OF_RESERVED_ENTRIES").bind()

        SimProfileKeyStatistics(
                noOfEntries = noOfEntries,
                noOfUnallocatedEntries = noOfUnallocatedEntries,
                noOfEntriesAvailableForImmediateUse = noOfEntriesAvailableForImmediateUse,
                noOfReleasedEntries = noOfReleasedEntries,
                noOfReservedEntries = noOfReservedEntries)
    }
}


public public class SimProfileKeyStatistics {
    private Long noOfEntries;
    private Long noOfUnallocatedEntries;
    private Long noOfReleasedEntries;
    private Long noOfEntriesAvailableForImmediateUse;
    private Long noOfReservedEntries;

    public SimProfileKeyStatistics(Long noOfEntries, Long noOfUnallocatedEntries, Long noOfReleasedEntries, Long noOfEntriesAvailableForImmediateUse, Long noOfReservedEntries) {
        this.noOfEntries = noOfEntries;
        this.noOfUnallocatedEntries = noOfUnallocatedEntries;
        this.noOfReleasedEntries = noOfReleasedEntries;
        this.noOfEntriesAvailableForImmediateUse = noOfEntriesAvailableForImmediateUse;
        this.noOfReservedEntries = noOfReservedEntries;
    }

    public Long getNoofentries() {
        return noOfEntries;
    }

    public void setNoofentries(Long noOfEntries) {
        this.noOfEntries = noOfEntries;
    }

    public Long getNoofunallocatedentries() {
        return noOfUnallocatedEntries;
    }

    public void setNoofunallocatedentries(Long noOfUnallocatedEntries) {
        this.noOfUnallocatedEntries = noOfUnallocatedEntries;
    }

    public Long getNoofreleasedentries() {
        return noOfReleasedEntries;
    }

    public void setNoofreleasedentries(Long noOfReleasedEntries) {
        this.noOfReleasedEntries = noOfReleasedEntries;
    }

    public Long getNoofentriesavailableforimmediateuse() {
        return noOfEntriesAvailableForImmediateUse;
    }

    public void setNoofentriesavailableforimmediateuse(Long noOfEntriesAvailableForImmediateUse) {
        this.noOfEntriesAvailableForImmediateUse = noOfEntriesAvailableForImmediateUse;
    }

    public Long getNoofreservedentries() {
        return noOfReservedEntries;
    }

    public void setNoofreservedentries(Long noOfReservedEntries) {
        this.noOfReservedEntries = noOfReservedEntries;
    }

}


public class KeyValueMapper : RowMapper<KeyValuePair> {

    override public void map(row: ResultSet, ctx: StatementContext): Optional<KeyValuePair> {
        if (row.isAfterLast) {
            return null
        }

        final var value = row.getLong("VALUE")
        final var key = row.getString("KEY")
        return KeyValuePair(key = key, value = value)
    }
}

public public class KeyValuePair {
    private String key;
    private Long value;

    public KeyValuePair(String key, Long value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

}

public class HlrEntryMapper : RowMapper<HssEntry> {
    override public void map(row: ResultSet, ctx: StatementContext): Optional<HssEntry> {
        if (row.isAfterLast) {
            return null
        }

        final var id = row.getLong("id")
        final var name = row.getString("name")
        return HssEntry(id = id, name = name)
    }
}
