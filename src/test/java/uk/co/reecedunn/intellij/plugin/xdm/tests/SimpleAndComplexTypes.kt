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
package uk.co.reecedunn.intellij.plugin.xdm.tests

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*

class SimpleAndComplexTypes {
    @Test
    fun testXsAnyType() {
        assertThat(XsAnyType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnyType.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsAnyType.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsAnyType.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsAnyType.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyType.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsAnyType.typeName?.localName?.staticValue as String, `is`("anyType"))

        assertThat(XsAnyType.baseType, `is`(nullValue()))
        assertThat(XsAnyType.isPrimitive, `is`(false))

        assertThat(XsAnyType.toString(), `is`("xs:anyType"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "anyType").toXmlSchemaType(),
                `is`(XsAnyType))
    }

    @Test
    fun testXsUntyped() {
        assertThat(XsUntyped.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUntyped.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsUntyped.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsUntyped.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsUntyped.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUntyped.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsUntyped.typeName?.localName?.staticValue as String, `is`("untyped"))

        assertThat(XsUntyped.baseType, `is`(XsAnyType))
        assertThat(XsUntyped.isPrimitive, `is`(false))

        assertThat(XsUntyped.toString(), `is`("xs:untyped"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "untyped").toXmlSchemaType(),
                `is`(XsUntyped))
    }

    @Test
    fun testXsAnySimpleType() {
        assertThat(XsAnySimpleType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnySimpleType.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsAnySimpleType.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsAnySimpleType.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsAnySimpleType.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnySimpleType.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsAnySimpleType.typeName?.localName?.staticValue as String, `is`("anySimpleType"))

        assertThat(XsAnySimpleType.baseType, `is`(XsAnyType))
        assertThat(XsAnySimpleType.isPrimitive, `is`(false))

        assertThat(XsAnySimpleType.toString(), `is`("xs:anySimpleType"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "anySimpleType").toXmlSchemaType(),
                `is`(XsAnySimpleType))
    }

    @Test
    fun testXsAnyAtomicType() {
        assertThat(XsAnyAtomicType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnyAtomicType.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsAnyAtomicType.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsAnyAtomicType.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsAnyAtomicType.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyAtomicType.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsAnyAtomicType.typeName?.localName?.staticValue as String, `is`("anyAtomicType"))

        assertThat(XsAnyAtomicType.baseType, `is`(XsAnySimpleType))
        assertThat(XsAnyAtomicType.isPrimitive, `is`(false))

        assertThat(XsAnyAtomicType.toString(), `is`("xs:anyAtomicType"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "anyAtomicType").toXmlSchemaType(),
                `is`(XsAnyAtomicType))
    }

    @Test
    fun testXsNumeric() {
        assertThat(XsNumeric.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNumeric.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsNumeric.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNumeric.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsNumeric.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNumeric.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsNumeric.typeName?.localName?.staticValue as String, `is`("numeric"))

        assertThat(XsNumeric.baseType, `is`(XsAnySimpleType))
        assertThat(XsNumeric.isPrimitive, `is`(false))

        assertThat(XsNumeric.unionOf.size, `is`(3))
        assertThat(XsNumeric.unionOf[0], `is`(XsDouble))
        assertThat(XsNumeric.unionOf[1], `is`(XsFloat))
        assertThat(XsNumeric.unionOf[2], `is`(XsDecimal))

        assertThat(XsNumeric.toString(), `is`("xs:numeric"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "numeric").toXmlSchemaType(),
                `is`(XsNumeric))
    }

    @Test
    fun testXsIDREFS() {
        assertThat(XsIDREFS.typeName?.declaration, `is`(nullValue()))

        assertThat(XsIDREFS.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsIDREFS.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsIDREFS.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsIDREFS.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsIDREFS.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsIDREFS.typeName?.localName?.staticValue as String, `is`("IDREFS"))

        assertThat(XsIDREFS.baseType, `is`(XsAnySimpleType))
        assertThat(XsIDREFS.isPrimitive, `is`(false))

        assertThat(XsIDREFS.itemType, `is`(XsIDREF))

        assertThat(XsIDREFS.toString(), `is`("xs:IDREFS"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "IDREFS").toXmlSchemaType(),
                `is`(XsIDREFS))
    }

    @Test
    fun testXsNMTOKENS() {
        assertThat(XsNMTOKENS.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNMTOKENS.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsNMTOKENS.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsNMTOKENS.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsNMTOKENS.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNMTOKENS.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsNMTOKENS.typeName?.localName?.staticValue as String, `is`("NMTOKENS"))

        assertThat(XsNMTOKENS.baseType, `is`(XsAnySimpleType))
        assertThat(XsNMTOKENS.isPrimitive, `is`(false))

        assertThat(XsNMTOKENS.itemType, `is`(XsNMTOKEN))

        assertThat(XsNMTOKENS.toString(), `is`("xs:NMTOKENS"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "NMTOKENS").toXmlSchemaType(),
                `is`(XsNMTOKENS))
    }

    @Test
    fun testXsENTITIES() {
        assertThat(XsENTITIES.typeName?.declaration, `is`(nullValue()))

        assertThat(XsENTITIES.typeName?.prefix?.staticType, `is`(XsNCName))
        assertThat(XsENTITIES.typeName?.prefix?.staticValue as String, `is`("xs"))

        assertThat(XsENTITIES.typeName?.namespace?.staticType, `is`(XsAnyURI))
        assertThat(XsENTITIES.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsENTITIES.typeName?.localName?.staticType, `is`(XsNCName))
        assertThat(XsENTITIES.typeName?.localName?.staticValue as String, `is`("ENTITIES"))

        assertThat(XsENTITIES.baseType, `is`(XsAnySimpleType))
        assertThat(XsENTITIES.isPrimitive, `is`(false))

        assertThat(XsENTITIES.itemType, `is`(XsENTITY))

        assertThat(XsENTITIES.toString(), `is`("xs:ENTITIES"))
        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "ENTITIES").toXmlSchemaType(),
                `is`(XsENTITIES))
    }
}
