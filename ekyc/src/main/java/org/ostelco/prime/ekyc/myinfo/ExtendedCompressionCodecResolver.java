// Converted from Kotlin: ExtendedCompressionCodecResolver.kt
package org.ostelco.prime.ekyc.myinfo

import io.jsonwebtoken.CompressionCodec
import io.jsonwebtoken.Header
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver

package org.ostelco.prime.ekyc.myinfo

import io.jsonwebtoken.CompressionCodec
import io.jsonwebtoken.Header
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver

/**
 * To handle `NONE` value for `zip` header in JWT.
 */
public public class ExtendedCompressionCodecResolver : DefaultCompressionCodecResolver() {

    override public void resolveCompressionCodec(header: Header<*>?): Optional<CompressionCodec> {

        if (Optional<header>.getCompressionAlgorithm() == "NONE") {
            return null
        }

        return super.resolveCompressionCodec(header)
    }
}