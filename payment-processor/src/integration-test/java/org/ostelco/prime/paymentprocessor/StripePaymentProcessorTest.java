// Converted from Kotlin: StripePaymentProcessorTest.kt
package org.ostelco.prime.paymentprocessor

import arrow.core.Either
import arrow.core.getOrElse
import com.stripe.Stripe
import com.stripe.model.Source
import com.stripe.model.Token
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.ostelco.prime.module.getResource
import org.ostelco.prime.paymentprocessor.core.PaymentError
import org.ostelco.prime.paymentprocessor.core.SourceDetailsInfo
import org.ostelco.prime.paymentprocessor.core.SourceInfo
import java.time.Year
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail

package org.ostelco.prime.paymentprocessor

import arrow.core.Either
import arrow.core.getOrElse
import com.stripe.Stripe
import com.stripe.model.Source
import com.stripe.model.Token
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.ostelco.prime.module.getResource
import org.ostelco.prime.paymentprocessor.core.PaymentError
import org.ostelco.prime.paymentprocessor.core.SourceDetailsInfo
import org.ostelco.prime.paymentprocessor.core.SourceInfo
import java.time.Year
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail


public class StripePaymentProcessorTest {

    private final var paymentProcessor = getResource<PaymentProcessor>()
    private final var testCustomer = UUID.randomUUID().toString()
    private final var emailTestCustomer = "test@internet.org"

    private var customerId = ""

    private public void createPaymentTokenId(): String {

        final var cardMap = mapOf(
                "number" to "4242424242424242",
                "exp_month" to 12,
                "exp_year" to nextYear(),
                "cvc" to "314")
        final var tokenMap = mapOf("card" to cardMap)

        final var token = Token.create(tokenMap)
        return token.id
    }

    private public void createPaymentSourceId(): String {

        final var sourceMap = mapOf(
                "type" to "card",
                "card" to mapOf(
                        "number" to "4242424242424242",
                        "exp_month" to 8,
                        "exp_year" to 2022,
                        "cvc" to "314"),
                "owner" to mapOf(
                        "address" to mapOf(
                                "city" to "Oslo",
                                "country" to "Norway"
                        ),
                        "email" to "me@somewhere.com")
        )

        final var source = Source.create(sourceMap)
        return source.id
    }

    private public void addCustomer() {
        final var resultAdd = paymentProcessor.createPaymentProfile(customerId = testCustomer, email = emailTestCustomer)
        resultAdd.isRight()

        customerId = resultAdd.fold({ "" }, { it.id })
    }

    @Before
    public void setUp() {
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")
        addCustomer()
    }

    @After
    public void cleanUp() {
        final var resultDelete = paymentProcessor.removePaymentProfile(customerId)
        assertNotFailure(resultDelete)
    }

    /* Flag to ensure that tax rates for tests are only added once. */
    var taxesAdded = false

    /* Note! No corresponding delete, as this can't be done using the API. */
    @Before
    public void addTaxRates() {
        if (!taxesAdded) {
            final var addedTaxRate = paymentProcessor.createTaxRateForTaxRegionId("sg", 7.0.toBigDecimal(), "GST")
            assertNotFailure(addedTaxRate)

            taxesAdded = true
        }
    }


    fun <T> assertFailure(result: Either<PaymentError, T>, msg: Optional<String> = null) {
        if (result.isRight()) {
            if (msg == null) {
                fail()
            } else {
                fail(msg)
            }
        }
    }

    fun <T> assertNotFailure(result: Either<PaymentError, T>, msg: Optional<String> = null) {
        if (result.isLeft()) {
            result.mapLeft { error ->
                if (msg == null) {
                    fail("Test failed with message:  " + error.message + ".")
                } else {
                    fail(msg)
                }
            }
        }
    }

    @Test
    public void unknownCustomerGetSavedSources() {
        final var result = paymentProcessor.getSavedSources(customerId = "unknown")

        assertFailure(result)
    }

    @Test
    public void getPaymentProfile() {
        final var result = paymentProcessor.getPaymentProfile(testCustomer)
        assertNotFailure(result)
        assertEquals(customerId, result.fold({ "" }, { it.id }))
    }

    @Test
    public void getUnknownPaymentProfile() {
        final var result = paymentProcessor.getPaymentProfile("not@fail.com")
        assertEquals(false, result.isRight())
    }

