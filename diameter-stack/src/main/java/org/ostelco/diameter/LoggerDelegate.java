// Converted from Kotlin: LoggerDelegate.kt
package org.ostelco.diameter

import org.slf4j.Logger
import org.slf4j.LoggerFactory

package org.ostelco.diameter

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <R : Any> R.getLogger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}
