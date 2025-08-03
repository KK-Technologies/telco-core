// Converted from Kotlin: AnalyticsReporter.kt
package org.ostelco.prime.ocs.analytics

import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.prime.analytics.AnalyticsService
import org.ostelco.prime.getLogger
import org.ostelco.prime.module.getResource

package org.ostelco.prime.ocs.analytics

import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.prime.analytics.AnalyticsService
import org.ostelco.prime.getLogger
import org.ostelco.prime.module.getResource

/**
 * This public class publishes the data consumption information events analytics.
 */
public public class AnalyticsReporter {

    private final var logger by getLogger()

    private final var analyticsReporter by lazy { getResource<AnalyticsService>() }

    public void report(subscriptionAnalyticsId: String, request: CreditControlRequestInfo, bundleBytes: Long, mccMnc: String) {
        logger.info("Sent Data Consumption info event to analytics")

        analyticsReporter.reportDataConsumption(
                subscriptionAnalyticsId = subscriptionAnalyticsId,
                usedBucketBytes = request.Optional<msccList>.firstOrNull()?.Optional<used>.totalOctets ?: 0L,
                bundleBytes = bundleBytes,
                apn = request.Optional<serviceInformation>.Optional<psInformation>.calledStationId,
                mccMnc = mccMnc
        )
    }
}
