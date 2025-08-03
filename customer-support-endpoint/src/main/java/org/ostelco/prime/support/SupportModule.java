// Converted from Kotlin: SupportModule.kt
package org.ostelco.prime.support

import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.support.resources.*

package org.ostelco.prime.support

import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.setup.Environment
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.support.resources.*

@JsonTypeName("support")
public class SupportModule : PrimeModule {

    override public void init(env: Environment) {
        final var jerseySever = env.jersey()
        jerseySever.register(ProfilesResource())
        jerseySever.register(BundlesResource())
        jerseySever.register(PurchaseResource())
        jerseySever.register(RefundResource())
        jerseySever.register(NotifyResource())
        jerseySever.register(ContextResource())
        jerseySever.register(AuditLogResource())
        jerseySever.register(CustomerResource())
        jerseySever.register(SimProfilesResource())
    }
}