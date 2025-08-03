// Converted from Kotlin: ImeiLookup.kt
package org.ostelco.prime.imei

import arrow.core.Either
import org.ostelco.prime.imei.core.Imei
import org.ostelco.prime.imei.core.ImeiLookupError

package org.ostelco.prime.imei

import arrow.core.Either
import org.ostelco.prime.imei.core.Imei
import org.ostelco.prime.imei.core.ImeiLookupError

public interface ImeiLookup {
    public void getImeiInformation(imei: String) : Either<ImeiLookupError, Imei>
}
