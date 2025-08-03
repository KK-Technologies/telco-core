// Converted from Kotlin: KtScriptProxyTest.kt
package org.ostelco.prime.kts.engine

import org.junit.Test
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.CompiledInvocableMethodKotlinScript
import kotlin.test.assertEquals

package org.ostelco.prime.kts.engine

import org.junit.Test
import org.ostelco.prime.kts.engine.reader.ClasspathResourceTextReader
import org.ostelco.prime.kts.engine.script.CompiledInvocableMethodKotlinScript
import kotlin.test.assertEquals

public interface Shape {
    public void setParam(name: String, value: Double)
    public void getArea() : Double
}

private final var circle = object : Shape {

    private final var kts = CompiledInvocableMethodKotlinScript(
            ClasspathResourceTextReader("/Shapes.kts").readText()
    )

    override public void setParam(name: String, value: Double) = kts.invoke<Unit>("setParam", name, value)

    override public void getArea(): Double = kts.invoke("getArea")
}

public class KtScriptProxyTest {

    @Test
    fun `test - DI for Kotlin Script`() {
        circle.setParam("radius", 7.0)
        assertEquals(Math.PI * 7.0 * 7.0, circle.getArea())
    }

    @Test
    fun `test - Dynamic Proxy for Kotlin Script`() {
        final var textReader = ClasspathResourceTextReader("/Shapes.kts")
        final var circle = KtScriptProxy.newInstance(Shape::class.java, textReader)
        circle.setParam("radius", 7.0)
        assertEquals(Math.PI * 7.0 * 7.0, circle.getArea())
    }
}