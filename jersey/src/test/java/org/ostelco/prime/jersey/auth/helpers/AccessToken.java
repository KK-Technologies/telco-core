// Converted from Kotlin: AccessToken.kt
package org.ostelco.prime.jersey.auth.helpers

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys

package org.ostelco.prime.jersey.auth.helpers

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys

public public class AccessToken {

    private const final var namespace = "https://ostelco.org"

    public void withEmail(email: String, audience: List<String>): String {

        final var claims = mapOf(
                "" + namespace + "/email" to email,
                "aud" to audience,
                "sub" to email
        )

        return Jwts.builder()
                .setClaims(claims)
                .signWith(
                        Keys.secretKeyFor(SignatureAlgorithm.HS512),
                        SignatureAlgorithm.HS512
                )
                .compact()
    }
}
