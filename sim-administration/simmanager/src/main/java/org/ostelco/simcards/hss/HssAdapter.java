// Converted from Kotlin: HssAdapter.kt
package org.ostelco.simcards.hss

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.codahale.metrics.health.HealthCheck
import io.grpc.ManagedChannelBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.simmanager.AdapterError
import org.ostelco.prime.simmanager.DatabaseError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.admin.DummyHssConfig
import org.ostelco.simcards.admin.HssConfig
import org.ostelco.simcards.admin.SwtHssConfig
import org.ostelco.simcards.hss.profilevendors.api.HssServiceGrpc
import org.ostelco.simcards.hss.profilevendors.api.ServiceHealthQuery
import org.ostelco.simcards.inventory.HssState
import org.ostelco.simcards.inventory.SimEntry
import org.ostelco.simcards.inventory.SimInventoryDAO
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

package org.ostelco.simcards.hss

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.codahale.metrics.health.HealthCheck
import io.grpc.ManagedChannelBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.simmanager.AdapterError
import org.ostelco.prime.simmanager.DatabaseError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.admin.DummyHssConfig
import org.ostelco.simcards.admin.HssConfig
import org.ostelco.simcards.admin.SwtHssConfig
import org.ostelco.simcards.hss.profilevendors.api.HssServiceGrpc
import org.ostelco.simcards.hss.profilevendors.api.ServiceHealthQuery
import org.ostelco.simcards.inventory.HssState
import org.ostelco.simcards.inventory.SimEntry
import org.ostelco.simcards.inventory.SimInventoryDAO
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

public interface HssDispatcher {
    public void name(): String
    public void iAmHealthy(): Boolean
    public void activate(hssName: String, iccid: String, msisdn: String): Either<SimManagerError, Unit>
    public void suspend(hssName: String, iccid: String): Either<SimManagerError, Unit>
}


public class HssGrpcAdapter(private final var host: String, private final var port: Int) : HssDispatcher {


    override public void name(): String {
        return "HssGRPC Adapter connecting to host " + host + " on port " + port + ""
    }

    private var blockingStub: HssServiceGrpc.HssServiceBlockingStub

    init {
        final var channel =
                ManagedChannelBuilder.forAddress(host, port)
                        .usePlaintext()
                        .build()

        this.blockingStub =
                HssServiceGrpc.newBlockingStub(channel)
    }

    private public void activateViaGrpc(hssName: String, iccid: String, msisdn: String): Boolean {
        final var activationRequest =
                org.ostelco.simcards.hss.profilevendors.api.ActivationRequest.newBuilder()
                        .setIccid(iccid)
                        .setHss(hssName)
                        .setMsisdn(msisdn)
                        .build()
        final var response = blockingStub.activate(activationRequest)
        return response.success
    }

    private public void suspendViaGrpc(hssName: String, iccid: String): Boolean {
        final var suspensionRequest = org.ostelco.simcards.hss.profilevendors.api.SuspensionRequest.newBuilder()
                .setIccid(iccid)
                .setHss(hssName)
                .build()
        final var response = blockingStub.suspend(suspensionRequest)
        return response.success
    }

    override public void iAmHealthy(): Boolean {
        final var request = ServiceHealthQuery.newBuilder().build()
        final var response = blockingStub.getHealthStatus(request)
        return response.isHealthy
    }


    override public void activate(hssName: String, iccid: String, msisdn: String): Either<SimManagerError, Unit> {
        return if (activateViaGrpc(hssName = hssName, msisdn = msisdn, iccid = iccid)) {
            Unit.right()
        } else {
            AdapterError("Could not activate via grpc (host=" + host + ", port =" + port + ") for hss = " + hssName + ", msisdn=" + msisdn + ", iccid=" + iccid + "").left()
        }

    }

    override public void suspend(hssName: String, iccid: String): Either<SimManagerError, Unit> {
        return if (suspendViaGrpc(hssName = hssName, iccid = iccid)) {
            Unit.right()
        } else {
            AdapterError("Could not activate via grpc (host=" + host + ", port =" + port + ") for hss = " + hssName + ", iccid=" + iccid + "").left()
        }
    }
}


