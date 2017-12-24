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
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSimpleExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.toInt
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathIntegerLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathDoubleLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathDecimalLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryXdmTest : ParserTestCase() {
    private inline fun <reified T> parseLiteral(xquery: String): XdmAtomicValue {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmAtomicValue
    }

    private inline fun <reified T> parseSimpleExpression(xquery: String): XdmSimpleExpression {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmSimpleExpression
    }

    // region Atomic Value for Literal Types
    // region DoubleLiteral

    fun testDoubleLiteral() {
        val literal = parseLiteral<XPathDoubleLiteral>("1e3")
        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))
    }

    // endregion
    // region DecimalLiteral

    fun testDecimalLiteral() {
        val literal = parseLiteral<XPathDecimalLiteral>("12.34")
        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))
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

    fun testStringLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XPathStringLiteral>("\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.lexicalRepresentation, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    fun testStringLiteral_CharRef() {
        val literal = parseLiteral<XPathStringLiteral>("\"&#xA0;&#160;&#x20;\"")
        assertThat(literal.lexicalRepresentation, `is`("\u00A0\u00A0\u0020"))
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
    // region UriLiteral

    fun testUriLiteral() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com\uFFFF\"")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testUriLiteral_Unclosed() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testUriLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.lexicalRepresentation, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testUriLiteral_CharRef() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")
        assertThat(literal.lexicalRepresentation, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testUriLiteral_EscapeApos() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = '''\"\"'")
        assertThat(literal.lexicalRepresentation, `is`("'\"\""))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testUriLiteral_EscapeQuot() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"''\"\"\"")
        assertThat(literal.lexicalRepresentation, `is`("''\""))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    // endregion
    // region Literal

    fun testLiteral_DoubleLiteral() {
        val literal = parseLiteral<XQueryLiteral>("1e3")
        assertThat(literal.lexicalRepresentation, `is`("1e3"))
        assertThat(literal.staticType, `is`(XsDouble as XdmSequenceType))
    }

    fun testLiteral_DecimalLiteral() {
        val literal = parseLiteral<XQueryLiteral>("12.34")
        assertThat(literal.lexicalRepresentation, `is`("12.34"))
        assertThat(literal.staticType, `is`(XsDecimal as XdmSequenceType))
    }

    fun testLiteral_IntegerLiteral() {
        val literal = parseLiteral<XQueryLiteral>("123")
        assertThat(literal.lexicalRepresentation, `is`("123"))
        assertThat(literal.staticType, `is`(XsInteger as XdmSequenceType))
    }

    fun testLiteral_StringLiteral() {
        val literal = parseLiteral<XQueryLiteral>("\"Lorem ipsum.\"")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.staticType, `is`(XsString as XdmSequenceType))
    }

    // endregion
    // endregion
    // region Simple Expressions
    // region PostfixExpr

    fun testPostfixExpr_LiteralValue() {
        val expr = parseSimpleExpression<XQueryPostfixExpr>("1e3")
        assertThat(expr.constantValue, `is`(notNullValue()))
        assertThat(expr.constantValue, `is`(instanceOf(String::class.java)))
        assertThat(expr.constantValue as String, `is`("1e3"))
        assertThat(expr.staticType, `is`(XsDouble as XdmSequenceType))
    }

    fun testPostfixExpr_LiteralValue_ComplexExpression() {
        val expr = parseSimpleExpression<XQueryPostfixExpr>("1?1")
        assertThat(expr.constantValue, `is`(nullValue())) // Expression is invalid, and cannot be resolved.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    fun testPostfixExpr_NonLiteralValue() {
        val expr = parseSimpleExpression<XQueryPostfixExpr>("test()")
        assertThat(expr.constantValue, `is`(nullValue())) // Cannot evaluate non-literal expression.
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    // endregion
    // endregion
}
