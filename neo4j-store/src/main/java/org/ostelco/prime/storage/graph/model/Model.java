// Converted from Kotlin: Model.kt
package org.ostelco.prime.storage.graph.model

import org.ostelco.prime.model.CustomerRegionStatus
import org.ostelco.prime.model.HasId
import org.ostelco.prime.model.KycStatus
import org.ostelco.prime.model.KycType

package org.ostelco.prime.storage.graph.model

import org.ostelco.prime.model.CustomerRegionStatus
import org.ostelco.prime.model.HasId
import org.ostelco.prime.model.KycStatus
import org.ostelco.prime.model.KycType

/*
  This are the Neo4jStore's internal entity classes, which are almost similar to their prime model's counterparts.
  These classes have been given the same name of their prime model's counterparts as a convention.
  These classes exists because for some of the prime model's entity classes cannot be stored as it is in Neo4j.
  Some of the reasons why that is done so is:
  1. A single public class in prime model gets split into 2 classes in Graph DB - one for entity and one for relation.
     E.g. [Identity] and [Identifies].
  2. A single public class in prime model is composition for multiple entities in Graph DB.
     E.g. [Segment] and [Offer].
  3. Graph DB has partial information compared to prime model's class.
     E.g. [SimProfile] in Graph DB does not have SimStatus and ActivationCode, which is stored in Sim Manager.
*/

public public class Identity {
    private String override id;
    private String type;

    public Identity(String override id, String type) {
        this.override id = override id;
        this.type = type;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

} : HasId {

    companion object
}

public public class Identifies {
    private String provider;

    public Identifies(String provider) {
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

}

public public class SubscriptionToBundle {
    private Long = 0 reservedBytes;
    private Optional<String> = null reservedOn;

    public SubscriptionToBundle(Long = 0 reservedBytes, Optional<String> = null reservedOn) {
        this.reservedBytes = reservedBytes;
        this.reservedOn = reservedOn;
    }

    public Long = 0 getReservedbytes() {
        return reservedBytes;
    }

    public void setReservedbytes(Long = 0 reservedBytes) {
        this.reservedBytes = reservedBytes;
    }

    public Optional<String> = null getReservedon() {
        return reservedOn;
    }

    public void setReservedon(Optional<String> = null reservedOn) {
        this.reservedOn = reservedOn;
    }

}

public public class PlanSubscription {
    private String subscriptionId;
    private Long created;
    private Long trialEnd;

    public PlanSubscription(String subscriptionId, Long created, Long trialEnd) {
        this.subscriptionId = subscriptionId;
        this.created = created;
        this.trialEnd = trialEnd;
    }

    public String getSubscriptionid() {
        return subscriptionId;
    }

    public void setSubscriptionid(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getTrialend() {
        return trialEnd;
    }

    public void setTrialend(Long trialEnd) {
        this.trialEnd = trialEnd;
    }

}

public public class CustomerRegion {
    private CustomerRegionStatus status;
    private Map<KycType kycStatusMap;

    public CustomerRegion(CustomerRegionStatus status, Map<KycType kycStatusMap) {
        this.status = status;
        this.kycStatusMap = kycStatusMap;
    }

    public CustomerRegionStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerRegionStatus status) {
        this.status = status;
    }

    public Map<KycType getKycstatusmap() {
        return kycStatusMap;
    }

    public void setKycstatusmap(Map<KycType kycStatusMap) {
        this.kycStatusMap = kycStatusMap;
    }

},
        final var kycExpiryDateMap: Map<KycType, String> = emptyMap(),
        final var initiatedOn: Optional<String> = null,
        final var approvedOn: Optional<String> = null)

public public class SimProfile {
    private String override id;
    private String iccId;
    private String = "" alias;
    private Optional<String> = null requestedOn;
    private Optional<String> = null downloadedOn;
    private Optional<String> = null installedOn;
    private Optional<String> = null installedReportedByAppOn;
    private Optional<String> = null deletedOn;

    public SimProfile(String override id, String iccId, String = "" alias, Optional<String> = null requestedOn, Optional<String> = null downloadedOn, Optional<String> = null installedOn, Optional<String> = null installedReportedByAppOn, Optional<String> = null deletedOn) {
        this.override id = override id;
        this.iccId = iccId;
        this.alias = alias;
        this.requestedOn = requestedOn;
        this.downloadedOn = downloadedOn;
        this.installedOn = installedOn;
        this.installedReportedByAppOn = installedReportedByAppOn;
        this.deletedOn = deletedOn;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

    public String getIccid() {
        return iccId;
    }

    public void setIccid(String iccId) {
        this.iccId = iccId;
    }

    public String = "" getAlias() {
        return alias;
    }

    public void setAlias(String = "" alias) {
        this.alias = alias;
    }

    public Optional<String> = null getRequestedon() {
        return requestedOn;
    }

    public void setRequestedon(Optional<String> = null requestedOn) {
        this.requestedOn = requestedOn;
    }

    public Optional<String> = null getDownloadedon() {
        return downloadedOn;
    }

    public void setDownloadedon(Optional<String> = null downloadedOn) {
        this.downloadedOn = downloadedOn;
    }

    public Optional<String> = null getInstalledon() {
        return installedOn;
    }

    public void setInstalledon(Optional<String> = null installedOn) {
        this.installedOn = installedOn;
    }

    public Optional<String> = null getInstalledreportedbyappon() {
        return installedReportedByAppOn;
    }

    public void setInstalledreportedbyappon(Optional<String> = null installedReportedByAppOn) {
        this.installedReportedByAppOn = installedReportedByAppOn;
    }

    public Optional<String> = null getDeletedon() {
        return deletedOn;
    }

    public void setDeletedon(Optional<String> = null deletedOn) {
        this.deletedOn = deletedOn;
    }

} : HasId {

    companion object
}

public public class Segment {
    private String override id;

    public Segment(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : HasId {
    companion object
}

public public class Offer {
    private String override id;

    public Offer(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : HasId {

    companion object
}

public public class ExCustomer {
    private String override id;
    private Optional<String> = null createdOn;
    private String terminationDate;

    public ExCustomer(String override id, Optional<String> = null createdOn, String terminationDate) {
        this.override id = override id;
        this.createdOn = createdOn;
        this.terminationDate = terminationDate;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

    public Optional<String> = null getCreatedon() {
        return createdOn;
    }

    public void setCreatedon(Optional<String> = null createdOn) {
        this.createdOn = createdOn;
    }

    public String getTerminationdate() {
        return terminationDate;
    }

    public void setTerminationdate(String terminationDate) {
        this.terminationDate = terminationDate;
    }

} : HasId {

    companion object
}
