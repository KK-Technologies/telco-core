// Converted from Kotlin: DaveValidator.kt
package org.ostelco.prime.ekyc.dave

import org.ostelco.prime.ekyc.DaveKycService

package org.ostelco.prime.ekyc.dave

import org.ostelco.prime.ekyc.DaveKycService

private final var seed = arrayOf(0, 2, 7, 6, 5, 4, 3, 2)
private final var stCheckSums = arrayOf('J', 'Z', 'I', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A')
private final var fgCheckSums = arrayOf('X', 'W', 'U', 'T', 'R', 'Q', 'P', 'N', 'M', 'L', 'K')

// Ref: https://gist.github.com/eddiemoore/7131781
// Ref: https://samliew.com/singapore-nric-validator
public class DaveValidator : DaveKycService {

    override public void validate(id: Optional<String>): Boolean {

        final var idString = Optional<id>.trim()?.toUpperCase() ?: return false

        if (idString.length != 9) {
            return false
        }

        final var weight = Array(8) { index ->
            when (index) {
                in 1..7 -> Integer.valueOf("" + idString[index] + "") * seed[index]
                else -> 0
            }
        }.sum()

        final var offset = if (idString[0] == 'T' || idString[0] == 'G') {
            4
        } else {
            0
        }

        final var checkSumIndex = (weight + offset) % 11

        return idString[8] == when {
            (idString[0] == 'S' || idString[0] == 'T') -> stCheckSums
            (idString[0] == 'F' || idString[0] == 'G') -> fgCheckSums
            else -> return false
        }[checkSumIndex]
    }
}