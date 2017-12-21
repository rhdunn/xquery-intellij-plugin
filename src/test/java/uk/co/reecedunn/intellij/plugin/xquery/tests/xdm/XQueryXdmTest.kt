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
package uk.co.reecedunn.intellij.plugin.xquery.tests.xdm

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.extensions.descendants
import uk.co.reecedunn.intellij.plugin.xdm.XsDecimal
import uk.co.reecedunn.intellij.plugin.xdm.XsDouble
import uk.co.reecedunn.intellij.plugin.xdm.XsInteger
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryXdmTest : ParserTestCase() {
    private inline fun <reified T> parseLiteral(xquery: String): XdmAtomicValue {
        return parseText(xquery)!!.descendants().filterIsInstance<T>().first() as XdmAtomicValue
    }

    // region Atomic Value for Literal Types
    // region DoubleLiteral

    fun testDoubleLiteral() {
        val literal = parseLiteral<XQueryDoubleLiteral>("1e3")
        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.lexicalType, `is`(XsDouble as XdmType))
    }

    // endregion
    // region DecimalLiteral

    fun testDecimalLiteral() {
        val literal = parseLiteral<XQueryDecimalLiteral>("12.34")
        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.lexicalType, `is`(XsDecimal as XdmType))
    }

    // endregion
    // region IntegerLiteral

    fun testIntegerLiteral() {
        val literal = parseLiteral<XQueryIntegerLiteral>("123")
        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.lexicalType, `is`(XsInteger as XdmType))
    }

    // endregion
    // endregion
}