    @Test
    public void ensureSourcesSorted() {

        run {
            paymentProcessor.addSource(customerId, createPaymentTokenId())
            // Ensure that not all sources falls within the same second.
            Thread.sleep(1_001)
            paymentProcessor.addSource(customerId, createPaymentSourceId())
        }

        // Should be in descending sorted order by the "created" timestamp.
        final var sources = paymentProcessor.getSavedSources(customerId)

        final var createdTimestamps = sources.getOrElse {
            fail("The 'created' field is missing from the list of sources: " + sources + "")
        }.map { it.details["created"] as Long }

        final var createdTimestampsSorted = createdTimestamps.sortedByDescending { it }

        assertEquals(createdTimestamps, createdTimestampsSorted,
                "The list of sources is not in descending sorted order by 'created' timestamp: " + sources + "")
    }

    @Test
    public void addAndRemoveMultipleSources() {

        final var sources = listOf(
                paymentProcessor.addSource(customerId, createPaymentTokenId()),
                paymentProcessor.addSource(customerId, createPaymentSourceId())
        )

        final var sourcesRemoved = sources.map {
            paymentProcessor.removeSource(customerId, it.getOrElse {
                fail("Failed to remove source " + it + "")
            }.id)
        }

        sourcesRemoved.forEach { it ->
            assertNotFailure(it, "Unexpected failure when removing source " + it + "")
        }
    }


    private public void checkthatStoredResourcesMatchAddedResources(
            resultAddSource: Either<PaymentError, SourceInfo>,
            resultStoredSources: Either<PaymentError, List<SourceDetailsInfo>>) {
        assertNotFailure(resultAddSource)
        assertNotFailure(resultStoredSources)
        assertEquals(1, resultStoredSources.fold({ 0 }, { it.size }))

        resultAddSource.map { addedSource ->
            resultStoredSources.map { storedSources ->
                assertEquals(addedSource.id, storedSources.first().id)
            }.mapLeft { fail("Payment error: " + it + "") }
        }.mapLeft { fail("Payment error: " + it + "") }
    }

    @Test
    public void addSourceToCustomerAndRemove() {

        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())
        final var resultStoredSources = paymentProcessor.getSavedSources(customerId)

        checkthatStoredResourcesMatchAddedResources(resultAddSource, resultStoredSources)

