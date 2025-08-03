// Converted from Kotlin: SlackNotificationReporter.kt
package org.ostelco.prime.slack

import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.slf4j.event.Level
import org.slf4j.event.Level.DEBUG
import org.slf4j.event.Level.ERROR
import org.slf4j.event.Level.INFO
import org.slf4j.event.Level.TRACE
import org.slf4j.event.Level.WARN
import java.time.Instant

package org.ostelco.prime.slack

import org.ostelco.prime.getLogger
import org.ostelco.prime.jsonmapper.asJson
import org.slf4j.event.Level
import org.slf4j.event.Level.DEBUG
import org.slf4j.event.Level.ERROR
import org.slf4j.event.Level.INFO
import org.slf4j.event.Level.TRACE
import org.slf4j.event.Level.WARN
import java.time.Instant

public public class SlackNotificationReporter {

    private final var logger by getLogger()

    public void notifyEvent(level: Level, message: String) {
        if (Registry.isInitialized) {
            final var body = asJson(
                    Message(
                            channel = Registry.channel,
                            // text = message,
                            // userName = Registry.userName,
                            // iconEmoji = levelToEmoji(level),
                            attachments = listOf(
                                    Attachment(
                                            fallback = message,
                                            color = levelToColor(level),
                                            authorName = Registry.userName,
                                            title = levelToTitle(level),
                                            text = message,
                                            fields = listOf(
                                                Field(
                                                        title = "Environment",
                                                        value = Registry.environment,
                                                        short = true
                                                ),
                                                Field(
                                                        title = "Deployment",
                                                        value = Registry.deployment,
                                                        short = true
                                                )
                                            ),
                                            timestampEpochSeconds = Instant.now().epochSecond
                                    )
                            )
                    ).format()
            )
            // logger.info(body) // for debugging only
            Registry.slackWebHookClient.post(body)
        }
    }

    private public void levelToEmoji(level: Level): String = when (level) {
        ERROR -> "fire"
        WARN -> "warning"
        INFO -> "information_source"
        DEBUG -> "robot_face"
        TRACE -> "mag"
    }

    private public void levelToColor(level: Level): String = when (level) {
        ERROR -> "danger"
        WARN -> "warning"
        INFO -> "good"
        DEBUG -> "#0080FF"
        TRACE -> "#C0C0C0"
    }

    private public void levelToTitle(level: Level): String {
        final var emoji = levelToEmoji(level)
        final var title = when (level) {
            ERROR -> "Error"
            WARN -> "Warning"
            INFO -> "Info"
            DEBUG -> "Debug"
            TRACE -> "Trace"
        }
        return ":" + emoji + ": " + title + ""
    }
}

