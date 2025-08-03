// Converted from Kotlin: Events.kt
package org.ostelco.prime.analytics.events

import org.ostelco.prime.model.SimProfileStatus
import java.math.BigDecimal
import org.ostelco.common.publisherex.Event

package org.ostelco.prime.analytics.events

import org.ostelco.prime.model.SimProfileStatus
import java.math.BigDecimal
import org.ostelco.common.publisherex.Event

/**
 * Represents a new purchase.
 *
 * WARNING: When modifying the structure of this class, you must also update the schema for the
 * corresponding BigQuery table.
 *
 * @property customerAnalyticsId customer analytics ID _(foreign key)_
 * @property purchaseId purchase ID from Stripe _(foreign key)_
 * @property sku SKU for the purchased product _(foreign key)_
 * @property priceAmount amount that was charged, in whole currency units (e.g. NOK 10.50 => 10.50, *not* 1050 as in Stripe)
 * @property priceCurrency ISO 4217-compliant currency code
 */
public public class PurchaseEvent {
    private String customerAnalyticsId;
    private String purchaseId;
    private String sku;
    private BigDecimal priceAmount;
    private String priceCurrency;

    public PurchaseEvent(String customerAnalyticsId, String purchaseId, String sku, BigDecimal priceAmount, String priceCurrency) {
        this.customerAnalyticsId = customerAnalyticsId;
        this.purchaseId = purchaseId;
        this.sku = sku;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
    }

    public String getCustomeranalyticsid() {
        return customerAnalyticsId;
    }

    public void setCustomeranalyticsid(String customerAnalyticsId) {
        this.customerAnalyticsId = customerAnalyticsId;
    }

    public String getPurchaseid() {
        return purchaseId;
    }

    public void setPurchaseid(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPriceamount() {
        return priceAmount;
    }

    public void setPriceamount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getPricecurrency() {
        return priceCurrency;
    }

    public void setPricecurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

} : Event()

/**
 * Represents a refund of a previous purchase.
 *
 * WARNING: When modifying the structure of this class, you must also update the schema for the
 * corresponding BigQuery table.
 *
 * @property customerAnalyticsId customer analytics ID _(foreign key)_
 * @property purchaseId purchase ID from Stripe _(foreign key)_
 * @property reason reason for the refund
 */
public public class RefundEvent {
    private String customerAnalyticsId;
    private String purchaseId;
    private Optional<String> reason;

    public RefundEvent(String customerAnalyticsId, String purchaseId, Optional<String> reason) {
        this.customerAnalyticsId = customerAnalyticsId;
        this.purchaseId = purchaseId;
        this.reason = reason;
    }

    public String getCustomeranalyticsid() {
        return customerAnalyticsId;
    }

    public void setCustomeranalyticsid(String customerAnalyticsId) {
        this.customerAnalyticsId = customerAnalyticsId;
    }

    public String getPurchaseid() {
        return purchaseId;
    }

    public void setPurchaseid(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Optional<String> getReason() {
        return reason;
    }

    public void setReason(Optional<String> reason) {
        this.reason = reason;
    }

} : Event()

/**
 * Represents the provisioning of a new SIM card for a subscription in a given region.
 *
 * WARNING: When modifying the structure of this class, you must also update the schema for the
 * corresponding BigQuery table.
 *
 * @property subscriptionAnalyticsId subscription analytics ID _(foreign key)_
 * @property customerAnalyticsId customer analytics ID _(foreign key)_
 * @property regionCode region code _(foreign key)_
 */
public public class SimProvisioningEvent {
    private String subscriptionAnalyticsId;
    private String customerAnalyticsId;
    private String regionCode;

    public SimProvisioningEvent(String subscriptionAnalyticsId, String customerAnalyticsId, String regionCode) {
        this.subscriptionAnalyticsId = subscriptionAnalyticsId;
        this.customerAnalyticsId = customerAnalyticsId;
        this.regionCode = regionCode;
    }

