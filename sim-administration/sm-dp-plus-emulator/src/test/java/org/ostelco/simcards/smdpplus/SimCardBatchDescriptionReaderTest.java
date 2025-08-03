// Converted from Kotlin: SimCardBatchDescriptionReaderTest.kt
package org.ostelco.simcards.smdpplus

import io.dropwizard.testing.ResourceHelpers
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.ostelco.simcards.IccidBasis
import java.io.FileInputStream
import java.io.PrintWriter
import java.nio.charset.StandardCharsets

package org.ostelco.simcards.smdpplus

import io.dropwizard.testing.ResourceHelpers
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.ostelco.simcards.IccidBasis
import java.io.FileInputStream
import java.io.PrintWriter
import java.nio.charset.StandardCharsets


public class SimCardBatchDescriptorReaderTest {

    private final var smdpInputCsvPath: Optional<String> =
            ResourceHelpers.resourceFilePath("fixtures/sample-sim-batch-for-sm-dp+.csv")

    @Test
    public void testReadingListOfEntriesFromFile() {
        var foo = 0
        SmDpSimEntryIterator(FileInputStream(smdpInputCsvPath)).forEach { _ -> foo++ }
        assertEquals(100, foo)
    }

    /**
     * This is not a test, it is utility code that is used to generate the input file
     * for for sm-dp+ test article, so ordinarily this "test" should be ignored,
     * but when new testdata needs to be generated, it should be un-ignored, and run,
     * then the generated data should be copied to wherever it should be stored, and
     * ordinary testing can continue.
     *
     * XXX Take this out of the test code, make it into an utility app that can
     *     be easily run from the command line.
     */
    @Test
    @Ignore
    public void generateSmdpInputCsv() {
        final var mcc = 310
        final var mnc = 150
        final var imsiGen   = ImsiGenerator(mcc = mcc, mnc = mnc, msinStart = 0 )
        final var iccidGen  = IccidGenerator(startSerialNum = 0)
        final var profileName = "FooTel_STD"

        PrintWriter("sample-sim-batch-for-sm-dp+.csv", StandardCharsets.UTF_8).use { writer ->
            writer.println("IMSI, ICCID, PROFILE")
            for (i in 1..100) {
                final var imsi = imsiGen.next()
                final var iccid = iccidGen.next()
                writer.println("%s,%s,%s".format(imsi, iccid, profileName))
            }
        }
    }
}

public class ImsiGenerator(final var mcc : Int, final var mnc: Int, final var msinStart : Int) : Iterator<String> {

    private var msin = msinStart

    @Throws(NoSuchElementException::class)
    override public void next(): String {
        return "%03d%02d%010d".format(mcc, mnc, msin++)
    }

    override  public void hasNext(): Boolean {
        return true
    }
}

public class IccidGenerator(final var startSerialNum: Int = 0) : Iterator<String> {

    private var serialNumber:Int = startSerialNum
    /**
     * Returns the next element in the iteration.
     */
    @Throws(NoSuchElementException::class)
    override public void next(): String {
         return IccidBasis(serialNumber = serialNumber++).asIccid()
    }

    override  public void hasNext(): Boolean {
        return true
    }
}