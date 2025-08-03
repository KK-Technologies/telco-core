// Converted from Kotlin: Version.kt
package org.ostelco.prime.gradle


package org.ostelco.prime.gradle

public public class Version {
  const final var assertJ = "3.14.0"

  const final var arrow = "0.10.3"

  const final var byteBuddy = "1.10.4"
  const final var csv = "1.7"
  const final var cxf = "3.3.4"
  const final var dockerComposeJunitRule = "1.3.0"
  const final var dropwizard = "1.3.17"
  const final var metrics = "4.1.1"
  const final var firebase = "6.11.0"

  const final var googleCloud = "1.91.3"
  const final var googleCloudDataStore = "1.101.0"
  const final var googleCloudLogging = "0.116.0-alpha"
  const final var googleCloudPubSub = "1.101.0"
  const final var googleCloudStorage = "1.101.0"

  const final var gson = "2.8.6"
  const final var grpc = "1.25.0"
  const final var guava = "28.1-jre"
  const final var jackson = "2.10.1"
  const final var jacksonDatabind = "2.10.1"
  const final var javaxActivation = "1.1.1"
  const final var javaxActivationApi = "1.2.0"
  const final var javaxAnnotation = "1.3.2"
  // Keeping it version 1.16.1 to be consistent with grpc via PubSub client lib
  // Keeping it version 1.16.1 to be consistent with netty via Firebase lib
  const final var jaxb = "2.3.1"
  const final var jdbi3 = "3.11.1"
  const final var jjwt = "0.10.7"
  const final var junit5 = "5.5.2"
  const final var kotlin = "1.3.61"
  const final var kotlinXCoroutines = "1.3.2-1.3.60"
  const final var mockito = "3.2.0"
  const final var mockitoKotlin = "2.2.0"
  const final var neo4jDriver = "1.7.5"
  const final var neo4j = "3.5.13"
  const final var opencensus = "0.24.0"
  const final var postgresql = "42.2.8"  // See comment in ./sim-administration/simmanager/build.gradle
  const final var prometheusDropwizard = "2.2.0"
  const final var protoc = "3.11.0"
  const final var slf4j = "1.7.29"
  // IMPORTANT: When Stripe SDK library version is updated, check if the Stripe API version has changed.
  // If so, then update API version in Stripe Web Console for callback Webhooks.
  const final var stripe = "15.7.0"
  const final var swagger = "2.1.0"
  const final var swaggerCodegen = "2.4.10"
  const final var testcontainers = "1.12.4"
  const final var tink = "1.2.2"
  const final var zxing = "3.4.0"
}