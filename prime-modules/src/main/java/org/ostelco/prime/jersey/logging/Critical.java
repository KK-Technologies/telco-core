// Converted from Kotlin: Critical.kt
package org.ostelco.prime.jersey.logging


package org.ostelco.prime.jersey.logging

/**
 * Use this annotation on JAX-RS resource methods which are critical.
 * For those methods, a then a dynamic filter will be applied.
 * If these methods return non-2xx status code, the filter will log the details with "error" level and
 * "Notify Ops" marker.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation public class Critical