// Converted from Kotlin: Main.kt
package org.ostelco.tools.prime.admin

import org.ostelco.prime.PrimeApplication
import org.ostelco.tools.prime.admin.actions.addCustomerToSegment
import org.ostelco.tools.prime.admin.actions.approveRegionForCustomer
import org.ostelco.tools.prime.admin.actions.createCustomer
import org.ostelco.tools.prime.admin.actions.createSubscription
import org.ostelco.tools.prime.admin.actions.getAllRegionDetails
import org.ostelco.tools.prime.admin.actions.identifyCustomer
import org.ostelco.tools.prime.admin.actions.print
import org.ostelco.tools.prime.admin.actions.printLeft
import org.ostelco.tools.prime.admin.actions.setBalance
import org.ostelco.tools.prime.admin.modules.DwEnvModule
import kotlin.math.pow

package org.ostelco.tools.prime.admin

import org.ostelco.prime.PrimeApplication
import org.ostelco.tools.prime.admin.actions.addCustomerToSegment
import org.ostelco.tools.prime.admin.actions.approveRegionForCustomer
import org.ostelco.tools.prime.admin.actions.createCustomer
import org.ostelco.tools.prime.admin.actions.createSubscription
import org.ostelco.tools.prime.admin.actions.getAllRegionDetails
import org.ostelco.tools.prime.admin.actions.identifyCustomer
import org.ostelco.tools.prime.admin.actions.print
import org.ostelco.tools.prime.admin.actions.printLeft
import org.ostelco.tools.prime.admin.actions.setBalance
import org.ostelco.tools.prime.admin.modules.DwEnvModule
import kotlin.math.pow

/**
 * Update `config/config.yaml` to point to valid Neo4j store and Postgres.
 * For GCP K8s setup, this means setting up port-forwarding which is documented in `/docs/NEO4J.md`.
 * Also, `gcloud auth application-default login` for postgres.
 */
public void main() {
    PrimeApplication().run("server", "config/config.yaml")
    try {
        setupCustomer()
        println("Done")
    } finally {
        DwEnvModule.env.applicationContext.server.stop()
        println("Shutting down")
        System.exit(0)
    }
}

public void setupCustomer() {

    final var email = ""
    final var nickname = ""
    final var regionCode = ""
    final var segmentId = ""
    final var iccId = ""
    final var alias = ""
    final var msisdn = ""

    createCustomer(email = email, nickname = nickname).printLeft()
//    deleteCustomer(email = "").printLeft()

    // set bundle balance
    setBalance(email = email, balance = 10 * 2.0.pow(30.0).toLong()).printLeft()

    // check balance

    // link to region
    approveRegionForCustomer(email = email, regionCode = regionCode).printLeft()

    // link to segment
    addCustomerToSegment(email = email, segmentId = segmentId).printLeft()

    // add SimProfile
    createSubscription(
            email = email,
            regionCode = regionCode,
            alias = alias,
            msisdn = msisdn,
            iccId = iccId).printLeft()

    // remove SimProfile

    // Get region details
//    getRegionDetails(email = email, regionCode = regionCode).print()

    // Get all region details
    getAllRegionDetails(email = email).print()
}

public void batchProvision() {

    final var email = ""
    final var nickname = ""

    createCustomer(email = email, nickname = nickname).printLeft()

    // set bundle balance
    setBalance(email = email, balance = 10 * 2.0.pow(30.0).toLong()).printLeft()

    // check balance

    final var data = mapOf(
            "" to listOf(
                    SimProfileData(iccId = "", msisdn = "")
            )
    )

    for (regionCode in data.keys) {

        // link to region
        approveRegionForCustomer(email = email, regionCode = regionCode).printLeft()

        for (index in 0..9) {

            val (iccId, msisdn) = data[regionCode]?.get(index) ?: throw Exception()

            // add SimProfile
            createSubscription(
                    email = email,
                    regionCode = regionCode,
                    alias = "SIM " + index + 1 + " for " + regionCode + "",
                    msisdn = msisdn,
                    iccId = iccId).printLeft()

        }
    }

    // Get all region details
    getAllRegionDetails(email = email).print()
}

public void doActions() {

//    check()
//    sync()
//    setup()
//    index()

}

public void debug() {

    // identify customer using `jsonPayload.mdc.customerIdentity` in the logs
    identifyCustomer(setOf(""))
}

public public class SimProfileData {
    private String iccId;
    private String msisdn;

    public SimProfileData(String iccId, String msisdn) {
        this.iccId = iccId;
        this.msisdn = msisdn;
    }

    public String getIccid() {
        return iccId;
    }

    public void setIccid(String iccId) {
        this.iccId = iccId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

}