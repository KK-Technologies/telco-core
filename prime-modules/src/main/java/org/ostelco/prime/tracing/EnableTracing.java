// Converted from Kotlin: EnableTracing.kt
package org.ostelco.prime.tracing


package org.ostelco.prime.tracing

/**
 * Use this annotation on JAX-RS resource methods which are to be traced.
 * For those methods, a then a dynamic filter will be applied.
 * This filter will start a trace for entire request processing till response is sent back.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation public class EnableTracing