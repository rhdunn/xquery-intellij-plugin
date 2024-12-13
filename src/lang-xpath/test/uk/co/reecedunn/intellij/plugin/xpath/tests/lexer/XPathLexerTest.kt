/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathLexer
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

@DisplayName("XPath 3.1 - Lexer")
class XPathLexerTest : LexerTestCase() {
    override val lexer: Lexer = XPathLexer()

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun invalidState() {
            val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, 4096) }
            assertThat(e.message, `is`("Invalid state: 4096"))
        }

        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() = tokenize("") {
        }

        @Test
        @DisplayName("bad characters")
        fun badCharacters() = tokenize("^\uFFFE\u0000\uFFFF") {
            token("^", XPathTokenType.BAD_CHARACTER)
            token("\uFFFE", XPathTokenType.BAD_CHARACTER)
            token("\u0000", XPathTokenType.BAD_CHARACTER)
            token("\uFFFF", XPathTokenType.BAD_CHARACTER)
        }
    }

    @Test
    @DisplayName("XML 1.0 EBNF (3) S")
    fun s() {
        token(" ", XPathTokenType.WHITE_SPACE)
        token("\t", XPathTokenType.WHITE_SPACE)
        token("\r", XPathTokenType.WHITE_SPACE)
        token("\n", XPathTokenType.WHITE_SPACE)
        token("   \t  \r\n ", XPathTokenType.WHITE_SPACE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (2) Expr")
    fun expr() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (4) ForExpr")
    fun forExpr() {
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (5) SimpleForClause")
    fun simpleForClause() {
        token("for", XPathTokenType.K_FOR)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (6) QuantifiedExpr")
    fun quantifiedExpr() {
        token("some", XPathTokenType.K_SOME)
        token("every", XPathTokenType.K_EVERY)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
        token(",", XPathTokenType.COMMA)
        token("satisfies", XPathTokenType.K_SATISFIES)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (7) IfExpr")
    fun ifExpr() {
        token("if", XPathTokenType.K_IF)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("then", XPathTokenType.K_THEN)
        token("else", XPathTokenType.K_ELSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (21) OrExpr ; XPath 2.0 EBNF (8) OrExpr")
    fun orExpr() {
        token("or", XPathTokenType.K_OR)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (22) AndExpr ; XPath 2.0 EBNF (9) AndExpr")
    fun andExpr() {
        token("and", XPathTokenType.K_AND)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (11) RangeExpr")
    fun rangeExpr() {
        token("to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (25) AdditiveExpr ; XPath 2.0 EBNF (12) AdditiveExpr")
    fun additiveExpr() {
        token("+", XPathTokenType.PLUS)
        token("-", XPathTokenType.MINUS)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (26) MultiplicativeExpr ; XPath 1.0 EBNF (34) MultiplyOperator ; XPath 2.0 EBNF (13) MultiplicativeExpr")
    fun multiplicativeExpr() {
        token("*", XPathTokenType.STAR)
        token("div", XPathTokenType.K_DIV)
        token("idiv", XPathTokenType.K_IDIV)
        token("mod", XPathTokenType.K_MOD)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (18) UnionExpr ; XPath 2.0 EBNF (14) UnionExpr")
    fun unionExpr() {
        token("union", XPathTokenType.K_UNION)
        token("|", XPathTokenType.UNION)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (15) IntersectExceptExpr")
    fun intersectExceptExpr() {
        token("intersect", XPathTokenType.K_INTERSECT)
        token("except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (16) InstanceofExpr")
    fun instanceofExpr() {
        token("instance", XPathTokenType.K_INSTANCE)
        token("of", XPathTokenType.K_OF)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (17) TreatExpr")
    fun treatExpr() {
        token("treat", XPathTokenType.K_TREAT)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (18) CastableExpr")
    fun castableExpr() {
        token("castable", XPathTokenType.K_CASTABLE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (19) CastExpr")
    fun castExpr() {
        token("cast", XPathTokenType.K_CAST)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (27) UnaryExpr ; XPath 2.0 EBNF (20) UnaryExpr")
    fun unaryExpr() {
        token("+", XPathTokenType.PLUS)
        token("-", XPathTokenType.MINUS)

        tokenize("++") {
            token("+", XPathTokenType.PLUS)
            token("+", XPathTokenType.PLUS)
        }

        tokenize("--") {
            token("-", XPathTokenType.MINUS)
            token("-", XPathTokenType.MINUS)
        }
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (23) EqualityExpr ; XPath 1.0 EBNF (24) RelationalExpr ; XPath 2.0 EBNF (22) GeneralComp")
    fun generalComp() {
        token("=", XPathTokenType.EQUAL)
        token("!=", XPathTokenType.NOT_EQUAL)
        token("<", XPathTokenType.LESS_THAN)
        token("<=", XPathTokenType.LESS_THAN_OR_EQUAL)
        token(">", XPathTokenType.GREATER_THAN)
        token(">=", XPathTokenType.GREATER_THAN_OR_EQUAL)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (23) ValueComp")
    fun valueComp() {
        token("eq", XPathTokenType.K_EQ)
        token("ne", XPathTokenType.K_NE)
        token("lt", XPathTokenType.K_LT)
        token("le", XPathTokenType.K_LE)
        token("gt", XPathTokenType.K_GT)
        token("ge", XPathTokenType.K_GE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (24) NodeComp")
    fun nodeComp() {
        token("is", XPathTokenType.K_IS)
        token("<<", XPathTokenType.NODE_BEFORE)
        token(">>", XPathTokenType.NODE_AFTER)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (2) AbsoluteLocationPath ; XPath 1.0 EBNF (10) AbbreviatedAbsoluteLocationPath ; XPath 2.0 EBNF (25) PathExpr")
    fun pathExpr() {
        token("/", XPathTokenType.DIRECT_DESCENDANTS_PATH)
        token("//", XPathTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (3) RelativeLocationPath ; XPath 1.0 EBNF (10) AbbreviatedRelativeLocationPath ; XPath 1.0 EBNF (16) PathExpr ; XPath 2.0 EBNF (26) RelativePathExpr")
    fun relativePathExpr() {
        token("/", XPathTokenType.DIRECT_DESCENDANTS_PATH)
        token("//", XPathTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (5) AxisSpecifier ; XPath 1.0 EBNF (6) AxisName ; XPath 2.0 EBNF (30) ForwardAxis")
    fun forwardAxis() {
        token("attribute", XPathTokenType.K_ATTRIBUTE)
        token("child", XPathTokenType.K_CHILD)
        token("descendant", XPathTokenType.K_DESCENDANT)
        token("descendant-or-self", XPathTokenType.K_DESCENDANT_OR_SELF)
        token("following", XPathTokenType.K_FOLLOWING)
        token("following-sibling", XPathTokenType.K_FOLLOWING_SIBLING)
        token("namespace", XPathTokenType.K_NAMESPACE)
        token("self", XPathTokenType.K_SELF)
        token("::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (13) AbbreviatedAxisSpecifier ; XPath 2.0 EBNF (31) AbbrevForwardStep")
    fun abbrevForwardStep() {
        token("@", XPathTokenType.ATTRIBUTE_SELECTOR)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (5) AxisSpecifier ; XPath 1.0 EBNF (6) AxisName ; XPath 2.0 EBNF (33) ReverseAxis")
    fun reverseAxis() {
        token("ancestor", XPathTokenType.K_ANCESTOR)
        token("ancestor-or-self", XPathTokenType.K_ANCESTOR_OR_SELF)
        token("parent", XPathTokenType.K_PARENT)
        token("preceding", XPathTokenType.K_PRECEDING)
        token("preceding-sibling", XPathTokenType.K_PRECEDING_SIBLING)
        token("::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (12) AbbreviatedStep ; XPath 2.0 EBNF (34) AbbrevReverseStep")
    fun abbrevReverseStep() {
        token("..", XPathTokenType.PARENT_SELECTOR)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (37) NameTest ; XPath 2.0 EBNF (37) Wildcard")
    fun wildcard() {
        token("*", XPathTokenType.STAR)
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (8) Predicate ; XPath 2.0 EBNF (40) Predicate")
    fun predicate() {
        token("[", XPathTokenType.SQUARE_OPEN)
        token("]", XPathTokenType.SQUARE_CLOSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (36) VariableReference ; XPath 2.0 EBNF (44) VarRef")
    fun varRef() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (15) PrimaryExpr ; XPath 2.0 EBNF (46) ParenthesizedExpr")
    fun parenthesizedExpr() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (12) AbbreviatedStep ; XPath 2.0 EBNF (47) ContextItemExpr")
    fun contextItemExpr() {
        token(".", XPathTokenType.DOT)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (16) FunctionCall ; XPath 2.0 EBNF (48) FunctionCall")
    fun functionCall() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (49) SingleType")
    fun singleType() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (50) SequenceType")
    fun sequenceType() {
        token("empty-sequence", XPathTokenType.K_EMPTY_SEQUENCE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (51) OccurrenceIndicator")
    fun occurrenceIndicator() {
        token("?", XPathTokenType.OPTIONAL)
        token("*", XPathTokenType.STAR)
        token("+", XPathTokenType.PLUS)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (52) ItemType")
    fun itemType() {
        token("item", XPathTokenType.K_ITEM)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (7) NodeTest ; XPath 1.0 EBNF (38) NodeType ; XPath 2.0 EBNF (55) AnyKindTest")
    fun anyKindTest() {
        token("node", XPathTokenType.K_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (56) DocumentTest")
    fun documentTest() {
        token("document-node", XPathTokenType.K_DOCUMENT_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (7) NodeTest ; XPath 1.0 EBNF (38) NodeType ; XPath 2.0 EBNF (57) TextTest")
    fun textTest() {
        token("text", XPathTokenType.K_TEXT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (7) NodeTest ; XPath 1.0 EBNF (38) NodeType ; XPath 2.0 EBNF (58) CommentTest")
    fun commentTest() {
        token("comment", XPathTokenType.K_COMMENT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (7) NodeTest ; XPath 1.0 EBNF (38) NodeType ; XPath 2.0 EBNF (59) PITest")
    fun piTest() {
        token("processing-instruction", XPathTokenType.K_PROCESSING_INSTRUCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (60) AttributeTest")
    fun attributeTest() {
        token("attribute", XPathTokenType.K_ATTRIBUTE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (61) AttribNameOrWildcard")
    fun attribNameOrWildcard() {
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (62) SchemaAttributeTest")
    fun schemaAttributeTest() {
        token("schema-attribute", XPathTokenType.K_SCHEMA_ATTRIBUTE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (64) ElementTest")
    fun elementTest() {
        token("element", XPathTokenType.K_ELEMENT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (65) ElementNameOrWildcard")
    fun elementNameOrWildcard() {
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (66) SchemaElementTest")
    fun schemaElementTest() {
        token("schema-element", XPathTokenType.K_SCHEMA_ELEMENT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (30) Number ; XPath 2.0 EBNF (71) IntegerLiteral")
    fun integerLiteral() {
        token("1234", XPathTokenType.INTEGER_LITERAL)
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (30) Number ; XPath 2.0 EBNF (72) DecimalLiteral")
    fun decimalLiteral() {
        token("47.", XPathTokenType.DECIMAL_LITERAL)
        token("1.234", XPathTokenType.DECIMAL_LITERAL)
        token(".25", XPathTokenType.DECIMAL_LITERAL)

        tokenize(".1.2") {
            token(".1", XPathTokenType.DECIMAL_LITERAL)
            token(".2", XPathTokenType.DECIMAL_LITERAL)
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (73) DoubleLiteral")
    internal inner class DoubleLiteral {
        @Test
        @DisplayName("double literal")
        fun doubleLiteral() {
            tokenize("3e7 3e+7 3e-7") {
                token("3e7", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("3e+7", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("3e-7", XPathTokenType.DOUBLE_LITERAL)
            }

            tokenize("43E22 43E+22 43E-22") {
                token("43E22", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("43E+22", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("43E-22", XPathTokenType.DOUBLE_LITERAL)
            }

            tokenize("2.1e3 2.1e+3 2.1e-3") {
                token("2.1e3", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("2.1e+3", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("2.1e-3", XPathTokenType.DOUBLE_LITERAL)
            }

            tokenize("1.7E99 1.7E+99 1.7E-99") {
                token("1.7E99", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("1.7E+99", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("1.7E-99", XPathTokenType.DOUBLE_LITERAL)
            }

            tokenize(".22e42 .22e+42 .22e-42") {
                token(".22e42", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".22e+42", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".22e-42", XPathTokenType.DOUBLE_LITERAL)
            }

            tokenize(".8E2 .8E+2 .8E-2") {
                token(".8E2", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".8E+2", XPathTokenType.DOUBLE_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".8E-2", XPathTokenType.DOUBLE_LITERAL)
            }

            tokenize("1e 1e+ 1e-") {
                token("1", XPathTokenType.INTEGER_LITERAL)
                state(3)
                token("e", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("1", XPathTokenType.INTEGER_LITERAL)
                state(3)
                token("e+", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("1", XPathTokenType.INTEGER_LITERAL)
                state(3)
                token("e-", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
            }

            tokenize("1E 1E+ 1E-") {
                token("1", XPathTokenType.INTEGER_LITERAL)
                state(3)
                token("E", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("1", XPathTokenType.INTEGER_LITERAL)
                state(3)
                token("E+", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("1", XPathTokenType.INTEGER_LITERAL)
                state(3)
                token("E-", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
            }

            tokenize("8.9e 8.9e+ 8.9e-") {
                token("8.9", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("e", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("8.9", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("e+", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("8.9", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("e-", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
            }

            tokenize("8.9E 8.9E+ 8.9E-") {
                token("8.9", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("E", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("8.9", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("E+", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("8.9", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("E-", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
            }

            tokenize(".4e .4e+ .4e-") {
                token(".4", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("e", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".4", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("e+", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".4", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("e-", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
            }

            tokenize(".4E .4E+ .4E-") {
                token(".4", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("E", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".4", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("E+", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(".4", XPathTokenType.DECIMAL_LITERAL)
                state(3)
                token("E-", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
                state(0)
            }
        }

        @Test
        @DisplayName("initial state")
        fun initialState() = tokenize("1e", 1, 2, 3) {
            token("e", XPathTokenType.PARTIAL_DOUBLE_LITERAL_EXPONENT)
            state(0)
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (29) Literal ; XPath 2.0 EBNF (74) StringLiteral")
    internal inner class StringLiteral {
        @Test
        @DisplayName("string literal")
        fun stringLiteral() {
            tokenize("\"Hello World\"") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            tokenize("'Hello World'") {
                token("'", XPathTokenType.STRING_LITERAL_START)
                state(2)
                token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("'", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            // XPath does not support predefined entity references; they are handled by XML.
            tokenize("'One &amp; Two'") {
                token("'", XPathTokenType.STRING_LITERAL_START)
                state(2)
                token("One &amp; Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("'", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            tokenize("\"Hello World\"", 1, 13, 1) {
                token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            tokenize("'Hello World'", 1, 13, 2) {
                token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("'", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }
        }

        @Test
        @DisplayName("open brace - bad character in BracedURILiteral, not StringLiteral")
        fun openBrace() {
            // '{' is a bad character in BracedURILiterals, but not string literals.
            tokenize("\"{\"") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("{", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            // '{' is a bad character in BracedURILiterals, but not string literals.
            tokenize("'{'") {
                token("'", XPathTokenType.STRING_LITERAL_START)
                state(2)
                token("{", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("'", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (75) EscapeQuot")
        fun escapeQuot() = tokenize("\"One\"\"Two\"") {
            token("\"", XPathTokenType.STRING_LITERAL_START)
            state(1)
            token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("\"\"", XPathTokenType.ESCAPED_CHARACTER)
            token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("\"", XPathTokenType.STRING_LITERAL_END)
            state(0)
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (76) EscapeApos")
        fun escapeApos() = tokenize("'One''Two'") {
            token("'", XPathTokenType.STRING_LITERAL_START)
            state(2)
            token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("''", XPathTokenType.ESCAPED_CHARACTER)
            token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("'", XPathTokenType.STRING_LITERAL_END)
            state(0)
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            token("(:", 4, XPathTokenType.COMMENT_START_TAG)
            token(":)", 0, XPathTokenType.COMMENT_END_TAG)

            tokenize("(: Test :") {
                token("(:", XPathTokenType.COMMENT_START_TAG)
                state(4)
                token(" Test :", XPathTokenType.COMMENT)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("(: Test :)") {
                token("(:", XPathTokenType.COMMENT_START_TAG)
                state(4)
                token(" Test ", XPathTokenType.COMMENT)
                token(":)", XPathTokenType.COMMENT_END_TAG)
                state(0)
            }

            tokenize("(::Test::)") {
                token("(:", XPathTokenType.COMMENT_START_TAG)
                state(4)
                token(":Test:", XPathTokenType.COMMENT)
                token(":)", XPathTokenType.COMMENT_END_TAG)
                state(0)
            }

            tokenize("(:\nMultiline\nComment\n:)") {
                token("(:", XPathTokenType.COMMENT_START_TAG)
                state(4)
                token("\nMultiline\nComment\n", XPathTokenType.COMMENT)
                token(":)", XPathTokenType.COMMENT_END_TAG)
                state(0)
            }

            tokenize("(: Outer (: Inner :) Outer :)") {
                token("(:", XPathTokenType.COMMENT_START_TAG)
                state(4)
                token(" Outer (: Inner :) Outer ", XPathTokenType.COMMENT)
                token(":)", XPathTokenType.COMMENT_END_TAG)
                state(0)
            }

            tokenize("(: Outer ( : Inner :) Outer :)") {
                token("(:", XPathTokenType.COMMENT_START_TAG)
                state(4)
                token(" Outer ( : Inner ", XPathTokenType.COMMENT)
                token(":)", XPathTokenType.COMMENT_END_TAG)
                state(0)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("Outer", XPathTokenType.NCNAME)
                token(" ", XPathTokenType.WHITE_SPACE)
                token(":)", XPathTokenType.COMMENT_END_TAG)
            }
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            tokenize("(: Test :", 2, 9, 4) {
                token(" Test :", XPathTokenType.COMMENT)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("(: Test :)", 2, 10, 4) {
                token(" Test ", XPathTokenType.COMMENT)
                token(":)", XPathTokenType.COMMENT_END_TAG)
                state(0)
            }
        }
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (78) QName ; Namespaces in XML 1.0 EBNF (7) QName")
    fun qname() = tokenize("one:two") {
        token("one", XPathTokenType.NCNAME)
        token(":", XPathTokenType.QNAME_SEPARATOR)
        token("two", XPathTokenType.NCNAME)
    }

    @Test
    @DisplayName("XPath 2.0 EBNF (79) NCName ; Namespaces in XML 1.0 EBNF (4) NCName")
    fun ncname() = tokenize("test x b2b F.G a-b g\u0330d") {
        token("test", XPathTokenType.NCNAME)
        token(" ", XPathTokenType.WHITE_SPACE)
        token("x", XPathTokenType.NCNAME)
        token(" ", XPathTokenType.WHITE_SPACE)
        token("b2b", XPathTokenType.NCNAME)
        token(" ", XPathTokenType.WHITE_SPACE)
        token("F.G", XPathTokenType.NCNAME)
        token(" ", XPathTokenType.WHITE_SPACE)
        token("a-b", XPathTokenType.NCNAME)
        token(" ", XPathTokenType.WHITE_SPACE)
        token("g\u0330d", XPathTokenType.NCNAME)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (2) ParamList")
    fun paramList() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (3) Param")
    fun param() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (5) EnclosedExpr")
    fun enclosedExpr() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (10) SimpleForBinding")
    fun simpleForBinding() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (11) LetExpr")
    fun letExpr() {
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (12) SimpleLetClause")
    fun simpleLetClause() {
        token("let", XPathTokenType.K_LET)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (13) SimpleLetBinding")
    fun simpleLetBinding() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (19) StringConcatExpr")
    fun stringConcatExpr() {
        token("||", XPathTokenType.CONCATENATION)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (34) SimpleMapExpr")
    fun simpleMapExpr() {
        token("!", XPathTokenType.MAP_OPERATOR)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (49) ArgumentList")
    fun argumentList() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (61) ArgumentPlaceholder")
    fun argumentPlaceholder() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (63) NamedFunctionRef")
    fun namedFunctionRef() {
        token("#", XPathTokenType.FUNCTION_REF_OPERATOR)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (64) InlineFunctionExpr")
    fun inlineFunctionExpr() {
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (66) TypeDeclaration")
    fun typeDeclaration() {
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (76) NamespaceNodeTest")
    fun namespaceNodeTest() {
        token("namespace-node", XPathTokenType.K_NAMESPACE_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (91) AnyFunctionTest")
    fun anyFunctionTest() {
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("*", XPathTokenType.STAR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (92) TypedFunctionTest")
    fun typedFunctionTest() {
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 3.0 EBNF (93) ParenthesizedItemType")
    fun parenthesizedItemType() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (100) BracedURILiteral")
    internal inner class BracedURILiteral {
        @Test
        @DisplayName("braced uri literal")
        fun bracedURILiteral() {
            token("Q", XPathTokenType.NCNAME)
            token("Q{", 26, XPathTokenType.BRACED_URI_LITERAL_START)

            tokenize("Q{Hello World}") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(0)
            }

            // NOTE: "", '', {{ and }} are used as escaped characters in string and attribute literals.
            tokenize("Q{A\"\"B''C{{D}}E}") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("A\"\"B''C", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("{", XPathTokenType.BAD_CHARACTER)
                token("{", XPathTokenType.BAD_CHARACTER)
                token("D", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(0)
                token("}", XPathTokenType.BLOCK_CLOSE)
                token("E", XPathTokenType.NCNAME)
                token("}", XPathTokenType.BLOCK_CLOSE)
            }
        }
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (29) ArrowExpr")
    fun arrowExpr() {
        token("=>", XPathTokenType.ARROW)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (53) Lookup")
    fun lookup() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (54) KeySpecifier")
    fun keySpecifier() {
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (69) MapConstructor")
    fun mapConstructor() {
        token("map", XPathTokenType.K_MAP)
        token("{", XPathTokenType.BLOCK_OPEN)
        token(",", XPathTokenType.COMMA)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (70) MapConstructorEntry")
    fun mapConstructorEntry() {
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (74) SquareArrayConstructor")
    fun squareArrayConstructor() {
        token("[", XPathTokenType.SQUARE_OPEN)
        token(",", XPathTokenType.COMMA)
        token("]", XPathTokenType.SQUARE_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (75) CurlyArrayConstructor")
    fun curlyArrayConstructor() {
        token("array", XPathTokenType.K_ARRAY)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (76) UnaryLookup")
    fun unaryLookup() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (106) AnyMapTest")
    fun anyMapTest() {
        token("map", XPathTokenType.K_MAP)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("*", XPathTokenType.STAR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (107) TypedMapTest")
    fun typedMapTest() {
        token("map", XPathTokenType.K_MAP)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (109) AnyArrayTest")
    fun anyArrayTest() {
        token("array", XPathTokenType.K_ARRAY)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("*", XPathTokenType.STAR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 3.1 EBNF (110) TypedArrayTest")
    fun typedArrayTest() {
        token("array", XPathTokenType.K_ARRAY)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (9) WithExpr")
    fun withExpr() {
        token("with", XPathTokenType.K_WITH)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (10) NamespaceDeclaration")
    fun namespaceDeclaration() {
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (11) TernaryConditionalExpr")
    fun ternaryConditionalExpr() {
        token("??", XPathTokenType.TERNARY_IF)
        token("!!", XPathTokenType.TERNARY_ELSE)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (13) SimpleForClause")
    fun simpleForClause_XPath40() {
        token("for", XPathTokenType.K_FOR)
        token("member", XPathTokenType.K_MEMBER)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (19) QuantifierBinding")
    fun quantifierBinding() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (28) OtherwiseExpr")
    fun otherwiseExpr() {
        token("otherwise", XPathTokenType.K_OTHERWISE)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (33) FunctionSignature")
    fun functionSignature() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (38) FatArrowTarget")
    fun fatArrowTarget() {
        token("=>", XPathTokenType.ARROW)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (39) ThinArrowTarget")
    fun thinArrowTarget() {
        token("->", XPathTokenType.THIN_ARROW)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (59) PositionalArgumentList")
    fun positionalArgumentList() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (60) PositionalArguments")
    fun positionalArguments() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (61) KeywordArguments")
    fun keywordArguments() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (62) KeywordArgument")
    fun keywordArgument() {
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (83) InlineFunctionExpr")
    fun inlineFunctionExpr_XPath40() {
        token("function", XPathTokenType.K_FUNCTION)
        token("->", XPathTokenType.THIN_ARROW)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (122) RecordTest")
    fun recordTest() {
        token("record", XPathTokenType.K_RECORD)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (123) FieldDeclaration")
    fun fieldDeclaration() {
        token("?", XPathTokenType.OPTIONAL)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (125) SelfReference")
    fun selfReference() {
        token("..", XPathTokenType.PARENT_SELECTOR)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (126) ExtensibleFlag")
    fun extensibleFlag() {
        token(",", XPathTokenType.COMMA)
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (127) LocalUnionType")
    fun localUnionType() {
        token("union", XPathTokenType.K_UNION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (128) EnumerationType")
    fun enumerationType() {
        token("enum", XPathTokenType.K_ENUM)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }
}
