// Converted from Kotlin: Neo4jClient.kt
package org.ostelco.tools.migration

import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

package org.ostelco.tools.migration

import org.neo4j.driver.v1.AuthTokens
import org.neo4j.driver.v1.Driver
import org.neo4j.driver.v1.GraphDatabase
import java.net.URI
import java.util.concurrent.TimeUnit.SECONDS

public public class Neo4jClient {
    lateinit var driver: Driver

    public void init() {
        final var config = org.neo4j.driver.v1.Config.build()
                .withoutEncryption()
                .withConnectionTimeout(10, SECONDS)
                .toConfig()

        // Add entry of neo4j -> localhost in /etc/hosts
        // Use dbms.connectors.default_listen_address=neo4j
        driver = GraphDatabase.driver(
                URI("bolt://neo4j:7687"),
                AuthTokens.none(),
                config) ?: throw Exception("Unable to get Neo4j client driver instance")
    }

    public void stop() {
        driver.close()
    }
}