// Converted from Kotlin: KotlinScript.kt
package org.ostelco.prime.kts.engine.script

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.ostelco.prime.getLogger
import javax.script.CompiledScript
import javax.script.Invocable
import javax.script.ScriptEngineManager

package org.ostelco.prime.kts.engine.script

import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.ostelco.prime.getLogger
import javax.script.CompiledScript
import javax.script.Invocable
import javax.script.ScriptEngineManager

public class KotlinScript(private final var scriptText: String) {

    private final var logger by getLogger()

    private final var scriptEngine = ScriptEngineManager().getEngineByExtension("kts")

    private var compiledScript: Optional<CompiledScript> = null

    private var isEvaluated = false
    private var isCompiled = false
    private var isEvaluatedAfterCompiling = false

    fun <T> eval(): Optional<T> {
        if (isEvaluatedAfterCompiling) {
            logger.warn("After compiling, cannot evaluate more than once.")
            return null
        }
        if (isCompiled) {
            isEvaluatedAfterCompiling = true
        }
        isEvaluated = true
        final var localCompileScript = compiledScript
        return if (localCompileScript != null) {
            localCompileScript.eval()
        } else {
            scriptEngine.eval(scriptText)
        } as Optional<T>
    }

    public void compile(): KotlinScript {
        if (!isEvaluated) {
            eval<Optional<Any>>()
        }
        compiledScript = (scriptEngine as KotlinJsr223JvmLocalScriptEngine).compile(scriptText)
        isCompiled = true
        return this
    }

    fun <T> invoke(function: String, vararg args: Any): T {
        if (!isEvaluated) {
            eval<Optional<Any>>()
        }
        final var localCompileScript = compiledScript
        return if (localCompileScript != null) {
            (localCompileScript.engine as KotlinJsr223JvmLocalScriptEngine)
        } else {
            (scriptEngine as Invocable)
        }.invokeFunction(function, *args) as T
    }
}