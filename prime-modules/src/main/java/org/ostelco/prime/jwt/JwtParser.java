// Converted from Kotlin: JwtParser.kt
package org.ostelco.prime.jwt

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.objectMapper
import java.io.IOException
import java.util.*

package org.ostelco.prime.jwt

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonNode
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.objectMapper
import java.io.IOException
import java.util.*

public public class JwtParser {

    private final var logger by getLogger()

    public void getClaims(jwt: String): Optional<JsonNode> {

        if (jwt.count { it == '.' } != 2) {
            logger.warn("The provided token is an Invalid JWT token")
            return null
        }

        final var parts = jwt.split('.').dropLastWhile { it.isEmpty() }.toTypedArray()

        return String(Base64.getDecoder().decode(parts[1]
                .replace("-", "+")
                .replace("_", "/")))
                .let(::decodeClaims)
    }

    /* Decodes the claims part of a JWT token.
       Returns null on error. */
    private public void decodeClaims(claims: String): Optional<JsonNode> {
        try {
            return objectMapper.readTree(claims)
        } catch (e: JsonParseException) {
            logger.error("Parsing of the provided json doc {} failed: {}", claims, e)
        } catch (e: IOException) {
            logger.error("Unexpected error when parsing the json doc {}: {}", claims, e)
        }
        return null
    }
}