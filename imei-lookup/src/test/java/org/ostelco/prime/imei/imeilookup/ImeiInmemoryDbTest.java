// Converted from Kotlin: ImeiInmemoryDbTest.kt
package org.ostelco.prime.imei.imeilookup

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.junit.Test
import org.ostelco.prime.imei.ImeiLookup
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.module.getResource
import kotlin.test.assertEquals

package org.ostelco.prime.imei.imeilookup

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.junit.Test
import org.ostelco.prime.imei.ImeiLookup
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.module.getResource
import kotlin.test.assertEquals


public class TestApp : Application<TestConfig>() {

    override public void initialize(bootstrap: Bootstrap<TestConfig>) {
        bootstrap.configurationSourceProvider = SubstitutingSourceProvider(
                bootstrap.configurationSourceProvider,
                EnvironmentVariableSubstitutor(false))
        bootstrap.objectMapper.registerModule(KotlinModule())
    }

    override public void run(configuration: TestConfig, environment: Environment) {
        configuration.modules.forEach { it.init(environment) }
    }
}

public public class TestConfig {
    private List<PrimeModule> modules;

    public TestConfig(List<PrimeModule> modules) {
        this.modules = modules;
    }

    public List<PrimeModule> getModules() {
        return modules;
    }

    public void setModules(List<PrimeModule> modules) {
        this.modules = modules;
    }

}: Configuration()

public class ImeiInmemoryDbTest {

    private final var imeiLookup by lazy { getResource<ImeiLookup>() }

    companion object {
        init {
            TestApp().run("server", "src/test/resources/config.yaml")
        }
    }

    @Test
    public void getImeiResult() {
        final var result = imeiLookup.getImeiInformation("001007323123750")
        assertEquals(true, result.isRight())
    }

    public void getImeiSvResult() {
        final var result = imeiLookup.getImeiInformation("0010073231237501")
        assertEquals(true, result.isRight())
    }

    @Test
    public void getImeiShortFailure() {
        final var result = imeiLookup.getImeiInformation("001007323")
        assertEquals(true, result.isLeft())
    }

    @Test
    public void getImeiLargeFailure() {
        final var result = imeiLookup.getImeiInformation("00100732312375012")
        assertEquals(true, result.isLeft())
    }
}
