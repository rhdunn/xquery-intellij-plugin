/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocLexer
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_XQUERY_COMMENT
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryLexerWithXQDocTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQDocLexer(), 0x70000000, STATE_XQUERY_COMMENT, XQueryTokenType.COMMENT)
        return lexer
    }

    @Test
    fun testXQueryTokens() {
        val lexer = createLexer()

        matchSingleToken(lexer, "xquery", XQueryTokenType.K_XQUERY)
        matchSingleToken(lexer, "version", XQueryTokenType.K_VERSION)
        matchSingleToken(lexer, "encoding", XQueryTokenType.K_ENCODING)
    }

    @Test
    fun testXQueryComment() {
        val lexer = createLexer()

        lexer.start("(: Test :)")
        matchToken(lexer, "(:", 0x00000000 or 0, 0, 2, XQueryTokenType.COMMENT_START_TAG)
        matchToken(lexer, " Test ", 0x70000000 or 0, 2, 8, XQDocTokenType.COMMENT_CONTENTS)
        matchToken(lexer, ":)", 0x00000000 or 4, 8, 10, XQueryTokenType.COMMENT_END_TAG)
        matchToken(lexer, "", 0x00000000 or 0, 10, 10, null)
    }

    @Test
    fun testXQDocComment() {
        val lexer = createLexer()

        lexer.start("(:~\n@xqdoc comment:)")
        matchToken(lexer, "(:", 0x00000000 or 0, 0, 2, XQueryTokenType.COMMENT_START_TAG)
        matchToken(lexer, "~", 0x70000000 or 0, 2, 3, XQDocTokenType.XQDOC_COMMENT_MARKER)
        matchToken(lexer, "\n", 0x70000000 or 8, 3, 4, XQDocTokenType.TRIM)
        matchToken(lexer, "@", 0x70000000 or 8, 4, 5, XQDocTokenType.TAG_MARKER)
        matchToken(lexer, "xqdoc", 0x70000000 or 2, 5, 10, XQDocTokenType.TAG)
        matchToken(lexer, " ", 0x70000000 or 2, 10, 11, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "comment", 0x70000000 or 1, 11, 18, XQDocTokenType.CONTENTS)
        matchToken(lexer, ":)", 0x00000000 or 4, 18, 20, XQueryTokenType.COMMENT_END_TAG)
        matchToken(lexer, "", 0x00000000 or 0, 20, 20, null)
    }

    @Test
    fun testXQueryStateRestore() {
        val lexer = createLexer()

        lexer.start("Q{Hello World}", 2, 14, 0x00000000 or 26)
        matchToken(lexer, "Hello World", 26, 2, 13, XQueryTokenType.STRING_LITERAL_CONTENTS)
        matchToken(lexer, "}", 26, 13, 14, XQueryTokenType.BRACED_URI_LITERAL_END)
        matchToken(lexer, "", 0, 14, 14, null)
    }

    @Test
    fun testXQDocStateRestore() {
        val lexer = createLexer()

        lexer.start("(:~\n@xqdoc comment:)", 5, 20, 0x70000000 or 2)
        matchToken(lexer, "xqdoc", 0x70000000 or 2, 5, 10, XQDocTokenType.TAG)
        matchToken(lexer, " ", 0x70000000 or 2, 10, 11, XQDocTokenType.WHITE_SPACE)
        matchToken(lexer, "comment", 0x70000000 or 1, 11, 18, XQDocTokenType.CONTENTS)
        matchToken(lexer, ":)", 0x00000000 or 4, 18, 20, XQueryTokenType.COMMENT_END_TAG)
        matchToken(lexer, "", 0x00000000 or 0, 20, 20, null)
    }
}
