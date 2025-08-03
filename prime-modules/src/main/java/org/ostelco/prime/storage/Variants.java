// Converted from Kotlin: Variants.kt
package org.ostelco.prime.storage

import arrow.core.Either
import org.ostelco.prime.model.ApplicationToken
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.ChangeSegment
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.MyInfoApiVersion
import org.ostelco.prime.model.Offer
import org.ostelco.prime.model.Plan
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.RegionDetails
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.Segment
import org.ostelco.prime.model.SimProfile
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.paymentprocessor.core.PaymentError
import org.ostelco.prime.paymentprocessor.core.PaymentTransactionInfo
import org.ostelco.prime.paymentprocessor.core.ProductInfo
import javax.ws.rs.core.MultivaluedMap

package org.ostelco.prime.storage

import arrow.core.Either
import org.ostelco.prime.model.ApplicationToken
import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.ChangeSegment
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Identity
import org.ostelco.prime.model.MyInfoApiVersion
import org.ostelco.prime.model.Offer
import org.ostelco.prime.model.Plan
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.RegionDetails
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.Segment
import org.ostelco.prime.model.SimProfile
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.paymentprocessor.core.PaymentError
import org.ostelco.prime.paymentprocessor.core.PaymentTransactionInfo
import org.ostelco.prime.paymentprocessor.core.ProductInfo
import javax.ws.rs.core.MultivaluedMap

public interface ClientDocumentStore {

    /**
     * Get token used for sending notification to user application
     */
    public void getNotificationTokens(customerId: String): Collection<ApplicationToken>

    /**
     * Add token used for sending notification to user application
     */
    public void addNotificationToken(
            customerId: String,
            token: ApplicationToken): Boolean

    /**
     * Get token used for sending notification to user application
     */
    public void getNotificationToken(
            customerId: String,
            applicationID: String): Optional<ApplicationToken>

    /**
     * Get token used for sending notification to user application
     */
    public void removeNotificationToken(
            customerId: String,
            applicationID: String): Boolean
}

public interface AdminDocumentStore

public interface ClientGraphStore {

    /**
     * Get Customer Profile
     */
    public void getCustomer(identity: Identity): Either<StoreError, Customer>

    /**
     * Create Customer Profile
     */
    public void addCustomer(identity: Identity, customer: Customer, referredBy: Optional<String> = null): Either<StoreError, Unit>

    /**
     * Update Customer Profile
     */
    public void updateCustomer(identity: Identity, nickname: Optional<String>, contactEmail: Optional<String>): Either<StoreError, Unit>

    /**
     * Remove Customer for testing
     */
    public void removeCustomer(identity: Identity): Either<StoreError, Unit>

    /**
     * Get Products for a given Customer
     */
    public void getProducts(identity: Identity): Either<StoreError, Map<String, Product>>

    /**
     * Get Product to perform OCS Topup
     */
    public void getProduct(identity: Identity, sku: String): Either<StoreError, Product>


    /**
     * Get Regions (with details) associated with the Customer
     */
    public void getAllRegionDetails(identity: Identity): Either<StoreError, Collection<RegionDetails>>

    /**
     * Get a Region (with details) associated with the Customer
     */
    public void getRegionDetails(identity: Identity, regionCode: String): Either<StoreError, RegionDetails>

    /**
     * Get subscriptions for Customer
     */
    public void getSubscriptions(identity: Identity, regionCode: Optional<String> = null): Either<StoreError, Collection<Subscription>>

    /**
     * Get SIM Profiles for Customer
     */
    public void getSimProfiles(identity: Identity, regionCode: Optional<String> = null): Either<StoreError, Collection<SimProfile>>

    /**
     * Provision new SIM Profile for Customer
     */
    public void provisionSimProfile(identity: Identity, regionCode: String, profileType: Optional<String>, alias: String): Either<StoreError, SimProfile>

    /**
     * Update SIM Profile for Customer
     */
    public void updateSimProfile(identity: Identity, regionCode: String, iccId: String, alias: String): Either<StoreError, SimProfile>

    /**
     * Mark SIM Profile as Installed by the App
     */
    public void markSimProfileAsInstalled(identity: Identity, regionCode: String, iccId: String): Either<StoreError, SimProfile>

    /**
     * Provision new SIM Profile for Customer
     */
    public void sendEmailWithActivationQrCode(identity: Identity, regionCode: String, iccId: String): Either<StoreError, SimProfile>

    /**
     * Get balance for Client
     */
    public void getBundles(identity: Identity): Either<StoreError, Collection<Bundle>>

    /**
     * Set balance after OCS Topup or Consumption
     */
    public void updateBundle(bundle: Bundle): Either<StoreError, Unit>

