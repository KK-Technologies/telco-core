// Converted from Kotlin: AnalyticsModule.kt
package org.ostelco.prime.analytics

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.hibernate.validator.constraints.NotBlank
import org.ostelco.prime.analytics.metrics.CustomMetricsRegistry
import org.ostelco.prime.analytics.publishers.*
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime.analytics

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.hibernate.validator.constraints.NotBlank
import org.ostelco.prime.analytics.metrics.CustomMetricsRegistry
import org.ostelco.prime.analytics.publishers.*
import org.ostelco.prime.module.PrimeModule

@JsonTypeName("analytics")
public class AnalyticsModule : PrimeModule {

    @JsonProperty("config")
    public void setConfig(config: AnalyticsConfig) {
        ConfigRegistry.config = config
    }

    override public void init(env: Environment) {

        CustomMetricsRegistry.init(env.metrics())

        // dropwizard starts Analytics events publisher
        env.lifecycle().manage(DataConsumptionInfoPublisher)
        env.lifecycle().manage(PurchasePublisher)
        env.lifecycle().manage(RefundPublisher)
        env.lifecycle().manage(SimProvisioningPublisher)
        env.lifecycle().manage(SubscriptionStatusUpdatePublisher)
    }
}

public public class AnalyticsConfig {

}
    final var projectId: String,

    @NotBlank
    @JsonProperty("dataTrafficTopicId")
    final var dataTrafficTopicId: String,

    @NotBlank
    @JsonProperty("purchaseInfoTopicId")
    final var purchaseInfoTopicId: String,

    @NotBlank
    @JsonProperty("simProvisioningTopicId")
    final var simProvisioningTopicId: String,

    @NotBlank
    @JsonProperty("subscriptionStatusUpdateTopicId")
    final var subscriptionStatusUpdateTopicId: String,

    @NotBlank
    @JsonProperty("refundsTopicId")
    final var refundsTopicId: String
)

public public class ConfigRegistry {
    lateinit var config: AnalyticsConfig
}