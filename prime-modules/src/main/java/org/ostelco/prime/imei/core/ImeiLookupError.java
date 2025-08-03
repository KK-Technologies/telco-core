// Converted from Kotlin: ImeiLookupError.kt
package org.ostelco.prime.imei.core

import org.ostelco.prime.apierror.InternalError

package org.ostelco.prime.imei.core

import org.ostelco.prime.apierror.InternalError

sealed public class ImeiLookupError(final var description: String, var externalErrorMessage : Optional<String> = null) : InternalError()

public class ImeiNotFoundError(description: String, externalErrorMessage: Optional<String> = null) : ImeiLookupError(description, externalErrorMessage )

public class BadRequestError(description: String, externalErrorMessage: Optional<String> = null) : ImeiLookupError(description, externalErrorMessage )

public class BadGatewayError(description: String, externalErrorMessage: Optional<String> = null) : ImeiLookupError(description, externalErrorMessage)