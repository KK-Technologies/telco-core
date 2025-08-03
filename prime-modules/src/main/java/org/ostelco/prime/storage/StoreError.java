package org.ostelco.prime.storage;

import org.ostelco.prime.apierror.InternalError;

/**
 * Base class for store errors
 */
public abstract class StoreError extends InternalError {
    private final String type;
    private final String id;
    private String message;
    private final InternalError error;

    protected StoreError(String type, String id, String message, InternalError error) {
        this.type = type;
        this.id = id;
        this.message = message;
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InternalError getError() {
        return error;
    }

    @Override
    public String toString() {
        return "StoreError{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

/**
 * Error when a resource is not found
 */
class NotFoundError extends StoreError {
    public NotFoundError(String type, String id) {
        this(type, id, null);
    }

    public NotFoundError(String type, String id, InternalError error) {
        super(type, id, type + " - " + id + " not found.", error);
    }
}

/**
 * Error when a resource already exists
 */
class AlreadyExistsError extends StoreError {
    public AlreadyExistsError(String type, String id) {
        this(type, id, null);
    }

    public AlreadyExistsError(String type, String id, InternalError error) {
        super(type, id, type + " - " + id + " already exists.", error);
    }
}

/**
 * Error when a resource is not created
 */
class NotCreatedError extends StoreError {
    private final int expectedCount;
    private final int actualCount;

    public NotCreatedError(String type) {
        this(type, "", 1, 0, null);
    }

    public NotCreatedError(String type, String id) {
        this(type, id, 1, 0, null);
    }

    public NotCreatedError(String type, String id, int expectedCount, int actualCount, InternalError error) {
        super(type, id, "Failed to create " + type + " - " + id, error);
        this.expectedCount = expectedCount;
        this.actualCount = actualCount;
    }

    public int getExpectedCount() {
        return expectedCount;
    }

    public int getActualCount() {
        return actualCount;
    }
}

/**
 * Error when a resource is not updated
 */
class NotUpdatedError extends StoreError {
    public NotUpdatedError(String type, String id) {
        this(type, id, null);
    }

    public NotUpdatedError(String type, String id, InternalError error) {
        super(type, id, type + " - " + id + " not updated.", error);
    }
}

/**
 * Error when a resource is not deleted
 */
class NotDeletedError extends StoreError {
    public NotDeletedError(String type, String id) {
        this(type, id, null);
    }

    public NotDeletedError(String type, String id, InternalError error) {
        super(type, id, type + " - " + id + " not deleted.", error);
    }
}

/**
 * Validation error
 */
class ValidationError extends StoreError {
    public ValidationError(String type, String id, String message) {
        this(type, id, message, null);
    }

    public ValidationError(String type, String id, String message, InternalError error) {
        super(type, id, message, error);
    }
}

/**
 * Database error
 */
class DatabaseError extends StoreError {
    public DatabaseError(String type, String id, String message) {
        this(type, id, message, null);
    }

    public DatabaseError(String type, String id, String message, InternalError error) {
        super(type, id, message, error);
    }
}

/**
 * System error
 */
class SystemError extends StoreError {
    public SystemError(String type, String id, String message) {
        this(type, id, message, null);
    }

    public SystemError(String type, String id, String message, InternalError error) {
        super(type, id, message, error);
    }
}