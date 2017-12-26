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
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XmlSchemaType

class AtomicTypes : TestCase() {
    fun testXsUntypedAtomic() {
        assertThat(XsUntypedAtomic.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUntypedAtomic.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUntypedAtomic.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUntypedAtomic.typeName?.localName?.lexicalRepresentation, `is`("untypedAtomic"))

        assertThat(XsUntypedAtomic.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDateTime() {
        assertThat(XsDateTime.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDateTime.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDateTime.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDateTime.typeName?.localName?.lexicalRepresentation, `is`("dateTime"))

        assertThat(XsDateTime.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDateTimeStamp() {
        assertThat(XsDateTimeStamp.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDateTimeStamp.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDateTimeStamp.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDateTimeStamp.typeName?.localName?.lexicalRepresentation, `is`("dateTimeStamp"))

        assertThat(XsDateTimeStamp.baseType, `is`(XsDateTime as XmlSchemaType))
    }

    fun testXsDate() {
        assertThat(XsDate.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDate.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDate.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDate.typeName?.localName?.lexicalRepresentation, `is`("date"))

        assertThat(XsDate.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsTime() {
        assertThat(XsTime.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsTime.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsTime.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsTime.typeName?.localName?.lexicalRepresentation, `is`("time"))

        assertThat(XsTime.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDuration() {
        assertThat(XsDuration.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDuration.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDuration.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDuration.typeName?.localName?.lexicalRepresentation, `is`("duration"))

        assertThat(XsDuration.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsYearMonthDuration() {
        assertThat(XsYearMonthDuration.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsYearMonthDuration.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsYearMonthDuration.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsYearMonthDuration.typeName?.localName?.lexicalRepresentation, `is`("yearMonthDuration"))

        assertThat(XsYearMonthDuration.baseType, `is`(XsDuration as XmlSchemaType))
    }

    fun testXsDayTimeDuration() {
        assertThat(XsDayTimeDuration.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDayTimeDuration.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDayTimeDuration.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDayTimeDuration.typeName?.localName?.lexicalRepresentation, `is`("dayTimeDuration"))

        assertThat(XsDayTimeDuration.baseType, `is`(XsDuration as XmlSchemaType))
    }

    fun testXsFloat() {
        assertThat(XsFloat.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsFloat.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsFloat.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsFloat.typeName?.localName?.lexicalRepresentation, `is`("float"))

        assertThat(XsFloat.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDouble() {
        assertThat(XsDouble.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDouble.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDouble.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDouble.typeName?.localName?.lexicalRepresentation, `is`("double"))

        assertThat(XsDouble.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsDecimal() {
        assertThat(XsDecimal.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDecimal.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDecimal.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDecimal.typeName?.localName?.lexicalRepresentation, `is`("decimal"))

        assertThat(XsDecimal.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsInteger() {
        assertThat(XsInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsInteger.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsInteger.typeName?.localName?.lexicalRepresentation, `is`("integer"))

        assertThat(XsInteger.baseType, `is`(XsDecimal as XmlSchemaType))
    }

    fun testXsNonPositiveInteger() {
        assertThat(XsNonPositiveInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNonPositiveInteger.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNonPositiveInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNonPositiveInteger.typeName?.localName?.lexicalRepresentation, `is`("nonPositiveInteger"))

        assertThat(XsNonPositiveInteger.baseType, `is`(XsInteger as XmlSchemaType))
    }

    fun testXsNegativeInteger() {
        assertThat(XsNegativeInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNegativeInteger.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNegativeInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNegativeInteger.typeName?.localName?.lexicalRepresentation, `is`("negativeInteger"))

        assertThat(XsNegativeInteger.baseType, `is`(XsNonPositiveInteger as XmlSchemaType))
    }

    fun testXsLong() {
        assertThat(XsLong.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsLong.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsLong.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsLong.typeName?.localName?.lexicalRepresentation, `is`("long"))

        assertThat(XsLong.baseType, `is`(XsInteger as XmlSchemaType))
    }

    fun testXsInt() {
        assertThat(XsInt.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsInt.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsInt.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsInt.typeName?.localName?.lexicalRepresentation, `is`("int"))

        assertThat(XsInt.baseType, `is`(XsLong as XmlSchemaType))
    }

    fun testXsShort() {
        assertThat(XsShort.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsShort.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsShort.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsShort.typeName?.localName?.lexicalRepresentation, `is`("short"))

        assertThat(XsShort.baseType, `is`(XsInt as XmlSchemaType))
    }

    fun testXsByte() {
        assertThat(XsByte.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsByte.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsByte.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsByte.typeName?.localName?.lexicalRepresentation, `is`("byte"))

        assertThat(XsByte.baseType, `is`(XsShort as XmlSchemaType))
    }

    fun testXsNonNegativeInteger() {
        assertThat(XsNonNegativeInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNonNegativeInteger.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNonNegativeInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNonNegativeInteger.typeName?.localName?.lexicalRepresentation, `is`("nonNegativeInteger"))

        assertThat(XsNonNegativeInteger.baseType, `is`(XsInteger as XmlSchemaType))
    }

    fun testXsUnsignedLong() {
        assertThat(XsUnsignedLong.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedLong.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedLong.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedLong.typeName?.localName?.lexicalRepresentation, `is`("unsignedLong"))

        assertThat(XsUnsignedLong.baseType, `is`(XsNonNegativeInteger as XmlSchemaType))
    }

    fun testXsUnsignedInt() {
        assertThat(XsUnsignedInt.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedInt.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedInt.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedInt.typeName?.localName?.lexicalRepresentation, `is`("unsignedInt"))

        assertThat(XsUnsignedInt.baseType, `is`(XsUnsignedLong as XmlSchemaType))
    }

    fun testXsUnsignedShort() {
        assertThat(XsUnsignedShort.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedShort.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedShort.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedShort.typeName?.localName?.lexicalRepresentation, `is`("unsignedShort"))

        assertThat(XsUnsignedShort.baseType, `is`(XsUnsignedInt as XmlSchemaType))
    }

    fun testXsUnsignedByte() {
        assertThat(XsUnsignedByte.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedByte.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedByte.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedByte.typeName?.localName?.lexicalRepresentation, `is`("unsignedByte"))

        assertThat(XsUnsignedByte.baseType, `is`(XsUnsignedShort as XmlSchemaType))
    }

    fun testXsPositiveInteger() {
        assertThat(XsPositiveInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsPositiveInteger.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsPositiveInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsPositiveInteger.typeName?.localName?.lexicalRepresentation, `is`("positiveInteger"))

        assertThat(XsPositiveInteger.baseType, `is`(XsNonNegativeInteger as XmlSchemaType))
    }

    fun testXsGYearMonth() {
        assertThat(XsGYearMonth.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGYearMonth.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGYearMonth.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGYearMonth.typeName?.localName?.lexicalRepresentation, `is`("gYearMonth"))

        assertThat(XsGYearMonth.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGYear() {
        assertThat(XsGYear.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGYear.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGYear.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGYear.typeName?.localName?.lexicalRepresentation, `is`("gYear"))

        assertThat(XsGYear.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGMonthDay() {
        assertThat(XsGMonthDay.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGMonthDay.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGMonthDay.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGMonthDay.typeName?.localName?.lexicalRepresentation, `is`("gMonthDay"))

        assertThat(XsGMonthDay.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGDay() {
        assertThat(XsGDay.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGDay.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGDay.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGDay.typeName?.localName?.lexicalRepresentation, `is`("gDay"))

        assertThat(XsGDay.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsGMonth() {
        assertThat(XsGMonth.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGMonth.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGMonth.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGMonth.typeName?.localName?.lexicalRepresentation, `is`("gMonth"))

        assertThat(XsGMonth.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsString() {
        assertThat(XsString.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsString.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsString.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsString.typeName?.localName?.lexicalRepresentation, `is`("string"))

        assertThat(XsString.baseType, `is`(XsAnyAtomicType as XmlSchemaType))

        assertThat(XsString.pattern, `is`(nullValue()))

        assertThat(XsString.matches("true"), `is`(true))
        assertThat(XsString.matches("1234"), `is`(true))
        assertThat(XsString.matches("()"), `is`(true))
        assertThat(XsString.matches("Lorem ipsum."), `is`(true))
    }

    fun testXsNormalizedString() {
        assertThat(XsNormalizedString.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNormalizedString.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNormalizedString.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNormalizedString.typeName?.localName?.lexicalRepresentation, `is`("normalizedString"))

        assertThat(XsNormalizedString.baseType, `is`(XsString as XmlSchemaType))
    }

    fun testXsToken() {
        assertThat(XsToken.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsToken.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsToken.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsToken.typeName?.localName?.lexicalRepresentation, `is`("token"))

        assertThat(XsToken.baseType, `is`(XsNormalizedString as XmlSchemaType))
    }

    fun testXsLanguage() {
        assertThat(XsLanguage.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsLanguage.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsLanguage.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsLanguage.typeName?.localName?.lexicalRepresentation, `is`("language"))

        assertThat(XsLanguage.baseType, `is`(XsToken as XmlSchemaType))
    }

    fun testXsNMTOKEN() {
        assertThat(XsNMTOKEN.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNMTOKEN.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNMTOKEN.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNMTOKEN.typeName?.localName?.lexicalRepresentation, `is`("NMTOKEN"))

        assertThat(XsNMTOKEN.baseType, `is`(XsToken as XmlSchemaType))
    }

    fun testXsName() {
        assertThat(XsName.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsName.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsName.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsName.typeName?.localName?.lexicalRepresentation, `is`("Name"))

        assertThat(XsName.baseType, `is`(XsToken as XmlSchemaType))
    }

    fun testXsNCName() {
        assertThat(XsNCName.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNCName.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNCName.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNCName.typeName?.localName?.lexicalRepresentation, `is`("NCName"))

        assertThat(XsNCName.baseType, `is`(XsName as XmlSchemaType))
    }

    fun testXsID() {
        assertThat(XsID.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsID.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsID.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsID.typeName?.localName?.lexicalRepresentation, `is`("ID"))

        assertThat(XsID.baseType, `is`(XsNCName as XmlSchemaType))
    }

    fun testXsIDREF() {
        assertThat(XsIDREF.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsIDREF.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsIDREF.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsIDREF.typeName?.localName?.lexicalRepresentation, `is`("IDREF"))

        assertThat(XsIDREF.baseType, `is`(XsNCName as XmlSchemaType))
    }

    fun testXsENTITY() {
        assertThat(XsENTITY.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsENTITY.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsENTITY.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsENTITY.typeName?.localName?.lexicalRepresentation, `is`("ENTITY"))

        assertThat(XsENTITY.baseType, `is`(XsNCName as XmlSchemaType))
    }

    fun testXsBoolean() {
        assertThat(XsBoolean.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsBoolean.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsBoolean.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsBoolean.typeName?.localName?.lexicalRepresentation, `is`("boolean"))

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
        assertThat(XsBase64Binary.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsBase64Binary.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsBase64Binary.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsBase64Binary.typeName?.localName?.lexicalRepresentation, `is`("base64Binary"))

        assertThat(XsBase64Binary.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsHexBinary() {
        assertThat(XsHexBinary.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsHexBinary.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsHexBinary.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsHexBinary.typeName?.localName?.lexicalRepresentation, `is`("hexBinary"))

        assertThat(XsHexBinary.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsAnyURI() {
        assertThat(XsAnyURI.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnyURI.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyURI.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnyURI.typeName?.localName?.lexicalRepresentation, `is`("anyURI"))

        assertThat(XsAnyURI.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsQName() {
        assertThat(XsQName.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsQName.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsQName.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsQName.typeName?.localName?.lexicalRepresentation, `is`("QName"))

        assertThat(XsQName.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }

    fun testXsNOTATION() {
        assertThat(XsNOTATION.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNOTATION.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNOTATION.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNOTATION.typeName?.localName?.lexicalRepresentation, `is`("NOTATION"))

        assertThat(XsNOTATION.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
    }
}
