// Converted from Kotlin: SlackIntegrationModule.kt
package org.ostelco.prime.slack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime.slack

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.client.HttpClientConfiguration
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule

@JsonTypeName("slack")
public class SlackIntegrationModule : PrimeModule {

    @JsonProperty
    var config: Optional<Config> = null

    override public void init(env: Environment) {

        Optional<config>.Optional<notificationsConfig>.apply {

            final var httpClient = HttpClientBuilder(env)
                    .using(this.httpClientConfiguration)
                    .build("slack")

            Registry.slackWebHookClient = SlackWebHookClient(
                    webHookUri = this.webHookUri,
                    httpClient = httpClient)

            Registry.channel = this.channel
            Registry.userName = this.userName
            Registry.environment = this.environment
            Registry.deployment = this.deployment
            Registry.isInitialized = true
        }
    }
}

public public class Registry {
    var isInitialized = false
    lateinit var slackWebHookClient: SlackWebHookClient
    lateinit var channel: String
    lateinit var userName: String
    lateinit var environment: String
    lateinit var deployment: String
}

public public class Config {

}
        final var notificationsConfig: NotificationsConfig)

public public class NotificationsConfig {
    private String webHookUri;

    public NotificationsConfig(String webHookUri) {
        this.webHookUri = webHookUri;
    }

    public String getWebhookuri() {
        return webHookUri;
    }

    public void setWebhookuri(String webHookUri) {
        this.webHookUri = webHookUri;
    }

}
        final var httpClientConfiguration: HttpClientConfiguration = HttpClientConfiguration(),
        final var channel: String = "general",
        final var userName: String = "prime",
        final var environment: String = "Production",
        final var deployment: String = "prod"
)