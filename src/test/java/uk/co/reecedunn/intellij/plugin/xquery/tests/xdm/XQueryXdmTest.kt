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
import uk.co.reecedunn.intellij.plugin.core.extensions.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmAtomicValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryXdmTest : ParserTestCase() {
    private inline fun <reified T> parseLiteral(xquery: String): XdmAtomicValue {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmAtomicValue
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
    // region StringLiteral

    fun testStringLiteral() {
        val literal = parseLiteral<XQueryStringLiteral>("\"Lorem ipsum.\uFFFF\"")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum.\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.lexicalType, `is`(XsString as XdmType))
    }

    fun testStringLiteral_Unclosed() {
        val literal = parseLiteral<XQueryStringLiteral>("\"Lorem ipsum.")
        assertThat(literal.lexicalRepresentation, `is`("Lorem ipsum."))
        assertThat(literal.lexicalType, `is`(XsString as XdmType))
    }

    fun testStringLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XQueryStringLiteral>("\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.lexicalRepresentation, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.lexicalType, `is`(XsString as XdmType))
    }

    fun testStringLiteral_CharRef() {
        val literal = parseLiteral<XQueryStringLiteral>("\"&#xA0;&#160;&#x20;\"")
        assertThat(literal.lexicalRepresentation, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.lexicalType, `is`(XsString as XdmType))
    }

    fun testStringLiteral_EscapeApos() {
        val literal = parseLiteral<XQueryStringLiteral>("'''\"\"'")
        assertThat(literal.lexicalRepresentation, `is`("'\"\""))
        assertThat(literal.lexicalType, `is`(XsString as XdmType))
    }

    fun testStringLiteral_EscapeQuot() {
        val literal = parseLiteral<XQueryStringLiteral>("\"''\"\"\"")
        assertThat(literal.lexicalRepresentation, `is`("''\""))
        assertThat(literal.lexicalType, `is`(XsString as XdmType))
    }

    // endregion
    // region UriLiteral

    fun testUriLiteral() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com\uFFFF\"")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
        assertThat(literal.lexicalType, `is`(XsAnyURI as XdmType))
    }

    fun testUriLiteral_Unclosed() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"http://www.example.com")
        assertThat(literal.lexicalRepresentation, `is`("http://www.example.com"))
        assertThat(literal.lexicalType, `is`(XsAnyURI as XdmType))
    }

    fun testUriLiteral_PredefinedEntityReference() {
        // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"")
        assertThat(literal.lexicalRepresentation, `is`("<áā\uD835\uDD04≪\u0338&;&gt"))
        assertThat(literal.lexicalType, `is`(XsAnyURI as XdmType))
    }

    fun testUriLiteral_CharRef() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"&#xA0;&#160;&#x20;\"")
        assertThat(literal.lexicalRepresentation, `is`("\u00A0\u00A0\u0020"))
        assertThat(literal.lexicalType, `is`(XsAnyURI as XdmType))
    }

    fun testUriLiteral_EscapeApos() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = '''\"\"'")
        assertThat(literal.lexicalRepresentation, `is`("'\"\""))
        assertThat(literal.lexicalType, `is`(XsAnyURI as XdmType))
    }

    fun testUriLiteral_EscapeQuot() {
        val literal = parseLiteral<XQueryUriLiteral>("module namespace test = \"''\"\"\"")
        assertThat(literal.lexicalRepresentation, `is`("''\""))
        assertThat(literal.lexicalType, `is`(XsAnyURI as XdmType))
    }

    // endregion
    // endregion
}
