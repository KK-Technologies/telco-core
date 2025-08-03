// Converted from Kotlin: GraphQLModule.kt
package org.ostelco.prime.graphql

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule
import java.io.File

package org.ostelco.prime.graphql

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule
import java.io.File

@JsonTypeName("graphql")
public class GraphQLModule : PrimeModule {

    @JsonProperty
    var config: Config = Config(schemaFile = "/config/customer.graphqls")

    override public void init(env: Environment) {
        env.jersey().register(
                GraphQLResource(QueryHandler(File(config.schemaFile))))
    }
}

public public class Config {
    private String schemaFile;

    public Config(String schemaFile) {
        this.schemaFile = schemaFile;
    }

    public String getSchemafile() {
        return schemaFile;
    }

    public void setSchemafile(String schemaFile) {
        this.schemaFile = schemaFile;
    }

}