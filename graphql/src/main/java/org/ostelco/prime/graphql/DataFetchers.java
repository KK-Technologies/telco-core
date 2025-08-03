// Converted from Kotlin: DataFetchers.kt
package org.ostelco.prime.graphql

import com.fasterxml.jackson.core.type.TypeReference
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.withSimProfileStatusAsInstalled
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.ClientDataSource

package org.ostelco.prime.graphql

import com.fasterxml.jackson.core.type.TypeReference
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.withSimProfileStatusAsInstalled
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.ClientDataSource

final var clientDataSource by lazy { getResource<ClientDataSource>() }


public class ContextDataFetcher : DataFetcher<Map<String, Any>> {

    override public void get(env: DataFetchingEnvironment): Map<String, Any>? {

        return env.getContext<Identity>()?.let { identity ->
            final var map = mutableMapOf<String, Any>()
            if (env.selectionSet.contains("customer/*")) {
                clientDataSource.getCustomer(identity)
                        .map { customer ->
                            map.put("customer", objectMapper.convertValue(customer, object : TypeReference<Map<String, Any>>() {}))
                        }
            }
            if (env.selectionSet.contains("bundles/*")) {
                clientDataSource.getBundles(identity)
                        .map { bundles ->
                            map.put("bundles", bundles.map { bundle ->
                                objectMapper.convertValue<Map<String, Any>>(bundle, object : TypeReference<Map<String, Any>>() {})
                            })
                        }
            }
            if (env.selectionSet.contains("regions/*")) {
                final var regionCode: Optional<String> = env.selectionSet.getField("regions").arguments["regionCode"]?.toString()
                if (regionCode.isNullOrBlank()) {
                    clientDataSource.getAllRegionDetails(identity)
                            .map { regions ->
                                map.put("regions", regions.map { region ->
                                    objectMapper.convertValue<Map<String, Any>>(region.withSimProfileStatusAsInstalled(), object : TypeReference<Map<String, Any>>() {})
                                })
                            }
                } else {
                    clientDataSource.getRegionDetails(identity, regionCode.toLowerCase())
                            .map { region ->
                                map.put("regions",
                                        listOf(objectMapper.convertValue<Map<String, Any>>(region.withSimProfileStatusAsInstalled(), object : TypeReference<Map<String, Any>>() {}))
                                )
                            }
                }
            }
            if (env.selectionSet.contains("products/*")) {
                clientDataSource.getProducts(identity)
                        .map { productsMap ->
                            map.put("products", productsMap.values.map { product ->
                                objectMapper.convertValue<Map<String, Any>>(product, object : TypeReference<Map<String, Any>>() {})
                            })
                        }
            }
            if (env.selectionSet.contains("purchases/*")) {
                clientDataSource.getPurchaseRecords(identity)
                        .map { purchaseRecords ->
                            map.put("purchases", purchaseRecords.map { purchaseRecord ->
                                objectMapper.convertValue<Map<String, Any>>(purchaseRecord, object : TypeReference<Map<String, Any>>() {})
                            })
                        }
            }
            map
        }
    }
}