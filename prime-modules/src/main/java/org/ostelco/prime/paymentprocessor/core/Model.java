// Converted from Kotlin: Model.kt
package org.ostelco.prime.paymentprocessor.core

import java.math.BigDecimal

package org.ostelco.prime.paymentprocessor.core

import java.math.BigDecimal

/* Intended for reporting status for payment operations. */
enum public class PaymentStatus {
    PAYMENT_SUCCEEDED,
    TRIAL_START,
    REQUIRES_PAYMENT_METHOD,
    REQUIRES_ACTION,
}

public public class PlanInfo {
    private String id;

    public PlanInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

public public class ProductInfo {
    private String id;

    public ProductInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

public public class ProfileInfo {
    private String id;

    public ProfileInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

public public class SourceInfo {
    private String id;

    public SourceInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

public public class SourceDetailsInfo {
    private String id;
    private String type;
    private Map<String details;

    public SourceDetailsInfo(String id, String type, Map<String details) {
        this.id = id;
        this.type = type;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String getDetails() {
        return details;
    }

    public void setDetails(Map<String details) {
        this.details = details;
    }

}

public public class SubscriptionInfo {
    private String id;

    public SubscriptionInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

public public class SubscriptionDetailsInfo {
    private String id;
    private PaymentStatus status;
    private String invoiceId;
    private String chargeId;
    private Long created;
    private Long = 0L trialEnd;

    public SubscriptionDetailsInfo(String id, PaymentStatus status, String invoiceId, String chargeId, Long created, Long = 0L trialEnd) {
        this.id = id;
        this.status = status;
        this.invoiceId = invoiceId;
        this.chargeId = chargeId;
        this.created = created;
        this.trialEnd = trialEnd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getInvoiceid() {
        return invoiceId;
    }

    public void setInvoiceid(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getChargeid() {
        return chargeId;
    }

    public void setChargeid(String chargeId) {
        this.chargeId = chargeId;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long = 0L getTrialend() {
        return trialEnd;
    }

    public void setTrialend(Long = 0L trialEnd) {
        this.trialEnd = trialEnd;
    }

}

public public class TaxRateInfo {
    private String id;
    private BigDecimal percentage;
    private String displayName;
    private Boolean inclusive;

    public TaxRateInfo(String id, BigDecimal percentage, String displayName, Boolean inclusive) {
        this.id = id;
        this.percentage = percentage;
        this.displayName = displayName;
        this.inclusive = inclusive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public String getDisplayname() {
        return displayName;
    }

    public void setDisplayname(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getInclusive() {
        return inclusive;
    }

    public void setInclusive(Boolean inclusive) {
        this.inclusive = inclusive;
    }

}

public public class InvoiceItemInfo {
    private String id;

    public InvoiceItemInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

public public class InvoiceInfo {
    private String id;

    public InvoiceInfo(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

public public class InvoicePaymentInfo {
    private String id;
    private String chargeId;

    public InvoicePaymentInfo(String id, String chargeId) {
        this.id = id;
        this.chargeId = chargeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChargeid() {
        return chargeId;
    }

    public void setChargeid(String chargeId) {
        this.chargeId = chargeId;
    }

}

public public class PaymentTransactionInfo {
    private String id;
    private Int amount;
    private String currency;
    private Long created;
    private Boolean refunded;

    public PaymentTransactionInfo(String id, Int amount, String currency, Long created, Boolean refunded) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.created = created;
        this.refunded = refunded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Int getAmount() {
        return amount;
    }

    public void setAmount(Int amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Boolean getRefunded() {
        return refunded;
    }

    public void setRefunded(Boolean refunded) {
        this.refunded = refunded;
    }

}
