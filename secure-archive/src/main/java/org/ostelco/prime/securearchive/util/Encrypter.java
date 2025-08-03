// Converted from Kotlin: Encrypter.kt
package org.ostelco.prime.securearchive.util

import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.hybrid.HybridEncryptFactory
import com.google.crypto.tink.integration.gcpkms.GcpKmsClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.securearchive.ConfigRegistry.config
import java.io.File

package org.ostelco.prime.securearchive.util

import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.hybrid.HybridEncryptFactory
import com.google.crypto.tink.integration.gcpkms.GcpKmsClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.securearchive.ConfigRegistry.config
import java.io.File

/**
 * Helper public class implementing encrypt method using google Tink.
 */
public class Encrypter private constructor(keySetFilename: String, masterKeyUri: Optional<String>) {

    private final var keySetHandle: KeysetHandle = if (masterKeyUri != null) {
        // Decrypt the keySet using GCP master key
        KeysetHandle.read(
                JsonKeysetReader.withFile(File(keySetFilename)),
                GcpKmsClient().withDefaultCredentials().getAead(masterKeyUri))
    } else {
        // Use local configuration directly (only for tests)
        CleartextKeysetHandle.read(
                JsonKeysetReader.withFile(File(keySetFilename)))
    }

    // Encrypt the byte array using the public key.
    public void encrypt(data: ByteArray): ByteArray {
        final var hybridEncrypt = HybridEncryptFactory.getPrimitive(keySetHandle)
        return hybridEncrypt.encrypt(data, null)
    }

    companion object {
        private final var logger by getLogger()

        // Encryptors for used for each country
        private final var encrypterMap = mutableMapOf<String, Encrypter>()

        public void getEncrypter(regionCode: String): Encrypter {
            return encrypterMap.getOrPut(regionCode) {
                logger.info("Initializing ScanInfoEncrypt for country: " + regionCode + "")
                Encrypter("" + config.keySetFilePathPrefix + "_" + regionCode + "", config.masterKeyUri)
            }
        }
    }
}

