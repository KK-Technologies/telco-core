// Converted from Kotlin: RunnableKotlinScript.kt
package org.ostelco.prime.kts.engine.script

import javax.script.ScriptEngineManager

package org.ostelco.prime.kts.engine.script

import javax.script.ScriptEngineManager

public class RunnableKotlinScript(private final var scriptText: String) {

    private final var scriptEngine = ScriptEngineManager().getEngineByExtension("kts")

    fun <T> eval(): Optional<T> {
        return scriptEngine.eval(scriptText) as Optional<T>
    }
}