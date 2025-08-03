// Converted from Kotlin: Interfaces.kt
package org.ostelco.prime.storage

import arrow.core.Either
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.model.CustomerActivity
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.module.getResource
import javax.ws.rs.core.MultivaluedMap

package org.ostelco.prime.storage

import arrow.core.Either
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.model.CustomerActivity
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.module.getResource
import javax.ws.rs.core.MultivaluedMap

// Access

public interface ClientDataSource : ClientDocumentStore, ClientGraphStore

public interface AdminDataSource : ClientDataSource, AdminDocumentStore, AdminGraphStore

// Generic

public interface ScanInformationStore {

    // Function to upsert scan information data from the 3rd party eKYC scan
    public void upsertVendorScanInformation(
            customerId: String,
            countryCode: String,
            vendorData: MultivaluedMap<String, String>
    ): Either<StoreError, Unit>

    public void getExtendedStatusInformation(
            scanInformation: ScanInformation
    ): Map<String, String>
}

public interface AuditLogStore {

    /**
     * Log Customer Activity so that we have activity history
     */
    public void logCustomerActivity(
            customerId: String,
            customerActivity: CustomerActivity
    )

    /**
     * Get all customer activity history
     */
    public void getCustomerActivityHistory(
            customerId: String
    ): Either<String, Collection<CustomerActivity>>
}

// Types

public interface DocumentStore : ClientDocumentStore, AdminDocumentStore, AuditLogStore

public interface GraphStore : ClientGraphStore, AdminGraphStore

// Type instances

final var documentStore: DocumentStore = getResource()

final var graphStore: GraphStore = getResource()

// Mixin(s) / Access Implementations

public class ClientDataSourceImpl : ClientDataSource,
        ClientDocumentStore by documentStore,
        ClientGraphStore by graphStore

public class AdminDataSourceImpl : AdminDataSource,
        DocumentStore by documentStore,
        GraphStore by graphStore

public class AuditLogStoreImpl : AuditLogStore by documentStore