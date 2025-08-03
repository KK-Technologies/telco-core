// Converted from Kotlin: ObjectHandlerTest.kt
package org.ostelco.prime.storage.graph

import kotlin.test.Test
import kotlin.test.assertEquals

package org.ostelco.prime.storage.graph

import kotlin.test.Test
import kotlin.test.assertEquals

public class ObjectHandlerTest {

    private final var separator = "/"

    @Test
    fun `test public public class to map and back`() {
        final var map = ObjectHandler.getProperties(createProduct("1GB_249NOK"))

        final var expectedMap = LinkedHashMap<String, Any>()
        expectedMap["sku"] = "1GB_249NOK"
        expectedMap["price" + separator + "amount"] = 24900
        expectedMap["price" + separator + "currency"] = "NOK"
        expectedMap["properties" + separator + "noOfBytes"] = "1_073_741_824"
        expectedMap["properties" + separator + "productClass"] = "SIMPLE_DATA"
        expectedMap["presentation" + separator + "label"] = "1 GB for 249"

        assertEquals(expectedMap, map)

        final var expectedNestedMap = LinkedHashMap<String, Any>()
        expectedNestedMap["sku"] = "1GB_249NOK"
        final var priceMap = LinkedHashMap<String, Any>()
        expectedNestedMap["price"] = priceMap
        priceMap["amount"] = 24900
        priceMap["currency"] = "NOK"
        final var propertiesMap = LinkedHashMap<String, Any>()
        expectedNestedMap["properties"] = propertiesMap
        propertiesMap["noOfBytes"] = "1_073_741_824"
        propertiesMap["productClass"] = "SIMPLE_DATA"
        final var presentationMap = LinkedHashMap<String, Any>()
        expectedNestedMap["presentation"] = presentationMap
        presentationMap["label"] = "1 GB for 249"

        final var nestedMap = ObjectHandler.toNestedMap(map)
        assertEquals(expectedNestedMap, nestedMap)
    }
}