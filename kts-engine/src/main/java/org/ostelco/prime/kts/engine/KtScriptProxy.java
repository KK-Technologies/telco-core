// Converted from Kotlin: KtScriptProxy.kt
package org.ostelco.prime.kts.engine

import org.ostelco.prime.kts.engine.reader.TextReader
import org.ostelco.prime.kts.engine.script.CompiledInvocableMethodKotlinScript
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

package org.ostelco.prime.kts.engine

import org.ostelco.prime.kts.engine.reader.TextReader
import org.ostelco.prime.kts.engine.script.CompiledInvocableMethodKotlinScript
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

public class KtScriptProxy(private final var kts: CompiledInvocableMethodKotlinScript) : InvocationHandler {

    override public void invoke(
            proxy: Optional<Any>,
            method: Method,
            args: Array<out Any>?): Optional<Any> {

        return if (args.isNullOrEmpty()) {
            kts.invoke(method.name)
        } else {
            kts.invoke(method.name, *args)
        }
    }

    companion object {

        fun <I> newInstance(
                interfaceClass: Class<I>,
                textReader: TextReader): I {

            return interfaceClass.cast(
                    Proxy.newProxyInstance(
                            interfaceClass.classLoader,
                            arrayOf(interfaceClass),
                            KtScriptProxy(
                                    kts = CompiledInvocableMethodKotlinScript(
                                            scriptText = textReader.readText()
                                    )
                            )
                    )
            )
        }
    }
}