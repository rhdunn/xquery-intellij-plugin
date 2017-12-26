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
        assertThat(XsAnyType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnyType.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnyType.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyType.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnyType.typeName?.localName?.lexicalRepresentation, `is`("anyType"))

        assertThat(XsAnyType.baseType, `is`(nullValue()))
    }

    fun testXsUntyped() {
        assertThat(XsUntyped.typeName?.declaration, `is`(nullValue()))

        assertThat(XsUntyped.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsUntyped.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsUntyped.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsUntyped.typeName?.localName?.lexicalRepresentation, `is`("untyped"))

        assertThat(XsUntyped.baseType, `is`(XsAnyType))
    }

    fun testXsAnySimpleType() {
        assertThat(XsAnySimpleType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnySimpleType.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnySimpleType.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnySimpleType.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnySimpleType.typeName?.localName?.lexicalRepresentation, `is`("anySimpleType"))

        assertThat(XsAnySimpleType.baseType, `is`(XsAnyType))
    }

    fun testXsAnyAtomicType() {
        assertThat(XsAnyAtomicType.typeName?.declaration, `is`(nullValue()))

        assertThat(XsAnyAtomicType.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsAnyAtomicType.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsAnyAtomicType.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsAnyAtomicType.typeName?.localName?.lexicalRepresentation, `is`("anyAtomicType"))

        assertThat(XsAnyAtomicType.baseType, `is`(XsAnySimpleType as XmlSchemaType))
    }

    fun testXsNumeric() {
        assertThat(XsNumeric.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNumeric.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNumeric.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNumeric.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNumeric.typeName?.localName?.lexicalRepresentation, `is`("numeric"))

        assertThat(XsNumeric.baseType, `is`(XsAnySimpleType as XmlSchemaType))

        assertThat(XsNumeric.unionOf.size, `is`(3))
        assertThat(XsNumeric.unionOf[0], `is`(XsDouble as XdmSimpleType))
        assertThat(XsNumeric.unionOf[1], `is`(XsFloat as XdmSimpleType))
        assertThat(XsNumeric.unionOf[2], `is`(XsDecimal as XdmSimpleType))
    }

    fun testXsIDREFS() {
        assertThat(XsIDREFS.typeName?.declaration, `is`(nullValue()))

        assertThat(XsIDREFS.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsIDREFS.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsIDREFS.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsIDREFS.typeName?.localName?.lexicalRepresentation, `is`("IDREFS"))

        assertThat(XsIDREFS.baseType, `is`(XsAnySimpleType as XmlSchemaType))

        assertThat(XsIDREFS.itemType, `is`(XsIDREF as XdmSequenceType))
    }

    fun testXsNMTOKENS() {
        assertThat(XsNMTOKENS.typeName?.declaration, `is`(nullValue()))

        assertThat(XsNMTOKENS.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsNMTOKENS.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsNMTOKENS.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsNMTOKENS.typeName?.localName?.lexicalRepresentation, `is`("NMTOKENS"))

        assertThat(XsNMTOKENS.baseType, `is`(XsAnySimpleType as XmlSchemaType))

        assertThat(XsNMTOKENS.itemType, `is`(XsNMTOKEN as XdmSequenceType))
    }

    fun testXsENTITIES() {
        assertThat(XsENTITIES.typeName?.declaration, `is`(nullValue()))

        assertThat(XsENTITIES.typeName?.namespace?.staticType, `is`(XsAnyURI as XdmSequenceType))
        assertThat(XsENTITIES.typeName?.namespace?.lexicalRepresentation, `is`("http://www.w3.org/2001/XMLSchema"))

        assertThat(XsENTITIES.typeName?.localName?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(XsENTITIES.typeName?.localName?.lexicalRepresentation, `is`("ENTITIES"))

        assertThat(XsENTITIES.baseType, `is`(XsAnySimpleType as XmlSchemaType))

        assertThat(XsENTITIES.itemType, `is`(XsENTITY as XdmSequenceType))
    }
}
