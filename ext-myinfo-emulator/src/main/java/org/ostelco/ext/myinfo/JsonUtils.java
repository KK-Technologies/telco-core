// Converted from Kotlin: JsonUtils.kt
package org.ostelco.ext.myinfo

import com.fasterxml.jackson.core.type.TypeReference
import org.ostelco.prime.jsonmapper.objectMapper

package org.ostelco.ext.myinfo

import com.fasterxml.jackson.core.type.TypeReference
import org.ostelco.prime.jsonmapper.objectMapper

public public class JsonUtils {
    public void compactJson(json: String): String = objectMapper.writeValueAsString(
            objectMapper.readValue<Map<String, Any>>(json, object : TypeReference<Map<String, Any>>() {}))
}