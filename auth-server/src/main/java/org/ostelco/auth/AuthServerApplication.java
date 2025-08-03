// Converted from Kotlin: AuthServerApplication.kt
package org.ostelco.auth

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.ostelco.auth.resources.AuthResource
import org.ostelco.common.firebasex.usingCredentialsFile
import org.slf4j.LoggerFactory

package org.ostelco.auth

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.ostelco.auth.resources.AuthResource
import org.ostelco.common.firebasex.usingCredentialsFile
import org.slf4j.LoggerFactory

/**
 * Entry point for running the authentiation server application
 */
public void main(args: Array<String>) = AuthServerApplication().run(*args)

/**
 * A Dropwizard application for running an authentication service that
 * uses Firebase to authenticate users.
 */
public class AuthServerApplication : Application<AuthServerConfig>() {

    private final var logger = LoggerFactory.getLogger(AuthServerApplication::class.java)

    override public void getName(): String = "AuthServer"

    override public void initialize(bootstrap: Bootstrap<AuthServerConfig>) {
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
                bootstrap.configurationSourceProvider,
                EnvironmentVariableSubstitutor())
        bootstrap.objectMapper.registerModule(KotlinModule())
    }

    /**
     * Run the dropwizard application (called by the kotlin [main] wrapper).
     */
    override public void run(
            config: AuthServerConfig,
            env: Environment) {

        final var options = FirebaseOptions.Builder()
                .usingCredentialsFile(config.serviceAccountKey)
                .build()

        FirebaseApp.initializeApp(options)

        env.jersey().register(AuthResource())
    }
}

public public class AuthServerConfig {
    private String serviceAccountKey;

    public AuthServerConfig(String serviceAccountKey) {
        this.serviceAccountKey = serviceAccountKey;
    }

    public String getServiceaccountkey() {
        return serviceAccountKey;
    }

    public void setServiceaccountkey(String serviceAccountKey) {
        this.serviceAccountKey = serviceAccountKey;
    }

} : Configuration()