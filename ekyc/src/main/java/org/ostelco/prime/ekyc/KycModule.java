// Converted from Kotlin: KycModule.kt
package org.ostelco.prime.ekyc

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.ostelco.prime.ekyc.Registry.myInfoClient
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime.ekyc

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.ostelco.prime.ekyc.Registry.myInfoClient
import org.ostelco.prime.module.PrimeModule

@JsonTypeName("kyc")
public class KycModule : PrimeModule {

    @JsonProperty
    public void setConfig(config: Config) {
        ConfigRegistry.myInfoV3 = config.myInfoV3
    }

    override public void init(env: Environment) {
        final var connManager = PoolingHttpClientConnectionManager()

        /* Defaults for httpclient:
             max-total = 20
             default-max-per-route = 2
           Sets these to higher values as this is too low. */
        /* TODO: Make this configurable or something - or maybe
                 just follow up on the todo below... */
        connManager.maxTotal = 1024
        connManager.defaultMaxPerRoute = 1024

        // TODO change this to Dropwizard's HttpClientBuilder with appropriate timeout values
        myInfoClient = HttpClientBuilder.create().setConnectionManager(connManager).build()
    }
}

public public class Config {
    private MyInfoV3Config myInfoV3;

    public Config(MyInfoV3Config myInfoV3) {
        this.myInfoV3 = myInfoV3;
    }

    public MyInfoV3Config getMyinfov3() {
        return myInfoV3;
    }

    public void setMyinfov3(MyInfoV3Config myInfoV3) {
        this.myInfoV3 = myInfoV3;
    }

}

public public class MyInfoV3Config {
    private String myInfoApiUri;
    private String myInfoApiClientId;
    private String myInfoApiClientSecret;
    private Boolean = true myInfoApiEnableSecurity;
    private String myInfoRedirectUri;
    private String myInfoServerPublicKey;
    private String myInfoClientPrivateKey;
    private String = "name myInfoPersonDataAttributes;

    public MyInfoV3Config(String myInfoApiUri, String myInfoApiClientId, String myInfoApiClientSecret, Boolean = true myInfoApiEnableSecurity, String myInfoRedirectUri, String myInfoServerPublicKey, String myInfoClientPrivateKey, String = "name myInfoPersonDataAttributes) {
        this.myInfoApiUri = myInfoApiUri;
        this.myInfoApiClientId = myInfoApiClientId;
        this.myInfoApiClientSecret = myInfoApiClientSecret;
        this.myInfoApiEnableSecurity = myInfoApiEnableSecurity;
        this.myInfoRedirectUri = myInfoRedirectUri;
        this.myInfoServerPublicKey = myInfoServerPublicKey;
        this.myInfoClientPrivateKey = myInfoClientPrivateKey;
        this.myInfoPersonDataAttributes = myInfoPersonDataAttributes;
    }

    public String getMyinfoapiuri() {
        return myInfoApiUri;
    }

    public void setMyinfoapiuri(String myInfoApiUri) {
        this.myInfoApiUri = myInfoApiUri;
    }

    public String getMyinfoapiclientid() {
        return myInfoApiClientId;
    }

    public void setMyinfoapiclientid(String myInfoApiClientId) {
        this.myInfoApiClientId = myInfoApiClientId;
    }

    public String getMyinfoapiclientsecret() {
        return myInfoApiClientSecret;
    }

    public void setMyinfoapiclientsecret(String myInfoApiClientSecret) {
        this.myInfoApiClientSecret = myInfoApiClientSecret;
    }

    public Boolean = true getMyinfoapienablesecurity() {
        return myInfoApiEnableSecurity;
    }

    public void setMyinfoapienablesecurity(Boolean = true myInfoApiEnableSecurity) {
        this.myInfoApiEnableSecurity = myInfoApiEnableSecurity;
    }

    public String getMyinforedirecturi() {
        return myInfoRedirectUri;
    }

    public void setMyinforedirecturi(String myInfoRedirectUri) {
        this.myInfoRedirectUri = myInfoRedirectUri;
    }

    public String getMyinfoserverpublickey() {
        return myInfoServerPublicKey;
    }

    public void setMyinfoserverpublickey(String myInfoServerPublicKey) {
        this.myInfoServerPublicKey = myInfoServerPublicKey;
    }

    public String getMyinfoclientprivatekey() {
        return myInfoClientPrivateKey;
    }

    public void setMyinfoclientprivatekey(String myInfoClientPrivateKey) {
        this.myInfoClientPrivateKey = myInfoClientPrivateKey;
    }

    public String = "name getMyinfopersondataattributes() {
        return myInfoPersonDataAttributes;
    }

    public void setMyinfopersondataattributes(String = "name myInfoPersonDataAttributes) {
        this.myInfoPersonDataAttributes = myInfoPersonDataAttributes;
    }

}

public public class ConfigRegistry {
    lateinit var myInfoV3: MyInfoV3Config
}

public public class Registry {
    lateinit var myInfoClient: HttpClient
}