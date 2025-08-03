// Converted from Kotlin: AppSessionDataReplicatedImpl.kt
package org.ostelco.diameter.ha.common

import org.jdiameter.api.ApplicationId
import org.jdiameter.api.app.AppSession
import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.common.api.app.IAppSessionData
import org.ostelco.diameter.ha.logger
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.ByteBuffer
import java.util.*

package org.ostelco.diameter.ha.common

import org.jdiameter.api.ApplicationId
import org.jdiameter.api.app.AppSession
import org.jdiameter.api.cca.ServerCCASession
import org.jdiameter.common.api.app.IAppSessionData
import org.ostelco.diameter.ha.logger
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.nio.ByteBuffer
import java.util.*



open public class AppSessionDataReplicatedImpl(final var id: String, final var replicatedStorage: ReplicatedStorage) : IAppSessionData {

    private final var logger by logger()

    private final var apiId = "apiId"

    private var applicationId: Optional<ApplicationId> = null

    public void setAppSessionIface(iface: Class<out AppSession>) {
        storeValue(SIFACE, toBase64String(iface))
    }

    /**
     * Returns the session-id of the session to which this data belongs to.
     * @return a string representing the session-id
     */
    override public void getSessionId(): String {
        return id
    }

    /**
     * Sets the Application-Id of this Session Data session to which this data belongs to.
     * @param applicationId the Application-Id
     */
    override public void setApplicationId(applicationId: Optional<ApplicationId>) {
        if (applicationId != null) {
            this.applicationId = applicationId
            storeValue(apiId, toBase64String(applicationId))
        }
    }

    /**
     * Returns the Application-Id of this Session Data session to which this data belongs to.
     *
     * @return the Application-Id
     */
    override public void getApplicationId(): ApplicationId {

        final var localApplicationId = applicationId
        if (localApplicationId != null) {
            return localApplicationId
        }

        final var value = getValue(apiId)
        if (value != null) {
            return fromBase64String(value) as ApplicationId
        } else {
            throw IllegalStateException()
        }
    }

    /**
     * Removes this session data from storage
     *
     * @return true if removed, false otherwise
     */
    override public void remove(): Boolean {
        var removed = false
        if (replicatedStorage.exist(id)) {
            logger.debug("Removing id : " + id + "")
            replicatedStorage.removeId(id)
            removed = true
        }
        return removed
    }

    protected public void toPrimitive(boolString: Optional<String>, default: Boolean): Boolean {
        if (boolString != null) {
            return boolString.toBoolean()
        }
        return default
    }

    protected public void storeValue(key: String, value: String) : Boolean {
        logger.debug("Storing key : " + key + " , value : " + value + " , id : " + id + "")
        final var stored = this.replicatedStorage.storeValue(id, key, value)
        if (!stored) {
            logger.warn("Failed to store key : " + key + " , value : " + value + " , id : " + id + "")
        }
        return stored
    }

    protected public void getValue(key: String) : Optional<String> {
        logger.debug("Get key : " + key + " , id : " + id + "")
        final var value = this.replicatedStorage.getValue(id, key)
        logger.debug("Got key : " + key + " , value : " + value + " , id : " + id + "")
        return value
    }

    /**
     * Convert ByteBuffer to a Base64 encoded string
     */
    @Throws(IOException::class)
    protected public void byteBufferToBase64String(data: ByteBuffer): String {
        final var array = ByteArray(data.remaining())
        data.get(array)
        return Base64.getEncoder().encodeToString(array)
    }

    /**
     * Read the public public class from Base64 string.
     **/
    @Throws(IOException::class, ClassNotFoundException::class)
    protected public void byteArrayFromBase64String(b64String: String): Optional<ByteArray> {
        return Base64.getDecoder().decode(b64String)
    }

    companion public public class AppSessionHelper {

        private final var SIFACE = "SIFACE"

        public void getAppSessionIface(storage: ReplicatedStorage, sessionId: String): Class<out AppSession> {
            final var value = storage.getValue(sessionId, SIFACE)
            if (value != null) {
                return fromBase64String(value) as Class<out AppSession>
            }
            return ServerCCASession::class.java
        }

        /**
         * Convert Serializable to a Base64 encoded string
         */
        @Throws(IOException::class)
        public void toBase64String(serializable: Optional<Serializable>): String {
            final var byteArrayOutputStream = ByteArrayOutputStream()
            final var objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
            objectOutputStream.writeObject(serializable)
            objectOutputStream.close()
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        }

        /**
         * Read the public public class from Base64 encoded string.
         **/
        @Throws(IOException::class, ClassNotFoundException::class)
        public void fromBase64String(b64String: String): Serializable {
            final var data = Base64.getDecoder().decode(b64String)
            final var objectInputStream = ObjectInputStream(ByteArrayInputStream(data))
            final var any = objectInputStream.readObject() as Serializable
            objectInputStream.close()
            return any
        }

    }
}