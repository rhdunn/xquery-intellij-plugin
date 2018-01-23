/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm

import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmTypeCastResult

/**
 * Represents the `empty-sequence()` type.
 */
object XdmEmptySequence : XdmSequenceType {
    override val itemType get(): XdmSequenceType = XsUntyped
    override val lowerBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
    override val upperBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO

    override fun cast(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return XdmTypeCastResult(value, XsUntyped) // Not implemented.
    }

    override fun toString(): String = "empty-sequence()"
}

/**
 * Represents the `itemType?` occurrence indicator.
 */
open class XdmOptional(override val itemType: XdmSequenceType) : XdmSequenceType {
    override val lowerBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
    override val upperBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE

    override fun cast(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        if (type === XdmEmptySequence || type === XsUntyped) {
            return XdmTypeCastResult(value, type)
        }
        return itemType.cast(value, type)
    }

    override fun toString(): String = "$itemType?"
}

/**
 * Represents the `itemType*` occurrence indicator.
 */
open class XdmOptionalSequence(override val itemType: XdmSequenceType) : XdmSequenceType {
    override val lowerBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
    override val upperBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.MANY

    override fun cast(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return XdmTypeCastResult(value, XsUntyped) // Not implemented.
    }

    override fun toString(): String = "$itemType*"
}

/**
 * Represents the `itemType+` occurrence indicator.
 */
open class XdmSequence(override val itemType: XdmSequenceType) : XdmSequenceType {
    override val lowerBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
    override val upperBound get(): XdmSequenceType.Occurs = XdmSequenceType.Occurs.MANY

    override fun cast(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return XdmTypeCastResult(value, XsUntyped) // Not implemented.
    }

    override fun toString(): String = "$itemType+"
}

object XdmItemSequence : XdmOptionalSequence(XdmItem)