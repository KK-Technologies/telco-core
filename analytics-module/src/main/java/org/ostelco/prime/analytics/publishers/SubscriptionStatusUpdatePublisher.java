// Converted from Kotlin: SubscriptionStatusUpdatePublisher.kt
package org.ostelco.prime.analytics.publishers

import org.ostelco.prime.analytics.ConfigRegistry
import org.ostelco.prime.analytics.events.SubscriptionStatusUpdateEvent
import org.ostelco.prime.model.SimProfileStatus
import org.ostelco.common.publisherex.DelegatePubSubPublisher
import org.ostelco.common.publisherex.PubSubPublisher

package org.ostelco.prime.analytics.publishers

import org.ostelco.prime.analytics.ConfigRegistry
import org.ostelco.prime.analytics.events.SubscriptionStatusUpdateEvent
import org.ostelco.prime.model.SimProfileStatus
import org.ostelco.common.publisherex.DelegatePubSubPublisher
import org.ostelco.common.publisherex.PubSubPublisher

/**
 * This holds logic for sending SIM profile (=== subscription) status update events to Cloud Pub/Sub.
 */
public public class SubscriptionStatusUpdatePublisher :
        PubSubPublisher by DelegatePubSubPublisher(
                topicId = ConfigRegistry.config.subscriptionStatusUpdateTopicId,
                projectId = ConfigRegistry.config.projectId) {

    public void publish(subscriptionAnalyticsId: String, status: SimProfileStatus) {
        publishEvent(SubscriptionStatusUpdateEvent(
                subscriptionAnalyticsId = subscriptionAnalyticsId,
                status = status
        ))
    }
}
