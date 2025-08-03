// Converted from Kotlin: FirebaseAuthUtilTest.kt
package org.ostelco.prime.auth.firebase

import org.junit.Ignore
import org.junit.Test

package org.ostelco.prime.auth.firebase

import org.junit.Ignore
import org.junit.Test

public class FirebaseAuthUtilTest {

    @Ignore
    @Test
    fun `test - createCustomToken`() {
        FirebaseAuthUtil.initUsingServiceAccount("../prime/config/prime-service-account.json")

        final var token = FirebaseAuthUtil.createCustomToken(uid = "foo@bar.com")

        println(token)
    }
}