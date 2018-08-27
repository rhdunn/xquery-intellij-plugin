/*
 * Copyright (C) 2018 Reece H. Dunn
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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmProcessingInstruction
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType

class ItemTypes {
    @Test
    fun testXdmItem() {
        assertThat(XdmItem.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmItem.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmItem.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmItem.toString(), `is`("item()"))
    }

    @Test
    fun testXdmAnyFunction() {
        assertThat(XdmAnyFunction.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyFunction.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyFunction.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyFunction.toString(), `is`("function(*)"))
    }

    @Test
    fun testXdmAnyMap() {
        assertThat(XdmAnyMap.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyMap.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyMap.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyMap.toString(), `is`("map(*)"))
    }

    @Test
    fun testXdmAnyArray() {
        assertThat(XdmAnyArray.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyArray.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyArray.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyArray.toString(), `is`("array(*)"))
    }

    @Test
    fun testXdmNode() {
        assertThat(XdmNode.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmNode.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNode.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNode.toString(), `is`("node()"))
    }

    @Test
    fun testXdmAnyAttribute() {
        assertThat(XdmAnyAttribute.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyAttribute.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyAttribute.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyAttribute.toString(), `is`("attribute()"))
    }

    @Test
    fun testXdmComment() {
        assertThat(XdmComment.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmComment.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmComment.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmComment.toString(), `is`("comment()"))
    }

    @Test
    fun testXdmAnyDocument() {
        assertThat(XdmAnyDocument.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyDocument.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyDocument.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyDocument.toString(), `is`("document-node()"))
    }

    @Test
    fun testXdmAnyElement() {
        assertThat(XdmAnyElement.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyElement.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyElement.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyElement.toString(), `is`("element()"))
    }

    @Test
    fun testXdmNamespace() {
        assertThat(XdmNamespace.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmNamespace.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNamespace.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNamespace.toString(), `is`("namespace-node()"))
    }

    @Test
    fun testXdmAnyProcessingInstruction() {
        val type = XdmProcessingInstruction(null)
        assertThat(type.nodeName, `is`(nullValue()))
        assertThat(type.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.toString(), `is`("processing-instruction()"))
    }

    @Test
    fun testXdmProcessingInstruction() {
        val type = XdmProcessingInstruction(createLexicalQName(null, "xml-stylesheet"))
        assertThat(type.nodeName?.declaration, `is`(nullValue()))
        assertThat(type.nodeName?.namespace, `is`(nullValue()))
        assertThat(type.nodeName?.prefix, `is`(nullValue()))
        assertThat(type.nodeName?.localName?.staticValue as String, `is`("xml-stylesheet"))

        assertThat(type.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(type.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(type.toString(), `is`("processing-instruction(xml-stylesheet)"))
    }

    @Test
    fun testXdmText() {
        assertThat(XdmText.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmText.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmText.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmText.toString(), `is`("text()"))
    }
}