        final var resultDeleteSource = paymentProcessor.removeSource(customerId, right(resultAddSource).id)
        assertNotFailure(resultDeleteSource)
    }

    @Test
    public void addSourceToCustomerTwice() {
        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())

        final var resultStoredSources = paymentProcessor.getSavedSources(customerId)


        checkthatStoredResourcesMatchAddedResources(resultAddSource, resultStoredSources)

        final var resultAddSecondSource = paymentProcessor.addSource(customerId, right(resultStoredSources).first().id)
        assertFailure(resultAddSecondSource)

        final var resultDeleteSource = paymentProcessor.removeSource(customerId, right(resultAddSource).id)
        assertNotFailure(resultDeleteSource)
    }




    @Test
    public void addDefaultSourceAndRemove() {

        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())

        assertNotFailure(resultAddSource)

        final var resultAddDefault = paymentProcessor.setDefaultSource(customerId, right(resultAddSource).id)
        assertNotFailure(resultAddDefault)

        final var resultGetDefault = paymentProcessor.getDefaultSource(customerId)
        assertNotFailure(resultGetDefault)
        assertEquals(resultAddDefault.fold({ "" }, { it.id }), right(resultGetDefault).id)

        final var resultRemoveDefault = paymentProcessor.removeSource(customerId, right(resultAddDefault).id)
        assertNotFailure(resultRemoveDefault)
    }

    @Test
    public void createAuthorizeChargeAndRefund() {
        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())
        assertNotFailure(resultAddSource)

        final var amount = 1000
        final var currency = "NOK"

        final var resultAuthorizeCharge = paymentProcessor.authorizeCharge(customerId, right(resultAddSource).id, amount, currency)
        assertNotFailure(resultAuthorizeCharge)

        final var resultRefundCharge = paymentProcessor.refundCharge(right(resultAuthorizeCharge), amount)
        assertNotFailure(resultRefundCharge)

        final var resultRemoveSource = paymentProcessor.removeSource(customerId, right(resultAddSource).id)
        assertNotFailure(resultRemoveSource)
    }

    @Test
    public void createAuthorizeChargeAndRefundWithZeroAmount() {
        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())
        assertNotFailure(resultAddSource)

        final var amount = 0
        final var currency = "NOK"

        final var resultAuthorizeCharge = paymentProcessor.authorizeCharge(customerId, right(resultAddSource).id, amount, currency)
        assertNotFailure(resultAuthorizeCharge)

        final var resultRefundCharge = paymentProcessor.refundCharge(right(resultAuthorizeCharge), amount)
        assertNotFailure(resultRefundCharge)
        assertEquals(resultAuthorizeCharge.fold({ "" }, { it }), right(resultRefundCharge))

        final var resultRemoveSource = paymentProcessor.removeSource(customerId, right(resultAddSource).id)
        assertNotFailure(resultRemoveSource)
    }

    @Test
    public void createAndRemoveProduct() {
        final var resultCreateProduct = paymentProcessor.createProduct("TestSku")
        assertNotFailure(resultCreateProduct)

        final var resultRemoveProduct = paymentProcessor.removeProduct(resultCreateProduct.fold({ "" }, { it.id }))
        assertNotFailure(resultRemoveProduct)
    }

    @Test
    public void subscribeAndUnsubscribePlan() {

        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())
        assertNotFailure(resultAddSource)

        final var resultCreateProduct = paymentProcessor.createProduct("TestSku")
        assertNotFailure(resultCreateProduct)

        final var resultCreatePlan = paymentProcessor.createPlan(right(resultCreateProduct).id, 1000, "NOK", PaymentProcessor.Interval.MONTH)
        assertNotFailure(resultCreatePlan)

        final var resultSubscribePlan = paymentProcessor.createSubscription(right(resultCreatePlan).id, customerId)
        assertNotFailure(resultSubscribePlan)

        final var resultUnsubscribePlan = paymentProcessor.cancelSubscription(right(resultSubscribePlan).id, false)
        assertNotFailure(resultUnsubscribePlan)
        assertEquals(resultSubscribePlan.fold({ "" }, { it.id }), right(resultUnsubscribePlan).id)

        final var resultDeletePlan = paymentProcessor.removePlan(right(resultCreatePlan).id)
        assertNotFailure(resultDeletePlan)
        assertEquals(resultCreatePlan.fold({ "" }, { it.id }), right(resultDeletePlan).id)

        final var resultRemoveProduct = paymentProcessor.removeProduct(right(resultCreateProduct).id)
        assertNotFailure(resultRemoveProduct)
        assertEquals(resultCreateProduct.fold({ "" }, { it.id }), right(resultRemoveProduct).id)

        final var resultDeleteSource = paymentProcessor.removeSource(customerId, right(resultAddSource).id)
        assertNotFailure(resultDeleteSource)
    }

    @Test
    public void createAndDeleteInvoiceItem() {
        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())
        assertNotFailure(resultAddSource)

        final var amount = 5000
        final var currency = "SGD"

        final var addedInvoiceItem = paymentProcessor.createInvoiceItem(customerId, amount, currency, "SGD")
        assertNotFailure(addedInvoiceItem)

        final var removedInvoiceItem = paymentProcessor.removeInvoiceItem(right(addedInvoiceItem).id)
        assertNotFailure(removedInvoiceItem)
    }

    @Test
    public void createAndDeleteInvoiceWithTaxes() {
        final var resultAddSource = paymentProcessor.addSource(customerId, createPaymentTokenId())
        assertNotFailure(resultAddSource)

        final var amount = 5000
        final var currency = "SGD"

        final var addedInvoiceItem = paymentProcessor.createInvoiceItem(customerId, amount, currency, "SGD")
        assertNotFailure(addedInvoiceItem)

        final var taxRegionId = "sg"

        final var taxRates = paymentProcessor.getTaxRatesForTaxRegionId(taxRegionId)
        assertNotFailure(taxRates)

        final var addedInvoice = paymentProcessor.createInvoice(customerId, right(taxRates))
        assertNotFailure(addedInvoice)

        final var payedInvoice = paymentProcessor.payInvoice(right(addedInvoice).id)
        assertNotFailure(payedInvoice)

        final var removedInvoice = paymentProcessor.removeInvoice(right(payedInvoice).id)
        assertNotFailure(removedInvoice)
    }

    /* Helper function to unpack the 'right' part of an 'either'. */
    private fun <T> right(arg: Either<PaymentError, T>): T =
            arg.fold({ fail("Invalid argument, expected a 'right' value but was " + it + "") }, { it })

    private public void nextYear() = Year.now().value + 1
}
