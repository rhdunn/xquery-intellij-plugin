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
package uk.co.reecedunn.intellij.plugin.xdm.tests

import junit.framework.TestCase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XmlSchemaType

class AtomicTypes : TestCase() {
    fun testXsUntypedAtomic() {
        assertThat(XsUntypedAtomic.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsUntypedAtomic.typeName?.localName, `is`("untypedAtomic"))
        assertThat(XsUntypedAtomic.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDateTime() {
        assertThat(XsDateTime.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsDateTime.typeName?.localName, `is`("dateTime"))
        assertThat(XsDateTime.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDateTimeStamp() {
        assertThat(XsDateTimeStamp.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsDateTimeStamp.typeName?.localName, `is`("dateTimeStamp"))
        assertThat(XsDateTimeStamp.baseType, `is`(XsDateTime as XmlSchemaType))
    }

    fun testXsDate() {
        assertThat(XsDate.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsDate.typeName?.localName, `is`("date"))
        assertThat(XsDate.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsTime() {
        assertThat(XsTime.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsTime.typeName?.localName, `is`("time"))
        assertThat(XsTime.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDuration() {
        assertThat(XsDuration.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsDuration.typeName?.localName, `is`("duration"))
        assertThat(XsDuration.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsYearMonthDuration() {
        assertThat(XsYearMonthDuration.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsYearMonthDuration.typeName?.localName, `is`("yearMonthDuration"))
        assertThat(XsYearMonthDuration.baseType, `is`(XsDuration as XmlSchemaType))
    }

    fun testXsDayTimeDuration() {
        assertThat(XsDayTimeDuration.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsDayTimeDuration.typeName?.localName, `is`("dayTimeDuration"))
        assertThat(XsDayTimeDuration.baseType, `is`(XsDuration as XmlSchemaType))
    }

    fun testXsFloat() {
        assertThat(XsFloat.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsFloat.typeName?.localName, `is`("float"))
        assertThat(XsFloat.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDouble() {
        assertThat(XsDouble.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsDouble.typeName?.localName, `is`("double"))
        assertThat(XsDouble.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDecimal() {
        assertThat(XsDecimal.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsDecimal.typeName?.localName, `is`("decimal"))
        assertThat(XsDecimal.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsInteger() {
        assertThat(XsInteger.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsInteger.typeName?.localName, `is`("integer"))
        assertThat(XsInteger.baseType, `is`(XsDecimal as XmlSchemaType))
    }

    fun testXsNonPositiveInteger() {
        assertThat(XsNonPositiveInteger.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNonPositiveInteger.typeName?.localName, `is`("nonPositiveInteger"))
        assertThat(XsNonPositiveInteger.baseType, `is`(XsInteger as XmlSchemaType))
    }

    fun testXsNegativeInteger() {
        assertThat(XsNegativeInteger.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNegativeInteger.typeName?.localName, `is`("negativeInteger"))
        assertThat(XsNegativeInteger.baseType, `is`(XsNonPositiveInteger as XmlSchemaType))
    }

    fun testXsLong() {
        assertThat(XsLong.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsLong.typeName?.localName, `is`("long"))
        assertThat(XsLong.baseType, `is`(XsInteger as XmlSchemaType))
    }

    fun testXsInt() {
        assertThat(XsInt.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsInt.typeName?.localName, `is`("int"))
        assertThat(XsInt.baseType, `is`(XsLong as XmlSchemaType))
    }

    fun testXsShort() {
        assertThat(XsShort.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsShort.typeName?.localName, `is`("short"))
        assertThat(XsShort.baseType, `is`(XsInt as XmlSchemaType))
    }

    fun testXsByte() {
        assertThat(XsByte.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsByte.typeName?.localName, `is`("byte"))
        assertThat(XsByte.baseType, `is`(XsShort as XmlSchemaType))
    }

    fun testXsNonNegativeInteger() {
        assertThat(XsNonNegativeInteger.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNonNegativeInteger.typeName?.localName, `is`("nonNegativeInteger"))
        assertThat(XsNonNegativeInteger.baseType, `is`(XsInteger as XmlSchemaType))
    }

    fun testXsUnsignedLong() {
        assertThat(XsUnsignedLong.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsUnsignedLong.typeName?.localName, `is`("unsignedLong"))
        assertThat(XsUnsignedLong.baseType, `is`(XsNonNegativeInteger as XmlSchemaType))
    }

    fun testXsUnsignedInt() {
        assertThat(XsUnsignedInt.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsUnsignedInt.typeName?.localName, `is`("unsignedInt"))
        assertThat(XsUnsignedInt.baseType, `is`(XsUnsignedLong as XmlSchemaType))
    }

    fun testXsUnsignedShort() {
        assertThat(XsUnsignedShort.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsUnsignedShort.typeName?.localName, `is`("unsignedShort"))
        assertThat(XsUnsignedShort.baseType, `is`(XsUnsignedInt as XmlSchemaType))
    }

    fun testXsUnsignedByte() {
        assertThat(XsUnsignedByte.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsUnsignedByte.typeName?.localName, `is`("unsignedByte"))
        assertThat(XsUnsignedByte.baseType, `is`(XsUnsignedShort as XmlSchemaType))
    }

    fun testXsPositiveInteger() {
        assertThat(XsPositiveInteger.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsPositiveInteger.typeName?.localName, `is`("positiveInteger"))
        assertThat(XsPositiveInteger.baseType, `is`(XsNonNegativeInteger as XmlSchemaType))
    }

    fun testXsGYearMonth() {
        assertThat(XsGYearMonth.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsGYearMonth.typeName?.localName, `is`("gYearMonth"))
        assertThat(XsGYearMonth.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGYear() {
        assertThat(XsGYear.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsGYear.typeName?.localName, `is`("gYear"))
        assertThat(XsGYear.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGMonthDay() {
        assertThat(XsGMonthDay.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsGMonthDay.typeName?.localName, `is`("gMonthDay"))
        assertThat(XsGMonthDay.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGDay() {
        assertThat(XsGDay.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsGDay.typeName?.localName, `is`("gDay"))
        assertThat(XsGDay.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGMonth() {
        assertThat(XsGMonth.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsGMonth.typeName?.localName, `is`("gMonth"))
        assertThat(XsGMonth.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsString() {
        assertThat(XsString.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsString.typeName?.localName, `is`("string"))
        assertThat(XsString.baseType, `is`(XsAnyAtomicType as XmlSchemaType))

        assertThat(XsString.pattern, `is`(nullValue()))

        assertThat(XsString.matches("true"), `is`(true))
        assertThat(XsString.matches("1234"), `is`(true))
        assertThat(XsString.matches("()"), `is`(true))
        assertThat(XsString.matches("Lorem ipsum."), `is`(true))
    }

    fun testXsNormalizedString() {
        assertThat(XsNormalizedString.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNormalizedString.typeName?.localName, `is`("normalizedString"))
        assertThat(XsNormalizedString.baseType, `is`(XsString as XmlSchemaType))
    }

    fun testXsToken() {
        assertThat(XsToken.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsToken.typeName?.localName, `is`("token"))
        assertThat(XsToken.baseType, `is`(XsNormalizedString as XmlSchemaType))
    }

    fun testXsLanguage() {
        assertThat(XsLanguage.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsLanguage.typeName?.localName, `is`("language"))
        assertThat(XsLanguage.baseType, `is`(XsToken as XmlSchemaType))
    }

    fun testXsNMTOKEN() {
        assertThat(XsNMTOKEN.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNMTOKEN.typeName?.localName, `is`("NMTOKEN"))
        assertThat(XsNMTOKEN.baseType, `is`(XsToken as XmlSchemaType))
    }

    fun testXsName() {
        assertThat(XsName.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsName.typeName?.localName, `is`("Name"))
        assertThat(XsName.baseType, `is`(XsToken as XmlSchemaType))
    }

    fun testXsNCName() {
        assertThat(XsNCName.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNCName.typeName?.localName, `is`("NCName"))
        assertThat(XsNCName.baseType, `is`(XsName as XmlSchemaType))
    }

    fun testXsID() {
        assertThat(XsID.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsID.typeName?.localName, `is`("ID"))
        assertThat(XsID.baseType, `is`(XsNCName as XmlSchemaType))
    }

    fun testXsIDREF() {
        assertThat(XsIDREF.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsIDREF.typeName?.localName, `is`("IDREF"))
        assertThat(XsIDREF.baseType, `is`(XsNCName as XmlSchemaType))
    }

    fun testXsENTITY() {
        assertThat(XsENTITY.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsENTITY.typeName?.localName, `is`("ENTITY"))
        assertThat(XsENTITY.baseType, `is`(XsNCName as XmlSchemaType))
    }

    fun testXsBoolean() {
        assertThat(XsBoolean.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsBoolean.typeName?.localName, `is`("boolean"))
        assertThat(XsBoolean.baseType, `is`(XsAnyAtomicType as XmlSchemaType))

        assertThat(XsBoolean.pattern.toString(), `is`("true|false|[10]"))

        assertThat(XsBoolean.matches("true"), `is`(true))
        assertThat(XsBoolean.matches("false"), `is`(true))

        assertThat(XsBoolean.matches("1"), `is`(true))
        assertThat(XsBoolean.matches("0"), `is`(true))

        assertThat(XsBoolean.matches("TRUE"), `is`(false))
        assertThat(XsBoolean.matches("2"), `is`(false))
        assertThat(XsBoolean.matches("()"), `is`(false))
    }

    fun testXsBase64Binary() {
        assertThat(XsBase64Binary.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsBase64Binary.typeName?.localName, `is`("base64Binary"))
        assertThat(XsBase64Binary.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsHexBinary() {
        assertThat(XsHexBinary.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsHexBinary.typeName?.localName, `is`("hexBinary"))
        assertThat(XsHexBinary.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsAnyURI() {
        assertThat(XsAnyURI.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsAnyURI.typeName?.localName, `is`("anyURI"))
        assertThat(XsAnyURI.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsQName() {
        assertThat(XsQName.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsQName.typeName?.localName, `is`("QName"))
        assertThat(XsQName.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsNOTATION() {
        assertThat(XsNOTATION.typeName?.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNOTATION.typeName?.localName, `is`("NOTATION"))
        assertThat(XsNOTATION.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }
}
