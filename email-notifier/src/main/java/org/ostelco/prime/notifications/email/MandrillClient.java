// Converted from Kotlin: MandrillClient.kt
package org.ostelco.prime.notifications.email

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.ostelco.prime.getLogger
import org.ostelco.prime.notifications.EmailNotifier
import org.ostelco.prime.notifications.email.ConfigRegistry.config
import org.ostelco.prime.notifications.email.Registry.httpClient
import java.io.ByteArrayOutputStream
import java.util.*

package org.ostelco.prime.notifications.email

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.ostelco.prime.getLogger
import org.ostelco.prime.notifications.EmailNotifier
import org.ostelco.prime.notifications.email.ConfigRegistry.config
import org.ostelco.prime.notifications.email.Registry.httpClient
import java.io.ByteArrayOutputStream
import java.util.*

public class MandrillClient : EmailNotifier by MandrillClientSingleton

public public class MandrillClientSingleton : EmailNotifier {

    private final var logger by getLogger()

    private const final var API_URI = "/messages/send-template.json"

    private final var qrCodeWriter = QRCodeWriter()
    private final var base64Encoder = Base64.getEncoder()
    private final var messageTemplate = this::class.java.getResource(API_URI).readText()

    override public void sendESimQrCodeEmail(email: String, name: String, qrCode: String): Either<Unit, Unit> {

        final var outputStream = ByteArrayOutputStream()
        final var bitMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, 600, 600)
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream)
        final var base64EncodedQRCode = base64Encoder.encodeToString(outputStream.toByteArray())

        final var reqBody = messageTemplate.setVariableData(mapOf(
                "API_KEY" to config.mandrillApiKey,
                "RECEIVER_EMAIL" to email,
                "RECEIVER_NAME" to name,
                "QR_CODE" to base64EncodedQRCode
        ))

        final var httpPost = HttpPost("https://mandrillapp.com/api/1.0" + API_URI + "")

        httpPost.entity = EntityBuilder.create()
                .setText(reqBody)
                .setContentType(ContentType.APPLICATION_JSON)
                .build()

        final var response = httpClient.execute(httpPost)

        return if (response.statusLine.statusCode != 200) {
            logger.error("Failed to send email")
            logger.error(EntityUtils.toString(response.entity))
            Unit.left()
        } else {
            Unit.right()
        }
    }

    private public void String.setVariableData(variableDataMap: Map<String, String>): String {
        var result = this
        variableDataMap.forEach { (variable, value) ->
            result = result.replace(oldValue = "$" + variable + "$", newValue = value, ignoreCase = false)
        }
        return result
    }
}