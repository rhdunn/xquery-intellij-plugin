/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.xslt.lang.ValueTemplate
import uk.co.reecedunn.intellij.plugin.xslt.lexer.XsltValueTemplateLexer

@DisplayName("XSLT 3.0 - Lexer - Value Templates")
class XsltValueTemplateLexerTest : LexerTestCase() {
    override val lexer: Lexer = XsltValueTemplateLexer(XmlCodePointRangeImpl())

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XSLT EBNF (1) ValueTemplate")
    internal inner class ValueTemplateTest {
        @Test
        @DisplayName("escaped braces")
        fun escapedBraces() = tokenize("Lorem {{ipsum}} dolor.") {
            token("Lorem ", ValueTemplate.VALUE_CONTENTS)
            token("{{", ValueTemplate.ESCAPED_CHARACTER)
            token("ipsum", ValueTemplate.VALUE_CONTENTS)
            token("}}", ValueTemplate.ESCAPED_CHARACTER)
            token(" dolor.", ValueTemplate.VALUE_CONTENTS)
        }

        @Test
        @DisplayName("unpaired right brace")
        fun unpairedRightBrace() = tokenize("Lorem } ipsum") {
            token("Lorem ", ValueTemplate.VALUE_CONTENTS)
            token("}", XPathTokenType.BLOCK_CLOSE)
            token(" ipsum", ValueTemplate.VALUE_CONTENTS)
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XSLT EBNF (2) ValueContentChar")
    fun valueContentChar() {
        token("Lorem ipsum dolor.", ValueTemplate.VALUE_CONTENTS)
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (5) EnclosedExpr")
    internal inner class EnclosedExpr {
        @Test
        @DisplayName("enclosed expression")
        fun enclosedExpr() = tokenize("One {2} Three") {
            token("One ", ValueTemplate.VALUE_CONTENTS)
            token("{", XPathTokenType.BLOCK_OPEN)
            state(32)
            token("2", XPathTokenType.INTEGER_LITERAL)
            token("}", XPathTokenType.BLOCK_CLOSE)
            state(0)
            token(" Three", ValueTemplate.VALUE_CONTENTS)
        }

        @Test
        @DisplayName("nested")
        fun nested() = tokenize("One {2{3}4} Five") {
            token("One ", ValueTemplate.VALUE_CONTENTS)
            token("{", XPathTokenType.BLOCK_OPEN)
            state(32)
            token("2", XPathTokenType.INTEGER_LITERAL)
            token("{", XPathTokenType.BLOCK_OPEN)
            token("3", XPathTokenType.INTEGER_LITERAL)
            token("}", XPathTokenType.BLOCK_CLOSE)
            token("4", XPathTokenType.INTEGER_LITERAL)
            token("}", XPathTokenType.BLOCK_CLOSE)
            state(0)
            token(" Five", ValueTemplate.VALUE_CONTENTS)
        }
    }
}
