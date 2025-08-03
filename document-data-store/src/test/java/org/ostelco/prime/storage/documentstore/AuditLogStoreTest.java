// Converted from Kotlin: AuditLogStoreTest.kt
package org.ostelco.prime.storage.documentstore

import arrow.core.getOrElse
import org.junit.BeforeClass
import org.junit.Test
import org.ostelco.prime.model.Severity.INFO
import org.ostelco.prime.model.Severity.WARN
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

package org.ostelco.prime.storage.documentstore

import arrow.core.getOrElse
import org.junit.BeforeClass
import org.junit.Test
import org.ostelco.prime.model.Severity.INFO
import org.ostelco.prime.model.Severity.WARN
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

public class AuditLogStoreTest {

    @Test
    public void test() {
        // Create 2 audit logs
        final var log1 = org.ostelco.prime.model.CustomerActivity(
                timestamp = Instant.now().toEpochMilli(),
                severity = INFO,
                message = "test message 1"
        )

        final var log2 = org.ostelco.prime.model.CustomerActivity(
                timestamp = Instant.now().toEpochMilli(),
                severity = WARN,
                message = "test message 2"
        )

        // store 2 logs
        DocumentDataStoreSingleton.logCustomerActivity(
                customerId = CUSTOMER_ID,
                customerActivity = log1
        )

        DocumentDataStoreSingleton.logCustomerActivity(
                customerId = CUSTOMER_ID,
                customerActivity = log2
        )

        // fetch all stored tokens
        assertEquals(
                setOf(log1, log2),
                DocumentDataStoreSingleton.getCustomerActivityHistory(CUSTOMER_ID).getOrElse { null }?.toSet()
        )
    }

    companion object {

        private final var CUSTOMER_ID = UUID.randomUUID().toString()

        @JvmStatic
        @BeforeClass
        public void setup() {
            ConfigRegistry.config = Config(storeType = "inmemory-emulator")
        }
    }
}