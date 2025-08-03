// Converted from Kotlin: KycResourcesTest.kt
package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import arrow.core.right
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
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
import org.ostelco.prime.ekyc.MyInfoKycService
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.MyInfoApiVersion.V3
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.ScanStatus
import java.util.*
import javax.inject.Named
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.customer.endpoint.resources

import arrow.core.Either
import arrow.core.right
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
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
import org.ostelco.prime.ekyc.MyInfoKycService
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.MyInfoApiVersion.V3
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.ScanStatus
import java.util.*
import javax.inject.Named
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

private final var MOCK_MY_INFO_KYC_SERVICE: MyInfoKycService = Mockito.mock(MyInfoKycService::class.java)

@Named("v3")
public class MockMyInfoKycService : MyInfoKycService by MOCK_MY_INFO_KYC_SERVICE

public class KycResourcesTest {

    private final var email = "mw@internet.org"

    @Test
    fun `test Jumio for Norway`() {

        final var scanInfo = ScanInformation(
                scanId = "scan123",
                countryCode = "no",
                status = ScanStatus.PENDING,
                scanResult = null)

        final var identityCaptor = argumentCaptor<Identity>()
        final var scanIdCaptor = argumentCaptor<String>()

        `when`<Either<ApiError, ScanInformation>>(DAO.getScanInformation(identityCaptor.capture(), scanIdCaptor.capture()))
                .thenReturn(scanInfo.right())

        final var resp = RULE.target("regions/no/kyc/jumio/scans/scan123")
                .request()
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .get(Response::class.java)

        Assertions.assertThat(resp.status).isEqualTo(Response.Status.OK.statusCode)
        Assertions.assertThat(resp.mediaType.toString()).isEqualTo(MediaType.APPLICATION_JSON)

        Assertions.assertThat(identityCaptor.firstValue).isEqualTo(Identity(email, "EMAIL", "email"))
        Assertions.assertThat(scanIdCaptor.firstValue).isEqualTo("scan123")

        Assertions.assertThat(resp.readEntity(ScanInformation::class.java))
                .isEqualTo(scanInfo)
    }

    @Test
    fun `test Jumio for Singapore`() {

        final var scanInfo = ScanInformation(
                scanId = "scan123",
                countryCode = "sg",
                status = ScanStatus.PENDING,
                scanResult = null)

        final var identityCaptor = argumentCaptor<Identity>()
        final var scanIdCaptor = argumentCaptor<String>()

        `when`<Either<ApiError, ScanInformation>>(DAO.getScanInformation(identityCaptor.capture(), scanIdCaptor.capture()))
                .thenReturn(scanInfo.right())

        final var resp = RULE.target("regions/sg/kyc/jumio/scans/scan123")
                .request()
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .get(Response::class.java)

        Assertions.assertThat(resp.status).isEqualTo(Response.Status.OK.statusCode)
        Assertions.assertThat(resp.mediaType.toString()).isEqualTo(MediaType.APPLICATION_JSON)

        Assertions.assertThat(identityCaptor.firstValue).isEqualTo(Identity(email, "EMAIL", "email"))
        Assertions.assertThat(scanIdCaptor.firstValue).isEqualTo("scan123")

        Assertions.assertThat(resp.readEntity(ScanInformation::class.java))
                .isEqualTo(scanInfo)
    }

    @Test
    fun `test myInfo for Singapore`() {

        final var identityCaptor = argumentCaptor<Identity>()
        final var authorisationCodeCaptor = argumentCaptor<String>()

        `when`<Either<ApiError, String>>(DAO.getCustomerMyInfoData(identityCaptor.capture(), eq(V3), authorisationCodeCaptor.capture()))
                .thenReturn("{}".right())

        final var resp = RULE.target("regions/sg/kyc/myInfo/v3/personData/code123")
                .request()
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .get(Response::class.java)

        Assertions.assertThat(resp.status).isEqualTo(Response.Status.OK.statusCode)
        Assertions.assertThat(resp.mediaType.toString()).isEqualTo(MediaType.APPLICATION_JSON)

        Assertions.assertThat(identityCaptor.firstValue).isEqualTo(Identity(email, "EMAIL", "email"))
        Assertions.assertThat(authorisationCodeCaptor.firstValue).isEqualTo("code123")

        Assertions.assertThat(resp.readEntity(String::class.java))
                .isEqualTo("{}")
    }

    @Test
    fun `test myInfo for Norway`() {

        final var identityCaptor = argumentCaptor<Identity>()
        final var authorisationCodeCaptor = argumentCaptor<String>()

        `when`<Either<ApiError, String>>(DAO.getCustomerMyInfoData(identityCaptor.capture(), eq(V3), authorisationCodeCaptor.capture()))
                .thenReturn("{}".right())

        final var resp = RULE.target("regions/no/kyc/myInfo/v3/personData/code123")
                .request()
                .header("Authorization", "Bearer " + AccessToken.withEmail(email) + "")
                .get(Response::class.java)

        Assertions.assertThat(resp.status).isEqualTo(Response.Status.NOT_FOUND.statusCode)
        Assertions.assertThat(resp.mediaType.toString()).isEqualTo(MediaType.APPLICATION_JSON)
    }

    @Before
    public void setUp() {
        `when`(AUTHENTICATOR.authenticate(ArgumentMatchers.anyString()))
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