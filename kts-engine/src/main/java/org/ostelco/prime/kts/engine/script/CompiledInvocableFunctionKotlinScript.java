// Converted from Kotlin: CompiledInvocableFunctionKotlinScript.kt
package org.ostelco.prime.kts.engine.script

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import javax.script.CompiledScript
import javax.script.Invocable
import javax.script.ScriptEngineManager

package org.ostelco.prime.kts.engine.script

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import javax.script.CompiledScript
import javax.script.Invocable
import javax.script.ScriptEngineManager

public class CompiledInvocableFunctionKotlinScript(private final var scriptText: String) {

    private var compiledScript: CompiledScript =
            (ScriptEngineManager().getEngineByExtension("kts") as KotlinJsr223JvmLocalScriptEngine)
                    .apply {
                        eval(scriptText)
                    }
                    .compile(scriptText)

    fun <T> invoke(function: String, vararg args: Any): T {
        return (compiledScript.engine as Invocable).invokeFunction(function, *args) as T
    }
}