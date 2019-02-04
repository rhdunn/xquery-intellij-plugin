/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.lexer

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XPath SyntaxHighlighter")
class XPathSyntaxHighlighterTest {
    @Test
    @DisplayName("syntax highlighter factory")
    fun testFactory() {
        val factory = XPathSyntaxHighlighterFactory()
        val highlighter = factory.getSyntaxHighlighter(null, null)
        assertThat(highlighter.javaClass.name, `is`(XPathSyntaxHighlighter::class.java.name))
    }

    @Test
    @DisplayName("bad character")
    fun testTokenHighlights_BadCharacter() {
        val highlighter = XPathSyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.BAD_CHARACTER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BAD_CHARACTER)[0], `is`(XPathSyntaxHighlighterColors.BAD_CHARACTER))
    }

    @Test
    @DisplayName("comment")
    fun testTokenHighlights_Comment() {
        val highlighter = XPathSyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_START_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_START_TAG)[0], `is`(XPathSyntaxHighlighterColors.COMMENT))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT)[0], `is`(XPathSyntaxHighlighterColors.COMMENT))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_END_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_END_TAG)[0], `is`(XPathSyntaxHighlighterColors.COMMENT))
    }

    @Test
    @DisplayName("number")
    fun testTokenHighlights_Number() {
        val highlighter = XPathSyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.INTEGER_LITERAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.INTEGER_LITERAL)[0], `is`(XPathSyntaxHighlighterColors.NUMBER))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.DECIMAL_LITERAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.DECIMAL_LITERAL)[0], `is`(XPathSyntaxHighlighterColors.NUMBER))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.DOUBLE_LITERAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.DOUBLE_LITERAL)[0], `is`(XPathSyntaxHighlighterColors.NUMBER))

        // NOTE: This token is for the parser, so that a parser error will be emitted for incomplete double literals.
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)[0], `is`(XPathSyntaxHighlighterColors.NUMBER))
    }

    @Test
    @DisplayName("other token")
    fun testTokenHighlights_OtherToken() {
        val highlighter = XPathSyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.NOT_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.VARIABLE_INDICATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARENTHESIS_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARENTHESIS_CLOSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STAR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PLUS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMA).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.MINUS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.DOT).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BLOCK_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BLOCK_CLOSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.LESS_THAN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.GREATER_THAN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.LESS_THAN_OR_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.GREATER_THAN_OR_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.UNION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.OPTIONAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.AXIS_SEPARATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.QNAME_SEPARATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ASSIGN_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.DIRECT_DESCENDANTS_PATH).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ALL_DESCENDANTS_PATH).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ATTRIBUTE_SELECTOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.SQUARE_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.SQUARE_CLOSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARENT_SELECTOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NODE_BEFORE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NODE_AFTER).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.CONCATENATION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.MAP_OPERATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.FUNCTION_REF_OPERATOR).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.ARROW).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.ELLIPSIS).size, `is`(0))
    }
}
