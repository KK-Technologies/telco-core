// Converted from Kotlin: DocumentDataStoreModule.kt
package org.ostelco.prime.storage.documentstore

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime.storage.documentstore

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import org.ostelco.prime.module.PrimeModule

@JsonTypeName("doc-data-store")
public class DocumentDataStoreModule : PrimeModule {

    @JsonProperty("config")
    public void setConfig(config: Config) {
        ConfigRegistry.config = config
    }
}

public public class ConfigRegistry {
    var config: Config = Config()
}

public public class Config {
    private String = "default" storeType;
    private String = "" namespace;

    public Config(String = "default" storeType, String = "" namespace) {
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

}