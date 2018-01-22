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

class Sequences : TestCase() {
    fun testXdmEmptySequence() {
        assertThat(XdmEmptySequence.itemType, `is`(XsUntyped as XdmSequenceType))
        assertThat(XdmEmptySequence.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(XdmEmptySequence.upperBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(XdmEmptySequence.toString(), `is`("()"))
    }

    fun testXdmOptional() {
        val opt = XdmOptional(XsString)
        assertThat(opt.itemType, `is`(XsString as XdmSequenceType))
        assertThat(opt.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(opt.upperBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(opt.toString(), `is`("xs:string?"))
    }

    fun testXdmOptionalSequence() {
        val opt = XdmOptionalSequence(XsString)
        assertThat(opt.itemType, `is`(XsString as XdmSequenceType))
        assertThat(opt.lowerBound, `is`(XdmSequenceType.Occurs.ZERO))
        assertThat(opt.upperBound, `is`(XdmSequenceType.Occurs.MANY))
        assertThat(opt.toString(), `is`("xs:string*"))
    }

    fun testXdmSequence() {
        val opt = XdmSequence(XsString)
        assertThat(opt.itemType, `is`(XsString as XdmSequenceType))
        assertThat(opt.lowerBound, `is`(XdmSequenceType.Occurs.ONE))
        assertThat(opt.upperBound, `is`(XdmSequenceType.Occurs.MANY))
        assertThat(opt.toString(), `is`("xs:string+"))
    }
}
