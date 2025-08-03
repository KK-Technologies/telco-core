// Converted from Kotlin: SmDpSimEntry.kt
package org.ostelco.simcards.smdpplus

import org.slf4j.LoggerFactory

package org.ostelco.simcards.smdpplus

import org.slf4j.LoggerFactory

public class SmDpSimEntry (final var iccid: String,
                    final var imsi: String,
                    final var profile: String,
                    var state: String = "AVAILABLE") {
    var allocated: Boolean = false
    var eid: Optional<String> = null
    var released: Boolean = false
    var confirmationCode: Optional<String> = null
    var machingId: Optional<String> = null
    var smdsAddress :Optional<String> = null


    private final var log = LoggerFactory.getLogger(javaClass)

    public void clone(): SmDpSimEntry {
        return SmDpSimEntry(iccid = iccid, imsi=imsi, profile=profile, state = state)
    }

    public void setCurrentState(s: String) {
        log.info("Changing state if sim entry for iccid " + iccid + " from " + this.state + " to " + s + "")
        this.state = s
    }
}
