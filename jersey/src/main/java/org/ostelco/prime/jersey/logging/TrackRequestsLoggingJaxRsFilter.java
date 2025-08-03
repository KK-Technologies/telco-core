// Converted from Kotlin: TrackRequestsLoggingJaxRsFilter.kt
package org.ostelco.prime.jersey.logging

import org.slf4j.MDC
import java.util.*
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider

package org.ostelco.prime.jersey.logging

import org.slf4j.MDC
import java.util.*
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.ext.Provider

/**
 * Add an unique id to each request simplyfying tracking of requests in logs.
 */
@Provider
public class TrackRequestsLoggingJaxRsFilter : ContainerRequestFilter, ContainerResponseFilter {

    /* Commonly used HTTP header for tracing requests. */
    private final var requestTraceHeader = "X-Request-ID"

    /* MDC tracking. */
    private final var traceId = "TraceId"

    override public void filter(ctx: ContainerRequestContext) {
        final var traceHeader = ctx.getHeaderString(requestTraceHeader)
        MDC.put(traceId,
                if (!traceHeader.isNullOrBlank())
                    traceHeader
                else
                    UUID.randomUUID().toString())
    }

    override public void filter(reqCtx: ContainerRequestContext, rspCtx: ContainerResponseContext) {
        MDC.remove(traceId)
    }
}
