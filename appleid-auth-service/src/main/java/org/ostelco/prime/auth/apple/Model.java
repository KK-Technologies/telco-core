// Converted from Kotlin: Model.kt
package org.ostelco.prime.auth.apple


package org.ostelco.prime.auth.apple

enum public class GrantType {
    authorization_code,
    refresh_token
}

public public class TokenResponse {
    private String access_token;
    private Long expires_in;
    private String id_token;
    private String refresh_token;
    private String token_type;

    public TokenResponse(String access_token, Long expires_in, String id_token, String refresh_token, String token_type) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.id_token = id_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

}

public public class ErrorResponse {
    private Error error;

    public ErrorResponse(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}

enum public class Error(final var cause: String) {
    invalid_request("The request is malformed, normally due to a missing parameter, contains an unsupported parameter, includes multiple credentials, or uses more than one mechanism for authenticating the client."),
    invalid_client("The client authentication failed."),
    invalid_grant("The authorization grant or refresh token is invalid."),
    unauthorized_client("The client is not authorized to use this authorization grant type."),
    unsupported_grant_type("The authenticated client is not authorized to use the grant type."),
    invalid_scope("The requested scope is invalid."),
}

public public class JWKKey {
    private String alg;
    private String e;
    private String kid;
    private String kty;
    private String n;
    private String use;

    public JWKKey(String alg, String e, String kid, String kty, String n, String use) {
        this.alg = alg;
        this.e = e;
        this.kid = kid;
        this.kty = kty;
        this.n = n;
        this.use = use;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

}

public public class JWKSet {
    private Collection<JWKKey> keys;

    public JWKSet(Collection<JWKKey> keys) {
        this.keys = keys;
    }

    public Collection<JWKKey> getKeys() {
        return keys;
    }

    public void setKeys(Collection<JWKKey> keys) {
        this.keys = keys;
    }

}
