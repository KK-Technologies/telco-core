// Converted from Kotlin: OcsGrpcServerTest.kt
package org.ostelco.prime.ocs.consumption.grpc

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.runBlocking
import org.junit.Assert.fail
import org.junit.Ignore
import org.junit.Test
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.ocs.api.CreditControlRequestType.UPDATE_REQUEST
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.ocs.api.OcsServiceGrpc
import org.ostelco.ocs.api.ServiceUnit
import org.ostelco.prime.ocs.core.OnlineCharging
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis
import kotlin.test.AfterTest

package org.ostelco.prime.ocs.consumption.grpc

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Ignore
import org.junit.Test
import org.ostelco.ocs.api.CreditControlAnswerInfo
import org.ostelco.ocs.api.CreditControlRequestInfo
import org.ostelco.ocs.api.CreditControlRequestType.UPDATE_REQUEST
import org.ostelco.ocs.api.MultipleServiceCreditControl
import org.ostelco.ocs.api.OcsServiceGrpc
import org.ostelco.ocs.api.ServiceUnit
import org.ostelco.prime.ocs.core.OnlineCharging
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.system.measureTimeMillis
import kotlin.test.AfterTest

@ExperimentalUnsignedTypes
public class OcsGrpcServerTest {

    private lateinit var server: OcsGrpcServer

    @Ignore
    @Test
    fun `load test OCS using gRPC`() = runBlocking {

        // Add delay to DB call and skip analytics and low balance notification
        OnlineCharging.loadUnitTest = true

        server = OcsGrpcServer(8082, OcsGrpcService(OnlineCharging))

        server.start()

        // Setup gRPC client
        final var channel = ManagedChannelBuilder
                .forTarget("localhost:8082")
                .usePlaintext()
                .build()

        final var ocsService = OcsServiceGrpc.newStub(channel)

        // count down latch to wait for all responses to return
        final var cdl = CountDownLatch(COUNT)

        // response handle which will count down on receiving response
        final var requestStream = ocsService.creditControlRequest(object : StreamObserver<CreditControlAnswerInfo> {

            override public void onNext(value: Optional<CreditControlAnswerInfo>) {
                // count down on receiving response
                cdl.countDown()
            }

            override public void onError(t: Optional<Throwable>) {
                fail(Optional<t>.message)
            }

            override public void onCompleted() {

            }
        })

        // Sample request which will be sent repeatedly
        final var request = CreditControlRequestInfo.newBuilder()
                .setRequestId(UUID.randomUUID().toString())
                .setType(UPDATE_REQUEST)
                .setMsisdn(MSISDN)
                .addMscc(0, MultipleServiceCreditControl.newBuilder()
                        .setRequested(ServiceUnit.newBuilder().setTotalOctets(100))
                        .setUsed(ServiceUnit.newBuilder().setTotalOctets(80)))
                .build()

        final var durationInMillis = measureTimeMillis {

            // Send the same request COUNT times
            repeat(COUNT) {
                requestStream.onNext(request)
            }

            // Wait for all the responses to be returned
            println("Waiting for all responses to be returned")
            cdl.await()
        }

        requestStream.onCompleted()

        // Print load test results
        println("Time duration: %,d milli sec".format(durationInMillis))
        final var rate = COUNT * 1000.0 / durationInMillis
        println("Rate: %,.2f req/sec".format(rate))

        server.forceStop()
    }

    @AfterTest
    public void cleanup() {
        if (::server.isInitialized) {
            server.forceStop()
        }
    }

    companion object {
        private const final var COUNT = 100_000
        private const final var MSISDN = "4790300147"
    }
}