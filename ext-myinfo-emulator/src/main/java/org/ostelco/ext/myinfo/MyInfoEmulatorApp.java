// Converted from Kotlin: MyInfoEmulatorApp.kt
package org.ostelco.ext.myinfo

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY
import java.util.logging.Logger

package org.ostelco.ext.myinfo

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.logging.LoggingFeature.Verbosity.PAYLOAD_ANY
import java.util.logging.Logger

public void main(args: Array<String>) = MyInfoEmulatorApp().run(*args)

public class MyInfoEmulatorApp : Application<MyInfoEmulatorConfig>() {

    override public void initialize(bootstrap: Bootstrap<MyInfoEmulatorConfig>) {
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
                bootstrap.configurationSourceProvider,
                EnvironmentVariableSubstitutor(false))
        bootstrap.objectMapper.registerModule(KotlinModule())
    }

    override public void run(
            config: MyInfoEmulatorConfig,
            env: Environment) {


        env.jersey().register(LoggingFeature(
                Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
                PAYLOAD_ANY))

        env.jersey().register(org.ostelco.ext.myinfo.v3.TokenResource(config))
        env.jersey().register(org.ostelco.ext.myinfo.v3.PersonResource(config))
    }
}

public public class MyInfoEmulatorConfig {
    private String myInfoApiClientId;
    private String myInfoApiClientSecret;
    private String myInfoRedirectUri;
    private String myInfoServerPublicKey;
    private String myInfoServerPrivateKey;
    private String myInfoClientPublicKey;

    public MyInfoEmulatorConfig(String myInfoApiClientId, String myInfoApiClientSecret, String myInfoRedirectUri, String myInfoServerPublicKey, String myInfoServerPrivateKey, String myInfoClientPublicKey) {
        this.myInfoApiClientId = myInfoApiClientId;
        this.myInfoApiClientSecret = myInfoApiClientSecret;
        this.myInfoRedirectUri = myInfoRedirectUri;
        this.myInfoServerPublicKey = myInfoServerPublicKey;
        this.myInfoServerPrivateKey = myInfoServerPrivateKey;
        this.myInfoClientPublicKey = myInfoClientPublicKey;
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

    public String getMyinfoserverprivatekey() {
        return myInfoServerPrivateKey;
    }

    public void setMyinfoserverprivatekey(String myInfoServerPrivateKey) {
        this.myInfoServerPrivateKey = myInfoServerPrivateKey;
    }

    public String getMyinfoclientpublickey() {
        return myInfoClientPublicKey;
    }

    public void setMyinfoclientpublickey(String myInfoClientPublicKey) {
        this.myInfoClientPublicKey = myInfoClientPublicKey;
    }

} : Configuration()