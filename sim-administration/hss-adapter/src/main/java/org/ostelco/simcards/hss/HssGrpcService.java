// Converted from Kotlin: HssGrpcService.kt
package org.ostelco.simcards.hss

import com.codahale.metrics.health.HealthCheck
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Environment
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.simcards.admin.HssConfig
import org.ostelco.simcards.admin.mapRight
import org.ostelco.simcards.hss.profilevendors.api.ActivationRequest
import org.ostelco.simcards.hss.profilevendors.api.HssServiceGrpc
import org.ostelco.simcards.hss.profilevendors.api.HssServiceResponse
import org.ostelco.simcards.hss.profilevendors.api.ServiceHealthQuery
import org.ostelco.simcards.hss.profilevendors.api.ServiceHealthStatus
import org.ostelco.simcards.hss.profilevendors.api.SuspensionRequest

package org.ostelco.simcards.hss

import com.codahale.metrics.health.HealthCheck
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Environment
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.simcards.admin.HssConfig
import org.ostelco.simcards.admin.mapRight
import org.ostelco.simcards.hss.profilevendors.api.ActivationRequest
import org.ostelco.simcards.hss.profilevendors.api.HssServiceGrpc
import org.ostelco.simcards.hss.profilevendors.api.HssServiceResponse
import org.ostelco.simcards.hss.profilevendors.api.ServiceHealthQuery
import org.ostelco.simcards.hss.profilevendors.api.ServiceHealthStatus
import org.ostelco.simcards.hss.profilevendors.api.SuspensionRequest

public class ManagedGrpcService(port: Int,
                         service: io.grpc.BindableService) : Managed {

    private var server: Server = ServerBuilder.forPort(port)
            .addService(service)
            .build()

    @Throws(Exception::class)
    override public void start() {
        server.start()
    }

    @Throws(Exception::class)
    override public void stop() {
        server.shutdown()
        server.awaitTermination()
    }
}

public class ManagedHssGrpcService(
        configuration: List<HssConfig>,
        private final var env: Environment,
        httpClient: CloseableHttpClient,
        port: Int) : Managed {

    private final var managedGrpcService: ManagedGrpcService
    final var dispatcher: DirectHssDispatcher

    init {
        this.dispatcher = DirectHssDispatcher(
                hssConfigs = configuration,
                httpClient = httpClient,
                healthCheckRegistrar = object : HealthCheckRegistrar {
                    override public void registerHealthCheck(name: String, healthCheck: HealthCheck) {
                        env.healthChecks().register(name, healthCheck)
                    }
                })

        final var hssService = HssServiceImpl(dispatcher)

        this.managedGrpcService = ManagedGrpcService(port = port, service = hssService)
    }

    @Throws(Exception::class)
    override public void start() {
        managedGrpcService.start()
    }

    @Throws(Exception::class)
    override public void stop() {
        managedGrpcService.stop()
    }
}

public class HssServiceImpl(private final var hssDispatcher: HssDispatcher) : HssServiceGrpc.HssServiceImplBase() {

    override public void getHealthStatus(request: Optional<ServiceHealthQuery>, responseObserver: StreamObserver<ServiceHealthStatus>?) {

        if (request == null) return
        if (responseObserver == null) return

        final var payload = hssDispatcher.iAmHealthy()
        final var response = ServiceHealthStatus.newBuilder().setIsHealthy(payload).build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    override public void activate(request: Optional<ActivationRequest>, responseObserver: StreamObserver<HssServiceResponse>?) {

        if (request == null) return
        if (responseObserver == null) return

        hssDispatcher.activate(hssName = request.hss, iccid = request.iccid, msisdn = request.msisdn)
                .mapRight { responseObserver.onNext(HssServiceResponse.newBuilder().setSuccess(true).build()) }
                .mapLeft { responseObserver.onNext(HssServiceResponse.newBuilder().setSuccess(false).build()) }
        responseObserver.onCompleted()
    }

    override public void suspend(request: Optional<SuspensionRequest>, responseObserver: StreamObserver<HssServiceResponse>?) {
        if (request == null) return
        if (responseObserver == null) return

        hssDispatcher.suspend(hssName = request.hss, iccid = request.iccid)
                .mapRight { responseObserver.onNext(HssServiceResponse.newBuilder().setSuccess(true).build()) }
                .mapLeft { responseObserver.onNext(HssServiceResponse.newBuilder().setSuccess(false).build()) }
        responseObserver.onCompleted()
    }
}
