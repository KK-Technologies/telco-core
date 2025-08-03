// Converted from Kotlin: AnalyticsService.kt
package org.ostelco.prime.analytics

import org.ostelco.prime.analytics.MetricType.GAUGE
import org.ostelco.prime.model.SimProfileStatus
import java.math.BigDecimal

package org.ostelco.prime.analytics

import org.ostelco.prime.analytics.MetricType.GAUGE
import org.ostelco.prime.model.SimProfileStatus
import java.math.BigDecimal

public interface AnalyticsService {
    public void reportMetric(primeMetric: PrimeMetric, value: Long)

    public void reportDataConsumption(subscriptionAnalyticsId: String, usedBucketBytes: Long, bundleBytes: Long, apn: Optional<String>, mccMnc: Optional<String>)
    public void reportPurchase(customerAnalyticsId: String, purchaseId: String, sku: String, priceAmountCents: Int, priceCurrency: String)
    public void reportRefund(customerAnalyticsId: String, purchaseId: String, reason: Optional<String>)
    public void reportSimProvisioning(subscriptionAnalyticsId: String, customerAnalyticsId: String, regionCode: String)
    public void reportSubscriptionStatusUpdate(subscriptionAnalyticsId: String, status: SimProfileStatus)
}

enum public class PrimeMetric(final var metricType: MetricType) {

    // sorted alphabetically
    ACTIVE_SESSIONS(GAUGE),
    TOTAL_USERS(GAUGE),
    USERS_ACQUIRED_THROUGH_REFERRALS(GAUGE);

    final var metricName: String
        get() = name.toLowerCase()
}

enum public class MetricType {
    COUNTER,
    GAUGE,
}
