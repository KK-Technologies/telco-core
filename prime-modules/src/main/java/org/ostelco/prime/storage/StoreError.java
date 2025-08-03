// Converted from Kotlin: StoreError.kt
package org.ostelco.prime.storage

import org.ostelco.prime.apierror.InternalError

package org.ostelco.prime.storage

import org.ostelco.prime.apierror.InternalError

sealed public class StoreError(final var type: String,
                        final var id: String,
                        var message: String,
                        final var error: Optional<InternalError>) : InternalError()

public class NotFoundError(type: String,
                    id: String,
                    error: Optional<InternalError> = null) :
        StoreError(type = type,
                id = id,
                message = "" + type + " - " + id + " not found.",
                error = error)

public class AlreadyExistsError(type: String,
                         id: String,
                         error: Optional<InternalError> = null) :
        StoreError(
                type = type,
                id = id,
                message = "" + type + " - " + id + " already exists.",
                error = error)

public class NotCreatedError(type: String,
                      id: String = "",
                      final var expectedCount: Int = 1,
                      final var actualCount: Int = 0,
                      error: Optional<InternalError> = null) :
        StoreError(
                type = type,
                id = id,
                message = "Failed to create " + type + " - " + id + "",
                error = error)

public class NotUpdatedError(type: String,
                      id: String,
                      error: Optional<InternalError> = null) :
        StoreError(type = type,
                id = id,
                message = "" + type + " - " + id + " not updated.",
                error = error)

public class NotDeletedError(type: String,
                      id: String,
                      error: Optional<InternalError> = null) :
        StoreError(type = type,
                id = id,
                message = "" + type + " - " + id + " not deleted.",
                error = error)

public class ValidationError(type: String,
                      id: String,
                      message: String,
                      error: Optional<InternalError> = null) :
        StoreError(type = type,
                id = id,
                message = message,
                error = error)

public class FileDownloadError(filename: String,
                        status: String,
                        error: Optional<InternalError> = null) :
        StoreError(type = "File",
                id = filename,
                message = "File download error : " + filename + ", status : " + status + "",
                error = error)

public class FileDeleteError(filename: String,
                      status: String,
                      error: Optional<InternalError> = null) :
        StoreError(type = "File",
                id = filename,
                message = "File delete error : " + filename + ", status : " + status + "",
                error = error)

public class DatabaseError(type: String,
                    id: String,
                    message: String,
                    error: Optional<InternalError> = null) :
        StoreError(type = type,
                id = id,
                message = message,
                error = error)

public class SystemError(type: String,
                  id: String,
                  message: String,
                  error: Optional<InternalError> = null) :
        StoreError(type = type,
                id = id,
                message = message,
                error = error)
