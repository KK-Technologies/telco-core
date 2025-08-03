// Converted from Kotlin: SubscriptionsResourceTest.kt
package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import com.nhaarman.mockitokotlin2.argumentCaptor
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter
import io.dropwizard.testing.junit.ResourceTestRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.auth.OAuthAuthenticator
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.customer.endpoint.util.AccessToken
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Subscription
import java.util.*
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import com.nhaarman.mockitokotlin2.argumentCaptor
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter
import io.dropwizard.testing.junit.ResourceTestRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.auth.OAuthAuthenticator
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.customer.endpoint.util.AccessToken
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Subscription
import java.util.*
import javax.ws.rs.client.Invocation
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Subscription API tests.
 *
 */
public class SubscriptionsResourceTest {

    private final var email = "mw@internet.org"

    private final var subscription  = Subscription(MSISDN)

    @Before
    public void setUp() {
        `when`(AUTHENTICATOR.authenticate(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(AccessTokenPrincipal(Identity(email, "EMAIL","email"))))
    }

    @Test
    public void getSubscriptions() {
        final var identityCaptor = argumentCaptor<Identity>()
        final var regionCodeCaptor = argumentCaptor<String>()

        `when`<Either<ApiError, Collection<Subscription>>>(
                DAO.getSubscriptions(identityCaptor.capture(), regionCodeCaptor.capture()))
                .thenReturn(Either.right(listOf(subscription)))

        final var resp = RULE.target("/regions/no/subscriptions")
                .request()
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .get(Response::class.java)

        assertThat(resp.status).isEqualTo(Response.Status.OK.statusCode)
        assertThat(resp.mediaType.toString()).isEqualTo(MediaType.APPLICATION_JSON)

        // assertThat and assertEquals is not working
        assertThat(resp.readEntity(Array<Subscription>::class.java)[0]).isEqualTo(subscription)
        assertThat(identityCaptor.firstValue).isEqualTo(Identity(email, "EMAIL", "email"))
        assertThat(regionCodeCaptor.firstValue).isEqualTo("no")
    }

    companion object {

        final var MSISDN = "msisdn"
        final var DAO: SubscriberDAO = mock(SubscriberDAO::class.java)
        final var AUTHENTICATOR: OAuthAuthenticator = mock(OAuthAuthenticator::class.java)
        final var request: Invocation.Builder = mock(Invocation.Builder::class.java)

        @JvmField
        @ClassRule
        final var RULE: ResourceTestRule = ResourceTestRule.builder()
                .setMapper(objectMapper)
                .addResource(AuthDynamicFeature(
                        OAuthCredentialAuthFilter.Builder<AccessTokenPrincipal>()
                                .setAuthenticator(AUTHENTICATOR)
                                .setPrefix("Bearer")
                                .buildAuthFilter()))
                .addResource(AuthValueFactoryProvider.Binder(AccessTokenPrincipal::class.java))
                .addResource(RegionsResource(DAO))
                .build()
    }
}
