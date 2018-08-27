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
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType

class Sequences {
    @Test
    fun testXdmEmptySequence() {
        assertThat(XdmEmptySequence.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmEmptySequence.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(XdmEmptySequence.upperBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(XdmEmptySequence.toString(), `is`("empty-sequence()"))
    }

    @Test
    fun testXdmOptional() {
        val seq = XdmOptional(XsString)
        assertThat(seq.itemType, `is`(XsString as XdmSequenceType))
        assertThat(seq.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(seq.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(seq.toString(), `is`("xs:string?"))
    }

    @Test
    fun testXdmOptionalSequence() {
        val seq = XdmOptionalSequence(XsString)
        assertThat(seq.itemType, `is`(XsString as XdmSequenceType))
        assertThat(seq.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(seq.upperBound, `is`(XdmSequenceType.Occurs.MANY))
        assertThat(seq.toString(), `is`("xs:string*"))
    }

    @Test
    fun testXdmSequence() {
        val seq = XdmSequence(XsString)
        assertThat(seq.itemType, `is`(XsString as XdmSequenceType))
        assertThat(seq.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(seq.upperBound, `is`(XdmSequenceType.Occurs.MANY))
        assertThat(seq.toString(), `is`("xs:string+"))
    }

    @Test
    fun testXdmOptionalItem() {
        assertThat(XdmItemSequence.itemType, `is`(XdmItem as XdmSequenceType))
        assertThat(XdmItemSequence.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(XdmItemSequence.upperBound, `is`(XdmSequenceType.Occurs.MANY))
        assertThat(XdmItemSequence.toString(), `is`("item()*"))
    }
}
