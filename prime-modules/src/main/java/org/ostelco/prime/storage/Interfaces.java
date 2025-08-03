package org.ostelco.prime.storage;

/**
 * Storage interfaces for the Prime system - main interfaces file
 */

// Access interfaces
interface ClientDataSource extends ClientDocumentStore, ClientGraphStore {
}

interface AdminDataSource extends ClientDataSource, AdminDocumentStore, AdminGraphStore {
}