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
package uk.co.reecedunn.intellij.plugin.xdm.tests.lexer

import com.intellij.lexer.Lexer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xdm.lexer.STATE_NCNAME
import uk.co.reecedunn.intellij.plugin.xdm.lexer.XmlSchemaDataTypeLexer
import uk.co.reecedunn.intellij.plugin.xdm.lexer.XmlSchemaDataTypeTokenType

class XmlSchemaDataTypeLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer = XmlSchemaDataTypeLexer()

    // region Lexer :: Invalid State

    @Test
    fun testInvalidState() {
        val lexer = createLexer()

        val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, -1) }
        assertThat(e.message, `is`("Invalid state: -1"))
    }

    // endregion
    // region NCName

    @Test
    fun testNCName_EmptyBuffer() {
        val lexer = createLexer()

        lexer.start("", 0, 0, STATE_NCNAME)
        matchToken(lexer, "", STATE_NCNAME, 0, 0, null)
    }

    @Test
    fun testNCName_WhiteSpace() {
        val lexer = createLexer()

        lexer.start(" \t\r\n", 0, 4, STATE_NCNAME)
        matchToken(lexer, " \t\r\n", STATE_NCNAME, 0, 4, XmlSchemaDataTypeTokenType.WHITE_SPACE)
    }

    @Test
    fun testNCName_PredefinedEntityRef() {
        val lexer = createLexer()

        lexer.start("One&abc;&aBc;&Abc;&ABC;&a4;&a;Two", 0, 33, STATE_NCNAME)
        matchToken(lexer, "One", STATE_NCNAME, 0, 3, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "&abc;", 2, 3, 8, XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, "&aBc;", 2, 8, 13, XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, "&Abc;", 2, 13, 18, XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, "&ABC;", 2, 18, 23, XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, "&a4;", 2, 23, 27, XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, "&a;", 2, 27, 30, XmlSchemaDataTypeTokenType.PREDEFINED_ENTITY_REFERENCE)
        matchToken(lexer, "Two", 2, 30, 33, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "", 2, 33, 33, null)

        lexer.start("&", 0, 1, STATE_NCNAME)
        matchToken(lexer, "&", STATE_NCNAME, 0, 1, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 1, 1, null)

        lexer.start("&abc!", 0, 5, STATE_NCNAME)
        matchToken(lexer, "&abc", STATE_NCNAME, 0, 4, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "!", STATE_NCNAME, 4, 5, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "", STATE_NCNAME, 5, 5, null)

        lexer.start("& ", 0, 2, STATE_NCNAME)
        matchToken(lexer, "&", STATE_NCNAME, 0, 1, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, " ", STATE_NCNAME, 1, 2, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "", STATE_NCNAME, 2, 2, null)

        lexer.start("&abc", 0, 4, STATE_NCNAME)
        matchToken(lexer, "&abc", STATE_NCNAME, 0, 4, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 4, 4, null)

        lexer.start("&;", 0, 2, STATE_NCNAME)
        matchToken(lexer, "&;", STATE_NCNAME, 0, 2, XmlSchemaDataTypeTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 2, 2, null)
    }

    @Test
    fun testNCName_CharRef_Octal() {
        val lexer = createLexer()

        lexer.start("One&#20;Two", 0, 11, STATE_NCNAME)
        matchToken(lexer, "One", STATE_NCNAME, 0, 3, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "&#20;", 2, 3, 8, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "Two", 2, 8, 11, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "", 2, 11, 11, null)

        lexer.start("&#", 0, 2, STATE_NCNAME)
        matchToken(lexer, "&#", STATE_NCNAME, 0, 2, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 2, 2, null)

        lexer.start("&# ", 0, 3, STATE_NCNAME)
        matchToken(lexer, "&#", STATE_NCNAME, 0, 2, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, " ", STATE_NCNAME, 2, 3, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "", STATE_NCNAME, 3, 3, null)

        lexer.start("&#12", 0, 4, STATE_NCNAME)
        matchToken(lexer, "&#12", STATE_NCNAME, 0, 4, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 4, 4, null)

        lexer.start("&#;", 0, 3, STATE_NCNAME)
        matchToken(lexer, "&#;", STATE_NCNAME, 0, 3, XmlSchemaDataTypeTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 3, 3, null)
    }

    @Test
    fun testNCName_CharRef_Hexadecimal() {
        val lexer = createLexer()

        lexer.start("One&#x20;&#xae;&#xDC;Two", 0, 24, STATE_NCNAME)
        matchToken(lexer, "One", STATE_NCNAME, 0, 3, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "&#x20;", 2, 3, 9, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "&#xae;", 2, 9, 15, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "&#xDC;", 2, 15, 21, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "Two", 2, 21, 24, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "", 2, 24, 24, null)

        lexer.start("&#x", 0, 3, STATE_NCNAME)
        matchToken(lexer, "&#x", STATE_NCNAME, 0, 3, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 3, 3, null)

        lexer.start("&#x ", 0, 4, STATE_NCNAME)
        matchToken(lexer, "&#x", STATE_NCNAME, 0, 3, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, " ", STATE_NCNAME, 3, 4, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "", STATE_NCNAME, 4, 4, null)

        lexer.start("&#x12", 0, 5, STATE_NCNAME)
        matchToken(lexer, "&#x12", STATE_NCNAME, 0, 5, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "", STATE_NCNAME, 5, 5, null)

        lexer.start("&#x;&#x2G;&#x2g;&#xg2;", 0, 22, STATE_NCNAME)
        matchToken(lexer, "&#x;", STATE_NCNAME, 0, 4, XmlSchemaDataTypeTokenType.EMPTY_ENTITY_REFERENCE)
        matchToken(lexer, "&#x2", STATE_NCNAME, 4, 8, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "G", STATE_NCNAME, 8, 9, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, ";", STATE_NCNAME, 9, 10, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "&#x2", 3, 10, 14, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "g", 3, 14, 15, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, ";", 3, 15, 16, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "&#x", 3, 16, 19, XmlSchemaDataTypeTokenType.PARTIAL_ENTITY_REFERENCE)
        matchToken(lexer, "g2", 3, 19, 21, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, ";", 3, 21, 22, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "", 3, 22, 22, null)
    }

    @Test
    fun testNCName_NCNames() {
        val lexer = createLexer()

        lexer.start("test x b2b F.G a-b g\u0330d", 0, 22, STATE_NCNAME)
        matchToken(lexer, "test", STATE_NCNAME, 0, 4, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, " ", STATE_NCNAME, 4, 5, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "x", STATE_NCNAME, 5, 6, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, " ", STATE_NCNAME, 6, 7, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "b2b", STATE_NCNAME, 7, 10, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, " ", STATE_NCNAME, 10, 11, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "F.G", STATE_NCNAME, 11, 14, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, " ", STATE_NCNAME, 14, 15, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "a-b", STATE_NCNAME, 15, 18, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, " ", STATE_NCNAME, 18, 19, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "g\u0330d", STATE_NCNAME, 19, 22, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "", STATE_NCNAME, 22, 22, null)
    }

    @Test
    fun testNCName_NCNameContinuationAfterNamedOrCharRef() {
        val lexer = createLexer()

        lexer.start("a&#x74;0&#x75;.&#x76;-&#x77;b c", 0, 31, STATE_NCNAME)
        matchToken(lexer, "a", STATE_NCNAME, 0, 1, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "&#x74;", 2, 1, 7, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "0", 2, 7, 8, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "&#x75;", 2, 8, 14, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, ".", 2, 14, 15, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "&#x76;", 2, 15, 21, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "-", 2, 21, 22, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "&#x77;", 2, 22, 28, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "b", 2, 28, 29, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, " ", 2, 29, 30, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "c", STATE_NCNAME, 30, 31, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "", STATE_NCNAME, 31, 31, null)
    }

    @Test
    fun testNCName_InvalidCharacter() {
        val lexer = createLexer()

        lexer.start("~&#x74;b&#x75;.&#x76;-&#x77;0 c", 0, 31, STATE_NCNAME)
        matchToken(lexer, "~", STATE_NCNAME, 0, 1, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "&#x74;", 3, 1, 7, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "b", 3, 7, 8, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "&#x75;", 3, 8, 14, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, ".", 3, 14, 15, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "&#x76;", 3, 15, 21, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "-", 3, 21, 22, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, "&#x77;", 3, 22, 28, XmlSchemaDataTypeTokenType.CHARACTER_REFERENCE)
        matchToken(lexer, "0", 3, 28, 29, XmlSchemaDataTypeTokenType.UNKNOWN)
        matchToken(lexer, " ", 3, 29, 30, XmlSchemaDataTypeTokenType.WHITE_SPACE)
        matchToken(lexer, "c", STATE_NCNAME, 30, 31, XmlSchemaDataTypeTokenType.NCNAME)
        matchToken(lexer, "", STATE_NCNAME, 31, 31, null)
    }

    // endregion
}
