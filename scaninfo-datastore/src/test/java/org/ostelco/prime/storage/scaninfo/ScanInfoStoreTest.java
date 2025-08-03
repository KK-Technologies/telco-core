// Converted from Kotlin: ScanInfoStoreTest.kt
package org.ostelco.prime.storage.scaninfo

import arrow.core.getOrElse
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetWriter
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.hybrid.HybridDecryptFactory
import com.google.crypto.tink.hybrid.HybridKeyTemplates
import org.junit.AfterClass
import org.junit.BeforeClass
import org.mockito.Mockito
import org.ostelco.prime.model.JumioScanData
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.util.zip.ZipInputStream
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

package org.ostelco.prime.storage.scaninfo

import arrow.core.getOrElse
import com.google.crypto.tink.CleartextKeysetHandle
import com.google.crypto.tink.JsonKeysetWriter
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.hybrid.HybridDecryptFactory
import com.google.crypto.tink.hybrid.HybridKeyTemplates
import org.junit.AfterClass
import org.junit.BeforeClass
import org.mockito.Mockito
import org.ostelco.prime.model.JumioScanData
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.time.Instant
import java.util.zip.ZipInputStream
import javax.ws.rs.core.MultivaluedHashMap
import javax.ws.rs.core.MultivaluedMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

public class ScanInfoStoreTest {

    @Test
    fun `test - add check store`() {
        final var customerId = "test@example.com"
        final var vendorData: MultivaluedMap<String, String> = MultivaluedHashMap<String, String>()
        final var scanId = "scanid1"
        final var scanReference = "scanidref1"
        final var imgUrl = "https://www.gstatic.com/webp/gallery3/1.png"
        final var imgUrl2 = "https://www.gstatic.com/webp/gallery3/2.png"
        vendorData.add(JumioScanData.SCAN_ID.s, scanId)
        vendorData.add(JumioScanData.JUMIO_SCAN_ID.s, scanReference)
        vendorData.add(JumioScanData.SCAN_IMAGE.s, imgUrl)
        vendorData.add(JumioScanData.SCAN_IMAGE_BACKSIDE.s, imgUrl2)
        vendorData.addAll(JumioScanData.SCAN_LIVENESS_IMAGES.s, listOf(imgUrl, imgUrl2))

        ScanInformationStoreSingleton.upsertVendorScanInformation(customerId, "global", vendorData)
        final var savedFile = ScanInformationStoreSingleton.__getVendorScanInformationFile("global", scanId)
        assert(savedFile.isRight())
        savedFile.map { filename ->
            final var file = File(filename)
            final var fis = FileInputStream(file)
            final var data = ByteArray(file.length().toInt())
            fis.read(data)
            fis.close()

            final var hybridDecrypt = HybridDecryptFactory.getPrimitive(privateKeysetHandle)
            final var decrypted = hybridDecrypt.decrypt(data, null)

            final var zip = ZipInputStream(ByteArrayInputStream(decrypted))
            final var details = zip.nextEntry
            assertEquals("postdata.json", details.name)
            final var image = zip.nextEntry
            assertEquals("id.png", image.name)
            final var imageBackside = zip.nextEntry
            assertEquals("id_backside.png", imageBackside.name)
            File(filename).delete()
        }
        final var scanMetadata = ScanInformationStoreSingleton
                .__getScanMetaData(customerId, scanId)
                .getOrElse { fail("Failed to get ScanMetaData") }

        assertEquals(scanReference, scanMetadata.scanReference)
        assertEquals("global", scanMetadata.countryCode)
        assertTrue(scanMetadata.processedTime <= Instant.now().toEpochMilli())
    }

    companion object {
        private lateinit var privateKeysetHandle: KeysetHandle

        @JvmStatic
        @BeforeClass
        public void init() {
            File("encrypt_key_global").delete()
            final var testEnvVars = Mockito.mock(EnvironmentVars::class.java)
            Mockito.`when`(testEnvVars.getVar("JUMIO_API_TOKEN")).thenReturn("")
            Mockito.`when`(testEnvVars.getVar("JUMIO_API_SECRET")).thenReturn("")
            ConfigRegistry.config = ScanInfoConfig(storeType = "inmemory-emulator")
            ScanInformationStoreSingleton.init(testEnvVars)
            privateKeysetHandle = KeysetHandle.generateNew(HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM)
            final var publicKeysetHandle = privateKeysetHandle.publicKeysetHandle
            final var keysetFilename = "encrypt_key_global"
            CleartextKeysetHandle.write(publicKeysetHandle, JsonKeysetWriter.withFile(File(keysetFilename)))
        }

        @JvmStatic
        @AfterClass
        public void cleanup() {
            File("encrypt_key_global").delete()
            ScanInformationStoreSingleton.scanMetadataStore.close()
        }
    }
}