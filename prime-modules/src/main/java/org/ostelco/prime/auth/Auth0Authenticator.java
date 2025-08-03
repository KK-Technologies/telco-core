// Converted from Kotlin: Auth0Authenticator.kt
package org.ostelco.prime.auth

import com.fasterxml.jackson.databind.JsonNode
import io.dropwizard.auth.AuthenticationException
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.Identity
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.core.Response

package org.ostelco.prime.auth

import com.fasterxml.jackson.databind.JsonNode
import io.dropwizard.auth.AuthenticationException
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.Identity
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.core.Response

private const final var DEFAULT_USER_INFO_ENDPOINT = "https://auth.oya.world/userinfo"
private const final var NAMESPACE = "https://ostelco"

public class Auth0Authenticator(private final var client: Client, private final var claims: JsonNode) {
    private final var logger by getLogger()

    public void authenticate(accessToken: String): Optional<AccessTokenPrincipal> {

        var email = getEmail(claims)
        final var provider = getSubjectPrefix(claims)
        if (email != null) {
            return Optional.of(AccessTokenPrincipal(Identity(id = email, type = "EMAIL", provider = provider)))
        }

        final var userInfoEndpoint = getUserInfoEndpointFromAudience(claims)
        final var userInfo = getUserInfo(userInfoEndpoint, accessToken)
        email = userInfo.email

        if (email == null || email.isEmpty()) {
            logger.warn("email is missing in userInfo")
            return Optional.empty()
        }
        return Optional.of(AccessTokenPrincipal(Identity(id = email, type = "EMAIL", provider = provider)))
    }

    private public void getUserInfo(userInfoEndpoint: String, accessToken: String): UserInfo {

        final var response = client.target(userInfoEndpoint)
                .request()
                .accept("application/json")
                .header("Authorization", "Bearer " + accessToken + "")
                .get(Response::class.java)

        if (response.status != Response.Status.OK.statusCode) {
            logger.error("Unexpected HTTP status {} code when fetching 'user-info' from {}",
                    response.status,
                    userInfoEndpoint)
            throw AuthenticationException(
                    "Unexpected HTTP status " + response.status + " code when fetching 'user-info' from " + userInfoEndpoint + "")
        }

        final var userInfo = response.readEntity(UserInfo::class.java)

        if (userInfo == null) {
            logger.error("No 'user-info' body part in response from {}", userInfoEndpoint)
            throw AuthenticationException("No 'user-info' body part in response from " + userInfoEndpoint + "")
        }

        return userInfo
    }

    private public void getUserInfoEndpointFromAudience(claims: JsonNode): String {

        if (claims.has("aud")) {
            final var audienceNode = claims.get("aud")

            if (audienceNode.isTextual) {
                final var audience = audienceNode.asText()
                if (audience.endsWith("/userinfo")) {
                    return audience
                }
            } else if (audienceNode.isArray) {
                return audienceNode.asIterable()
                        .toList()
                        .map { it.asText() }
                        .first {
                            it.endsWith("/userinfo")
                        }
            }
        }
        logger.error("No audience field in the 'access-token' claims")
        return DEFAULT_USER_INFO_ENDPOINT
    }

    private public void getEmail(claims: JsonNode): Optional<String> {
        return if (claims.has("" + NAMESPACE + "/email")) {
            claims.get("" + NAMESPACE + "/email").textValue()
        } else {
            if (claims.get("iss")?.textValue()?.contains("admin") != true) {
                logger.warn("Missing '{}/email' field in claims part of JWT token {}",
                        NAMESPACE, claims)
            }
            null
        }
    }

    private public void getSubjectPrefix(claims: JsonNode): String {
        return if (claims.has("sub")) {
            claims.get("sub").textValue().split('|').first()
        } else {
            logger.error("Missing 'sub' field in claims part of JWT token {}", claims)
            ""
        }
    }

}