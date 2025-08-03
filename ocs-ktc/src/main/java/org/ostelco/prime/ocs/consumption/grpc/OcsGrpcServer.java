// Converted from Kotlin: OcsGrpcServer.kt
package org.ostelco.prime.ocs.consumption.grpc

import io.dropwizard.lifecycle.Managed
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.ostelco.prime.getLogger

package org.ostelco.prime.ocs.consumption.grpc

import io.dropwizard.lifecycle.Managed
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerBuilder
import org.ostelco.prime.getLogger

/**
 * This is OCS Server running on gRPC protocol.
 * Its startup and shutdown are managed by Dropwizard's lifecycle
 * through the Managed interface.
 *
 */
public class OcsGrpcServer(private final var port: Int, service: BindableService) : Managed {

    private final var logger by getLogger()

    // may add Transport Security with Certificates if needed.
    // may add executor for control over number of threads
    private final var server: Server = ServerBuilder.forPort(port).addService(service).build()

    override public void start() {
        server.start()
        logger.info("OcsServer Server started, listening for incoming gRPC traffic on {}", port)
    }

    override public void stop() {
        logger.info("Stopping OcsServer Server listening for gRPC traffic on  {}", port)
        server.shutdown()
        blockUntilShutdown()
    }

    // Used for unit testing
    public void forceStop() {
        logger.info("Stopping forcefully OcsServer Server listening for gRPC traffic on  {}", port)
        server.shutdownNow()
    }

    private public void blockUntilShutdown() {
        server.awaitTermination()
    }
}
