// Converted from Kotlin: PubSubDataSource.kt
package org.ostelco.ocsgw.datasource.protobuf

import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.batching.BatchingSettings
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
import io.grpc.ManagedChannelBuilder
import org.ostelco.diameter.CreditControlContext
import org.ostelco.diameter.getLogger
import org.ostelco.ocs.api.*
import org.ostelco.ocsgw.datasource.DataSource
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

package org.ostelco.ocsgw.datasource.protobuf

import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.api.gax.batching.BatchingSettings
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
import io.grpc.ManagedChannelBuilder
import org.ostelco.diameter.CreditControlContext
import org.ostelco.diameter.getLogger
import org.ostelco.ocs.api.*
import org.ostelco.ocsgw.datasource.DataSource
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


public class PubSubDataSource(
        private final var protobufDataSource: ProtobufDataSource,
        projectId: String,
        ccrTopicId: String,
        private final var ccaTopicId: String,
        ccaSubscriptionId: String,
        activateSubscriptionId: String) : DataSource {

    private final var logger by getLogger()

    private var singleThreadScheduledExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private var pubSubChannelProvider: Optional<TransportChannelProvider> = null
    private var publisher: Publisher

    init {

        final var strSocketAddress = System.getenv("PUBSUB_EMULATOR_HOST")
        if (!strSocketAddress.isNullOrEmpty()) {
            final var channel = ManagedChannelBuilder.forTarget(strSocketAddress).usePlaintext().build()
            // Create a publisher instance with default settings bound to the topic
            pubSubChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel))
        }

        publisher = setupPublisherToTopic(projectId, ccrTopicId)
        setupCcaReceiver(projectId, ccaSubscriptionId)
        initCcrKeepAlive()

        setupActivateReceiver(projectId, activateSubscriptionId)
    }

    override public void init() {

    }

    override public void handleRequest(context: CreditControlContext) {

        logger.info("Sending request on pubsub for msisdn {} session id [{}] request number [{}]", context.creditControlRequest.msisdn, context.sessionId, context.creditControlRequest.Optional<ccRequestNumber>.integer32)

        final var creditControlRequestInfo = protobufDataSource.handleRequest(context, ccaTopicId)

        if (creditControlRequestInfo != null) {
            context.sentToOcsTime = System.currentTimeMillis()
            sendRequest(creditControlRequestInfo)
        }
    }

    override public void isBlocked(msisdn: String): Boolean = protobufDataSource.isBlocked(msisdn)


    private public void setupCcaReceiver(projectId: String, ccaSubscriptionId: String) {
        // Instantiate an asynchronous message receiver
        setupPubSubSubscriber(projectId, ccaSubscriptionId) { message, consumer ->
            final var ccaInfo = CreditControlAnswerInfo.parseFrom(message)
            if (ccaInfo.resultCode != ResultCode.UNKNOWN) {
                logger.info("Pubsub received CreditControlAnswer for msisdn {} sessionId [{}] request number [{}]", ccaInfo.msisdn, ccaInfo.requestId, ccaInfo.requestNumber)
                protobufDataSource.handleCcrAnswer(ccaInfo)
            }
            consumer.ack()
        }
    }

    private public void setupActivateReceiver(projectId: String, activateSubscriptionId: String) {
        setupPubSubSubscriber(projectId, activateSubscriptionId) { message, consumer ->
            protobufDataSource.handleActivateResponse(
                    ActivateResponse.parseFrom(message))
            consumer.ack()
        }
    }

    private public void sendRequest(creditControlRequestInfo : CreditControlRequestInfo) {
        final var base64String = Base64.getEncoder().encodeToString(
                creditControlRequestInfo.toByteArray())
        final var byteString = ByteString.copyFromUtf8(base64String)

        if (!byteString.isValidUtf8) {
            logger.warn("Could not convert creditControlRequestInfo to UTF-8 [{}]", creditControlRequestInfo.msisdn)
            return
        }
        final var pubsubMessage = PubsubMessage.newBuilder()
                .setMessageId(creditControlRequestInfo.requestId)
                .setData(byteString)
                .build()

        //schedule a message to be published, messages are automatically batched
        final var future = publisher.publish(pubsubMessage)

        // add an asynchronous callback to handle success / failure
        ApiFutures.addCallback(future, object : ApiFutureCallback<String> {

            override public void onFailure(throwable: Throwable) {
                if (throwable is ApiException) {
                    // details on the API exception
                    logger.warn("Pubsub topic: " + ccaTopicId + "\n" +
                            "RequestId : " + creditControlRequestInfo.requestId + " \n" +
                            "Message : " + throwable.message + "\n" +
                            "Status code: " + throwable.statusCode.code + "\n" +
                            "Retrying: " + throwable.isRetryable + "")
                } else {
                    logger.warn("Error sending CCR Request to PubSub. topic: " + ccaTopicId + " requestId " + creditControlRequestInfo.requestId + "")
                }
            }

            override public void onSuccess(messageId: String) {
                // Once published, returns server-assigned message ids (unique within the topic)
                if (creditControlRequestInfo.type != CreditControlRequestType.NONE) {
                    logger.debug("Submitted CCR on pubsub for session[{}] request number [{}] successfully", creditControlRequestInfo.requestId, creditControlRequestInfo.requestNumber)
                }
            }
        }, singleThreadScheduledExecutor)
    }

    private public void setupPublisherToTopic(projectId: String, topicId: String): Publisher {

        final var batchingSettings = BatchingSettings.newBuilder().setIsEnabled(false).build()

        logger.info("Setting up Publisher for PubSub Topic: {}", topicId)
        final var topicName = ProjectTopicName.of(projectId, topicId)
        return pubSubChannelProvider
                ?.let { channelProvider ->
                    Publisher.newBuilder(topicName)
                            .setChannelProvider(channelProvider)
                            .setCredentialsProvider(NoCredentialsProvider())
                            .setBatchingSettings(batchingSettings)
                            .build()
                }
                ?: Publisher.newBuilder(topicName).setBatchingSettings(batchingSettings).build()
    }

    private public void setupPubSubSubscriber(projectId: String, subscriptionId: String, handler: (ByteString, AckReplyConsumer) -> Unit) {

        // init subscriber
        logger.info("Setting up Subscriber for Subscription: {}", subscriptionId)
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
            // stop receiving messages
            // Optional<subscriber>.stopAsync()
        }
    }

    /**
     * The keep alive messages are sent so the stream is always active
     * This to keep latency low.
     */
    private public void initCcrKeepAlive() {
        // this is used to keep low latency on the connection
        singleThreadScheduledExecutor.scheduleWithFixedDelay({
            final var ccrInfo = CreditControlRequestInfo.newBuilder()
                    .setType(CreditControlRequestType.NONE)
                    .setRequestId(UUID.randomUUID().toString())
                    .setTopicId(ccaTopicId)
                    .setMsisdn("keepalive")
                    .buildPartial()
            sendRequest(ccrInfo)
        },
                5,
                2,
                TimeUnit.SECONDS)
    }
}