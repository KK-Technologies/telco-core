// Converted from Kotlin: AppleIdAuthenticator.kt
package org.ostelco.prime.auth

import com.fasterxml.jackson.databind.JsonNode
import org.ostelco.prime.model.Identity
import java.util.*

package org.ostelco.prime.auth

import com.fasterxml.jackson.databind.JsonNode
import org.ostelco.prime.model.Identity
import java.util.*

public class AppleIdAuthenticator(private final var claims: JsonNode) {

    public void authenticate(): Optional<AccessTokenPrincipal> {
        final var apple = claims.path("apple")
        final var identity: Optional<String> = apple.get("identity").textValue()
        final var provider: Optional<String> = apple.get("provider").textValue()
        final var type: Optional<String> = apple.get("type").textValue()
        return if (identity != null && type != null && provider != null) {
            Optional.of(AccessTokenPrincipal(Identity(id = identity, type = type, provider = provider)))
        } else {
            Optional.empty()
        }
    }
}
