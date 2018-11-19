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
package uk.co.reecedunn.intellij.plugin.intellij.tests.lexer

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xqdoc.lexer.XQDocTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lexer.SyntaxHighlighter
import uk.co.reecedunn.intellij.plugin.intellij.lexer.SyntaxHighlighterFactory
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("IntelliJ - Custom Language Support - Syntax Highlighting - SyntaxHighlighter")
class SyntaxHighlighterTest {
    @Test
    @DisplayName("syntax highlighter factory")
    fun testFactory() {
        val factory = SyntaxHighlighterFactory()
        val highlighter = factory.getSyntaxHighlighter(null, null)
        assertThat(highlighter.javaClass.name, `is`(SyntaxHighlighter::class.java.name))
    }

    @Test
    @DisplayName("bad character")
    fun testTokenHighlights_BadCharacter() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.BAD_CHARACTER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BAD_CHARACTER)[0], `is`(SyntaxHighlighter.BAD_CHARACTER))
    }

    @Test
    @DisplayName("comment")
    fun testTokenHighlights_Comment() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_START_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_START_TAG)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_END_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMENT_END_TAG)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_START_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_START_TAG)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_END_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_COMMENT_END_TAG)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XQDOC_COMMENT_MARKER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XQDOC_COMMENT_MARKER)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CONTENTS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CONTENTS)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TRIM).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TRIM)[0], `is`(SyntaxHighlighter.COMMENT))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ELEMENT_CONTENTS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ELEMENT_CONTENTS)[0], `is`(SyntaxHighlighter.COMMENT))
    }

    @Test
    @DisplayName("number")
    fun testTokenHighlights_Number() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.INTEGER_LITERAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.INTEGER_LITERAL)[0], `is`(SyntaxHighlighter.NUMBER))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.DECIMAL_LITERAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.DECIMAL_LITERAL)[0], `is`(SyntaxHighlighter.NUMBER))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.DOUBLE_LITERAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.DOUBLE_LITERAL)[0], `is`(SyntaxHighlighter.NUMBER))

        // NOTE: This token is for the parser, so that a parser error will be emitted for incomplete double literals.
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)[0], `is`(SyntaxHighlighter.NUMBER))
    }

    @Test
    @DisplayName("string")
    fun testTokenHighlights_String() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_START).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_START)[0], `is`(SyntaxHighlighter.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_CONTENTS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_CONTENTS)[0], `is`(SyntaxHighlighter.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_END).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STRING_LITERAL_END)[0], `is`(SyntaxHighlighter.STRING))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_CONSTRUCTOR_START).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_CONSTRUCTOR_START)[0], `is`(SyntaxHighlighter.STRING))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)[0], `is`(SyntaxHighlighter.STRING))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_CONSTRUCTOR_END).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_CONSTRUCTOR_END)[0], `is`(SyntaxHighlighter.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_START).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_START)[0], `is`(SyntaxHighlighter.STRING))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_END).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.BRACED_URI_LITERAL_END)[0], `is`(SyntaxHighlighter.STRING))
    }

    @Test
    @DisplayName("escaped character")
    fun testTokenHighlights_EscapedCharacter() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.ESCAPED_CHARACTER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ESCAPED_CHARACTER)[0], `is`(SyntaxHighlighter.ESCAPED_CHARACTER))
    }

    @Test
    @DisplayName("entity reference")
    fun testTokenHighlights_EntityReference() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.ENTITY_REFERENCE))

        // NOTE: This token is for the parser, so that a parser error will be emitted for invalid entity references.
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CHARACTER_REFERENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CHARACTER_REFERENCE)[0], `is`(SyntaxHighlighter.ENTITY_REFERENCE))

        // NOTE: This token is for the parser, so that a parser error will be emitted for invalid entity references.
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARTIAL_ENTITY_REFERENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PARTIAL_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.ENTITY_REFERENCE))

        // NOTE: This token is for the parser, so that a parser error will be emitted for invalid entity references.
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.EMPTY_ENTITY_REFERENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.EMPTY_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.ENTITY_REFERENCE))
    }

    @Test
    @DisplayName("identifier")
    fun testTokenHighlights_Identifier() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XPathTokenType.NCNAME).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NCNAME)[0], `is`(SyntaxHighlighter.IDENTIFIER))
    }

    @Test
    @DisplayName("keywords")
    fun testTokenHighlights_Keywords() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AFTER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AFTER)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ALL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ALL)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ALLOWING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ALLOWING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR_OR_SELF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ANCESTOR_OR_SELF)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AND).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AND)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANDALSO).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANDALSO)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ANY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ARRAY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ARRAY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ARRAY_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ARRAY_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_AS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ASCENDING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ASCENDING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ASSIGNABLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ASSIGNABLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_AT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ATTRIBUTE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_ATTRIBUTE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ATTRIBUTE_DECL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ATTRIBUTE_DECL)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BASE_URI).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BASE_URI)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BEFORE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BEFORE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BINARY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BINARY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BLOCK).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BLOCK)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BOOLEAN_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BOOLEAN_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BOUNDARY_SPACE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BOUNDARY_SPACE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_BY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CASE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CAST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CAST)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CASTABLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CASTABLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CATCH).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CATCH)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CHILD).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_CHILD)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COLLATION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COLLATION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COMMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COMMENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COMPLEX_TYPE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COMPLEX_TYPE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONSTRUCTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONSTRUCTION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONTAINS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONTAINS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONTENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONTENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONTEXT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_CONTEXT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COPY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COPY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COPY_NAMESPACES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COPY_NAMESPACES)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COUNT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_COUNT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECIMAL_FORMAT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECIMAL_FORMAT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECIMAL_SEPARATOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECIMAL_SEPARATOR)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECLARE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DECLARE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DEFAULT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DEFAULT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DELETE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DELETE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT_OR_SELF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DESCENDANT_OR_SELF)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DESCENDING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIACRITICS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIACRITICS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIFFERENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIFFERENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIGIT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DIGIT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DISTANCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DISTANCE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIV).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_DIV)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_DOCUMENT_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ELEMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ELEMENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ELEMENT_DECL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ELEMENT_DECL)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY_SEQUENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EMPTY_SEQUENCE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ENCODING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ENCODING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_END).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_END)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ENTIRE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ENTIRE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EQ).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EQ)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EVERY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EVERY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXACTLY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXACTLY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EXCEPT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_EXCEPT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXIT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXIT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXTERNAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_EXTERNAL)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FIRST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FIRST)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FN)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING_SIBLING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOLLOWING_SIBLING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_FOR)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FROM).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FROM)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FT_OPTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FT_OPTION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FTAND).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FTAND)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FTNOT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FTNOT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FTOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FTOR)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FUNCTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FUNCTION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FUZZY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_FUZZY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GREATEST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GREATEST)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GROUP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GROUP)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GROUPING_SEPARATOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_GROUPING_SEPARATOR)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_GT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IDIV).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IDIV)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IMPORT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_IMPORT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IN)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INFINITY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INFINITY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INHERIT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INHERIT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INSENSITIVE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INSENSITIVE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INSERT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INSERT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INSTANCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INSTANCE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INTERSECT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_INTERSECT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INTO).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INTO)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INVOKE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_INVOKE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_IS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ITEM).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ITEM)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LANGUAGE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LANGUAGE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LAST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LAST)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LAX).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LAX)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LEAST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LEAST)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LET).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LET)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LEVELS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LEVELS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LOWERCASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_LOWERCASE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_LT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MAP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MAP)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MINUS_SIGN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MINUS_SIGN)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MOD).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_MOD)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MODIFY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MODIFY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MODULE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MODULE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MOST).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_MOST)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NAMESPACE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NAMESPACE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NAMESPACE_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NAMESPACE_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NAN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NAN)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_NE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NEXT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NEXT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_INHERIT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_INHERIT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_PRESERVE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NO_PRESERVE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NODES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NODES)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NON_DETERMINISTIC).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NON_DETERMINISTIC)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NOT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NOT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NULL_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NULL_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NUMBER_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_NUMBER_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OBJECT_NODE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OBJECT_NODE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OCCURS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OCCURS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OF)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ONLY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ONLY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OPTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_OPTION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_OR)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDER).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDER)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERED).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERED)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORDERING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORELSE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ORELSE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PARAGRAPH).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PARAGRAPH)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PARAGRAPHS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PARAGRAPHS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PARENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PATTERN_SEPARATOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PATTERN_SEPARATOR)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PER_MILLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PER_MILLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PERCENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PERCENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PHRASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PHRASE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING_SIBLING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_PRECEDING_SIBLING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRESERVE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRESERVE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PREVIOUS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PREVIOUS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PROCESSING_INSTRUCTION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PROCESSING_INSTRUCTION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PROPERTY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PROPERTY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RELATIONSHIP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RELATIONSHIP)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RENAME).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RENAME)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_REPLACE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_REPLACE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RETURN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_RETURN)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RETURNING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_RETURNING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_REVALIDATION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_REVALIDATION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SAME).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SAME)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SATISFIES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SATISFIES)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ATTRIBUTE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ATTRIBUTE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_COMPONENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_COMPONENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ELEMENT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ELEMENT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_FACET).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_FACET)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_PARTICLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_PARTICLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ROOT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_ROOT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_TYPE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCHEMA_TYPE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCORE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SCORE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SELF).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SELF)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SENSITIVE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SENSITIVE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SENTENCE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SENTENCE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SENTENCES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SENTENCES)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SIMPLE_TYPE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SIMPLE_TYPE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SKIP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SKIP)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SLIDING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SLIDING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SOME).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_SOME)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STABLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STABLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_START).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_START)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STEMMING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STEMMING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STOP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STOP)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRICT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRICT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRIP).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STRIP)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STYLESHEET).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_STYLESHEET)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SWITCH).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SWITCH)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TEXT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TEXT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_THEN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_THEN)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_THESAURUS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_THESAURUS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TIMES).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TIMES)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TO).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TO)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TRANSFORM).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TRANSFORM)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TREAT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_TREAT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TRY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TRY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TUMBLING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TUMBLING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TUPLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TUPLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TYPE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TYPE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TYPESWITCH).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_TYPESWITCH)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNASSIGNABLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNASSIGNABLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_UNION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.K_UNION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNORDERED).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UNORDERED)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UPDATE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UPDATE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UPPERCASE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UPPERCASE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_USING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_USING)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VALIDATE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VALIDATE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VALUE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VALUE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VARIABLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VARIABLE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VERSION).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_VERSION)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WEIGHT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WEIGHT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHEN).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHEN)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHERE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHERE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHILE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WHILE)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WILDCARDS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WILDCARDS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WINDOW).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WINDOW)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WITH).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WITH)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WITHOUT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WITHOUT)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WORD).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WORD)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WORDS).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_WORDS)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_XQUERY).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_XQUERY)[0], `is`(SyntaxHighlighter.KEYWORD))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ZERO_DIGIT).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_ZERO_DIGIT)[0], `is`(SyntaxHighlighter.KEYWORD))
    }

    @Test
    @DisplayName("annotation")
    fun testTokenHighlights_Annotation() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ANNOTATION_INDICATOR).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ANNOTATION_INDICATOR)[0], `is`(SyntaxHighlighter.ANNOTATION))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PUBLIC).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PUBLIC)[0], `is`(SyntaxHighlighter.ANNOTATION))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRIVATE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_PRIVATE)[0], `is`(SyntaxHighlighter.ANNOTATION))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SEQUENTIAL).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SEQUENTIAL)[0], `is`(SyntaxHighlighter.ANNOTATION))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SIMPLE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_SIMPLE)[0], `is`(SyntaxHighlighter.ANNOTATION))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UPDATING).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.K_UPDATING)[0], `is`(SyntaxHighlighter.ANNOTATION))
    }

    @Test
    @DisplayName("xml tag")
    fun testTokenHighlights_XmlTag() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.OPEN_XML_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.OPEN_XML_TAG)[0], `is`(SyntaxHighlighter.XML_TAG))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.END_XML_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.END_XML_TAG)[0], `is`(SyntaxHighlighter.XML_TAG))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CLOSE_XML_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CLOSE_XML_TAG)[0], `is`(SyntaxHighlighter.XML_TAG))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.SELF_CLOSING_XML_TAG).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.SELF_CLOSING_XML_TAG)[0], `is`(SyntaxHighlighter.XML_TAG))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_WHITE_SPACE).size, `is`(1))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_WHITE_SPACE)[0], `is`(SyntaxHighlighter.XML_TAG))
    }

    @Test
    @DisplayName("xml tag name")
    fun testTokenHighlights_XmlTagName() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_TAG_NCNAME).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_TAG_NCNAME)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_TAG_NCNAME)[1], `is`(SyntaxHighlighter.XML_TAG_NAME))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_TAG_QNAME_SEPARATOR).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_TAG_QNAME_SEPARATOR)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_TAG_QNAME_SEPARATOR)[1], `is`(SyntaxHighlighter.XML_TAG_NAME))
    }

    @Test
    @DisplayName("xml attribute name")
    fun testTokenHighlights_XmlAttributeName() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_NCNAME).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_NCNAME)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_NCNAME)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_NAME))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_NAME))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EQUAL).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EQUAL)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EQUAL)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_NAME))
    }

    @Test
    @DisplayName("xml attribute value")
    fun testTokenHighlights_XmlAttributeValue() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_START).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_START)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_START)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_VALUE))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_VALUE))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_END).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_END)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ATTRIBUTE_VALUE_END)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_VALUE))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_VALUE))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)[1], `is`(SyntaxHighlighter.XML_ATTRIBUTE_VALUE))
    }

    @Test
    @DisplayName("xml escaped character")
    fun testTokenHighlights_XmlEscapedCharacter() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ESCAPED_CHARACTER).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ESCAPED_CHARACTER)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ESCAPED_CHARACTER)[1], `is`(SyntaxHighlighter.XML_ESCAPED_CHARACTER))
    }

    @Test
    @DisplayName("xml entity reference")
    fun testTokenHighlights_XmlEntityReference() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)[1], `is`(SyntaxHighlighter.XML_ENTITY_REFERENCE))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_CHARACTER_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_CHARACTER_REFERENCE)[0], `is`(SyntaxHighlighter.XML_TAG))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_CHARACTER_REFERENCE)[1], `is`(SyntaxHighlighter.XML_ENTITY_REFERENCE))
    }

    @Test
    @DisplayName("other token")
    fun testTokenHighlights_OtherToken() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.INVALID).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CDATA_SECTION_START_TAG).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CDATA_SECTION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CDATA_SECTION_END_TAG).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PRAGMA_BEGIN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PRAGMA_CONTENTS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PRAGMA_END).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PROCESSING_INSTRUCTION_END).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.XML_ELEMENT_CONTENTS).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XPathTokenType.NOT_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.VARIABLE_INDICATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARENTHESIS_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARENTHESIS_CLOSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.STAR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PLUS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.COMMA).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.MINUS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.DOT).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.BLOCK_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.BLOCK_CLOSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.SEPARATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.LESS_THAN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.GREATER_THAN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.LESS_THAN_OR_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.GREATER_THAN_OR_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.UNION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.OPTIONAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.AXIS_SEPARATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.QNAME_SEPARATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ASSIGN_EQUAL).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.DIRECT_DESCENDANTS_PATH).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ALL_DESCENDANTS_PATH).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.ATTRIBUTE_SELECTOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.SQUARE_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.SQUARE_CLOSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.PARENT_SELECTOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NODE_BEFORE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XPathTokenType.NODE_AFTER).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.CONCATENATION).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.MAP_OPERATOR).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.FUNCTION_REF_OPERATOR).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ARROW).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_INTERPOLATION_OPEN).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.STRING_INTERPOLATION_CLOSE).size, `is`(0))

        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ELLIPSIS).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.TERNARY_IF).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.TERNARY_ELSE).size, `is`(0))
        assertThat(highlighter.getTokenHighlights(XQueryTokenType.ELVIS).size, `is`(0))
    }

    @Test
    @DisplayName("xqdoc tag")
    fun testTokenHighlights_XQDocTag() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TAG_MARKER).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TAG_MARKER)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TAG_MARKER)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TAG).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TAG)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.TAG)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_AUTHOR).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_AUTHOR)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_AUTHOR)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_DEPRECATED).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_DEPRECATED)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_DEPRECATED)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_ERROR).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_ERROR)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_ERROR)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_PARAM).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_PARAM)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_PARAM)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_RETURN).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_RETURN)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_RETURN)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_SEE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_SEE)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_SEE)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_SINCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_SINCE)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_SINCE)[1], `is`(SyntaxHighlighter.XQDOC_TAG))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_VERSION).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_VERSION)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.T_VERSION)[1], `is`(SyntaxHighlighter.XQDOC_TAG))
    }

    @Test
    @DisplayName("xqdoc tag value")
    fun testTokenHighlights_XQDocTagValue() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.VARIABLE_INDICATOR).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.VARIABLE_INDICATOR)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.VARIABLE_INDICATOR)[1], `is`(SyntaxHighlighter.XQDOC_TAG_VALUE))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.NCNAME).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.NCNAME)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.NCNAME)[1], `is`(SyntaxHighlighter.XQDOC_TAG_VALUE))
    }

    @Test
    @DisplayName("xqdoc xml markup")
    fun testTokenHighlights_XQDocMarkup() {
        val highlighter = SyntaxHighlighter()

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.OPEN_XML_TAG).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.OPEN_XML_TAG)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.OPEN_XML_TAG)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.END_XML_TAG).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.END_XML_TAG)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.END_XML_TAG)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CLOSE_XML_TAG).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CLOSE_XML_TAG)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CLOSE_XML_TAG)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.SELF_CLOSING_XML_TAG).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.SELF_CLOSING_XML_TAG)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.SELF_CLOSING_XML_TAG)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_TAG).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_TAG)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_TAG)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_EQUAL).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_EQUAL)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_EQUAL)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_START).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_START)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_START)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_END).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_END)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.XML_ATTRIBUTE_VALUE_END)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.INVALID).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.INVALID)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.INVALID)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.PREDEFINED_ENTITY_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.PREDEFINED_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.PREDEFINED_ENTITY_REFERENCE)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.PARTIAL_ENTITY_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.PARTIAL_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.PARTIAL_ENTITY_REFERENCE)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.EMPTY_ENTITY_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.EMPTY_ENTITY_REFERENCE)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.EMPTY_ENTITY_REFERENCE)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))

        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CHARACTER_REFERENCE).size, `is`(2))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CHARACTER_REFERENCE)[0], `is`(SyntaxHighlighter.COMMENT))
        assertThat(highlighter.getTokenHighlights(XQDocTokenType.CHARACTER_REFERENCE)[1], `is`(SyntaxHighlighter.XQDOC_MARKUP))
    }
}
