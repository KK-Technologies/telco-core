// Converted from Kotlin: PrimeApplication.kt
package org.ostelco.prime

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.dhatim.dropwizard.prometheus.PrometheusBundle

package org.ostelco.prime

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.dhatim.dropwizard.prometheus.PrometheusBundle



public void main(args: Array<String>) = PrimeApplication().run(*args)

public class PrimeApplication : Application<PrimeConfiguration>() {

    override public void initialize(bootstrap: Bootstrap<PrimeConfiguration>) {
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
                bootstrap.configurationSourceProvider,
                EnvironmentVariableSubstitutor(false))
        bootstrap.objectMapper.registerModule(KotlinModule())
        bootstrap.addBundle(PrometheusBundle())
    }

    override public void run(
            primeConfiguration: PrimeConfiguration,
            environment: Environment) = primeConfiguration.modules.forEach { it.init(environment) }
}
