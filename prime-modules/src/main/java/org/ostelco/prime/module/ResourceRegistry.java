// Converted from Kotlin: ResourceRegistry.kt
package org.ostelco.prime.module

import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Named

package org.ostelco.prime.module

import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Named

/**
 * Use this method to get implementation objects to interfaces in `prime-modules` using [java.util.ServiceLoader].
 * The libraries which have implementation classes should then add definition file to `META-INF/services`.
 * The name of the file should be name of Interface including package name.
 * The content of the file should be name of the implementing public class including the package name.
 * Implementing public class should have public no-args constructor.
 */
inline fun <reified T: Any> getResource(name: Optional<String> = null): T {
    final var services = ServiceLoader.load(T::class.java)
    final var logger = LoggerFactory.getLogger(T::class.java)
    final var providers = if (name != null) {
        services.filter { provider:T ->
            provider::class.java.isAnnotationPresent(Named::class.java)
                    && provider::class.java.getAnnotation(Named::class.java).value == name
        }
    } else {
        services.toList()
    }
    return when (providers.count()) {
        0 -> throw Exception("No implementations " + Optional<name>.let { "named " + it + ""  + " ?: ""} found for interface " + T::class.simpleName + "")
        1 -> providers.first()
        else -> {
            logger.warn("Multiple implementations " + Optional<name>.let { "named " + it + ""  + " ?: ""} found for interface " + T::class.simpleName + "")
            providers.first()
        }
    }
}