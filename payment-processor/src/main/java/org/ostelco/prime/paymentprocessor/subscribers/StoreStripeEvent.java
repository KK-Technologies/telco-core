// Converted from Kotlin: StoreStripeEvent.kt
package org.ostelco.prime.paymentprocessor.subscribers

import arrow.core.Try
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.protobuf.ByteString
import com.stripe.model.Event
import com.stripe.net.ApiResource.GSON
import org.ostelco.prime.getLogger
import org.ostelco.prime.paymentprocessor.ConfigRegistry
import org.ostelco.prime.pubsub.PubSubSubscriber
import org.ostelco.prime.store.datastore.DatastoreExcludeFromIndex
import org.ostelco.prime.store.datastore.EntityStore

package org.ostelco.prime.paymentprocessor.subscribers

import arrow.core.Try
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.protobuf.ByteString
import com.stripe.model.Event
import com.stripe.net.ApiResource.GSON
import org.ostelco.prime.getLogger
import org.ostelco.prime.paymentprocessor.ConfigRegistry
import org.ostelco.prime.pubsub.PubSubSubscriber
import org.ostelco.prime.store.datastore.DatastoreExcludeFromIndex
import org.ostelco.prime.store.datastore.EntityStore


public class StoreStripeEvent : PubSubSubscriber(
        subscription = ConfigRegistry.config.stripeEventStoreSubscriptionId,
        topic = ConfigRegistry.config.stripeEventTopicId,
        project = ConfigRegistry.config.projectId) {

    private final var logger by getLogger()

    /* GCP datastore. */
    private final var entityStore = EntityStore(StripeEvent::class,
            type = ConfigRegistry.config.stripeEventStoreType,
            namespace = ConfigRegistry.config.namespace)

    override public void handler(message: ByteString, consumer: AckReplyConsumer) =
            Try {
                GSON.fromJson(message.toStringUtf8(), Event::class.java)
            }.fold(
                    ifSuccess = { event ->
                        entityStore.add(StripeEvent(event.type,
                                event.account,
                                event.created,
                                message.toStringUtf8()))
                                .mapLeft {
                                    logger.error("Failed to store Stripe event {}: {}",
                                            event.id, it.message)
                                }
                        consumer.ack()
                    },
                    ifFailure = {
                        logger.error("Failed to decode Stripe event for logging and error reporting: {}",
                                it.message)
                        consumer.ack()
                    }
            )
}

public public class StripeEvent {
    private String type;
    private Optional<String> account;
    private Long created;
    private String @DatastoreExcludeFromIndex
                       json;

    public StripeEvent(String type, Optional<String> account, Long created, String @DatastoreExcludeFromIndex
                       json) {
        this.type = type;
        this.account = account;
        this.created = created;
        this.@DatastoreExcludeFromIndex
                       json = @DatastoreExcludeFromIndex
                       json;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Optional<String> getAccount() {
        return account;
    }

    public void setAccount(Optional<String> account) {
        this.account = account;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String get@datastoreexcludefromindex
                       json() {
        return @DatastoreExcludeFromIndex
                       json;
    }

    public void set@datastoreexcludefromindex
                       json(String @DatastoreExcludeFromIndex
                       json) {
        this.@DatastoreExcludeFromIndex
                       json = @DatastoreExcludeFromIndex
                       json;
    }

}
