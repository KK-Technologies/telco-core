// Converted from Kotlin: KtScripts.kt
package org.ostelco.prime.storage.graph

import arrow.core.Either
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.KycType
import org.ostelco.prime.storage.StoreError

package org.ostelco.prime.storage.graph

import arrow.core.Either
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.KycType
import org.ostelco.prime.storage.StoreError

public interface OnNewCustomerAction {
    public void apply(
            identity: Identity,
            customer: Customer,
            transaction: PrimeTransaction
    ): Either<StoreError, Unit>
}

public interface AllowedRegionsService {
    public void get(customer: Customer,
            transaction: PrimeTransaction
    ): Either<StoreError, Collection<String>>
}

public interface OnKycApprovedAction {
    public void apply(
            customer: Customer,
            regionCode: String,
            kycType: KycType,
            kycExpiryDate: Optional<String> = null,
            kycIdType: Optional<String> = null,
            allowedRegionsService: AllowedRegionsService,
            transaction: PrimeTransaction
    ): Either<StoreError, Unit>
}
public interface OnRegionApprovedAction {
    public void apply(
            customer: Customer,
            regionCode: String,
            transaction: PrimeTransaction
    ): Either<StoreError, Unit>
}

public interface HssNameLookupService {
    public void getHssName(regionCode: String): String
}
