// Converted from Kotlin: PsInformation.kt
package org.ostelco.diameter.model

import org.jdiameter.api.Avp
import org.ostelco.diameter.parser.AvpField
import java.net.InetAddress
import java.util.*

package org.ostelco.diameter.model

import org.jdiameter.api.Avp
import org.ostelco.diameter.parser.AvpField
import java.net.InetAddress
import java.util.*

/**
 * PS-Information ( Avp 874 )
 * http://www.3gpp.org/ftp/Specs/html-info/32299.htm
 *
 * TODO martin: Add 3GPP-GPRS-Negotiated-QoS-Profile ( Avp 5 )
 */
public class PsInformation {

    // 3GPP-Charging-Id (Avp 2)
    @AvpField(Avp.TGPP_CHARGING_ID)
    var chargingId: Optional<ByteArray> = null

    // 3GPP-PDP-Type ( Avp 3 )
    @AvpField(Avp.TGPP_PDP_TYPE)
    var pdpType: Optional<Int> = null

    // PDP-Address ( Avp 1227 )
    @AvpField(Avp.PDP_ADDRESS)
    var pdpAddress: Optional<InetAddress> = null

    // SGSN-Adress ( Avp 1228 )
    @AvpField(Avp.SGSN_ADDRESS)
    var sgsnAddress: Optional<InetAddress> = null

    // GGSN-Address ( Avp 847 )
    @AvpField(Avp.GGSN_ADDRESS)
    var ggsnAddress: Optional<InetAddress> = null

    // 3GPP-IMSI-MNC-MCC ( Avp 8 )
    @AvpField(Avp.TGPP_IMSI_MCC_MNC)
    var imsiMccMnc: Optional<String> = null

    // 3GPP-GGSN-MCC-MNC ( Avp 9 )
    @AvpField(Avp.TGPP_GGSN_MCC_MNC)
    var ggsnMccMnc: Optional<String> = null

    // 3GPP-NSAPI ( Avp 10 )
    @AvpField(10)
    var nsapi: Optional<ByteArray> = null

    // Called-Station-Id ( Avp 30 ) - Absent in dictionary
    @AvpField(30)
    var calledStationId: Optional<String> = null

    // 3GPP-Selection-Mode ( Avp 12 )
    @AvpField(Avp.TGPP_SELECTION_MODE)
    var selectionMode: Optional<String> = null

    // 3GPP-Charging-Characteristics ( Avp 13 )
    @AvpField(Avp.TGPP_CHARGING_CHARACTERISTICS)
    var chargingCharacteristics: Optional<String> = null

    // 3GPP-SGSN-MCC-MNC ( Avp 18)
    @AvpField(Avp.GPP_SGSN_MCC_MNC)
    var sgsnMccMnc: Optional<String> = null

    // 3GPP-MS-TimeZone ( Avp 23 )
    @AvpField(Avp.TGPP_MS_TIMEZONE)
    var msTimezone: Optional<ByteArray> = null

    // Charging-Rule-Base-Name ( Avp 1004 )
    @AvpField(Avp.CHARGING_RULE_BASE_NAME)
    var chargingRulebaseName: Optional<String> = null

    // 3GPP-RAT-Type ( Avp 21 )
    @AvpField(Avp.TGPP_RAT_TYPE)
    var ratType: Optional<ByteArray> = null

    // 3GPP-User-Location-Info ( Avp 21 )
    @AvpField(Avp.GPP_USER_LOCATION_INFO)
    var userLocationInfo: Optional<ByteArray> = null
}