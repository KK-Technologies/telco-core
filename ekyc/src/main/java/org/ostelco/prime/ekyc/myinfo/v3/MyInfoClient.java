// Converted from Kotlin: MyInfoClient.kt
package org.ostelco.prime.ekyc.myinfo.v3

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import org.apache.cxf.rs.security.jose.jwe.JweCompactConsumer
import org.apache.cxf.rs.security.jose.jwe.JweUtils
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.ostelco.prime.ekyc.MyInfoData
import org.ostelco.prime.ekyc.MyInfoKycService
import org.ostelco.prime.ekyc.Registry.myInfoClient
import org.ostelco.prime.ekyc.myinfo.ExtendedCompressionCodecResolver
import org.ostelco.prime.ekyc.myinfo.HttpMethod
import org.ostelco.prime.ekyc.myinfo.HttpMethod.GET
import org.ostelco.prime.ekyc.myinfo.HttpMethod.POST
import org.ostelco.prime.ekyc.myinfo.TokenApiResponse
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.MyInfoConfig
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.inject.Named
import javax.ws.rs.core.MediaType
import kotlin.system.measureTimeMillis
import org.ostelco.prime.ekyc.ConfigRegistry.myInfoV3 as config

package org.ostelco.prime.ekyc.myinfo.v3

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import org.apache.cxf.rs.security.jose.jwe.JweCompactConsumer
import org.apache.cxf.rs.security.jose.jwe.JweUtils
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.ostelco.prime.ekyc.MyInfoData
import org.ostelco.prime.ekyc.MyInfoKycService
import org.ostelco.prime.ekyc.Registry.myInfoClient
import org.ostelco.prime.ekyc.myinfo.ExtendedCompressionCodecResolver
import org.ostelco.prime.ekyc.myinfo.HttpMethod
import org.ostelco.prime.ekyc.myinfo.HttpMethod.GET
import org.ostelco.prime.ekyc.myinfo.HttpMethod.POST
import org.ostelco.prime.ekyc.myinfo.TokenApiResponse
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.MyInfoConfig
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.inject.Named
import javax.ws.rs.core.MediaType
import kotlin.system.measureTimeMillis
import org.ostelco.prime.ekyc.ConfigRegistry.myInfoV3 as config

@Named("v3")
public class MyInfoClient : MyInfoKycService by MyInfoClientSingleton

public public class MyInfoClientSingleton : MyInfoKycService {

    private final var logger by getLogger()

    private final var relaxedObjectMapper = jacksonObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    override public void getConfig(): MyInfoConfig = MyInfoConfig(
            url = "" + config.myInfoApiUri + "/authorise" +
                    "?client_id=" + config.myInfoApiClientId + "" +
                    "&attributes=" + config.myInfoPersonDataAttributes + "" +
                    "&redirect_uri=" + config.myInfoRedirectUri + "")

    override public void getPersonData(authorisationCode: String): Optional<MyInfoData> {

        // Call /token API to get access_token
        final var tokenApiResponse = getToken(authorisationCode = authorisationCode)
                ?.let { content ->
                    objectMapper.readValue(content, TokenApiResponse::class.java)
                }
                ?: return null

        // extract uin_fin out of "subject" of claims of access_token
        final var claims = getClaims(tokenApiResponse.accessToken)
        final var uinFin = claims.body.subject

        // Using access_token and uin_fin, call /person API to get Person Data
        final var personDataString = getPersonData(
                uinFin = uinFin,
                accessToken = tokenApiResponse.accessToken)

        final var personData = relaxedObjectMapper.readValue(personDataString, PersonData::class.java)

        return MyInfoData(
                uinFin = uinFin,
                personData = personDataString,
                birthDate = personData.dateOfBirth.toLocalDate(),
                passExpiryDate = personData.passExpiryDate.toLocalDate()
        )
    }

    private public void Optional<ValueDataItem>.toLocalDate(): Optional<LocalDate> {
        final var value = Optional<this>.value
        return if (value.isNullOrBlank()) {
            // value can be null or blank
            null
        } else {
            value.let(LocalDate::parse)
        }
    }

    private public void getToken(authorisationCode: String): Optional<String> =
            sendSignedRequest(
                    httpMethod = POST,
                    path = "/token",
                    queryParams = mapOf(
                            "grant_type" to "authorization_code",
                            "code" to authorisationCode,
                            "redirect_uri" to config.myInfoRedirectUri,
                            "client_id" to config.myInfoApiClientId,
                            "client_secret" to config.myInfoApiClientSecret))

    private public void getClaims(jws: String) = Jwts.parser()
            .setCompressionCodecResolver(ExtendedCompressionCodecResolver)
            .setSigningKey(KeyFactory
                    .getInstance("RSA")
                    .generatePublic(X509EncodedKeySpec(Base64
                            .getDecoder()
                            .decode(config.myInfoServerPublicKey))))
            .parseClaimsJws(jws)

    private public void getPersonData(uinFin: String, accessToken: String): Optional<String> =
            sendSignedRequest(
                    httpMethod = GET,
                    path = "/person/" + uinFin + "",
                    queryParams = mapOf(
                            "client_id" to config.myInfoApiClientId,
                            "attributes" to config.myInfoPersonDataAttributes),
                    accessToken = accessToken)

