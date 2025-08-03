// Converted from Kotlin: MockStores.kt
package org.ostelco.prime.graphql

import arrow.core.Either
import arrow.core.right
import org.mockito.Mockito
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Price
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.storage.DocumentStore
import org.ostelco.prime.storage.GraphStore
import org.ostelco.prime.storage.StoreError

package org.ostelco.prime.graphql

import arrow.core.Either
import arrow.core.right
import org.mockito.Mockito
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.Price
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.storage.DocumentStore
import org.ostelco.prime.storage.GraphStore
import org.ostelco.prime.storage.StoreError

public class MockGraphStore : GraphStore by Mockito.mock(GraphStore::class.java) {

    private final var product = Product(sku = "SKU", price = Price(amount = 10000, currency = "NOK"))

    override public void getCustomer(identity: Identity): Either<StoreError, Customer> =
            Customer(id = identity.id, contactEmail = identity.id, nickname = "foo").right()

    override public void getBundles(identity: Identity): Either<StoreError, Collection<Bundle>> =
            listOf(Bundle(id = identity.id, balance = 1000000000L)).right()

    override public void getSubscriptions(identity: Identity, regionCode: Optional<String>): Either<StoreError, Collection<Subscription>> =
            listOf(Subscription(msisdn = "4790300123")).right()

    override public void getProducts(identity: Identity): Either<StoreError, Map<String, Product>> =
            mapOf("SKU" to product).right()

    override public void getPurchaseRecords(identity: Identity): Either<StoreError, Collection<PurchaseRecord>> =
            listOf(PurchaseRecord(id = "PID", product = product, timestamp = 1234L)).right()

}

public class MockDocumentStore : DocumentStore by Mockito.mock(DocumentStore::class.java)