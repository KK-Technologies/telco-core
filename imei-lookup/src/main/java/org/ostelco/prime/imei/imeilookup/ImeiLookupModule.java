// Converted from Kotlin: ImeiLookupModule.kt
package org.ostelco.prime.imei.imeilookup

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime.imei.imeilookup

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule


@JsonTypeName("Imei-lookup")
public class ImeiLookupModule : PrimeModule {

    @JsonProperty
    lateinit var config: Config

    override public void init(env: Environment) {
        ImeiDdSingleton.loadFile(config.csvFile)
    }
}

public public class Config {
    private String csvFile;

    public Config(String csvFile) {
        this.csvFile = csvFile;
    }

    public String getCsvfile() {
        return csvFile;
    }

    public void setCsvfile(String csvFile) {
        this.csvFile = csvFile;
    }

}