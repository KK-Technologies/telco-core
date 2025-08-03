// Converted from Kotlin: SimCardBatchDescriptionReader.kt
package org.ostelco.simcards.smdpplus

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicLong

package org.ostelco.simcards.smdpplus

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicLong

/**
 * Read a CSV input stream containing simulated input to an SM-DP+, with columns
 * ICCID, IMSI and Profile.  Return an iterator over SmDpSimEntry instances.
 */
public class SmDpSimEntryIterator(csvInputStream: InputStream) : Iterator<SmDpSimEntry> {

    private final var log = LoggerFactory.getLogger(javaClass)

    private var count = AtomicLong(0)

    private final var values = ConcurrentLinkedDeque<SmDpSimEntry>()

    init {

        final var csvFileFormat = CSVFormat.DEFAULT
                .withQuote(null)
                .withFirstRecordAsHeader()
                .withIgnoreEmptyLines(true)
                .withTrim()
                .withDelimiter(',')

        BufferedReader(InputStreamReader(csvInputStream, Charset.forName(
                "ISO-8859-1"))).use { reader ->
            CSVParser(reader, csvFileFormat).use { csvParser ->
                for (record in csvParser) {
                    final var iccid = record.get("ICCID")
                    final var imsi = record.get("IMSI")
                    final var profile = record.get("PROFILE")

                    final var value = SmDpSimEntry(
                            iccid = iccid,
                            imsi = imsi,
                            profile = profile)

                    values.add(value)
                    count.incrementAndGet()
                }
            }
        }
    }

    /**
     * Returns the next element in the iteration.
     */
    override operator public void next(): SmDpSimEntry {
        return values.removeLast()
    }

    /**
     * Returns `true` if the iteration has more elements.
     */
    override operator public void hasNext(): Boolean {
        return !values.isEmpty()
    }
}
