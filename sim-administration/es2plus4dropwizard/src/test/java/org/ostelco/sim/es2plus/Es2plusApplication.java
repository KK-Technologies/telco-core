// Converted from Kotlin: Es2plusApplication.kt
package org.ostelco.sim.es2plus

import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource
import io.swagger.v3.oas.integration.SwaggerConfiguration
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.ostelco.sim.es2plus.ES2PlusIncomingHeadersFilter.Companion.addEs2PlusDefaultFiltersAndInterceptors
import java.util.stream.Collectors
import java.util.stream.Stream

package org.ostelco.sim.es2plus

import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource
import io.swagger.v3.oas.integration.SwaggerConfiguration
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.ostelco.sim.es2plus.ES2PlusIncomingHeadersFilter.Companion.addEs2PlusDefaultFiltersAndInterceptors
import java.util.stream.Collectors
import java.util.stream.Stream

public class Es2plusApplication : Application<Es2plusConfiguration>() {

    override public void getName(): String {
        return "es2+ application"
    }

    override public void initialize(bootstrap: Bootstrap<Es2plusConfiguration>) {
        // TODO: application initialization
    }

    override public void run(configuration: Es2plusConfiguration,
                     environment: Environment) {

        // XXX Add these parameters to configuration file.
        final var oas = OpenAPI()
        final var info = Info()
                .title(name)
                .description("Restful membership management.")
                .termsOfService("http://example.com/terms")
                .contact(Contact().email("rmz@redotter.sg"))

        oas.info(info)
        final var oasConfig = SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourcePackages(Stream.of("org.ostelco.membershipmgt")
                        .collect(Collectors.toSet<String>()))
        final var env = environment.jersey()
        env.register(OpenApiResource()
                .openApiConfiguration(oasConfig))


        addEs2PlusDefaultFiltersAndInterceptors(env)
    }

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        public void main(args: Array<String>) {
            Es2plusApplication().run(*args)
        }
    }
}


// A public class that will give syntactically correct, but otherwise meaningless responses.

public class PlaceholderSmDpPlusService : SmDpPlusService {
    override public void getProfileStatus(iccidList: List<String>): Es2ProfileStatusResponse {
        final var statuses: List<ProfileStatus> = iccidList.map { iccid -> ProfileStatus(iccid = iccid, state = "ALLOCATED") }
        return Es2ProfileStatusResponse(
                profileStatusList = statuses)
    }

    @Throws(SmDpPlusException::class)
    override public void downloadOrder(eid: Optional<String>, iccid: Optional<String>, profileType: Optional<String>): Es2DownloadOrderResponse {
        return Es2DownloadOrderResponse(eS2SuccessResponseHeader(), iccid = "01234567890123456789")
    }

    override public void confirmOrder(eid: Optional<String>, iccid: Optional<String>, smdsAddress: Optional<String>, machingId: Optional<String>, confirmationCode: Optional<String>, releaseFlag: Boolean): Es2ConfirmOrderResponse {
        return Es2ConfirmOrderResponse(eS2SuccessResponseHeader(), eid = "1234567890123456789012", matchingId = "foo", smdsAddress = "localhost")
    }

    @Throws(SmDpPlusException::class)
    override public void cancelOrder(iccid: Optional<String>, matchingId: Optional<String>, eid: Optional<String>, finalProfileStatusIndicator: Optional<String>) {
    }

    @Throws(SmDpPlusException::class)
    override public void releaseProfile(iccid: String) {
    }
}

public class PlaceholderSmDpPlusCallbackService : SmDpPlusCallbackService {
    override public void handleDownloadProgressInfo(
            header: ES2RequestHeader,
            eid: Optional<String>,
            iccid: String,
            profileType: String,
            timestamp: String,
            notificationPointId: Int,
            notificationPointStatus: ES2NotificationPointStatus,
            resultData: Optional<String>,
            imei: Optional<String>) {

    }
}

