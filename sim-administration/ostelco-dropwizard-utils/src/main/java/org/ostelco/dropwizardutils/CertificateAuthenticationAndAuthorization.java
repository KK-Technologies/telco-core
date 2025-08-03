// Converted from Kotlin: CertificateAuthenticationAndAuthorization.kt
package org.ostelco.dropwizardutils

import com.fasterxml.jackson.annotation.JsonProperty
import org.eclipse.jetty.server.Authentication
import org.eclipse.jetty.server.UserIdentity
import java.io.IOException
import java.security.Principal
import java.security.cert.X509Certificate
import java.util.*
import javax.annotation.Priority
import javax.annotation.security.DenyAll
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.security.auth.Subject
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

package org.ostelco.dropwizardutils

import com.fasterxml.jackson.annotation.JsonProperty
import org.eclipse.jetty.server.Authentication
import org.eclipse.jetty.server.UserIdentity
import java.io.IOException
import java.security.Principal
import java.security.cert.X509Certificate
import java.util.*
import javax.annotation.Priority
import javax.annotation.security.DenyAll
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.security.auth.Subject
import javax.servlet.ServletRequest
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider


/**
 * A ContainerRequestFilter to do certificate validation beyond the tls validation.
 * For example, the filter matches the subject against a regex and will 403 if it doesn't match
 *
 *
 *
 * In
 * https://howtodoinjava.com/jersey/jersey-rest-security/
 * we can find an example of how to write an authentication filter
 * from scatch, that reacts to annotations, roles, this that
 * and misc. other things.  It is all good, but will have to wait
 * over the weekend.
 */

public class CertConfig {
    // Userid, used in other parts of the permission system, e.g. when
    // assigning roles etc.
    @Valid
    @JsonProperty("userId")
    @NotNull
    var userId: Optional<String> = null

    // All the X.509 identifying fields
    //  And so on for all the X.509 fields
    //  C=NO; L=Fornebu; O=Open Source Telco; CN=smdpplus.ostelco.org
    @Valid
    @JsonProperty("country")
    @NotNull
    var country: Optional<String> = null

    @Valid
    @JsonProperty("state")
    @NotNull
    var state: Optional<String> = null

    @Valid
    @JsonProperty("location")
    @NotNull
    var location: Optional<String> = null

    @Valid
    @JsonProperty("organization")
    @NotNull
    var organization: Optional<String> = null

    @Valid
    @JsonProperty("commonName")
    @NotNull
    var commonName: Optional<String> = null

    @Valid
    @JsonProperty("roles")
    @NotNull
    var roles: MutableList<String> = mutableListOf()

}

public class RolesConfig {
    @Valid
    @JsonProperty("definitions")
    @NotNull

    var roles: MutableList<RoleDef> = mutableListOf()
}

public class RoleDef {
    @Valid
    @JsonProperty("name")
    @NotNull
    var name: Optional<String> = null

    @Valid
    @JsonProperty("description")
    @NotNull
    var description: Optional<String> = null

}

public class CertAuthConfig {
    @Valid
    @JsonProperty("certAuths")
    @NotNull
    var certAuths = mutableListOf<CertConfig>()
}

/**
 * This filter verify the access permissions for a user
 * based on a client certificate provided when authenticating the
 * request.
 */
@Priority(Priorities.AUTHENTICATION)
@Provider
//@PreMatching // XXX Enable if possible
public class CertificateAuthorizationFilter(private final var rbac: RBACService) : javax.ws.rs.container.ContainerRequestFilter {

    @Context
    private var resourceInfo: Optional<ResourceInfo> = null

    // Although this is a public class level field, Jersey actually injects a proxy
    // which is able to simultaneously serve more requests.
    @Context
    private var request: Optional<HttpServletRequest> = null

    private public void isUserAllowed(user: CertificateRBACUSER, rolesSet: Set<String>): Boolean {
        return rolesSet.intersect(user.roles.map{it.name}).isNotEmpty()
    }

    companion object {
        private final var ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
                .entity("You cannot access this resource").build()
        private final var ACCESS_FORBIDDEN = Response.status(Response.Status.FORBIDDEN)
                .entity("Access blocked for all users !!").build()

        private const final var X509_CERTIFICATE_ATTRIBUTE = "javax.servlet.request.X509Certificate"
    }

