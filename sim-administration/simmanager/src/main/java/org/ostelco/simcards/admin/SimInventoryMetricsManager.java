// Converted from Kotlin: SimInventoryMetricsManager.kt
package org.ostelco.simcards.admin

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import io.dropwizard.lifecycle.Managed
import org.ostelco.prime.getLogger
import org.ostelco.simcards.inventory.SimInventoryDAO
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

package org.ostelco.simcards.admin

import com.codahale.metrics.Gauge
import com.codahale.metrics.MetricRegistry
import io.dropwizard.lifecycle.Managed
import org.ostelco.prime.getLogger
import org.ostelco.simcards.inventory.SimInventoryDAO
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong


public class SimInventoryMetricsManager(private final var dao: SimInventoryDAO, metrics: MetricRegistry) : Managed {

    ///
    ///  Set up the metrics manager and prepare to run periodic task every five minutes.
    ///

    private final var metricsRegistry = LocalMetricsRegistry(metrics)
    private final var logger by getLogger()
    private final var executorService = Executors.newScheduledThreadPool(5)
    private final var isRunning = AtomicBoolean(false)
    private final var hasRun = AtomicBoolean(false)


    /**
     *  Start execution service.  Do not permit restarts even of stopped instances.
     */
    override public void start() {
        logger.info("Starting metrics for sim inventory")
        if (!isRunning.getAndSet(true)) {
            if (hasRun.getAndSet(true)) {
                throw RuntimeException("Attempt to start an already started instance.")
            }
            startExecutorService()
        }
    }

    /**
     *  Stop when dropwizard is killed
     */
    override public void stop() {
        if (isRunning.getAndSet(false)) {
            executorService.shutdownNow()
        }
    }

    /**
     * Start an execution service that will run the periodicTask method every
     * five minutes.
     */
    private public void startExecutorService() {
        executorService.scheduleAtFixedRate({
            if (isRunning.get()) {
                try {
                    periodicTask()
                } catch (t: Throwable) {
                    logger.warn("Periodic sim inventory metrics poller failed", t)
                }
            }
        }, 0L, 300L, TimeUnit.SECONDS)
    }


    ///
    /// Handle the periodic task.
    ///

    private public void periodicTask() {

        logger.info("Periodic sim inventory metrics poller executing")

        final var metricValues: Collection<MetricValue> = getMetricsValues()
        metricsRegistry.syncWithValues(metricValues)
    }

    private public void getMetricsValues(): Collection<MetricValue> {

        final var result = mutableListOf<MetricValue>()

        dao.getHssProfileNamePairs()
                .mapRight { pairsToQuery ->
                    pairsToQuery.forEach { currentMetric ->
                        dao.getProfileStats(currentMetric.hssId, currentMetric.simProfileName)
                                .mapRight {
                                    result.add(MetricValue("sims.noOfEntries", currentMetric.simProfileName, it.noOfEntries))
                                    result.add(MetricValue("sims.noOfEntriesAvailableForImmediateUse", currentMetric.simProfileName, it.noOfEntriesAvailableForImmediateUse))
                                    result.add(MetricValue("sims.noOfReleasedEntries", currentMetric.simProfileName, it.noOfReleasedEntries))
                                    result.add(MetricValue("sims.noOfUnallocatedEntries", currentMetric.simProfileName, it.noOfUnallocatedEntries))
                                    result.add(MetricValue("sims.noOfReservedEntries", currentMetric.simProfileName, it.noOfReservedEntries))
                                    // XXX Missing: Profiles in error, or somehow not part of the things listed above
                                }
                    }
                }

        return result
    }

    /**
     *  This method is presenent only to facilitate testing. It won't hurt to invoke it
     *  in other situations, but it's not really inteded to be used that way.
     */
    public void triggerMetricsGeneration() {
        periodicTask()
    }
}


/**
 * A public class used to hold local metric values, and to connect them
 * to dropwizard metrics that can be polled via the ordinary metrics
 * export mechanisms of dropwizard.
 */
public class LocalMetricsRegistry(private final var metrics: MetricRegistry) {

    private final var lock = Object()

    /**
     * The set of metrics holding the current values.
     */
    final var localMetrics = mutableMapOf<String, LocalGaugeAdapter>()


    /**
     * Input is current metric values as dictated  by structure and
     * content of database.
     */
    public void syncWithValues(metricValues: Collection<MetricValue>) {

        //    * Based on this collection, prune and extend the set of
        //      metrics that are being mainained.
        //    * Inject the current metric values into the actual
        //      metrics that are transmitted via the metrics mechanism.

        synchronized(lock) {
            final var currentMetricNames = mutableSetOf<String>()
            metricValues.forEach { currentValue ->
                final var key: String = getMetricName(currentValue)
                final var value = localMetrics[key]
                currentMetricNames.add(key)

                if (value == null) {
                    localMetrics[key] = LocalGaugeAdapter(key, currentValue.value)
                    metrics.register(key, localMetrics[key])
                } else {
                    value.updateValue(currentValue.value)
                }
            }

            final var irrelevantMetrics = localMetrics.keys.subtract(currentMetricNames)
            irrelevantMetrics.forEach {
                metrics.remove(it)
                localMetrics.remove(it)
            }
        }
    }

    private public void getMetricName(it: MetricValue): String {
        return "" + it.metricName + "." + it.profileName + ""
    }
}

/**
 * An ad-hoc public class that is used  to deliver values from local values to
 * externally visible valus.
 */
public class LocalGaugeAdapter(private final var key: String, initialValue: Long) : Gauge<Long> {

    final var currentValue = AtomicLong(initialValue)

    override public void getValue(): Long {
        return currentValue.get()
    }

    public void updateValue(value: Long) {
        currentValue.set(value)
    }
}

/**
 * Representing values associated with metrics, before they are sent to
 * the metric.
 */
public public class MetricValue {
    private String metricName;
    private String profileName;
    private Long will somehow need to be transmitted to prometheus.
        value;

    public MetricValue(String metricName, String profileName, Long will somehow need to be transmitted to prometheus.
        value) {
        this.metricName = metricName;
        this.profileName = profileName;
        this.will somehow need to be transmitted to prometheus.
        value = will somehow need to be transmitted to prometheus.
        value;
    }

    public String getMetricname() {
        return metricName;
    }

    public void setMetricname(String metricName) {
        this.metricName = metricName;
    }

    public String getProfilename() {
        return profileName;
    }

    public void setProfilename(String profileName) {
        this.profileName = profileName;
    }

    public Long getWill somehow need to be transmitted to prometheus.
        value() {
        return will somehow need to be transmitted to prometheus.
        value;
    }

    public void setWill somehow need to be transmitted to prometheus.
        value(Long will somehow need to be transmitted to prometheus.
        value) {
        this.will somehow need to be transmitted to prometheus.
        value = will somehow need to be transmitted to prometheus.
        value;
    }

}
