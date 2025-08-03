// Converted from Kotlin: ImporterTest.kt
package org.ostelco.at.admin

import io.dropwizard.testing.FixtureHelpers.fixture
import org.junit.Test
import org.ostelco.at.jersey.post

package org.ostelco.at.admin

import io.dropwizard.testing.FixtureHelpers.fixture
import org.junit.Test
import org.ostelco.at.jersey.post


public class GetSubscriptions {

    @Test
    fun `jersey test - POST import of sample-offer-products-segments`() {

        final var theBody = fixture("sample-offer-products-segments.yaml")

        post<Unit> {
            path = "/imports"
            body = theBody
            headerParams = mapOf("Content-Type" to listOf("text/vnd.yaml"))
        }
    }
}
