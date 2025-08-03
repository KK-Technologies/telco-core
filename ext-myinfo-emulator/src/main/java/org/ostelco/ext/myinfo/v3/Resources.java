// Converted from Kotlin: Resources.kt
package org.ostelco.ext.myinfo.v3

import org.ostelco.ext.myinfo.JsonUtils.compactJson
import org.ostelco.ext.myinfo.JweCompactUtils
import org.ostelco.ext.myinfo.JwtUtils
import org.ostelco.ext.myinfo.JwtUtils.createAccessToken
import org.ostelco.ext.myinfo.JwtUtils.getClaims
import org.ostelco.ext.myinfo.MyInfoEmulatorConfig
import org.ostelco.prime.getLogger
import javax.ws.rs.Consumes
import javax.ws.rs.FormParam
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.ext.myinfo.v3

import org.ostelco.ext.myinfo.JsonUtils.compactJson
import org.ostelco.ext.myinfo.JweCompactUtils
import org.ostelco.ext.myinfo.JwtUtils
import org.ostelco.ext.myinfo.JwtUtils.createAccessToken
import org.ostelco.ext.myinfo.JwtUtils.getClaims
import org.ostelco.ext.myinfo.MyInfoEmulatorConfig
import org.ostelco.prime.getLogger
import javax.ws.rs.Consumes
import javax.ws.rs.FormParam
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/v3/token")
public class TokenResource(private final var config: MyInfoEmulatorConfig) {

    private final var logger by getLogger()

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public void getToken(
            @FormParam("grant_type") grantType: Optional<String>,
            @FormParam("code") authorisationCode: Optional<String>,
            @FormParam("redirect_uri") redirectUri: Optional<String>,
            @FormParam("client_id") clientId: Optional<String>,
            @FormParam("client_secret") clientSecret: Optional<String>,
            @HeaderParam("Authorization") authHeaderString: Optional<String>,
            @Context headers: HttpHeaders,
            body: String): Response {

        logger.debug("Content-Type: " + headers.mediaType + "")
        logger.debug("Headers >>>\n" + headers.requestHeaders.entries.joinToString("\n") + "\n<<< End of Headers")
        logger.debug("Body >>>\n" + body + "\n<<< End of Body")

        return when {

            headers.mediaType != MediaType.APPLICATION_FORM_URLENCODED_TYPE ->
                Response.status(Response.Status.BAD_REQUEST)
                        .entity("""{reason: "Invalid Content-Type - " + headers.mediaType + ""}""")
                        .build()

            grantType != "authorization_code" ->
                Response.status(Response.Status.BAD_REQUEST)
                        .entity("""{reason: "Invalid grant_type"}""")
                        .build()

            redirectUri != config.myInfoRedirectUri ->
                Response.status(Response.Status.FORBIDDEN)
                        .entity("""{reason: "Invalid redirect_uri"}""")
                        .build()

            clientId != config.myInfoApiClientId ->
                Response.status(Response.Status.FORBIDDEN)
                        .entity("""{reason: "Invalid client_id"}""")
                        .build()

            clientSecret != config.myInfoApiClientSecret ->
                Response.status(Response.Status.FORBIDDEN)
                        .entity("""{reason: "Invalid client_secret"}""")
                        .build()

            else ->
                Response.status(Response.Status.OK).entity("""
                    {
                        "access_token":"" + createAccessToken(config.myInfoServerPrivateKey) + "",
                        "scope":"mobileno nationality dob name mailadd email sex residentialstatus",
                        "token_type":"Bearer",
                        "expires_in":1799
                    }""".trimIndent())
                        .build()
        }
    }
}

@Path("/v3/person")
public class PersonResource(private final var config: MyInfoEmulatorConfig) {

    private final var logger by getLogger()

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{uinFin}")
    public void getToken(
            @PathParam("uinFin") uinFin: String,
            @QueryParam("client_id") clientId: String,
            @QueryParam("attributes") attributes: String,
            @HeaderParam("Authorization") authHeaderString: String,
            @Context headers: HttpHeaders,
            body: String): Response {

        logger.debug("Content-Type: " + headers.mediaType + "")
        logger.debug("Headers >>>\n" + headers.requestHeaders.entries.joinToString("\n") + "\n<<< End of Headers")
        logger.debug("Body >>>\n" + body + "\n<<< End of Body")

        if (!authHeaderString.contains("Bearer ")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("""{reason: "Missing JWT Access Token"}""")
                    .build()
        }

        final var claims = getClaims(authHeaderString.substringAfter("Bearer "), config.myInfoServerPublicKey)

        if (claims.body.subject != uinFin) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("""{reason: "Invalid Subject in Access Token"}""")
                    .build()
        }

        if (authHeaderString.startsWith("Bearer ")) {
            return Response
                    .status(Response.Status.OK)
                    .entity(personData)
                    .build()
        }

        return Response
                .status(Response.Status.OK)
                .entity(JweCompactUtils
                        .encrypt(
                                config.myInfoClientPublicKey,
                                JwtUtils.createJws(config.myInfoServerPrivateKey, personData)))
                .build()
    }

    private final var personData = compactJson("""
{
  "name": {
    "lastupdated": "2019-04-05",
    "source": "1",
    "classification": "C",
    "value": "TAN XIAO HUI"
  },
  "sex": {
    "lastupdated": "2019-04-05",
    "code": "F",
    "source": "1",
    "classification": "C",
    "desc": "FEMALE"
  },
  "nationality": {
    "lastupdated": "2019-04-05",
    "code": "SG",
    "source": "1",
    "classification": "C",
    "desc": "SINGAPORE CITIZEN"
  },
  "dob": {
    "lastupdated": "2019-04-05",
    "source": "1",
    "classification": "C",
    "value": "1998-06-06"
  },
  "email": {
    "lastupdated": "2019-04-05",
    "source": "2",
    "classification": "C",
    "value": "myinfotesting@gmail.com"
  },
  "mobileno": {
    "lastupdated": "2019-04-05",
    "source": "2",
    "classification": "C",
    "areacode": {
      "value": "65"
    },
    "prefix": {
      "value": "+"
    },
    "nbr": {
      "value": "97399245"
    }
  },
  "mailadd": {
    "country": {
      "code": "SG",
      "desc": "SINGAPORE"
    },
    "unit": {
      "value": "128"
    },
    "street": {
      "value": "BEDOK NORTH AVENUE 4"
    },
    "lastupdated": "2019-04-05",
    "block": {
      "value": "102"
    },
    "source": "1",
    "postal": {
      "value": "460102"
    },
    "classification": "C",
    "floor": {
      "value": "09"
    },
    "type": "SG",
    "building": {
      "value": "PEARL GARDEN"
    }
  }
}
    """)
}