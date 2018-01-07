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
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSimpleType
import uk.co.reecedunn.intellij.plugin.xdm.model.XmlSchemaType

class SimpleAndComplexTypes : TestCase() {
    fun testXsAnyType() {
        assertThat(XsAnyType.typeName?.prefix, `is`(nullValue()))
        assertThat(XsAnyType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnyType.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnyType.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyType.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnyType.typeName?.localName?.staticValue as String, `is`("anyType"))

        assertThat(XsAnyType.baseType, `is`(nullValue()))
        assertThat(XsAnyType.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "anyType").toXmlSchemaType(),
                `is`(XsAnyType as XdmSequenceType))
    }

    fun testXsUntyped() {
        assertThat(XsUntyped.typeName?.prefix, `is`(nullValue()))
        assertThat(XsUntyped.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUntyped.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUntyped.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUntyped.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUntyped.typeName?.localName?.staticValue as String, `is`("untyped"))

        assertThat(XsUntyped.baseType, `is`(XsAnyType))
        assertThat(XsUntyped.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "untyped").toXmlSchemaType(),
                `is`(XsUntyped as XdmSequenceType))
    }

    fun testXsAnySimpleType() {
        assertThat(XsAnySimpleType.typeName?.prefix, `is`(nullValue()))
        assertThat(XsAnySimpleType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnySimpleType.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnySimpleType.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnySimpleType.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnySimpleType.typeName?.localName?.staticValue as String, `is`("anySimpleType"))

        assertThat(XsAnySimpleType.baseType, `is`(XsAnyType))
        assertThat(XsAnySimpleType.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "anySimpleType").toXmlSchemaType(),
                `is`(XsAnySimpleType as XdmSequenceType))
    }

    fun testXsAnyAtomicType() {
        assertThat(XsAnyAtomicType.typeName?.prefix, `is`(nullValue()))
        assertThat(XsAnyAtomicType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnyAtomicType.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnyAtomicType.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyAtomicType.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnyAtomicType.typeName?.localName?.staticValue as String, `is`("anyAtomicType"))

        assertThat(XsAnyAtomicType.baseType, `is`(XsAnySimpleType as XmlSchemaType))
        assertThat(XsAnyAtomicType.isPrimitive, `is`(false))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "anyAtomicType").toXmlSchemaType(),
                `is`(XsAnyAtomicType as XdmSequenceType))
    }

    fun testXsNumeric() {
        assertThat(XsNumeric.typeName?.prefix, `is`(nullValue()))
        assertThat(XsNumeric.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNumeric.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNumeric.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNumeric.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNumeric.typeName?.localName?.staticValue as String, `is`("numeric"))

        assertThat(XsNumeric.baseType, `is`(XsAnySimpleType as XmlSchemaType))
        assertThat(XsNumeric.isPrimitive, `is`(false))

        assertThat(XsNumeric.unionOf.size, `is`(3))
        assertThat(XsNumeric.unionOf[0], `is`(XsDouble as XdmSimpleType))
        assertThat(XsNumeric.unionOf[1], `is`(XsFloat as XdmSimpleType))
        assertThat(XsNumeric.unionOf[2], `is`(XsDecimal as XdmSimpleType))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "numeric").toXmlSchemaType(),
                `is`(XsNumeric as XdmSequenceType))
    }

    fun testXsIDREFS() {
        assertThat(XsIDREFS.typeName?.prefix, `is`(nullValue()))
        assertThat(XsIDREFS.typeName?.declaration, `is`(nullValue()))

        assertThat(XsIDREFS.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsIDREFS.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsIDREFS.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsIDREFS.typeName?.localName?.staticValue as String, `is`("IDREFS"))

        assertThat(XsIDREFS.baseType, `is`(XsAnySimpleType as XmlSchemaType))
        assertThat(XsIDREFS.isPrimitive, `is`(false))

        assertThat(XsIDREFS.itemType, `is`(XsIDREF as XdmSequenceType))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "IDREFS").toXmlSchemaType(),
                `is`(XsIDREFS as XdmSequenceType))
    }

    fun testXsNMTOKENS() {
        assertThat(XsNMTOKENS.typeName?.prefix, `is`(nullValue()))
        assertThat(XsNMTOKENS.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNMTOKENS.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNMTOKENS.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNMTOKENS.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNMTOKENS.typeName?.localName?.staticValue as String, `is`("NMTOKENS"))

        assertThat(XsNMTOKENS.baseType, `is`(XsAnySimpleType as XmlSchemaType))
        assertThat(XsNMTOKENS.isPrimitive, `is`(false))

        assertThat(XsNMTOKENS.itemType, `is`(XsNMTOKEN as XdmSequenceType))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "NMTOKENS").toXmlSchemaType(),
                `is`(XsNMTOKENS as XdmSequenceType))
    }

    fun testXsENTITIES() {
        assertThat(XsENTITIES.typeName?.prefix, `is`(nullValue()))
        assertThat(XsENTITIES.typeName?.declaration, `is`(nullValue()))

        assertThat(XsENTITIES.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsENTITIES.typeName?.namespace?.staticValue as String, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsENTITIES.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsENTITIES.typeName?.localName?.staticValue as String, `is`("ENTITIES"))

        assertThat(XsENTITIES.baseType, `is`(XsAnySimpleType as XmlSchemaType))
        assertThat(XsENTITIES.isPrimitive, `is`(false))

        assertThat(XsENTITIES.itemType, `is`(XsENTITY as XdmSequenceType))

        assertThat(createQName("http://www.w3.org/2001/XMLSchema", "ENTITIES").toXmlSchemaType(),
                `is`(XsENTITIES as XdmSequenceType))
    }
}
