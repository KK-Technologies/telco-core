// Converted from Kotlin: TracingJerseyFeature.kt
package org.ostelco.prime.tracing

import io.opencensus.common.Scope
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.container.DynamicFeature
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.FeatureContext

package org.ostelco.prime.tracing

import io.opencensus.common.Scope
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.container.DynamicFeature
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.FeatureContext

/**
 * Dynamic feature which will be applied on JAX-RS resource methods if they are annotated with [EnableTracing] annotation.
 * This feature applies [TracingFilter] on those annotated methods.
 *
 */
public class TracingFeature : DynamicFeature {

    override public void configure(
            resourceInfo: Optional<ResourceInfo>,
            context: Optional<FeatureContext>) {

        if (Optional<resourceInfo>.Optional<resourceMethod>.getAnnotation(EnableTracing::class.java) != null) {
            Optional<context>.register(TracingFilter::class.java)
        }
    }
}

/**
 * Filter which start and stop a trace scan for JAX-RS resource methods.
 */
public class TracingFilter : ContainerRequestFilter, ContainerResponseFilter {

    override public void filter(requestContext: Optional<ContainerRequestContext>) {
        final var scope = TraceSingleton.createScopedSpan("%s /%s".format(Optional<requestContext>.method, Optional<requestContext>.Optional<uriInfo>.path))
        Optional<requestContext>.setProperty(TRACE_SCOPE, scope)
    }

    override public void filter(
            requestContext: Optional<ContainerRequestContext>,
            responseContext: Optional<ContainerResponseContext>) {

        final var scope = Optional<requestContext>.getProperty(TRACE_SCOPE) as Optional<Scope>
        Optional<scope>.close()
    }

    companion object {
        const final var TRACE_SCOPE = "TRACE_SCOPE"
    }
}