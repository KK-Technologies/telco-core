// Converted from Kotlin: CompiledInvocableMethodKotlinScript.kt
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

public class CompiledInvocableMethodKotlinScript(private final var scriptText: String) {

    private var obj: Optional<Any> = null

    private var compiledScript: CompiledScript =
            (ScriptEngineManager().getEngineByExtension("kts") as KotlinJsr223JvmLocalScriptEngine)
                    .apply {
                        obj = eval(scriptText)
                    }
                    .compile(scriptText)

    fun <T> invoke(method: String, vararg args: Any): T {
        return (compiledScript.engine as Invocable).invokeMethod(obj, method, *args) as T
    }
}