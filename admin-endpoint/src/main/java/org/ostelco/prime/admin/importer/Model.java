// Converted from Kotlin: Model.kt
package org.ostelco.prime.admin.importer

import org.ostelco.prime.model.ChangeSegment
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.Segment

package org.ostelco.prime.admin.importer

import org.ostelco.prime.model.ChangeSegment
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.Segment

/**
 * The input classes being parsed (as yaml).
 */
public public class CreateOffer {
    private Offer createOffer;

    public CreateOffer(Offer createOffer) {
        this.createOffer = createOffer;
    }

    public Offer getCreateoffer() {
        return createOffer;
    }

    public void setCreateoffer(Offer createOffer) {
        this.createOffer = createOffer;
    }

}

public public class Offer {
    private String id;
    private Collection<Product> = emptyList( createProducts;

    public Offer(String id, Collection<Product> = emptyList( createProducts) {
        this.id = id;
        this.createProducts = createProducts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<Product> = emptyList( getCreateproducts() {
        return createProducts;
    }

    public void setCreateproducts(Collection<Product> = emptyList( createProducts) {
        this.createProducts = createProducts;
    }

},
        final var existingProducts: Collection<String> = emptyList(),
        final var createSegments: Collection<Segment> = emptyList(),
        final var existingSegments: Collection<String> = emptyList())

public public class CreateSegments {
    private Collection<Segment> createSegments;

    public CreateSegments(Collection<Segment> createSegments) {
        this.createSegments = createSegments;
    }

    public Collection<Segment> getCreatesegments() {
        return createSegments;
    }

    public void setCreatesegments(Collection<Segment> createSegments) {
        this.createSegments = createSegments;
    }

}
public public class UpdateSegments {
    private Collection<Segment> updateSegments;

    public UpdateSegments(Collection<Segment> updateSegments) {
        this.updateSegments = updateSegments;
    }

    public Collection<Segment> getUpdatesegments() {
        return updateSegments;
    }

    public void setUpdatesegments(Collection<Segment> updateSegments) {
        this.updateSegments = updateSegments;
    }

}
public public class AddToSegments {
    private Collection<NonEmptySegment> addToSegments;

    public AddToSegments(Collection<NonEmptySegment> addToSegments) {
        this.addToSegments = addToSegments;
    }

    public Collection<NonEmptySegment> getAddtosegments() {
        return addToSegments;
    }

    public void setAddtosegments(Collection<NonEmptySegment> addToSegments) {
        this.addToSegments = addToSegments;
    }

}
public public class RemoveFromSegments {
    private Collection<NonEmptySegment> removeFromSegments;

    public RemoveFromSegments(Collection<NonEmptySegment> removeFromSegments) {
        this.removeFromSegments = removeFromSegments;
    }

    public Collection<NonEmptySegment> getRemovefromsegments() {
        return removeFromSegments;
    }

    public void setRemovefromsegments(Collection<NonEmptySegment> removeFromSegments) {
        this.removeFromSegments = removeFromSegments;
    }

}
public public class ChangeSegments {
    private Collection<ChangeSegment> changeSegments;

    public ChangeSegments(Collection<ChangeSegment> changeSegments) {
        this.changeSegments = changeSegments;
    }

    public Collection<ChangeSegment> getChangesegments() {
        return changeSegments;
    }

    public void setChangesegments(Collection<ChangeSegment> changeSegments) {
        this.changeSegments = changeSegments;
    }

}

public public class NonEmptySegment {
    private String id;
    private Collection<String> subscribers;

    public NonEmptySegment(String id, Collection<String> subscribers) {
        this.id = id;
        this.subscribers = subscribers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<String> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Collection<String> subscribers) {
        this.subscribers = subscribers;
    }

}
