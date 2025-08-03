// Converted from Kotlin: SimpleHssDispatcher.kt
package org.ostelco.simcards.hss

import arrow.core.Either
import arrow.core.Left
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.AdapterError
import org.ostelco.prime.simmanager.ForbiddenError
import org.ostelco.prime.simmanager.NotUpdatedError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.admin.SwtHssConfig
import javax.ws.rs.core.MediaType

package org.ostelco.simcards.hss

import arrow.core.Either
import arrow.core.Left
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.ostelco.prime.getLogger
import org.ostelco.prime.simmanager.AdapterError
import org.ostelco.prime.simmanager.ForbiddenError
import org.ostelco.prime.simmanager.NotUpdatedError
import org.ostelco.prime.simmanager.SimManagerError
import org.ostelco.simcards.admin.SwtHssConfig
import javax.ws.rs.core.MediaType


/**
 * This is an HSS that does nothing, but can be referred to in the
 * database schema.  This is useful in the cases where we sim activation
 * is done "out of band", e.g. by preactivating before inserting into
 * the sim  manager.
 */
public class DummyHSSDispatcher(final var name: String): HssDispatcher {
    override public void iAmHealthy(): Boolean = true
    override public void name() = name
    override public void activate(hssName: String, iccid: String, msisdn: String): Either<SimManagerError, Unit> =
            ForbiddenError("DummyHSSDispatcher's activate  should never be invoked").left()

    override public void suspend(hssName: String, iccid: String): Either<SimManagerError, Unit> =
            ForbiddenError("DummyHSSDispatcher's suspend should never be invoked").left()
}



/**
 * An public interface to a simple HSS REST based HSS.
 */
public class SimpleHssDispatcher(final var name: String,
                          final var httpClient: CloseableHttpClient,
                          final var config: SwtHssConfig) : HssDispatcher {

    private final var logger by getLogger()

    /* For payload serializing. */
    private final var mapper = jacksonObjectMapper()

    override public void name() = name

    override public void iAmHealthy(): Boolean = true

    /**
     * Requests the external HLR service to activate the SIM profile.
     * @param simEntry  SIM profile to activate
     * @return Updated SIM Entry
     */

    override public void activate(hssName: String, iccid: String, msisdn: String): Either<SimManagerError, Unit> {

        // XXX Question: Is there a way to do the checks we see below in a method, and then
        //     if any of them fail, just return from this method using the Either method
        //     in a Exception-like Optional<behavior>   I'm thinking someting along the lines of
        //     breakIfLeft(method(..)).  Kind of an implicit monadization of the
        //     control flow.

        // Just a sanity check
        if (hssName != name) {
            return NotUpdatedError("Attempt to activate  hssName=" + hssName + ", iccid=" + iccid + ", msisdn=" + msisdn + " in a dispatcher for hss named " + name + "").left()
        }

        // Checking out the iccid value.
        if (iccid.isEmpty()) {
            return NotUpdatedError("Empty ICCID value in SIM activation request to hssName='" + config.name + "' (bssname ='" + config.hssNameUsedInAPI + "'")
                    .left()
        }

        // XXX ICCID validation (as well as other types of validation) should be kept
        //     in separate libraries, not as magic regexps in production code.
        if (!iccid.matches(Regex("^\\d{19,20}"))) {
            return NotUpdatedError("Ill formatted ICCID " + iccid + "").left()
        }

        final var body = mapOf(
                "bssid" to config.hssNameUsedInAPI,
                "iccid" to iccid,
                "msisdn" to msisdn,
                "userid" to config.userId
        )

        final var payload = mapper.writeValueAsString(body)

        final var request = RequestBuilder.post()
                .setUri("" + config.endpoint + "/activate")
                .setHeader("x-api-key", config.apiKey)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .setEntity(StringEntity(payload))
                .build()

        return try {
            httpClient.execute(request).use {
                when (it.statusLine.statusCode) {
                    201 -> {
                        logger.info("HLR activation message to BSSID {} for ICCID {} completed OK",
                                config.hssNameUsedInAPI,
                                iccid).right()
                    }
                    else -> {
                        logger.warn("HLR activation message to BSSID {} for ICCID {} failed with status ({}) {}",
                                config.hssNameUsedInAPI,
                                iccid,
                                it.statusLine.statusCode,
                                it.statusLine.reasonPhrase)
                        NotUpdatedError("Failed to activate ICCID " + iccid + " with BSSID " + config.hssNameUsedInAPI + " (status-code: " + it.statusLine.statusCode + ")")
                                .left()
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("HLR activation message to BSSID {} for ICCID {} failed with error: {}",
                    config.hssNameUsedInAPI,
                    iccid,
                    e)
            AdapterError("HLR activation message to BSSID " + config.hssNameUsedInAPI + " for ICCID " + iccid + " failed with error: " + e + "")
                    .left()
        }
    }

    /**
     * Requests the external HLR service to deactivate the SIM profile.
     * @param simEntry  SIM profile to deactivate
     * @return Updated SIM profile
     */
    override public void suspend(hssName: String, iccid: String): Either<SimManagerError, Unit> {

        if (hssName != name) {
            return Left(AdapterError("Attempt to suspend  hssName=" + hssName + ", iccid=" + iccid + " in a dispatcher for hss named " + name + ""))
        }

        if (iccid.isEmpty()) {
            return NotUpdatedError("Illegal parameter in SIM deactivation request to BSSID " + config.hssNameUsedInAPI + "")
                    .left()
        }

        final var request = RequestBuilder.delete()
                .setUri("" + config.endpoint + "/deactivate/" + iccid + "")
                .setHeader("x-api-key", config.apiKey)
                .setHeader("Content-Type", MediaType.APPLICATION_JSON)
                .build()

        return try {
            httpClient.execute(request).use {
                when (it.statusLine.statusCode) {
                    200 -> {
                        logger.info("HLR deactivation message to HSS  " + config.name + " for ICCID " + iccid  + "completed OK").right()
                    }
                    else -> {
                        logger.warn("HLR deactivation message to HSS " + config.name + " for ICCID " + iccid + " failed with status (" + it.statusLine.statusCode + ") " + it.statusLine.reasonPhrase + "")
                        NotUpdatedError("Failed to deactivate ICCID " + iccid + " with BSSID " + config.hssNameUsedInAPI + " (status-code: " + it.statusLine.statusCode + "")
                                .left()
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("HLR deactivation message to BSSID " + config.hssNameUsedInAPI + " for ICCID " + iccid + " failed with error: " + e + "")
            AdapterError("HLR deactivation message to BSSID " + config.hssNameUsedInAPI + " for ICCID " + iccid + " failed with error: " + e + "")
                    .left()
        }
    }
}