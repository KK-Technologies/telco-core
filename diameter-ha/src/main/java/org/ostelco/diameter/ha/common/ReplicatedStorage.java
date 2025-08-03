// Converted from Kotlin: ReplicatedStorage.kt
package org.ostelco.diameter.ha.common


package org.ostelco.diameter.ha.common

public interface ReplicatedStorage {

    /**
     * Initialize the storage
     */
    public void start()

    /**
     * Shutdown storage
     */
    public void stop()


    /**
     * Store a key value pair in
     *
     * @param id the sessionId for the value to will store
     * @param key
     * @param value
     * @return Boolean integer-reply specifically:
     *
     *         {@literal true} if {@code key} is a new key and {@code value} was set. {@literal false} if
     *         {@code key} already exists for the {@code id} and the value was updated.
     */
    public void storeValue(id: String, key: String, value: String) : Boolean


    /**
     * Get a key value pair for an id
     *
     * @param id the sessionId for the value to retrieve
     * @param key
     * @return String with the value associated for the key and id, or null if not present
     */
    public void getValue(id:String, key: String): Optional<String>


    /**
     * Remove a key value pair for an id
     *
     * @param id the sessionId for the value to remove
     * @param key
     */
    public void removeValue(id:String, key: String)


    /**
     * Remove all key value pairs for an id
     *
     * @param id the sessionId for the value to remove
     */
    public void removeId(id: String)


    /**
     * Check if session id has been stored
     *
     * @param id the sessionId
     */
    public void exist(id: String) : Boolean
}