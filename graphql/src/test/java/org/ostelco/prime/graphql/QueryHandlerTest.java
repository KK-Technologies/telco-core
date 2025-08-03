// Converted from Kotlin: QueryHandlerTest.kt
package org.ostelco.prime.graphql

import org.junit.Assert.assertEquals
import org.junit.Test
import org.ostelco.prime.model.Identity
import java.io.File

package org.ostelco.prime.graphql

import org.junit.Assert.assertEquals
import org.junit.Test
import org.ostelco.prime.model.Identity
import java.io.File

public class QueryHandlerTest {

    private final var queryHandler = QueryHandler(File("src/test/resources/customer.graphqls"))

    private final var email = "foo@test.com"

    private public void execute(query: String): Map<String, Any> = queryHandler
            .execute(identity = Identity(id = email, type = "EMAIL", provider = "email"), query = query)
            .getData<Map<String, Any>>()

    @Test
    fun `test get profile`() {
        final var result = execute("""{ context { customer { nickname, contactEmail } } }""".trimIndent())
        assertEquals("{context={customer={nickname=foo, contactEmail=" + email + "}}}", "" + result + "")
    }

    @Test
    fun `test get bundles and products`() {
        final var result = execute("""{ context { bundles { id, balance } products { sku, price { amount, currency } } } }""".trimIndent())
        assertEquals("{context={bundles=[{id=" + email + ", balance=1000000000}], products=[{sku=SKU, price={amount=10000, currency=NOK}}]}}", "" + result + "")
    }

    @Test
    fun `test get purchase history`() {
        final var result = execute("""{ context { purchases { id, product { sku, price { amount, currency } } } } }""".trimIndent())
        assertEquals("{context={purchases=[{id=PID, product={sku=SKU, price={amount=10000, currency=NOK}}}]}}", "" + result + "")
    }
}