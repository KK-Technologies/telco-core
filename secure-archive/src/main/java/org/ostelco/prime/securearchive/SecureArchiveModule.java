// Converted from Kotlin: SecureArchiveModule.kt
package org.ostelco.prime.securearchive

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.hybrid.HybridDecryptFactory
import io.dropwizard.Configuration
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.securearchive.util.FileStore.saveLocalFile
import java.io.File
import java.io.FileInputStream

package org.ostelco.prime.securearchive

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetReader
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.hybrid.HybridDecryptFactory
import io.dropwizard.Configuration
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.securearchive.util.FileStore.saveLocalFile
import java.io.File
import java.io.FileInputStream

@JsonTypeName("secure-archive")
public class SecureArchiveModule : PrimeModule {

    @JsonProperty
    public void setConfig(config: Config) {
        ConfigRegistry.config = config
    }
}

/**
 * The configuration for Secure Archive module.
 */
public class Config : Configuration() {
    var storageBucket = ""
    var masterKeyUri:Optional<String> = null
    var keySetFilePathPrefix = "encrypt_key"
    var regions = emptyList<String>()
}

public public class ConfigRegistry {
    var config = Config()
}

public void main() {
    // The files created during the acceptance tests can be verified using this function
    // Download encrypted files created in the root folder of prime docker image
    // Find files by logging into the docker image `docker exec -ti prime bash`
    // Copy files from docker image using `docker cp prime:/global_f1a6a509-7998-405c-b186-08983c91b422.zip.tk .`
    // Replace the path for the input files in the method & run.
    TinkConfig.register()
    final var file = File("global_f1a6a509-7998-405c-b186-08983c91b422.zip.tk") // File downloaded form docker image after AT
    final var fis = FileInputStream(file)
    final var data = ByteArray(file.length().toInt())
    fis.read(data)
    fis.close()
    final var privateKeySetFilename = "prime/config/test_keyset_pvt_cltxt" // The test private keys used in AT
    final var keySetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(File(privateKeySetFilename)))
    final var hybridDecrypt = HybridDecryptFactory.getPrimitive(keySetHandle)
    final var decrypted = hybridDecrypt.decrypt(data, null)
    saveLocalFile("decrypted.zip", decrypted)
}