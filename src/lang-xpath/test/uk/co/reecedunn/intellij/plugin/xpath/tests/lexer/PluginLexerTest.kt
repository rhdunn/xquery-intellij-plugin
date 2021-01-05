/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.lexer

import com.intellij.lexer.Lexer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XmlCodePointRangeImpl
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@DisplayName("XQuery IntelliJ Plugin - Lexer")
class PluginLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer = XPathLexer(XmlCodePointRangeImpl())

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (20) AndExpr")
    fun andExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "and", XPathTokenType.K_AND)
        matchSingleToken(lexer, "andAlso", XPathTokenType.K_ANDALSO)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (25) TupleType")
    fun tupleType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "tuple", XPathTokenType.K_TUPLE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (26) FieldDeclaration")
    fun fieldDeclaration() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?", XPathTokenType.OPTIONAL)
        matchSingleToken(lexer, "as", XPathTokenType.K_AS)

        matchSingleToken(lexer, "?:", XPathTokenType.ELVIS) // compact whitespace -- BaseX 9.1 (XQuery) extension
        matchSingleToken(lexer, ":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (25) ForwardAxis")
    fun forwardAxis() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute", XPathTokenType.K_ATTRIBUTE)
        matchSingleToken(lexer, "child", XPathTokenType.K_CHILD)
        matchSingleToken(lexer, "descendant", XPathTokenType.K_DESCENDANT)
        matchSingleToken(lexer, "descendant-or-self", XPathTokenType.K_DESCENDANT_OR_SELF)
        matchSingleToken(lexer, "following", XPathTokenType.K_FOLLOWING)
        matchSingleToken(lexer, "following-sibling", XPathTokenType.K_FOLLOWING_SIBLING)
        matchSingleToken(lexer, "namespace", XPathTokenType.K_NAMESPACE) // XPath and MarkLogic
        matchSingleToken(lexer, "property", XPathTokenType.K_PROPERTY) // MarkLogic
        matchSingleToken(lexer, "self", XPathTokenType.K_SELF)
        matchSingleToken(lexer, "::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
    fun anyBooleanNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
    fun namedBooleanNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
    fun anyNumberNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "number-node", XPathTokenType.K_NUMBER_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
    fun namedNumberNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "number-node", XPathTokenType.K_NUMBER_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
    fun anyNullNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "null-node", XPathTokenType.K_NULL_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
    fun namedNullNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "null-node", XPathTokenType.K_NULL_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
    fun anyArrayNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array-node", XPathTokenType.K_ARRAY_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
    fun namedArrayNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array-node", XPathTokenType.K_ARRAY_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
    fun anyMapNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "object-node", XPathTokenType.K_OBJECT_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
    fun namedMapNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "object-node", XPathTokenType.K_OBJECT_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    fun sequenceType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "empty-sequence", XPathTokenType.K_EMPTY_SEQUENCE)
        matchSingleToken(lexer, "empty", XPathTokenType.K_EMPTY)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (19) OrExpr")
    fun orExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "or", XPathTokenType.K_OR)
        matchSingleToken(lexer, "orElse", XPathTokenType.K_ORELSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (24) ContextItemFunctionExpr")
    fun contextItemFunctionExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "fn", XPathTokenType.K_FN)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)

        matchSingleToken(lexer, ".{", XPathTokenType.CONTEXT_FUNCTION)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)

        matchSingleToken(lexer, ".", XPathTokenType.DOT)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (22) ParamList")
    fun paramList() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, "...", XPathTokenType.ELLIPSIS)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (34) TypeAlias")
    fun typeAlias() {
        val lexer = createLexer()

        matchSingleToken(lexer, "~", XPathTokenType.TYPE_ALIAS)

        matchSingleToken(lexer, "type", XPathTokenType.K_TYPE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (35) LambdaFunctionExpr")
    fun lambdaFunctionExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "_{", XPathTokenType.LAMBDA_FUNCTION)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)

        matchSingleToken(lexer, "_", XPathTokenType.K__)
        matchSingleToken(lexer, "{", XPathTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (37) ParamRef")
    fun paramRef() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
    }
}
