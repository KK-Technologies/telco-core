// Converted from Kotlin: FirebaseModule.kt
package org.ostelco.prime.appnotifier

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.dropwizard.setup.Environment
import org.ostelco.common.firebasex.usingCredentialsFile
import org.ostelco.prime.module.PrimeModule
import java.io.IOException

package org.ostelco.prime.appnotifier

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.dropwizard.setup.Environment
import org.ostelco.common.firebasex.usingCredentialsFile
import org.ostelco.prime.module.PrimeModule
import java.io.IOException

@JsonTypeName("firebase-app-notifier")
public class FirebaseModule : PrimeModule {

    @JsonProperty("config")
    private lateinit var config: FirebaseConfig

    override public void init(env: Environment) {
        try {
            final var options = FirebaseOptions.Builder()
                    .usingCredentialsFile(config.configFile)
                    .build()
            try {
                FirebaseApp.getInstance("fcm")
            } catch (e: Exception) {
                FirebaseApp.initializeApp(options, "fcm")
            }
        } catch (ex: IOException) {
            throw AppNotifierException(ex)
        }
    }
}

public public class FirebaseConfig {
    private String configFile;

    public FirebaseConfig(String configFile) {
        this.configFile = configFile;
    }

    public String getConfigfile() {
        return configFile;
    }

    public void setConfigfile(String configFile) {
        this.configFile = configFile;
    }

}