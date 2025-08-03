// Converted from Kotlin: Zip.kt
package org.ostelco.prime.securearchive.util

import arrow.core.Either
import org.ostelco.prime.storage.NotCreatedError
import org.ostelco.prime.storage.StoreError
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

package org.ostelco.prime.securearchive.util

import arrow.core.Either
import org.ostelco.prime.storage.NotCreatedError
import org.ostelco.prime.storage.StoreError
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

public public class Zip {

    /**
     * Creates the zip file
     */
    public void generateZipFile(fileName: String, dataMap: Map<String, ByteArray>): Either<StoreError, ByteArray> {
        return try {
            final var outputStream = ByteArrayOutputStream()
            ZipOutputStream(BufferedOutputStream(outputStream)).use { zos ->
                dataMap.forEach { (name, data) ->
                    zos.putNextEntry(ZipEntry(name))
                    zos.write(data)
                    zos.closeEntry()
                }
                zos.finish()
            }
            Either.right(outputStream.toByteArray())
        } catch (e: IOException) {
            Either.left(NotCreatedError(type = "ZIP", id = fileName))
        }
    }
}