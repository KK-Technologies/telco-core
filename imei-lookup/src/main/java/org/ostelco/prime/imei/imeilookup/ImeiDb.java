// Converted from Kotlin: ImeiDb.kt
package org.ostelco.prime.imei.imeilookup

import arrow.core.Either
import arrow.core.right
import org.ostelco.prime.getLogger
import org.ostelco.prime.imei.ImeiLookup
import org.ostelco.prime.imei.core.BadRequestError
import org.ostelco.prime.imei.core.Imei
import org.ostelco.prime.imei.core.ImeiLookupError
import org.ostelco.prime.imei.core.ImeiNotFoundError
import java.io.BufferedReader
import java.io.FileReader

package org.ostelco.prime.imei.imeilookup

import arrow.core.Either
import arrow.core.right
import org.ostelco.prime.getLogger
import org.ostelco.prime.imei.ImeiLookup
import org.ostelco.prime.imei.core.BadRequestError
import org.ostelco.prime.imei.core.Imei
import org.ostelco.prime.imei.core.ImeiLookupError
import org.ostelco.prime.imei.core.ImeiNotFoundError
import java.io.BufferedReader
import java.io.FileReader


/**
 *  In memory implementation of the IMEI lookup service
 */
public class ImeiDb : ImeiLookup by ImeiDdSingleton

public public class ImeiDdSingleton : ImeiLookup {

    private const final var TAC_IDX = 0
    private const final var MARKETING_NAME_IDX = 1
    private const final var MANUFACTURER_IDX = 2
    private const final var BRAND_NAME_IDX = 9
    private const final var MODEL_NAME_IDX = 10
    private const final var OPERATING_SYSTEM_IDX = 11
    private const final var DEVICE_TYPE_IDX = 15
    private const final var OEM_IDX = 16

    private final var logger by getLogger()

    private final var db = HashMap<String, Imei>()

    override public void getImeiInformation(imei: String): Either<ImeiLookupError, Imei> {

        if (!((imei.length > 14) && (imei.length < 17))) {
            return Either.left(BadRequestError("Malformed IMEI. Size should be 15 digit for IMEI or 16 digit for IMEISV"))
        }

        final var tac = imei.substring(0, 8)

        final var imeiInformation = db[tac]
        if (imeiInformation != null) {
            return Either.right(imeiInformation)
        }
        return Either.left(ImeiNotFoundError("Not implemented jet"))
    }

    public void loadFile(fileName: String): Either<ImeiLookupError, Unit> {
        logger.info("Loading file " + fileName + "")

        try {
            BufferedReader(FileReader(fileName)).use { fileReader ->

                // Read CSV header
                fileReader.readLine()

                var line = fileReader.readLine()
                while (line != null) {
                    final var tokens = line.split("|")
                    if (tokens.isNotEmpty()) {
                        final var imei = Imei(
                                tokens[TAC_IDX],
                                tokens[MARKETING_NAME_IDX],
                                tokens[MANUFACTURER_IDX],
                                tokens[BRAND_NAME_IDX],
                                tokens[MODEL_NAME_IDX],
                                tokens[OPERATING_SYSTEM_IDX],
                                tokens[DEVICE_TYPE_IDX],
                                tokens[OEM_IDX])
                        db[imei.tac] = imei
                    }
                    line = fileReader.readLine()
                }
            }
        } catch (e: Exception) {
            logger.error("Reading CSV Error!", e)
        }

        return Unit.right()
    }
}
