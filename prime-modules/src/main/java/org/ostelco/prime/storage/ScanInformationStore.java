package org.ostelco.prime.storage;

/**
 * Scan information store interface
 */
public interface ScanInformationStore {

    /**
     * Function to upsert scan information data from the 3rd party eKYC scan
     */
    Either<StoreError, Void> upsertVendorScanInformation(
            String customerId,
            String countryCode,
            java.util.Map<String, String> vendorData
    );

    /**
     * Get extended status information for scan
     */
    java.util.Map<String, String> getExtendedStatusInformation(String scanInformation);
}