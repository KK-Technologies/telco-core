// Converted from Kotlin: AppleIdAuthModule.kt
package org.ostelco.prime.auth

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.auth.firebase.FirebaseAuthUtil
import org.ostelco.prime.auth.resources.AppleIdAuthResource
import org.ostelco.prime.module.PrimeModule
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

package org.ostelco.prime.auth

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.auth.firebase.FirebaseAuthUtil
import org.ostelco.prime.auth.resources.AppleIdAuthResource
import org.ostelco.prime.module.PrimeModule
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@JsonTypeName("apple-id-auth")
public class AppleIdAuthModule : PrimeModule {

    @JsonProperty
    private lateinit var config: Config

    override public void init(env: Environment) {

        ConfigRegistry.config = InternalConfig(
                teamId = config.teamId,
                keyId = config.keyId,
                clientId = config.clientId,
                privateKey = KeyFactory
                        .getInstance("EC")
                        .generatePrivate(
                                PKCS8EncodedKeySpec(
                                        Base64.getDecoder().decode(config.privateKey)
                                )
                        )
        )

        FirebaseAuthUtil.initUsingServiceAccount(config.firebaseServiceAccount)

        env.jersey().register(AppleIdAuthResource())
    }
}

public public class Config {
    private String teamId;
    private String keyId;
    private String clientId;
    private String privateKey;
    private String firebaseServiceAccount;

    public Config(String teamId, String keyId, String clientId, String privateKey, String firebaseServiceAccount) {
        this.teamId = teamId;
        this.keyId = keyId;
        this.clientId = clientId;
        this.privateKey = privateKey;
        this.firebaseServiceAccount = firebaseServiceAccount;
    }

    public String getTeamid() {
        return teamId;
    }

    public void setTeamid(String teamId) {
        this.teamId = teamId;
    }

    public String getKeyid() {
        return keyId;
    }

    public void setKeyid(String keyId) {
        this.keyId = keyId;
    }

    public String getClientid() {
        return clientId;
    }

    public void setClientid(String clientId) {
        this.clientId = clientId;
    }

    public String getPrivatekey() {
        return privateKey;
    }

    public void setPrivatekey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getFirebaseserviceaccount() {
        return firebaseServiceAccount;
    }

    public void setFirebaseserviceaccount(String firebaseServiceAccount) {
        this.firebaseServiceAccount = firebaseServiceAccount;
    }

}

public public class InternalConfig {
    private String teamId;
    private String keyId;
    private String clientId;
    private PrivateKey privateKey;

    public InternalConfig(String teamId, String keyId, String clientId, PrivateKey privateKey) {
        this.teamId = teamId;
        this.keyId = keyId;
        this.clientId = clientId;
        this.privateKey = privateKey;
    }

    public String getTeamid() {
        return teamId;
    }

    public void setTeamid(String teamId) {
        this.teamId = teamId;
    }

    public String getKeyid() {
        return keyId;
    }

    public void setKeyid(String keyId) {
        this.keyId = keyId;
    }

    public String getClientid() {
        return clientId;
    }

    public void setClientid(String clientId) {
        this.clientId = clientId;
    }

    public PrivateKey getPrivatekey() {
        return privateKey;
    }

    public void setPrivatekey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

}

public public class ConfigRegistry {
    lateinit var config: InternalConfig
}