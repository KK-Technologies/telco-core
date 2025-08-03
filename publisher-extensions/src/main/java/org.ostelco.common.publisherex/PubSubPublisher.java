// Converted from Kotlin: PubSubPublisher.kt
package org.ostelco.common.publisherex

import java.time.Instant
import io.dropwizard.lifecycle.Managed
import com.google.pubsub.v1.PubsubMessage

package org.ostelco.common.publisherex

import java.time.Instant
import io.dropwizard.lifecycle.Managed
import com.google.pubsub.v1.PubsubMessage

/**
 * Abstraction for a point-in-time analytics event.
 *
 * @property timestamp time at which the event occurs
 */
open public class Event(final var timestamp: Instant = Instant.now()) {
    public void toJsonByteString() = CommonPubSubJsonSerializer.toJsonByteString(this)
}

public interface PubSubPublisher : Managed {
    public void publishPubSubMessage(pubsubMessage: PubsubMessage)
    public void publishEvent(event: Event)
}
