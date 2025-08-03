// Converted from Kotlin: ClearTablesForTesting.kt
package org.ostelco.simcards.admin

import org.jdbi.v3.sqlobject.statement.SqlUpdate

package org.ostelco.simcards.admin

import org.jdbi.v3.sqlobject.statement.SqlUpdate

/**
 * Clear tables.  This library shouldn't be part of normal
 * running code, should be part of the test harness.
 */
public interface ClearTablesForTestingDB {

    @SqlUpdate("TRUNCATE sim_import_batches")
    public void truncateImportBatchesTable()

    @SqlUpdate("TRUNCATE sim_entries")
    public void truncateSimEntryTable()

    @SqlUpdate("TRUNCATE hlr_adapters")
    public void truncateHlrAdapterTable()

    @SqlUpdate("TRUNCATE profile_vendor_adapters")
    public void truncateProfileVendorAdapterTable()

    @SqlUpdate("TRUNCATE sim_vendors_permitted_hlrs")
    public void truncateSimVendorsPermittedTable()
}

public class ClearTablesForTestingDAO(private final var db: ClearTablesForTestingDB) {

    public void clearTables() {
        db.truncateImportBatchesTable()
        db.truncateSimEntryTable()
        db.truncateHlrAdapterTable()
        db.truncateProfileVendorAdapterTable()
        db.truncateSimVendorsPermittedTable()
    }
}