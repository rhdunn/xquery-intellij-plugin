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
package uk.co.reecedunn.intellij.plugin.xdm.tests

import junit.framework.TestCase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSimpleType
import uk.co.reecedunn.intellij.plugin.xdm.model.XmlSchemaType

class SimpleAndComplexTypes : TestCase() {
    fun testXsAnyType() {
        assertThat(XsAnyType.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsAnyType.typeName.localName, `is`("anyType"))
        assertThat(XsAnyType.baseType, `is`(nullValue()))
    }

    fun testXsUntyped() {
        assertThat(XsUntyped.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsUntyped.typeName.localName, `is`("untyped"))
        assertThat(XsUntyped.baseType, `is`(XsAnyType))
    }

    fun testXsAnySimpleType() {
        assertThat(XsAnySimpleType.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsAnySimpleType.typeName.localName, `is`("anySimpleType"))
        assertThat(XsAnySimpleType.baseType, `is`(XsAnyType))
    }

    fun testXsAnyAtomicType() {
        assertThat(XsAnyAtomicType.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsAnyAtomicType.typeName.localName, `is`("anyAtomicType"))
        assertThat(XsAnyAtomicType.baseType, `is`(XsAnySimpleType as XmlSchemaType))
    }

    fun testXsNumeric() {
        assertThat(XsNumeric.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNumeric.typeName.localName, `is`("numeric"))
        assertThat(XsNumeric.baseType, `is`(XsAnySimpleType as XmlSchemaType))

        assertThat(XsNumeric.unionOf.size, `is`(3))
        assertThat(XsNumeric.unionOf[0], `is`(XsDouble as XdmSimpleType))
        assertThat(XsNumeric.unionOf[1], `is`(XsFloat as XdmSimpleType))
        assertThat(XsNumeric.unionOf[2], `is`(XsDecimal as XdmSimpleType))
    }

    fun testXsIDREFS() {
        assertThat(XsIDREFS.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsIDREFS.typeName.localName, `is`("IDREFS"))
        assertThat(XsIDREFS.baseType, `is`(XsAnySimpleType as XmlSchemaType))
    }

    fun testXsNMTOKENS() {
        assertThat(XsNMTOKENS.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsNMTOKENS.typeName.localName, `is`("NMTOKENS"))
        assertThat(XsNMTOKENS.baseType, `is`(XsAnySimpleType as XmlSchemaType))
    }

    fun testXsENTITIES() {
        assertThat(XsENTITIES.typeName.namespace, `is`("http://www.w3.org/2001/XMLSchema"))
        assertThat(XsENTITIES.typeName.localName, `is`("ENTITIES"))
        assertThat(XsENTITIES.baseType, `is`(XsAnySimpleType as XmlSchemaType))
    }
}
