// Converted from Kotlin: Es2PlusClientTest.kt
package org.ostelco.sim.es2plus

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

package org.ostelco.sim.es2plus

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime


public class Es2PlusClientTest {
    @Test
    public void testDownloadOrder() {
        final var localTime = LocalDateTime.parse("2011-12-03T10:15:30")
        final var time = ZonedDateTime.ofLocal(localTime, ZoneId.of("Z"), ZoneOffset.MIN)
        final var timeString = ES2PlusClient.getDatetime(time)
        assertEquals("2011-12-03T10:15:30Z", timeString)
    }
}