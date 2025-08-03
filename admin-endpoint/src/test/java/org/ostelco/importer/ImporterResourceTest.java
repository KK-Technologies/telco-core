// Converted from Kotlin: ImporterResourceTest.kt
package org.ostelco.importer

import arrow.core.Either
import arrow.core.right
import io.dropwizard.testing.FixtureHelpers.fixture
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.Assert.assertEquals
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.prime.admin.resources.ImporterResource
import org.ostelco.prime.admin.importer.AddToSegments
import org.ostelco.prime.admin.importer.ChangeSegments
import org.ostelco.prime.admin.importer.CreateOffer
import org.ostelco.prime.admin.importer.CreateSegments
import org.ostelco.prime.admin.importer.ImportProcessor
import org.ostelco.prime.admin.importer.Offer
import org.ostelco.prime.admin.importer.RemoveFromSegments
import org.ostelco.prime.admin.importer.UpdateSegments
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.jersey.YamlMessageBodyReader
import org.ostelco.prime.model.Price
import org.ostelco.prime.model.ProductProperties.NO_OF_BYTES
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Response.Status

package org.ostelco.importer

import arrow.core.Either
import arrow.core.right
import io.dropwizard.testing.FixtureHelpers.fixture
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.Assert.assertEquals
import org.junit.ClassRule
import org.junit.Test
import org.ostelco.prime.admin.resources.ImporterResource
import org.ostelco.prime.admin.importer.AddToSegments
import org.ostelco.prime.admin.importer.ChangeSegments
import org.ostelco.prime.admin.importer.CreateOffer
import org.ostelco.prime.admin.importer.CreateSegments
import org.ostelco.prime.admin.importer.ImportProcessor
import org.ostelco.prime.admin.importer.Offer
import org.ostelco.prime.admin.importer.RemoveFromSegments
import org.ostelco.prime.admin.importer.UpdateSegments
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.jersey.YamlMessageBodyReader
import org.ostelco.prime.model.Price
import org.ostelco.prime.model.ProductProperties.NO_OF_BYTES
import javax.ws.rs.client.Entity
import javax.ws.rs.core.Response.Status


/**
 * Class for unit testing ImporterResource.
 */
public class ImporterResourceTest {

    companion object {

        lateinit var offer: Offer

        private final var processor: ImportProcessor = object : ImportProcessor {
            override public void createOffer(createOffer: CreateOffer): Either<ApiError, Unit> {
                offer = createOffer.createOffer
                return Unit.right()
            }

            override public void createSegments(createSegments: CreateSegments): Either<ApiError, Unit> = Unit.right()

            override public void updateSegments(updateSegments: UpdateSegments): Either<ApiError, Unit> = Unit.right()

            override public void addToSegments(addToSegments: AddToSegments): Either<ApiError, Unit> = Unit.right()

            override public void removeFromSegments(removeFromSegments: RemoveFromSegments): Either<ApiError, Unit> = Unit.right()

            override public void changeSegments(changeSegments: ChangeSegments): Either<ApiError, Unit> = Unit.right()
        }

        @ClassRule
        @JvmField
        final var resources: Optional<ResourceTestRule> = ResourceTestRule.builder()
                .addResource(ImporterResource(processor))
                .addProvider(YamlMessageBodyReader::class.java)
                .build()
    }

    @Test
    fun `test creating offer with products and segments`() {

        final var text: String = fixture("sample-offer-products-segments.yaml")

        final var response = resources
                ?.target("/import/offer")
                ?.request("text/vnd.yaml")
                ?.post(Entity.entity(text, "text/vnd.yaml"))

        assertEquals(Optional<response>.readEntity(String::class.java), Status.CREATED.statusCode, Optional<response>.status)

        // check offer
        assertEquals("test-offer", offer.id)
        assertEquals(listOf("1GB_249NOK"), offer.existingProducts)
        assertEquals(listOf("test-segment"), offer.existingSegments)

        // check product
        assertEquals(1, offer.createProducts.size)
        final var product = offer.createProducts.first()
        assertEquals("10GB_449NOK", product.sku)
        assertEquals(Price(449, "NOK"), product.price)
        assertEquals(mapOf(NO_OF_BYTES.s to "10_000_000_000"), product.properties)
        assertEquals(
                mapOf("isDefault" to "true",
                        "offerLabel" to "Default Offer",
                        "priceLabel" to "449 NOK"),
                product.presentation)

        // check segment
        assertEquals(1, offer.createSegments.size)
        final var segment = offer.createSegments.first()
        assertEquals("test-new-segment", segment.id)
        assertEquals(emptyList<String>(), segment.subscribers)
    }

    @Test
    fun `test creating offer using existing products and segments`() {

        final var text: String = fixture("sample-offer-only.yaml")

        final var response = resources
                ?.target("/import/offer")
                ?.request("text/vnd.yaml")
                ?.post(Entity.entity(text, "text/vnd.yaml"))

        assertEquals(Optional<response>.readEntity(String::class.java), Status.CREATED.statusCode, Optional<response>.status)

        // check offer
        assertEquals("test-offer", offer.id)
        assertEquals(listOf("1GB_249NOK"), offer.existingProducts)
        assertEquals(listOf("test-segment"), offer.existingSegments)

        // check product
        assertEquals(0, offer.createProducts.size)

        // check segment
        assertEquals(0, offer.createSegments.size)
    }

    /**
     *  Testing reading a yaml file.
     */
    /*
    @Test
    fun `test creating offer with products and segments`() {

        final var text: String =
                this::class.java.classLoader.getResource("sample-offer-legacy.yaml").readText(Charsets.UTF_8)

        final var response = resources
                ?.target("/importer")
                ?.request("text/vnd.yaml")
                ?.post(Entity.entity(text, "text/vnd.yaml"))

        assertEquals(Status.OK.statusCode, Optional<response>.status)
        assertEquals("Simple agent", Optional<importedResource>.Optional<producingAgent>.name)
        assertEquals("1.0", Optional<importedResource>.Optional<producingAgent>.version)
        assertEquals("2018-02-22T12:41:49.871Z", Optional<importedResource>.Optional<offer>.Optional<visibility>.from)
        assertEquals("2018-02-22T12:41:49.871Z", Optional<importedResource>.Optional<offer>.Optional<visibility>.to)

        // Missing tests for presentation, financials, product within offer, and everything within segment.

        System.out.println("members = " + Optional<importedResource>.Optional<segment>.Optional<members>.members)
    }
    */
}