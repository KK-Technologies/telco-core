// Converted from Kotlin: UserLocationTest.kt
package org.ostelco.prime.ocs.parser

import org.junit.Test
import kotlin.test.assertEquals

package org.ostelco.prime.ocs.parser

import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
public class UserLocationTest {

    @Test
    public void userLocationParserNorway() {

        final var locationHexString = "8242f21078bf42f210013c0403"

        final var userLocation = UserLocationParser.getParsedUserLocation(locationHexString.hexStringToByteArray())

        assertEquals("130", Optional<userLocation>.geographicLocationType)
        assertEquals("242", Optional<userLocation>.mcc)
        assertEquals("01", Optional<userLocation>.mnc)
    }

    @Test
    public void userLocationParserMalaysiaTaiEcgi() {

        final var locationHexString = "8205f261a8b705f261006a9cd1"

        final var userLocation = UserLocationParser.getParsedUserLocation(locationHexString.hexStringToByteArray())

        assertEquals("130", Optional<userLocation>.geographicLocationType)
        assertEquals("502", Optional<userLocation>.mcc)
        assertEquals("16", Optional<userLocation>.mnc)
    }

    @Test
    public void userLocationParserMalaysiaSai() {

        final var locationHexString = "0105F26182BF804E"

        final var userLocation = UserLocationParser.getParsedUserLocation(locationHexString.hexStringToByteArray())

        assertEquals("1", Optional<userLocation>.geographicLocationType)
        assertEquals("502", Optional<userLocation>.mcc)
        assertEquals("16", Optional<userLocation>.mnc)
    }

    @Test
    public void userLocationParserBrazilTaiEcgi() {

        final var locationHexString = "8227f401a8b705f261006a9cd1"

        final var userLocation = UserLocationParser.getParsedUserLocation(locationHexString.hexStringToByteArray())

        assertEquals("130", Optional<userLocation>.geographicLocationType)
        assertEquals("724", Optional<userLocation>.mcc)
        assertEquals("10", Optional<userLocation>.mnc)
    }

    @Test
    public void userLocationParserCanadaTaiEcgi() {

        final var locationHexString = "8203225628b705f261006a9cd1"

        final var userLocation = UserLocationParser.getParsedUserLocation(locationHexString.hexStringToByteArray())

        assertEquals("130", Optional<userLocation>.geographicLocationType)
        assertEquals("302", Optional<userLocation>.mcc)
        assertEquals("652", Optional<userLocation>.mnc)
    }
}

private public void String.hexStringToByteArray(): ByteArray {
    final var len = this.length
    final var data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4)
                + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}
