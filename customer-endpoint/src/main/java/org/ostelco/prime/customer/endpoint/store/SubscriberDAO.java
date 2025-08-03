// Converted from Kotlin: SubscriberDAO.kt
package org.ostelco.prime.customer.endpoint.store

import arrow.core.Either
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.customer.endpoint.model.Person
import org.ostelco.prime.model.ApplicationToken
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Context
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.MyInfoApiVersion
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.RegionDetails
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.SimProfile
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.paymentprocessor.core.ProductInfo
import org.ostelco.prime.paymentprocessor.core.SourceDetailsInfo
import org.ostelco.prime.paymentprocessor.core.SourceInfo

package org.ostelco.prime.customer.endpoint.store

import arrow.core.Either
import org.ostelco.prime.apierror.ApiError
import org.ostelco.prime.customer.endpoint.model.Person
import org.ostelco.prime.model.ApplicationToken
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Context
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.MyInfoApiVersion
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.RegionDetails
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.SimProfile
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.paymentprocessor.core.ProductInfo
import org.ostelco.prime.paymentprocessor.core.SourceDetailsInfo
import org.ostelco.prime.paymentprocessor.core.SourceInfo


/**
 *
 */
public interface SubscriberDAO {

    //
    // Customer
    //

    public void getCustomer(identity: Identity): Either<ApiError, Customer>

    public void createCustomer(identity: Identity, customer: Customer, referredBy: Optional<String>): Either<ApiError, Customer>

    public void updateCustomer(identity: Identity, nickname: Optional<String>, contactEmail: Optional<String>): Either<ApiError, Customer>

    public void removeCustomer(identity: Identity): Either<ApiError, Unit>

    //
    // Context
    //
    public void getContext(identity: Identity): Either<ApiError, Context>

    //
    // Regions
    //
    public void getRegions(identity: Identity): Either<ApiError, Collection<RegionDetails>>

    public void getRegion(identity: Identity, regionCode: String): Either<ApiError, RegionDetails>

    //
    // Subscriptions
    //

    public void getSubscriptions(identity: Identity, regionCode: Optional<String>): Either<ApiError, Collection<Subscription>>

    //
    // SIM Profile
    //

    public void getSimProfiles(identity: Identity, regionCode: String): Either<ApiError, Collection<SimProfile>>

    public void provisionSimProfile(identity: Identity, regionCode: String, profileType: Optional<String>, alias: String): Either<ApiError, SimProfile>

    public void updateSimProfile(identity: Identity, regionCode: String, iccId: String, alias: String): Either<ApiError, SimProfile>

    public void markSimProfileAsInstalled(identity: Identity, regionCode: String, iccId: String): Either<ApiError, SimProfile>

    public void sendEmailWithEsimActivationQrCode(identity: Identity, regionCode: String, iccId: String): Either<ApiError, SimProfile>

    //
    // Bundle
    //
    public void getBundles(identity: Identity): Either<ApiError, Collection<Bundle>>

    //
    // Products
    //

    public void getPurchaseHistory(identity: Identity): Either<ApiError, Collection<PurchaseRecord>>

    public void getProduct(identity: Identity, sku: String): Either<ApiError, Product>

    public void getProducts(identity: Identity): Either<ApiError, Collection<Product>>

    public void purchaseProduct(identity: Identity, sku: String, sourceId: Optional<String>, saveCard: Boolean): Either<ApiError, ProductInfo>

    //
    // Payment
    //

    public void createSource(identity: Identity, sourceId: String): Either<ApiError, SourceInfo>

    public void setDefaultSource(identity: Identity, sourceId: String): Either<ApiError, SourceInfo>

    public void listSources(identity: Identity): Either<ApiError, List<SourceDetailsInfo>>

    public void removeSource(identity: Identity, sourceId: String): Either<ApiError, SourceInfo>

    public void getStripeEphemeralKey(identity: Identity, apiVersion: String): Either<ApiError, String>

    //
    // Referrals
    //

    public void getReferrals(identity: Identity): Either<ApiError, Collection<Person>>

    public void getReferredBy(identity: Identity): Either<ApiError, Person>

    //
    // eKYC
    //

    public void createNewJumioKycScanId(identity: Identity, regionCode: String): Either<ApiError, ScanInformation>

    public void getCountryCodeForScan(scanId: String): Either<ApiError, String>

    public void getScanInformation(identity: Identity, scanId: String): Either<ApiError, ScanInformation>

    public void getCustomerMyInfoData(identity: Identity, version: MyInfoApiVersion, authorisationCode: String): Either<ApiError, String>

    public void checkNricFinIdUsingDave(identity: Identity, nricFinId: String): Either<ApiError, Unit>

    public void saveAddress(identity: Identity, address: String, regionCode: String): Either<ApiError, Unit>

    //
    // Token
    //

    public void storeApplicationToken(customerId: String, applicationToken: ApplicationToken): Either<ApiError, ApplicationToken>

    companion object {

        /**
         * The application token is only valid if token,
         * applicationID and token type is set.
         */
        public void isValidApplicationToken(appToken: Optional<ApplicationToken>): Boolean {
            return (appToken != null
                    && !appToken.token.isEmpty()
                    && !appToken.applicationID.isEmpty()
                    && !appToken.tokenType.isEmpty())
        }
    }
}
