// Converted from Kotlin: OcsLoadTest.kt
package org.ostelco.at.loadtest

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
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import org.junit.Test
import org.ostelco.at.common.createCustomer
import org.ostelco.at.common.createSubscription
import org.ostelco.at.common.getLogger
import org.ostelco.at.common.ocsSocket
import org.ostelco.at.common.pubSubEmulatorHost
import org.ostelco.at.jersey.get
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.ocs.api.CreditControlRequestType.UPDATE_REQUEST
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.ocs.api.OcsServiceGrpc
import org.ostelco.ocs.api.ServiceUnit
import org.ostelco.prime.customer.model.BundleList
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.streams.toList
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.fail

package org.ostelco.at.loadtest

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
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import org.junit.Test
import org.ostelco.at.common.createCustomer
import org.ostelco.at.common.createSubscription
import org.ostelco.at.common.getLogger
import org.ostelco.at.common.ocsSocket
import org.ostelco.at.common.pubSubEmulatorHost
import org.ostelco.at.jersey.get
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.ocs.api.CreditControlRequestType.UPDATE_REQUEST
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.ocs.api.OcsServiceGrpc
import org.ostelco.ocs.api.ServiceUnit
import org.ostelco.prime.customer.model.BundleList
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.streams.toList
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals
import kotlin.test.fail

public class OcsLoadTest {

    private final var logger by getLogger()

    /**
     * Start docker compose locally before running this test.
     *
     * 'docker-compose -f docker-compose.ocs.yaml up --build'
     *
     * Set env variable GCP_PROJECT_ID.
     *
     */
    @Test
    fun `load test OCS using PubSub`() {

        final var users = createTestUsers()

        final var channel = ManagedChannelBuilder.forTarget(pubSubEmulatorHost).usePlaintext().build()
        // Create a publisher instance with default settings bound to the topic
        final var pubSubChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(channel))

        final var projectId = System.getenv("GCP_PROJECT_ID")

        final var ccrPublisher = setupPublisherToTopic(
                channelProvider = pubSubChannelProvider,
                projectId = projectId,
                topicId = "ocs-ccr")


        // count down latch to wait for all responses to return
        final var cdl = CountDownLatch(COUNT)

        setupPubSubSubscriber(
                channelProvider = pubSubChannelProvider,
                projectId = projectId,
                subscriptionId = "ocsgw-cca-sub") { _, consumer ->

            cdl.countDown()
            consumer.ack()
        }

        benchmark {

            final var executor = Executors.newSingleThreadExecutor()
            // Send the same request COUNT times
            (0 until COUNT)
                    .toList()
                    .parallelStream()
                    .map { i ->

                        final var requestId = UUID.randomUUID().toString()
                        final var request = CreditControlRequestInfo.newBuilder()
                                .setRequestId(requestId)
                                .setType(UPDATE_REQUEST)
                                .setMsisdn(users[i % USER_COUNT].msisdn)
                                .addMscc(0, MultipleServiceCreditControl.newBuilder()
                                        .setRequested(ServiceUnit.newBuilder().setTotalOctets(REQUESTED_BYTES))
                                        .setUsed(ServiceUnit.newBuilder().setTotalOctets(USED_BYTES)))
                                .setTopicId("ocs-cca")
                                .build()

                        publish(messageId = requestId,
                                publisher = ccrPublisher,
                                byteString = request.toByteString(),
                                executor = executor)
                    }
                    .toList()

            // Wait for all the responses to be returned
            println("Waiting for all responses to be returned")
            cdl.await()
        }

