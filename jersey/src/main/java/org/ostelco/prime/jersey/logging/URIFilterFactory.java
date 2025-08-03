// Converted from Kotlin: URIFilterFactory.kt
package org.ostelco.prime.jersey.logging

import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.logging.filter.FilterFactory

package org.ostelco.prime.jersey.logging

import ch.qos.logback.access.spi.IAccessEvent
import ch.qos.logback.core.filter.Filter
import ch.qos.logback.core.spi.FilterReply
import com.fasterxml.jackson.annotation.JsonTypeName
import io.dropwizard.logging.filter.FilterFactory

@JsonTypeName("URI")
public class URIFilterFactory : FilterFactory<IAccessEvent> {

    var uriSet: Set<String> = emptySet()
        set(value) {
            field = value.map { "/" + it + "" }.toSet()
        }

    override public void build() = object : Filter<IAccessEvent>() {
        override public void decide(event: IAccessEvent): FilterReply {
            return if (uriSet.contains(event.requestURI)) {
                FilterReply.DENY
            } else {
                FilterReply.NEUTRAL
            }
        }
    }
}