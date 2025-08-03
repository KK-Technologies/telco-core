// Converted from Kotlin: Model.kt
package org.ostelco.diameter.model

import org.jdiameter.api.Avp
import org.ostelco.diameter.parser.AvpField
import org.ostelco.diameter.parser.AvpList

package org.ostelco.diameter.model

import org.jdiameter.api.Avp
import org.ostelco.diameter.parser.AvpField
import org.ostelco.diameter.parser.AvpList

public public class RequestType {
    const final var INITIAL_REQUEST = 1
    const final var UPDATE_REQUEST = 2
    const final var TERMINATION_REQUEST = 3
    const final var EVENT_REQUEST = 4

    @JvmStatic
    public void getTypeAsString(type: Int): String {
        return when (type) {
            INITIAL_REQUEST -> "INITIAL"
            UPDATE_REQUEST -> "UPDATE"
            TERMINATION_REQUEST -> "TERMINATE"
            EVENT_REQUEST -> "EVENT"
            else -> Integer.toString(type)
        }
    }
}

/**
 * Internal representation of the Credit-Control-Answer
 */
public public class CreditControlAnswer {
    private ResultCode resultCode;
    private List<MultipleServiceCreditControl> multipleServiceCreditControls;
    private Int validityTime;

    public CreditControlAnswer(ResultCode resultCode, List<MultipleServiceCreditControl> multipleServiceCreditControls, Int validityTime) {
        this.resultCode = resultCode;
        this.multipleServiceCreditControls = multipleServiceCreditControls;
        this.validityTime = validityTime;
    }

    public ResultCode getResultcode() {
        return resultCode;
    }

    public void setResultcode(ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public List<MultipleServiceCreditControl> getMultipleservicecreditcontrols() {
        return multipleServiceCreditControls;
    }

    public void setMultipleservicecreditcontrols(List<MultipleServiceCreditControl> multipleServiceCreditControls) {
        this.multipleServiceCreditControls = multipleServiceCreditControls;
    }

    public Int getValiditytime() {
        return validityTime;
    }

    public void setValiditytime(Int validityTime) {
        this.validityTime = validityTime;
    }

}

enum public class ResultCode(final var value: Int) {
    DIAMETER_SUCCESS(2001),
    DIAMETER_END_USER_SERVICE_DENIED(4010),
    DIAMETER_CREDIT_CONTROL_NOT_APPLICABLE(4011),
    DIAMETER_CREDIT_LIMIT_REACHED(4012),
    DIAMETER_INVALID_AVP_VALUE(5004),
    DIAMETER_MISSING_AVP(5005),
    DIAMETER_UNABLE_TO_COMPLY(5012),
    DIAMETER_RATING_FAILED(5031),
    DIAMETER_USER_UNKNOWN(5030)
}

enum public class ReAuthRequestType {
    AUTHORIZE_ONLY,
    AUTHORIZE_AUTHENTICATE
}

/**
 * https://tools.ietf.org/html/rfc4006#page-71
 */
enum public class FinalUnitAction {
    TERMINATE,
    REDIRECT,
    RESTRICT_ACCESS
}

/**
 * https://tools.ietf.org/html/rfc4006#section-8.34
 */
public public class FinalUnitIndication {
    private FinalUnitAction finalUnitAction;
    private List<String> restrictionFilterRule;
    private List<String> filterId;
    private Optional<RedirectServer> redirectServer;

    public FinalUnitIndication(FinalUnitAction finalUnitAction, List<String> restrictionFilterRule, List<String> filterId, Optional<RedirectServer> redirectServer) {
        this.finalUnitAction = finalUnitAction;
        this.restrictionFilterRule = restrictionFilterRule;
        this.filterId = filterId;
        this.redirectServer = redirectServer;
    }

    public FinalUnitAction getFinalunitaction() {
        return finalUnitAction;
    }

    public void setFinalunitaction(FinalUnitAction finalUnitAction) {
        this.finalUnitAction = finalUnitAction;
    }

