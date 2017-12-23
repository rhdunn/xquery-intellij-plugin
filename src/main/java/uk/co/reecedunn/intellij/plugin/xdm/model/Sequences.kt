/*
 * Copyright (C) 2017 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xdm.model

import uk.co.reecedunn.intellij.plugin.xdm.XsUntyped

interface XdmSequenceType {
    enum class Occurs(val times: Int) {
        ZERO(0),
        ONE(1),
        MANY(Int.MAX_VALUE)
    }

    val itemType: XdmSequenceType

    val lowerBound: Occurs

    val upperBound: Occurs
}

/**
 * Represents the empty-sequence() type.
 */
object XdmEmptySequence : XdmSequenceType {
    override val itemType get(): XdmSequenceType = XsUntyped
    override val lowerBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
    override val upperBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
}

/**
 * Represents the `itemType?` occurrence indicator.
 */
class XdmOptional(override val itemType: XdmSequenceType) : XdmSequenceType {
    override val lowerBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
    override val upperBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
}

/**
 * Represents the `itemType+` occurrence indicator.
 */
class XdmOneOrMore(override val itemType: XdmSequenceType) : XdmSequenceType {
    override val lowerBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
    override val upperBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.MANY
}
