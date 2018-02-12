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

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xdm.lexer.STATE_NCNAME
import uk.co.reecedunn.intellij.plugin.xdm.lexer.XmlSchemaDataTypeLexer
import uk.co.reecedunn.intellij.plugin.xdm.lexer.XmlSchemaDataTypeTokenType

class XmlSchemaDataTypeLexerTest : LexerTestCase() {
    // region Lexer :: Invalid State

    fun testInvalidState() {
        val lexer = XmlSchemaDataTypeLexer()

        val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, -1) }
        assertThat(e.message, `is`("Invalid state: -1"))
    }

    // endregion
    // region NCName

    fun testNCName_EmptyBuffer() {
        val lexer = XmlSchemaDataTypeLexer()

        lexer.start("", 0, 0, STATE_NCNAME)
        matchToken(lexer, "", STATE_NCNAME, 0, 0, null)
    }

    fun testNCName_WhiteSpace() {
        val lexer = XmlSchemaDataTypeLexer()

        lexer.start(" \t\r\n", 0, 4, STATE_NCNAME)
        matchToken(lexer, " \t\r\n", STATE_NCNAME, 0, 4, XmlSchemaDataTypeTokenType.WHITE_SPACE)
    }

    fun testNCName_NCNames() {
        val lexer = XmlSchemaDataTypeLexer()

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

    // endregion
}