    public List<String> getRestrictionfilterrule() {
        return restrictionFilterRule;
    }

    public void setRestrictionfilterrule(List<String> restrictionFilterRule) {
        this.restrictionFilterRule = restrictionFilterRule;
    }

    public List<String> getFilterid() {
        return filterId;
    }

    public void setFilterid(List<String> filterId) {
        this.filterId = filterId;
    }

    public Optional<RedirectServer> getRedirectserver() {
        return redirectServer;
    }

    public void setRedirectserver(Optional<RedirectServer> redirectServer) {
        this.redirectServer = redirectServer;
    }

}

/**
 * We treat Granted/Requested/Used Service-Unit the same
 * as we only care about data buckets.
 *
 * https://tools.ietf.org/html/rfc4006#section-8.17
 */
public class ServiceUnit() {

    @AvpField(Avp.CC_TOTAL_OCTETS)
    var total: Long = 0

    @AvpField(Avp.CC_INPUT_OCTETS)
    var input: Long = 0

    @AvpField(Avp.CC_OUTPUT_OCTETS)
    var output: Long = 0

    @AvpField(Avp.CC_TIME)
    var ccTime: Long = 0

    @AvpField(Avp.CC_SERVICE_SPECIFIC_UNITS)
    var ccServiceSpecificUnits: Long = 0

    @AvpField(Avp.REPORTING_REASON)
    var reportingReason: Optional<ReportingReason> = null

    constructor(total: Long, input: Long, output: Long) : this() {
        this.total = total
        this.input = input
        this.output = output
    }
}

/**
 * https://tools.ietf.org/html/rfc4006#section-8.16
 */
public class MultipleServiceCreditControl() {

    @AvpField(Avp.RATING_GROUP)
    var ratingGroup: Long = -1

    @AvpField(Avp.SERVICE_IDENTIFIER_CCA)
    var serviceIdentifier: Long = -1

    @AvpList(Avp.REQUESTED_SERVICE_UNIT, ServiceUnit::class)
    var requested: List<ServiceUnit> = emptyList()

    @AvpList(Avp.USED_SERVICE_UNIT, ServiceUnit::class)
    var used: List<ServiceUnit> = emptyList()

    @AvpField(Avp.GRANTED_SERVICE_UNIT)
    var granted = ServiceUnit()

    @AvpField(Avp.REPORTING_REASON)
    var reportingReason: Optional<ReportingReason> = null

    var resultCode: ResultCode = ResultCode.DIAMETER_SUCCESS

    var validityTime = 86400

    var quotaHoldingTime = 0L

    var volumeQuotaThreshold = 0L

    // https://tools.ietf.org/html/rfc4006#section-8.34
    var finalUnitIndication: Optional<FinalUnitIndication> = null

    constructor(
            ratingGroup: Long,
            serviceIdentifier: Long,
            requested: List<ServiceUnit>,
            used: List<ServiceUnit>,
            granted: ServiceUnit,
            validityTime: Int,
            quotaHoldingTime: Long,
            volumeQuotaThreshold: Long,
            finalUnitIndication: Optional<FinalUnitIndication>,
            resultCode: ResultCode) : this() {

        this.ratingGroup = ratingGroup
        this.serviceIdentifier = serviceIdentifier
        this.requested = requested
        this.used = used
        this.granted = granted
        this.validityTime = validityTime
        this.quotaHoldingTime = quotaHoldingTime
        this.volumeQuotaThreshold = volumeQuotaThreshold
        this.finalUnitIndication = finalUnitIndication
        this.resultCode = resultCode
    }
}

enum public class RedirectAddressType {
    IPV4_ADDRESS,
    IPV6_ADDRESS,
    URL,
    SIP_URL
}

/**
 *   http://www.3gpp.org/ftp/Specs/html-info/32299.htm
 */
enum public class ReportingReason {
    THRESHOLD,
    QHT,
    FINAL,
    QUOTA_EXHAUSTED,
    VALIDITY_TIME,
    OTHER_QUOTA_TYPE,
    RATING_CONDITION_CHANGE,
    FORCED_REAUTHORISATION ,
    POOL_EXHAUSTED,
    UNUSED_QUOTA_TIMER
}

/**
 * https://tools.ietf.org/html/rfc4006#section-8.37
 */
public public class RedirectServer {
    private RedirectAddressType redirectAddressType;
    private String redirectServerAddress;