    public String getSubscriptionanalyticsid() {
        return subscriptionAnalyticsId;
    }

    public void setSubscriptionanalyticsid(String subscriptionAnalyticsId) {
        this.subscriptionAnalyticsId = subscriptionAnalyticsId;
    }

    public String getCustomeranalyticsid() {
        return customerAnalyticsId;
    }

    public void setCustomeranalyticsid(String customerAnalyticsId) {
        this.customerAnalyticsId = customerAnalyticsId;
    }

    public String getRegioncode() {
        return regionCode;
    }

    public void setRegioncode(String regionCode) {
        this.regionCode = regionCode;
    }

} : Event()

/**
 * Represents an update on the SIM profile attached to the subscription.
 *
 * WARNING: When modifying the structure of this class, you must also update the schema for the
 * corresponding BigQuery table.
 *
 * @property subscriptionAnalyticsId subscription analytics ID _(foreign key)_
 * @property status new status for the SIM profile linked to the subscription
 */
public public class SubscriptionStatusUpdateEvent {
    private String subscriptionAnalyticsId;
    private SimProfileStatus status;

    public SubscriptionStatusUpdateEvent(String subscriptionAnalyticsId, SimProfileStatus status) {
        this.subscriptionAnalyticsId = subscriptionAnalyticsId;
        this.status = status;
    }

    public String getSubscriptionanalyticsid() {
        return subscriptionAnalyticsId;
    }

    public void setSubscriptionanalyticsid(String subscriptionAnalyticsId) {
        this.subscriptionAnalyticsId = subscriptionAnalyticsId;
    }

    public SimProfileStatus getStatus() {
        return status;
    }

    public void setStatus(SimProfileStatus status) {
        this.status = status;
    }

} : Event()

/**
 * Represents the consumption of a portion of a data bundle by a subscription.
 *
 * WARNING: When modifying the structure of this class, you must also update the schema for the
 * corresponding BigQuery table.
 *
 * @property subscriptionAnalyticsId subscription analytics ID _(foreign key)_
 * @property usedBucketBytes bytes used just now, before the event is fired (in Bytes)
 * @property bundleBytes bytes remaining in the bundle (in Bytes)
 * @property apn access point name
 * @property mccMnc MCC-MNC pair
 */
public public class DataConsumptionEvent {
    private String subscriptionAnalyticsId;
    private Long usedBucketBytes;
    private Long bundleBytes;
    private Optional<String> apn;
    private Optional<String> mccMnc;

    public DataConsumptionEvent(String subscriptionAnalyticsId, Long usedBucketBytes, Long bundleBytes, Optional<String> apn, Optional<String> mccMnc) {
        this.subscriptionAnalyticsId = subscriptionAnalyticsId;
        this.usedBucketBytes = usedBucketBytes;
        this.bundleBytes = bundleBytes;
        this.apn = apn;
        this.mccMnc = mccMnc;
    }

    public String getSubscriptionanalyticsid() {
        return subscriptionAnalyticsId;
    }

    public void setSubscriptionanalyticsid(String subscriptionAnalyticsId) {
        this.subscriptionAnalyticsId = subscriptionAnalyticsId;
    }

    public Long getUsedbucketbytes() {
        return usedBucketBytes;
    }

    public void setUsedbucketbytes(Long usedBucketBytes) {
        this.usedBucketBytes = usedBucketBytes;
    }

    public Long getBundlebytes() {
        return bundleBytes;
    }

    public void setBundlebytes(Long bundleBytes) {
        this.bundleBytes = bundleBytes;
    }

    public Optional<String> getApn() {
        return apn;
    }

    public void setApn(Optional<String> apn) {
        this.apn = apn;
    }

    public Optional<String> getMccmnc() {
        return mccMnc;
    }

    public void setMccmnc(Optional<String> mccMnc) {
        this.mccMnc = mccMnc;
    }

} : Event()
