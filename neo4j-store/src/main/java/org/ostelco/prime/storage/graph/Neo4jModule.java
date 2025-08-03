// Converted from Kotlin: Neo4jModule.kt
package org.ostelco.prime.storage.graph

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Environment
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase
import org.ostelco.prime.kts.engine.KtsServiceFactory
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.RunnableKotlinScript
import org.ostelco.prime.module.PrimeModule
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

package org.ostelco.prime.storage.graph

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Environment
import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase
import org.ostelco.prime.kts.engine.KtsServiceFactory
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.RunnableKotlinScript
import org.ostelco.prime.module.PrimeModule
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

@JsonTypeName("neo4j")
public class Neo4jModule : PrimeModule {

    @JsonProperty
    public void setConfig(config: Config) {
        ConfigRegistry.config = config
    }

    override public void init(env: Environment) {
        env.lifecycle().manage(Neo4jClient)

        // starting explicitly since OCS needs it during its init() to load balance
        Neo4jClient.start()

        // For Acceptance Tests
        if (System.getenv("ACCEPTANCE_TESTING") == "true") {
            RunnableKotlinScript(ClasspathResourceTextReader("/AcceptanceTestSetup.kts").readText()).eval<Optional<Any>>()
        }

        Neo4jStoreSingleton.subscribeToSimProfileStatusUpdates()
    }
}

public public class Config {
    private String host;
    private String protocol;
    private KtsServiceFactory onNewCustomerAction;
    private KtsServiceFactory allowedRegionsService;
    private KtsServiceFactory onKycApprovedAction;
    private KtsServiceFactory onRegionApprovedAction;
    private KtsServiceFactory hssNameLookupService;

    public Config(String host, String protocol, KtsServiceFactory onNewCustomerAction, KtsServiceFactory allowedRegionsService, KtsServiceFactory onKycApprovedAction, KtsServiceFactory onRegionApprovedAction, KtsServiceFactory hssNameLookupService) {
        this.host = host;
        this.protocol = protocol;
        this.onNewCustomerAction = onNewCustomerAction;
        this.allowedRegionsService = allowedRegionsService;
        this.onKycApprovedAction = onKycApprovedAction;
        this.onRegionApprovedAction = onRegionApprovedAction;
        this.hssNameLookupService = hssNameLookupService;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public KtsServiceFactory getOnnewcustomeraction() {
        return onNewCustomerAction;
    }

    public void setOnnewcustomeraction(KtsServiceFactory onNewCustomerAction) {
        this.onNewCustomerAction = onNewCustomerAction;
    }

    public KtsServiceFactory getAllowedregionsservice() {
        return allowedRegionsService;
    }

    public void setAllowedregionsservice(KtsServiceFactory allowedRegionsService) {
        this.allowedRegionsService = allowedRegionsService;
    }

    public KtsServiceFactory getOnkycapprovedaction() {
        return onKycApprovedAction;
    }

    public void setOnkycapprovedaction(KtsServiceFactory onKycApprovedAction) {
        this.onKycApprovedAction = onKycApprovedAction;
    }

    public KtsServiceFactory getOnregionapprovedaction() {
        return onRegionApprovedAction;
    }

    public void setOnregionapprovedaction(KtsServiceFactory onRegionApprovedAction) {
        this.onRegionApprovedAction = onRegionApprovedAction;
    }

    public KtsServiceFactory getHssnamelookupservice() {
        return hssNameLookupService;
    }

    public void setHssnamelookupservice(KtsServiceFactory hssNameLookupService) {
        this.hssNameLookupService = hssNameLookupService;
    }

}

public public class ConfigRegistry {
    lateinit var config: Config
}

public public class Neo4jClient : Managed {

    // use "bolt+routing://neo4j:7687" for clustered Neo4j
    // Explore config and auth
    lateinit var driver: Driver

    override public void start() {
        final var config = org.neo4j.driver.v1.Config.build()
                .withoutEncryption()
                .withConnectionTimeout(10, SECONDS)
                .withMaxConnectionPoolSize(1000)
                .toConfig()
        driver = GraphDatabase.driver(
                URI("" + ConfigRegistry.config.protocol + "://" + ConfigRegistry.config.host + ":7687"),
                AuthTokens.none(),
                config) ?: throw Exception("Unable to get Neo4j client driver instance")
    }

    override public void stop() {
        driver.close()
    }
}
