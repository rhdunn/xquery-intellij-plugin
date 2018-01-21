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
import org.apache.xerces.xs.datatypes.XSDecimal
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XmlSchemaType

class AtomicTypes : TestCase() {
    fun testXsUntypedAtomic() {
        assertThat(XsUntypedAtomic.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUntypedAtomic.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUntypedAtomic.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsUntypedAtomic.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUntypedAtomic.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUntypedAtomic.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUntypedAtomic.typeName?.localName?.staticValue as String, `is`("untypedAtomic"))

        assertThat(XsUntypedAtomic.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsUntypedAtomic.isPrimitive, `is`(true)) // XDM casting rules

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "untypedAtomic").toXmlSchemaType(),
                `is`(XsUntypedAtomic as XdmSequenceType))
    }

    fun testXsDateTime() {
        assertThat(XsDateTime.typeName?.declaration, `is`(nullValue()))

        assertThat(XsDateTime.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDateTime.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsDateTime.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDateTime.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDateTime.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDateTime.typeName?.localName?.staticValue as String, `is`("dateTime"))

        assertThat(XsDateTime.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsDateTime.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "dateTime").toXmlSchemaType(),
                `is`(XsDateTime as XdmSequenceType))
    }

    fun testXsDateTimeStamp() {
        assertThat(XsDateTimeStamp.typeName?.declaration, `is`(nullValue()))

        assertThat(XsDateTimeStamp.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDateTimeStamp.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsDateTimeStamp.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDateTimeStamp.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDateTimeStamp.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDateTimeStamp.typeName?.localName?.staticValue as String, `is`("dateTimeStamp"))

        assertThat(XsDateTimeStamp.baseType, `is`(XsDateTime as XmlSchemaType))
        assertThat(XsDateTimeStamp.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "dateTimeStamp").toXmlSchemaType(),
                `is`(XsDateTimeStamp as XdmSequenceType))
    }

    fun testXsDate() {
        assertThat(XsDate.typeName?.declaration, `is`(nullValue()))

        assertThat(XsDate.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDate.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsDate.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDate.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDate.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDate.typeName?.localName?.staticValue as String, `is`("date"))

        assertThat(XsDate.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsDate.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "date").toXmlSchemaType(),
                `is`(XsDate as XdmSequenceType))
    }

    fun testXsTime() {
        assertThat(XsTime.typeName?.declaration, `is`(nullValue()))

        assertThat(XsTime.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsTime.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsTime.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsTime.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsTime.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsTime.typeName?.localName?.staticValue as String, `is`("time"))

        assertThat(XsTime.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsTime.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "time").toXmlSchemaType(),
                `is`(XsTime as XdmSequenceType))
    }

    fun testXsDuration() {
        assertThat(XsDuration.typeName?.declaration, `is`(nullValue()))

        assertThat(XsDuration.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDuration.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsDuration.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDuration.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDuration.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDuration.typeName?.localName?.staticValue as String, `is`("duration"))

        assertThat(XsDuration.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsDuration.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "duration").toXmlSchemaType(),
                `is`(XsDuration as XdmSequenceType))
    }

    fun testXsYearMonthDuration() {
        assertThat(XsYearMonthDuration.typeName?.declaration, `is`(nullValue()))

        assertThat(XsYearMonthDuration.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsYearMonthDuration.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsYearMonthDuration.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsYearMonthDuration.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsYearMonthDuration.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsYearMonthDuration.typeName?.localName?.staticValue as String, `is`("yearMonthDuration"))

        assertThat(XsYearMonthDuration.baseType, `is`(XsDuration as XmlSchemaType))
        assertThat(XsYearMonthDuration.isPrimitive, `is`(true)) // XDM casting rules

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "yearMonthDuration").toXmlSchemaType(),
                `is`(XsYearMonthDuration as XdmSequenceType))
    }

    fun testXsDayTimeDuration() {
        assertThat(XsDayTimeDuration.typeName?.declaration, `is`(nullValue()))

        assertThat(XsDayTimeDuration.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDayTimeDuration.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsDayTimeDuration.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDayTimeDuration.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDayTimeDuration.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDayTimeDuration.typeName?.localName?.staticValue as String, `is`("dayTimeDuration"))

        assertThat(XsDayTimeDuration.baseType, `is`(XsDuration as XmlSchemaType))
        assertThat(XsDayTimeDuration.isPrimitive, `is`(true)) // XDM casting rules

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "dayTimeDuration").toXmlSchemaType(),
                `is`(XsDayTimeDuration as XdmSequenceType))
    }

    fun testXsFloat() {
        assertThat(XsFloat.typeName?.declaration, `is`(nullValue()))

        assertThat(XsFloat.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsFloat.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsFloat.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsFloat.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsFloat.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsFloat.typeName?.localName?.staticValue as String, `is`("float"))

        assertThat(XsFloat.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsFloat.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "float").toXmlSchemaType(),
                `is`(XsFloat as XdmSequenceType))
    }

    fun testXsDouble() {
        assertThat(XsDouble.typeName?.declaration, `is`(nullValue()))

        assertThat(XsDouble.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDouble.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsDouble.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDouble.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDouble.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDouble.typeName?.localName?.staticValue as String, `is`("double"))

        assertThat(XsDouble.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsDouble.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "double").toXmlSchemaType(),
                `is`(XsDouble as XdmSequenceType))
    }

    fun testXsDecimal() {
        assertThat(XsDecimal.typeName?.declaration, `is`(nullValue()))

        assertThat(XsDecimal.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDecimal.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsDecimal.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsDecimal.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsDecimal.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsDecimal.typeName?.localName?.staticValue as String, `is`("decimal"))

        assertThat(XsDecimal.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsDecimal.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "decimal").toXmlSchemaType(),
                `is`(XsDecimal as XdmSequenceType))
    }

    fun testXsInteger() {
        assertThat(XsInteger.typeName?.declaration, `is`(nullValue()))

        assertThat(XsInteger.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsInteger.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsInteger.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsInteger.typeName?.localName?.staticValue as String, `is`("integer"))

        assertThat(XsInteger.baseType, `is`(XsDecimal as XmlSchemaType))
        assertThat(XsInteger.isPrimitive, `is`(true)) // XDM casting rules

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "integer").toXmlSchemaType(),
                `is`(XsInteger as XdmSequenceType))
    }

    fun testXsNonPositiveInteger() {
        assertThat(XsNonPositiveInteger.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNonPositiveInteger.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNonPositiveInteger.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNonPositiveInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNonPositiveInteger.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNonPositiveInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNonPositiveInteger.typeName?.localName?.staticValue as String, `is`("nonPositiveInteger"))

        assertThat(XsNonPositiveInteger.baseType, `is`(XsInteger as XmlSchemaType))
        assertThat(XsNonPositiveInteger.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "nonPositiveInteger").toXmlSchemaType(),
                `is`(XsNonPositiveInteger as XdmSequenceType))
    }

    fun testXsNegativeInteger() {
        assertThat(XsNegativeInteger.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNegativeInteger.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNegativeInteger.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNegativeInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNegativeInteger.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNegativeInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNegativeInteger.typeName?.localName?.staticValue as String, `is`("negativeInteger"))

        assertThat(XsNegativeInteger.baseType, `is`(XsNonPositiveInteger as XmlSchemaType))
        assertThat(XsNegativeInteger.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "negativeInteger").toXmlSchemaType(),
                `is`(XsNegativeInteger as XdmSequenceType))
    }

    fun testXsLong() {
        assertThat(XsLong.typeName?.declaration, `is`(nullValue()))

        assertThat(XsLong.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsLong.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsLong.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsLong.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsLong.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsLong.typeName?.localName?.staticValue as String, `is`("long"))

        assertThat(XsLong.baseType, `is`(XsInteger as XmlSchemaType))
        assertThat(XsLong.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "long").toXmlSchemaType(),
                `is`(XsLong as XdmSequenceType))
    }

    fun testXsInt() {
        assertThat(XsInt.typeName?.declaration, `is`(nullValue()))

        assertThat(XsInt.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsInt.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsInt.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsInt.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsInt.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsInt.typeName?.localName?.staticValue as String, `is`("int"))

        assertThat(XsInt.baseType, `is`(XsLong as XmlSchemaType))
        assertThat(XsInt.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "int").toXmlSchemaType(),
                `is`(XsInt as XdmSequenceType))
    }

    fun testXsShort() {
        assertThat(XsShort.typeName?.declaration, `is`(nullValue()))

        assertThat(XsShort.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsShort.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsShort.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsShort.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsShort.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsShort.typeName?.localName?.staticValue as String, `is`("short"))

        assertThat(XsShort.baseType, `is`(XsInt as XmlSchemaType))
        assertThat(XsShort.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "short").toXmlSchemaType(),
                `is`(XsShort as XdmSequenceType))
    }

    fun testXsByte() {
        assertThat(XsByte.typeName?.declaration, `is`(nullValue()))

        assertThat(XsByte.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsByte.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsByte.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsByte.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsByte.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsByte.typeName?.localName?.staticValue as String, `is`("byte"))

        assertThat(XsByte.baseType, `is`(XsShort as XmlSchemaType))
        assertThat(XsByte.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "byte").toXmlSchemaType(),
                `is`(XsByte as XdmSequenceType))
    }

    fun testXsNonNegativeInteger() {
        assertThat(XsNonNegativeInteger.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNonNegativeInteger.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNonNegativeInteger.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNonNegativeInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNonNegativeInteger.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNonNegativeInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNonNegativeInteger.typeName?.localName?.staticValue as String, `is`("nonNegativeInteger"))

        assertThat(XsNonNegativeInteger.baseType, `is`(XsInteger as XmlSchemaType))
        assertThat(XsNonNegativeInteger.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger").toXmlSchemaType(),
                `is`(XsNonNegativeInteger as XdmSequenceType))
    }

    fun testXsUnsignedLong() {
        assertThat(XsUnsignedLong.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUnsignedLong.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedLong.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsUnsignedLong.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedLong.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedLong.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedLong.typeName?.localName?.staticValue as String, `is`("unsignedLong"))

        assertThat(XsUnsignedLong.baseType, `is`(XsNonNegativeInteger as XmlSchemaType))
        assertThat(XsUnsignedLong.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "unsignedLong").toXmlSchemaType(),
                `is`(XsUnsignedLong as XdmSequenceType))
    }

    fun testXsUnsignedInt() {
        assertThat(XsUnsignedInt.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUnsignedInt.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedInt.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsUnsignedInt.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedInt.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedInt.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedInt.typeName?.localName?.staticValue as String, `is`("unsignedInt"))

        assertThat(XsUnsignedInt.baseType, `is`(XsUnsignedLong as XmlSchemaType))
        assertThat(XsUnsignedInt.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "unsignedInt").toXmlSchemaType(),
                `is`(XsUnsignedInt as XdmSequenceType))
    }

    fun testXsUnsignedShort() {
        assertThat(XsUnsignedShort.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUnsignedShort.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedShort.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsUnsignedShort.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedShort.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedShort.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedShort.typeName?.localName?.staticValue as String, `is`("unsignedShort"))

        assertThat(XsUnsignedShort.baseType, `is`(XsUnsignedInt as XmlSchemaType))
        assertThat(XsUnsignedShort.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "unsignedShort").toXmlSchemaType(),
                `is`(XsUnsignedShort as XdmSequenceType))
    }

    fun testXsUnsignedByte() {
        assertThat(XsUnsignedByte.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUnsignedByte.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedByte.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsUnsignedByte.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUnsignedByte.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUnsignedByte.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUnsignedByte.typeName?.localName?.staticValue as String, `is`("unsignedByte"))

        assertThat(XsUnsignedByte.baseType, `is`(XsUnsignedShort as XmlSchemaType))
        assertThat(XsUnsignedByte.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "unsignedByte").toXmlSchemaType(),
                `is`(XsUnsignedByte as XdmSequenceType))
    }

    fun testXsPositiveInteger() {
        assertThat(XsPositiveInteger.typeName?.declaration, `is`(nullValue()))

        assertThat(XsPositiveInteger.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsPositiveInteger.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsPositiveInteger.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsPositiveInteger.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsPositiveInteger.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsPositiveInteger.typeName?.localName?.staticValue as String, `is`("positiveInteger"))

        assertThat(XsPositiveInteger.baseType, `is`(XsNonNegativeInteger as XmlSchemaType))
        assertThat(XsPositiveInteger.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "positiveInteger").toXmlSchemaType(),
                `is`(XsPositiveInteger as XdmSequenceType))
    }

    fun testXsGYearMonth() {
        assertThat(XsGYearMonth.typeName?.declaration, `is`(nullValue()))

        assertThat(XsGYearMonth.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGYearMonth.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsGYearMonth.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGYearMonth.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGYearMonth.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGYearMonth.typeName?.localName?.staticValue as String, `is`("gYearMonth"))

        assertThat(XsGYearMonth.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsGYearMonth.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "gYearMonth").toXmlSchemaType(),
                `is`(XsGYearMonth as XdmSequenceType))
    }

    fun testXsGYear() {
        assertThat(XsGYear.typeName?.declaration, `is`(nullValue()))

        assertThat(XsGYear.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGYear.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsGYear.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGYear.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGYear.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGYear.typeName?.localName?.staticValue as String, `is`("gYear"))

        assertThat(XsGYear.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsGYear.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "gYear").toXmlSchemaType(),
                `is`(XsGYear as XdmSequenceType))
    }

    fun testXsGMonthDay() {
        assertThat(XsGMonthDay.typeName?.declaration, `is`(nullValue()))

        assertThat(XsGMonthDay.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGMonthDay.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsGMonthDay.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGMonthDay.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGMonthDay.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGMonthDay.typeName?.localName?.staticValue as String, `is`("gMonthDay"))

        assertThat(XsGMonthDay.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsGMonthDay.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "gMonthDay").toXmlSchemaType(),
                `is`(XsGMonthDay as XdmSequenceType))
    }

    fun testXsGDay() {
        assertThat(XsGDay.typeName?.declaration, `is`(nullValue()))

        assertThat(XsGDay.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGDay.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsGDay.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGDay.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGDay.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGDay.typeName?.localName?.staticValue as String, `is`("gDay"))

        assertThat(XsGDay.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsGDay.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "gDay").toXmlSchemaType(),
                `is`(XsGDay as XdmSequenceType))
    }

    fun testXsGMonth() {
        assertThat(XsGMonth.typeName?.declaration, `is`(nullValue()))

        assertThat(XsGMonth.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGMonth.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsGMonth.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsGMonth.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsGMonth.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsGMonth.typeName?.localName?.staticValue as String, `is`("gMonth"))

        assertThat(XsGMonth.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsGMonth.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "gMonth").toXmlSchemaType(),
                `is`(XsGMonth as XdmSequenceType))
    }

    fun testXsString() {
        assertThat(XsString.typeName?.declaration, `is`(nullValue()))

        assertThat(XsString.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsString.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsString.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsString.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsString.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsString.typeName?.localName?.staticValue as String, `is`("string"))

        assertThat(XsString.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsString.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "string").toXmlSchemaType(),
                `is`(XsString as XdmSequenceType))
    }

    fun testXsNormalizedString() {
        assertThat(XsNormalizedString.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNormalizedString.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNormalizedString.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNormalizedString.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNormalizedString.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNormalizedString.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNormalizedString.typeName?.localName?.staticValue as String, `is`("normalizedString"))

        assertThat(XsNormalizedString.baseType, `is`(XsString as XmlSchemaType))
        assertThat(XsNormalizedString.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "normalizedString").toXmlSchemaType(),
                `is`(XsNormalizedString as XdmSequenceType))
    }

    fun testXsToken() {
        assertThat(XsToken.typeName?.declaration, `is`(nullValue()))

        assertThat(XsToken.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsToken.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsToken.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsToken.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsToken.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsToken.typeName?.localName?.staticValue as String, `is`("token"))

        assertThat(XsToken.baseType, `is`(XsNormalizedString as XmlSchemaType))
        assertThat(XsToken.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "token").toXmlSchemaType(),
                `is`(XsToken as XdmSequenceType))
    }

    fun testXsLanguage() {
        assertThat(XsLanguage.typeName?.declaration, `is`(nullValue()))

        assertThat(XsLanguage.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsLanguage.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsLanguage.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsLanguage.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsLanguage.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsLanguage.typeName?.localName?.staticValue as String, `is`("language"))

        assertThat(XsLanguage.baseType, `is`(XsToken as XmlSchemaType))
        assertThat(XsLanguage.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "language").toXmlSchemaType(),
                `is`(XsLanguage as XdmSequenceType))
    }

    fun testXsNMTOKEN() {
        assertThat(XsNMTOKEN.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNMTOKEN.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNMTOKEN.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNMTOKEN.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNMTOKEN.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNMTOKEN.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNMTOKEN.typeName?.localName?.staticValue as String, `is`("NMTOKEN"))

        assertThat(XsNMTOKEN.baseType, `is`(XsToken as XmlSchemaType))
        assertThat(XsNMTOKEN.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "NMTOKEN").toXmlSchemaType(),
                `is`(XsNMTOKEN as XdmSequenceType))
    }

    fun testXsName() {
        assertThat(XsName.typeName?.declaration, `is`(nullValue()))

        assertThat(XsName.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsName.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsName.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsName.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsName.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsName.typeName?.localName?.staticValue as String, `is`("Name"))

        assertThat(XsName.baseType, `is`(XsToken as XmlSchemaType))
        assertThat(XsName.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "Name").toXmlSchemaType(),
                `is`(XsName as XdmSequenceType))
    }

    fun testXsNCName() {
        assertThat(XsNCName.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNCName.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNCName.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNCName.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNCName.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNCName.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNCName.typeName?.localName?.staticValue as String, `is`("NCName"))

        assertThat(XsNCName.baseType, `is`(XsName as XmlSchemaType))
        assertThat(XsNCName.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "NCName").toXmlSchemaType(),
                `is`(XsNCName as XdmSequenceType))
    }

    fun testXsID() {
        assertThat(XsID.typeName?.declaration, `is`(nullValue()))

        assertThat(XsID.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsID.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsID.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsID.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsID.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsID.typeName?.localName?.staticValue as String, `is`("ID"))

        assertThat(XsID.baseType, `is`(XsNCName as XmlSchemaType))
        assertThat(XsID.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "ID").toXmlSchemaType(),
                `is`(XsID as XdmSequenceType))
    }

    fun testXsIDREF() {
        assertThat(XsIDREF.typeName?.declaration, `is`(nullValue()))

        assertThat(XsIDREF.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsIDREF.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsIDREF.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsIDREF.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsIDREF.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsIDREF.typeName?.localName?.staticValue as String, `is`("IDREF"))

        assertThat(XsIDREF.baseType, `is`(XsNCName as XmlSchemaType))
        assertThat(XsIDREF.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "IDREF").toXmlSchemaType(),
                `is`(XsIDREF as XdmSequenceType))
    }

    fun testXsENTITY() {
        assertThat(XsENTITY.typeName?.declaration, `is`(nullValue()))

        assertThat(XsENTITY.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsENTITY.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsENTITY.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsENTITY.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsENTITY.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsENTITY.typeName?.localName?.staticValue as String, `is`("ENTITY"))

        assertThat(XsENTITY.baseType, `is`(XsNCName as XmlSchemaType))
        assertThat(XsENTITY.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "ENTITY").toXmlSchemaType(),
                `is`(XsENTITY as XdmSequenceType))
    }

    fun testXsBoolean() {
        assertThat(XsBoolean.typeName?.declaration, `is`(nullValue()))

        assertThat(XsBoolean.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsBoolean.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsBoolean.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsBoolean.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsBoolean.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsBoolean.typeName?.localName?.staticValue as String, `is`("boolean"))

        assertThat(XsBoolean.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsBoolean.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "boolean").toXmlSchemaType(),
                `is`(XsBoolean as XdmSequenceType))
    }

    fun testXsBase64Binary() {
        assertThat(XsBase64Binary.typeName?.declaration, `is`(nullValue()))

        assertThat(XsBase64Binary.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsBase64Binary.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsBase64Binary.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsBase64Binary.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsBase64Binary.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsBase64Binary.typeName?.localName?.staticValue as String, `is`("base64Binary"))

        assertThat(XsBase64Binary.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsBase64Binary.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "base64Binary").toXmlSchemaType(),
                `is`(XsBase64Binary as XdmSequenceType))
    }

    fun testXsHexBinary() {
        assertThat(XsHexBinary.typeName?.declaration, `is`(nullValue()))

        assertThat(XsHexBinary.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsHexBinary.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsHexBinary.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsHexBinary.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsHexBinary.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsHexBinary.typeName?.localName?.staticValue as String, `is`("hexBinary"))

        assertThat(XsHexBinary.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsHexBinary.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "hexBinary").toXmlSchemaType(),
                `is`(XsHexBinary as XdmSequenceType))
    }

    fun testXsAnyURI() {
        assertThat(XsAnyURI.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnyURI.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnyURI.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsAnyURI.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnyURI.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyURI.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnyURI.typeName?.localName?.staticValue as String, `is`("anyURI"))

        assertThat(XsAnyURI.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsAnyURI.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "anyURI").toXmlSchemaType(),
                `is`(XsAnyURI as XdmSequenceType))
    }

    fun testXsQName() {
        assertThat(XsQName.typeName?.declaration, `is`(nullValue()))

        assertThat(XsQName.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsQName.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsQName.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsQName.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsQName.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsQName.typeName?.localName?.staticValue as String, `is`("QName"))

        assertThat(XsQName.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsQName.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "QName").toXmlSchemaType(),
                `is`(XsQName as XdmSequenceType))
    }

    fun testXsNOTATION() {
        assertThat(XsNOTATION.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNOTATION.typeName?.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNOTATION.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNOTATION.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNOTATION.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNOTATION.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNOTATION.typeName?.localName?.staticValue as String, `is`("NOTATION"))

        assertThat(XsNOTATION.baseType, `is`(XsAnyAtomicType as XmlSchemaType))
        assertThat(XsNOTATION.isPrimitive, `is`(true)) // XMLSchema definition

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "NOTATION").toXmlSchemaType(),
                `is`(XsNOTATION as XdmSequenceType))
    }
}
