package org.ostelco.prime.storage;

import java.util.Collection;

/**
 * Audit log store interface
 */
public interface AuditLogStore {

    /**
     * Log Customer Activity so that we have activity history
     */
    void logCustomerActivity(String customerId, String customerActivity);

    /**
     * Get all customer activity history
     */
    Either<String, Collection<String>> getCustomerActivityHistory(String customerId);
}