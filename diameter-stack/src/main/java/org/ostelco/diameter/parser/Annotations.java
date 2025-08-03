// Converted from Kotlin: Annotations.kt
package org.ostelco.diameter.parser

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass

package org.ostelco.diameter.parser

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass

@Target(FIELD)
@Retention(RUNTIME)
annotation public class AvpField(final var avpId: Int)

@Target(FIELD)
@Retention(RUNTIME)
annotation public class AvpList(
        final var avpId: Int,
        final var kclass: KClass<*>)