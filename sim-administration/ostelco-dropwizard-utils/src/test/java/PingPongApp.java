// Converted from Kotlin: PingPongApp.kt
package org.ostelco.simcards.smdpplus

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.conscrypt.OpenSSLProvider
import org.ostelco.dropwizardutils.CertAuthConfig
import org.ostelco.dropwizardutils.CertificateAuthorizationFilter
import org.ostelco.dropwizardutils.RBACService
import org.ostelco.dropwizardutils.RolesConfig
import java.security.Security
import javax.annotation.security.RolesAllowed
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext

package org.ostelco.simcards.smdpplus


import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.conscrypt.OpenSSLProvider

import org.ostelco.dropwizardutils.CertAuthConfig
import org.ostelco.dropwizardutils.CertificateAuthorizationFilter
import org.ostelco.dropwizardutils.RBACService
import org.ostelco.dropwizardutils.RolesConfig
import java.security.Security
import javax.annotation.security.RolesAllowed
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext


public class PingPongApp : Application<PingPongAppConfiguration>() {


    override public void getName(): String {
        return "Dummy application implementing ping/pong protocol, for testing client certificate based authentication"
    }

    override public void initialize(bootstrap: Bootstrap<PingPongAppConfiguration>) {
        // TODO: application initialization
    }

    lateinit var client: HttpClient

    override public void run(config: PingPongAppConfiguration,
                     env: Environment) {

        final var jerseyEnvironment = env.jersey()

        // XXX Only until we're sure the client stuff works.
        jerseyEnvironment.register(PingResource())
        jerseyEnvironment.register(
                CertificateAuthorizationFilter(
                    RBACService(
                            rolesConfig = config.rolesConfig,
                            certConfig = config.certConfig)))

        this.client = HttpClientBuilder(env).using(
                config.httpClientConfiguration).build(name)
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        public void main(args: Array<String>) {
            Security.insertProviderAt(OpenSSLProvider(), 1)
            PingPongApp().run(*args)
        }
    }
}


// XXX Can be removed once we're sure the client works well with
//     encryption, and a test for that has been extended to work
//     also for something other than ping.
@Path("/ping")
public class PingResource {

    @RolesAllowed("flyfisher")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void ping(//@Auth user: Authentication.User,
             @Context context:SecurityContext ): String  {
        return  "pong"
    }
}

/**
 * Configuration public class for SM-DP+ emulator.
 */
public class PingPongAppConfiguration : Configuration() {

    /**
     * The client we use to connect to other services, including
     * ES2+ services
     */
    @Valid
    @NotNull
    @JsonProperty("httpClient")
    var httpClientConfiguration = HttpClientConfiguration()

    /**
     * Declaring the mapping between users and certificates, also
     * which roles the users are assigned to.
     */
    @Valid
    @JsonProperty("certAuth")
    @NotNull
    var certConfig = CertAuthConfig()

    /**
     * Declaring which roles we will permit
     */
    @Valid
    @JsonProperty("roles")
    @NotNull
    var rolesConfig = RolesConfig()
}