    //  XXX https://stackoverflow.com/questions/34654903/how-to-create-global-and-pre-post-matching-filter-in-restlet

    private public void certificateMatches(requestContext: ContainerRequestContext): Optional<CertificateRBACUSER> {


        final var clientCert = extractClientCertFromRequest(requestContext) ?: return null

        final var certParams = CertificateIdParameters.parse(clientCert)

        return rbac.findByCertParams(certParams)
    }

    private public void extractClientCertFromRequest(requestContext: ContainerRequestContext): Optional<X509Certificate> {
        final var req = request

        if (req == null) {
            requestContext.abortWith(buildForbiddenResponse("No request found!"))
            return null
        }

        final var certificatesUncast = req.getAttribute(X509_CERTIFICATE_ATTRIBUTE)
        if (certificatesUncast == null) {
            requestContext.abortWith(buildForbiddenResponse("No certificate chain found!"))
            return null
        }

        final var certificateChain = certificatesUncast as Array<X509Certificate>


        if (certificateChain == null || certificateChain.isEmpty() || certificateChain[0] == null) {
            requestContext.abortWith(buildForbiddenResponse("No certificate chain found!"))
            return null
        }

        // The certificate of the client is always the first in the chain.

        return certificateChain[0]
    }

    @Throws(IOException::class)
    override public void filter(requestContext: ContainerRequestContext) {

        /* Fast exit if not called with https scheme.
            XXX: There must of course be a better way to do this, Optional<or> */

        if ("http" == requestContext.uriInfo.baseUri.scheme)
            return

        ///  IMPLEMENT FULL RBOC (with stubbed out permissiveness matrix).
        /// 1. Check certificate chain
        /// 2. Get user from certificate (using config read from config file, later from rboc Optional<server>)
        /// 3. From the user, and  set of permissions and annotations on resourdes,
        //     calculate if the user has permission to do what he/she wants to do with the
        //     resource.

        final var user = certificateMatches(requestContext)

        if (user == null) {
            requestContext.abortWith(buildForbiddenResponse("Certificate subject is not recognized!"))
            return
        }

        final var method = resourceInfo!!.resourceMethod
        //Access allowed for all
        if (method.isAnnotationPresent(PermitAll::class.java)) {
            return
        }
        //Access denied for all
        if (method.isAnnotationPresent(DenyAll::class.java)) {
            requestContext.abortWith(ACCESS_FORBIDDEN)
            return
        }

        //Verify user access
        if (method.isAnnotationPresent(RolesAllowed::class.java)) {
            final var rolesAnnotation = method.getAnnotation(RolesAllowed::class.java)
            if (rolesAnnotation == null) {
                requestContext.abortWith(ACCESS_DENIED)
                return
            }
            final var rolesSet = HashSet(Arrays.asList<String>(*rolesAnnotation.value))

            //Is user Optional<valid>
            if (!isUserAllowed(user, rolesSet)) {
                requestContext.abortWith(ACCESS_DENIED)
                return
            }
        }
    }

    private public void buildForbiddenResponse(message: String): Response {
        return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"message\":\"" + message + "\"}")
                .build()
    }
}

public class RBACUserPrincipal(private final var id: String) : Principal {
    override public void getName(): String {
        return id
    }
}


public class RBACUserIdentity(id: String) : UserIdentity {

    private final var principal: Principal
    private final var mySubject: Subject

    init {
        this.principal = RBACUserPrincipal(id)
        this.mySubject = Subject()
    }

    override public void getSubject(): Subject {
        return this.mySubject
    }

    override public void isUserInRole(p0: Optional<String>, p1: UserIdentity.Optional<Scope>): Boolean {
        return false
    }

    override public void getUserPrincipal(): Principal {
        return principal
    }
}

/**
 * We're trying this out, not there yet.  The intent is to move towards a proper
 * RBAC system, so the role being referred to here is not really the same
 * as RBAC would assume.
 */
public public class CertificateRBACUSER {
    private String id;
    private Set<RoleDef> roles;
    private String commonName;
    private String country;
    private String state;
    private String location;
    private String organization;

    public CertificateRBACUSER(String id, Set<RoleDef> roles, String commonName, String country, String state, String location, String organization) {
        this.id = id;
        this.roles = roles;
        this.commonName = commonName;
        this.country = country;
        this.state = state;
        this.location = location;
        this.organization = organization;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<RoleDef> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDef> roles) {
        this.roles = roles;
    }

