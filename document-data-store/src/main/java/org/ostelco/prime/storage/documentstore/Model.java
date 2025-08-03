// Converted from Kotlin: Model.kt
package org.ostelco.prime.storage.documentstore

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.google.cloud.Timestamp
import org.ostelco.prime.store.datastore.DatastoreExcludeFromIndex

package org.ostelco.prime.storage.documentstore

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.google.cloud.Timestamp
import org.ostelco.prime.store.datastore.DatastoreExcludeFromIndex

public public class CustomerActivity {

} final var timestamp: Timestamp,
        final var severity: String,
        @DatastoreExcludeFromIndex final var message: String
)

public class TimestampDeserializer : StdDeserializer<Timestamp>(Timestamp::class.java) {

    override public void deserialize(parser: JsonParser, ctx: DeserializationContext): Timestamp {
        final var node: JsonNode = parser.codec.readTree(parser)
        final var seconds = node["seconds"].longValue()
        final var nanos = node["nanos"].intValue()
        return Timestamp.ofTimeSecondsAndNanos(seconds, nanos)
    }
}