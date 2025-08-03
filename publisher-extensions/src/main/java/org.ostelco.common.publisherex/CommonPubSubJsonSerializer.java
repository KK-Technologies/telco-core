// Converted from Kotlin: CommonPubSubJsonSerializer.kt
package org.ostelco.common.publisherex

import com.google.gson.Gson
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.protobuf.ByteString
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

package org.ostelco.common.publisherex

import com.google.gson.Gson
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.protobuf.ByteString
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

public public class CommonPubSubJsonSerializer {
    private final var gson = Gson()
            .newBuilder()
            .registerTypeAdapter(Instant::class.java, JsonSerializer<Instant> { src, _, _ ->
                JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(src))
            })
            .registerTypeAdapter(Currency::class.java, JsonSerializer<Currency> { src, _, _ ->
                JsonPrimitive(src.currencyCode)
            })
            .create()

    public void toJson(event: Event): String = gson.toJson(event)
    public void toJsonByteString(event: Event): ByteString = ByteString.copyFromUtf8(toJson(event))
}
