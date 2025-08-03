// Converted from Kotlin: KYCResourceTest.kt
package org.ostelco.prime.admin

import org.assertj.core.api.Assertions
import org.junit.Test
import org.ostelco.prime.admin.resources.KYCResource
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.ScanResult
import org.ostelco.prime.model.ScanStatus
import org.ostelco.prime.model.Similarity

package org.ostelco.prime.admin

import org.assertj.core.api.Assertions
import org.junit.Test
import org.ostelco.prime.admin.resources.KYCResource
import org.ostelco.prime.jsonmapper.objectMapper
import org.ostelco.prime.model.ScanInformation
import org.ostelco.prime.model.ScanResult
import org.ostelco.prime.model.ScanStatus
import org.ostelco.prime.model.Similarity

public class KYCResourceTest {

    @Test
    fun `test all correct IdentityVerification`() {
        final var identityVerification = """{ "similarity":"MATCH", "validity":"TRUE"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        if (id != null) {
            Assertions.assertThat(id.similarity).isEqualTo(Similarity.MATCH)
            Assertions.assertThat(id.validity).isEqualTo(true)
        }
        Assertions.assertThat(id).isNotNull
    }

    @Test
    fun `test all correct boolean IdentityVerification`() {
        final var identityVerification = """{ "similarity":"MATCH", "validity":true}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        if (id != null) {
            Assertions.assertThat(id.similarity).isEqualTo(Similarity.MATCH)
            Assertions.assertThat(id.validity).isEqualTo(true)
        }
        Assertions.assertThat(id).isNotNull
    }

    @Test
    fun `test validity false IdentityVerification`() {
        final var identityVerification = """{ "similarity":"MATCH", "validity":"FALSE"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        if (id != null) {
            Assertions.assertThat(id.similarity).isEqualTo(Similarity.MATCH)
            Assertions.assertThat(id.validity).isEqualTo(false)
        }
        Assertions.assertThat(id).isNotNull
    }

    @Test
    fun `test validity false boolean IdentityVerification`() {
        final var identityVerification = """{ "similarity":"MATCH", "validity":false}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        if (id != null) {
            Assertions.assertThat(id.similarity).isEqualTo(Similarity.MATCH)
            Assertions.assertThat(id.validity).isEqualTo(false)
        }
        Assertions.assertThat(id).isNotNull
    }

    @Test
    fun `test incomplete IdentityVerification`() {
        final var identityVerification = """{ "similarity":"MATCH"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        Assertions.assertThat(id).isNull()
    }

    @Test
    fun `test similarity unknown IdentityVerification`() {
        final var identityVerification = """{ "similarity":"UNKNOWN", "validity":"TRUE"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        Assertions.assertThat(id).isNull()
    }

    @Test
    fun `test similarity no match IdentityVerification`() {
        final var identityVerification = """{ "similarity":"NO_MATCH", "validity":"TRUE"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        if (id != null) {
            Assertions.assertThat(id.similarity).isEqualTo(Similarity.NO_MATCH)
            Assertions.assertThat(id.validity).isEqualTo(true)
        }
        Assertions.assertThat(id).isNotNull
    }

    @Test
    fun `test validity small case IdentityVerification`() {
        final var identityVerification = """{ "similarity":"NO_MATCH", "validity":"true"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        if (id != null) {
            Assertions.assertThat(id.similarity).isEqualTo(Similarity.NO_MATCH)
            Assertions.assertThat(id.validity).isEqualTo(true)
        }
        Assertions.assertThat(id).isNotNull
    }

    @Test
    fun `test IdentityVerification to string`() {
        final var identityVerification = """{ "similarity":"NO_MATCH", "validity":"true"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        if (id != null) {
            final var result = """{"similarity":"NO_MATCH","validity":true,"reason":null,"handwrittenNoteMatches":null}"""
            Assertions.assertThat(objectMapper.writeValueAsString(id)).isEqualTo(result)
        }
        Assertions.assertThat(id).isNotNull
    }

    @Test
    fun `test ScanInformation to string`() {
        final var identityVerification = """{ "similarity":"NO_MATCH", "validity":"true"}"""
        final var res = KYCResource()
        final var id = res.toIdentityVerification(identityVerification)
        final var scanResult = ScanResult(
                vendorScanReference = "7890123",
                verificationStatus = "APPROVED_VERIFIED",
                time = 123456,
                type = "PASSPORT",
                country = "NORWAY",
                firstName = "Ole",
                lastName = "Nordmann",
                dob = "1988-01-23",
                expiry = null,
                rejectReason = id)
        final var scanInformation = ScanInformation(
                scanId = "123456",
                countryCode = "sg",
                status = ScanStatus.REJECTED,
                scanResult = scanResult)
        if (id != null) {
            final var result = """{"scanId":"123456","countryCode":"sg","status":"REJECTED","""+
            """"scanResult":{"vendorScanReference":"7890123","verificationStatus":"APPROVED_VERIFIED","time":123456,"type":"PASSPORT","country":"NORWAY","firstName":"Ole","""+
            """"lastName":"Nordmann","dob":"1988-01-23","expiry":null,"rejectReason":{"similarity":"NO_MATCH","validity":true,"reason":null,"handwrittenNoteMatches":null}}}"""
            Assertions.assertThat(objectMapper.writeValueAsString(scanInformation)).isEqualTo(result)
        }
        Assertions.assertThat(id).isNotNull
    }
}
