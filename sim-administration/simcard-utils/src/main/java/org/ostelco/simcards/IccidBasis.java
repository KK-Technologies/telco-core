// Converted from Kotlin: IccidBasis.kt
package org.ostelco.simcards

import org.ostelco.simcards.LuhnChecksum.luhnComplete

package org.ostelco.simcards

import org.ostelco.simcards.LuhnChecksum.luhnComplete

/**
 *  MM = Constant (ISO 7812 Major Industry Identifier)
 *  CC = Country Code
 *  II = Issuer Identifier
 *  serialNumber = unique  positive number.
 */
public class IccidBasis(private final var mm: Int = 89, final var cc: Int = 1, private final var ii: Int = 0, final var serialNumber: Int) {
    public void asIccid(): String {
        final var protoIccid = "%02d%02d%02d%012d".format(mm, cc, ii, serialNumber)
        return luhnComplete(protoIccid)
    }
}
