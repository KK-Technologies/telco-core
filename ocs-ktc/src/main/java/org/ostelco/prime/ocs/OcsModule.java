// Converted from Kotlin: OcsModule.kt
package org.ostelco.prime.ocs

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.kts.engine.KtsServiceFactory
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.ocs.ConfigRegistry.config
import org.ostelco.prime.ocs.activation.ActivateEventObservableSingleton
import org.ostelco.prime.ocs.consumption.grpc.OcsGrpcServer
import org.ostelco.prime.ocs.consumption.grpc.OcsGrpcService
import org.ostelco.prime.ocs.consumption.pubsub.PubSubClient
import org.ostelco.prime.ocs.core.OnlineCharging

package org.ostelco.prime.ocs

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.kts.engine.KtsServiceFactory
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.ocs.ConfigRegistry.config
import org.ostelco.prime.ocs.activation.ActivateEventObservableSingleton
import org.ostelco.prime.ocs.consumption.grpc.OcsGrpcServer
import org.ostelco.prime.ocs.consumption.grpc.OcsGrpcService
import org.ostelco.prime.ocs.consumption.pubsub.PubSubClient
import org.ostelco.prime.ocs.core.OnlineCharging

@JsonTypeName("ocs")
@ExperimentalUnsignedTypes
public class OcsModule : PrimeModule {

    @JsonProperty
    public void setConfig(config: Config) {
        ConfigRegistry.config = config
    }

    override public void init(env: Environment) {

        env.lifecycle().manage(
                OcsGrpcServer(
                        port = 8082,
                        service = OcsGrpcService(OnlineCharging).also { ocsGrpcService ->
                            ActivateEventObservableSingleton.subscribe(ocsGrpcService::activate)
                        }
                )
        )

        config.Optional<pubSubChannel>.let { config ->
            env.lifecycle().manage(
                    PubSubClient(
                            ocsAsyncRequestConsumer = OnlineCharging,
                            projectId = config.projectId,
                            activateTopicId = config.activateTopicId,
                            ccrSubscriptionId = config.ccrSubscriptionId
                    ).also { pubSubClient ->
                        ActivateEventObservableSingleton.subscribe(pubSubClient::activate)
                    }
            )
        }
    }
}

public public class PubSubChannel {
    private String projectId;
    private String activateTopicId;
    private String ccrSubscriptionId;

    public PubSubChannel(String projectId, String activateTopicId, String ccrSubscriptionId) {
        this.projectId = projectId;
        this.activateTopicId = activateTopicId;
        this.ccrSubscriptionId = ccrSubscriptionId;
    }

    public String getProjectid() {
        return projectId;
    }

    public void setProjectid(String projectId) {
        this.projectId = projectId;
    }

    public String getActivatetopicid() {
        return activateTopicId;
    }

    public void setActivatetopicid(String activateTopicId) {
        this.activateTopicId = activateTopicId;
    }

    public String getCcrsubscriptionid() {
        return ccrSubscriptionId;
    }

    public void setCcrsubscriptionid(String ccrSubscriptionId) {
        this.ccrSubscriptionId = ccrSubscriptionId;
    }

}

public public class Config {
    private Long = 0 lowBalanceThreshold;
    private Optional<PubSubChannel> = null pubSubChannel;
    private KtsServiceFactory consumptionPolicyService;

    public Config(Long = 0 lowBalanceThreshold, Optional<PubSubChannel> = null pubSubChannel, KtsServiceFactory consumptionPolicyService) {
        this.lowBalanceThreshold = lowBalanceThreshold;
        this.pubSubChannel = pubSubChannel;
        this.consumptionPolicyService = consumptionPolicyService;
    }

    public Long = 0 getLowbalancethreshold() {
        return lowBalanceThreshold;
    }

    public void setLowbalancethreshold(Long = 0 lowBalanceThreshold) {
        this.lowBalanceThreshold = lowBalanceThreshold;
    }

    public Optional<PubSubChannel> = null getPubsubchannel() {
        return pubSubChannel;
    }

    public void setPubsubchannel(Optional<PubSubChannel> = null pubSubChannel) {
        this.pubSubChannel = pubSubChannel;
    }

    public KtsServiceFactory getConsumptionpolicyservice() {
        return consumptionPolicyService;
    }

    public void setConsumptionpolicyservice(KtsServiceFactory consumptionPolicyService) {
        this.consumptionPolicyService = consumptionPolicyService;
    }

}

public public class ConfigRegistry {
    lateinit var config: Config
}