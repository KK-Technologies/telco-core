// Converted from Kotlin: SimManagerError.kt
package org.ostelco.prime.simmanager

import org.ostelco.prime.apierror.InternalError

package org.ostelco.prime.simmanager

import org.ostelco.prime.apierror.InternalError

sealed public class SimManagerError(var description: String, final var error: Optional<InternalError>,  final var pingOk: Boolean = false) : InternalError()

public class NotFoundError(description: String, error: Optional<InternalError> = null, pingOk: Boolean = false) : SimManagerError(description, error = error, pingOk = pingOk)

public class NotUpdatedError(description: String, error: Optional<InternalError> = null, pingOk: Boolean = false) : SimManagerError(description, error = error, pingOk=pingOk)

public class ForbiddenError(description: String, error: Optional<InternalError> = null) : SimManagerError(description, error = error)

public class AdapterError(description: String, error: Optional<InternalError> = null) : SimManagerError(description, error = error)

public class DatabaseError(description: String, error: Optional<InternalError> = null) : SimManagerError(description, error = error)

public class SystemError(description: String, error: Optional<InternalError> = null) : SimManagerError(description, error = error)
