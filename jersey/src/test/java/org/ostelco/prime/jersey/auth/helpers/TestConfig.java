// Converted from Kotlin: TestConfig.kt
package org.ostelco.prime.jersey.auth.helpers

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.cache.CacheBuilderSpec
import io.dropwizard.Configuration
import io.dropwizard.client.JerseyClientConfiguration
import javax.validation.Valid
import javax.validation.constraints.NotNull

package org.ostelco.prime.jersey.auth.helpers

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.cache.CacheBuilderSpec
import io.dropwizard.Configuration
import io.dropwizard.client.JerseyClientConfiguration
import javax.validation.Valid
import javax.validation.constraints.NotNull

public class TestConfig : Configuration() {
    @NotNull
    @Valid
    @get:JsonProperty("secret")
    @set:JsonProperty("secret")
    lateinit var secret: String

    @Valid
    @NotNull
    @get:JsonProperty("authenticationCachePolicy")
    lateinit var authenticationCachePolicy: CacheBuilderSpec
        private set

    @Valid
    @NotNull
    @get:JsonProperty("jerseyClient")
    final var jerseyClientConfiguration = JerseyClientConfiguration()

    @JsonProperty("authenticationCachePolicy")
    public void setAuthenticationCachePolicy(spec: String) {
        this.authenticationCachePolicy = CacheBuilderSpec.parse(spec)
    }
}
