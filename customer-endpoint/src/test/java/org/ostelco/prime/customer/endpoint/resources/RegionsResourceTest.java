// Converted from Kotlin: RegionsResourceTest.kt
package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import arrow.core.right
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
import org.mockito.Mockito.`when`
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.auth.OAuthAuthenticator
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.customer.endpoint.util.AccessToken
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.CustomerRegionStatus.PENDING
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Region
import org.ostelco.prime.model.RegionDetails
import java.util.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import arrow.core.right
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
import org.mockito.Mockito.`when`
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.auth.OAuthAuthenticator
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.customer.endpoint.util.AccessToken
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.CustomerRegionStatus.PENDING
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Region
import org.ostelco.prime.model.RegionDetails
import java.util.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

public class RegionsResourceTest {

    private final var email = "mw@internet.org"

    @Test
    fun `test getRegions`() {

        final var regions = listOf(
                RegionDetails(
                        region = Region(id = "no", name = "Norway"),
                        status = PENDING))

        final var identityCaptor = argumentCaptor<Identity>()

        `when`<Either<ApiError, Collection<RegionDetails>>>(DAO.getRegions(identityCaptor.capture()))
                .thenReturn(regions.right())

        final var resp = RULE.target("regions")
                .request()
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .get(Response::class.java)

        Assertions.assertThat(resp.status).isEqualTo(Response.Status.OK.statusCode)
        Assertions.assertThat(resp.mediaType.toString()).isEqualTo(MediaType.APPLICATION_JSON)

        Assertions.assertThat(identityCaptor.firstValue).isEqualTo(Identity(email, "EMAIL", "email"))

        Assertions.assertThat(resp.readEntity(Array<RegionDetails>::class.java))
                .isEqualTo(regions.toTypedArray())

    }

    @Before
    public void setUp() {
        Mockito.`when`(AUTHENTICATOR.authenticate(ArgumentMatchers.anyString()))
                .thenReturn(Optional.of(AccessTokenPrincipal(Identity(email, "EMAIL","email"))))
    }

    companion object {

        final var DAO: SubscriberDAO = Mockito.mock(SubscriberDAO::class.java)
        final var AUTHENTICATOR: OAuthAuthenticator = Mockito.mock(OAuthAuthenticator::class.java)

        @JvmField
        @ClassRule
        final var RULE: ResourceTestRule = ResourceTestRule.builder()
                .setMapper(objectMapper)
                .addResource(AuthDynamicFeature(
                        Builder<AccessTokenPrincipal>()
                                .setAuthenticator(AUTHENTICATOR)
                                .setPrefix("Bearer")
                                .buildAuthFilter()))
                .addResource(AuthValueFactoryProvider.Binder(AccessTokenPrincipal::class.java))
                .addResource(RegionsResource(DAO))
                .build()
    }
}