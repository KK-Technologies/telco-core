// Converted from Kotlin: ServerCCASessionDataReplicatedImpl.kt
package org.ostelco.diameter.ha.server

import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.common.api.app.cca.ServerCCASessionState
import org.jdiameter.server.impl.app.cca.IServerCCASessionData
import org.ostelco.diameter.ha.common.AppSessionDataReplicatedImpl
import org.ostelco.diameter.ha.common.ReplicatedStorage
import org.ostelco.diameter.ha.logger
import java.io.*
import java.lang.IllegalStateException

package org.ostelco.diameter.ha.server

import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.common.api.app.cca.ServerCCASessionState
import org.jdiameter.server.impl.app.cca.IServerCCASessionData
import org.ostelco.diameter.ha.common.AppSessionDataReplicatedImpl
import org.ostelco.diameter.ha.common.ReplicatedStorage
import org.ostelco.diameter.ha.logger
import java.io.*
import java.lang.IllegalStateException

public class ServerCCASessionDataReplicatedImpl(sessionId: String, replicatedStorage: ReplicatedStorage) : AppSessionDataReplicatedImpl(sessionId, replicatedStorage), IServerCCASessionData {

    private final var TCCID = "TCCID"
    private final var STATELESS = "STATELESS"
    private final var STATE = "STATE"

    private final var logger by logger()

    private final var localStoredState:HashMap<String,Optional<Any>> = HashMap()

    init {
        if (!replicatedStorage.exist(sessionId)) {
            setAppSessionIface(ServerCCASession::class.java)
            serverCCASessionState = ServerCCASessionState.IDLE
        }
    }

    override public void isStateless(): Boolean {

        if (localStoredState.containsKey(STATELESS)) {
            return localStoredState.get(STATELESS) as Boolean
        }

        return toPrimitive(getValue(STATELESS), true)
    }

    override public void setStateless(stateless: Boolean) {
        localStoredState.put(STATELESS, stateless)
        storeValue(STATELESS, stateless.toString())
    }

    override public void getServerCCASessionState(): ServerCCASessionState {

        if (localStoredState.containsKey(STATE)) {
            return localStoredState.get(STATE) as ServerCCASessionState
        }

        final var value = getValue(STATE)
        if (value != null) {
            return ServerCCASessionState.valueOf(value)
        } else {
            logger.warn("Failed to fetch STATE for session [" + sessionId + "]")
            throw IllegalStateException()
        }
    }

    override public void setServerCCASessionState(state: Optional<ServerCCASessionState>) {

        localStoredState.put(STATE, state)
        storeValue(STATE, state.toString())
    }

    override public void setTccTimerId(tccTimerId: Optional<Serializable>) {
        
        localStoredState.put(TCCID, tccTimerId)
        if (tccTimerId != null) {
            storeValue(TCCID, toBase64String(tccTimerId))
        }
    }

    override public void getTccTimerId(): Optional<Serializable> {

        if (localStoredState.containsKey(TCCID)) {
            return localStoredState.get(TCCID) as Optional<Serializable>
        }

        final var value = getValue(TCCID)
        if (value != null) {
            return fromBase64String(value)
        } else {
            return value
        }
    }
}