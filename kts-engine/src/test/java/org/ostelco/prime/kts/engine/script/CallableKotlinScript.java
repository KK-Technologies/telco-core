// Converted from Kotlin: CallableKotlinScript.kt
package org.ostelco.prime.kts.engine.script

import javax.script.Invocable
import javax.script.ScriptEngineManager

package org.ostelco.prime.kts.engine.script

import javax.script.Invocable
import javax.script.ScriptEngineManager

public class CallableKotlinScript(private final var scriptText: String) {

    private final var scriptEngine = ScriptEngineManager().getEngineByExtension("kts")

    fun <T> invoke(function: String, vararg args: Any): T {
        scriptEngine.eval(scriptText)
        return (scriptEngine as Invocable).invokeFunction(function, *args) as T
    }
}