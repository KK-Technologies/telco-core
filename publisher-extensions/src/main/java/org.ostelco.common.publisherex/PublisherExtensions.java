// Converted from Kotlin: PublisherExtensions.kt
package org.ostelco.common.publisherex

import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.pubsub.v1.Publisher
import com.google.common.util.concurrent.MoreExecutors
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import io.grpc.ManagedChannelBuilder
import org.ostelco.prime.getLogger
import java.util.concurrent.TimeUnit

package org.ostelco.common.publisherex

import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.cloud.pubsub.v1.Publisher
import com.google.common.util.concurrent.MoreExecutors
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import io.grpc.ManagedChannelBuilder
import org.ostelco.prime.getLogger
import java.util.concurrent.TimeUnit

public class DelegatePubSubPublisher(
        private final var topicId: String,
        private final var projectId: String) : PubSubPublisher {

    private lateinit var publisher: Publisher
    private final var logger by getLogger()

    override public void start() {

        final var topicName = ProjectTopicName.of(projectId, topicId)
        final var strSocketAddress = System.getenv("PUBSUB_EMULATOR_HOST")
        publisher = if (!strSocketAddress.isNullOrEmpty()) {
            final var channel = ManagedChannelBuilder.forTarget(strSocketAddress).usePlaintext().build()
            // Create a publisher instance with default settings bound to the topic
            final var channelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel))
            Publisher.newBuilder(topicName)
                    .setChannelProvider(channelProvider)
                    .setCredentialsProvider(NoCredentialsProvider())
                    .build()
        } else {
            Publisher.newBuilder(topicName).build()
        }
    }

    override public void stop() {
        // When finished with the publisher, shutdown to free up resources.
        publisher.shutdown()
        publisher.awaitTermination(1, TimeUnit.MINUTES)
    }

    override public void publishPubSubMessage(pubsubMessage: PubsubMessage) {
        final var future = publisher.publish(pubsubMessage)

        // add an asynchronous callback to handle success / failure
        ApiFutures.addCallback(future, object : ApiFutureCallback<String> {

            override public void onFailure(throwable: Throwable) {
                if (throwable is ApiException) {
                    // details on the API exception
                    logger.warn("Error publishing message to Pubsub topic: " + topicId + "\n" +
                            "Message: " + throwable.message + "\n" +
                            "Status code: " + throwable.statusCode.code + "\n" +
                            "Retrying: " + throwable.isRetryable + "")
                } else {
                    logger.warn("Error publishing message to Pubsub topic: " + topicId + "")
                }
            }

            override public void onSuccess(messageId: String) {
                // Once published, returns server-assigned message ids (unique within the topic)
                logger.debug("Published message " + messageId + " to topic " + topicId + "")
            }
        }, MoreExecutors.directExecutor())
    }

    override public void publishEvent(event: Event) {
        final var message = PubsubMessage.newBuilder()
                .setData(event.toJsonByteString())
                .build()
        publishPubSubMessage(message)
    }
}
