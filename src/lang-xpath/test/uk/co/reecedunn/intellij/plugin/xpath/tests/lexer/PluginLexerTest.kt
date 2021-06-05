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
    override val lexer: Lexer = XPathLexer()

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (20) AndExpr")
    fun andExpr() {
        token("and", XPathTokenType.K_AND)
        token("andAlso", XPathTokenType.K_ANDALSO)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (25) RecordTest")
    fun recordTest() {
        token("tuple", XPathTokenType.K_TUPLE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (26) FieldDeclaration")
    fun fieldDeclaration() {
        token("?", XPathTokenType.OPTIONAL)
        token("as", XPathTokenType.K_AS)

        token("?:", XPathTokenType.ELVIS) // compact whitespace -- BaseX 9.1 (XQuery) extension
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (25) ForwardAxis")
    fun forwardAxis() {
        token("attribute", XPathTokenType.K_ATTRIBUTE)
        token("child", XPathTokenType.K_CHILD)
        token("descendant", XPathTokenType.K_DESCENDANT)
        token("descendant-or-self", XPathTokenType.K_DESCENDANT_OR_SELF)
        token("following", XPathTokenType.K_FOLLOWING)
        token("following-sibling", XPathTokenType.K_FOLLOWING_SIBLING)
        token("namespace", XPathTokenType.K_NAMESPACE) // XPath and MarkLogic
        token("property", XPathTokenType.K_PROPERTY) // MarkLogic
        token("self", XPathTokenType.K_SELF)
        token("::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
    fun anyBooleanNodeTest() {
        token("boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
    fun namedBooleanNodeTest() {
        token("boolean-node", XPathTokenType.K_BOOLEAN_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
    fun anyNumberNodeTest() {
        token("number-node", XPathTokenType.K_NUMBER_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
    fun namedNumberNodeTest() {
        token("number-node", XPathTokenType.K_NUMBER_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
    fun anyNullNodeTest() {
        token("null-node", XPathTokenType.K_NULL_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
    fun namedNullNodeTest() {
        token("null-node", XPathTokenType.K_NULL_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
    fun anyArrayNodeTest() {
        token("array-node", XPathTokenType.K_ARRAY_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
    fun namedArrayNodeTest() {
        token("array-node", XPathTokenType.K_ARRAY_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
    fun anyMapNodeTest() {
        token("object-node", XPathTokenType.K_OBJECT_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
    fun namedMapNodeTest() {
        token("object-node", XPathTokenType.K_OBJECT_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    fun sequenceType() {
        token("empty-sequence", XPathTokenType.K_EMPTY_SEQUENCE)
        token("empty", XPathTokenType.K_EMPTY)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (19) OrExpr")
    fun orExpr() {
        token("or", XPathTokenType.K_OR)
        token("orElse", XPathTokenType.K_ORELSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (24) ContextItemFunctionExpr")
    fun contextItemFunctionExpr() {
        token("fn", XPathTokenType.K_FN)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)

        token(".{", XPathTokenType.CONTEXT_FUNCTION)
        token("}", XPathTokenType.BLOCK_CLOSE)

        token(".", XPathTokenType.DOT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (22) ParamList")
    fun paramList() {
        token(",", XPathTokenType.COMMA)
        token("...", XPathTokenType.ELLIPSIS)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (34) TypeAlias")
    fun typeAlias() {
        token("~", XPathTokenType.TYPE_ALIAS)

        token("type", XPathTokenType.K_TYPE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (35) LambdaFunctionExpr")
    fun lambdaFunctionExpr() {
        token("_{", XPathTokenType.LAMBDA_FUNCTION)
        token("}", XPathTokenType.BLOCK_CLOSE)

        token("_", XPathTokenType.K__)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (37) ParamRef")
    fun paramRef() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }
}
