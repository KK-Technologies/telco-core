package org.ostelco.prime.storage;

import java.util.Collection;

/**
 * Client document store interface - simplified version
 */
public interface ClientDocumentStore {
    
    /**
     * Get notification tokens for a customer
     */
    Collection<String> getNotificationTokens(String customerId);
    
    /**
     * Add notification token for a customer
     */
    boolean addNotificationToken(String customerId, String token);
    
    /**
     * Get specific notification token
     */
    String getNotificationToken(String customerId, String applicationID);
    
    /**
     * Remove notification token
     */
    boolean removeNotificationToken(String customerId, String applicationID);
}