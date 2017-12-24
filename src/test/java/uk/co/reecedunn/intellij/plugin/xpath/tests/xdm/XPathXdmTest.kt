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
package uk.co.reecedunn.intellij.plugin.xpath.tests.xdm

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSimpleExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.toInt
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XPathXdmTest : ParserTestCase() {
    private inline fun <reified T> parseLiteral(xquery: String): XdmAtomicValue {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmAtomicValue
    }

    private inline fun <reified T> parseSimpleExpression(xquery: String): XdmSimpleExpression {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmSimpleExpression
    }

    // region Atomic Values
    // region DecimalLiteral

    fun testDecimalLiteral() {
        val literal = parseLiteral<XPathDecimalLiteral>("12.34")
        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))
    }

    // endregion
    // region DoubleLiteral

    fun testDoubleLiteral() {
        val literal = parseLiteral<XPathDoubleLiteral>("1e3")
        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))
    }

    // endregion
    // region IntegerLiteral

    fun testIntegerLiteral() {
        val literal = parseLiteral<XPathIntegerLiteral>("123")
        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))
        assertThat(literal.toInt(), `is`(123))
    }

    // endregion
    // region Literal

    fun testLiteral_DoubleLiteral() {
        val literal = parseLiteral<XPathLiteral>("1e3")
        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))
    }

    fun testLiteral_DecimalLiteral() {
        val literal = parseLiteral<XPathLiteral>("12.34")
        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))
    }

    fun testLiteral_IntegerLiteral() {
        val literal = parseLiteral<XPathLiteral>("123")
        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))
    }

    fun testLiteral_StringLiteral() {
        val literal = parseLiteral<XPathLiteral>("\"Lorem ipsum.\"")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    // endregion
    // region StringLiteral

    fun testStringLiteral() {
        val literal = parseLiteral<XPathStringLiteral>("\"Lorem ipsum.\uFFFF\"")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    fun testStringLiteral_Unclosed() {
        val literal = parseLiteral<XPathStringLiteral>("\"Lorem ipsum.")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    fun testStringLiteral_EscapeApos() {
        val literal = parseLiteral<XPathStringLiteral>("'''\"\"'")
        assertThat(literal.lexicalRepresentation, `is`("'\"\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    fun testStringLiteral_EscapeQuot() {
        val literal = parseLiteral<XPathStringLiteral>("\"''\"\"\"")
        assertThat(literal.lexicalRepresentation, `is`("''\""))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    // endregion
    // endregion
    // region Simple Expressions
    // region PostfixExpr

    fun testPostfixExpr_LiteralValue() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("1e3")
        assertThat(expr.constantValue, `is`(notNullValue()))
        assertThat(expr.constantValue, `is`(instanceOf(String::class.java)))
        assertThat(expr.constantValue as String, `is`("1e3"))
        assertThat(expr.staticType, `is`(XsDouble as XdmSequenceType))
    }

    fun testPostfixExpr_LiteralValue_ComplexExpression() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("1?1")
        assertThat(expr.constantValue, `is`(nullValue())) // Expression is invalid, and cannot be resolved.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    fun testPostfixExpr_NonLiteralValue() {
        val expr = parseSimpleExpression<XPathPostfixExpr>("test()")
        assertThat(expr.constantValue, `is`(nullValue())) // Cannot evaluate non-literal expression.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    // endregion
    // endregion
}
