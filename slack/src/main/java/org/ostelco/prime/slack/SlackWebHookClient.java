// Converted from Kotlin: SlackWebHookClient.kt
package org.ostelco.prime.slack

import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import org.ostelco.prime.getLogger

package org.ostelco.prime.slack

import org.apache.http.client.entity.EntityBuilder
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import org.ostelco.prime.getLogger

/**
 * Simple HttpClient for Slack
 */
public class SlackWebHookClient(
        private final var webHookUri: String,
        private final var httpClient: CloseableHttpClient) {

    private final var logger by getLogger()

    public void post(body: String) {
        final var entity = EntityBuilder.create().apply { this.text = body }.build()
        final var request = HttpPost(webHookUri).apply { this.entity = entity }
        final var response = httpClient.execute(request)
        final var responseText = EntityUtils.toString(response.entity)
        if (responseText != "ok") {
            logger.error("Failed to send messages to slack. Reason: {}", responseText)
        }
    }
}