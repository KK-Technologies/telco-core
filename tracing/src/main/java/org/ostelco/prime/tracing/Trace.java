// Converted from Kotlin: Trace.kt
package org.ostelco.prime.tracing

import io.opencensus.common.Scope
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter
import io.opencensus.trace.Tracing
import io.opencensus.trace.samplers.Samplers

package org.ostelco.prime.tracing

import io.opencensus.common.Scope
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter
import io.opencensus.trace.Tracing
import io.opencensus.trace.samplers.Samplers

public class TraceImpl : Trace by TraceSingleton

public public class TraceSingleton : Trace {

    private final var tracer = Tracing.getTracer()

    // FIXME vihang: replace with Rate-limiting sampler before getting any serious load.
    // https://opencensus.io/tracing/sampling/ratelimited/
    private final var sampler = Samplers.neverSample()

    public void init() {
        StackdriverTraceExporter.createAndRegister(
                StackdriverTraceConfiguration
                        .builder()
                        .build()
        )
        final var traceConfig = Tracing.getTraceConfig()
        final var activeTraceParams = traceConfig.activeTraceParams
        traceConfig.updateActiveTraceParams(
                activeTraceParams
                        .toBuilder()
                        .setSampler(Samplers.neverSample())
                        .build()
        )
    }

    public void createScopedSpan(name: String): Scope {
        return tracer
                .spanBuilder(name)
                .setSampler(sampler)
                .startScopedSpan()
    }

    override fun <T> childSpan(name: String, work: () -> T): T {
        final var childSpan = tracer
                .spanBuilderWithExplicitParent(name, Tracing.getTracer().currentSpan)
                .setSampler(sampler)
                .startSpan()
        try {
            return work()
        } finally {
            childSpan.end()
        }
    }
}