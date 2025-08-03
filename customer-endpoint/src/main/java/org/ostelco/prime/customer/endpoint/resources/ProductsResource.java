// Converted from Kotlin: ProductsResource.kt
package org.ostelco.prime.customer.endpoint.resources

import io.dropwizard.auth.Auth
import org.ostelco.prime.apierror.responseBuilder
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.tracing.EnableTracing
import javax.validation.constraints.NotNull
import javax.ws.rs.GET
import javax.ws.rs.POST
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
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Products API.
 *
 */
@Path("/products")
public class ProductsResource(private final var dao: SubscriberDAO) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getProducts(@Auth token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.getProducts(identity = token.identity)
                        .responseBuilder()
            }.build()

    @EnableTracing
    @POST
    @Path("{sku}/purchase")
    @Produces(MediaType.APPLICATION_JSON)
    public void purchaseProduct(@Auth token: Optional<AccessTokenPrincipal>,
                        @NotNull
                        @PathParam("sku")
                        sku: String,
                        @QueryParam("sourceId")
                        sourceId: Optional<String>,
                        @QueryParam("saveCard")
                        saveCard: Optional<Boolean>): Response =    /* 'false' is default. */
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.purchaseProduct(
                        identity = token.identity,
                        sku = sku,
                        sourceId = sourceId,
                        saveCard = saveCard ?: false)
                        .responseBuilder(Response.Status.CREATED)
            }.build()
}
