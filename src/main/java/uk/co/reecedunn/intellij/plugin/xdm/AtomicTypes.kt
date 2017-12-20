/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
 * XPath and XQuery Type System Part 3: Atomic Types
 *
 * Reference: https://www.w3.org/TR/xpath-datamodel-31
 * Reference: https://www.w3.org/TR/2012/REC-xmlschema11-1-20120405
 */
package uk.co.reecedunn.intellij.plugin.xdm

import uk.co.reecedunn.intellij.plugin.xdm.model.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicType

val XsUntypedAtomic = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "untypedAtomic"), XsAnyAtomicType)

val XsDateTime = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "dateTime"), XsAnyAtomicType)

val XsDateTimeStamp = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "dateTimeStamp"), XsDateTime)

val XsDate = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "date"), XsAnyAtomicType)

val XsTime = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "time"), XsAnyAtomicType)

val XsDuration = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "duration"), XsAnyAtomicType)

val XsYearMonthDuration = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "yearMonthDuration"), XsDuration)

val XsDayTimeDuration = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "dayTimeDuration"), XsDuration)

val XsFloat = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "float"), XsAnyAtomicType)

val XsDouble = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "double"), XsAnyAtomicType)

val XsDecimal = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "decimal"), XsAnyAtomicType)

val XsInteger = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "integer"), XsDecimal)

val XsNonPositiveInteger = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "nonPositiveInteger"), XsInteger)

val XsNegativeInteger = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "negativeInteger"), XsNonPositiveInteger)

val XsLong = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "long"), XsInteger)

val XsInt = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "int"), XsLong)

val XsShort = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "short"), XsInt)

val XsByte = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "byte"), XsShort)

val XsNonNegativeInteger = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger"), XsInteger)

val XsUnsignedLong = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "unsignedLong"), XsNonNegativeInteger)

val XsUnsignedInt = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "unsignedInt"), XsUnsignedLong)

val XsUnsignedShort = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "unsignedShort"), XsUnsignedInt)

val XsUnsignedByte = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "unsignedByte"), XsUnsignedShort)

val XsPositiveInteger = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "positiveInteger"), XsNonNegativeInteger)

val XsGYearMonth = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "gYearMonth"), XsAnyAtomicType)

val XsGYear = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "gYear"), XsAnyAtomicType)

val XsGMonthDay = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "gMonthDay"), XsAnyAtomicType)

val XsGDay = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "gDay"), XsAnyAtomicType)

val XsGMonth = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "gMonth"), XsAnyAtomicType)

val XsString = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "string"), XsAnyAtomicType)

val XsNormalizedString = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "normalizedString"), XsString)

val XsToken = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "token"), XsNormalizedString)

val XsLanguage = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "language"), XsToken)

val XsNMTOKEN = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "NMTOKEN"), XsToken)

val XsName = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "Name"), XsToken)

val XsNCName = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "NCName"), XsName)

val XsID = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "ID"), XsNCName)

val XsIDREF = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "IDREF"), XsNCName)

val XsENTITY = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "ENTITY"), XsNCName)

val XsBoolean = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "boolean"), XsAnyAtomicType)

val XsBase64Binary = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), XsAnyAtomicType)

val XsHexBinary = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "hexBinary"), XsAnyAtomicType)

val XsAnyURI = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "anyURI"), XsAnyAtomicType)

val XsQName = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "QName"), XsAnyAtomicType)

val XsNOTATION = XdmAtomicType(QName("http://www.w3.org/2001/XMLSchema", "NOTATION"), XsAnyAtomicType)
