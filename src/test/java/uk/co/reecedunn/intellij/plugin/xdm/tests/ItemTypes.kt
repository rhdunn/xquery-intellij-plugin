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

import junit.framework.TestCase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.*

class ItemTypes : TestCase() {
    fun testXdmItem() {
        assertThat(XdmItem.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmItem.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmItem.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmAnyFunction() {
        assertThat(XdmAnyFunction.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyFunction.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyFunction.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmAnyMap() {
        assertThat(XdmAnyMap.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyMap.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyMap.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmAnyArray() {
        assertThat(XdmAnyArray.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyArray.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyArray.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmNode() {
        assertThat(XdmNode.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmNode.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNode.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmAnyAttribute() {
        assertThat(XdmAnyAttribute.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyAttribute.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyAttribute.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmComment() {
        assertThat(XdmComment.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmComment.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmComment.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmAnyDocument() {
        assertThat(XdmAnyDocument.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyDocument.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyDocument.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmAnyElement() {
        assertThat(XdmAnyElement.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAnyElement.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAnyElement.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmNamespace() {
        assertThat(XdmNamespace.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmNamespace.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNamespace.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmProcessingInstruction() {
        assertThat(XdmProcessingInstruction.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmProcessingInstruction.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmProcessingInstruction.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmText() {
        assertThat(XdmText.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmText.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmText.upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }
}
