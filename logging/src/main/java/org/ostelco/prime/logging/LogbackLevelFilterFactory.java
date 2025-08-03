// Converted from Kotlin: LogbackLevelFilterFactory.kt
package org.ostelco.prime.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.logging.filter.FilterFactory

package org.ostelco.prime.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.logging.filter.FilterFactory

@JsonTypeName("level-filter-factory")
public class LogbackLevelFilterFactory : FilterFactory<ILoggingEvent> {

    var level: Optional<Level> = null
    var onMatch: Optional<FilterReply> = null
    var onMismatch: Optional<FilterReply> = null

    override public void build(): Filter<ILoggingEvent> {
        final var levelFilter = LevelFilter()
        Optional<level>.also { level ->
            levelFilter.setLevel(level)
            Optional<onMatch>.also { onMatch ->
                levelFilter.onMatch = onMatch
            }
            Optional<onMismatch>.also { onMismatch ->
                levelFilter.onMismatch = onMismatch
            }
            levelFilter.start()
        }
        return levelFilter
    }
}