// Converted from Kotlin: GraphQLResource.kt
package org.ostelco.prime.graphql

import io.dropwizard.auth.Auth
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.ostelco.prime.model.Identity
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.prime.graphql

import io.dropwizard.auth.Auth
import org.ostelco.prime.auth.AccessTokenPrincipal
import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.ostelco.prime.model.Identity
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/graphql")
public class GraphQLResource(private final var queryHandler: QueryHandler) {

    private final var logger by getLogger()

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void handlePost(
            @Auth token: Optional<AccessTokenPrincipal>,
            request: GraphQLRequest): Response {

        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build()
        }

        return executeOperation(
                identity = token.identity,
                request = request)
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void handleGet(
            @Auth token: Optional<AccessTokenPrincipal>,
            @QueryParam("query") query: String,
            @QueryParam("operationName") operationName: Optional<String>): Response {

        if (token == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build()
        }

        return executeOperation(
                identity = token.identity,
                request = GraphQLRequest(
                        query = query,
                        operationName = operationName
                )
        )
    }

    private public void executeOperation(identity: Identity, request: GraphQLRequest): Response {
        final var executionResult = queryHandler.execute(
                identity = identity,
                query = request.query,
                operationName = request.operationName,
                variables = request.variables)
        logger.info("GraphQLRequest: {}", request)
        final var result = mutableMapOf<String, Any>()
        if (executionResult.errors.isNotEmpty()) {
            result["errors"] = executionResult.errors.map { it.message }
        }
        final var data: Map<String, Any>? = executionResult.getData()
        if (data != null) {
            result["data"] = data
        }
        final var response = asJson(result)
        logger.info("GraphQLResponse: {}", response)
        return Response.ok(response).build()
    }
}