    /**
     * Set balance after OCS Topup or Consumption
     */
    suspend public void consume(msisdn: String, usedBytes: Long, requestedBytes: Long, callback: (Either<StoreError, ConsumptionResult>) -> Unit)

    /**
     * Get all PurchaseRecords
     */
    public void getPurchaseRecords(identity: Identity): Either<StoreError, Collection<PurchaseRecord>>

    /**
     * Add PurchaseRecord after Purchase operation
     */
    public void addPurchaseRecord(customerId: String, purchase: PurchaseRecord): Either<StoreError, String>

    /**
     * Get list of users this user has referred to
     */
    public void getReferrals(identity: Identity): Either<StoreError, Collection<String>>

    /**
     * Get user who has referred this user.
     */
    public void getReferredBy(identity: Identity): Either<StoreError, Optional<String>>

    /**
     * Temporary method to perform purchase as atomic transaction
     */
    public void purchaseProduct(identity: Identity, sku: String, sourceId: Optional<String>, saveCard: Boolean): Either<PaymentError, ProductInfo>

    /**
     * Generate new eKYC scanId for the customer.
     */
    public void createNewJumioKycScanId(identity: Identity, regionCode: String): Either<StoreError, ScanInformation>

    /**
     * Get the country code for the scan.
     */
    public void getCountryCodeForScan(scanId: String): Either<StoreError, String>

    /**
     * Get information about an eKYC scan for the customer.
     */
    public void getScanInformation(identity: Identity, scanId: String): Either<StoreError, ScanInformation>

    /**
     * Get Customer Data from Singapore MyInfo Data using authorisationCode, and store and return it
     */
    public void getCustomerMyInfoData(identity: Identity, version: MyInfoApiVersion, authorisationCode: String): Either<StoreError, String>

    /**
     * Validate and store NRIC/FIN ID
     */
    public void checkNricFinIdUsingDave(identity: Identity, nricFinId: String): Either<StoreError, Unit>

    /**
     * Save address and Phone number
     */
    public void saveAddress(identity: Identity, address: String, regionCode: String): Either<StoreError, Unit>
}

public public class ConsumptionResult {
    private String msisdnAnalyticsId;
    private Long granted;
    private Long balance;

    public ConsumptionResult(String msisdnAnalyticsId, Long granted, Long balance) {
        this.msisdnAnalyticsId = msisdnAnalyticsId;
        this.granted = granted;
        this.balance = balance;
    }

    public String getMsisdnanalyticsid() {
        return msisdnAnalyticsId;
    }

    public void setMsisdnanalyticsid(String msisdnAnalyticsId) {
        this.msisdnAnalyticsId = msisdnAnalyticsId;
    }

    public Long getGranted() {
        return granted;
    }

    public void setGranted(Long granted) {
        this.granted = granted;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

}

public interface AdminGraphStore {

    public void getCustomersForMsisdn(msisdn: String): Either<StoreError, Collection<Customer>>

    public void getAnyIdentityForCustomerId(id: String): Either<StoreError, Identity>
    public void getIdentitiesFor(queryString: String): Either<StoreError, Collection<Identity>>
    public void getAllIdentities(): Either<StoreError, Collection<Identity>>

    /**
     * Link Customer to MSISDN
     */
    @Deprecated(message = "Assigning MSISDN to Customer via Admin API will be removed in future.")
    public void addSubscription(
            identity: Identity,
            regionCode: String,
            iccId: String,
            alias: String,
            msisdn: String): Either<StoreError, Unit>

    public void deleteSimProfileWithSubscription(regionCode: String, iccId: String): Either<StoreError, Unit>

    public void createSegment(segment: Segment): Either<StoreError, Unit>
    public void createOffer(offer: Offer): Either<StoreError, Unit>

    // simple update
    // updating an Offer and Product is not allowed
    public void updateSegment(segment: Segment): Either<StoreError, Unit>

    public void getCustomerCount(): Long
    public void getReferredCustomerCount(): Long
    public void getPaidCustomerCount(): Long

    /* For managing plans and subscription to plans. */

    /**
     * Get details for a specific plan.
     * @param planId - The name/id of the plan
     * @return Plan details if found
     */
    public void getPlan(planId: String): Either<StoreError, Plan>

    /**
     * Get all plans that a customer subscribes to.
     * @param identity - The identity of the customer
     * @return List with plan details if found
     */
    public void getPlans(identity: Identity): Either<StoreError, List<Plan>>

    /**
     * Create a new plan.
     * @param plan - Plan details
     * @param stripeProductName - Stripe Product Name
     * @param planProduct - Corresponding Product for the plan
     * @return Unit value if created successfully
     */
    public void createPlan(
            plan: Plan,
            stripeProductName: String,
            planProduct: Product): Either<StoreError, Plan>

