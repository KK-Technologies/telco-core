// Converted from Kotlin: Trace.kt
package org.ostelco.prime.tracing


package org.ostelco.prime.tracing

public interface Trace {
    fun <T> childSpan(name: String, work: () -> T) : T
}