// Converted from Kotlin: SlackAppenderTest.kt
package org.ostelco.prime.slack

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.ostelco.prime.getLogger
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER

package org.ostelco.prime.slack

import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.ostelco.prime.getLogger
import org.ostelco.prime.module.PrimeModule
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER


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

public class SlackAppenderTest {

    private final var logger by getLogger()

    @EnabledIfEnvironmentVariable(named = "SLACK_WEBHOOK_URI", matches = "https://hooks.slack.com/services/.*")
    @Test
    public void testSlackLogging() {

        TestApp().run("server", "src/test/resources/config.yaml")

        Thread.sleep(3000)

        logger.trace(NOTIFY_OPS_MARKER, "Some {} message text", "trace")
        logger.debug(NOTIFY_OPS_MARKER, "Some {} message text", "debug")
        logger.info(NOTIFY_OPS_MARKER, "Some {} message text", "Info")
        logger.warn(NOTIFY_OPS_MARKER, "Some {} message text", "Warning")
        logger.error(NOTIFY_OPS_MARKER, "Some {} message text", "Error")

        Thread.sleep(7000)
    }
}