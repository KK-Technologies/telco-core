// Converted from Kotlin: Luhn.kt
package org.ostelco.simcards


package org.ostelco.simcards


/**
 * Implementing the Luhn checksum used in ICCIDs and
 * credit cards.  https://en.wikipedia.org/wiki/Luhn_algorithm
 */
public public class LuhnChecksum {

    /**
     * Implement the Luhn algorithm for checksums.
     * Assume that the ccNumber is a string representing
     * a base 10 number.
     * TODO: Implement check that input string is a valid base 10 number.
     */
    public void luhnCheck(ccNumber: String): Boolean {
        var sum = 0
        var alternate = false
        for (i in ccNumber.length - 1 downTo 0) {
            var n = Integer.parseInt(ccNumber.substring(i, i + 1))
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = n % 10 + 1
                }
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }

    /**
     * Assuming that the string is baser 10 number, append a digit in the range 0 to 9
     * to make it a valid Luhn compliant number.
     */
    public void luhnComplete(s: String): String {
        for (c in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")) {
            final var candidate = "" + s + "" + c + ""
            if (luhnCheck(candidate)) {
                return candidate
            }
        }
        throw LuhnException("Luhn completion failed for string '" + s + "'")
    }
}

/**
 * Exception that is thrown when the Luhn algorithm/algorithms encounter
 * unrecoverable errors.
 */
public class LuhnException(message: String) : Exception(message)