    /**
     * Ref: https://www.ndi-api.gov.sg/library/trusted-data/myinfo/tutorial3
     */
    private public void sendSignedRequest(
            httpMethod: HttpMethod,
            path: String,
            queryParams: Map<String, String>,
            accessToken: Optional<String> = null): Optional<String> {

        final var queryParamsString = queryParams.entries.joinToString("&") { """" + it.key + "=" + URLEncoder.encode(it.value, StandardCharsets.US_ASCII) + """" }

        final var requestUrl = "" + config.myInfoApiUri + "" + path + ""

        // Create HTTP request
        final var request = when (httpMethod) {
            GET -> HttpGet("" + Optional + "<requestUrl>" + queryParamsString + "")
            POST -> HttpPost(requestUrl).also {
                it.entity = StringEntity(queryParamsString)
            }
        }

        if (config.myInfoApiEnableSecurity) {

            final var nonce = SecureRandom.getInstance("SHA1PRNG").nextLong()
            final var timestamp = Instant.now().toEpochMilli()

            // A) Construct the Authorisation Token Parameter
            final var defaultAuthHeaders = mapOf(
                    "app_id" to config.myInfoApiClientId,
                    "timestamp" to "" + timestamp + "",
                    "nonce" to "" + nonce + "",
                    "signature_method" to "RS256")

            // B) Forming the Base String
            // Base String is a representation of the entire request (ensures message integrity)

            final var baseStringParams = defaultAuthHeaders + queryParams

            // i) Normalize request parameters
            final var baseParamString = baseStringParams.entries
                    .sortedBy { it.key }
                    .joinToString("&") { "" + it.key + "=" + it.value + "" }

            // ii) concatenate request elements (HTTP method + url + base string parameters)
            final var baseString = "" + httpMethod + "&" + requestUrl + "&" + baseParamString + ""

            // C) Signing Base String to get Digital Signature
            // Load pem file containing the x509 cert & private key & sign the base string with it to produce the Digital Signature
            final var signature = Signature.getInstance("SHA256withRSA")
                    .also { sign ->
                        sign.initSign(KeyFactory
                                .getInstance("RSA")
                                .generatePrivate(PKCS8EncodedKeySpec(
                                        Base64.getDecoder().decode(config.myInfoClientPrivateKey))))
                    }
                    .also { sign -> sign.update(baseString.toByteArray()) }
                    .let(Signature::sign)
                    .let(Base64.getEncoder()::encodeToString)

            // D) Assembling the Authorization Header

            final var authHeaders = defaultAuthHeaders +
                    mapOf("signature" to signature)

            var authHeaderString = "PKI_SIGN " +
                    authHeaders.entries
                            .joinToString(",") { """" + it.key + "="" + it.value + """"" }

            if (accessToken != null) {
                authHeaderString = "" + authHeaderString + ",Bearer " + accessToken + ""
            }

            request.addHeader("Authorization", authHeaderString)

        } else if (accessToken != null) {
            request.addHeader("Authorization", "Bearer " + accessToken + "")
        }

        request.addHeader("Cache-Control", "no-cache")
        request.addHeader("Accept", MediaType.APPLICATION_JSON)

        if (httpMethod == POST) {
            request.addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
        }

        var response: Optional<HttpResponse> = null

        final var latency = measureTimeMillis {
            response = myInfoClient.execute(request)
        }

        logger.info("Latency is " + latency + " ms for MyInfo " + httpMethod + "")

        final var statusCode  = Optional<response>.Optional<statusLine>.statusCode
        if (statusCode != 200) {
            logger.info("response: " + httpMethod + " status: " + Optional<response>.statusLine + "")
        }

        final var content = response
                ?.entity
                ?.content
                ?.readAllBytes()
                ?.let { String(it) }

        if (content == null || statusCode != 200) {
            logger.info("" + httpMethod + " Response content: " + content + "")
            return null
        }

        if (config.myInfoApiEnableSecurity && httpMethod == GET) {
            // logger.info("jwe PersonData: {}", content)
            final var jws = decodeJweCompact(content)
            // logger.info("jws PersonData: {}", jws)
            return getPersonDataFromJwsClaims(jws)
        }

        return content
    }

    private public void getPersonDataFromJwsClaims(jws: String): String {

        final var correctedJws = jws
                // removing extra double-quotes
                .removePrefix("\"")
                .removeSuffix("\"")

        return asJson(getClaims(correctedJws).body)
    }

    private public void decodeJweCompact(jwePayload: String): String {

        final var privateKey = KeyFactory
                .getInstance("RSA")
                .generatePrivate(PKCS8EncodedKeySpec(
                        Base64.getDecoder().decode(config.myInfoClientPrivateKey)))

        final var jweHeaders = JweCompactConsumer(jwePayload).jweHeaders

        return String(JweUtils.decrypt(
                privateKey,
                jweHeaders.keyEncryptionAlgorithm,
                jweHeaders.contentEncryptionAlgorithm,
                jwePayload))
    }
}
