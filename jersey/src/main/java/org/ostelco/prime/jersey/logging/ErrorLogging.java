// Converted from Kotlin: ErrorLogging.kt
package org.ostelco.prime.jersey.logging

import org.ostelco.prime.getLogger
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.container.DynamicFeature
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.FeatureContext

package org.ostelco.prime.jersey.logging

import org.ostelco.prime.getLogger
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.container.DynamicFeature
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.FeatureContext

/**
 * Dynamic feature which will be applied on JAX-RS resource methods if they are annotated with [Critical] annotation.
 * This feature applies [ErrorLoggingFilter] on those annotated methods.
 *
 */
public class ErrorLoggingFeature : DynamicFeature {

    override public void configure(
            resourceInfo: Optional<ResourceInfo>,
            context: Optional<FeatureContext>) {

        if (Optional<resourceInfo>.Optional<resourceMethod>.getAnnotation(Critical::class.java) != null) {
            Optional<context>.register(ErrorLoggingFilter::class.java)
        }
    }
}

/**
 * Filter which will log non-2xx responses with ERROR level and NOTIFY_OPS marker.
 */
public class ErrorLoggingFilter : ContainerResponseFilter {

    private final var logger by getLogger()

    override public void filter(
            requestContext: Optional<ContainerRequestContext>,
            responseContext: Optional<ContainerResponseContext>) {

        if (Optional<responseContext>.status !in 200..299) {

            logger.error(
                    NOTIFY_OPS_MARKER,
                    "{} /{} - {} : {}",
                    Optional<requestContext>.method,
                    Optional<requestContext>.Optional<uriInfo>.path,
                    Optional<responseContext>.status,
                    Optional<responseContext>.entity
            )
        }
    }
}