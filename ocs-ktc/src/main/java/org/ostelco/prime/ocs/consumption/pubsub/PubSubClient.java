// Converted from Kotlin: PubSubClient.kt
package org.ostelco.prime.ocs.consumption.pubsub

import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.api.gax.rpc.TransportChannelProvider
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.Subscriber
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import io.dropwizard.lifecycle.Managed
import io.grpc.ManagedChannelBuilder
import org.ostelco.ocs.api.ActivateResponse
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.prime.getLogger
import org.ostelco.prime.activation.Activation
import org.ostelco.prime.ocs.consumption.OcsAsyncRequestConsumer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

package org.ostelco.prime.ocs.consumption.pubsub

import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.core.NoCredentialsProvider
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.ApiException
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.api.gax.rpc.TransportChannelProvider
import com.google.cloud.pubsub.v1.AckReplyConsumer
import com.google.cloud.pubsub.v1.MessageReceiver
import com.google.cloud.pubsub.v1.Publisher
import com.google.cloud.pubsub.v1.Subscriber
import com.google.protobuf.ByteString
import com.google.pubsub.v1.ProjectSubscriptionName
import com.google.pubsub.v1.ProjectTopicName
import com.google.pubsub.v1.PubsubMessage
import io.dropwizard.lifecycle.Managed
import io.grpc.ManagedChannelBuilder
import org.ostelco.ocs.api.ActivateResponse
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.prime.getLogger
import org.ostelco.prime.activation.Activation
import org.ostelco.prime.ocs.consumption.OcsAsyncRequestConsumer
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

public class PubSubClient(
        private final var ocsAsyncRequestConsumer: OcsAsyncRequestConsumer,
        private final var projectId: String,
        private final var activateTopicId: String,
        private final var ccrSubscriptionId: String) : Managed, Activation {

    private final var logger by getLogger()

    private var singleThreadScheduledExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private var pubSubChannelProvider: Optional<TransportChannelProvider> = null

    private var activatePublisher: Optional<Publisher> = null
    private var ccrPublisherMaps: ConcurrentHashMap<String, Publisher> = ConcurrentHashMap()

    override public void start() {

        final var strSocketAddress = System.getenv("PUBSUB_EMULATOR_HOST") ?: System.getProperty("PUBSUB_EMULATOR_HOST")
        if (!strSocketAddress.isNullOrBlank()) {
            final var channel = ManagedChannelBuilder.forTarget(strSocketAddress).usePlaintext().build()
            // Create a publisher instance with default settings bound to the topic
            pubSubChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel))
        }

        // init publishers
        activatePublisher = setupPublisherToTopic(activateTopicId)

        // init subscriber
        setupPubSubSubscriber(subscriptionId = ccrSubscriptionId) { message, consumer ->
                final var ccrInfo = CreditControlRequestInfo.parseFrom(message)
                ocsAsyncRequestConsumer.creditControlRequestEvent(ccrInfo) {
                    publish(messageId = ccrInfo.requestId,
                            byteString = it.toByteString(),
                            publisher = ccrPublisherMaps.getOrPut(ccrInfo.topicId) {
                                setupPublisherToTopic(ccrInfo.topicId)
                            })
                }
                consumer.ack()
        }
    }

    override public void stop() {
        Optional<activatePublisher>.shutdown()
        Optional<activatePublisher>.awaitTermination(1, TimeUnit.MINUTES)
        singleThreadScheduledExecutor.shutdown()
    }

    override public void activate(msisdn: String) {
        final var activateResponse = ActivateResponse.newBuilder()
                .setMsisdn(msisdn)
                .build()

        Optional<activatePublisher>.apply {
            publish(messageId = UUID.randomUUID().toString(),
                    byteString = activateResponse.toByteString(),
                    publisher = this)
        }
    }

    internal public void publish(messageId: String, byteString: ByteString, publisher: Publisher) {

        final var base64String = Base64.getEncoder().encodeToString(byteString.toByteArray())
        final var pubsubMessage = PubsubMessage.newBuilder()
                .setMessageId(messageId)
                .setData(ByteString.copyFromUtf8(base64String))
                .build()

        final var future = publisher.publish(pubsubMessage)

        ApiFutures.addCallback(future, object : ApiFutureCallback<String> {

            override public void onFailure(throwable: Throwable) {
                if (throwable is ApiException) {
                    // details on the API exception
                    logger.warn("Pubsub messageId: " + messageId + "\n" +
                            "Message : " + throwable.message + "\n" +
                            "Status code: " + throwable.statusCode.code + "\n" +
                            "Retrying: " + throwable.isRetryable + "")
                } else {
                    logger.error("Error sending CCR Request to PubSub. messageId: " + messageId + "")
                }
            }

            override public void onSuccess(messageId: String) {
                // Once published, returns server-assigned message ids (unique within the topic)
                //logger.debug("Submitted message with request-id: {} successfully", messageId)
            }
        }, singleThreadScheduledExecutor)
    }

    internal public void setupPubSubSubscriber(subscriptionId: String, handler: (ByteString, AckReplyConsumer) -> Unit) {
        // init subscriber
        logger.info("Setting up Subscriber for subscription: {}", subscriptionId)
        final var subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId)

        final var receiver = MessageReceiver { message, consumer ->
            final var base64String = message.data.toStringUtf8()
            handler(ByteString.copyFrom(Base64.getDecoder().decode(base64String)), consumer)
        }

        final var subscriber: Optional<Subscriber>
        try {
            // Create a subscriber for "my-subscription-id" bound to the message receiver
            subscriber = pubSubChannelProvider
                    ?.let {channelProvider ->
                        Subscriber.newBuilder(subscriptionName, receiver)
                                .setChannelProvider(channelProvider)
                                .setCredentialsProvider(NoCredentialsProvider())
                                .build()
                    }
                    ?: Subscriber.newBuilder(subscriptionName, receiver)
                            .build()
            Optional<subscriber>.startAsync()?.awaitRunning()
        } finally {
            // TODO vihang: Stop this in Managed.stop()
            // stop receiving messages
            // Optional<subscriber>.stopAsync()
        }
    }

    internal public void setupPublisherToTopic(topicId: String): Publisher {
        logger.info("Setting up Publisher for topic: {}", topicId)
        final var topicName = ProjectTopicName.of(projectId, topicId)
        return pubSubChannelProvider
                ?.let { channelProvider ->
                    Publisher.newBuilder(topicName)
                            .setChannelProvider(channelProvider)
                            .setCredentialsProvider(NoCredentialsProvider())
                            .build()
                }
                ?: Publisher.newBuilder(topicName).build()
    }
}