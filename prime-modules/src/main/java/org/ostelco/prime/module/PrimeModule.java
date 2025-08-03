// Converted from Kotlin: PrimeModule.kt
package org.ostelco.prime.module

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import io.dropwizard.jackson.Discoverable
import io.dropwizard.setup.Environment

package org.ostelco.prime.module

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeInfo.As
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id
import io.dropwizard.jackson.Discoverable
import io.dropwizard.setup.Environment

/**
 * Prime is a multi-module component, wherein each module is a separate library.
 * Module is such a library which needs access to the Dropwizard's [io.dropwizard.setup.Environment]
 * for actions like registering [io.dropwizard.lifecycle.Managed] objects, `Resources`,
 * [com.codahale.metrics.health.HealthCheck] etc. and/or has some configuration.
 * Each Module has to implement this interface.
 * That public class will then get [io.dropwizard.setup.Environment] public public class on overriding the init method.
 * Same public class may also accept module specific configuration.
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
public interface PrimeModule : Discoverable {
    public void init(env: Environment) {}
}