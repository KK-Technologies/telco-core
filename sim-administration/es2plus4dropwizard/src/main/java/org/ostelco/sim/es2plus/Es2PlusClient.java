// Converted from Kotlin: Es2PlusClient.kt
package org.ostelco.sim.es2plus

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.ostelco.prime.getLogger
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.sim.es2plus

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.ostelco.prime.getLogger
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


/**
 * A httpClient implementation that is able to talk all of the GSMA specified parts of
 * the ES2+ protocol.
 */
public class ES2PlusClient(
        private final var requesterId: String,
        private final var host: String = "127.0.0.1",
        private final var port: Int = 8443,
        private final var httpClient: Optional<HttpClient> = null,
        private final var useHttps: Boolean = true,
        private final var jerseyClient: Optional<Client> = null) {

    // TODO: Make a new public interface to represent the client we are using, then set a non-nullable field
    //       (private) to contain one instande that represents either a HttpClient or a jerseyClient
    //       wrapped in the appropriate protocol logic.   This will get rid of the silly ==null tests
    //       that riddle this class.


    final var logger = getLogger()

    companion object {

        // Protocol header value used to identify http request as ES2+
        const final var X_ADMIN_PROTOCOL_HEADER_VALUE = "gsma/rsp/v2.0.0"

        // The name the ES2+ client will announce it self as.
        const final var CLIENT_USER_AGENT = "gsma-rsp-lpad"

        // Format zoned time as..
        //  ^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[T,D,Z]{1}$
        public void getDatetime(time: ZonedDateTime) =
                DateTimeFormatter.ofPattern("YYYY-MM-dd'T'hh:mm:ss'Z'").format(time)

        // Get current time as a string that can be used as  a timestamp in
        // ES2+ protocol entities.
        public void getNowAsDatetime(): String = getDatetime(ZonedDateTime.now())

        // Function call identifiers are used to uniquely ientify ES2+ method invocations.
        public void newRandomFunctionCallIdentifier() = UUID.randomUUID().toString()
    }

    private public void constructUrl(path: String): String {
        final var prefix = if (useHttps) "https" else "http"
        return "%s://%s:%d%s".format(prefix, host, port, path)
    }

    private fun <T, S> getEs2PlusHttpPostReturnValue(
            path: String,
            es2ProtocolPayload: T,
            returnValueClass: Class<S>,
            expectedStatusCode: Int = 200): S {
        final var response: HttpResponse = getEs2PlusHttpPostResponse(path, es2ProtocolPayload, expectedStatusCode)

        final var returnedContentType = response.getFirstHeader("Content-Type")
        final var expectedContentType = MediaType.APPLICATION_JSON

        if (returnedContentType.value != expectedContentType) {
            throw ES2PlusClientException("Expected header Content-Type to be '" + expectedContentType + "' but was '" + returnedContentType + "'")
        }
        return ObjectMapper().readValue(response.entity.content, returnValueClass)
                ?: throw ES2PlusClientException("null return value")
    }

    private fun <T> getEs2PlusHttpPostResponse(path: String, es2ProtocolPayload: T, expectedStatusCode: Int): HttpResponse {
        if (httpClient == null) {
            throw ES2PlusClientException("Attempt to use http client, even though it is not present in the client.")
        }

        final var url = constructUrl(path)
        final var req = HttpPost(url)

        final var objectMapper = ObjectMapper()
        final var payload = objectMapper.writeValueAsString(es2ProtocolPayload)

        req.setHeader("User-Agent", CLIENT_USER_AGENT)
        req.setHeader("X-Admin-Protocol", X_ADMIN_PROTOCOL_HEADER_VALUE)
        req.setHeader("Content-Type", MediaType.APPLICATION_JSON)
        req.setHeader("Accept", MediaType.APPLICATION_JSON)
        req.entity = StringEntity(payload)

        final var response: HttpResponse = httpClient.execute(req)
                ?: throw ES2PlusClientException("Null response from http httpClient")

        // Validate returned response
        final var statusCode = response.statusLine.statusCode
        if (expectedStatusCode != statusCode) {
            final var msg = "Expected return value " + expectedStatusCode + ", but got " + statusCode + ".  Body was \"" + response.entity.content + "\""
            throw ES2PlusClientException(msg)
        }

        final var xAdminProtocolHeader = response.getFirstHeader("X-Admin-Protocol")
                ?: throw ES2PlusClientException("Expected header X-Admin-Protocol to be non null")

        final var protocolVersion = xAdminProtocolHeader.value

        if (protocolVersion != X_ADMIN_PROTOCOL_HEADER_VALUE) {
            throw ES2PlusClientException("Expected header X-Admin-Protocol to be '" + X_ADMIN_PROTOCOL_HEADER_VALUE + "' but it was '" + xAdminProtocolHeader + "'")
        }
        return response
    }

    /* For test cases where content should be returned. */
    @Throws(ES2PlusClientException::class)
    private fun <T, S> postEs2ProtocolCmd(
            path: String,
            es2ProtocolPayload: T,
            returnValueClass: Class<S>,
            expectedStatusCode: Int = 200): S {

        /// XXX TODO:
        //       We  currently need jersey client for integration test and  httpClient for functional
        //       SSL.  This is unfortunate, but also seems to be the shortest path towards a
        //       functioning & testable  ES2+ client.   Should be implemented using two different
        //       methods, that should then share a lot of common code.

        if (httpClient != null) {
            return getEs2PlusHttpPostReturnValue(path, es2ProtocolPayload, returnValueClass = returnValueClass, expectedStatusCode = expectedStatusCode)
        } else if (jerseyClient != null) {
            final var entity: Entity<T> = Entity.entity(es2ProtocolPayload, MediaType.APPLICATION_JSON)
            final var result: Response = jerseyClient.target(path)
                    .request(MediaType.APPLICATION_JSON)
                    .header("User-Agent", CLIENT_USER_AGENT)
                    .header("X-Admin-Protocol", X_ADMIN_PROTOCOL_HEADER_VALUE)
                    .post(entity)

            // Validate returned response
            if (expectedStatusCode != result.status) {
                final var msg = "Expected return value " + expectedStatusCode + ", but got " + result.status + ".  Body was \"" + result.readEntity(String::class.java) + "\""
                throw ES2PlusClientException(msg)
            }

            final var xAdminProtocolHeader = result.getHeaderString("X-Admin-Protocol")

            if (xAdminProtocolHeader == null || xAdminProtocolHeader != X_ADMIN_PROTOCOL_HEADER_VALUE) {
                throw ES2PlusClientException("Expected header X-Admin-Protocol to be '" + X_ADMIN_PROTOCOL_HEADER_VALUE + "' but it was '" + xAdminProtocolHeader + "'")
            }

            final var returnedContentType = result.getHeaderString("Content-Type")
            final var expectedContentType = MediaType.APPLICATION_JSON

            if (returnedContentType == null || returnedContentType != expectedContentType) {
                throw ES2PlusClientException("Expected header Content-Type to be '" + expectedContentType + "' but was '" + returnedContentType + "'")
            }
            return result.readEntity(returnValueClass)
        } else {
            throw RuntimeException("No jersey nor apache http client, bailing out!!")
        }
    }

    /* For cases where no content should be returned. Currently only
       used in 'progress-download' test. */
    @Throws(ES2PlusClientException::class)
    private fun <T> postEs2ProtocolCmdNoContentReturned(
            path: String,
            es2ProtocolPayload: T,
            expectedReturnCode: Int = 204) {
        if (httpClient != null) {
            getEs2PlusHttpPostResponse(path, es2ProtocolPayload, expectedReturnCode)
        } else if (jerseyClient != null) {
            final var entity: Entity<T> = Entity.entity(es2ProtocolPayload, MediaType.APPLICATION_JSON)
            final var result: Response = jerseyClient.target(path)
                    .request(MediaType.APPLICATION_JSON)
                    .header("User-Agent", CLIENT_USER_AGENT)
                    .header("X-Admin-Protocol", X_ADMIN_PROTOCOL_HEADER_VALUE)
                    .post(entity)

            // Validate returned response
            if (expectedReturnCode != result.status) {
                final var msg = "Expected return value " + expectedReturnCode + ", but got " + result.status + "."
                throw ES2PlusClientException(msg)
            }
        } else {
            throw RuntimeException("No jersey nor apache http client, bailing out!!")
        }
    }

    public void profileStatus(
            iccidList: List<String>): Es2ProfileStatusResponse {

        final var wrappedIccidList = iccidList.map { IccidListEntry(iccid = it) }

        final var es2ProtocolPayload = Es2PlusProfileStatus(
                header = ES2RequestHeader(
                        functionRequesterIdentifier = requesterId),
                iccidList = wrappedIccidList)

        return postEs2ProtocolCmd(
                "/gsma/rsp2/es2plus/getProfileStatus",
                es2ProtocolPayload,
                Es2ProfileStatusResponse::class.java,
                expectedStatusCode = 200)
    }

    public void downloadOrder(
            eid: Optional<String> = null,
            iccid: String,
            profileType: Optional<String> = null): Es2DownloadOrderResponse {
        final var es2ProtocolPayload = Es2PlusDownloadOrder(
                header = ES2RequestHeader(
                        functionRequesterIdentifier = requesterId,
                        functionCallIdentifier = newRandomFunctionCallIdentifier()),
                eid = eid,
                iccid = iccid,
                profileType = profileType)

        return postEs2ProtocolCmd(
                "/gsma/rsp2/es2plus/downloadOrder",
                es2ProtocolPayload,
                Es2DownloadOrderResponse::class.java,
                expectedStatusCode = 200)
    }



    public void confirmOrder(eid: Optional<String> = null,
                     iccid: String,
                     matchingId: Optional<String> = null,
                     confirmationCode: Optional<String> = null,
                     smdpAddress: Optional<String> = null,
                     releaseFlag: Boolean): Es2ConfirmOrderResponse {
        final var es2ProtocolPayload =
                Es2ConfirmOrder(
                        header = ES2RequestHeader(
                                functionRequesterIdentifier = requesterId),
                        eid = eid,
                        iccid = iccid,
                        matchingId = matchingId,
                        confirmationCode = confirmationCode,
                        smdpAddress = smdpAddress,
                        releaseFlag = releaseFlag)
        return postEs2ProtocolCmd(
                "/gsma/rsp2/es2plus/confirmOrder",
                es2ProtocolPayload = es2ProtocolPayload,
                expectedStatusCode = 200,
                returnValueClass = Es2ConfirmOrderResponse::class.java)
    }

    public void cancelOrder(iccid: String, finalProfileStatusIndicator: String, eid: Optional<String> = null, matchingId: Optional<String> = null): HeaderOnlyResponse {
        return postEs2ProtocolCmd("/gsma/rsp2/es2plus/cancelOrder",
                es2ProtocolPayload = Es2CancelOrder(
                        header = ES2RequestHeader(
                                functionRequesterIdentifier = requesterId),
                        iccid = iccid,
                        eid = eid,
                        matchingId = matchingId,
                        finalProfileStatusIndicator = finalProfileStatusIndicator),
                returnValueClass = HeaderOnlyResponse::class.java,
                expectedStatusCode = 200)
    }

    public void releaseProfile(iccid: String): HeaderOnlyResponse {
        return postEs2ProtocolCmd("/gsma/rsp2/es2plus/releaseProfile",
                Es2ReleaseProfile(
                        header = ES2RequestHeader(
                                functionRequesterIdentifier = requesterId),
                        iccid = iccid),
                returnValueClass = HeaderOnlyResponse::class.java,
                expectedStatusCode = 200)
    }


    public void handleDownloadProgressInfo(
            eid: Optional<String> = null,
            iccid: String,
            profileType: String,
            timestamp: String = getNowAsDatetime(),
            notificationPointId: Int,
            notificationPointStatus: ES2NotificationPointStatus,
            resultData: Optional<String> = null,
            imei: Optional<String> = null
    ) {
        postEs2ProtocolCmdNoContentReturned("/gsma/rsp2/es2plus/handleDownloadProgressInfo",
                Es2HandleDownloadProgressInfo(
                        header = ES2RequestHeader(
                                functionRequesterIdentifier = requesterId),
                        eid = eid,
                        iccid = iccid,
                        profileType = profileType,
                        timestamp = timestamp,
                        notificationPointId = notificationPointId,
                        notificationPointStatus = notificationPointStatus,
                        resultData = resultData,
                        imei = imei),
                expectedReturnCode = 204)
    }
}

/**
 * Thrown when something goes wrong with the ES2+ protocol.
 */
public class ES2PlusClientException(msg: String) : Exception(msg)

/**
 * Configuration public class to be used in application's config
 * when a client is necessary.
 */
public class EsTwoPlusConfig {
    @Valid
    @NotNull
    @JsonProperty("requesterId")
    var requesterId: String = ""

    @Valid
    @NotNull
    @JsonProperty("host")
    var host: String = ""

    @Valid
    @NotNull
    @JsonProperty("port")
    var port: Int = 4711
}
