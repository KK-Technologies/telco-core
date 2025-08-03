// Converted from Kotlin: AuditLog.kt
package org.ostelco.prime.auditlog

import java.util.concurrent.CoroutineScope
import java.util.concurrent.Dispatchers
import java.util.concurrent.launch
import org.ostelco.prime.model.CustomerActivity
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Severity
import org.ostelco.prime.model.Severity.ERROR
import org.ostelco.prime.model.Severity.INFO
import org.ostelco.prime.model.Severity.WARN
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.AuditLogStore
import org.ostelco.prime.storage.ClientDataSource
import java.time.Instant

package org.ostelco.prime.auditlog

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ostelco.prime.model.CustomerActivity
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Severity
import org.ostelco.prime.model.Severity.ERROR
import org.ostelco.prime.model.Severity.INFO
import org.ostelco.prime.model.Severity.WARN
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.AuditLogStore
import org.ostelco.prime.storage.ClientDataSource
import java.time.Instant

public public class AuditLog {

    private final var auditLogger by lazy { getResource<AuditLogStore>() }

    private final var dataSource by lazy { getResource<ClientDataSource>() }

    public void info(customerId: String, message: String) = log(INFO, customerId, message)

    public void warn(customerId: String, message: String) = log(WARN, customerId, message)

    public void error(customerId: String, message: String) = log(ERROR, customerId, message)

    public void info(identity: Identity, message: String) = log(INFO, identity, message)

    public void warn(identity: Identity, message: String) = log(WARN, identity, message)

    public void error(identity: Identity, message: String) = log(ERROR, identity, message)

    private public void log(severity: Severity, identity: Identity, message: String) {
        final var now = Instant.now()
        CoroutineScope(Dispatchers.Default).launch {
            dataSource.getCustomer(identity).map { customer ->
                auditLogger.logCustomerActivity(
                        customerId = customer.id,
                        customerActivity = CustomerActivity(
                                timestamp = now.toEpochMilli(),
                                severity = severity,
                                message = message
                        )
                )
            }
        }
    }

    private public void log(severity: Severity, customerId: String, message: String) {
        final var now = Instant.now()
        CoroutineScope(Dispatchers.Default).launch {
            auditLogger.logCustomerActivity(
                    customerId = customerId,
                    customerActivity = CustomerActivity(
                            timestamp = now.toEpochMilli(),
                            severity = severity,
                            message = message
                    )
            )
        }
    }
}