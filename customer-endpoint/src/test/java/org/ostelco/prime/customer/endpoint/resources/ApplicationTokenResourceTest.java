// Converted from Kotlin: ApplicationTokenResourceTest.kt
package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import arrow.core.right
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
import org.ostelco.prime.model.ApplicationToken
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import arrow.core.right
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
import org.ostelco.prime.model.ApplicationToken
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * ApplicationToken API tests.
 *
 */
public class ApplicationTokenResourceTest {

    private final var email = "boaty@internet.org"

    private final var token = "testToken:kshfkajhka"
    private final var applicationID = "myAppID:4378932"
    private final var tokenType = "FCM"

    private final var applicationToken = ApplicationToken(
            applicationID = applicationID,
            token = token,
            tokenType = tokenType)

    @Before
    public void setUp() {
        `when`(AUTHENTICATOR.authenticate(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(AccessTokenPrincipal(Identity(email, "EMAIL","email"))))
    }

    @Test
    public void storeApplicationToken() {
        final var arg1 = argumentCaptor<String>()
        final var arg2 = argumentCaptor<ApplicationToken>()

        final var argIdentity = argumentCaptor<Identity>()
        final var customer = Customer(contactEmail = email, nickname = "foo")

        `when`(DAO.storeApplicationToken(arg1.capture(), arg2.capture()))
                .thenReturn(Either.right(applicationToken))
        `when`<Either<ApiError, Customer>>(DAO.getCustomer(argIdentity.capture())).thenReturn(customer.right())

        final var resp = RULE.target("/applicationToken")
                .request(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .post(Entity.json("""{
                    "token": "" + token + "",
                    "applicationID": "" + applicationID + "",
                    "tokenType": "" + tokenType + ""
                }""".trimIndent()))

        assertThat(resp.status).isEqualTo(Response.Status.CREATED.statusCode)
        assertThat(resp.mediaType.toString()).isEqualTo(MediaType.APPLICATION_JSON)
        assertThat(arg1.firstValue).isEqualTo(customer.id)
        assertThat(arg2.firstValue.token).isEqualTo(token)
        assertThat(arg2.firstValue.applicationID).isEqualTo(applicationID)
        assertThat(arg2.firstValue.tokenType).isEqualTo(tokenType)
    }

    companion object {

        final var DAO = mock(SubscriberDAO::class.java)
        final var AUTHENTICATOR = mock(OAuthAuthenticator::class.java)
        final var client: Client = mock(Client::class.java)

        @JvmField
        @ClassRule
        final var RULE = ResourceTestRule.builder()
                .setMapper(objectMapper)
                .addResource(AuthDynamicFeature(
                        OAuthCredentialAuthFilter.Builder<AccessTokenPrincipal>()
                                .setAuthenticator(AUTHENTICATOR)
                                .setPrefix("Bearer")
                                .buildAuthFilter()))
                .addResource(AuthValueFactoryProvider.Binder(AccessTokenPrincipal::class.java))
                .addResource(ApplicationTokenResource(DAO))
                .build()
    }
}
