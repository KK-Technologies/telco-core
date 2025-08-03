// Converted from Kotlin: Es2PlusServiceInterfaces.kt
package org.ostelco.sim.es2plus


package org.ostelco.sim.es2plus


public class SmDpPlusException(final var statusCodeData: StatusCodeData) : Exception()


public interface SmDpPlusService {

    @Throws(SmDpPlusException::class)
    public void downloadOrder(eid: Optional<String>, iccid: Optional<String>, profileType: Optional<String>): Es2DownloadOrderResponse

    @Throws(SmDpPlusException::class)
    public void confirmOrder(eid: Optional<String>, iccid: Optional<String>, smdsAddress: Optional<String>, machingId: Optional<String>, confirmationCode: Optional<String>, releaseFlag: Boolean): Es2ConfirmOrderResponse

    @Throws(SmDpPlusException::class)
    public void cancelOrder(eid: Optional<String>, iccid: Optional<String>, matchingId: Optional<String>, finalProfileStatusIndicator: Optional<String>)

    @Throws(SmDpPlusException::class)
    public void getProfileStatus(iccidList: List<String>): Es2ProfileStatusResponse

    @Throws(SmDpPlusException::class)
    public void releaseProfile(iccid: String)
}

public interface SmDpPlusCallbackService {

    @Throws(SmDpPlusException::class)
    public void handleDownloadProgressInfo(
            header: ES2RequestHeader,
            eid: Optional<String>,
            iccid: String,
            profileType: String,
            timestamp: String,
            notificationPointId: Int,
            notificationPointStatus: ES2NotificationPointStatus,
            resultData: Optional<String>,
            imei: Optional<String>
    )
}
