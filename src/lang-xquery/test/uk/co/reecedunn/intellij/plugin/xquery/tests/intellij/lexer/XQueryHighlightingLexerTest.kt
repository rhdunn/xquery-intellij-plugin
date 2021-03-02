/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.intellij.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCaseEx
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lexer.XQuerySyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XQuery Highlighting Lexer")
class XQueryHighlightingLexerTest : LexerTestCaseEx() {
    override val lexer: Lexer = XQuerySyntaxHighlighter.highlightingLexer

    @Test
    @DisplayName("xquery tokens")
    fun xqueryTokens() {
        token("xquery", XQueryTokenType.K_XQUERY)
        token("version", XQueryTokenType.K_VERSION)
        token("encoding", XQueryTokenType.K_ENCODING)
    }

    @Test
    @DisplayName("xquery comment")
    fun xqueryComment() = tokenize("(: Test :)") {
        token("(:", XPathTokenType.COMMENT_START_TAG)
        state(0x70000000 or 12)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(0x70000000 or 11)
        token("Test ", XQDocTokenType.CONTENTS)
        state(4)
        token(":)", XPathTokenType.COMMENT_END_TAG)
        state(0)
    }

    @Test
    @DisplayName("xqDoc comment")
    fun xqdocComment() = tokenize("(:~\n@xqdoc comment:)") {
        token("(:", XPathTokenType.COMMENT_START_TAG)
        state(0x70000000 or 0)
        token("~",XQDocTokenType.XQDOC_COMMENT_MARKER)
        state(0x70000000 or 8)
        token("\n",  XQDocTokenType.TRIM)
        token("@", XQDocTokenType.TAG_MARKER)
        state(0x70000000 or 2)
        token("xqdoc", XQDocTokenType.TAG)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(0x70000000 or 1)
        token("comment", XQDocTokenType.CONTENTS)
        state(4)
        token(":)", XPathTokenType.COMMENT_END_TAG)
        state(0)
    }

    @Test
    @DisplayName("state restore to the xquery lexer")
    fun xqueryStateRestore() = tokenize("Q{Hello World}", 2, 14, 26) {
        token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
        token("}", XPathTokenType.BRACED_URI_LITERAL_END)
        state(0)
    }

    @Test
    @DisplayName("state restore to the xqdoc lexer")
    fun xqdocStateRestore() = tokenize("(:~\n@xqdoc comment:)", 5, 20, 0x70000000 or 2) {
        token("xqdoc", XQDocTokenType.TAG)
        token(" ", XQDocTokenType.WHITE_SPACE)
        state(0x70000000 or 1)
        token("comment", XQDocTokenType.CONTENTS)
        state(4)
        token(":)", XPathTokenType.COMMENT_END_TAG)
        state(0)
    }
}