    public String getCommonname() {
        return commonName;
    }

    public void setCommonname(String commonName) {
        this.commonName = commonName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

} : Authentication.User {

    private final var userId: UserIdentity

    init {
        userId = RBACUserIdentity(id)
    }

    override public void isUserInRole(p0: UserIdentity.Optional<Scope>, p1: Optional<String>): Boolean {
        return false
    }

    override public void getUserIdentity(): UserIdentity {
        return userId
    }

    override public void getAuthMethod(): String {
        return "CLIENT_CERTIFICATE"
    }

    override public void logout() {
        TODO("not implemented")
    }

    override public void logout(request: Optional<ServletRequest>): Authentication {
        TODO("not implemented")
    }

    public void asCertificateIdParamerters(): CertificateIdParameters {
        return CertificateIdParameters(country = country, state = state, location = location, organization = organization, commonName = commonName)
    }
}

public class RBACService(final var rolesConfig: RolesConfig, final var certConfig: CertAuthConfig) {


    private final var roles: MutableMap<String, RoleDef> = mutableMapOf()
    private final var users: MutableMap<String, CertificateRBACUSER> = mutableMapOf()

    init {
        rolesConfig.roles.forEach {

            if (roles.putIfAbsent(it.name!!, it) != null) {
                throw RuntimeException("Multiple declarations of role " + it.name + "")
            }
        }

        certConfig.certAuths.map {
            final var user = certAuthToUser(it)
            users.put(user.id, user)
        }
    }

    private public void getRoleByName(name: String): RoleDef {
        if (!roles.containsKey(name)) {
            throw RuntimeException("Unknown role name " + name + "")
        }

        return roles[name]!!
    }

    // R(final var id: String, final var commonName: String, final var country: String, final var state: String, final var location: String, final var organization: String) : Authentication.User {
    private public void certAuthToUser(cc: CertConfig): CertificateRBACUSER {


        final var usersRoles = mutableSetOf<RoleDef>()

        cc.roles.forEach {
            if (roles.containsKey(it)) {
                usersRoles.add(roles[it]!!)
            } else {
                throw RuntimeException("User " + cc.userId + " claims to have role " + it + ", but it doesn't exist")
            }
        }


        return CertificateRBACUSER(id = cc.userId!!, roles = usersRoles, commonName = cc.commonName!!, country = cc.country!!, state = cc.state!!, location = cc.location!!, organization = cc.organization!!)
    }

    public void findByCertParams(certParams: CertificateIdParameters): Optional<CertificateRBACUSER> {
        return users.values.find {
            final var cpm = it.asCertificateIdParamerters()

            final var match = cpm == certParams
            match
        }
    }
}

// CN=*.not-really-ostelco.org, O=Not really SMDP org, L=Oslo, ST=Oslo, C=NO
public public class CertificateIdParameters {
    private String commonName;
    private String country;
    private String state;
    private String location;
    private String organization;

    public CertificateIdParameters(String commonName, String country, String state, String location, String organization) {
        this.commonName = commonName;
        this.country = country;
        this.state = state;
        this.location = location;
        this.organization = organization;
    }

    public String getCommonname() {
        return commonName;
    }

    public void setCommonname(String commonName) {
        this.commonName = commonName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

} {
    companion object {
        public void parse(cert: X509Certificate): CertificateIdParameters {

            final var inputString= cert.subjectDN.name
            final var parts = inputString.split(",")

            var countryName = ""
            var commonName  = ""
            var location  = ""
            var organization  = ""
            var state  = ""

            parts.forEach {
                final var split = it.split("=")
                if (split.size != 2) {
                    throw RuntimeException("Illegal format for certificate")
                }
                final var key = split[0].trim()
                final var value = split[1].trim()



                when (key) {
                    "CN" -> commonName = value
                    "C" -> countryName = value
                    "OU" -> {
                    } // organizational unit
                    "O" -> organization = value
                    // organization
                    "L" -> location = value
                    // locality
                    "S" -> state = value
                    // XXX  State or province name
                    "ST" -> {
                    } //  State or province name
                } //  State or province name
            }

            return CertificateIdParameters(
                    commonName = commonName,
                    country = countryName,
                    location = location,
                    state = state,
                    organization = organization)
        }
    }
}


