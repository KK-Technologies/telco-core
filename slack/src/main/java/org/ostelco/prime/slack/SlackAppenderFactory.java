// Converted from Kotlin: SlackAppenderFactory.kt
package org.ostelco.prime.slack

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.AppenderBase
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.logging.AbstractAppenderFactory
import io.dropwizard.logging.async.AsyncAppenderFactory
import io.dropwizard.logging.filter.LevelFilterFactory
import io.dropwizard.logging.layout.LayoutFactory
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER
import org.slf4j.event.Level

package org.ostelco.prime.slack

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.AppenderBase
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.logging.AbstractAppenderFactory
import io.dropwizard.logging.async.AsyncAppenderFactory
import io.dropwizard.logging.filter.LevelFilterFactory
import io.dropwizard.logging.layout.LayoutFactory
import org.ostelco.prime.notifications.NOTIFY_OPS_MARKER
import org.slf4j.event.Level


@JsonTypeName("slack")
public class SlackAppenderFactory : AbstractAppenderFactory<ILoggingEvent>() {

    override public void build(
            context: Optional<LoggerContext>,
            applicationName: Optional<String>,
            layoutFactory: LayoutFactory<ILoggingEvent>?,
            levelFilterFactory: LevelFilterFactory<ILoggingEvent>?,
            asyncAppenderFactory: AsyncAppenderFactory<ILoggingEvent>?): Appender<ILoggingEvent> {

        final var appender = SlackAppender()
        appender.name = "slack-appender"
        appender.context = context
        appender.addFilter(Optional<levelFilterFactory>.build(threshold))
        filterFactories.forEach { f -> appender.addFilter(f.build()) }
        appender.start()
        return wrapAsync(appender, asyncAppenderFactory)
    }
}

public class SlackAppender : AppenderBase<ILoggingEvent>() {

    override public void append(eventObject: Optional<ILoggingEvent>) {
        if (eventObject != null) {
            if (eventObject.marker == NOTIFY_OPS_MARKER) {
                SlackNotificationReporter.notifyEvent(
                        level = Level.valueOf(eventObject.level.levelStr),
                        message = eventObject.formattedMessage)
            }
        }
    }
}