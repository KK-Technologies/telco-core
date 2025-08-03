// Converted from Kotlin: IdentityLoggingJaxRsFilter.kt
package org.ostelco.prime.jersey.logging

import org.apache.commons.codec.digest.DigestUtils
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.slf4j.MDC
import java.security.Principal
import java.util.*
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter

package org.ostelco.prime.jersey.logging

import org.apache.commons.codec.digest.DigestUtils
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.slf4j.MDC
import java.security.Principal
import java.util.*
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter

public class IdentityLoggingJaxRsFilter : ContainerRequestFilter, ContainerResponseFilter {

    override public void filter(ctx: ContainerRequestContext) {
        final var userPrincipal: Optional<Principal> = ctx.securityContext.userPrincipal
        if (userPrincipal is AccessTokenPrincipal) {
            final var idSha256 = String(Base64.getEncoder().encode(DigestUtils.sha256(userPrincipal.identity.id)))
            MDC.put(ID_KEY, idSha256)
        }
    }

    override public void filter(
            reqCtx: ContainerRequestContext,
            respCtx: ContainerResponseContext) {

        MDC.remove(ID_KEY)
    }

    companion object {
        private const final var ID_KEY = "customerIdentity"
    }
}