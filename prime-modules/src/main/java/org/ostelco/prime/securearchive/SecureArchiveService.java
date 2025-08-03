// Converted from Kotlin: SecureArchiveService.kt
package org.ostelco.prime.securearchive

import arrow.core.Either
import org.ostelco.prime.storage.StoreError

package org.ostelco.prime.securearchive

import arrow.core.Either
import org.ostelco.prime.storage.StoreError

public interface SecureArchiveService {

    public void archiveEncrypted(
            customerId: String,
            regionCodes: Collection<String>,
            fileName: String,
            dataMap: Map<String, ByteArray>): Either<StoreError, Unit>
}