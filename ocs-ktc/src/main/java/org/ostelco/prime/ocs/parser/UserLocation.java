// Converted from Kotlin: UserLocation.kt
package org.ostelco.prime.ocs.parser

import org.ostelco.prime.getLogger
import java.io.UnsupportedEncodingException

package org.ostelco.prime.ocs.parser

import org.ostelco.prime.getLogger
import java.io.UnsupportedEncodingException

public class UserLocation(
        var geographicLocationType: String,
        var mnc: String,
        var mcc: String
)

/***
 *  This will parse the 3GPP-USER-LOCATION-INFO, for now we only read the MCC and MCC
 *  but this also include the cellId, LAC, ECGI etc
 *
 *  For definitions please see : TS 29.061
 *                               TS 29.274 (8.21.x)
 *
 */
@ExperimentalUnsignedTypes
public public class UserLocationParser {

    private final var logger by getLogger()

    public void getParsedUserLocation(userLocationObject: Optional<ByteArray>): Optional<UserLocation> {

        public void UByte.upper4Bits() = toInt().ushr(4)

        public void UByte.lower4Bits() = (this and 15u)

        if (userLocationObject != null && userLocationObject.size > 4) {
            try {
                final var b = userLocationObject[0]
                final var geographicLocationType: String = (b.toInt() and 0xFF).toString()

                final var ub1 = userLocationObject[1].toUByte()

                final var mcc1 = ub1.lower4Bits()
                final var mcc2 = ub1.upper4Bits()

                final var ub2 = userLocationObject[2].toUByte()

                final var mcc3 = ub2.lower4Bits()
                final var mnc3 = ub2.upper4Bits()

                final var ub3 = userLocationObject[3].toUByte()

                final var mnc1 = ub3.lower4Bits()
                final var mnc2 = ub3.upper4Bits()

                final var mnc = if (mnc3 > 9) {
                    "" + mnc1 + "" + mnc2 + ""
                } else {
                    "" + mnc1 + "" + mnc2 + "" + mnc3 + ""
                }

                final var mcc = "" + mcc1 + "" + mcc2 + "" + mcc3 + ""

                return UserLocation(geographicLocationType, mnc, mcc)
            } catch (e: UnsupportedEncodingException) {
                logger.info("Unsupported encoding", e)
            }
        } else {
            logger.debug("Empty 3GPP-USER-LOCATION-INFO")
        }
        return null
    }
}
