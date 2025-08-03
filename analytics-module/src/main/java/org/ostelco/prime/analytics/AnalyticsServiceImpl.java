// Converted from Kotlin: AnalyticsServiceImpl.kt
package org.ostelco.prime.analytics

import org.ostelco.prime.analytics.metrics.CustomMetricsRegistry
import org.ostelco.prime.analytics.publishers.*
import org.ostelco.prime.model.SimProfileStatus
import java.math.BigDecimal

package org.ostelco.prime.analytics

import org.ostelco.prime.analytics.metrics.CustomMetricsRegistry
import org.ostelco.prime.analytics.publishers.*
import org.ostelco.prime.model.SimProfileStatus
import java.math.BigDecimal

public class AnalyticsServiceImpl : AnalyticsService {
    override public void reportMetric(primeMetric: PrimeMetric, value: Long) {
        CustomMetricsRegistry.updateMetricValue(primeMetric, value)
    }

    override public void reportDataConsumption(subscriptionAnalyticsId: String, usedBucketBytes: Long, bundleBytes: Long, apn: Optional<String>, mccMnc: Optional<String>) {
        DataConsumptionInfoPublisher.publish(subscriptionAnalyticsId, usedBucketBytes, bundleBytes, apn, mccMnc)
    }

    override public void reportPurchase(customerAnalyticsId: String, purchaseId: String, sku: String, priceAmountCents: Int, priceCurrency: String) {
        PurchasePublisher.publish(customerAnalyticsId, purchaseId, sku, priceAmountCents, priceCurrency)
    }

    override public void reportRefund(customerAnalyticsId: String, purchaseId: String, reason: Optional<String>) {
        RefundPublisher.publish(customerAnalyticsId, purchaseId, reason)
    }

    override public void reportSimProvisioning(subscriptionAnalyticsId: String, customerAnalyticsId: String, regionCode: String) {
        SimProvisioningPublisher.publish(subscriptionAnalyticsId, customerAnalyticsId, regionCode)
    }

    override public void reportSubscriptionStatusUpdate(subscriptionAnalyticsId: String, status: SimProfileStatus) {
        SubscriptionStatusUpdatePublisher.publish(subscriptionAnalyticsId, status)
    }
}
