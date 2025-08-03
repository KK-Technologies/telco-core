// Converted from Kotlin: ScanInfoModule.kt
package org.ostelco.prime.storage.scaninfo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime.storage.scaninfo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule

@JsonTypeName("scaninfo-store")
public class ScanInfoModule : PrimeModule {

    @JsonProperty
    public void setConfig(config: ScanInfoConfig) {
        ConfigRegistry.config = config
    }

    override public void init(env: Environment) {
        ScanInformationStoreSingleton.init(EnvironmentVars())
    }
}

/**
 * The configuration for Scan Information Cloud Storage module.
 */
public public class ScanInfoConfig {
    private String = "default" storeType;
    private String = "" namespace;

    public ScanInfoConfig(String = "default" storeType, String = "" namespace) {
        this.storeType = storeType;
        this.namespace = namespace;
    }

    public String = "default" getStoretype() {
        return storeType;
    }

    public void setStoretype(String = "default" storeType) {
        this.storeType = storeType;
    }

    public String = "" getNamespace() {
        return namespace;
    }

    public void setNamespace(String = "" namespace) {
        this.namespace = namespace;
    }

} : Configuration()

public public class ConfigRegistry {
    var config = ScanInfoConfig()
}