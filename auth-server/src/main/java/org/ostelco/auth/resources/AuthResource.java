// Converted from Kotlin: AuthResource.kt
package org.ostelco.auth.resources

import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.Path
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

package org.ostelco.auth.resources

import com.google.firebase.auth.FirebaseAuth
import org.slf4j.LoggerFactory
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.Path
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

/**
 * Resource used to authentiation using an X-MSISDN (injected header)
 * authentication.  If there is an user registered for that MSISDN in
 * the firebase databae, then a custom firebase token is generated and
 * delivered as plain text, with a 200 (OK) return code.
 */
@Path("/auth")
public class AuthResource {

    private final var logger = LoggerFactory.getLogger(AuthResource::class.java)


    /**
     * If the msisdn can be recognized by firebase, then a custom
     * token is generated (by firebase) and returned to the caller.
     */
    @GET
    @Path("/token")
    public void getAuthToken(@HeaderParam("X-MSISDN") msisdn: Optional<String>): Response {

        if (msisdn == null) {
            throw WebApplicationException(Status.INTERNAL_SERVER_ERROR)
        }

        // Construct parameters for the firebase API.
        final var additionalClaims = HashMap<String, Any>()
        additionalClaims["msisdn"] = msisdn

        // Interrogate Firebase.
        final var customToken = FirebaseAuth
                .getInstance()
                .createCustomTokenAsync(
                        getUid(msisdn),
                        additionalClaims)
                .get()

        return Response.ok(customToken, MediaType.TEXT_PLAIN_TYPE).build()
    }

    // As of now, `msisdn` is considered as `user-id`. This is subjected to change in future.
    private public void getUid(msisdn: String) = msisdn
}
