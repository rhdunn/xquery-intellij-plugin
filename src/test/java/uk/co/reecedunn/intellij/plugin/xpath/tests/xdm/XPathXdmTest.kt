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
import uk.co.reecedunn.intellij.plugin.xdm.datatype.QName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmConstantExpression
import uk.co.reecedunn.intellij.plugin.xdm.model.toInt
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XPathXdmTest : ParserTestCase() {
    private inline fun <reified T> parseLiteral(xquery: String): XdmLexicalValue {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmLexicalValue
    }

    private inline fun <reified T> parseSimpleExpression(xquery: String): XdmConstantExpression {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmConstantExpression
    }

    // region Lexical Values
    // region BracedUriLiteral

    fun testBracedUriLiteral() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{http://www.example.com\uFFFF}")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com\uFFFF"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    fun testBracedUriLiteral_Unclosed() {
        val literal = parseLiteral<XPathBracedURILiteral>("Q{http://www.example.com")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com"))
        assertThat(literal.staticType, `is`(XsAnyURI as XdmSequenceType))
    }

    // endregion
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
    // region Lexical and Expanded QNames (Constant Expressions)
    // region NCName

    fun testNCName() {
        val expr = parseSimpleExpression<XPathNCName>("test")
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("test"))
    }

    fun testNCName_Keyword() {
        val expr = parseSimpleExpression<XPathNCName>("option")
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.prefix, `is`(nullValue()))
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("option"))

        assertThat(qname.toString(), `is`("option"))
    }

    // endregion
    // region QName

    fun testQName() {
        val expr = parseSimpleExpression<XPathQName>("fn:true")
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("fn"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("true"))

        assertThat(qname.toString(), `is`("fn:true"))
    }

    fun testQName_KeywordPrefix() {
        val expr = parseSimpleExpression<XPathQName>("option:test")
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("option"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("test"))

        assertThat(qname.toString(), `is`("option:test"))
    }

    fun testQName_KeywordLocalName() {
        val expr = parseSimpleExpression<XPathQName>("test:case")
        assertThat(expr.constantValue, `is`(instanceOf(QName::class.java)))
        assertThat(expr.staticType, `is`(XsQName as XdmSequenceType))

        val qname = expr.constantValue as QName
        assertThat(qname.namespace, `is`(nullValue()))
        assertThat(qname.declaration?.get(), `is`(expr))

        assertThat(qname.prefix?.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.prefix?.lexicalRepresentation, `is`("test"))

        assertThat(qname.localName.staticType, `is`(XsNCName as XdmSequenceType))
        assertThat(qname.localName.lexicalRepresentation, `is`("case"))

        assertThat(qname.toString(), `is`("test:case"))
    }

    fun testQName_NoLocalName() {
        val expr = parseSimpleExpression<XPathQName>("xs:")
        assertThat(expr.constantValue, `is`(nullValue()))
        assertThat(expr.staticType, `is`(XsUntyped as XdmSequenceType))
    }

    // endregion
    // endregion
    // region Constant Expressions
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
