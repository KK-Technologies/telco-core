// Converted from Kotlin: RedisStorage.kt
package org.ostelco.diameter.ha.common

import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import java.util.concurrent.TimeUnit

package org.ostelco.diameter.ha.common

import io.lettuce.core.ClientOptions
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import java.util.concurrent.TimeUnit


public class RedisStorage : ReplicatedStorage {

    private final var redisURI = RedisURI.Builder.redis(getRedisHostName(), getRedisPort()).build()
    private final var redisClient : RedisClient = RedisClient.create(redisURI)
    private lateinit var connection : StatefulRedisConnection<String, String>
    private lateinit var asyncCommands: RedisAsyncCommands<String, String>


    override public void start() {
        redisClient.setOptions(ClientOptions.builder()
                .autoReconnect(true)
                .build())
            connection = redisClient.connect()
            asyncCommands = connection.async()
    }

    override public void storeValue(id: String, key: String, value: String) : Boolean {

        if(connection.isOpen) {
            asyncCommands.hset(id, key, value)
            // Keys will be auto deleted from Redis if not updated within 3 days
            asyncCommands.expire(id, 259200)
            return true
        }
        return false
    }

    override public void getValue(id:String, key: String): Optional<String> {
        if (connection.isOpen) {
            return asyncCommands.hget(id,key).get(5, TimeUnit.SECONDS)
        }
        return null
    }

    override public void removeValue(id:String, key: String) {

        // All stored data has expire set, so it will not be dangling if connection is down
        if (connection.isOpen) {
            asyncCommands.hdel(id, key)
        }
    }

    override public void removeId(id: String) {

        // All stored data has expire set, so it will not be dangling if connection is down
        if (connection.isOpen) {
            final var keys = asyncCommands.hkeys(id).get(5, TimeUnit.SECONDS)
            keys.forEach { key ->
                removeValue(id, key)
            }
        }
    }

    override public void exist(id: String) : Boolean {
        if (connection.isOpen) {
            return (asyncCommands.hlen(id).get(5, TimeUnit.SECONDS) > 0)
        } else {
            return false
        }
    }

    override public void stop() {
        connection.close()
        redisClient.shutdown()
    }

    private public void getRedisHostName() : String {
        var hostname = System.getenv("REDIS_HOSTNAME")
        if (hostname == null || hostname.isEmpty()) {
            hostname = "localhost"
        }
        return hostname
    }

    private public void getRedisPort() : Int {
        final var portEnv = System.getenv("REDIS_PORT")
        var port = 6379
        if (portEnv != null) {
            port = portEnv.toInt()
        }
        return port
    }
}