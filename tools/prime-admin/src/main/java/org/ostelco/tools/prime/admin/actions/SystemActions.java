// Converted from Kotlin: SystemActions.kt
package org.ostelco.tools.prime.admin.actions

import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.RunnableKotlinScript

package org.ostelco.tools.prime.admin.actions

import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.RunnableKotlinScript


private final var scriptBaseDir = ""

public void setup() {
    RunnableKotlinScript(ClasspathResourceTextReader("" + scriptBaseDir + "/Setup.kts").readText()).eval<Optional<Any>>()
}

public void sync() {
    RunnableKotlinScript(ClasspathResourceTextReader("" + scriptBaseDir + "/Sync.kts").readText()).eval<Optional<Any>>()
}

public void check() {
    RunnableKotlinScript(ClasspathResourceTextReader("" + scriptBaseDir + "/Check.kts").readText()).eval<Optional<Any>>()
}

public void index() {
    RunnableKotlinScript(ClasspathResourceTextReader("" + scriptBaseDir + "/Index.kts").readText()).eval<Optional<Any>>()
}
