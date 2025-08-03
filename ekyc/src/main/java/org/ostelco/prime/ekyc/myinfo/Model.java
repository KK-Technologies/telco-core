// Converted from Kotlin: Model.kt
package org.ostelco.prime.ekyc.myinfo

import com.fasterxml.jackson.annotation.JsonProperty

package org.ostelco.prime.ekyc.myinfo

import com.fasterxml.jackson.annotation.JsonProperty

public public class TokenApiResponse {

}
        final var accessToken: String,

        final var scope: String,

        @JvmField
        @JsonProperty("token_type")
        final var tokenType: String,

        @JvmField
        @JsonProperty("expires_in")
        final var expiresIn: Long)

enum public class HttpMethod {
    GET,
    POST
}