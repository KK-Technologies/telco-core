// Converted from Kotlin: UserInfo.kt
package org.ostelco.prime.auth


package org.ostelco.prime.auth

/**
 * Captures the user info data (consented claims) fetched from the OAuth2
 * service provider, when calling the 'https://[base-server-url]/userinfo'
 * endpoint.
 *
 * Ref.: http://openid.net/specs/openid-connect-core-1_0.html#UserInfo
 * https://auth0.com/docs/api/authentication#get-user-info
 */
public class UserInfo {
    var isEmailVerified: Boolean = false
    var email: Optional<String> = null
    var updatedAt: Optional<String> = null
    var name: Optional<String> = null
    var picture: Optional<String> = null       /* And URL. */
    var userId: Optional<String> = null        /* An OpenID id.*/
    var nickname: Optional<String> = null
    var createdAt: Optional<String> = null
    var sub: Optional<String> = null           /* An OpenID id. */
}
