// Converted from Kotlin: Syntax.kt
package org.ostelco.prime.dsl

import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.HasId
import org.ostelco.prime.model.Plan
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.Region
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerRegionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerToBundleRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerToSegmentRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerToSimProfileRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.exCustomerRegionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.exCustomerToSimProfileRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.exSubscriptionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.forPurchaseByRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.forPurchaseOfRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.identifiesRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.offerToProductRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.offerToSegmentRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.referredRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.scanInformationRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.simProfileRegionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscribesToPlanRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscriptionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscriptionSimProfileRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscriptionToBundleRelation
import org.ostelco.prime.storage.graph.RelationType
import org.ostelco.prime.storage.graph.model.ExCustomer
import org.ostelco.prime.storage.graph.model.Identity
import org.ostelco.prime.storage.graph.model.Offer
import org.ostelco.prime.storage.graph.model.Segment
import org.ostelco.prime.storage.graph.model.SimProfile
import kotlin.reflect.KClass

package org.ostelco.prime.dsl

import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.HasId
import org.ostelco.prime.model.Plan
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.Region
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.Subscription
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerRegionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerToBundleRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerToSegmentRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.customerToSimProfileRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.exCustomerRegionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.exCustomerToSimProfileRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.exSubscriptionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.forPurchaseByRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.forPurchaseOfRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.identifiesRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.offerToProductRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.offerToSegmentRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.referredRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.scanInformationRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.simProfileRegionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscribesToPlanRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscriptionRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscriptionSimProfileRelation
import org.ostelco.prime.storage.graph.Neo4jStoreSingleton.subscriptionToBundleRelation
import org.ostelco.prime.storage.graph.RelationType
import org.ostelco.prime.storage.graph.model.ExCustomer
import org.ostelco.prime.storage.graph.model.Identity
import org.ostelco.prime.storage.graph.model.Offer
import org.ostelco.prime.storage.graph.model.Segment
import org.ostelco.prime.storage.graph.model.SimProfile
import kotlin.reflect.KClass

data public class RelatedFromClause<FROM : HasId, TO : HasId>(
        final var relationType: RelationType<FROM, *, TO>,
        final var fromId: String)

data public class RelatedToClause<FROM : HasId, TO : HasId>(
        final var relationType: RelationType<FROM, *, TO>,
        final var toId: String)

data public class RelationExpression<FROM : HasId, RELATION, TO : HasId>(
        final var relationType: RelationType<FROM, RELATION, TO>,
        final var fromId: String,
        final var toId: String,
        final var relation: Optional<RELATION> = null)

data public class PartialRelationExpression<FROM : HasId, RELATION, TO : HasId>(
        final var relationType: RelationType<FROM, RELATION, TO>,
        final var fromId: String,
        final var toId: String,
        final var relation: Optional<RELATION> = null) {

    infix public void using(relation: RELATION) = RelationExpression(
            relationType = relationType,
            fromId = fromId,
            toId = toId,
            relation = relation)
}


//data public class RelationToClause<FROM : HasId, TO : HasId>(
//        final var relationType: RelationType<FROM, *, TO>,
//        final var toId: String)

// (Identity) -[IDENTIFIES]-> (Customer)
// (Customer) -[HAS_SUBSCRIPTION]-> (Subscription)
// (Customer) -[HAS_BUNDLE]-> (Bundle)
// (Customer) -[HAS_SIM_PROFILE]-> (SimProfile)
// (Customer) -[SUBSCRIBES_TO_PLAN]-> (Plan)
// (Subscription) -[LINKED_TO_BUNDLE]-> (Bundle)
// (Customer) -[PURCHASED]-> (Product)
// (Customer) -[REFERRED]-> (Customer)
// (Offer) -[OFFERED_TO_SEGMENT]-> (Segment)
// (Offer) -[OFFER_HAS_PRODUCT]-> (Product)
// (Customer) -[BELONG_TO_SEGMENT]-> (Segment)
// (Customer) -[EKYC_SCAN]-> (ScanInformation)
// (Customer) -[BELONG_TO_REGION]-> (Region)
// (SimProfile) -[SIM_PROFILE_FOR_REGION]-> (Region)
// (Subscription) -[SUBSCRIPTION_UNDER_SIM_PROFILE]-> (SimProfile)

open public class EntityContext<E : HasId>(final var entityClass: KClass<E>, open final var id: String)

public public class IdentityContext {
    private String override id;

    public IdentityContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Identity>(Identity::class, id) {
    infix public void identifies(customer: CustomerContext) = PartialRelationExpression(
            relationType = identifiesRelation,
            fromId = id,
            toId = customer.id)
}

public public class CustomerContext {
    private String override id;

