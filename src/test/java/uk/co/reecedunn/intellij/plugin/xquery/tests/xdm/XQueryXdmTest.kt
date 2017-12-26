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
package uk.co.reecedunn.intellij.plugin.xquery.tests.xdm

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.xdm.*
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XQueryXdmTest : ParserTestCase() {
    private inline fun <reified T> parseLiteral(xquery: String): XdmLexicalValue {
        return parseText(xquery)!!.walkTree().filterIsInstance<T>().first() as XdmLexicalValue
    }

    // region Lexical Values
    // region StringLiteral

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

    // endregion
    // endregion
}
