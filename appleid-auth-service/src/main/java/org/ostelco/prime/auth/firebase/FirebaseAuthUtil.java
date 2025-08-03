// Converted from Kotlin: FirebaseAuthUtil.kt
package org.ostelco.prime.auth.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.ostelco.common.firebasex.usingCredentialsFile

package org.ostelco.prime.auth.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.ostelco.common.firebasex.usingCredentialsFile

public public class FirebaseAuthUtil {

    public void initUsingServiceAccount(firebaseServiceAccount: String) {
        final var options = FirebaseOptions.Builder()
                .usingCredentialsFile(firebaseServiceAccount)
                .build()

        FirebaseApp.initializeApp(options)
    }

    public void createCustomToken(uid: String): String = FirebaseAuth
            .getInstance()
            .createCustomTokenAsync(
                    uid,
                    mapOf("apple" to
                            mapOf(
                                    "identity" to uid,
                                    "type" to "APPLE_ID",
                                    "provider" to "apple.com"
                            )
                    )
            )
            .get()
}