// Converted from Kotlin: ClientCCASessionDataReplicatedImpl.kt
package org.ostelco.diameter.ha.client

import org.jdiameter.api.AvpDataException
import org.jdiameter.api.Request
import org.jdiameter.api.acc.ClientAccSession
import org.jdiameter.client.api.IContainer
import org.jdiameter.client.api.IMessage
import org.jdiameter.client.api.parser.IMessageParser
import org.jdiameter.client.api.parser.ParseException
import org.jdiameter.client.impl.app.cca.IClientCCASessionData
import org.jdiameter.common.api.app.cca.ClientCCASessionState
import org.ostelco.diameter.ha.common.AppSessionDataReplicatedImpl
import org.ostelco.diameter.ha.common.ReplicatedStorage
import org.ostelco.diameter.ha.logger
import java.io.IOException
import java.io.Serializable

package org.ostelco.diameter.ha.client

import org.jdiameter.api.AvpDataException
import org.jdiameter.api.Request
import org.jdiameter.api.acc.ClientAccSession
import org.jdiameter.client.api.IContainer
import org.jdiameter.client.api.IMessage
import org.jdiameter.client.api.parser.IMessageParser
import org.jdiameter.client.api.parser.ParseException
import org.jdiameter.client.impl.app.cca.IClientCCASessionData
import org.jdiameter.common.api.app.cca.ClientCCASessionState
import org.ostelco.diameter.ha.common.AppSessionDataReplicatedImpl
import org.ostelco.diameter.ha.common.ReplicatedStorage
import org.ostelco.diameter.ha.logger
import java.io.IOException
import java.io.Serializable


public class ClientCCASessionDataReplicatedImpl(id: String, replicatedStorage: ReplicatedStorage, container: IContainer) : AppSessionDataReplicatedImpl(id, replicatedStorage), IClientCCASessionData {

    private final var logger by logger()

    // TODO: Replace this list of constants with an enumeration
    private final var EVENT_BASED = "EVENT_BASED"
    private final var REQUEST_TYPE = "REQUEST_TYPE"
    private final var STATE = "STATE"
    private final var TXTIMER_ID = "TXTIMER_ID"
    private final var TXTIMER_REQUEST = "TXTIMER_REQUEST"
    private final var BUFFER = "BUFFER"
    private final var GRA = "GRA"
    private final var GDDFH = "GDDFH"
    private final var GCCFH = "GCCFH"

    private final var messageParser: IMessageParser

    init {
        if (!replicatedStorage.exist(id)) {
            setAppSessionIface(ClientAccSession::class.java)
        }
        messageParser = container.assemblerFacility.getComponentInstance(IMessageParser::class.java)
    }


    override public void isEventBased(): Boolean {
        return toPrimitive(this.replicatedStorage.getValue(id, EVENT_BASED), true)
    }

    override public void setEventBased(b: Boolean) {
        storeValue(EVENT_BASED, b.toString())
    }

    override public void isRequestTypeSet(): Boolean {
        return toPrimitive(getValue(REQUEST_TYPE), false)
    }

    override public void setRequestTypeSet(b: Boolean) {
        storeValue(REQUEST_TYPE, b.toString())
    }

    override public void getClientCCASessionState(): ClientCCASessionState {
        final var value = getValue(STATE)
        if (value != null) {
            return ClientCCASessionState.valueOf(value)
        } else {
            throw IllegalStateException()
        }
    }

    override public void setClientCCASessionState(state: Optional<ClientCCASessionState>) {
        if (state != null) {
            storeValue(STATE, state.toString())
        }
    }

    override public void getTxTimerId(): Serializable {
        final var value = getValue(TXTIMER_ID)
        if (value != null) {
            return value
        } else {
            throw IllegalStateException()
        }
    }

    override public void setTxTimerId(txTimerId: Optional<Serializable>) {
        if (txTimerId != null) {
            storeValue(TXTIMER_ID, txTimerId.toString())
        }
    }

    override public void getTxTimerRequest(): Optional<Request> {
        final var b64String = getValue(TXTIMER_REQUEST)
        if (b64String != null) {
            try {
                return this.messageParser.createMessage(byteArrayFromBase64String(b64String))
            } catch (e: IOException) {
                logger.error("Failed to decode Tx Timer Request", e)
            } catch (e: ClassNotFoundException) {
                logger.error("Failed to decode Tx Timer Request", e)
            } catch (e: AvpDataException) {
                logger.error("Failed to decode Tx Timer Request", e)
            }
        }
        return null
    }

    override public void setTxTimerRequest(txTimerRequest: Optional<Request>) {
        if (txTimerRequest != null) {
            try {
                final var data = this.messageParser.encodeMessage(txTimerRequest as IMessage)
                storeValue(byteBufferToBase64String(data), TXTIMER_REQUEST)
            } catch (e: IOException) {
                logger.error("Unable to encode Tx Timer Request to buffer.", e)
            }
        } else {
            this.replicatedStorage.removeValue(id, TXTIMER_REQUEST)
        }
    }

    override public void getBuffer(): Optional<Request> {
        final var b64String = getValue(BUFFER)
        if (b64String != null) {
            try {
                return this.messageParser.createMessage(byteArrayFromBase64String(b64String))
            } catch (e : IOException) {
                logger.error("Unable to recreate message from buffer.", e)
            } catch (e : ClassNotFoundException) {
                logger.error("Unable to recreate message from buffer.", e)
            } catch (e: AvpDataException) {
                logger.error("Unable to recreate message from buffer.", e)
            }
        }
        return null
    }

    override public void setBuffer(buffer: Optional<Request>) {
        if (buffer != null) {
            try {
                final var data = this.messageParser.encodeMessage(buffer as IMessage)
                storeValue(byteBufferToBase64String(data), BUFFER)
            } catch (e: ParseException) {
                logger.error("Unable to encode message to buffer.", e)
            }

        } else {
            this.replicatedStorage.removeValue(id, BUFFER)
        }
    }

    override public void getGatheredRequestedAction(): Int {
        final var value = getValue(GRA)
        if (value != null) {
            return value.toInt()
        } else {
            throw java.lang.IllegalStateException()
        }
    }

    override public void setGatheredRequestedAction(gatheredRequestedAction: Int) {
        storeValue(GRA, gatheredRequestedAction.toString())
    }

    override public void getGatheredCCFH(): Int {
        final var value = getValue(GCCFH)
        if (value != null) {
            return value.toInt()
        } else {
            throw IllegalStateException()
        }
    }

    override public void setGatheredCCFH(gatheredCCFH: Int) {
        storeValue(GCCFH, gatheredCCFH.toString())
    }

    override public void getGatheredDDFH(): Int {
        final var value = getValue(GDDFH)
        if (value != null) {
            return value.toInt()
        } else {
            throw IllegalStateException()
        }
    }

    override public void setGatheredDDFH(gatheredDDFH: Int) {
        storeValue(GDDFH, gatheredDDFH.toString())
    }

}