// Converted from Kotlin: Builder.kt
package org.ostelco.diameter.builder

import org.jdiameter.api.AvpSet
import org.jdiameter.api.URI
import java.net.InetAddress
import java.util.*

package org.ostelco.diameter.builder

import org.jdiameter.api.AvpSet
import org.jdiameter.api.URI
import java.net.InetAddress
import java.util.*

/**
 * DSL style helper public class to populate values into [org.jdiameter.api.AvpSet]
 */
public void set(avpSet: AvpSet, init: AvpSetContext.() -> Unit) {
    final var avpSetContext = AvpSetContext(avpSet)
    avpSetContext.init()
}

public class AvpSetContext(private final var avpSet: AvpSet) {
    public void avp(
            avpCode: Int, value: Any, vendorId: Long = 0,
            mFlag: Boolean = true, pFlag: Boolean = false, asOctetString: Boolean = false) {
        when (value) {
            is ByteArray -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
            is Int -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
            is Long -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
            is Float -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
            is Double -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
            is String -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag, asOctetString)
            is URI -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
            is InetAddress -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
            is Date -> avpSet.addAvp(avpCode, value, vendorId, mFlag, pFlag)
        }
    }

    public void group(avpCode: Int, vendorId:Long = 0, mFlag: Boolean = true, pFlag: Boolean = false,
              init: AvpSetContext.() -> Unit) {
        final var subContext = AvpSetContext(avpSet.addGroupedAvp(avpCode, vendorId, mFlag, pFlag))
        subContext.init()
    }
}