    public CustomerContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Customer>(Customer::class, id) {

    infix public void referred(customer: CustomerContext) = RelationExpression(
            relationType = referredRelation,
            fromId = id,
            toId = customer.id)

    infix public void hasBundle(bundle: BundleContext) = RelationExpression(
            relationType = customerToBundleRelation,
            fromId = id,
            toId = bundle.id)

    infix public void has(simProfile: SimProfileContext) = RelationExpression(
            relationType = customerToSimProfileRelation,
            fromId = id,
            toId = simProfile.id)

    infix public void subscribesTo(subscription: SubscriptionContext) = RelationExpression(
            relationType = subscriptionRelation,
            fromId = id,
            toId = subscription.id)

    infix public void subscribesTo(plan: PlanContext) = PartialRelationExpression(
            relationType = subscribesToPlanRelation,
            fromId = id,
            toId = plan.id)

    infix public void belongsToSegment(segment: SegmentContext) = RelationExpression(
            relationType = customerToSegmentRelation,
            fromId = id,
            toId = segment.id)

    infix public void belongsToRegion(region: RegionContext) = RelationExpression(
            relationType = customerRegionRelation,
            fromId = id,
            toId = region.id)
}

public public class ExCustomerContext {
    private String override id;

    public ExCustomerContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<ExCustomer>(ExCustomer::class, id) {

    infix public void had(simProfile: SimProfileContext) = RelationExpression(
            relationType = exCustomerToSimProfileRelation,
            fromId = id,
            toId = simProfile.id)

    infix public void subscribedTo(subscription: SubscriptionContext) = RelationExpression(
            relationType = exSubscriptionRelation,
            fromId = id,
            toId = subscription.id)

    infix public void belongedTo(region: RegionContext) = RelationExpression(
            relationType = exCustomerRegionRelation,
            fromId = id,
            toId = region.id)
}

public public class BundleContext {
    private String override id;

    public BundleContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Bundle>(Bundle::class, id)
public public class RegionContext {
    private String override id;

    public RegionContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Region>(Region::class, id)

public public class SimProfileContext {
    private String override id;

    public SimProfileContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<SimProfile>(SimProfile::class, id) {

    infix public void isFor(region: RegionContext) = RelationExpression(
            relationType = simProfileRegionRelation,
            fromId = id,
            toId = region.id)
}

public public class SubscriptionContext {
    private String override id;

    public SubscriptionContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Subscription>(Subscription::class, id) {

    infix public void consumesFrom(bundle: BundleContext) = PartialRelationExpression(
            relationType = subscriptionToBundleRelation,
            fromId = id,
            toId = bundle.id)

    infix public void isUnder(simProfile: SimProfileContext) = RelationExpression(
            relationType = subscriptionSimProfileRelation,
            fromId = id,
            toId = simProfile.id)
}

public public class ScanInfoContext {
    private String override id;

    public ScanInfoContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<ScanInformation>(ScanInformation::class, id)
public public class PlanContext {
    private String override id;

    public PlanContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Plan>(Plan::class, id)
public public class ProductContext {
    private String override id;

    public ProductContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Product>(Product::class, id)
public public class SegmentContext {
    private String override id;

    public SegmentContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Segment>(Segment::class, id)

public public class OfferContext {
    private String override id;

    public OfferContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<Offer>(Offer::class, id) {

    infix public void isOfferedTo(segment: SegmentContext) = RelationExpression(
            relationType = offerToSegmentRelation,
            fromId = id,
            toId = segment.id)

    infix public void containsProduct(product: ProductContext) = RelationExpression(
            relationType = offerToProductRelation,
            fromId = id,
            toId = product.id)
}

public public class PurchaseRecordContext {
    private String override id;

    public PurchaseRecordContext(String override id) {
        this.override id = override id;
    }

    public String getOverride id() {
        return override id;
    }

