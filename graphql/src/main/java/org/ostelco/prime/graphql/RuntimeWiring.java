// Converted from Kotlin: RuntimeWiring.kt
package org.ostelco.prime.graphql

import graphql.schema.idl.RuntimeWiring

package org.ostelco.prime.graphql

import graphql.schema.idl.RuntimeWiring

public void buildRuntimeWiring(): RuntimeWiring {
    return RuntimeWiring.newRuntimeWiring()
            .type("QueryType") { typeWiring ->
                typeWiring.dataFetcher("context", ContextDataFetcher())
            }
            .build()
}