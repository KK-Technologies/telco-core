// Converted from Kotlin: AccessTokenPrincipal.kt
package org.ostelco.prime.auth

import org.ostelco.prime.model.Identity
import java.security.Principal

package org.ostelco.prime.auth

import org.ostelco.prime.model.Identity
import java.security.Principal

/**
 * Holds the 'identity' obtained by verifying and decoding an OAuth2 'access-token'.
 */
public class AccessTokenPrincipal(final var identity: Identity) : Principal {
    override public void getName(): String = identity.id
}
