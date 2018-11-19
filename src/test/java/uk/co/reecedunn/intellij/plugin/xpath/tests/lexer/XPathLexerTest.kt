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
package uk.co.reecedunn.intellij.plugin.xpath.tests.lexer

import com.intellij.lexer.Lexer
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@DisplayName("XPath 3.1 - Lexer")
class XPathLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer = XPathLexer()

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun invalidState() {
            val lexer = createLexer()

            val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, 4096) }
            assertThat(e.message, `is`("Invalid state: 4096"))
        }

        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() {
            val lexer = createLexer()

            lexer.start("")
            matchToken(lexer, "", 0, 0, 0, null)
        }

        @Test
        @DisplayName("bad characters")
        fun badCharacters() {
            val lexer = createLexer()

            lexer.start("~\uFFFE\u0000\uFFFF")
            matchToken(lexer, "~", 0, 0, 1, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "\uFFFE", 0, 1, 2, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "\u0000", 0, 2, 3, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "\uFFFF", 0, 3, 4, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "", 0, 4, 4, null)
        }
    }

    @Test
    @DisplayName("XML 1.0 EBNF (3) S")
    fun s() {
        val lexer = createLexer()

        lexer.start(" ")
        matchToken(lexer, " ", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("\t")
        matchToken(lexer, "\t", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("\r")
        matchToken(lexer, "\r", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("\n")
        matchToken(lexer, "\n", 0, 0, 1, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 1, 1, null)

        lexer.start("   \t  \r\n ")
        matchToken(lexer, "   \t  \r\n ", 0, 0, 9, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 0, 9, 9, null)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (2) Expr")
    fun expr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 2.0 EBNF (4) ForExpr")
    fun forExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 2.0 EBNF (5) SimpleForClause")
    fun simpleForClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "for", XPathTokenType.K_FOR)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "in", XPathTokenType.K_IN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 2.0 EBNF (6) QuantifiedExpr")
    fun quantifiedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "some", XPathTokenType.K_SOME)
        matchSingleToken(lexer, "every", XPathTokenType.K_EVERY)
        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "in", XPathTokenType.K_IN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, "satisfies", XPathTokenType.K_SATISFIES)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (7) IfExpr")
    fun ifExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "if", XPathTokenType.K_IF)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "then", XPathTokenType.K_THEN)
        matchSingleToken(lexer, "else", XPathTokenType.K_ELSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (8) OrExpr")
    fun orExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "or", XPathTokenType.K_OR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (9) AndExpr")
    fun andExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "and", XPathTokenType.K_AND)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (11) RangeExpr")
    fun rangeExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (12) AdditiveExpr")
    fun additiveExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "+", XPathTokenType.PLUS)
        matchSingleToken(lexer, "-", XPathTokenType.MINUS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (13) MultiplicativeExpr")
    fun multiplicativeExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XPathTokenType.STAR)
        matchSingleToken(lexer, "div", XPathTokenType.K_DIV)
        matchSingleToken(lexer, "idiv", XPathTokenType.K_IDIV)
        matchSingleToken(lexer, "mod", XPathTokenType.K_MOD)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (14) UnionExpr")
    fun unionExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "union", XPathTokenType.K_UNION)
        matchSingleToken(lexer, "|", XPathTokenType.UNION)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (15) IntersectExceptExpr")
    fun intersectExceptExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "intersect", XPathTokenType.K_INTERSECT)
        matchSingleToken(lexer, "except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (16) InstanceofExpr")
    fun instanceofExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "instance", XPathTokenType.K_INSTANCE)
        matchSingleToken(lexer, "of", XPathTokenType.K_OF)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (17) TreatExpr")
    fun treatExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "treat", XPathTokenType.K_TREAT)
        matchSingleToken(lexer, "as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (18) CastableExpr")
    fun castableExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "castable", XPathTokenType.K_CASTABLE)
        matchSingleToken(lexer, "as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (19) CastExpr")
    fun castExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "cast", XPathTokenType.K_CAST)
        matchSingleToken(lexer, "as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (20) UnaryExpr")
    fun unaryExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "+", XPathTokenType.PLUS)
        matchSingleToken(lexer, "-", XPathTokenType.MINUS)

        lexer.start("++")
        matchToken(lexer, "+", 0, 0, 1, XPathTokenType.PLUS)
        matchToken(lexer, "+", 0, 1, 2, XPathTokenType.PLUS)
        matchToken(lexer, "", 0, 2, 2, null)

        lexer.start("--")
        matchToken(lexer, "-", 0, 0, 1, XPathTokenType.MINUS)
        matchToken(lexer, "-", 0, 1, 2, XPathTokenType.MINUS)
        matchToken(lexer, "", 0, 2, 2, null)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (22) GeneralComp")
    fun generalComp() {
        val lexer = createLexer()

        matchSingleToken(lexer, "=", XPathTokenType.EQUAL)
        matchSingleToken(lexer, "!=", XPathTokenType.NOT_EQUAL)
        matchSingleToken(lexer, "<", XPathTokenType.LESS_THAN)
        matchSingleToken(lexer, "<=", XPathTokenType.LESS_THAN_OR_EQUAL)
        matchSingleToken(lexer, ">", XPathTokenType.GREATER_THAN)
        matchSingleToken(lexer, ">=", XPathTokenType.GREATER_THAN_OR_EQUAL)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (23) ValueComp")
    fun valueComp() {
        val lexer = createLexer()

        matchSingleToken(lexer, "eq", XPathTokenType.K_EQ)
        matchSingleToken(lexer, "ne", XPathTokenType.K_NE)
        matchSingleToken(lexer, "lt", XPathTokenType.K_LT)
        matchSingleToken(lexer, "le", XPathTokenType.K_LE)
        matchSingleToken(lexer, "gt", XPathTokenType.K_GT)
        matchSingleToken(lexer, "ge", XPathTokenType.K_GE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (24) NodeComp")
    fun nodeComp() {
        val lexer = createLexer()

        matchSingleToken(lexer, "is", XPathTokenType.K_IS)
        matchSingleToken(lexer, "<<", XPathTokenType.NODE_BEFORE)
        matchSingleToken(lexer, ">>", XPathTokenType.NODE_AFTER)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (25) PathExpr")
    fun pathExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "/", XPathTokenType.DIRECT_DESCENDANTS_PATH)
        matchSingleToken(lexer, "//", XPathTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (26) RelativePathExpr")
    fun relativePathExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "/", XPathTokenType.DIRECT_DESCENDANTS_PATH)
        matchSingleToken(lexer, "//", XPathTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (30) ForwardAxis")
    fun forwardAxis() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute", XPathTokenType.K_ATTRIBUTE)
        matchSingleToken(lexer, "child", XPathTokenType.K_CHILD)
        matchSingleToken(lexer, "descendant", XPathTokenType.K_DESCENDANT)
        matchSingleToken(lexer, "descendant-or-self", XPathTokenType.K_DESCENDANT_OR_SELF)
        matchSingleToken(lexer, "following", XPathTokenType.K_FOLLOWING)
        matchSingleToken(lexer, "following-sibling", XPathTokenType.K_FOLLOWING_SIBLING)
        matchSingleToken(lexer, "namespace", XPathTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "self", XPathTokenType.K_SELF)
        matchSingleToken(lexer, "::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (31) AbbrevForwardStep")
    fun abbrevForwardStep() {
        val lexer = createLexer()

        matchSingleToken(lexer, "@", XPathTokenType.ATTRIBUTE_SELECTOR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (33) ReverseAxis")
    fun reverseAxis() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ancestor", XPathTokenType.K_ANCESTOR)
        matchSingleToken(lexer, "ancestor-or-self", XPathTokenType.K_ANCESTOR_OR_SELF)
        matchSingleToken(lexer, "parent", XPathTokenType.K_PARENT)
        matchSingleToken(lexer, "preceding", XPathTokenType.K_PRECEDING)
        matchSingleToken(lexer, "preceding-sibling", XPathTokenType.K_PRECEDING_SIBLING)
        matchSingleToken(lexer, "::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (34) AbbrevReverseStep")
    fun abbrevReverseStep() {
        val lexer = createLexer()

        matchSingleToken(lexer, "..", XPathTokenType.PARENT_SELECTOR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (37) Wildcard")
    fun wildcard() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XPathTokenType.STAR)
        matchSingleToken(lexer, ":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (40) Predicate")
    fun predicate() {
        val lexer = createLexer()

        matchSingleToken(lexer, "[", XPathTokenType.SQUARE_OPEN)
        matchSingleToken(lexer, "]", XPathTokenType.SQUARE_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (44) VarRef")
    fun varRef() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (46) ParenthesizedExpr")
    fun parenthesizedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (47) ContextItemExpr")
    fun contextItemExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ".", XPathTokenType.DOT)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (48) FunctionCall")
    fun functionCall() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XPathTokenType.COMMA)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (49) SingleType")
    fun singleType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (50) SequenceType")
    fun sequenceType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "empty-sequence", XPathTokenType.K_EMPTY_SEQUENCE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (51) OccurrenceIndicator")
    fun occurrenceIndicator() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?", XPathTokenType.OPTIONAL)
        matchSingleToken(lexer, "*", XPathTokenType.STAR)
        matchSingleToken(lexer, "+", XPathTokenType.PLUS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (52) ItemType")
    fun itemType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "item", XPathTokenType.K_ITEM)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (55) AnyKindTest")
    fun anyKindTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "node", XPathTokenType.K_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (56) DocumentTest")
    fun documentTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "document-node", XPathTokenType.K_DOCUMENT_NODE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (57) TextTest")
    fun textTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "text", XPathTokenType.K_TEXT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (58) CommentTest")
    fun commentTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "comment", XPathTokenType.K_COMMENT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (59) PITest")
    fun piTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "processing-instruction", XPathTokenType.K_PROCESSING_INSTRUCTION)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (60) AttributeTest")
    fun attributeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute", XPathTokenType.K_ATTRIBUTE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (61) AttribNameOrWildcard")
    fun attribNameOrWildcard() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (62) SchemaAttributeTest")
    fun schemaAttributeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-attribute", XPathTokenType.K_SCHEMA_ATTRIBUTE)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (64) ElementTest")
    fun elementTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "element", XPathTokenType.K_ELEMENT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (65) ElementNameOrWildcard")
    fun elementNameOrWildcard() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (66) SchemaElementTest")
    fun schemaElementTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-element", XPathTokenType.K_SCHEMA_ELEMENT)
        matchSingleToken(lexer, "(", XPathTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (71) IntegerLiteral")
    fun integerLiteral() {
        val lexer = createLexer()

        lexer.start("1234")
        matchToken(lexer, "1234", 0, 0, 4, XPathTokenType.INTEGER_LITERAL)
        matchToken(lexer, "", 0, 4, 4, null)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (72) DecimalLiteral")
    fun decimalLiteral() {
        val lexer = createLexer()

        lexer.start("47.")
        matchToken(lexer, "47.", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 3, 3, null)

        lexer.start("1.234")
        matchToken(lexer, "1.234", 0, 0, 5, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 5, 5, null)

        lexer.start(".25")
        matchToken(lexer, ".25", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 3, 3, null)

        lexer.start(".1.2")
        matchToken(lexer, ".1", 0, 0, 2, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, ".2", 0, 2, 4, XPathTokenType.DECIMAL_LITERAL)
        matchToken(lexer, "", 0, 4, 4, null)
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (73) DoubleLiteral")
    internal inner class DoubleLiteral {
        @Test
        @DisplayName("double literal")
        fun doubleLiteral() {
            val lexer = createLexer()

            lexer.start("3e7 3e+7 3e-7")
            matchToken(lexer, "3e7", 0, 0, 3, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "3e+7", 0, 4, 8, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 8, 9, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "3e-7", 0, 9, 13, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("43E22 43E+22 43E-22")
            matchToken(lexer, "43E22", 0, 0, 5, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 5, 6, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "43E+22", 0, 6, 12, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 12, 13, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "43E-22", 0, 13, 19, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 19, 19, null)

            lexer.start("2.1e3 2.1e+3 2.1e-3")
            matchToken(lexer, "2.1e3", 0, 0, 5, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 5, 6, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "2.1e+3", 0, 6, 12, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 12, 13, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "2.1e-3", 0, 13, 19, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 19, 19, null)

            lexer.start("1.7E99 1.7E+99 1.7E-99")
            matchToken(lexer, "1.7E99", 0, 0, 6, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1.7E+99", 0, 7, 14, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 14, 15, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1.7E-99", 0, 15, 22, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 22, 22, null)

            lexer.start(".22e42 .22e+42 .22e-42")
            matchToken(lexer, ".22e42", 0, 0, 6, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".22e+42", 0, 7, 14, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 14, 15, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".22e-42", 0, 15, 22, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 22, 22, null)

            lexer.start(".8E2 .8E+2 .8E-2")
            matchToken(lexer, ".8E2", 0, 0, 4, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".8E+2", 0, 5, 10, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".8E-2", 0, 11, 16, XPathTokenType.DOUBLE_LITERAL)
            matchToken(lexer, "", 0, 16, 16, null)

            lexer.start("1e 1e+ 1e-")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "e", 3, 1, 2, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 2, 3, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 3, 4, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "e+", 3, 4, 6, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 7, 8, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "e-", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("1E 1E+ 1E-")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "E", 3, 1, 2, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 2, 3, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 3, 4, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "E+", 3, 4, 6, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "1", 0, 7, 8, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "E-", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("8.9e 8.9e+ 8.9e-")
            matchToken(lexer, "8.9", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e", 3, 3, 4, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 5, 8, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e+", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 11, 14, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e-", 3, 14, 16, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 16, 16, null)

            lexer.start("8.9E 8.9E+ 8.9E-")
            matchToken(lexer, "8.9", 0, 0, 3, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E", 3, 3, 4, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 5, 8, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E+", 3, 8, 10, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "8.9", 0, 11, 14, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E-", 3, 14, 16, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 16, 16, null)

            lexer.start(".4e .4e+ .4e-")
            matchToken(lexer, ".4", 0, 0, 2, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e", 3, 2, 3, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 4, 6, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e+", 3, 6, 8, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 8, 9, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 9, 11, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "e-", 3, 11, 13, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start(".4E .4E+ .4E-")
            matchToken(lexer, ".4", 0, 0, 2, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E", 3, 2, 3, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 4, 6, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E+", 3, 6, 8, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, " ", 0, 8, 9, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ".4", 0, 9, 11, XPathTokenType.DECIMAL_LITERAL)
            matchToken(lexer, "E-", 3, 11, 13, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 13, 13, null)
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            val lexer = createLexer()

            lexer.start("1e", 1, 2, 3)
            matchToken(lexer, "e", 3, 1, 2, XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            matchToken(lexer, "", 0, 2, 2, null)
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (74) StringLiteral")
    internal inner class StringLiteral {
        @Test
        @DisplayName("string literal")
        fun stringLiteral() {
            val lexer = createLexer()

            lexer.start("\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "", 1, 1, 1, null)

            lexer.start("\"Hello World\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "Hello World", 1, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("'")
            matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "", 2, 1, 1, null)

            lexer.start("'Hello World'")
            matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "Hello World", 2, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "'", 2, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            val lexer = createLexer()

            lexer.start("\"Hello World\"", 1, 13, 1)
            matchToken(lexer, "Hello World", 1, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("'Hello World'", 1, 13, 2)
            matchToken(lexer, "Hello World", 2, 1, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "'", 2, 12, 13, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 13, 13, null)
        }

        @Test
        @DisplayName("open brace - bad character in BracedURILiteral, not StringLiteral")
        fun openBrace() {
            val lexer = createLexer()

            // '{' is a bad character in BracedURILiterals, but not string literals.
            lexer.start("\"{\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "{", 1, 1, 2, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 2, 3, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 3, 3, null)

            // '{' is a bad character in BracedURILiterals, but not string literals.
            lexer.start("'{'")
            matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "{", 2, 1, 2, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "'", 2, 2, 3, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 3, 3, null)
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (75) EscapeQuot")
        fun escapeQuot() {
            val lexer = createLexer()

            lexer.start("\"One\"\"Two\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "One", 1, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"\"", 1, 4, 6, XPathTokenType.ESCAPED_CHARACTER)
            matchToken(lexer, "Two", 1, 6, 9, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 9, 10, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 10, 10, null)
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (76) EscapeApos")
        fun escapeApos() {
            val lexer = createLexer()

            lexer.start("'One''Two'")
            matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "One", 2, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "''", 2, 4, 6, XPathTokenType.ESCAPED_CHARACTER)
            matchToken(lexer, "Two", 2, 6, 9, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "'", 2, 9, 10, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 10, 10, null)
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val lexer = createLexer()

            matchSingleToken(lexer, "(:", 4, XPathTokenType.COMMENT_START_TAG)
            matchSingleToken(lexer, ":)", 0, XPathTokenType.COMMENT_END_TAG)

            lexer.start("(: Test :")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Test :", 4, 2, 9, XPathTokenType.COMMENT)
            matchToken(lexer, "", 6, 9, 9, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 9, 9, null)

            lexer.start("(: Test :)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Test ", 4, 2, 8, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 8, 10, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("(::Test::)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, ":Test:", 4, 2, 8, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 8, 10, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("(:\nMultiline\nComment\n:)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, "\nMultiline\nComment\n", 4, 2, 21, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 21, 23, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 23, 23, null)

            lexer.start("(: Outer (: Inner :) Outer :)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Outer (: Inner :) Outer ", 4, 2, 27, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 27, 29, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 29, 29, null)

            lexer.start("(: Outer ( : Inner :) Outer :)")
            matchToken(lexer, "(:", 0, 0, 2, XPathTokenType.COMMENT_START_TAG)
            matchToken(lexer, " Outer ( : Inner ", 4, 2, 19, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 19, 21, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, " ", 0, 21, 22, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "Outer", 0, 22, 27, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 0, 27, 28, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, ":)", 0, 28, 30, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 30, 30, null)
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            val lexer = createLexer()

            lexer.start("(: Test :", 2, 9, 4)
            matchToken(lexer, " Test :", 4, 2, 9, XPathTokenType.COMMENT)
            matchToken(lexer, "", 6, 9, 9, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 9, 9, null)

            lexer.start("(: Test :)", 2, 10, 4)
            matchToken(lexer, " Test ", 4, 2, 8, XPathTokenType.COMMENT)
            matchToken(lexer, ":)", 4, 8, 10, XPathTokenType.COMMENT_END_TAG)
            matchToken(lexer, "", 0, 10, 10, null)
        }
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (78) QName ; Namespaces in XML 1.0 EBNF (7) QName")
    fun qname() {
        val lexer = createLexer()

        lexer.start("one:two")
        matchToken(lexer, "one", 0, 0, 3, XPathTokenType.NCNAME)
        matchToken(lexer, ":", 0, 3, 4, XPathTokenType.QNAME_SEPARATOR)
        matchToken(lexer, "two", 0, 4, 7, XPathTokenType.NCNAME)
        matchToken(lexer, "", 0, 7, 7, null)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (79) NCName ; Namespaces in XML 1.0 EBNF (4) NCName")
    fun ncname() {
        val lexer = createLexer()

        lexer.start("test x b2b F.G a-b g\u0330d")
        matchToken(lexer, "test", 0, 0, 4, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 4, 5, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "x", 0, 5, 6, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 6, 7, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "b2b", 0, 7, 10, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 10, 11, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "F.G", 0, 11, 14, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 14, 15, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "a-b", 0, 15, 18, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 0, 18, 19, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "g\u0330d", 0, 19, 22, XPathTokenType.NCNAME)
        matchToken(lexer, "", 0, 22, 22, null)
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (100) BracedURILiteral")
    internal inner class BracedURILiteral {
        @Test
        @DisplayName("braced uri literal")
        fun bracedURILiteral() {
            val lexer = createLexer()

            matchSingleToken(lexer, "Q", XPathTokenType.NCNAME)

            lexer.start("Q{")
            matchToken(lexer, "Q{", 0, 0, 2, XPathTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "", 26, 2, 2, null)

            lexer.start("Q{Hello World}")
            matchToken(lexer, "Q{", 0, 0, 2, XPathTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "Hello World", 26, 2, 13, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 26, 13, 14, XPathTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 0, 14, 14, null)

            // NOTE: "", '', {{ and }} are used as escaped characters in string and attribute literals.
            lexer.start("Q{A\"\"B''C{{D}}E}")
            matchToken(lexer, "Q{", 0, 0, 2, XPathTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "A\"\"B''C", 26, 2, 9, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "{", 26, 9, 10, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "{", 26, 10, 11, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "D", 26, 11, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 26, 12, 13, XPathTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "}", 0, 13, 14, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "E", 0, 14, 15, XPathTokenType.NCNAME)
            matchToken(lexer, "}", 0, 15, 16, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "", 0, 16, 16, null)
        }
    }
}
