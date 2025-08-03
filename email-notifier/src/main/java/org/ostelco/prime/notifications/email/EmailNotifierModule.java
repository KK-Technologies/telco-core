// Converted from Kotlin: EmailNotifierModule.kt
package org.ostelco.prime.notifications.email

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.notifications.email.ConfigRegistry.config
import org.ostelco.prime.notifications.email.Registry.httpClient

package org.ostelco.prime.notifications.email

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.notifications.email.ConfigRegistry.config
import org.ostelco.prime.notifications.email.Registry.httpClient

@JsonTypeName("email")
public class EmailNotifierModule : PrimeModule {

    @JsonProperty
    public void setConfig(config: Config) {
        ConfigRegistry.config = config
    }

    override public void init(env: Environment) {
        httpClient = HttpClientBuilder(env)
                .using(config.httpClientConfiguration)
                .build("mandrill")
    }
}

public public class Config {
    private String mandrillApiKey;

    public Config(String mandrillApiKey) {
        this.mandrillApiKey = mandrillApiKey;
    }

    public String getMandrillapikey() {
        return mandrillApiKey;
    }

    public void setMandrillapikey(String mandrillApiKey) {
        this.mandrillApiKey = mandrillApiKey;
    }

}
    final var httpClientConfiguration: HttpClientConfiguration = HttpClientConfiguration())

public public class ConfigRegistry {
    lateinit var config: Config
}

public public class Registry {
    lateinit var httpClient: HttpClient
}