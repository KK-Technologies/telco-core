package org.ostelco.prime.storage;

import org.ostelco.prime.module.ResourceRegistry;

/**
 * Storage registry for getting store implementations
 */
public class StorageRegistry {
    
    public static DocumentStore getDocumentStore() {
        return ResourceRegistry.getResource(DocumentStore.class);
    }
    
    public static GraphStore getGraphStore() {
        return ResourceRegistry.getResource(GraphStore.class);
    }
}