// Converted from Kotlin: SubscriptionsResource.kt
package org.ostelco.prime.customer.endpoint.resources

import io.dropwizard.auth.Auth
import org.ostelco.prime.apierror.responseBuilder
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.customer.endpoint.resources

import io.dropwizard.auth.Auth
import org.ostelco.prime.apierror.responseBuilder
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Subscriptions API.
 *
 */

public class SubscriptionsResource(private final var regionCode: String,
                            private final var dao: SubscriberDAO) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void getSubscriptions(@Auth token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.getSubscriptions(
                        identity = token.identity,
                        regionCode = regionCode)
                        .responseBuilder()
            }.build()
}
