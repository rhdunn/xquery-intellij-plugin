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
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.XsUntyped
import uk.co.reecedunn.intellij.plugin.xdm.model.*

class ItemTypes : TestCase() {
    fun testXdmItem() {
        assertThat(XdmItem().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmItem().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmItem().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmFunction() {
        assertThat(XdmFunction().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmFunction().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmFunction().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmMap() {
        assertThat(XdmMap().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmMap().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmMap().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmArray() {
        assertThat(XdmArray().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmArray().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmArray().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmNode() {
        assertThat(XdmNode().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmNode().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNode().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmAttribute() {
        assertThat(XdmAttribute().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmAttribute().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmAttribute().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmComment() {
        assertThat(XdmComment().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmComment().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmComment().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmDocument() {
        assertThat(XdmDocument().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmDocument().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmDocument().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmElement() {
        assertThat(XdmElement().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmElement().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmElement().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmNamespace() {
        assertThat(XdmNamespace().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmNamespace().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmNamespace().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmProcessingInstruction() {
        assertThat(XdmProcessingInstruction().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmProcessingInstruction().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmProcessingInstruction().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }

    fun testXdmText() {
        assertThat(XdmText().itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmText().lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(XdmText().upperBound, `is`(XdmSequenceType.Occurs.ONE))
    }
}
