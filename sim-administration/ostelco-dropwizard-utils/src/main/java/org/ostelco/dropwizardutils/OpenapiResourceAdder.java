// Converted from Kotlin: OpenapiResourceAdder.kt
package org.ostelco.dropwizardutils

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.jersey.setup.JerseyEnvironment
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource
import io.swagger.v3.oas.integration.SwaggerConfiguration
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.validation.Valid
import javax.validation.constraints.NotNull

package org.ostelco.dropwizardutils

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.jersey.setup.JerseyEnvironment
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource
import io.swagger.v3.oas.integration.SwaggerConfiguration
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * Utility for adding an OpenAPI resource to your app.   Adds an utility
 * method to add the resource to a jersey environment, and a condfiguration
 * public class that must be referenced by your app's config public class containing
 * misc. metainformation about the API that is required by openAPI.
 */
public class OpenapiResourceAdder {
    companion object {

        public void addOpenapiResourceToJerseyEnv(jerseyEnvironment: JerseyEnvironment, config:OpenapiResourceAdderConfig ) {
            final var oas = OpenAPI()
            final var info = Info()
                    .title(config.name)
                    .description(config.description)
                    .termsOfService(config.termsOfService)
                    .contact(Contact().email(config.contactEmail))

            oas.info(info)
            final var oasConfig = SwaggerConfiguration()
                    .openAPI(oas)
                    .prettyPrint(true)
                    .resourcePackages(Stream.of(config.resourcePackage)
                            .collect(Collectors.toSet<String>()))

            jerseyEnvironment.register(OpenApiResource()
                    .openApiConfiguration(oasConfig))
        }
    }
}

public class OpenapiResourceAdderConfig {

    @Valid
    @NotNull
    @JsonProperty("name")
    var name: String = ""

    @Valid
    @NotNull
    @JsonProperty("description")
    var description: String = ""

    @Valid
    @NotNull
    @JsonProperty("termsOfService")
    var termsOfService: String = ""


    @NotNull
    @JsonProperty("contactEmail")
    var contactEmail: String = ""

    @NotNull
    @JsonProperty("resourcePackage")
    var resourcePackage: String = ""
}