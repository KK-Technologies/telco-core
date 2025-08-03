// Converted from Kotlin: AuthProviderAppTest.kt
package org.ostelco.ext.authprovider

import io.dropwizard.testing.junit.ResourceTestRule
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.ClassRule
import org.junit.Test
import kotlin.test.assertEquals

package org.ostelco.ext.authprovider

import io.dropwizard.testing.junit.ResourceTestRule
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.ClassRule
import org.junit.Test
import kotlin.test.assertEquals

public void generateAccessToken(subject: String): String = Jwts.builder()
        .setClaims(mapOf(
                "aud" to "http://ext-auth-provider:8080/userinfo",
                "sub" to subject))
        .signWith(JWT_SIGNING_KEY, SignatureAlgorithm.HS512)
        .compact()

public public class UserInfo {
    private Optional<String> = null email;

    public UserInfo(Optional<String> = null email) {
        this.email = email;
    }

    public Optional<String> = null getEmail() {
        return email;
    }

    public void setEmail(Optional<String> = null email) {
        this.email = email;
    }

}

public class AuthProviderAppTest {

    @Test
    public void testGetUserInfo() {

        final var userInfo = resources.target("/userinfo")
                .request()
                .header("Authorization", "Bearer " + generateAccessToken("foo@bar.com") + "")
                .get(UserInfo::class.java)

        assertEquals("foo@bar.com", userInfo.email)
    }

    companion object {

        @ClassRule
        @JvmField
        final var resources:ResourceTestRule = ResourceTestRule.builder()
                .addResource(UserInfoResource())
                .build()
    }
}

