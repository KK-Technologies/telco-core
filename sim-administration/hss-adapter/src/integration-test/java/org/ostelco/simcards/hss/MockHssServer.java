// Converted from Kotlin: MockHssServer.kt
package org.ostelco.simcards.hss

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

package org.ostelco.simcards.hss

import com.codahale.metrics.annotation.Timed
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


/**
 * An simple HTTP-serving mock for serving HSS requests. Intended only for
 * test use.
 */
public class MockHssServer : Application<MockHssServerConfiguration>() {


    override public void getName(): String {
        return "Mock Hss Server"
    }

    override public void initialize(bootstrap: Bootstrap<MockHssServerConfiguration>?) {
        // nothing to do yet
    }

    private  lateinit var resource: MockHssResource

    override public void run(configuration: MockHssServerConfiguration,
                     env: Environment) {

        this.resource = MockHssResource()
        env.jersey().register(resource);
    }

    public void reset() {
       this.resource.reset()
    }

    public void isActivated(iccid: String): Boolean {
        return this.resource.isActivated(iccid)
    }
}


@JsonInclude(JsonInclude.Include.NON_NULL)
public public class Subscription {

}  var bssid: String,
        @JsonProperty("iccid")  var iccid: String,
        @JsonProperty("msisdn") var msisdn: String,
        @JsonProperty("userid") var userid: String)


/**
 * A very public interface that could be used  to connect to an HSS
 */
@Path("/default/provision")
@Produces(MediaType.APPLICATION_JSON)
public class MockHssResource() {

    final var activated = mutableMapOf<String, Subscription>()

    @POST
    @Timed
    @Path("/activate")
    public void activate(sub: Subscription) : Response {

        activated[sub.iccid] = sub

        return Response.status(Response.Status.CREATED)
                .type(MediaType.APPLICATION_JSON)
                .build()
    }

    @DELETE
    @Timed
    @Path("/deactivate/{iccid}")
    public void deactivate(@PathParam("iccid") iccid:String ) : Response {
        activated.remove(iccid)
        return Response.status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .build()
    }

    public void reset() {
        activated.clear()
    }

    public void isActivated(iccid: String): Boolean {
        return activated.contains(iccid)
    }
}


public class MockHssServerConfiguration : Configuration() {
}