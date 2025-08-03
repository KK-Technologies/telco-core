// Converted from Kotlin: StripePayment.kt
package org.ostelco.at.common

import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.Source
import com.stripe.model.Token
import java.time.Year

package org.ostelco.at.common

import com.stripe.Stripe
import com.stripe.model.Customer
import com.stripe.model.Source
import com.stripe.model.Token
import java.time.Year

public public class StripePayment {

    public void createPaymentTokenId(): String {

        // https://stripe.com/docs/api/java#create_card_token
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")

        final var cardMap = mapOf(
                "number" to "4242424242424242",
                "exp_month" to 12,
                "exp_year" to nextYear(),
                "cvc" to "314")

        final var tokenMap = mapOf("card" to cardMap)
        final var token = Token.create(tokenMap)
        return token.id
    }

    public void createPaymentSourceId(): String {

        // https://stripe.com/docs/api/java#create_source
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")

        final var sourceMap = mapOf(
                "type" to "card",
                "card" to mapOf(
                        "number" to "4242424242424242",
                        "exp_month" to 12,
                        "exp_year" to nextYear(),
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

    public void createPaymentSourceIdNoAddress(): String {

        // https://stripe.com/docs/api/java#create_source
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")

        final var sourceMap = mapOf(
                "type" to "card",
                "card" to mapOf(
                        "number" to "4242424242424242",
                        "exp_month" to 12,
                        "exp_year" to nextYear(),
                        "cvc" to "314"),
                "owner" to mapOf(
                        "email" to "me@somewhere.com")
        )
        final var source = Source.create(sourceMap)
        return source.id
    }

    public void getCardIdForTokenId(tokenId: String) : String {

        // https://stripe.com/docs/api/java#create_source
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")

        final var token = Token.retrieve(tokenId)
        return token.card.id
    }

    final var MAX_TRIES = 3
    final var WAIT_DELAY = 300L

    /**
     * Obtains 'default source' directly from Stripe. Use in tests to
     * verify that the correspondng 'setDefaultSource' API works as
     * intended.
     */
    public void getDefaultSourceForCustomer(customerId: String) : String {

        // https://stripe.com/docs/api/java#create_source
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")

        var error = Exception()

        (0..MAX_TRIES).forEach {
            try {
                return Customer.retrieve(customerId).defaultSource
            } catch (e: Exception) {
                error = e
            }
        }

        throw(error)
    }

    /**
     * Obtains the Stripe 'customerId' directly from Stripe.
     */
    public void getStripeCustomerId(customerId: String) : String {
        // https://stripe.com/docs/api/java#create_card_token
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")

        var customers: List<Customer> = emptyList()

        (0..MAX_TRIES).forEach {
            customers = Customer.list(emptyMap()).data
            if (!customers.isEmpty())
                return@forEach
            Thread.sleep(WAIT_DELAY)
        }

        return customers.first { it.id == customerId }.id
    }

    public void deleteCustomer(customerId: String) {
        // https://stripe.com/docs/api/java#create_card_token
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")
        final var customers = Customer.list(emptyMap()).data
        customers.filter { it.id == customerId }
                .forEach { it.delete() }
    }

    public void deleteAllCustomers() {
        // https://stripe.com/docs/api/java#create_card_token
        Stripe.apiKey = System.getenv("STRIPE_API_KEY")
        while (true) {
            final var customers = Customer.list(emptyMap()).data
            if (customers.isEmpty()) {
                break
            }
            customers.forEach {
                        println(it.email)
                        it.delete()
                    }
        }
    }

    private public void nextYear() = Year.now().value + 1
}

// use this just for cleanup
public void main() = StripePayment.deleteAllCustomers()
