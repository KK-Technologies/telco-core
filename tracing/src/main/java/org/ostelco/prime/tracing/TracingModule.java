// Converted from Kotlin: TracingModule.kt
package org.ostelco.prime.tracing

import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime.tracing

import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule

@JsonTypeName("tracing")
public class TracingModule : PrimeModule {

    override public void init(env: Environment) {
        TraceSingleton.init()
        env.jersey().register(TracingFeature::class.java)
    }
}