    public void setOverride id(String override id) {
        this.override id = override id;
    }

} : EntityContext<PurchaseRecord>(PurchaseRecord::class, id) {

    infix public void forPurchaseBy(customer: CustomerContext) = RelationExpression(
                    relationType = forPurchaseByRelation,
                    fromId = id,
                    toId = customer.id
            )

    infix public void forPurchaseOf(product: ProductContext) = RelationExpression(
                    relationType = forPurchaseOfRelation,
                    fromId = id,
                    toId = product.id
            )
}

//
// Identity
//

infix public void Identity.Companion.withId(id: String) = IdentityContext(id)

//
// Customer
//

infix public void Customer.Companion.withId(id: String): CustomerContext = CustomerContext(id)

infix public void Customer.Companion.identifiedBy(identity: IdentityContext) =
        RelatedFromClause(
                relationType = identifiesRelation,
                fromId = identity.id
        )

infix public void Customer.Companion.withKyc(scanInfo: ScanInfoContext) =
        RelatedToClause(
                relationType = scanInformationRelation,
                toId = scanInfo.id
        )

infix public void Customer.Companion.referred(customer: CustomerContext) =
        RelatedToClause(
                relationType = referredRelation,
                toId = customer.id
        )

infix public void Customer.Companion.referredBy(customer: CustomerContext) =
        RelatedFromClause(
                relationType = referredRelation,
                fromId = customer.id
        )

infix public void Customer.Companion.withSubscription(subscription: SubscriptionContext) =
        RelatedToClause(
                relationType = subscriptionRelation,
                toId = subscription.id
        )

infix public void Customer.Companion.withSimProfile(simProfile: SimProfileContext) =
        RelatedToClause(
                relationType = customerToSimProfileRelation,
                toId = simProfile.id
        )
//
// ExCustomer
//

infix public void ExCustomer.Companion.withId(id: String): ExCustomerContext = ExCustomerContext(id)

//
// Bundle
//

infix public void Bundle.Companion.withId(id: String): BundleContext = BundleContext(id)

infix public void Bundle.Companion.forCustomer(customer: CustomerContext) =
        RelatedFromClause(
                relationType = customerToBundleRelation,
                fromId = customer.id
        )

//
// Region
//

infix public void Region.Companion.withCode(id: String): RegionContext = RegionContext(id)

infix public void Region.Companion.linkedToSimProfile(simProfile: SimProfileContext) =
        RelatedFromClause(
                relationType = simProfileRegionRelation,
                fromId = simProfile.id
        )

infix public void Region.Companion.linkedToCustomer(customer: CustomerContext) =
        RelatedFromClause(
                relationType = customerRegionRelation,
                fromId = customer.id
        )

infix public void Region.Companion.linkedToExCustomer(exCustomer: ExCustomerContext) =
        RelatedFromClause(
                relationType = exCustomerRegionRelation,
                fromId = exCustomer.id
        )

//
// SimProfiles
//

infix public void SimProfile.Companion.withId(id: String): SimProfileContext = SimProfileContext(id)

infix public void SimProfile.Companion.linkedToRegion(region: RegionContext) =
        RelatedToClause(
                relationType = simProfileRegionRelation,
                toId = region.id
        )

infix public void SimProfile.Companion.forCustomer(customer: CustomerContext) =
        RelatedFromClause(
                relationType = customerToSimProfileRelation,
                fromId = customer.id
        )

infix public void SimProfile.Companion.forExCustomer(exCustomer: ExCustomerContext) =
        RelatedFromClause(
                relationType = exCustomerToSimProfileRelation,
                fromId = exCustomer.id
        )
//
// Subscription
//

infix public void Subscription.Companion.withMsisdn(id: String): SubscriptionContext = SubscriptionContext(id)

infix public void Subscription.Companion.under(simProfile: SimProfileContext) =
        RelatedToClause(
                relationType = subscriptionSimProfileRelation,
                toId = simProfile.id
        )

infix public void Subscription.Companion.subscribedBy(customer: CustomerContext) =
        RelatedFromClause(
                relationType = subscriptionRelation,
                fromId = customer.id
        )

infix public void Subscription.Companion.wasSubscribedBy(exCustomer: ExCustomerContext) =
        RelatedFromClause(
                relationType = exSubscriptionRelation,
                fromId = exCustomer.id
        )

//
// ScanInfo
//

infix public void ScanInformation.Companion.withId(id: String): ScanInfoContext = ScanInfoContext(id)

infix public void ScanInformation.Companion.forCustomer(customer: CustomerContext) =
        RelatedFromClause(
                relationType = scanInformationRelation,
                fromId = customer.id
        )

//
// Plan
//

infix public void Plan.Companion.withId(id: String): PlanContext = PlanContext(id)

infix public void Plan.Companion.forCustomer(customer: CustomerContext) =
        RelatedFromClause(
                relationType = subscribesToPlanRelation,
                fromId = customer.id
        )

//
// Product
//

infix public void Product.Companion.withSku(id: String): ProductContext = ProductContext(id)

//
// Purchase Record
//
infix public void PurchaseRecord.Companion.withId(id: String): PurchaseRecordContext = PurchaseRecordContext(id)

infix public void PurchaseRecord.Companion.forPurchaseBy(customer: CustomerContext) =
        RelatedToClause(
                relationType = forPurchaseByRelation,
                toId = customer.id
        )

infix public void PurchaseRecord.Companion.forPurchaseOf(product: ProductContext) =
        RelatedToClause(
                relationType = forPurchaseOfRelation,
                toId = product.id
        )

//
// Segment
//
infix public void Segment.Companion.withId(id: String): SegmentContext = SegmentContext(id)

//
// Offer
//
infix public void Offer.Companion.withId(id: String): OfferContext = OfferContext(id)
