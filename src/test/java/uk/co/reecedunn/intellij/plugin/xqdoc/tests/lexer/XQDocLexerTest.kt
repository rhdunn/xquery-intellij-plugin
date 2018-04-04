/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xqdoc.tests.lexer

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType

class XQDocLexerTest : LexerTestCase() {
    // region Lexer :: Invalid State

    fun testInvalidState() {
        val lexer = XQDocLexer()

        val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, -1) }
        assertThat(e.message, `is`("Invalid state: -1"))
    }

    // endregion
    // region Lexer :: Empty Buffer

    fun testEmptyBuffer() {
        val lexer = XQDocLexer()

        lexer.start("")
        matchToken(lexer, "", 0, 0, 0, null)
    }

    // endregion
    // region xqDoc :: CommentContents + Comment

    fun testCommentContents() {
        val lexer = XQDocLexer()

        lexer.start("Lorem ipsum dolor.")
        matchToken(lexer, "Lorem ipsum dolor.", 0, 0, 18, XQDocTokenType.COMMENT_CONTENTS)
        matchToken(lexer, "", 0, 18, 18, null)
    }

    // endregion
    // region xqDoc :: Contents + XQDocComment

    fun testContents() {
        val lexer = XQDocLexer()

        lexer.start("~Lorem ipsum dolor.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem ipsum dolor.", 1, 1, 19, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 19, 19, null)
    }

    // endregion
    // region xqDoc :: Contents + PredefinedEntityRef

    fun testContents_PredefinedEntityRef() {
        val lexer = XQDocLexer()

        lexer.start("~Lorem &amp; ipsum.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem ", 1, 1, 7, XQDocTokenType.CONTENTS)
        matchToken(lexer, "&amp;", 1, 7, 12, XQDocTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, " ipsum.", 1, 12, 19, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 19, 19, null)

        lexer.start("~&")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&", 1, 1, 2, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 2, 2, null)

        lexer.start("~&abc")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&abc", 1, 1, 5, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 5, 5, null)

        lexer.start("~&abc!")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&abc", 1, 1, 5, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "!", 1, 5, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 6, 6, null)

        lexer.start("~&;")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&;", 1, 1, 3, XQDocTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 3, 3, null)
    }

    // endregion
    // region xqDoc :: Contents + CharRef

    fun testContents_CharRef_Octal() {
        val lexer = XQDocLexer()

        lexer.start("~Lorem&#20;ipsum.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem", 1, 1, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "&#20;", 1, 6, 11, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "ipsum.", 1, 11, 17, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 17, 17, null)

        lexer.start("~&#")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#", 1, 1, 3, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 3, 3, null)

        lexer.start("~&#20")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#20", 1, 1, 5, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 5, 5, null)

        lexer.start("~&# ")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#", 1, 1, 3, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, " ", 1, 3, 4, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 4, 4, null)

        lexer.start("~&#;")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#;", 1, 1, 4, XQDocTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 4, 4, null)
    }

    fun testContents_CharRef_Hexadecimal() {
        val lexer = XQDocLexer()

        lexer.start("~One&#x20;&#xae;&#xDC;Two.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "One", 1, 1, 4, XQDocTokenType.CONTENTS)
        matchToken(lexer, "&#x20;", 1, 4, 10, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "&#xae;", 1, 10, 16, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "&#xDC;", 1, 16, 22, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "Two.", 1, 22, 26, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 26, 26, null)

        lexer.start("~&#x")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#x", 1, 1, 4, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 4, 4, null)

        lexer.start("~&#x20")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#x20", 1, 1, 6, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 1, 6, 6, null)

        lexer.start("~&#x ")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#x", 1, 1, 4, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, " ", 1, 4, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 5, 5, null)

        lexer.start("~&#x;&#x2G;&#x2g;&#xg2;")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "&#x;", 1, 1, 5, XQDocTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "&#x2", 1, 5, 9, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "G;", 1, 9, 11, XQDocTokenType.CONTENTS)
        matchToken(lexer, "&#x2", 1, 11, 15, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "g;", 1, 15, 17, XQDocTokenType.CONTENTS)
        matchToken(lexer, "&#x", 1, 17, 20, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "g2;", 1, 20, 23, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 23, 23, null)
    }

    // endregion
    // region xqDoc :: DirAttrConstructor + DirAttributeValue

    fun testDirAttrConstructor_Quot() {
        val lexer = XQDocLexer()

        lexer.start("~one <two three = \"four\" />")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "one ", 1, 1, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 5, 6, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "two", 3, 6, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 15, 16, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "=", 3, 16, 17, XQDocTokenType.XML_EQUAL)
        matchToken(lexer, " ", 3, 17, 18, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "\"", 3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
        matchToken(lexer, "four", 6, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
        matchToken(lexer, "\"", 6, 23, 24, XQDocTokenType.XML_ATTRIBUTE_VALUE_END)
        matchToken(lexer, " ", 3, 24, 25, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "/>", 3, 25, 27, XQDocTokenType.SELF_CLOSING_XML_TAG)
        matchToken(lexer, "", 1, 27, 27, null)

        lexer.start("~one <two three = \"four")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "one ", 1, 1, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 5, 6, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "two", 3, 6, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 15, 16, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "=", 3, 16, 17, XQDocTokenType.XML_EQUAL)
        matchToken(lexer, " ", 3, 17, 18, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "\"", 3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
        matchToken(lexer, "four", 6, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
        matchToken(lexer, "", 6, 23, 23, null)
    }

    fun testDirAttrConstructor_Apos() {
        val lexer = XQDocLexer()

        lexer.start("~one <two three = 'four' />")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "one ", 1, 1, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 5, 6, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "two", 3, 6, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 15, 16, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "=", 3, 16, 17, XQDocTokenType.XML_EQUAL)
        matchToken(lexer, " ", 3, 17, 18, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "'", 3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
        matchToken(lexer, "four", 7, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
        matchToken(lexer, "'", 7, 23, 24, XQDocTokenType.XML_ATTRIBUTE_VALUE_END)
        matchToken(lexer, " ", 3, 24, 25, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "/>", 3, 25, 27, XQDocTokenType.SELF_CLOSING_XML_TAG)
        matchToken(lexer, "", 1, 27, 27, null)

        lexer.start("~one <two three = 'four")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "one ", 1, 1, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 5, 6, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "two", 3, 6, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "three", 3, 10, 15, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 15, 16, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "=", 3, 16, 17, XQDocTokenType.XML_EQUAL)
        matchToken(lexer, " ", 3, 17, 18, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "'", 3, 18, 19, XQDocTokenType.XML_ATTRIBUTE_VALUE_START)
        matchToken(lexer, "four", 7, 19, 23, XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
        matchToken(lexer, "", 7, 23, 23, null)
    }

    // endregion
    // region xqDoc :: DirElemConstructor

    fun testDirElemConstructor() {
        val lexer = XQDocLexer()

        lexer.start("~one <two >three</two > four")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "one ", 1, 1, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 5, 6, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "two", 3, 6, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, ">", 3, 10, 11, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "three", 4, 11, 16, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "</", 4, 16, 18, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "two", 5, 18, 21, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 5, 21, 22, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, ">", 5, 22, 23, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, " four", 1, 23, 28, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 28, 28, null)

        lexer.start("~one <two >three")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "one ", 1, 1, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 5, 6, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "two", 3, 6, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, ">", 3, 10, 11, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "three", 4, 11, 16, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "", 4, 16, 16, null)

        lexer.start("~one <two#>three</two#> four")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "one ", 1, 1, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 5, 6, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "two", 3, 6, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, "#", 3, 9, 10, XQDocTokenType.INVALID)
        matchToken(lexer, ">", 3, 10, 11, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "three", 4, 11, 16, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "</", 4, 16, 18, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "two", 5, 18, 21, XQDocTokenType.XML_TAG)
        matchToken(lexer, "#", 5, 21, 22, XQDocTokenType.INVALID)
        matchToken(lexer, ">", 5, 22, 23, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, " four", 1, 23, 28, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 28, 28, null)
    }

    fun testDirElemConstructor_SelfClosing() {
        val lexer = XQDocLexer()

        lexer.start("~a <b /> c")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a ", 1, 1, 3, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 3, 4, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "b", 3, 4, 5, XQDocTokenType.XML_TAG)
        matchToken(lexer, " ", 3, 5, 6, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "/>", 3, 6, 8, XQDocTokenType.SELF_CLOSING_XML_TAG)
        matchToken(lexer, " c", 1, 8, 10, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 10, 10, null)

        lexer.start("~a <b/")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a ", 1, 1, 3, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 3, 4, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "b", 3, 4, 5, XQDocTokenType.XML_TAG)
        matchToken(lexer, "/", 3, 5, 6, XQDocTokenType.INVALID)
        matchToken(lexer, "", 3, 6, 6, null)

        lexer.start("~a <b#/> c")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a ", 1, 1, 3, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 3, 4, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "b", 3, 4, 5, XQDocTokenType.XML_TAG)
        matchToken(lexer, "#", 3, 5, 6, XQDocTokenType.INVALID)
        matchToken(lexer, "/>", 3, 6, 8, XQDocTokenType.SELF_CLOSING_XML_TAG)
        matchToken(lexer, " c", 1, 8, 10, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 10, 10, null)
    }

    fun testDirElemConstructor_Nested() {
        val lexer = XQDocLexer()

        lexer.start("~a<b>c<d>e</d>f</b>g")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a", 1, 1, 2, XQDocTokenType.CONTENTS)
        matchToken(lexer, "<", 1, 2, 3, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "b", 3, 3, 4, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 3, 4, 5, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "c", 4, 5, 6, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "<", 4, 6, 7, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "d", 3, 7, 8, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 3, 8, 9, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "e", 4, 9, 10, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "</", 4, 10, 12, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "d", 5, 12, 13, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 5, 13, 14, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "f", 4, 14, 15, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "</", 4, 15, 17, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "b", 5, 17, 18, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 5, 18, 19, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "g", 1, 19, 20, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 20, 20, null)
    }

    // endregion
    // region xqDoc :: DirElemConstructor (DirElemContents) + PredefinedEntityRef

    fun testDirElemConstructor_PredefinedEntityRef() {
        val lexer = XQDocLexer()

        lexer.start("~<p>Lorem &amp; ipsum.</p>")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "<", 1, 1, 2, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "p", 3, 2, 3, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 3, 3, 4, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "Lorem ", 4, 4, 10, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "&amp;", 4, 10, 15, XQDocTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, " ipsum.", 4, 15, 22, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "</", 4, 22, 24, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "p", 5, 24, 25, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 5, 25, 26, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "", 1, 26, 26, null)

        lexer.start("~<p>&</p>")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "<", 1, 1, 2, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "p", 3, 2, 3, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 3, 3, 4, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "&", 4, 4, 5, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "</", 4, 5, 7, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "p", 5, 7, 8, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 5, 8, 9, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "", 1, 9, 9, null)

        lexer.start("~<p>&abc</p>")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "<", 1, 1, 2, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "p", 3, 2, 3, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 3, 3, 4, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "&abc", 4, 4, 8, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "</", 4, 8, 10, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "p", 5, 10, 11, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 5, 11, 12, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "", 1, 12, 12, null)

        lexer.start("~<p>&abc!</p>")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "<", 1, 1, 2, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "p", 3, 2, 3, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 3, 3, 4, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "&abc", 4, 4, 8, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "!", 4, 8, 9, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "</", 4, 9, 11, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "p", 5, 11, 12, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 5, 12, 13, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "", 1, 13, 13, null)

        lexer.start("~<p>&;</p>")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "<", 1, 1, 2, XQDocTokenType.OPEN_XML_TAG)
        matchToken(lexer, "p", 3, 2, 3, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 3, 3, 4, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "&;", 4, 4, 6, XQDocTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "</", 4, 6, 8, XQDocTokenType.CLOSE_XML_TAG)
        matchToken(lexer, "p", 5, 8, 9, XQDocTokenType.XML_TAG)
        matchToken(lexer, ">", 5, 9, 10, XQDocTokenType.END_XML_TAG)
        matchToken(lexer, "", 1, 10, 10, null)
    }

    // endregion
    // region xqDoc :: DirElemConstructor (DirElemContents) + CharRef

    fun testDirElemConstructor_CharRef_Octal() {
        val lexer = XQDocLexer()

        lexer.start("Lorem&#20;ipsum.", 0, 16, 4)
        matchToken(lexer, "Lorem", 4, 0, 5, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "&#20;", 4, 5, 10, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "ipsum.", 4, 10, 16, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "", 4, 16, 16, null)

        lexer.start("&#", 0, 2, 4)
        matchToken(lexer, "&#", 4, 0, 2, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 4, 2, 2, null)

        lexer.start("&#20", 0, 4, 4)
        matchToken(lexer, "&#20", 4, 0, 4, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 4, 4, 4, null)

        lexer.start("&# ", 0, 3, 4)
        matchToken(lexer, "&#", 4, 0, 2, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, " ", 4, 2, 3, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "", 4, 3, 3, null)

        lexer.start("&#;", 0, 3, 4)
        matchToken(lexer, "&#;", 4, 0, 3, XQDocTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "", 4, 3, 3, null)
    }

    fun testDirElemConstructor_CharRef_Hexadecimal() {
        val lexer = XQDocLexer()

        lexer.start("One&#x20;&#xae;&#xDC;Two.", 0, 25, 4)
        matchToken(lexer, "One", 4, 0, 3, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "&#x20;", 4, 3, 9, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "&#xae;", 4, 9, 15, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "&#xDC;", 4, 15, 21, XQDocTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "Two.", 4, 21, 25, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "", 4, 25, 25, null)

        lexer.start("&#x", 0, 3, 4)
        matchToken(lexer, "&#x", 4, 0, 3, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 4, 3, 3, null)

        lexer.start("&#x20", 0, 5, 4)
        matchToken(lexer, "&#x20", 4, 0, 5, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", 4, 5, 5, null)

        lexer.start("&#x ", 0, 4, 4)
        matchToken(lexer, "&#x", 4, 0, 3, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, " ", 4, 3, 4, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "", 4, 4, 4, null)

        lexer.start("&#x;&#x2G;&#x2g;&#xg2;", 0, 22, 4)
        matchToken(lexer, "&#x;", 4, 0, 4, XQDocTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "&#x2", 4, 4, 8, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "G;", 4, 8, 10, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "&#x2", 4, 10, 14, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "g;", 4, 14, 16, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "&#x", 4, 16, 19, XQDocTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "g2;", 4, 19, 22, XQDocTokenType.XML_ELEMENT_CONTENTS)
        matchToken(lexer, "", 4, 22, 22, null)
    }

    // endregion
    // region xqDoc :: TaggedContents

    fun testTaggedContents() {
        val lexer = XQDocLexer()

        lexer.start("~Lorem\n@ipsum dolor.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem", 1, 1, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n", 8, 6, 7, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 7, 8, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "ipsum", 2, 8, 13, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 13, 14, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "dolor.", 1, 14, 20, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 20, 20, null)

        lexer.start("~Lorem\n@IPSUM dolor.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem", 1, 1, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n", 8, 6, 7, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 7, 8, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "IPSUM", 2, 8, 13, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 13, 14, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "dolor.", 1, 14, 20, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 20, 20, null)

        lexer.start("~Lorem\n@12345 dolor.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem", 1, 1, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n", 8, 6, 7, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 7, 8, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "12345", 2, 8, 13, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 13, 14, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "dolor.", 1, 14, 20, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 20, 20, null)

        lexer.start("~Lorem\n@# dolor.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem", 1, 1, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n", 8, 6, 7, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 7, 8, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "# dolor.", 2, 8, 16, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 16, 16, null)

        lexer.start("~@lorem ipsum.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "@", 8, 1, 2, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "lorem", 2, 2, 7, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 7, 8, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "ipsum.", 1, 8, 14, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 14, 14, null)

        lexer.start("~@ lorem ipsum.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "@", 8, 1, 2, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, " ", 2, 2, 3, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "lorem ipsum.", 1, 3, 15, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 15, 15, null)
    }

    fun testTaggedContents_AtSignInContents() {
        val lexer = XQDocLexer()

        lexer.start("~Lorem\n@ipsum ab@cd.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "Lorem", 1, 1, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n", 8, 6, 7, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 7, 8, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "ipsum", 2, 8, 13, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 13, 14, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "ab@cd.", 1, 14, 20, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 20, 20, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @author

    fun testTaggedContents_Author() {
        val lexer = XQDocLexer()

        lexer.start("~\n@author John Doe")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "author", 2, 3, 9, XQDocTokenType.T_AUTHOR)
        matchToken(lexer, " ", 2, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "John Doe", 1, 10, 18, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 18, 18, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @deprecated

    fun testTaggedContents_Deprecated() {
        val lexer = XQDocLexer()

        lexer.start("~\n@deprecated As of 1.1.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "deprecated", 2, 3, 13, XQDocTokenType.T_DEPRECATED)
        matchToken(lexer, " ", 2, 13, 14, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "As of 1.1.", 1, 14, 24, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 24, 24, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @error

    fun testTaggedContents_Error() {
        val lexer = XQDocLexer()

        lexer.start("~\n@error The URI does not exist.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "error", 2, 3, 8, XQDocTokenType.T_ERROR)
        matchToken(lexer, " ", 2, 8, 9, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "The URI does not exist.", 1, 9, 32, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 32, 32, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @param

    fun testTaggedContents_Param_VarRef() {
        val lexer = XQDocLexer()

        lexer.start("~\n@param \$arg An argument.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "param", 2, 3, 8, XQDocTokenType.T_PARAM)
        matchToken(lexer, " ", 9, 8, 9, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "$", 9, 9, 10, XQDocTokenType.VARIABLE_INDICATOR)
        matchToken(lexer, "arg", 10, 10, 13, XQDocTokenType.NCNAME)
        matchToken(lexer, " ", 10, 13, 14, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "An argument.", 1, 14, 26, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 26, 26, null)
    }

    fun testTaggedContents_Param_ContentsOnly() {
        val lexer = XQDocLexer()

        lexer.start("~\n@param - \$arg An argument.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "param", 2, 3, 8, XQDocTokenType.T_PARAM)
        matchToken(lexer, " ", 9, 8, 9, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "- \$arg An argument.", 9, 9, 28, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 28, 28, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @return

    fun testTaggedContents_Return() {
        val lexer = XQDocLexer()

        lexer.start("~\n@return Some value.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "return", 2, 3, 9, XQDocTokenType.T_RETURN)
        matchToken(lexer, " ", 2, 9, 10, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "Some value.", 1, 10, 21, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 21, 21, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @see

    fun testTaggedContents_See() {
        val lexer = XQDocLexer()

        lexer.start("~\n@see http://www.example.com")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "see", 2, 3, 6, XQDocTokenType.T_SEE)
        matchToken(lexer, " ", 2, 6, 7, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "http://www.example.com", 1, 7, 29, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 29, 29, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @since

    fun testTaggedContents_Since() {
        val lexer = XQDocLexer()

        lexer.start("~\n@since 1.2")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "since", 2, 3, 8, XQDocTokenType.T_SINCE)
        matchToken(lexer, " ", 2, 8, 9, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "1.2", 1, 9, 12, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 12, 12, null)
    }

    // endregion
    // region xqDoc :: TaggedContents :: @version

    fun testTaggedContents_Version() {
        val lexer = XQDocLexer()

        lexer.start("~\n@version 1.2")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 8, 2, 3, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "version", 2, 3, 10, XQDocTokenType.T_VERSION)
        matchToken(lexer, " ", 2, 10, 11, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "1.2", 1, 11, 14, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 14, 14, null)
    }

    // endregion
    // region xqDoc :: Trim

    fun testTrim_Linux() {
        val lexer = XQDocLexer()

        lexer.start("~a\nb\nc")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a", 1, 1, 2, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n", 8, 2, 3, XQDocTokenType.TRIM)
        matchToken(lexer, "b", 1, 3, 4, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n", 8, 4, 5, XQDocTokenType.TRIM)
        matchToken(lexer, "c", 1, 5, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 6, 6, null)

        lexer.start("~a\n \tb\n\t c")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a", 1, 1, 2, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n \t", 8, 2, 5, XQDocTokenType.TRIM)
        matchToken(lexer, "b", 1, 5, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n\t ", 8, 6, 9, XQDocTokenType.TRIM)
        matchToken(lexer, "c", 1, 9, 10, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 10, 10, null)

        lexer.start("~\n\n")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "\n", 8, 2, 3, XQDocTokenType.TRIM)
        matchToken(lexer, "", 8, 3, 3, null)
    }

    fun testTrim_Mac() {
        val lexer = XQDocLexer()

        // The xqDoc grammar does not support Mac line endings ('\r'), but XQuery/XML
        // line ending normalisation rules do.

        lexer.start("~a\rb\rc")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a", 1, 1, 2, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r", 8, 2, 3, XQDocTokenType.TRIM)
        matchToken(lexer, "b", 1, 3, 4, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r", 8, 4, 5, XQDocTokenType.TRIM)
        matchToken(lexer, "c", 1, 5, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 6, 6, null)

        lexer.start("~a\r \tb\r\t c")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a", 1, 1, 2, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r \t", 8, 2, 5, XQDocTokenType.TRIM)
        matchToken(lexer, "b", 1, 5, 6, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r\t ", 8, 6, 9, XQDocTokenType.TRIM)
        matchToken(lexer, "c", 1, 9, 10, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 10, 10, null)

        lexer.start("~\r\r")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\r", 8, 1, 2, XQDocTokenType.TRIM)
        matchToken(lexer, "\r", 8, 2, 3, XQDocTokenType.TRIM)
        matchToken(lexer, "", 8, 3, 3, null)
    }

    fun testTrim_Windows() {
        val lexer = XQDocLexer()

        // The xqDoc grammar does not support Windows line endings ('\r\n'), but XQuery/XML
        // line ending normalisation rules do.

        lexer.start("~a\r\nb\r\nc")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a", 1, 1, 2, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r\n", 8, 2, 4, XQDocTokenType.TRIM)
        matchToken(lexer, "b", 1, 4, 5, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r\n", 8, 5, 7, XQDocTokenType.TRIM)
        matchToken(lexer, "c", 1, 7, 8, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 8, 8, null)

        lexer.start("~a\r\n \tb\r\n\t c")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "a", 1, 1, 2, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r\n \t", 8, 2, 6, XQDocTokenType.TRIM)
        matchToken(lexer, "b", 1, 6, 7, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\r\n\t ", 8, 7, 11, XQDocTokenType.TRIM)
        matchToken(lexer, "c", 1, 11, 12, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 12, 12, null)

        lexer.start("~\r\n\r\n")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\r\n", 8, 1, 3, XQDocTokenType.TRIM)
        matchToken(lexer, "\r\n", 8, 3, 5, XQDocTokenType.TRIM)
        matchToken(lexer, "", 8, 5, 5, null)
    }

    fun testTrim_WhitespaceAfterTrim() {
        val lexer = XQDocLexer()

        // This is different to the xqDoc grammar, but is necessary to support treating
        // '@' characters within the line as part of the Contents token. The xqDoc
        // processor collates these in the parser phase, but the syntax highlighter
        // needs to highlight those as comment, not document tag, tokens.

        lexer.start("~\n : \t@lorem ipsum.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n :", 8, 1, 4, XQDocTokenType.TRIM)
        matchToken(lexer, " \t", 8, 4, 6, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "@", 8, 6, 7, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "lorem", 2, 7, 12, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 12, 13, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "ipsum.", 1, 13, 19, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 19, 19, null)

        lexer.start("~\n :\t @lorem ipsum.")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n :", 8, 1, 4, XQDocTokenType.TRIM)
        matchToken(lexer, "\t ", 8, 4, 6, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "@", 8, 6, 7, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "lorem", 2, 7, 12, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 12, 13, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "ipsum.", 1, 13, 19, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 19, 19, null)

        lexer.start("~\n : @lorem ipsum\n : @dolor sed emit")
        matchToken(lexer, "~", 0, 0, 1, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n :", 8, 1, 4, XQDocTokenType.TRIM)
        matchToken(lexer, " ", 8, 4, 5, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "@", 8, 5, 6, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "lorem", 2, 6, 11, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 11, 12, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "ipsum", 1, 12, 17, XQDocTokenType.CONTENTS)
        matchToken(lexer, "\n :", 8, 17, 20, XQDocTokenType.TRIM)
        matchToken(lexer, " ", 8, 20, 21, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "@", 8, 21, 22, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "dolor", 2, 22, 27, XQDocTokenType.TAG)
        matchToken(lexer, " ", 2, 27, 28, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "sed emit", 1, 28, 36, XQDocTokenType.CONTENTS)
        matchToken(lexer, "", 1, 36, 36, null)
    }

    // endregion
}
