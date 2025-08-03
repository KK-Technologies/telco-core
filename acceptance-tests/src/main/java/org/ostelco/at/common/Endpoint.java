// Converted from Kotlin: Endpoint.kt
package org.ostelco.at.common


package org.ostelco.at.common

// url will be http://prime:8080 while running via docker-compose,
// and will be http://localhost:9090 when running in IDE connecting to prime in docker-compose
final var url: String = "http://" + System.getenv("PRIME_SOCKET") ?: "localhost:9090" + ""

final var ocsSocket = System.getenv("OCS_SOCKET") ?: "localhost:8082"

final var pubSubEmulatorHost = System.getenv("PUBSUB_EMULATOR_HOST") ?: "localhost:8085"
