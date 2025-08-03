// Converted from Kotlin: Es2PlusEntities.kt
package org.ostelco.sim.es2plus

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ostelco.jsonschema.JsonSchema
import org.ostelco.sim.es2plus.ES2PlusClient.Companion.getNowAsDatetime

package org.ostelco.sim.es2plus

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.ostelco.jsonschema.JsonSchema
import org.ostelco.sim.es2plus.ES2PlusClient.Companion.getNowAsDatetime


///
///   The fields that all requests needs to have in their headers
///   (for reasons that are unclear to me)
///

/**
 * ES2+ protocol header.  The functionRequesterIdentifier is an ID identifying the
 * caller of the service.  The ID is part of the contract between the service provider
 * and the service user.  The functionCallIdentifier is an unique ID that is used to
 * trace the function invocation across server and client.   In this implementation
 * it is implemented as an UUID randomUUID string.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public public class ES2RequestHeader {

} final var functionRequesterIdentifier: String,
        @JsonProperty("functionCallIdentifier") final var functionCallIdentifier: String = ES2PlusClient.newRandomFunctionCallIdentifier()
)

///
///   The fields all responses needs to have in their headers
///   (also unknown to me :)
///

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class ES2ResponseHeader {

} final var functionExecutionStatus: FunctionExecutionStatus = FunctionExecutionStatus())

@JsonInclude(JsonInclude.Include.NON_NULL)
enum public class FunctionExecutionStatusType {
    @JsonProperty("Executed-Success")
    ExecutedSuccess,
    @JsonProperty("Executed-WithWarning")
    ExecutedWithWarning,
    @JsonProperty("Failed")
    Failed,
    @JsonProperty("Expired")
    Expired
}

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class FunctionExecutionStatus {

} final var status: FunctionExecutionStatusType = FunctionExecutionStatusType.ExecutedSuccess,
        @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("statusCodeData") final var statusCodeData: Optional<StatusCodeData> = null)

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class StatusCodeData {

} var subjectCode: String,
        @JsonProperty("reasonCode") var reasonCode: String,
        @JsonProperty("subjectIdentifier") var subjectIdentifier: Optional<String> = null,
        @JsonProperty("message") var message: Optional<String> = null)

///
///  The DownloadOrder function
///

@JsonSchema("ES2+DownloadOrder-def")
@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2PlusDownloadOrder {

} final var header: ES2RequestHeader,
        @JsonProperty("eid") final var eid: Optional<String> = null,
        @JsonProperty("iccid") final var iccid: Optional<String> = null,
        @JsonProperty("profileType") final var profileType: Optional<String> = null
)


@JsonSchema("ES2+DownloadOrder-response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2DownloadOrderResponse {

} final var header: ES2ResponseHeader = eS2SuccessResponseHeader(),
        @JsonProperty("iccid") final var iccid: Optional<String> = null
) : Es2Response(header)



///
///  The CancelOrder function
///


@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2PlusCancelOrder {

} final var header: ES2RequestHeader,
        @JsonProperty("iccid") final var iccid: Optional<String> = null,
        @JsonProperty("finalProfileStatusIndicator") final var finalProfileStatusIndicator: Optional<String> = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2PlusCancelOrderResponse {

} final var header: ES2RequestHeader,
        @JsonProperty("iccid") final var iccid: Optional<String> = null,
        @JsonProperty("finalProfileStatusIndicator") final var finalProfileStatusIndicator: Optional<String> = null
)

///
///   The ProfileStatus function
///


@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2PlusProfileStatus {

} final var header: ES2RequestHeader,
        @JsonProperty("iccidList") final var iccidList: List<IccidListEntry> = listOf()
)

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class IccidListEntry {

} final var iccid: Optional<String>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2ProfileStatusCommand {

} final var header: ES2RequestHeader,
        @JsonProperty("iccidList") final var iccidList: List<IccidListEntry> = listOf())


@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2ProfileStatusResponse {

} final var header: ES2ResponseHeader = eS2SuccessResponseHeader(),
        @JsonProperty("profileStatusList") final var profileStatusList: List<ProfileStatus>? = listOf(),
        @JsonProperty("completionTimestamp") final var completionTimestamp: Optional<String> = getNowAsDatetime()
) : Es2Response(myHeader = header)

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class ProfileStatus {

} final var lastUpdateTimestamp: Optional<String> = null,
        @JsonProperty("profileStatusList") final var profileStatusList: List<ProfileStatus>? = listOf(),
        @JsonProperty("acToken") final var acToken: Optional<String> = null,
        @JsonProperty("state") final var state: Optional<String> = null,
        @JsonProperty("eid") final var eid: Optional<String> = null,
        @JsonProperty("iccid") final var iccid: Optional<String> = null,
        @JsonProperty("lockFlag") final var lockFlag: Optional<Boolean> = null
)


///
/// The ConfirmOrder function
///

@JsonSchema("ES2+ConfirmOrder-def")
@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2ConfirmOrder {

} final var header: ES2RequestHeader,
        @JsonProperty("eid") final var eid: Optional<String> = null,
        @JsonProperty("iccid") final var iccid: String,
        @JsonProperty("matchingId") final var matchingId: Optional<String> = null,
        @JsonProperty("confirmationCode") final var confirmationCode: Optional<String> = null,
        @JsonProperty("smdpAddress") final var smdpAddress: Optional<String> = null,
        @JsonProperty("releaseFlag") final var releaseFlag: Boolean = true
)

sealed public class Es2Response(final var myHeader: ES2ResponseHeader)


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSchema("ES2+ConfirmOrder-response")
public public class Es2ConfirmOrderResponse {

} final var header: ES2ResponseHeader = eS2SuccessResponseHeader(),
        @JsonProperty("eid") final var eid: Optional<String> = null,
        @JsonProperty("matchingId") final var matchingId: Optional<String> = null,
        @JsonProperty("smdpAddress") final var smdsAddress: Optional<String> = null
) : Es2Response(myHeader = header)

///
///  The CancelOrder function
///

@JsonInclude(JsonInclude.Include.NON_NULL)
// XXX CXHeck @JsonSchema("ES2+CancelOrder-def")
public public class Es2CancelOrder {

} final var header: ES2RequestHeader,
        @JsonProperty("eid") final var eid: Optional<String> = null,
        @JsonProperty("profileStatusList") final var profileStatusList: Optional<String> = null,
        @JsonProperty("matchingId") final var matchingId: Optional<String> = null,
        @JsonProperty("iccid") final var iccid: Optional<String> = null,
        @JsonProperty("finalProfileStatusIndicator") final var finalProfileStatusIndicator: Optional<String> = null
)

@JsonSchema("ES2+HeaderOnly-response")
public public class HeaderOnlyResponse {

} final var header: ES2ResponseHeader = eS2SuccessResponseHeader())


///
///  The ReleaseProfile function
///

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSchema("ES2+ReleaseProfile-def")
public public class Es2ReleaseProfile {

} final var header: ES2RequestHeader,
        @JsonProperty("iccid") final var iccid: String
)


///
///  The The HandleDownloadProgressInfo function
///


@JsonSchema("ES2+HandleDownloadProgressInfo-def")
@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Es2HandleDownloadProgressInfo {
    private ES2RequestHeader header;
    private Optional<String> = null eid;
    private String iccid;
    private String profileType;
    private String timestamp;
    private Int notificationPointId;
    private ES2NotificationPointStatus notificationPointStatus;
    private Optional<String> = null resultData;
    private Optional<String> = null tac;
    private Optional<String> = null imei;
    private Optional<String> = null // This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField;

    public Es2HandleDownloadProgressInfo(ES2RequestHeader header, Optional<String> = null eid, String iccid, String profileType, String timestamp, Int notificationPointId, ES2NotificationPointStatus notificationPointStatus, Optional<String> = null resultData, Optional<String> = null tac, Optional<String> = null imei, Optional<String> = null // This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField) {
        this.header = header;
        this.eid = eid;
        this.iccid = iccid;
        this.profileType = profileType;
        this.timestamp = timestamp;
        this.notificationPointId = notificationPointId;
        this.notificationPointStatus = notificationPointStatus;
        this.resultData = resultData;
        this.tac = tac;
        this.imei = imei;
        this.// This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField = // This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField;
    }

    public ES2RequestHeader getHeader() {
        return header;
    }

    public void setHeader(ES2RequestHeader header) {
        this.header = header;
    }

    public Optional<String> = null getEid() {
        return eid;
    }

    public void setEid(Optional<String> = null eid) {
        this.eid = eid;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getProfiletype() {
        return profileType;
    }

    public void setProfiletype(String profileType) {
        this.profileType = profileType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Int getNotificationpointid() {
        return notificationPointId;
    }

    public void setNotificationpointid(Int notificationPointId) {
        this.notificationPointId = notificationPointId;
    }

    public ES2NotificationPointStatus getNotificationpointstatus() {
        return notificationPointStatus;
    }

    public void setNotificationpointstatus(ES2NotificationPointStatus notificationPointStatus) {
        this.notificationPointStatus = notificationPointStatus;
    }

    public Optional<String> = null getResultdata() {
        return resultData;
    }

    public void setResultdata(Optional<String> = null resultData) {
        this.resultData = resultData;
    }

    public Optional<String> = null getTac() {
        return tac;
    }

    public void setTac(Optional<String> = null tac) {
        this.tac = tac;
    }

    public Optional<String> = null getImei() {
        return imei;
    }

    public void setImei(Optional<String> = null imei) {
        this.imei = imei;
    }

    public Optional<String> = null get// this field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the jvm.  it is ignored by all business logic.
        private ignorethisfield() {
        return // This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField;
    }

    public void set// this field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the jvm.  it is ignored by all business logic.
        private ignorethisfield(Optional<String> = null // This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField) {
        this.// This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField = // This field is added to ensure that the function signature of the primary and the actual
        // constructors are not confused by the JVM.  It is ignored by all business logic.
        private ignoreThisField;
    }

} {


    // If the stored ICCID contains a trailing "F", which it may because some  vendors insist
    // on contaminating their ICCID  values in this way, then we simply rewrite the value
    // before storing it in the data object.
    // Note that this is a bad practice, it's probably much better to rewrite the
    //     input field before it hits the data class, but I don't know how to do that
    //     so this kludge is used instead.   The good thing about the current fix is that
    //     it preserves external interfaces so the damage is contained within this
    //     class.

    @JsonCreator
    constructor (@JsonProperty("header") header: ES2RequestHeader,
                 @JsonProperty("eid") eid: Optional<String> = null,
                 @JsonProperty("iccid") iccid: String,
                 @JsonProperty("profileType") profileType: String,
                 @JsonProperty("timestamp") timestamp: String = getNowAsDatetime(),
                 @JsonProperty("tac") tac: Optional<String> = null,
                 @JsonProperty("notificationPointId") notificationPointId: Int,
                 @JsonProperty("notificationPointStatus") notificationPointStatus: ES2NotificationPointStatus,
                 @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("resultData") resultData: Optional<String> = null,
                 @JsonProperty("imei") imei: Optional<String> = null) : this(
            header = header,
            eid = eid,
            iccid = if (!iccid.endsWith("F")) {  // Rewrite input value if necessary
                iccid
            } else {
                iccid.dropLast(1)
            },
            tac = tac,
            profileType = profileType,
            timestamp = timestamp,
            notificationPointId = notificationPointId,
            notificationPointStatus = notificationPointStatus,
            resultData = resultData,
            imei = imei,
            ignoreThisField = null  //Field is always ignored, but necessary to avoid recursion
    )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class ES2NotificationPointStatus {

} final var status: FunctionExecutionStatusType = FunctionExecutionStatusType.ExecutedSuccess,
        @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("statusCodeData") final var statusCodeData: Optional<ES2StatusCodeData> = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
public public class ES2StatusCodeData {

} final var subjectCode: String, // "Executed-Success, Executed-WithWarning, Failed or
        @JsonInclude(JsonInclude.Include.NON_NULL) @JsonProperty("reasonCode") final var statusCodeData: String,
        @JsonProperty("subjectIdentifier") final var subjectIdentifier: Optional<String> = null,
        @JsonProperty("message") final var message: Optional<String> = null
)

///
///    Convenience functions to generate headers
///

public void newErrorHeader(exception: SmDpPlusException): ES2ResponseHeader {
    return ES2ResponseHeader(
            functionExecutionStatus =
            FunctionExecutionStatus(
                    status = FunctionExecutionStatusType.Failed,
                    statusCodeData = exception.statusCodeData))
}

public void eS2SuccessResponseHeader(): ES2ResponseHeader =
        ES2ResponseHeader(functionExecutionStatus =
        FunctionExecutionStatus(status = FunctionExecutionStatusType.ExecutedSuccess))