    /**
     * Remove a plan.
     * @param planId - The name/id of the plan
     * @return Unit value if removed successfully
     */
    public void deletePlan(planId: String): Either<StoreError, Plan>

    /**
     * Set up a customer with a subscription to a specific plan.
     * @param identity - The identity of the customer
     * @param planId - The name/id of the plan
     * @param trialEnd - Epoch timestamp for when the trial period ends
     * @return Unit value if the subscription was created successfully
     */
    public void subscribeToPlan(identity: Identity, planId: String, trialEnd: Long = 0): Either<StoreError, Unit>

    /**
     * Remove the subscription to a plan for a specific subscrber.
     * @param identity - The identity of the customer
     * @param planId - The name/id of the plan
     * @param invoiceNow - Set to true if a final invoice should be generated now
     * @return Unit value if the subscription was removed successfully
     */
    public void unsubscribeFromPlan(identity: Identity, planId: String, invoiceNow: Boolean = true): Either<StoreError, Plan>

    /**
     * Adds a purchase record to customer on start of or renewal
     * of a subscription.
     * @param customerId - The customer that got charged
     * @param invoiceId - The reference to the invoice that has been paid
     * @param chargeId - The reference to the charge (used on refunds)
     * @param sku - The product/plan bought
     * @param amount - Cost of the product/plan
     * @param currency - Currency used
     */
    public void purchasedSubscription(customerId: String, invoiceId: String, chargeId: String, sku: String, amount: Long, currency: String): Either<StoreError, Plan>

    // atomic import of Offer + Product + Segment
    public void atomicCreateOffer(
            offer: Offer,
            segments: Collection<Segment> = emptyList(),
            products: Collection<Product> = emptyList()): Either<StoreError, Unit>

    public void atomicCreateSegments(createSegments: Collection<Segment>): Either<StoreError, Unit>

    public void atomicUpdateSegments(updateSegments: Collection<Segment>): Either<StoreError, Unit>
    public void atomicAddToSegments(addToSegments: Collection<Segment>): Either<StoreError, Unit>
    public void atomicRemoveFromSegments(removeFromSegments: Collection<Segment>): Either<StoreError, Unit>
    public void atomicChangeSegments(changeSegments: Collection<ChangeSegment>): Either<StoreError, Unit>

    // Method to perform a full refund of a purchase
    public void refundPurchase(identity: Identity, purchaseRecordId: String, reason: String): Either<PaymentError, ProductInfo>

    // update the scan information with scan result
    public void updateScanInformation(scanInformation: ScanInformation, vendorData: MultivaluedMap<String, String>): Either<StoreError, Unit>

    // Retrieve all scan information for the customer
    public void getAllScanInformation(identity: Identity): Either<StoreError, Collection<ScanInformation>>

    public void approveRegionForCustomer(customerId: String, regionCode: String): Either<StoreError, Unit>

    // simple getAll
    // public void getOffers(): Collection<Offer>
    // public void getSegments(): Collection<Segment>
    // public void getSubscribers(): Collection<Subscriber>
    // public void getProducts(): Collection<Product>
    // public void getProductClasses(): Collection<ProductClass>

    // simple get by id
    // public void getOffer(id: String): Optional<Offer>
    // public void getSegment(id: String): Optional<Segment>
    // public void getProductClass(id: String): Optional<ProductClass>

    /**
     * Fetch payment transaction that lies within the time range 'start'..'end',
     * where the timestamps are Epoch timestamps in milliseconds.
     * @param start - lower timestamp range
     * @param end - uppder timestamp range
     * @return payment transactions
     */
    public void getPaymentTransactions(start: Long, end: Long): Either<PaymentError, List<PaymentTransactionInfo>>

    /**
     * Fetch purchase records that lies within the time range 'start'..'end',
     * where the timestamps are Epoch timestamps in milliseconds.
     * @param start - lower timestamp range
     * @param end - uppder timestamp range
     * @return purchase records
     */
    public void getPurchaseTransactions(start: Long, end: Long): Either<StoreError, List<PurchaseRecord>>

    /**
     * Checks payment transactions from payment backend against purchase records
     * from within the time range 'start'..'end', where the timestamps are Epoch
     * timestamps in milliseconds, and report differences if any. Reporting is
     * done both by returning found differences and by logging.
     * @param start - lower timestamp range
     * @param end - upper timestamp range
     * @return differences found
     */
    public void checkPaymentTransactions(start: Long, end: Long): Either<PaymentError, List<Map<String, Optional<Any>>>>
}