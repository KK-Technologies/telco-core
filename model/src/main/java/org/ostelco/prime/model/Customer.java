package org.ostelco.prime.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a customer in the system
 */
public class Customer implements HasId {
    private final String id;
    private final String nickname;
    private final String contactEmail;
    private final String createdOn;
    private final String analyticsId;
    private final String referralId;

    @JsonCreator
    public Customer(@JsonProperty("id") String id,
                    @JsonProperty("nickname") String nickname,
                    @JsonProperty("contactEmail") String contactEmail,
                    @JsonProperty("createdOn") String createdOn,
                    @JsonProperty("analyticsId") String analyticsId,
                    @JsonProperty("referralId") String referralId) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.nickname = nickname;
        this.contactEmail = contactEmail;
        this.createdOn = createdOn;
        this.analyticsId = analyticsId != null ? analyticsId : UUID.randomUUID().toString();
        this.referralId = referralId != null ? referralId : UUID.randomUUID().toString();
    }

    // Convenience constructor
    public Customer(String nickname, String contactEmail) {
        this(null, nickname, contactEmail, null, null, null);
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getAnalyticsId() {
        return analyticsId;
    }

    public String getReferralId() {
        return referralId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) &&
                Objects.equals(nickname, customer.nickname) &&
                Objects.equals(contactEmail, customer.contactEmail) &&
                Objects.equals(createdOn, customer.createdOn) &&
                Objects.equals(analyticsId, customer.analyticsId) &&
                Objects.equals(referralId, customer.referralId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, contactEmail, createdOn, analyticsId, referralId);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", analyticsId='" + analyticsId + '\'' +
                ", referralId='" + referralId + '\'' +
                '}';
    }
}