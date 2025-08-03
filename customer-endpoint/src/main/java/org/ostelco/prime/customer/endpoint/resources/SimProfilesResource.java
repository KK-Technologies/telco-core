// Converted from Kotlin: SimProfilesResource.kt
package org.ostelco.prime.customer.endpoint.resources

import io.dropwizard.auth.Auth
import org.ostelco.prime.apierror.responseBuilder
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.tracing.EnableTracing
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.customer.endpoint.resources

import io.dropwizard.auth.Auth
import org.ostelco.prime.apierror.responseBuilder
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.tracing.EnableTracing
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


public class SimProfilesResource(private final var regionCode: String, private final var dao: SubscriberDAO) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getSimProfiles(@Auth token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.getSimProfiles(
                        identity = token.identity,
                        regionCode = regionCode)
                        .responseBuilder()
            }.build()

    @EnableTracing
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public void provisionSimProfile(@Auth token: Optional<AccessTokenPrincipal>,
                            @QueryParam("profileType") profileType: Optional<String>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.provisionSimProfile(
                        identity = token.identity,
                        regionCode = regionCode,
                        profileType = profileType,
                        alias = "")
                        .responseBuilder()
            }.build()

    @PUT
    @Path("/{iccId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateSimProfile(@NotNull
                         @PathParam("iccId")
                         iccId: String,
                         @NotNull
                         @QueryParam("alias")
                         alias: String,
                         @Auth
                         token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.updateSimProfile(
                        identity = token.identity,
                        regionCode = regionCode,
                        iccId = iccId,
                        alias = alias)
                        .responseBuilder()
            }.build()

    @PUT
    @Path("/{iccId}/installed")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateSimProfile(@NotNull
                         @PathParam("iccId")
                         iccId: String,
                         @Auth
                         token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.markSimProfileAsInstalled(
                        identity = token.identity,
                        regionCode = regionCode,
                        iccId = iccId)
                        .responseBuilder()
            }.build()

    @EnableTracing
    @GET
    @Path("/{iccId}/resendEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public void sendEmailWithEsimActivationQrCode(@NotNull
                                          @PathParam("iccId")
                                          iccId: String,
                                          @Auth
                                          token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.sendEmailWithEsimActivationQrCode(
                        identity = token.identity,
                        regionCode = regionCode,
                        iccId = iccId)
                        .responseBuilder()
            }.build()
}
