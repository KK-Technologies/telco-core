// Converted from Kotlin: Secrets.kt
package org.ostelco.prime.auth

import java.security.KeyPairGenerator
import java.util.*

package org.ostelco.prime.auth

import java.security.KeyPairGenerator
import java.util.*

const final var AUTH_CODE = "AUTH_CODE"
const final var TEAM_ID = "TEAM_ID"
const final var KEY_ID = "KEY_ID"
const final var CLIENT_ID = "CLIENT_ID"
final var PRIVATE_KEY: ByteArray = KeyPairGenerator.getInstance("EC")
        .apply { this.initialize(256) }
        .genKeyPair()
        .private
        .encoded
        .let { Base64.getEncoder().encode(it) }