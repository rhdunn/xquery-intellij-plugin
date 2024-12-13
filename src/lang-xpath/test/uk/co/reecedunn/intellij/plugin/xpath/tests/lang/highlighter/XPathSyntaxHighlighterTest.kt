/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.lang.highlighter

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xpath.lang.highlighter.XPathSyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.xpath.lang.highlighter.XPathSyntaxHighlighterColors
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@Suppress("Reformat")
@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - XPath SyntaxHighlighter")
class XPathSyntaxHighlighterTest {
    @Test
    @DisplayName("syntax highlighter factory")
    fun testFactory() {
        val highlighter = XPathSyntaxHighlighter.Factory().getSyntaxHighlighter(null, null)
        assertThat(highlighter.javaClass.name, `is`(XPathSyntaxHighlighter::class.java.name))
    }

    @Test
    @DisplayName("bad character")
    fun testTokenHighlights_BadCharacter() {
        val highlighter = XPathSyntaxHighlighter

        assertThat(highlighter.getTokenHighlights(XPathTokenType.BAD_CHARACTER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BAD_CHARACTER)[0], `is`(XPathSyntaxHighlighterColors.BAD_CHARACTER))
    }

    @Test
    @DisplayName("comment")
    fun testTokenHighlights_Comment() {
        val highlighter = XPathSyntaxHighlighter

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
        val highlighter = XPathSyntaxHighlighter

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
    @DisplayName("string")
    fun testTokenHighlights_String() {
        val highlighter = XPathSyntaxHighlighter

        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_START).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_START)[0], `is`(XPathSyntaxHighlighterColors.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_CONTENTS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_CONTENTS)[0], `is`(XPathSyntaxHighlighterColors.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_END).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_END)[0], `is`(XPathSyntaxHighlighterColors.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_START).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_START)[0], `is`(XPathSyntaxHighlighterColors.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_END).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_END)[0], `is`(XPathSyntaxHighlighterColors.STRING))
    }

    @Test
    @DisplayName("escaped character")
    fun testTokenHighlights_EscapedCharacter() {
        val highlighter = XPathSyntaxHighlighter

        assertThat(highlighter.getTokenHighlights(XPathTokenType.ESCAPED_CHARACTER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ESCAPED_CHARACTER)[0], `is`(XPathSyntaxHighlighterColors.ESCAPED_CHARACTER))
    }

    @Test
    @DisplayName("identifier")
    fun testTokenHighlights_Identifier() {
        val highlighter = XPathSyntaxHighlighter

        assertThat(highlighter.getTokenHighlights(XPathTokenType.NCNAME).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NCNAME)[0], `is`(XPathSyntaxHighlighterColors.IDENTIFIER))
    }

    @Test
    @DisplayName("keywords")
    fun testTokenHighlights_Keywords() {
        val highlighter = XPathSyntaxHighlighter

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ALL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ALL)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR_OR_SELF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR_OR_SELF)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AND).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AND)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANDALSO).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANDALSO)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANY)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ARRAY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ARRAY)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ARRAY_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ARRAY_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ATTRIBUTE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ATTRIBUTE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_BOOLEAN_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_BOOLEAN_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CASE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CAST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CAST)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CASTABLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CASTABLE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CHILD).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CHILD)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_COMMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_COMMENT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CONTAINS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CONTAINS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CONTENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CONTENT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DEFAULT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DEFAULT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT_OR_SELF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT_OR_SELF)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIACRITICS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIACRITICS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIFFERENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIFFERENT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DISTANCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DISTANCE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIV).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIV)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DOCUMENT_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DOCUMENT_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ELEMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ELEMENT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EMPTY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EMPTY)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EMPTY_SEQUENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EMPTY_SEQUENCE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_END).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_END)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ENTIRE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ENTIRE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ENUM).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ENUM)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EQ).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EQ)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EVERY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EVERY)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EXACTLY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EXACTLY)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EXCEPT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EXCEPT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING_SIBLING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING_SIBLING)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOR)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FROM).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FROM)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FTAND).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FTAND)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FTNOT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FTNOT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FTOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FTOR)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FUNCTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FUNCTION)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IDIV).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IDIV)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IN)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INSENSITIVE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INSENSITIVE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INSTANCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INSTANCE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INTERSECT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INTERSECT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ITEM).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ITEM)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LANGUAGE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LANGUAGE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LEAST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LEAST)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LET).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LET)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LEVELS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LEVELS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LOWERCASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LOWERCASE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MAP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MAP)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MEMBER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MEMBER)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MOD).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MOD)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MOST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MOST)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NAMESPACE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NAMESPACE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NAMESPACE_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NAMESPACE_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NO).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NO)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NOT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NOT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NULL_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NULL_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NUMBER_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NUMBER_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OBJECT_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OBJECT_NODE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OCCURS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OCCURS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OF)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OPTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OPTION)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OR)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ORDERED).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ORDERED)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ORELSE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ORELSE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OTHERWISE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OTHERWISE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARAGRAPH).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARAGRAPH)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARAGRAPHS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARAGRAPHS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARENT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PHRASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PHRASE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING_SIBLING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING_SIBLING)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PROCESSING_INSTRUCTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PROCESSING_INSTRUCTION)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PROPERTY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PROPERTY)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RECORD).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RECORD)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RELATIONSHIP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RELATIONSHIP)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RETURN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RETURN)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SAME).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SAME)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SATISFIES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SATISFIES)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SCHEMA_ATTRIBUTE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SCHEMA_ATTRIBUTE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SCHEMA_ELEMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SCHEMA_ELEMENT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SCORE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SCORE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SELF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SELF)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SENSITIVE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SENSITIVE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SENTENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SENTENCE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SENTENCES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SENTENCES)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SOME).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SOME)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_START).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_START)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_STEMMING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_STEMMING)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_STOP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_STOP)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TEXT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TEXT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_THEN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_THEN)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_THESAURUS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_THESAURUS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TIMES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TIMES)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TO).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TO)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TREAT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TREAT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TYPE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TYPE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_UNION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_UNION)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_UPPERCASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_UPPERCASE)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_USING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_USING)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WEIGHT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WEIGHT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WILDCARDS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WILDCARDS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WINDOW).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WINDOW)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WITH).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WITH)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WITHOUT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WITHOUT)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WORD).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WORD)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WORDS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_WORDS)[0], `is`(XPathSyntaxHighlighterColors.KEYWORD))
    }

    @Test
    @DisplayName("other token")
    fun testTokenHighlights_OtherToken() {
        val highlighter = XPathSyntaxHighlighter

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
        assertThat(highlighter.getTokenHighlights(XPathTokenType.SQUARE_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.SQUARE_CLOSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARENT_SELECTOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NODE_BEFORE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NODE_AFTER).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.PRAGMA_BEGIN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PRAGMA_CONTENTS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PRAGMA_END).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.CONCATENATION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.MAP_OPERATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.FUNCTION_REF_OPERATOR).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.ARROW).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.THIN_ARROW).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.CONTEXT_FUNCTION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ELLIPSIS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ELVIS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.LAMBDA_FUNCTION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.TERNARY_ELSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.TERNARY_IF).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.TYPE_ALIAS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K__).size, `is`(0)) // Saxon 10.0 lambda function.
    }

    @Test
    @DisplayName("attribute")
    fun testTokenHighlights_Attribute() {
        val highlighter = XPathSyntaxHighlighter

        assertThat(highlighter.getTokenHighlights(XPathTokenType.ATTRIBUTE_SELECTOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ATTRIBUTE_SELECTOR)[0], `is`(XPathSyntaxHighlighterColors.ATTRIBUTE))
    }
}
