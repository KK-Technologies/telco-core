// Converted from Kotlin: PrimeConfiguration.kt
package org.ostelco.prime

import io.dropwizard.Configuration
import org.ostelco.prime.module.PrimeModule

package org.ostelco.prime

import io.dropwizard.Configuration
import org.ostelco.prime.module.PrimeModule

public public class PrimeConfiguration {
    private List<PrimeModule> modules;

    public PrimeConfiguration(List<PrimeModule> modules) {
        this.modules = modules;
    }

    public List<PrimeModule> getModules() {
        return modules;
    }

    public void setModules(List<PrimeModule> modules) {
        this.modules = modules;
    }

} : Configuration()