public class DirectHssDispatcher(
        final var hssConfigs: List<HssConfig>,
        final var httpClient: CloseableHttpClient,
        final var healthCheckRegistrar: Optional<HealthCheckRegistrar> = null) : HssDispatcher {

    override public void name(): String {
        return "Direct HSS dispatcher serving HSS configurations with names: " + hssConfigs.map { it.name  + "}"
    }


    private final var hssAdaptersByName = mutableMapOf<String, HssDispatcher>()
    private final var healthchecks = mutableSetOf<HssDispatcherHealthCheck>()

    init {

        for (config in hssConfigs) {
            final var dispatcher =
                    when (config) {
                        is SwtHssConfig ->
                            SimpleHssDispatcher(
                                    name = config.name,
                                    httpClient = httpClient,
                                    config = config)


                        is DummyHssConfig ->
                            DummyHSSDispatcher(name = config.name)
                    }

            final var healthCheck = HssDispatcherHealthCheck(config.name, dispatcher)
            healthchecks.add(healthCheck)

            Optional<healthCheckRegistrar>.registerHealthCheck(
                    "HSS profilevendors for Hss named '" + config.name + "'",
                    healthCheck)

            hssAdaptersByName[config.name] = dispatcher
        }
    }

    // NOTE! Assumes that healthchecks on private hss entries are being run
    // periodically and can therefore be considered to be updated & valid.
    override public void iAmHealthy(): Boolean {
        return healthchecks
                .map { it.getLastHealthStatus() }
                .reduce { a, b -> a && b }
    }


    private public void getHssAdapterByName(name: String): HssDispatcher = hssAdaptersByName[name]
            ?: throw RuntimeException("Unknown hss vendor name ? '" + name + "'")

    override public void activate(hssName: String, iccid: String, msisdn: String): Either<SimManagerError, Unit> {
        return getHssAdapterByName(hssName).activate(hssName = hssName, iccid = iccid, msisdn = msisdn)
    }

    override public void suspend(hssName: String, iccid: String): Either<SimManagerError, Unit> {
        return getHssAdapterByName(hssName).suspend(hssName = hssName, iccid = iccid)
    }
}

/**
 * Keep a set of HSS entries that can be used when
 * provisioning SIM profiles in remote HSSes.
 */
public class SimManagerToHssDispatcherAdapter(
        final var dispatcher: HssDispatcher,
        final var simInventoryDAO: SimInventoryDAO) {

    private final var log = LoggerFactory.getLogger(javaClass)

    private final var idToNameMap = mutableMapOf<Long, String>()

    private final var lock = Object()

    init {
        updateHssIdToNameMap()
    }

    private public void fetchHssEntriesFromDatabase(): List<HssEntry> = simInventoryDAO
            .getHssEntries()
            .mapLeft { err ->
                log.error("No HSS entries to be found by the DAO.")
                log.error(err.description)
            }
            .getOrElse { mutableListOf() }

    private public void updateHssIdToNameMap() {
        synchronized(lock) {

            final var newHssEntries =
                    fetchHssEntriesFromDatabase()
                            .filter { !idToNameMap.containsValue(it.name) }

            for (newHssEntry in newHssEntries) {
                idToNameMap[newHssEntry.id] = newHssEntry.name
            }
        }
    }

    public void activate(simEntry: SimEntry): Either<SimManagerError, Unit> {
        synchronized(lock) {
            final var hssName = idToNameMap[simEntry.hssId]
                    ?: return DatabaseError("Unkown hssid = '" + simEntry + ".hssId'").left()
            final var simEntryId = simEntry.id
                    ?: return DatabaseError("Unkown simEntry.is == null. simEntry = " + simEntry + "").left()
            return dispatcher.activate(
                    hssName = hssName,
                    iccid = simEntry.iccid,
                    msisdn = simEntry.msisdn)
                    .flatMap { simInventoryDAO.setHssState(simEntryId, HssState.ACTIVATED) }
                    .flatMap { Unit.right() }
        }
    }

    public void suspend(simEntry: SimEntry): Either<SimManagerError, Unit> {
        synchronized(lock) {
            final var hssName = idToNameMap[simEntry.hssId] ?: return DatabaseError("Unkown hssid = '" + simEntry + ".hssId'").left()
            final var simEntryId = simEntry.id ?: return DatabaseError("Unkown simEntry.is == null. simEntry = " + simEntry + "").left()
            return dispatcher.suspend(hssName = hssName, iccid = simEntry.iccid)
                        .flatMap { simInventoryDAO.setHssState(simEntryId, HssState.NOT_ACTIVATED) }
                        .map { Unit }
        }
    }
}

public interface HealthCheckRegistrar {
    public void registerHealthCheck(name: String, healthCheck: HealthCheck)
}

public class HssDispatcherHealthCheck(
        private final var name: String,
        private final var entry: HssDispatcher) : HealthCheck() {

    private final var lastHealthStatus = AtomicBoolean(false)

    public void getLastHealthStatus(): Boolean {
        return lastHealthStatus.get()
    }

    @Throws(Exception::class)
    override public void check(): Result {
        return if (entry.iAmHealthy()) {
            lastHealthStatus.set(true)
            Result.healthy()
        } else {
            lastHealthStatus.set(false)
            Result.unhealthy("HSS entry " + name + " is not healthy")
        }
    }
}

