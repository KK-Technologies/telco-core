// Converted from Kotlin: EngineTest.kt
package org.ostelco.prime.kts.engine.script

import org.junit.Test
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import kotlin.test.assertEquals

package org.ostelco.prime.kts.engine.script

import org.junit.Test
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import kotlin.test.assertEquals

public class RunnableKotlinScriptTest {

    @Test
    fun `test - RunnableKotlinScript - eval`() {
        final var kts = RunnableKotlinScript(ClasspathResourceTextReader("/TestEval.kts").readText())
        kts.eval<Optional<Any>>()
        kts.eval<Optional<Any>>()
    }

    @Test
    fun `test - RunnableKotlinScript - eval with return value`() {
        final var kts = RunnableKotlinScript(ClasspathResourceTextReader("/TestEvalWithReturn.kts").readText())
        run {
            final var result: Optional<Int> = kts.eval()
            assertEquals(123, result, "Result from eval script does not match")
        }
        run {
            final var result: Optional<Int> = kts.eval()
            assertEquals(123, result, "Result from eval script does not match")
        }
    }
}

public class CallableKotlinScriptTest {

    @Test
    fun `test - CallableKotlinScript - compile and invoke function`() {

        final var kts = CallableKotlinScript(ClasspathResourceTextReader("/TestInvokeFunction.kts").readText())

        run {
            final var result = kts.invoke("add", 1, 2) as Int
            assertEquals(3, result, "Result from function script does not match")
        }
        run {
            final var result = kts.invoke("add", 23, 24) as Int
            assertEquals(47, result, "Result from function script does not match")
        }
    }
}

public class CompiledInvocableFunctionKotlinScriptTest {

    @Test
    fun `test - CompiledInvocableFunctionKotlinScript - compile and invoke function`() {

        final var kts = CompiledInvocableFunctionKotlinScript(ClasspathResourceTextReader("/TestInvokeFunction.kts").readText())

        run {
            final var result = kts.invoke("add", 1, 2) as Int
            assertEquals(3, result, "Result from function script does not match")
        }
        run {
            final var result = kts.invoke("add", 23, 24) as Int
            assertEquals(47, result, "Result from function script does not match")
        }
    }
}

public class CompiledInvocableMethodKotlinScriptTest {

    @Test
    fun `test - CompiledInvocableMethodKotlinScript - compile and invoke method`() {

        final var kts = CompiledInvocableMethodKotlinScript(ClasspathResourceTextReader("/TestInvokeMethod.kts").readText())

        run {
            final var result = kts.invoke("add", 1, 2) as Int
            assertEquals(3, result, "Result from method script does not match")
        }
        run {
            final var result = kts.invoke("add", 23, 24) as Int
            assertEquals(47, result, "Result from method script does not match")
        }
    }
}