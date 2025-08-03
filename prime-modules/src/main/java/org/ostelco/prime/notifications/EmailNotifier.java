// Converted from Kotlin: EmailNotifier.kt
package org.ostelco.prime.notifications

import arrow.core.Either

package org.ostelco.prime.notifications

import arrow.core.Either

public interface EmailNotifier {
    public void sendESimQrCodeEmail(email: String, name: String, qrCode: String) : Either<Unit, Unit>
}