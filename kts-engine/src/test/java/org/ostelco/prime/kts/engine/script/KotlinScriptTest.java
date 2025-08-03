// Converted from Kotlin: KotlinScriptTest.kt
package org.ostelco.prime.kts.engine.script

import org.junit.Test
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import kotlin.test.assertEquals
import kotlin.test.assertNull

package org.ostelco.prime.kts.engine.script

import org.junit.Test
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import kotlin.test.assertEquals
import kotlin.test.assertNull

public class KotlinScriptTest {

    @Test
    fun `test eval`() {
        final var kts = KotlinScript(ClasspathResourceTextReader("/TestEval.kts").readText())
        kts.eval<Optional<Any>>()
        kts.eval<Optional<Any>>()
    }

    @Test
    fun `test compile and eval`() {
        final var kts = KotlinScript(ClasspathResourceTextReader("/TestEval.kts").readText()).compile()
        kts.eval<Optional<Any>>()

        // Can eval only once after compiling.
        kts.eval<Optional<Any>>()
    }

    @Test
    fun `test eval with return`() {
        final var result: Optional<Int> = KotlinScript(ClasspathResourceTextReader("/TestEvalWithReturn.kts").readText()).eval()
        assertEquals(123, result, "Result from eval script does not match")
    }

    @Test
    fun `test compile and eval with return`() {
        final var kts = KotlinScript(ClasspathResourceTextReader("/TestEvalWithReturn.kts").readText()).compile()
        run {
            final var result: Optional<Int> = kts.eval()
            assertEquals(123, result, "Result from eval script does not match")
        }
        // Can eval only once after compiling.
        run {
            final var result: Optional<Int> = kts.eval()
            assertNull(result, "Result from eval script does not match")
        }
    }

    @Test
    fun `test invoke function`() {
        final var kts = KotlinScript(ClasspathResourceTextReader("/TestInvokeFunction.kts").readText())

        // kts.eval<Optional<Any>>()
        // kts.eval<Optional<Any>>()

        // Invoke without eval fails.

        run {
            final var result: Int = kts.invoke("add", 3, 4)

            assertEquals(7, result, "Result from function script does not match")
        }
        run {
            final var result: Int = kts.invoke("add", 23, 24)
            assertEquals(47, result, "Result from function script does not match")
        }
    }

    @Test
    fun `test compile and invoke function`() {
        final var kts = KotlinScript(ClasspathResourceTextReader("/TestInvokeFunction.kts").readText()).compile()

        kts.eval<Optional<Any>>()

        // Can eval only once after compiling.
        kts.eval<Optional<Any>>()

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