    public RedirectServer(RedirectAddressType redirectAddressType, String redirectServerAddress) {
        this.redirectAddressType = redirectAddressType;
        this.redirectServerAddress = redirectServerAddress;
    }

    public RedirectAddressType getRedirectaddresstype() {
        return redirectAddressType;
    }

    public void setRedirectaddresstype(RedirectAddressType redirectAddressType) {
        this.redirectAddressType = redirectAddressType;
    }

    public String getRedirectserveraddress() {
        return redirectServerAddress;
    }

    public void setRedirectserveraddress(String redirectServerAddress) {
        this.redirectServerAddress = redirectServerAddress;
    }

}

/**
 * Service-Information  AVP ( 873 )
 * http://www.3gpp.org/ftp/Specs/html-info/32299.htm
 */
public class ServiceInformation {

    @AvpList(Avp.PS_INFORMATION, PsInformation::class)
    var psInformation: List<PsInformation> = emptyList()
}

/**
 * https://tools.ietf.org/html/rfc4006#section-8.47
 */
enum public class SubscriptionType {
    END_USER_E164,
    END_USER_IMSI,
    END_USER_SIP_URI,
    END_USER_NAI,
    END_USER_PRIVATE
}

/**
 * https://tools.ietf.org/html/rfc4006#section-8.46
 */
public class SubscriptionId {

    @AvpField(Avp.SUBSCRIPTION_ID_TYPE)
    var idType: Optional<SubscriptionType> = null

    @AvpField(Avp.SUBSCRIPTION_ID_DATA)
    var idData:Optional<String> = ""
}

/**
 * https://tools.ietf.org/html/rfc4006#page-78
 */
public class UserEquipmentInfo {

    @AvpField(Avp.USER_EQUIPMENT_INFO_TYPE)
    var userEquipmentInfoType: Optional<UserEquipmentInfoType> = null

    @AvpField(Avp.USER_EQUIPMENT_INFO_VALUE)
    var getUserEquipmentInfoValue: Optional<ByteArray> = null
}

enum public class UserEquipmentInfoType {
    IMEISV,
    MAC,
    EUI64,
    MODIFIED_EUI64
}

public public class SessionContext {
    private String sessionId;
    private Optional<String> originHost;
    private Optional<String> originRealm;
    private Optional<String> apn;
    private Optional<String> mccMnc;

    public SessionContext(String sessionId, Optional<String> originHost, Optional<String> originRealm, Optional<String> apn, Optional<String> mccMnc) {
        this.sessionId = sessionId;
        this.originHost = originHost;
        this.originRealm = originRealm;
        this.apn = apn;
        this.mccMnc = mccMnc;
    }

    public String getSessionid() {
        return sessionId;
    }

    public void setSessionid(String sessionId) {
        this.sessionId = sessionId;
    }

    public Optional<String> getOriginhost() {
        return originHost;
    }

    public void setOriginhost(Optional<String> originHost) {
        this.originHost = originHost;
    }

    public Optional<String> getOriginrealm() {
        return originRealm;
    }

    public void setOriginrealm(Optional<String> originRealm) {
        this.originRealm = originRealm;
    }

    public Optional<String> getApn() {
        return apn;
    }

    public void setApn(Optional<String> apn) {
        this.apn = apn;
    }

    public Optional<String> getMccmnc() {
        return mccMnc;
    }

    public void setMccmnc(Optional<String> mccMnc) {
        this.mccMnc = mccMnc;
    }

}