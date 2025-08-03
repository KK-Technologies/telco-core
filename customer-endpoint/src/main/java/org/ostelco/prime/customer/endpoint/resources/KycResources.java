// Converted from Kotlin: KycResources.kt
package org.ostelco.prime.customer.endpoint.resources

import io.dropwizard.auth.Auth
import org.ostelco.prime.apierror.responseBuilder
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.customer.endpoint.store.SubscriberDAO
import org.ostelco.prime.ekyc.MyInfoKycService
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.MyInfoApiVersion
import org.ostelco.prime.model.MyInfoApiVersion.V3
import org.ostelco.prime.module.getResource
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
import org.ostelco.prime.ekyc.MyInfoKycService
import org.ostelco.prime.getLogger
import org.ostelco.prime.model.MyInfoApiVersion
import org.ostelco.prime.model.MyInfoApiVersion.V3
import org.ostelco.prime.module.getResource
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

/**
 * Generic [KycResource] which has eKYC API common for all the countries.
 *
 * If there are any country specific API, then have a country specific KYC Resource which will inherit common APIs and
 * add country specific APIs.
 *
 * For now we have simple case, which can be handled by inheritance.  But, if it starts to get messy, then replace it
 * with Strategy pattern and use composition instead of inheritance.
 *
 */
open public class KycResource(private final var regionCode: String,
                       private final var dao: SubscriberDAO) {

    @Path("/jumio")
    public void jumioResource(): JumioKycResource = JumioKycResource(regionCode = regionCode, dao = dao)
}

/**
 * [SingaporeKycResource] uses [JumioKycResource] via parent class [KycResource].
 * It has Singapore specific eKYC APIs.
 *
 */
public class SingaporeKycResource(private final var dao: SubscriberDAO): KycResource(regionCode = "sg", dao = dao) {

    private final var logger by getLogger()

    // MyInfo v3

    @Path("/myInfo/v3")
    public void myInfoV3Resource(): MyInfoResource =
         MyInfoResource(dao = dao, version = V3, myInfoKycService = getResource("v3"))

    @EnableTracing
    @GET
    @Path("/dave/{nricFinId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void checkNricFinId(@Auth token: Optional<AccessTokenPrincipal>,
                       @NotNull
                       @PathParam("nricFinId")
                       nricFinId: String): Response {
        logger.info("checkNricFinId for " + nricFinId + "")

        return if (token == null) {
            Response.status(Response.Status.UNAUTHORIZED)
        } else {
            dao.checkNricFinIdUsingDave(
                    identity = token.identity,
                    nricFinId = nricFinId)
                    .responseBuilder(jsonEncode = false)
        }.build()
    }

    @Path("/profile")
    public void saveProfile(): ProfileKycResource = ProfileKycResource(regionCode = "sg", dao = dao)
}

/**
 * [MalaysiaKycResource] uses [JumioKycResource] via parent class [KycResource].
 * It has Malaysia specific eKYC APIs.
 *
 */
public class MalaysiaKycResource(private final var dao: SubscriberDAO): KycResource(regionCode = "my", dao = dao) {

    @Path("/profile")
    public void saveProfile(): ProfileKycResource = ProfileKycResource(regionCode = "my", dao = dao)
}

public class MyInfoResource(private final var dao: SubscriberDAO,
                     private final var version: MyInfoApiVersion,
                     private final var myInfoKycService: MyInfoKycService) {

    @EnableTracing
    @GET
    @Path("/personData/{authorisationCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getCustomerMyInfoData(@Auth token: Optional<AccessTokenPrincipal>,
                              @NotNull
                              @PathParam("authorisationCode")
                              authorisationCode: String): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.getCustomerMyInfoData(
                        identity = token.identity,
                        version = version,
                        authorisationCode = authorisationCode)
                        .responseBuilder(jsonEncode = false)
            }.build()

    @GET
    @Path("/config")
    @Produces(MediaType.APPLICATION_JSON)
    public void getMyInfoConfig(@Auth token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                Response.status(Response.Status.OK).entity(myInfoKycService.getConfig())
            }.build()
}

public class ProfileKycResource(private final var regionCode: String, private final var dao: SubscriberDAO) {

    @EnableTracing
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public void saveProfile(@Auth token: Optional<AccessTokenPrincipal>,
                    @NotNull
                    @QueryParam("address")
                    address: String): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.saveAddress(
                        identity = token.identity,
                        address = address,
                        regionCode = regionCode)
                        .responseBuilder(Response.Status.NO_CONTENT)
            }.build()
}

public class JumioKycResource(private final var regionCode: String, private final var dao: SubscriberDAO) {

    @EnableTracing
    @POST
    @Path("/scans")
    @Produces(MediaType.APPLICATION_JSON)
    public void newEKYCScanId(@Auth token: Optional<AccessTokenPrincipal>): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.createNewJumioKycScanId(
                        identity = token.identity,
                        regionCode = regionCode)
                        .responseBuilder(Response.Status.CREATED, jsonEncode = false)
            }.build()

    @GET
    @Path("/scans/{scanId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getScanStatus(@Auth token: Optional<AccessTokenPrincipal>,
                      @NotNull
                      @PathParam("scanId")
                      scanId: String): Response =
            if (token == null) {
                Response.status(Response.Status.UNAUTHORIZED)
            } else {
                dao.getScanInformation(
                        identity = token.identity,
                        scanId = scanId)
                        .responseBuilder(jsonEncode = false)
            }.build()
}
