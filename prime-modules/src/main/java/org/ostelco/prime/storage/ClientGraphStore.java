package org.ostelco.prime.storage;

import java.util.Collection;
import java.util.Map;

/**
 * Client graph store interface - simplified version
 */
public interface ClientGraphStore {
    
    /**
     * Get Customer Profile
     */
    Either<StoreError, String> getCustomer(String identity);
    
    /**
     * Create Customer Profile
     */
    Either<StoreError, Void> addCustomer(String identity, String customer, String referredBy);
    
    /**
     * Update Customer Profile
     */
    Either<StoreError, Void> updateCustomer(String identity, String nickname, String contactEmail);
    
    /**
     * Remove Customer for testing
     */
    Either<StoreError, Void> removeCustomer(String identity);
    
    /**
     * Get Products for a given Customer
     */
    Either<StoreError, Map<String, String>> getProducts(String identity);
    
    /**
     * Get Product to perform OCS Topup
     */
    Either<StoreError, String> getProduct(String identity, String sku);
    
    /**
     * Get Regions associated with the Customer
     */
    Either<StoreError, Collection<String>> getAllRegionDetails(String identity);
    
    /**
     * Get a Region associated with the Customer
     */
    Either<StoreError, String> getRegionDetails(String identity, String regionCode);
}