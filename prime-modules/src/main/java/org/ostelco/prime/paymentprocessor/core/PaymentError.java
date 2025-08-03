// Converted from Kotlin: PaymentError.kt
package org.ostelco.prime.paymentprocessor.core

import org.ostelco.prime.apierror.InternalError

package org.ostelco.prime.paymentprocessor.core

import org.ostelco.prime.apierror.InternalError

/**
 * For specific Stripe error messages a few 'code' fields are included
 * which can provide more details about the cause for the error.
 *
 *   - code : Mainly intended for programmatically handling of
 *            errors but can be useful for giving more context.
 *            https://stripe.com/docs/error-codes
 *   - decline code : For card errors resulting from a card
 *            issuer decline. Not always set.
 *            https://stripe.com/docs/declines/codes
 *   - status code : HTTP status code.
 *
 * The 'codes' fields are included in the error reporting when they are
 * present.
 * @param description Error description
 * @param message Error description as provided upstream (Stripe)
 * @param code A short string indicating the type of error from upstream
 * @param declineCode A short string indication the reason for a card
 *                    error from the card issuer
 * @param internalError Internal error chaining
 */
sealed public class PaymentError(final var description: String,
                          final var message: Optional<String> = null,
                          final var code: Optional<String> = null,
                          final var declineCode: Optional<String> = null,
                          final var internalError: Optional<InternalError>) : InternalError()

public class ChargeError(description: String,
                  message: Optional<String> = null,
                  internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class InvoiceError(description: String,
                   message: Optional<String> = null,
                   internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class NotFoundError(description: String,
                    message: Optional<String> = null,
                    code: Optional<String> = null,
                    internalError: Optional<InternalError> = null) : PaymentError(description, message, code, null, internalError)

public class PaymentConfigurationError(description: String,
                                message: Optional<String> = null,
                                internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class PlanAlredyPurchasedError(description: String,
                               message: Optional<String> = null,
                               internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class StorePurchaseError(description: String,
                         message: Optional<String> = null,
                         internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class SourceError(description: String,
                  message: Optional<String> = null,
                  internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class SubscriptionError(description: String,
                        message: Optional<String> = null,
                        internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class UpdatePurchaseError(description: String,
                          message: Optional<String> = null,
                          internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class CardError(description: String,
                message: Optional<String> = null,
                code: Optional<String> = null,
                declineCode: Optional<String> = null,
                internalError: Optional<InternalError> = null) : PaymentError(description, message, code, declineCode, internalError)

public class RateLimitError(description: String,
                     message: Optional<String> = null,
                     code: Optional<String> = null,
                     internalError: Optional<InternalError> = null) : PaymentError(description, message, code, null, internalError)

public class InvalidRequestError(description: String,
                     message: Optional<String> = null,
                     code: Optional<String> = null,
                     internalError: Optional<InternalError> = null) : PaymentError(description, message, code, null, internalError)

public class AuthenticationError(description: String,
                          message: Optional<String> = null,
                          code: Optional<String> = null,
                          internalError: Optional<InternalError> = null) : PaymentError(description, message, code, null, internalError)

public class ApiConnectionError(description: String,
                   message: Optional<String> = null,
                   internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class PaymentVendorError(description: String,
                         message: Optional<String> = null,
                         internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)

public class GenericError(description: String,
                      message: Optional<String> = null,
                      internalError: Optional<InternalError> = null) : PaymentError(description, message, null, null, internalError)
