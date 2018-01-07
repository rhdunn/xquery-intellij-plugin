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
package uk.co.reecedunn.intellij.plugin.xdm

import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XmlSchemaType

private fun localNameTypeMapOf(vararg x: XmlSchemaType): Map<String, XmlSchemaType> {
    return x.map { type -> type.typeName!!.localName.staticValue to type }.toMap()
}

private val BUILTIN_XMLSCHEMA_TYPES = localNameTypeMapOf(
        XsAnyAtomicType,
        XsAnySimpleType,
        XsAnyType,
        XsAnyURI,
        XsBase64Binary,
        XsBoolean,
        XsByte,
        XsDate,
        XsDateTime,
        XsDateTimeStamp,
        XsDayTimeDuration,
        XsDecimal,
        XsDouble,
        XsDuration,
        XsENTITIES,
        XsENTITY,
        XsFloat,
        XsGDay,
        XsGMonth,
        XsGMonthDay,
        XsGYear,
        XsGYearMonth,
        XsHexBinary,
        XsID,
        XsIDREF,
        XsIDREFS,
        XsInt,
        XsInteger,
        XsLanguage,
        XsLong,
        XsName,
        XsNCName,
        XsNegativeInteger,
        XsNMTOKEN,
        XsNMTOKENS,
        XsNonNegativeInteger,
        XsNonPositiveInteger,
        XsNormalizedString,
        XsNOTATION,
        XsNumeric,
        XsPositiveInteger,
        XsQName,
        XsShort,
        XsString,
        XsTime,
        XsToken,
        XsUnsignedByte,
        XsUnsignedInt,
        XsUnsignedLong,
        XsUnsignedShort,
        XsUntyped,
        XsUntypedAtomic,
        XsYearMonthDuration)

fun QName.toXmlSchemaType(): XmlSchemaType? {
    // TODO: Use QName.expand()?.namespace?.staticValue instead, for lexical QName support.
    if (namespace?.staticValue == "http://www.w3.org/2001/XMLSchema") {
        return BUILTIN_XMLSCHEMA_TYPES[localName.staticValue]
    }
    return null
}
