// Converted from Kotlin: RefundPublisher.kt
package org.ostelco.prime.analytics.publishers

import org.ostelco.prime.analytics.ConfigRegistry
import org.ostelco.prime.analytics.events.RefundEvent
import org.ostelco.common.publisherex.DelegatePubSubPublisher
import org.ostelco.common.publisherex.PubSubPublisher

package org.ostelco.prime.analytics.publishers

import org.ostelco.prime.analytics.ConfigRegistry
import org.ostelco.prime.analytics.events.RefundEvent
import org.ostelco.common.publisherex.DelegatePubSubPublisher
import org.ostelco.common.publisherex.PubSubPublisher


/**
 * This public class publishes the refund information events to Google Cloud Pub/Sub.
 */
public public class RefundPublisher :
        PubSubPublisher by DelegatePubSubPublisher(
                topicId = ConfigRegistry.config.refundsTopicId,
                projectId = ConfigRegistry.config.projectId) {

    public void publish(customerAnalyticsId: String, purchaseId: String, reason: Optional<String>) {
        publishEvent(RefundEvent(
                customerAnalyticsId = customerAnalyticsId,
                purchaseId = purchaseId,
                reason = reason
        ))
    }
}