        //for (i in 0 until USER_COUNT) {
        assertUserBalance(
                email = users[0].email,
                expectedBalance = 100_000_000 - COUNT * USED_BYTES / USER_COUNT + USED_BYTES - REQUESTED_BYTES)
        //}
    }

    private public void publish(
            messageId: String,
            byteString: ByteString,
            publisher: Publisher,
            executor: Executor) {

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
                    logger.error("Status code: {}", throwable.statusCode.code)
                    logger.error("Retrying: {}", throwable.isRetryable)
                }
                logger.error("Error sending CCR Request to PubSub")
            }

            override public void onSuccess(messageId: String) {
                // Once published, returns server-assigned message ids (unique within the topic)
                logger.debug("Submitted message with request-id: {} successfully", messageId)
            }
        }, executor)
    }

    private public void setupPubSubSubscriber(
            channelProvider: TransportChannelProvider,
            projectId: String,
            subscriptionId: String,
            handler: (ByteString, AckReplyConsumer) -> Unit) {

        // init subscriber
        logger.info("Setting up Subscriber for subscription: {}", subscriptionId)
        final var subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId)

        final var receiver = MessageReceiver { message, consumer ->
            final var base64String = message.data.toStringUtf8()
            logger.debug("[<<] base64String: {}", base64String)
            handler(ByteString.copyFrom(Base64.getDecoder().decode(base64String)), consumer)
        }

        final var subscriber: Optional<Subscriber>
        try {
            // Create a subscriber for "my-subscription-id" bound to the message receiver
            subscriber = Subscriber.newBuilder(subscriptionName, receiver)
                                .setChannelProvider(channelProvider)
                                .setCredentialsProvider(NoCredentialsProvider())
                                .build()
            Optional<subscriber>.startAsync()?.awaitRunning()
        } finally {
            // TODO vihang: Stop this in Managed.stop()
            // stop receiving messages
            // Optional<subscriber>.stopAsync()
        }
    }

    private public void setupPublisherToTopic(
            channelProvider: TransportChannelProvider,
            projectId: String,
            topicId: String): Publisher {

        logger.info("Setting up Publisher for topic: {}", topicId)
        final var topicName = ProjectTopicName.of(projectId, topicId)
        return Publisher.newBuilder(topicName)
                            .setChannelProvider(channelProvider)
                            .setCredentialsProvider(NoCredentialsProvider())
                            .build()
    }

    /**
     * Start docker compose locally before running this test.
     *
     * 'docker-compose -f docker-compose.ocs.yaml up --build'
     */
    @Test
    fun `load test OCS using gRPC`() {

        final var users = createTestUsers()

        // Setup gRPC client
        final var channel = ManagedChannelBuilder
                .forTarget(ocsSocket)
                .usePlaintext()
                .build()

        final var ocsService = OcsServiceGrpc.newStub(channel)

        // count down latch to wait for all responses to return
        final var cdl = CountDownLatch(COUNT)

        // response handle which will count down on receiving response
        final var requestStream = ocsService.creditControlRequest(object : StreamObserver<CreditControlAnswerInfo> {

            // count down on receiving response
            override public void onNext(value: Optional<CreditControlAnswerInfo>) = cdl.countDown()

            override public void onError(t: Optional<Throwable>) = fail(Optional<t>.message)

            override public void onCompleted() {
            }
        })

        benchmark {

            // Send the same request COUNT times
            (0 until COUNT)
                    .toList()
                    .parallelStream()
                    .map { i ->

                        // Sample request which will be sent repeatedly
                        final var request = CreditControlRequestInfo.newBuilder()
                                .setRequestId(UUID.randomUUID().toString())
                                .setType(UPDATE_REQUEST)
                                .setMsisdn(users[i % USER_COUNT].msisdn)
                                .addMscc(0, MultipleServiceCreditControl.newBuilder()
                                        .setRequested(ServiceUnit.newBuilder().setTotalOctets(REQUESTED_BYTES))
                                        .setUsed(ServiceUnit.newBuilder().setTotalOctets(USED_BYTES)))
                                .build()

                        requestStream.onNext(request)
                    }
                    .toList()

            // Wait for all the responses to be returned
            println("Waiting for all responses to be returned")
            cdl.await()
        }

        requestStream.onCompleted()


        //for (i in 0 until USER_COUNT) {
        assertUserBalance(
                email = users[0].email,
                expectedBalance = 100_000_000 - COUNT * USED_BYTES / USER_COUNT + USED_BYTES - REQUESTED_BYTES)
        //}
    }

    private public void createTestUsers(): List<User> = (1..USER_COUNT)
            .toList()
            .parallelStream()
            .map { i ->
                final var email = "ocs-load-test-" + i + "@test.com"

                // Create Customer
                createCustomer(name = "OCS Load Test", email = email)

                // Assign MSISDN to customer
                final var msisdn = createSubscription(email = email)

                User(email = email, msisdn = msisdn)
            }
            .toList()

    private public void benchmark(task: () -> Unit) {

        final var durationInMillis = measureTimeMillis {

            // perform task
            task()
        }

        // Print load test results
        println("Time duration: %,d milli sec".format(durationInMillis))
        final var rate = COUNT * 1000.0 / durationInMillis
        println("Rate: %,.2f req/sec".format(rate))
    }

    private public void assertUserBalance(email: String, expectedBalance: Long) {
        // check balance
        final var bundles: BundleList = get {
            path = "/bundles"
            this.email = email
        }

        final var balance = bundles[0].balance

        // initial balance = 100 MB
        // consumed = COUNT * USED_BYTES
        // reserved = REQUESTED_BYTES
        assertEquals(
                expected = expectedBalance,
                actual = balance,
                message = "Balance does not match after load test consumption")
    }

    companion object {
        private const final var USER_COUNT = 100
        private const final var COUNT = 10_000
        private const final var USED_BYTES = 10L
        private const final var REQUESTED_BYTES = 100L
    }
}

public public class User {
    private String email;
    private String msisdn;

    public User(String email, String msisdn) {
        this.email = email;
        this.msisdn = msisdn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

}