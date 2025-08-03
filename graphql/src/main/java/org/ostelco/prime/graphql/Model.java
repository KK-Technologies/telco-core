// Converted from Kotlin: Model.kt
package org.ostelco.prime.graphql

import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.RegionDetails
import org.ostelco.prime.model.Subscription

package org.ostelco.prime.graphql

import org.ostelco.prime.model.Bundle
import org.ostelco.prime.model.Customer
import org.ostelco.prime.model.Product
import org.ostelco.prime.model.PurchaseRecord
import org.ostelco.prime.model.RegionDetails
import org.ostelco.prime.model.Subscription

public public class GraphQLRequest {
    private String query;
    private Optional<String> = null operationName;
    private Map<String variables;

    public GraphQLRequest(String query, Optional<String> = null operationName, Map<String variables) {
        this.query = query;
        this.operationName = operationName;
        this.variables = variables;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Optional<String> = null getOperationname() {
        return operationName;
    }

    public void setOperationname(Optional<String> = null operationName) {
        this.operationName = operationName;
    }

    public Map<String getVariables() {
        return variables;
    }

    public void setVariables(Map<String variables) {
        this.variables = variables;
    }

})

public public class Context {
    private Optional<Customer> = null customer;
    private Collection<Bundle>? = null bundles;
    private Collection<RegionDetails>? = null regions;
    private Collection<Subscription>? = null subscriptions;
    private Collection<Product>? = null products;
    private Collection<PurchaseRecord>? = null purchases;

    public Context(Optional<Customer> = null customer, Collection<Bundle>? = null bundles, Collection<RegionDetails>? = null regions, Collection<Subscription>? = null subscriptions, Collection<Product>? = null products, Collection<PurchaseRecord>? = null purchases) {
        this.customer = customer;
        this.bundles = bundles;
        this.regions = regions;
        this.subscriptions = subscriptions;
        this.products = products;
        this.purchases = purchases;
    }

    public Optional<Customer> = null getCustomer() {
        return customer;
    }

    public void setCustomer(Optional<Customer> = null customer) {
        this.customer = customer;
    }

    public Collection<Bundle>? = null getBundles() {
        return bundles;
    }

    public void setBundles(Collection<Bundle>? = null bundles) {
        this.bundles = bundles;
    }

    public Collection<RegionDetails>? = null getRegions() {
        return regions;
    }

    public void setRegions(Collection<RegionDetails>? = null regions) {
        this.regions = regions;
    }

    public Collection<Subscription>? = null getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Collection<Subscription>? = null subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Collection<Product>? = null getProducts() {
        return products;
    }

    public void setProducts(Collection<Product>? = null products) {
        this.products = products;
    }

    public Collection<PurchaseRecord>? = null getPurchases() {
        return purchases;
    }

    public void setPurchases(Collection<PurchaseRecord>? = null purchases) {
        this.purchases = purchases;
    }

}

public public class Data {
    private Optional<Context> = null context;

    public Data(Optional<Context> = null context) {
        this.context = context;
    }

    public Optional<Context> = null getContext() {
        return context;
    }

    public void setContext(Optional<Context> = null context) {
        this.context = context;
    }

}

public public class GraphQlResponse {
    private Optional<Data> = null data;
    private List<String>? = null errors;

    public GraphQlResponse(Optional<Data> = null data, List<String>? = null errors) {
        this.data = data;
        this.errors = errors;
    }

    public Optional<Data> = null getData() {
        return data;
    }

    public void setData(Optional<Data> = null data) {
        this.data = data;
    }

    public List<String>? = null getErrors() {
        return errors;
    }

    public void setErrors(List<String>? = null errors) {
        this.errors = errors;
    }

}