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
/*
 * XPath and XQuery Type System Part 2: Simple and Complex Types
 *
 * Reference: https://www.w3.org/TR/xpath-datamodel-31
 * Reference: https://www.w3.org/TR/2012/REC-xmlschema11-1-20120405
 */
package uk.co.reecedunn.intellij.plugin.xdm.model

import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName

open class XmlSchemaType(val typeName: QName?, val baseType: XmlSchemaType?): XdmSequenceType {
    override fun toString(): String = typeName.toString()

    override val itemType get(): XdmSequenceType = XsUntyped
    override val lowerBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
    override val upperBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.MANY

    val isPrimitive get(): Boolean {
        return baseType == XsAnyAtomicType || // Includes xs:untypedAtomic (XDM casting rules)
               this == XsInteger ||           // XDM casting rules
               this == XsYearMonthDuration || // XDM casting rules
               this == XsDayTimeDuration      // XDM casting rules
    }

    override fun cast(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return when (type) {
            is XdmAtomicType ->
                if (type.isPrimitive)
                    castPrimitive(value, type)
                else
                    XdmTypeCastResult(value, XsUntyped) // Not implemented.
            else ->
                XdmTypeCastResult(value, XsUntyped) // Not implemented.
        }
    }

    open fun castPrimitive(value: Any?, type: XdmSequenceType): XdmTypeCastResult {
        return XdmTypeCastResult(value, XsUntyped) // Not implemented.
    }
}

open class XdmComplexType(typeName: QName): XmlSchemaType(typeName, XsAnyType)

open class XdmSimpleType(typeName: QName?, baseType: XmlSchemaType): XmlSchemaType(typeName, baseType)

open class XdmAtomicType(typeName: QName, baseType: XmlSchemaType):
        XdmSimpleType(typeName, baseType),
        XdmItemType {

    override val itemType get(): XdmSequenceType = this
    override val lowerBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
    override val upperBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE

    override fun toString(): String = typeName.toString()
}

open class XdmListType(typeName: QName?, override val itemType: XdmSequenceType):
        XdmSimpleType(typeName, XsAnySimpleType) {

    constructor(itemType: XdmSequenceType): this(null, itemType)

    override val lowerBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ZERO
    override val upperBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.MANY
}

open class XdmUnionType(typeName: QName, val unionOf: Array<XdmSimpleType>):
        XdmSimpleType(typeName, XsAnySimpleType) {

    override val itemType get(): XdmSequenceType = this
    override val lowerBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
    override val upperBound: XdmSequenceType.Occurs = XdmSequenceType.Occurs.ONE
}
