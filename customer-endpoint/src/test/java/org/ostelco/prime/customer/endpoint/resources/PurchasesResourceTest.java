// Converted from Kotlin: PurchasesResourceTest.kt
package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import com.nhaarman.mockitokotlin2.argumentCaptor
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter.Builder
import io.dropwizard.testing.junit.ResourceTestRule
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.auth.OAuthAuthenticator
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.customer.endpoint.util.AccessToken
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Price
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import java.time.Instant
import java.util.*

package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import com.nhaarman.mockitokotlin2.argumentCaptor
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter.Builder
import io.dropwizard.testing.junit.ResourceTestRule
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.auth.OAuthAuthenticator
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.customer.endpoint.util.AccessToken
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Price
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import java.time.Instant
import java.util.*

/**
 * Purchases API tests.
 *
 */
public class PurchasesResourceTest {

    private final var email = "mw@internet.org"

    private final var userInfo = Base64.getEncoder().encodeToString(
            """{
                 "issuer": "someone",
                 "email": "mw@internet.org"
               }""".trimIndent()
                    .toByteArray())

    @Before
    public void setUp() {
        Mockito.`when`(AUTHENTICATOR.authenticate(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(AccessTokenPrincipal(Identity(email, "EMAIL","email"))))
    }

    @Test
    public void testGetPurchaseRecords() {
        final var arg1 = argumentCaptor<Identity>()

        final var product = Product("1", Price(10, "NOK"), Collections.emptyMap(), Collections.emptyMap())
        final var now = Instant.now().toEpochMilli()
        final var purchaseRecord = PurchaseRecord(
                product = product,
                timestamp = now,
                id = UUID.randomUUID().toString())

        Mockito.`when`<Either<ApiError, Collection<PurchaseRecord>>>(DAO.getPurchaseHistory(arg1.capture()))
                .thenReturn(Either.right(listOf(purchaseRecord)))

        final var purchaseRecords = RULE.target("/purchases")
                .request()
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .header("X-Endpoint-API-UserInfo", userInfo)
                .get(Array<PurchaseRecord>::class.java)

        Assertions.assertThat(purchaseRecords).isEqualTo(arrayOf(purchaseRecord))
    }

    companion object {

        final var DAO: SubscriberDAO = Mockito.mock(SubscriberDAO::class.java)
        final var AUTHENTICATOR: OAuthAuthenticator = Mockito.mock(OAuthAuthenticator::class.java)

        @JvmField
        @ClassRule
        final var RULE = ResourceTestRule.builder()
                .setMapper(objectMapper)
                .addResource(AuthDynamicFeature(
                        Builder<AccessTokenPrincipal>()
                                .setAuthenticator(AUTHENTICATOR)
                                .setPrefix("Bearer")
                                .buildAuthFilter()))
                .addResource(AuthValueFactoryProvider.Binder(AccessTokenPrincipal::class.java))
                .addResource(ProductsResource(DAO))
                .addResource(PurchaseResource(DAO))
                .build()
    }
}