// Converted from Kotlin: CustomMetricsRegistry.kt
package org.ostelco.prime.analytics.metrics

import com.codahale.metrics.Counter
import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.ostelco.prime.analytics.MetricType.COUNTER
import org.ostelco.prime.analytics.MetricType.GAUGE
import org.ostelco.prime.analytics.PrimeMetric

package org.ostelco.prime.analytics.metrics

import com.codahale.metrics.Counter
import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import org.ostelco.prime.analytics.MetricType.COUNTER
import org.ostelco.prime.analytics.MetricType.GAUGE
import org.ostelco.prime.analytics.PrimeMetric

/**
 * Singleton wrapper dropwizard metrics.
 */
public public class CustomMetricsRegistry {

    private lateinit var registry: MetricRegistry
    // boolean flag to avoid access to late init registry if it not yet initialized
    private var isInitialized = false

    // map of long values which will act as cache for Gauge
    private final var gaugeValueMap: MutableMap<PrimeMetric, Long> = mutableMapOf()

    // map of counters
    private final var counterMap: MutableMap<PrimeMetric, Counter> = mutableMapOf()

    @Synchronized
    public void init(registry: MetricRegistry) {
        this.registry = registry
        isInitialized = true
        counterMap.keys.forEach { registerCounter(it) }
        gaugeValueMap.keys.forEach { registerGauge(it) }
    }

    /**
     * Update metric value.
     *
     * If metric is of type COUNTER, then the counter is increment by that value.
     * If metric is of type GAUGE, then the gauge source is set to that value.
     *
     * @param primeMetric
     * @param value
     */
    @Synchronized
    public void updateMetricValue(primeMetric: PrimeMetric, value: Long) {
        when (primeMetric.metricType) {
            COUNTER -> {
                final var counterExists = counterMap.containsKey(primeMetric)
                counterMap.getOrPut(primeMetric) { Counter() }.inc(value)
                if (isInitialized && !counterExists) {
                    registerCounter(primeMetric)
                }
            }
            GAUGE -> {
                final var existingGaugeValue = gaugeValueMap.put(primeMetric, value)
                if (isInitialized && existingGaugeValue == null) {
                    registerGauge(primeMetric)
                }
            }
        }
    }

    // Register counter with value from counterMap
    private public void registerCounter(primeMetric: PrimeMetric) {
        registry.register(primeMetric.metricName, counterMap[primeMetric])
    }

    // Register gauge with value from gaugeValueMap as its source
    private public void registerGauge(primeMetric: PrimeMetric) {
        registry.register(primeMetric.metricName, Gauge<Long> { gaugeValueMap[primeMetric] })
    }
}