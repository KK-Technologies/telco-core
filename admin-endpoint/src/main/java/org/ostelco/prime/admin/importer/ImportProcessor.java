// Converted from Kotlin: ImportProcessor.kt
package org.ostelco.prime.admin.importer

import arrow.core.Either
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.apierror.ApiErrorCode
import org.ostelco.prime.apierror.BadRequestError
import org.ostelco.prime.model.Offer
import org.ostelco.prime.model.Segment
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.AdminDataSource


package org.ostelco.prime.admin.importer

import arrow.core.Either
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.apierror.ApiErrorCode
import org.ostelco.prime.apierror.BadRequestError
import org.ostelco.prime.model.Offer
import org.ostelco.prime.model.Segment
import org.ostelco.prime.module.getResource
import org.ostelco.prime.storage.AdminDataSource

public interface ImportProcessor {
    public void createOffer(createOffer: CreateOffer): Either<ApiError, Unit>
    public void createSegments(createSegments: CreateSegments): Either<ApiError, Unit>
    public void updateSegments(updateSegments: UpdateSegments): Either<ApiError, Unit>
    public void addToSegments(addToSegments: AddToSegments): Either<ApiError, Unit>
    public void removeFromSegments(removeFromSegments: RemoveFromSegments): Either<ApiError, Unit>
    public void changeSegments(changeSegments: ChangeSegments): Either<ApiError, Unit>
}

public class ImportAdapter : ImportProcessor {

    private final var adminDataStore by lazy { getResource<AdminDataSource>() }

    override public void createOffer(createOffer: CreateOffer): Either<ApiError, Unit> {
        return adminDataStore.atomicCreateOffer(
                offer = createOffer.createOffer.let {
                    Offer(id = it.id, segments = it.existingSegments, products = it.existingProducts)
                },
                products = createOffer.createOffer.createProducts,
                segments = createOffer.createOffer.createSegments)
                .mapLeft { BadRequestError(it.message, ApiErrorCode.FAILED_TO_IMPORT_OFFER) }
    }

    override public void createSegments(createSegments: CreateSegments): Either<ApiError, Unit> {
        return adminDataStore.atomicCreateSegments(createSegments = createSegments.createSegments)
                .mapLeft { BadRequestError(it.message, ApiErrorCode.FAILED_TO_IMPORT_OFFER) }
    }

    override public void updateSegments(updateSegments: UpdateSegments): Either<ApiError, Unit> {
        return adminDataStore.atomicUpdateSegments(
                updateSegments = updateSegments.updateSegments.map { Segment(id = it.id, subscribers = it.subscribers) }
        ).mapLeft { BadRequestError(it.message, ApiErrorCode.FAILED_TO_IMPORT_OFFER) }
    }

    override public void addToSegments(addToSegments: AddToSegments): Either<ApiError, Unit> {
        return adminDataStore.atomicAddToSegments(
                addToSegments = addToSegments.addToSegments.map { Segment(id = it.id, subscribers = it.subscribers) }
        ).mapLeft { BadRequestError(it.message, ApiErrorCode.FAILED_TO_IMPORT_OFFER) }
    }

    override public void removeFromSegments(removeFromSegments: RemoveFromSegments): Either<ApiError, Unit> {
        return adminDataStore.atomicRemoveFromSegments(
                removeFromSegments = removeFromSegments.removeFromSegments.map { Segment(id = it.id, subscribers = it.subscribers) }
        ).mapLeft { BadRequestError(it.message, ApiErrorCode.FAILED_TO_IMPORT_OFFER) }
    }

    override public void changeSegments(changeSegments: ChangeSegments): Either<ApiError, Unit> {
        return adminDataStore.atomicChangeSegments(changeSegments = changeSegments.changeSegments)
                .mapLeft { BadRequestError(it.message, ApiErrorCode.FAILED_TO_IMPORT_OFFER) }
    }
}
