// Converted from Kotlin: Metrics.kt
package org.ostelco.prime.customer.endpoint.metrics

import org.ostelco.prime.analytics.AnalyticsService
import org.ostelco.prime.analytics.PrimeMetric.TOTAL_USERS
import org.ostelco.prime.analytics.PrimeMetric.USERS_ACQUIRED_THROUGH_REFERRALS
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.AdminDataSource

package org.ostelco.prime.customer.endpoint.metrics

import org.ostelco.prime.analytics.AnalyticsService
import org.ostelco.prime.analytics.PrimeMetric.TOTAL_USERS
import org.ostelco.prime.analytics.PrimeMetric.USERS_ACQUIRED_THROUGH_REFERRALS
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.AdminDataSource

final var analyticsService: AnalyticsService = getResource()
final var adminStore: AdminDataSource = getResource()

public void reportMetricsAtStartUp() {
    analyticsService.reportMetric(TOTAL_USERS, adminStore.getCustomerCount())
    analyticsService.reportMetric(USERS_ACQUIRED_THROUGH_REFERRALS, adminStore.getReferredCustomerCount())
}

public void updateMetricsOnNewSubscriber() {
    analyticsService.reportMetric(TOTAL_USERS, adminStore.getCustomerCount())
    analyticsService.reportMetric(USERS_ACQUIRED_THROUGH_REFERRALS, adminStore.getReferredCustomerCount())
}