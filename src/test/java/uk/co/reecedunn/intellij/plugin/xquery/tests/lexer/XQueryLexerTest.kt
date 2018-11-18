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
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.STATE_START_DIR_ELEM_CONSTRUCTOR
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@DisplayName("XQuery 3.1 - Lexer")
class XQueryLexerTest : LexerTestCase() {
    private fun createLexer(): Lexer {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(XQueryLexer(), 0x50000000, 0, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
        lexer.addState(XQueryLexer(), 0x60000000, 0, STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
        return lexer
    }

    private fun createXQueryLexer(): Lexer = XQueryLexer()

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun testInvalidState() {
            val lexer = createLexer()

            val e = assertThrows(AssertionError::class.java) { lexer.start("123", 0, 3, 4096) }
            assertThat(e.message, `is`("Invalid state: 4096"))
        }

        @Test
        @DisplayName("empty stack when calling advance()")
        fun testEmptyStackInAdvance() {
            val lexer = createLexer()

            lexer.start("\"Hello World\"")
            lexer.advance()
            assertThat(lexer.state, `is`(1))

            lexer.start("} {\"")
            matchToken(lexer, "}", 0, 0, 1, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "{", 0, 2, 3, XQueryTokenType.BLOCK_OPEN)
            matchToken(lexer, "\"", 0, 3, 4, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "", 1, 4, 4, null)
        }

        @Test
        @DisplayName("empty stack when calling popState()")
        fun testEmptyStackInPopState() {
            val lexer = createLexer()

            lexer.start("} } ")
            matchToken(lexer, "}", 0, 0, 1, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "}", 0, 2, 3, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "", 0, 4, 4, null)
        }

        @Test
        @DisplayName("empty buffer")
        fun testEmptyBuffer() {
            val lexer = createLexer()

            lexer.start("")
            matchToken(lexer, "", 0, 0, 0, null)
        }

        @Test
        @DisplayName("bad characters")
        fun testBadCharacters() {
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
    @DisplayName("XQuery 1.0 EBNF (2) VersionDecl")
    fun testVersionDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "xquery", XQueryTokenType.K_XQUERY)
        matchSingleToken(lexer, "version", XQueryTokenType.K_VERSION)
        matchSingleToken(lexer, "encoding", XQueryTokenType.K_ENCODING)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (5) ModuleDecl")
    fun testModuleDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "module", XQueryTokenType.K_MODULE)
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (9) Separator")
    fun testSeparator() {
        val lexer = createLexer()

        matchSingleToken(lexer, ";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (10) NamespaceDecl")
    fun testNamespaceDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (11) BoundarySpaceDecl")
    fun testBoundarySpaceDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "boundary-space", XQueryTokenType.K_BOUNDARY_SPACE)
        matchSingleToken(lexer, "preserve", XQueryTokenType.K_PRESERVE)
        matchSingleToken(lexer, "strip", XQueryTokenType.K_STRIP)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (12) DefaultNamespaceDecl")
    fun testDefaultNamespaceDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "element", XQueryTokenType.K_ELEMENT)
        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION)
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (13) OptionDecl")
    fun testOptionDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "option", XQueryTokenType.K_OPTION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (14) OrderingModeDecl")
    fun testOrderingModeDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "ordering", XQueryTokenType.K_ORDERING)
        matchSingleToken(lexer, "ordered", XQueryTokenType.K_ORDERED)
        matchSingleToken(lexer, "unordered", XQueryTokenType.K_UNORDERED)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (15) EmptyOrderDecl")
    fun testEmptyOrderDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "order", XQueryTokenType.K_ORDER)
        matchSingleToken(lexer, "empty", XQueryTokenType.K_EMPTY)
        matchSingleToken(lexer, "greatest", XQueryTokenType.K_GREATEST)
        matchSingleToken(lexer, "least", XQueryTokenType.K_LEAST)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (16) CopyNamespacesDecl")
    fun testCopyNamespacesDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "copy-namespaces", XQueryTokenType.K_COPY_NAMESPACES)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (17) PreserveMode")
    fun testPreserveMode() {
        val lexer = createLexer()

        matchSingleToken(lexer, "preserve", XQueryTokenType.K_PRESERVE)
        matchSingleToken(lexer, "no-preserve", XQueryTokenType.K_NO_PRESERVE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (18) InheritMode")
    fun testInheritMode() {
        val lexer = createLexer()

        matchSingleToken(lexer, "inherit", XQueryTokenType.K_INHERIT)
        matchSingleToken(lexer, "no-inherit", XQueryTokenType.K_NO_INHERIT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (19) DefaultCollationDecl")
    fun testDefaultCollationDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "collation", XQueryTokenType.K_COLLATION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (20) BaseURIDecl")
    fun testBaseURIDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "base-uri", XQueryTokenType.K_BASE_URI)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (21) SchemaImport")
    fun testSchemaImport() {
        val lexer = createLexer()

        matchSingleToken(lexer, "import", XQueryTokenType.K_IMPORT)
        matchSingleToken(lexer, "schema", XQueryTokenType.K_SCHEMA)
        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (22) SchemaPrefix")
    fun testSchemaPrefix() {
        val lexer = createLexer()

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)

        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "element", XQueryTokenType.K_ELEMENT)
        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (23) ModuleImport")
    fun testModuleImport() {
        val lexer = createLexer()

        matchSingleToken(lexer, "import", XQueryTokenType.K_IMPORT)
        matchSingleToken(lexer, "module", XQueryTokenType.K_MODULE)
        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (24) VarDecl")
    fun testVarDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "variable", XQueryTokenType.K_VARIABLE)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (25) ConstructionDecl")
    fun testConstructionDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "construction", XQueryTokenType.K_CONSTRUCTION)
        matchSingleToken(lexer, "strip", XQueryTokenType.K_STRIP)
        matchSingleToken(lexer, "preserve", XQueryTokenType.K_PRESERVE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (26) FunctionDecl")
    fun testFunctionDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (27) ParamList")
    fun testParamList() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (28) Param")
    fun testParam() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (29) EnclosedExpr")
    fun testEnclosedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (31) Expr")
    fun testExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr")
    fun testFLWORExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (34) ForClause")
    fun testForClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "for", XQueryTokenType.K_FOR)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "in", XQueryTokenType.K_IN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (35) PosiitonalVar")
    fun testPositionalVar() {
        val lexer = createLexer()

        matchSingleToken(lexer, "at", XQueryTokenType.K_AT)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (36) LetClause")
    fun testLetClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "let", XQueryTokenType.K_LET)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (37) WhereClause")
    fun testWhereClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "where", XQueryTokenType.K_WHERE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (38) OrderByClause")
    fun testOrderByClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "stable", XQueryTokenType.K_STABLE)
        matchSingleToken(lexer, "order", XQueryTokenType.K_ORDER)
        matchSingleToken(lexer, "by", XQueryTokenType.K_BY)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (39) OrderSpecList")
    fun testOrderSpecList() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (41) OrderModifier")
    fun testOrderModifier() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ascending", XQueryTokenType.K_ASCENDING)
        matchSingleToken(lexer, "descending", XQueryTokenType.K_DESCENDING)

        matchSingleToken(lexer, "empty", XQueryTokenType.K_EMPTY)
        matchSingleToken(lexer, "greatest", XQueryTokenType.K_GREATEST)
        matchSingleToken(lexer, "least", XQueryTokenType.K_LEAST)

        matchSingleToken(lexer, "collation", XQueryTokenType.K_COLLATION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (42) QuantifiedExpr")
    fun testQuantifiedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "some", XQueryTokenType.K_SOME)
        matchSingleToken(lexer, "every", XQueryTokenType.K_EVERY)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "in", XQueryTokenType.K_IN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, "satisfies", XQueryTokenType.K_SATISFIES)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (43) TypeswitchExpr")
    fun testTypeswitchExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "typeswitch", XQueryTokenType.K_TYPESWITCH)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (44) CaseClause")
    fun testCaseClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "case", XQueryTokenType.K_CASE)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (45) IfExpr")
    fun testIfExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "if", XQueryTokenType.K_IF)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "then", XQueryTokenType.K_THEN)
        matchSingleToken(lexer, "else", XQueryTokenType.K_ELSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (46) OrExpr")
    fun testOrExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "or", XQueryTokenType.K_OR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (47) AndExpr")
    fun testAndExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "and", XQueryTokenType.K_AND)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (49) RangeExpr")
    fun testRangeExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "to", XQueryTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (50) AdditiveExpr")
    fun testAdditiveExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "+", XQueryTokenType.PLUS)
        matchSingleToken(lexer, "-", XQueryTokenType.MINUS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (51) MultiplicativeExpr")
    fun testMultiplicativeExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
        matchSingleToken(lexer, "div", XQueryTokenType.K_DIV)
        matchSingleToken(lexer, "idiv", XQueryTokenType.K_IDIV)
        matchSingleToken(lexer, "mod", XQueryTokenType.K_MOD)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (52) UnionExpr")
    fun testUnionExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "union", XQueryTokenType.K_UNION)
        matchSingleToken(lexer, "|", XQueryTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (53) IntersectExceptExpr")
    fun testIntersectExceptExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "intersect", XQueryTokenType.K_INTERSECT)
        matchSingleToken(lexer, "except", XQueryTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (54) InstanceofExpr")
    fun testInstanceofExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "instance", XQueryTokenType.K_INSTANCE)
        matchSingleToken(lexer, "of", XQueryTokenType.K_OF)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (55) TreatExpr")
    fun testTreatExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "treat", XQueryTokenType.K_TREAT)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (56) CastableExpr")
    fun testCastableExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "castable", XQueryTokenType.K_CASTABLE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (57) CastExpr")
    fun testCastExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "cast", XQueryTokenType.K_CAST)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (58) UnaryExpr")
    fun testUnaryExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "+", XQueryTokenType.PLUS)
        matchSingleToken(lexer, "-", XQueryTokenType.MINUS)

        lexer.start("++")
        matchToken(lexer, "+", 0, 0, 1, XQueryTokenType.PLUS)
        matchToken(lexer, "+", 0, 1, 2, XQueryTokenType.PLUS)
        matchToken(lexer, "", 0, 2, 2, null)

        lexer.start("--")
        matchToken(lexer, "-", 0, 0, 1, XQueryTokenType.MINUS)
        matchToken(lexer, "-", 0, 1, 2, XQueryTokenType.MINUS)
        matchToken(lexer, "", 0, 2, 2, null)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (60) GeneralComp")
    fun testGeneralComp() {
        val lexer = createLexer()

        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)
        matchSingleToken(lexer, "!=", XQueryTokenType.NOT_EQUAL)
        matchSingleToken(lexer, "<", XQueryTokenType.LESS_THAN)
        matchSingleToken(lexer, "<=", XQueryTokenType.LESS_THAN_OR_EQUAL)
        matchSingleToken(lexer, ">", XQueryTokenType.GREATER_THAN)
        matchSingleToken(lexer, ">=", XQueryTokenType.GREATER_THAN_OR_EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (61) ValueComp")
    fun testValueComp() {
        val lexer = createLexer()

        matchSingleToken(lexer, "eq", XQueryTokenType.K_EQ)
        matchSingleToken(lexer, "ne", XQueryTokenType.K_NE)
        matchSingleToken(lexer, "lt", XQueryTokenType.K_LT)
        matchSingleToken(lexer, "le", XQueryTokenType.K_LE)
        matchSingleToken(lexer, "gt", XQueryTokenType.K_GT)
        matchSingleToken(lexer, "ge", XQueryTokenType.K_GE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (62) NodeComp")
    fun testNodeComp() {
        val lexer = createLexer()

        matchSingleToken(lexer, "is", XQueryTokenType.K_IS)
        matchSingleToken(lexer, "<<", XQueryTokenType.NODE_BEFORE)
        matchSingleToken(lexer, ">>", XQueryTokenType.NODE_AFTER)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (63) ValidateExpr")
    fun testValidateExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "validate", XQueryTokenType.K_VALIDATE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (64) ValidateMode")
    fun testValidationMode() {
        val lexer = createLexer()

        matchSingleToken(lexer, "lax", XQueryTokenType.K_LAX)
        matchSingleToken(lexer, "strict", XQueryTokenType.K_STRICT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (65) ExtensionExpr")
    fun testExtensionExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (66) Pragma ; XQuery 1.0 EBNF (67) PragmaContents")
    fun testPragma() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(#", 8, XQueryTokenType.PRAGMA_BEGIN)
        matchSingleToken(lexer, "#)", 0, XQueryTokenType.PRAGMA_END)

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, "#", XQueryTokenType.FUNCTION_REF_OPERATOR)

        lexer.start("(#  let:for  6^gkgw~*#g#)")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "  ", 8, 2, 4, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "let", 8, 4, 7, XPathTokenType.NCNAME)
        matchToken(lexer, ":", 9, 7, 8, XPathTokenType.QNAME_SEPARATOR)
        matchToken(lexer, "for", 9, 8, 11, XPathTokenType.NCNAME)
        matchToken(lexer, "  ", 9, 11, 13, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "6^gkgw~*#g", 10, 13, 23, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "#)", 10, 23, 25, XQueryTokenType.PRAGMA_END)
        matchToken(lexer, "", 0, 25, 25, null)

        lexer.start("(#let ##)")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "let", 8, 2, 5, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 9, 5, 6, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "#", 10, 6, 7, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "#)", 10, 7, 9, XQueryTokenType.PRAGMA_END)
        matchToken(lexer, "", 0, 9, 9, null)

        lexer.start("(#let 2")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "let", 8, 2, 5, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 9, 5, 6, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "2", 10, 6, 7, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "", 6, 7, 7, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
        matchToken(lexer, "", 0, 7, 7, null)

        lexer.start("(#let ")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "let", 8, 2, 5, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 9, 5, 6, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 10, 6, 6, null)

        lexer.start("(#let~~~#)")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "let", 8, 2, 5, XPathTokenType.NCNAME)
        matchToken(lexer, "~~~", 9, 5, 8, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "#)", 10, 8, 10, XQueryTokenType.PRAGMA_END)
        matchToken(lexer, "", 0, 10, 10, null)

        lexer.start("(#let~~~")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "let", 8, 2, 5, XPathTokenType.NCNAME)
        matchToken(lexer, "~~~", 9, 5, 8, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "", 6, 8, 8, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
        matchToken(lexer, "", 0, 8, 8, null)

        lexer.start("(#:let 2#)")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, ":", 8, 2, 3, XPathTokenType.QNAME_SEPARATOR)
        matchToken(lexer, "let", 9, 3, 6, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 9, 6, 7, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "2", 10, 7, 8, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "#)", 10, 8, 10, XQueryTokenType.PRAGMA_END)
        matchToken(lexer, "", 0, 10, 10, null)

        lexer.start("(#~~~#)")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "~~~", 8, 2, 5, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "#)", 10, 5, 7, XQueryTokenType.PRAGMA_END)
        matchToken(lexer, "", 0, 7, 7, null)

        lexer.start("(#~~~")
        matchToken(lexer, "(#", 0, 0, 2, XQueryTokenType.PRAGMA_BEGIN)
        matchToken(lexer, "~~~", 8, 2, 5, XQueryTokenType.PRAGMA_CONTENTS)
        matchToken(lexer, "", 6, 5, 5, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
        matchToken(lexer, "", 0, 5, 5, null)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (68) PathExpr")
    fun testPathExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "/", XQueryTokenType.DIRECT_DESCENDANTS_PATH)
        matchSingleToken(lexer, "//", XQueryTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (69) RelativePathExpr")
    fun testRelativePathExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "/", XQueryTokenType.DIRECT_DESCENDANTS_PATH)
        matchSingleToken(lexer, "//", XQueryTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (73) ForwardAxis")
    fun testForwardAxis() {
        val lexer = createLexer()

        matchSingleToken(lexer, "child", XQueryTokenType.K_CHILD)
        matchSingleToken(lexer, "descendant", XQueryTokenType.K_DESCENDANT)
        matchSingleToken(lexer, "attribute", XQueryTokenType.K_ATTRIBUTE)
        matchSingleToken(lexer, "self", XQueryTokenType.K_SELF)
        matchSingleToken(lexer, "descendant-or-self", XQueryTokenType.K_DESCENDANT_OR_SELF)
        matchSingleToken(lexer, "following-sibling", XQueryTokenType.K_FOLLOWING_SIBLING)
        matchSingleToken(lexer, "following", XQueryTokenType.K_FOLLOWING)
        matchSingleToken(lexer, "::", XQueryTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (74) AbbrevForwardStep")
    fun testAbbrevForwardStep() {
        val lexer = createLexer()

        matchSingleToken(lexer, "@", XQueryTokenType.ATTRIBUTE_SELECTOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (76) ReverseAxis")
    fun testReverseAxis() {
        val lexer = createLexer()

        matchSingleToken(lexer, "parent", XQueryTokenType.K_PARENT)
        matchSingleToken(lexer, "ancestor", XQueryTokenType.K_ANCESTOR)
        matchSingleToken(lexer, "preceding-sibling", XQueryTokenType.K_PRECEDING_SIBLING)
        matchSingleToken(lexer, "preceding", XQueryTokenType.K_PRECEDING)
        matchSingleToken(lexer, "ancestor-or-self", XQueryTokenType.K_ANCESTOR_OR_SELF)
        matchSingleToken(lexer, "::", XQueryTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (77) AbbrevReverseStep")
    fun testAbbrevReverseStep() {
        val lexer = createLexer()

        matchSingleToken(lexer, "..", XQueryTokenType.PARENT_SELECTOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (80) Wildcard")
    fun testWildcard() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
        matchSingleToken(lexer, ":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (83) Predicate")
    fun testPredicate() {
        val lexer = createLexer()

        matchSingleToken(lexer, "[", XQueryTokenType.SQUARE_OPEN)
        matchSingleToken(lexer, "]", XQueryTokenType.SQUARE_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (87) VarRef")
    fun testVarRef() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (89) ParenthesizedExpr")
    fun testParenthesizedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (90) ContextItemExpr")
    fun testContextItemExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, ".", XQueryTokenType.DOT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (91) OrderedExpr")
    fun testOrderedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "ordered", XQueryTokenType.K_ORDERED)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (92) UnorderedExpr")
    fun testUnorderedExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "unordered", XQueryTokenType.K_UNORDERED)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (93) FunctionCall")
    fun testFunctionCall() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (96) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("as single token using XQueryLexer")
        fun testDirElemConstructor_OpenXmlTagAsSingleToken() {
            val lexer = createXQueryLexer()

            matchSingleToken(lexer, "<", XQueryTokenType.LESS_THAN)
            matchSingleToken(lexer, ">", XQueryTokenType.GREATER_THAN)

            matchSingleToken(lexer, "</", XQueryTokenType.CLOSE_XML_TAG)
            matchSingleToken(lexer, "/>", XQueryTokenType.SELF_CLOSING_XML_TAG)

            lexer.start("<one:two/>")
            matchToken(lexer, "<one:two/>", 0, 0, 10, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("<one:two></one:two  >")
            matchToken(lexer, "<one:two>", 0, 0, 9, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
            matchToken(lexer, "</", 17, 9, 11, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "one", 12, 11, 14, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 12, 14, 15, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 12, 15, 18, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "  ", 12, 18, 20, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, ">", 12, 20, 21, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 21, 21, null)

            lexer.start("<one:two  ></one:two>")
            matchToken(lexer, "<one:two  >", 0, 0, 11, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
            matchToken(lexer, "</", 17, 11, 13, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "one", 12, 13, 16, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 12, 16, 17, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 12, 17, 20, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 20, 21, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 21, 21, null)

            lexer.start("<one:two/*/>")
            matchToken(lexer, "<one:two", 0, 0, 8, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "/", 0, 8, 9, XQueryTokenType.DIRECT_DESCENDANTS_PATH)
            matchToken(lexer, "*", 0, 9, 10, XQueryTokenType.STAR)
            matchToken(lexer, "/>", 0, 10, 12, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 12, 12, null)

            lexer.start("<one:two//*/>")
            matchToken(lexer, "<one:two", 0, 0, 8, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "//", 0, 8, 10, XQueryTokenType.ALL_DESCENDANTS_PATH)
            matchToken(lexer, "*", 0, 10, 11, XQueryTokenType.STAR)
            matchToken(lexer, "/>", 0, 11, 13, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("1 < fn:abs (")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "< fn:abs ", 0, 2, 11, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "(", 0, 11, 12, XQueryTokenType.PARENTHESIS_OPEN)
            matchToken(lexer, "", 0, 12, 12, null)

            lexer.start("1 <fn:abs (")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<fn:abs ", 0, 2, 10, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "(", 0, 10, 11, XQueryTokenType.PARENTHESIS_OPEN)
            matchToken(lexer, "", 0, 11, 11, null)

            lexer.start("1 < fn:abs #")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "< fn:abs ", 0, 2, 11, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "#", 0, 11, 12, XQueryTokenType.FUNCTION_REF_OPERATOR)
            matchToken(lexer, "", 0, 12, 12, null)

            lexer.start("1 <fn:abs #")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<fn:abs ", 0, 2, 10, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "#", 0, 10, 11, XQueryTokenType.FUNCTION_REF_OPERATOR)
            matchToken(lexer, "", 0, 11, 11, null)

            lexer.start("1 < 2")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<", 0, 2, 3, XQueryTokenType.LESS_THAN)
            matchToken(lexer, " ", 0, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "2", 0, 4, 5, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "", 0, 5, 5, null)
        }

        @Test
        @DisplayName("as single token using XQueryLexer, adding an xml element")
        fun testDirElemConstructor_OpenXmlTagAsSingleToken_AddingXmlElement() {
            val lexer = createXQueryLexer()

            lexer.start("<<a")
            matchToken(lexer, "<<", 0, 0, 2, XQueryTokenType.NODE_BEFORE)
            matchToken(lexer, "a", 0, 2, 3, XPathTokenType.NCNAME)
            matchToken(lexer, "", 0, 3, 3, null)

            lexer.start("<<a/>")
            matchToken(lexer, "<", 0, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "<a/>", 0, 1, 5, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
            matchToken(lexer, "", 0, 5, 5, null)

            lexer.start("<a<a/>")
            matchToken(lexer, "<a", 0, 0, 2, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "<a/>", 0, 2, 6, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
            matchToken(lexer, "", 0, 6, 6, null)

            lexer.start("<a <a/>")
            matchToken(lexer, "<a ", 0, 0, 3, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG)
            matchToken(lexer, "<a/>", 0, 3, 7, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
            matchToken(lexer, "", 0, 7, 7, null)
        }

        @Test
        @DisplayName("maybe direct element constructor state")
        fun testDirElemConstructor_MaybeDirElem() {
            val lexer = createLexer()

            lexer.start("<one:two/>", 0, 10, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR)
            matchToken(lexer, "<", 29, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "one", 29, 1, 4, XPathTokenType.NCNAME)
            matchToken(lexer, ":", 29, 4, 5, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "two", 29, 5, 8, XPathTokenType.NCNAME)
            matchToken(lexer, "/>", 29, 8, 10, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 29, 10, 10, null)

            lexer.start("<one:two>", 0, 9, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR)
            matchToken(lexer, "<", 29, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "one", 29, 1, 4, XPathTokenType.NCNAME)
            matchToken(lexer, ":", 29, 4, 5, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "two", 29, 5, 8, XPathTokenType.NCNAME)
            matchToken(lexer, ">", 29, 8, 9, XQueryTokenType.GREATER_THAN)
            matchToken(lexer, "", 29, 9, 9, null)

            lexer.start("<  one:two  ", 0, 12, STATE_MAYBE_DIR_ELEM_CONSTRUCTOR)
            matchToken(lexer, "<", 29, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "  ", 29, 1, 3, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "one", 29, 3, 6, XPathTokenType.NCNAME)
            matchToken(lexer, ":", 29, 6, 7, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "two", 29, 7, 10, XPathTokenType.NCNAME)
            matchToken(lexer, "  ", 29, 10, 12, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "", 29, 12, 12, null)
        }

        @Test
        @DisplayName("start direct element constructor state")
        fun testDirElemConstructor_StartDirElem() {
            val lexer = createLexer()

            lexer.start("<one:two/>", 0, 10, STATE_START_DIR_ELEM_CONSTRUCTOR)
            matchToken(lexer, "<", 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "one", 11, 1, 4, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 11, 4, 5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 11, 5, 8, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "/>", 11, 8, 10, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("<one:two>", 0, 9, STATE_START_DIR_ELEM_CONSTRUCTOR)
            matchToken(lexer, "<", 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "one", 11, 1, 4, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 11, 4, 5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 11, 5, 8, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 11, 8, 9, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 17, 9, 9, null)

            lexer.start("<  one:two  ", 0, 12, STATE_START_DIR_ELEM_CONSTRUCTOR)
            matchToken(lexer, "<", 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "  ", 30, 1, 3, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "one", 11, 3, 6, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 11, 6, 7, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 11, 7, 10, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "  ", 11, 10, 12, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "", 25, 12, 12, null)
        }

        @Test
        @DisplayName("as separate tokens using CombinedLexer")
        fun testDirElemConstructor() {
            val lexer = createLexer()

            matchSingleToken(lexer, "<", XQueryTokenType.LESS_THAN)
            matchSingleToken(lexer, ">", XQueryTokenType.GREATER_THAN)

            matchSingleToken(lexer, "</", XQueryTokenType.CLOSE_XML_TAG)
            matchSingleToken(lexer, "/>", XQueryTokenType.SELF_CLOSING_XML_TAG)

            lexer.start("<one:two/>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "one", 0x60000000 or 11, 1, 4, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 0x60000000 or 11, 4, 5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 0x60000000 or 11, 5, 8, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "/>", 0x60000000 or 11, 8, 10, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("<one:two></one:two  >")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "one", 0x60000000 or 11, 1, 4, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 0x60000000 or 11, 4, 5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 0x60000000 or 11, 5, 8, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 8, 9, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "</", 17, 9, 11, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "one", 12, 11, 14, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 12, 14, 15, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 12, 15, 18, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "  ", 12, 18, 20, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, ">", 12, 20, 21, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 21, 21, null)

            lexer.start("<one:two  ></one:two>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "one", 0x60000000 or 11, 1, 4, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 0x60000000 or 11, 4, 5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 0x60000000 or 11, 5, 8, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "  ", 0x60000000 or 11, 8, 10, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, ">", 0x60000000 or 25, 10, 11, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "</", 17, 11, 13, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "one", 12, 13, 16, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 12, 16, 17, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 12, 17, 20, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 20, 21, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 21, 21, null)

            lexer.start("<one:two//*/>")
            matchToken(lexer, "<", 0x50000000 or 29, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "one", 0x50000000 or 29, 1, 4, XPathTokenType.NCNAME)
            matchToken(lexer, ":", 0x50000000 or 29, 4, 5, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "two", 0x50000000 or 29, 5, 8, XPathTokenType.NCNAME)
            matchToken(lexer, "//", 0, 8, 10, XQueryTokenType.ALL_DESCENDANTS_PATH)
            matchToken(lexer, "*", 0, 10, 11, XQueryTokenType.STAR)
            matchToken(lexer, "/>", 0, 11, 13, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("1 < fn:abs (")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<", 0x50000000 or 29, 2, 3, XQueryTokenType.LESS_THAN)
            matchToken(lexer, " ", 0x50000000 or 29, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "fn", 0x50000000 or 29, 4, 6, XQueryTokenType.K_FN)
            matchToken(lexer, ":", 0x50000000 or 29, 6, 7, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "abs", 0x50000000 or 29, 7, 10, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 0x50000000 or 29, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "(", 0, 11, 12, XQueryTokenType.PARENTHESIS_OPEN)
            matchToken(lexer, "", 0, 12, 12, null)

            lexer.start("1 <fn:abs (")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<", 0x50000000 or 29, 2, 3, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "fn", 0x50000000 or 29, 3, 5, XQueryTokenType.K_FN)
            matchToken(lexer, ":", 0x50000000 or 29, 5, 6, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "abs", 0x50000000 or 29, 6, 9, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 0x50000000 or 29, 9, 10, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "(", 0, 10, 11, XQueryTokenType.PARENTHESIS_OPEN)
            matchToken(lexer, "", 0, 11, 11, null)

            lexer.start("1 < fn:abs #")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<", 0x50000000 or 29, 2, 3, XQueryTokenType.LESS_THAN)
            matchToken(lexer, " ", 0x50000000 or 29, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "fn", 0x50000000 or 29, 4, 6, XQueryTokenType.K_FN)
            matchToken(lexer, ":", 0x50000000 or 29, 6, 7, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "abs", 0x50000000 or 29, 7, 10, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 0x50000000 or 29, 10, 11, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "#", 0, 11, 12, XQueryTokenType.FUNCTION_REF_OPERATOR)
            matchToken(lexer, "", 0, 12, 12, null)

            lexer.start("1 <fn:abs #")
            matchToken(lexer, "1", 0, 0, 1, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, " ", 0, 1, 2, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<", 0x50000000 or 29, 2, 3, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "fn", 0x50000000 or 29, 3, 5, XQueryTokenType.K_FN)
            matchToken(lexer, ":", 0x50000000 or 29, 5, 6, XPathTokenType.QNAME_SEPARATOR)
            matchToken(lexer, "abs", 0x50000000 or 29, 6, 9, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 0x50000000 or 29, 9, 10, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "#", 0, 10, 11, XQueryTokenType.FUNCTION_REF_OPERATOR)
            matchToken(lexer, "", 0, 11, 11, null)
        }

        @Test
        @DisplayName("as separate tokens using CombinedLexer, adding an xml element")
        fun testDirElemConstructor_AddingXmlElement() {
            val lexer = createLexer()

            lexer.start("<<a")
            matchToken(lexer, "<<", 0, 0, 2, XQueryTokenType.NODE_BEFORE)
            matchToken(lexer, "a", 0, 2, 3, XPathTokenType.NCNAME)
            matchToken(lexer, "", 0, 3, 3, null)

            lexer.start("<<a/>")
            matchToken(lexer, "<", 0, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "<", 0x60000000 or 30, 1, 2, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 2, 3, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "/>", 0x60000000 or 11, 3, 5, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 5, 5, null)

            lexer.start("<a<a/>")
            matchToken(lexer, "<", 0x50000000 or 29, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "a", 0x50000000 or 29, 1, 2, XPathTokenType.NCNAME)
            matchToken(lexer, "<", 0x60000000 or 30, 2, 3, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 3, 4, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "/>", 0x60000000 or 11, 4, 6, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 6, 6, null)

            lexer.start("<a <a/>")
            matchToken(lexer, "<", 0x50000000 or 29, 0, 1, XQueryTokenType.LESS_THAN)
            matchToken(lexer, "a", 0x50000000 or 29, 1, 2, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 0x50000000 or 29, 2, 3, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "<", 0x60000000 or 30, 3, 4, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 4, 5, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "/>", 0x60000000 or 11, 5, 7, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 7, 7, null)
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (97) DirAttributeList ; XQuery 1.0 EBNF (98) DirAttributeValue")
    internal inner class DirAttributeList {
        @Test
        @DisplayName("as single token using XQueryLexer")
        fun testDirAttributeList_OpenXmlTagAsSingleToken() {
            val lexer = createXQueryLexer()

            matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)

            lexer.start("<one:two  a:b  =  \"One\"  c:d  =  'Two'  />")
            matchToken(lexer, "<one:two  ", 0, 0, 10, XQueryTokenType.DIRELEM_OPEN_XML_TAG)
            matchToken(lexer, "a", 25, 10, 11, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, ":", 25, 11, 12, XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)
            matchToken(lexer, "b", 25, 12, 13, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, "  ", 25, 13, 15, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "=", 25, 15, 16, XQueryTokenType.XML_EQUAL)
            matchToken(lexer, "  ", 25, 16, 18, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "\"", 25, 18, 19, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One", 13, 19, 22, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "\"", 13, 22, 23, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "  ", 25, 23, 25, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "c", 25, 25, 26, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, ":", 25, 26, 27, XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)
            matchToken(lexer, "d", 25, 27, 28, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, "  ", 25, 28, 30, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "=", 25, 30, 31, XQueryTokenType.XML_EQUAL)
            matchToken(lexer, "  ", 25, 31, 33, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "'", 25, 33, 34, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "Two", 14, 34, 37, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "'", 14, 37, 38, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "  ", 25, 38, 40, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "/>", 25, 40, 42, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 42, 42, null)
        }

        @Test
        @DisplayName("as separate tokens using CombinedLexer")
        fun testDirAttributeList() {
            val lexer = createLexer()

            matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)

            lexer.start("<one:two  a:b  =  \"One\"  c:d  =  'Two'  />")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "one", 0x60000000 or 11, 1, 4, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ":", 0x60000000 or 11, 4, 5, XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            matchToken(lexer, "two", 0x60000000 or 11, 5, 8, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, "  ", 0x60000000 or 11, 8, 10, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "a", 25, 10, 11, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, ":", 25, 11, 12, XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)
            matchToken(lexer, "b", 25, 12, 13, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, "  ", 25, 13, 15, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "=", 25, 15, 16, XQueryTokenType.XML_EQUAL)
            matchToken(lexer, "  ", 25, 16, 18, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "\"", 25, 18, 19, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One", 13, 19, 22, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "\"", 13, 22, 23, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "  ", 25, 23, 25, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "c", 25, 25, 26, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, ":", 25, 26, 27, XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)
            matchToken(lexer, "d", 25, 27, 28, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, "  ", 25, 28, 30, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "=", 25, 30, 31, XQueryTokenType.XML_EQUAL)
            matchToken(lexer, "  ", 25, 31, 33, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "'", 25, 33, 34, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "Two", 14, 34, 37, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "'", 14, 37, 38, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "  ", 25, 38, 40, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "/>", 25, 40, 42, XQueryTokenType.SELF_CLOSING_XML_TAG)
            matchToken(lexer, "", 0, 42, 42, null)
        }

        @Test
        @DisplayName("incomplete closing tag")
        fun testDirAttributeList_IncompleteClosingTag() {
            val lexer = createLexer()

            lexer.start("<a b/")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, " ", 0x60000000 or 11, 2, 3, XQueryTokenType.XML_WHITE_SPACE)
            matchToken(lexer, "b", 25, 3, 4, XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            matchToken(lexer, "/", 25, 4, 5, XQueryTokenType.INVALID)
            matchToken(lexer, "", 25, 5, 5, null)
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (98) DirAttributeValue")
    internal inner class DirAttributeValue {
        @Test
        @DisplayName("XQuery 1.0 EBNF (99) QuotAttrValueContent ; XQuery 1.0 EBNF (149) QuotAttrContentChar")
        fun testDirAttributeValue_QuotAttrValueContent() {
            val lexer = createLexer()

            lexer.start("\"One {2}<& \u3053\u3093\u3070\u3093\u306F.\"", 0, 18, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One ", 13, 1, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "{", 13, 5, 6, XQueryTokenType.BLOCK_OPEN)
            matchToken(lexer, "2", 15, 6, 7, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}", 15, 7, 8, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "<", 13, 8, 9, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "&", 13, 9, 10, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            matchToken(
                lexer,
                " \u3053\u3093\u3070\u3093\u306F.",
                13,
                10,
                17,
                XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS
            )
            matchToken(lexer, "\"", 13, 17, 18, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 18, 18, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (100) AposAttrValueContent ; XQuery 1.0 EBNF (150) AposAttrContentChar")
        fun testDirAttributeValue_AposAttrValueContent() {
            val lexer = createLexer()

            lexer.start("'One {2}<& \u3053\u3093\u3070\u3093\u306F.}'", 0, 19, 11)
            matchToken(lexer, "'", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One ", 14, 1, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "{", 14, 5, 6, XQueryTokenType.BLOCK_OPEN)
            matchToken(lexer, "2", 16, 6, 7, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}", 16, 7, 8, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "<", 14, 8, 9, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "&", 14, 9, 10, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, " \u3053\u3093\u3070\u3093\u306F.", 14, 10, 17, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "}", 14, 17, 18, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "'", 14, 18, 19, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 19, 19, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent")
        fun testDirAttributeValue_CommonContent() {
            val lexer = createLexer()

            lexer.start("\"{{}}\"", 0, 6, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "{{", 13, 1, 3, XQueryTokenType.XML_ESCAPED_CHARACTER)
            matchToken(lexer, "}}", 13, 3, 5, XQueryTokenType.XML_ESCAPED_CHARACTER)
            matchToken(lexer, "\"", 13, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 6, 6, null)

            lexer.start("'{{}}'", 0, 6, 11)
            matchToken(lexer, "'", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "{{", 14, 1, 3, XQueryTokenType.XML_ESCAPED_CHARACTER)
            matchToken(lexer, "}}", 14, 3, 5, XQueryTokenType.XML_ESCAPED_CHARACTER)
            matchToken(lexer, "'", 14, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 6, 6, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        fun testDirAttributeValue_PredefinedEntityRef() {
            val lexer = createLexer()

            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            lexer.start("\"One&abc;&aBc;&Abc;&ABC;&a4;&a;Two\"", 0, 35, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One", 13, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "&abc;", 13, 4, 9, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&aBc;", 13, 9, 14, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&Abc;", 13, 14, 19, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&ABC;", 13, 19, 24, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a4;", 13, 24, 28, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a;", 13, 28, 31, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "Two", 13, 31, 34, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "\"", 13, 34, 35, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 35, 35, null)

            lexer.start("'One&abc;&aBc;&Abc;&ABC;&a4;&a;Two'", 0, 35, 11)
            matchToken(lexer, "'", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One", 14, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "&abc;", 14, 4, 9, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&aBc;", 14, 9, 14, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&Abc;", 14, 14, 19, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&ABC;", 14, 19, 24, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a4;", 14, 24, 28, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a;", 14, 28, 31, XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "Two", 14, 31, 34, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "'", 14, 34, 35, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 35, 35, null)

            lexer.start("\"&\"", 0, 3, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "&", 13, 1, 2, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "\"", 13, 2, 3, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 3, 3, null)

            lexer.start("\"&abc!\"", 0, 7, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "&abc", 13, 1, 5, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "!", 13, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "\"", 13, 6, 7, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 7, 7, null)

            lexer.start("\"& \"", 0, 4, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "&", 13, 1, 2, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, " ", 13, 2, 3, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "\"", 13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 4, 4, null)

            lexer.start("\"&", 0, 2, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "&", 13, 1, 2, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 13, 2, 2, null)

            lexer.start("\"&abc", 0, 5, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "&abc", 13, 1, 5, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 13, 5, 5, null)

            lexer.start("\"&;\"", 0, 4, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "&;", 13, 1, 3, XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)
            matchToken(lexer, "\"", 13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 4, 4, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (146) EscapeQuot")
        fun testDirAttributeValue_EscapeQuot() {
            val lexer = createLexer()

            lexer.start("\"One\"\"Two\"", 0, 10, 11)
            matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One", 13, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "\"\"", 13, 4, 6, XQueryTokenType.XML_ESCAPED_CHARACTER)
            matchToken(lexer, "Two", 13, 6, 9, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "\"", 13, 9, 10, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 10, 10, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (147) EscapeApos")
        fun testDirAttributeValue_EscapeApos() {
            val lexer = createLexer()

            lexer.start("'One''Two'", 0, 10, 11)
            matchToken(lexer, "'", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            matchToken(lexer, "One", 14, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "''", 14, 4, 6, XQueryTokenType.XML_ESCAPED_CHARACTER)
            matchToken(lexer, "Two", 14, 6, 9, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            matchToken(lexer, "'", 14, 9, 10, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            matchToken(lexer, "", 11, 10, 10, null)
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class DirAttributeValue_CharRef {
            @Test
            @DisplayName("octal")
            fun testDirAttributeValue_CharRef_Octal() {
                val lexer = createLexer()

                lexer.start("\"One&#20;Two\"", 0, 13, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "One", 13, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "&#20;", 13, 4, 9, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 13, 9, 12, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "\"", 13, 12, 13, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 13, 13, null)

                lexer.start("'One&#20;Two'", 0, 13, 11)
                matchToken(lexer, "'", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "One", 14, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "&#20;", 14, 4, 9, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 14, 9, 12, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "'", 14, 12, 13, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 13, 13, null)

                lexer.start("\"&#\"", 0, 4, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#", 13, 1, 3, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "\"", 13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 4, 4, null)

                lexer.start("\"&# \"", 0, 5, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#", 13, 1, 3, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 13, 3, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "\"", 13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 5, 5, null)

                lexer.start("\"&#", 0, 3, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#", 13, 1, 3, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 13, 3, 3, null)

                lexer.start("\"&#12", 0, 5, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#12", 13, 1, 5, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 13, 5, 5, null)

                lexer.start("\"&#;\"", 0, 5, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#;", 13, 1, 4, XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "\"", 13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 5, 5, null)
            }

            @Test
            @DisplayName("hexadecimal")
            fun testDirAttributeValue_CharRef_Hexadecimal() {
                val lexer = createLexer()

                lexer.start("\"One&#x20;&#xae;&#xDC;Two\"", 0, 26, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "One", 13, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "&#x20;", 13, 4, 10, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "&#xae;", 13, 10, 16, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "&#xDC;", 13, 16, 22, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 13, 22, 25, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "\"", 13, 25, 26, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 26, 26, null)

                lexer.start("'One&#x20;&#xae;&#xDC;Two'", 0, 26, 11)
                matchToken(lexer, "'", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "One", 14, 1, 4, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "&#x20;", 14, 4, 10, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "&#xae;", 14, 10, 16, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "&#xDC;", 14, 16, 22, XQueryTokenType.XML_CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 14, 22, 25, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "'", 14, 25, 26, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 26, 26, null)

                lexer.start("\"&#x\"", 0, 5, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#x", 13, 1, 4, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "\"", 13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 5, 5, null)

                lexer.start("\"&#x \"", 0, 6, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#x", 13, 1, 4, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 13, 4, 5, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "\"", 13, 5, 6, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 6, 6, null)

                lexer.start("\"&#x", 0, 4, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#x", 13, 1, 4, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 13, 4, 4, null)

                lexer.start("\"&#x12", 0, 6, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#x12", 13, 1, 6, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 13, 6, 6, null)

                lexer.start("\"&#x;&#x2G;&#x2g;&#xg2;\"", 0, 24, 11)
                matchToken(lexer, "\"", 11, 0, 1, XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                matchToken(lexer, "&#x;", 13, 1, 5, XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "&#x2", 13, 5, 9, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "G;", 13, 9, 11, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "&#x2", 13, 11, 15, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g;", 13, 15, 17, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "&#x", 13, 17, 20, XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g2;", 13, 20, 23, XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                matchToken(lexer, "\"", 13, 23, 24, XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                matchToken(lexer, "", 11, 24, 24, null)
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (101) DirElemContent")
    internal inner class DirElemContent {
        @Test
        @DisplayName("XQuery 1.0 EBNF (148) ElementContentChar")
        fun testDirElemContent_ElementContentChar() {
            val lexer = createLexer()

            lexer.start("<a>One {2}<& \u3053\u3093\u3070\u3093\u306F.}</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "One ", 17, 3, 7, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "{", 17, 7, 8, XQueryTokenType.BLOCK_OPEN)
            matchToken(lexer, "2", 18, 8, 9, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}", 18, 9, 10, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "<", 17, 10, 11, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "&", 17, 11, 12, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, " \u3053\u3093\u3070\u3093\u306F.", 17, 12, 19, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "}", 17, 19, 20, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "</", 17, 20, 22, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 22, 23, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 23, 24, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 24, 24, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (96) DirElemConstructor")
        fun testDirElemContent_DirElemConstructor() {
            val lexer = createLexer()

            lexer.start("<a>One <b>Two</b> Three</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "One ", 17, 3, 7, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "<", 17, 7, 8, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "b", 11, 8, 9, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 11, 9, 10, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "Two", 17, 10, 13, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "</", 17, 13, 15, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "b", 12, 15, 16, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 16, 17, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, " Three", 17, 17, 23, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "</", 17, 23, 25, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 25, 26, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 26, 27, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 27, 27, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (103) DirCommentConstructor")
        fun testDirElemContent_DirCommentConstructor() {
            val lexer = createLexer()

            lexer.start("<!<!-", 0, 5, 17)
            matchToken(lexer, "<!", 17, 0, 2, XQueryTokenType.INVALID)
            matchToken(lexer, "<!-", 17, 2, 5, XQueryTokenType.INVALID)
            matchToken(lexer, "", 17, 5, 5, null)

            lexer.start("<a>One <!-- 2 --> Three</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "One ", 17, 3, 7, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "<!--", 17, 7, 11, XQueryTokenType.XML_COMMENT_START_TAG)
            matchToken(lexer, " 2 ", 19, 11, 14, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "-->", 19, 14, 17, XQueryTokenType.XML_COMMENT_END_TAG)
            matchToken(lexer, " Three", 17, 17, 23, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "</", 17, 23, 25, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 25, 26, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 26, 27, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 27, 27, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (107) CDataSection")
        fun testDirElemContent_CDataSection() {
            val lexer = createLexer()

            lexer.start("<!<![<![C<![CD<![CDA<![CDAT<![CDATA", 0, 35, 17)
            matchToken(lexer, "<!", 17, 0, 2, XQueryTokenType.INVALID)
            matchToken(lexer, "<![", 17, 2, 5, XQueryTokenType.INVALID)
            matchToken(lexer, "<![C", 17, 5, 9, XQueryTokenType.INVALID)
            matchToken(lexer, "<![CD", 17, 9, 14, XQueryTokenType.INVALID)
            matchToken(lexer, "<![CDA", 17, 14, 20, XQueryTokenType.INVALID)
            matchToken(lexer, "<![CDAT", 17, 20, 27, XQueryTokenType.INVALID)
            matchToken(lexer, "<![CDATA", 17, 27, 35, XQueryTokenType.INVALID)
            matchToken(lexer, "", 17, 35, 35, null)

            lexer.start("<a>One <![CDATA[ 2 ]]> Three</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "One ", 17, 3, 7, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "<![CDATA[", 17, 7, 16, XQueryTokenType.CDATA_SECTION_START_TAG)
            matchToken(lexer, " 2 ", 20, 16, 19, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "]]>", 20, 19, 22, XQueryTokenType.CDATA_SECTION_END_TAG)
            matchToken(lexer, " Three", 17, 22, 28, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "</", 17, 28, 30, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 30, 31, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 31, 32, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 32, 32, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (105) DirPIConstructor")
        fun testDirElemContent_DirPIConstructor() {
            val lexer = createLexer()

            lexer.start("<a><?for  6^gkgw~*?g?></a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "<?", 17, 3, 5, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            matchToken(lexer, "for", 23, 5, 8, XPathTokenType.NCNAME)
            matchToken(lexer, "  ", 23, 8, 10, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "6^gkgw~*?g", 24, 10, 20, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
            matchToken(lexer, "?>", 24, 20, 22, XQueryTokenType.PROCESSING_INSTRUCTION_END)
            matchToken(lexer, "</", 17, 22, 24, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 24, 25, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 25, 26, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 26, 26, null)

            lexer.start("<a><?for?></a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "<?", 17, 3, 5, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            matchToken(lexer, "for", 23, 5, 8, XPathTokenType.NCNAME)
            matchToken(lexer, "?>", 23, 8, 10, XQueryTokenType.PROCESSING_INSTRUCTION_END)
            matchToken(lexer, "</", 17, 10, 12, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 12, 13, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 13, 14, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 14, 14, null)

            lexer.start("<a><?*?$?></a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "<?", 17, 3, 5, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            matchToken(lexer, "*", 23, 5, 6, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "?", 23, 6, 7, XQueryTokenType.INVALID)
            matchToken(lexer, "$", 23, 7, 8, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "?>", 23, 8, 10, XQueryTokenType.PROCESSING_INSTRUCTION_END)
            matchToken(lexer, "</", 17, 10, 12, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 12, 13, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 13, 14, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 14, 14, null)

            lexer.start("<?a ?", 0, 5, 17)
            matchToken(lexer, "<?", 17, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            matchToken(lexer, "a", 23, 2, 3, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 23, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "?", 24, 4, 5, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
            matchToken(lexer, "", 6, 5, 5, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 17, 5, 5, null)

            lexer.start("<?a ", 0, 4, 17)
            matchToken(lexer, "<?", 17, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            matchToken(lexer, "a", 23, 2, 3, XPathTokenType.NCNAME)
            matchToken(lexer, " ", 23, 3, 4, XPathTokenType.WHITE_SPACE)
            matchToken(lexer, "", 24, 4, 4, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent")
        fun testDirElemContent_CommonContent() {
            val lexer = createLexer()

            lexer.start("<a>{{}}</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "{{", 17, 3, 5, XPathTokenType.ESCAPED_CHARACTER)
            matchToken(lexer, "}}", 17, 5, 7, XPathTokenType.ESCAPED_CHARACTER)
            matchToken(lexer, "</", 17, 7, 9, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 9, 10, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 10, 11, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 11, 11, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        fun testDirElemContent_PredefinedEntityRef() {
            val lexer = createLexer()

            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            lexer.start("<a>One&abc;&aBc;&Abc;&ABC;&a4;&a;Two</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "One", 17, 3, 6, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "&abc;", 17, 6, 11, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&aBc;", 17, 11, 16, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&Abc;", 17, 16, 21, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&ABC;", 17, 21, 26, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a4;", 17, 26, 30, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a;", 17, 30, 33, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "Two", 17, 33, 36, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "</", 17, 36, 38, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 38, 39, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 39, 40, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 40, 40, null)

            lexer.start("<a>&</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "&", 17, 3, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "</", 17, 4, 6, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 6, 7, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 7, 8, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 8, 8, null)

            lexer.start("<a>&abc!</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "&abc", 17, 3, 7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "!", 17, 7, 8, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "</", 17, 8, 10, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 10, 11, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 11, 12, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 12, 12, null)

            lexer.start("<a>&")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "&", 17, 3, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 17, 4, 4, null)

            lexer.start("<a>&abc")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "&abc", 17, 3, 7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 17, 7, 7, null)

            lexer.start("<a>&;</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "&;", 17, 3, 5, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
            matchToken(lexer, "</", 17, 5, 7, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 7, 8, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 8, 9, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 9, 9, null)
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class DirElemContent_CharRef {
            @Test
            @DisplayName("octal")
            fun testDirElemContent_CharRef_Octal() {
                val lexer = createLexer()

                lexer.start("<a>One&#20;Two</a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "One", 17, 3, 6, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "&#20;", 17, 6, 11, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 17, 11, 14, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "</", 17, 14, 16, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 16, 17, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 17, 18, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 18, 18, null)

                lexer.start("<a>&#</a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#", 17, 3, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "</", 17, 5, 7, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 7, 8, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 8, 9, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 9, 9, null)

                lexer.start("<a>&# </a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#", 17, 3, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 17, 5, 6, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "</", 17, 6, 8, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 8, 9, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 9, 10, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 10, 10, null)

                lexer.start("<a>&#")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#", 17, 3, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 17, 5, 5, null)

                lexer.start("<a>&#12")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#12", 17, 3, 7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 17, 7, 7, null)

                lexer.start("<a>&#;</a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#;", 17, 3, 6, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "</", 17, 6, 8, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 8, 9, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 9, 10, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 10, 10, null)
            }

            @Test
            @DisplayName("hexadecimal")
            fun testDirElemContent_CharRef_Hexadecimal() {
                val lexer = createLexer()

                lexer.start("<a>One&#x20;&#xae;&#xDC;Two</a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "One", 17, 3, 6, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "&#x20;", 17, 6, 12, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xae;", 17, 12, 18, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xDC;", 17, 18, 24, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 17, 24, 27, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "</", 17, 27, 29, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 29, 30, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 30, 31, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 31, 31, null)

                lexer.start("<a>&#x</a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#x", 17, 3, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "</", 17, 6, 8, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 8, 9, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 9, 10, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 10, 10, null)

                lexer.start("<a>&#x </a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#x", 17, 3, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 17, 6, 7, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "</", 17, 7, 9, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 9, 10, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 10, 11, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 11, 11, null)

                lexer.start("<a>&#x")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#x", 17, 3, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 17, 6, 6, null)

                lexer.start("<a>&#x12")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#x12", 17, 3, 8, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 17, 8, 8, null)

                lexer.start("<a>&#x;&#x2G;&#x2g;&#xg2;</a>")
                matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
                matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "&#x;", 17, 3, 7, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "&#x2", 17, 7, 11, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "G;", 17, 11, 13, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "&#x2", 17, 13, 17, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g;", 17, 17, 19, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "&#x", 17, 19, 22, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g2;", 17, 22, 25, XQueryTokenType.XML_ELEMENT_CONTENTS)
                matchToken(lexer, "</", 17, 25, 27, XQueryTokenType.CLOSE_XML_TAG)
                matchToken(lexer, "a", 12, 27, 28, XQueryTokenType.XML_TAG_NCNAME)
                matchToken(lexer, ">", 12, 28, 29, XQueryTokenType.END_XML_TAG)
                matchToken(lexer, "", 0, 29, 29, null)
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (103) DirCommentConstructor ; XQuery 1.0 EBNF (104) DirCommentContents")
    internal inner class DirCommentConstructor {
        @Test
        @DisplayName("direct comment constructor")
        fun testDirCommentConstructor() {
            val lexer = createLexer()

            matchSingleToken(lexer, "<", 0, XQueryTokenType.LESS_THAN)
            matchSingleToken(lexer, "<!", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<!-", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<!--", 5, XQueryTokenType.XML_COMMENT_START_TAG)

            // Unary Minus
            lexer.start("--")
            matchToken(lexer, "-", 0, 0, 1, XQueryTokenType.MINUS)
            matchToken(lexer, "-", 0, 1, 2, XQueryTokenType.MINUS)
            matchToken(lexer, "", 0, 2, 2, null)

            matchSingleToken(lexer, "-->", XQueryTokenType.XML_COMMENT_END_TAG)

            lexer.start("<!-- Test")
            matchToken(lexer, "<!--", 0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG)
            matchToken(lexer, " Test", 5, 4, 9, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "", 6, 9, 9, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 9, 9, null)

            lexer.start("<!-- Test --")
            matchToken(lexer, "<!--", 0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG)
            matchToken(lexer, " Test --", 5, 4, 12, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "", 6, 12, 12, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 12, 12, null)

            lexer.start("<!-- Test -->")
            matchToken(lexer, "<!--", 0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG)
            matchToken(lexer, " Test ", 5, 4, 10, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "-->", 5, 10, 13, XQueryTokenType.XML_COMMENT_END_TAG)
            matchToken(lexer, "", 0, 13, 13, null)

            lexer.start("<!--\nMultiline\nComment\n-->")
            matchToken(lexer, "<!--", 0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG)
            matchToken(lexer, "\nMultiline\nComment\n", 5, 4, 23, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "-->", 5, 23, 26, XQueryTokenType.XML_COMMENT_END_TAG)
            matchToken(lexer, "", 0, 26, 26, null)

            lexer.start("<!---")
            matchToken(lexer, "<!--", 0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG)
            matchToken(lexer, "-", 5, 4, 5, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "", 6, 5, 5, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 5, 5, null)

            lexer.start("<!----")
            matchToken(lexer, "<!--", 0, 0, 4, XQueryTokenType.XML_COMMENT_START_TAG)
            matchToken(lexer, "--", 5, 4, 6, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "", 6, 6, 6, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 6, 6, null)
        }

        @Test
        @DisplayName("initial state")
        fun testDirCommentConstructor_InitialState() {
            val lexer = createLexer()

            lexer.start("<!-- Test", 4, 9, 5)
            matchToken(lexer, " Test", 5, 4, 9, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "", 6, 9, 9, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 9, 9, null)

            lexer.start("<!-- Test -->", 4, 13, 5)
            matchToken(lexer, " Test ", 5, 4, 10, XQueryTokenType.XML_COMMENT)
            matchToken(lexer, "-->", 5, 10, 13, XQueryTokenType.XML_COMMENT_END_TAG)
            matchToken(lexer, "", 0, 13, 13, null)
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (105) DirPIConstructor ; XQuery 1.0 EBNF (106) DirPIContents")
    fun testDirPIConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "<?", 21, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        matchSingleToken(lexer, "?>", 0, XQueryTokenType.PROCESSING_INSTRUCTION_END)


        lexer.start("<?for  6^gkgw~*?g?>")
        matchToken(lexer, "<?", 0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        matchToken(lexer, "for", 21, 2, 5, XPathTokenType.NCNAME)
        matchToken(lexer, "  ", 21, 5, 7, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "6^gkgw~*?g", 22, 7, 17, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
        matchToken(lexer, "?>", 22, 17, 19, XQueryTokenType.PROCESSING_INSTRUCTION_END)
        matchToken(lexer, "", 0, 19, 19, null)

        lexer.start("<?for?>")
        matchToken(lexer, "<?", 0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        matchToken(lexer, "for", 21, 2, 5, XPathTokenType.NCNAME)
        matchToken(lexer, "?>", 21, 5, 7, XQueryTokenType.PROCESSING_INSTRUCTION_END)
        matchToken(lexer, "", 0, 7, 7, null)

        lexer.start("<?*?$?>")
        matchToken(lexer, "<?", 0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        matchToken(lexer, "*", 21, 2, 3, XPathTokenType.BAD_CHARACTER)
        matchToken(lexer, "?", 21, 3, 4, XQueryTokenType.INVALID)
        matchToken(lexer, "$", 21, 4, 5, XPathTokenType.BAD_CHARACTER)
        matchToken(lexer, "?>", 21, 5, 7, XQueryTokenType.PROCESSING_INSTRUCTION_END)
        matchToken(lexer, "", 0, 7, 7, null)

        lexer.start("<?a ?")
        matchToken(lexer, "<?", 0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        matchToken(lexer, "a", 21, 2, 3, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 21, 3, 4, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "?", 22, 4, 5, XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
        matchToken(lexer, "", 6, 5, 5, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
        matchToken(lexer, "", 0, 5, 5, null)

        lexer.start("<?a ")
        matchToken(lexer, "<?", 0, 0, 2, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        matchToken(lexer, "a", 21, 2, 3, XPathTokenType.NCNAME)
        matchToken(lexer, " ", 21, 3, 4, XPathTokenType.WHITE_SPACE)
        matchToken(lexer, "", 22, 4, 4, null)
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (107) CDataSection ; XQuery 1.0 EBNF (108) CDataSectionContents")
    internal inner class CDataSection {
        @Test
        @DisplayName("cdata section")
        fun testCDataSection() {
            val lexer = createLexer()

            matchSingleToken(lexer, "<", 0, XQueryTokenType.LESS_THAN)
            matchSingleToken(lexer, "<!", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<![", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<![C", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<![CD", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<![CDA", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<![CDAT", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<![CDATA", 0, XQueryTokenType.INVALID)
            matchSingleToken(lexer, "<![CDATA[", 7, XQueryTokenType.CDATA_SECTION_START_TAG)

            matchSingleToken(lexer, "]", XQueryTokenType.SQUARE_CLOSE)

            lexer.start("]]")
            matchToken(lexer, "]", 0, 0, 1, XQueryTokenType.SQUARE_CLOSE)
            matchToken(lexer, "]", 0, 1, 2, XQueryTokenType.SQUARE_CLOSE)
            matchToken(lexer, "", 0, 2, 2, null)

            matchSingleToken(lexer, "]]>", XQueryTokenType.CDATA_SECTION_END_TAG)

            lexer.start("<![CDATA[ Test")
            matchToken(lexer, "<![CDATA[", 0, 0, 9, XQueryTokenType.CDATA_SECTION_START_TAG)
            matchToken(lexer, " Test", 7, 9, 14, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "", 6, 14, 14, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 14, 14, null)

            lexer.start("<![CDATA[ Test ]]")
            matchToken(lexer, "<![CDATA[", 0, 0, 9, XQueryTokenType.CDATA_SECTION_START_TAG)
            matchToken(lexer, " Test ]]", 7, 9, 17, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "", 6, 17, 17, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 17, 17, null)

            lexer.start("<![CDATA[ Test ]]>")
            matchToken(lexer, "<![CDATA[", 0, 0, 9, XQueryTokenType.CDATA_SECTION_START_TAG)
            matchToken(lexer, " Test ", 7, 9, 15, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "]]>", 7, 15, 18, XQueryTokenType.CDATA_SECTION_END_TAG)
            matchToken(lexer, "", 0, 18, 18, null)

            lexer.start("<![CDATA[\nMultiline\nComment\n]]>")
            matchToken(lexer, "<![CDATA[", 0, 0, 9, XQueryTokenType.CDATA_SECTION_START_TAG)
            matchToken(lexer, "\nMultiline\nComment\n", 7, 9, 28, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "]]>", 7, 28, 31, XQueryTokenType.CDATA_SECTION_END_TAG)
            matchToken(lexer, "", 0, 31, 31, null)

            lexer.start("<![CDATA[]")
            matchToken(lexer, "<![CDATA[", 0, 0, 9, XQueryTokenType.CDATA_SECTION_START_TAG)
            matchToken(lexer, "]", 7, 9, 10, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "", 6, 10, 10, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 10, 10, null)

            lexer.start("<![CDATA[]]")
            matchToken(lexer, "<![CDATA[", 0, 0, 9, XQueryTokenType.CDATA_SECTION_START_TAG)
            matchToken(lexer, "]]", 7, 9, 11, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "", 6, 11, 11, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 11, 11, null)
        }

        @Test
        @DisplayName("initial state")
        fun testCDataSection_InitialState() {
            val lexer = createLexer()

            lexer.start("<![CDATA[ Test", 9, 14, 7)
            matchToken(lexer, " Test", 7, 9, 14, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "", 6, 14, 14, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 14, 14, null)

            lexer.start("<![CDATA[ Test ]]>", 9, 18, 7)
            matchToken(lexer, " Test ", 7, 9, 15, XQueryTokenType.CDATA_SECTION)
            matchToken(lexer, "]]>", 7, 15, 18, XQueryTokenType.CDATA_SECTION_END_TAG)
            matchToken(lexer, "", 0, 18, 18, null)
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (110) CompDocConstructor")
    fun testCompDocConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "document", XQueryTokenType.K_DOCUMENT)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (111) CompElemConstructor")
    fun testCompElemConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "element", XQueryTokenType.K_ELEMENT)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (113) CompAttrConstructor")
    fun testCompAttrConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute", XQueryTokenType.K_ATTRIBUTE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (114) CompTextConstructor")
    fun testCompTextConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "text", XQueryTokenType.K_TEXT)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (115) CompCommentConstructor")
    fun testCompCommentConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "comment", XQueryTokenType.K_COMMENT)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (116) CompPIConstructor")
    fun testCompPIConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "processing-instruction", XQueryTokenType.K_PROCESSING_INSTRUCTION)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (118) TypeDeclaration")
    fun testTypeDeclaration() {
        val lexer = createLexer()

        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (119) SequenceType")
    fun testSequenceType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "empty-sequence", XQueryTokenType.K_EMPTY_SEQUENCE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (120) OccurrenceIndicator")
    fun testOccurrenceIndicator() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL)
        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
        matchSingleToken(lexer, "+", XQueryTokenType.PLUS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (121) ItemType")
    fun testItemType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "item", XQueryTokenType.K_ITEM)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (124) AnyKindTest")
    fun testAnyKindTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "node", XQueryTokenType.K_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (125) DocumentTest")
    fun testDocumentTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "document-node", XQueryTokenType.K_DOCUMENT_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (126) TextTest")
    fun testTextTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "text", XQueryTokenType.K_TEXT)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (127) CompTest")
    fun testCommentTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "comment", XQueryTokenType.K_COMMENT)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (128) PITest")
    fun testPITest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "processing-instruction", XQueryTokenType.K_PROCESSING_INSTRUCTION)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
    fun testAttributeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "attribute", XQueryTokenType.K_ATTRIBUTE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (130) AttribNameOrWildcard")
    fun testAttribNameOrWildcard() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (131) SchemaAttributeTest")
    fun testSchemaAttributeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-attribute", XQueryTokenType.K_SCHEMA_ATTRIBUTE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
    fun testElementTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "element", XQueryTokenType.K_ELEMENT)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (134) ElementNameOrWildcard")
    fun testElementNameOrWildcard() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (135) SchemaElementTest")
    fun testSchemaElementTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "schema-element", XQueryTokenType.K_SCHEMA_ELEMENT)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (141) IntegerLiteral")
    fun integerLiteral() {
        val lexer = createLexer()

        lexer.start("1234")
        matchToken(lexer, "1234", 0, 0, 4, XPathTokenType.INTEGER_LITERAL)
        matchToken(lexer, "", 0, 4, 4, null)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (142) DecimalLiteral")
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
    @DisplayName("XQuery 1.0 EBNF (143) DoubleLiteral")
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
    @DisplayName("XQuery 1.0 EBNF (144) StringLiteral")
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
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        fun predefinedEntityRef() {
            val lexer = createLexer()

            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            lexer.start("\"One&abc;&aBc;&Abc;&ABC;&a4;&a;Two\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "One", 1, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "&abc;", 1, 4, 9, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&aBc;", 1, 9, 14, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&Abc;", 1, 14, 19, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&ABC;", 1, 19, 24, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a4;", 1, 24, 28, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a;", 1, 28, 31, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "Two", 1, 31, 34, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 34, 35, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 35, 35, null)

            lexer.start("'One&abc;&aBc;&Abc;&ABC;&a4;&a;Two'")
            matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "One", 2, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "&abc;", 2, 4, 9, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&aBc;", 2, 9, 14, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&Abc;", 2, 14, 19, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&ABC;", 2, 19, 24, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a4;", 2, 24, 28, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a;", 2, 28, 31, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "Two", 2, 31, 34, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "'", 2, 34, 35, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 35, 35, null)

            lexer.start("\"&\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "&", 1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "\"", 1, 2, 3, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 3, 3, null)

            lexer.start("\"&abc!\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "&abc", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "!", 1, 5, 6, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 6, 7, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 7, 7, null)

            lexer.start("\"& \"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "&", 1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, " ", 1, 2, 3, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "\"", 1, 3, 4, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 4, 4, null)

            lexer.start("\"&")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "&", 1, 1, 2, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 1, 2, 2, null)

            lexer.start("\"&abc")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "&abc", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 1, 5, 5, null)

            lexer.start("\"&;\"")
            matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
            matchToken(lexer, "&;", 1, 1, 3, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
            matchToken(lexer, "\"", 1, 3, 4, XPathTokenType.STRING_LITERAL_END)
            matchToken(lexer, "", 0, 4, 4, null)

            lexer.start("&")
            matchToken(lexer, "&", 0, 0, 1, XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING)
            matchToken(lexer, "", 0, 1, 1, null)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (146) EscapeQuot")
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
        @DisplayName("XQuery 1.0 EBNF (147) EscapeApos")
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

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("octal")
            fun octal() {
                val lexer = createLexer()

                lexer.start("\"One&#20;Two\"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "One", 1, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#20;", 1, 4, 9, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 1, 9, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "\"", 1, 12, 13, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 13, 13, null)

                lexer.start("'One&#20;Two'")
                matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "One", 2, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#20;", 2, 4, 9, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 2, 9, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "'", 2, 12, 13, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 13, 13, null)

                lexer.start("\"&#\"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "\"", 1, 3, 4, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 4, 4, null)

                lexer.start("\"&# \"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 1, 3, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "\"", 1, 4, 5, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 5, 5, null)

                lexer.start("\"&#")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#", 1, 1, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 1, 3, 3, null)

                lexer.start("\"&#12")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#12", 1, 1, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 1, 5, 5, null)

                lexer.start("\"&#;\"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#;", 1, 1, 4, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "\"", 1, 4, 5, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 5, 5, null)
            }

            @Test
            @DisplayName("hexadecimal")
            fun hexadecimal() {
                val lexer = createLexer()

                lexer.start("\"One&#x20;&#xae;&#xDC;Two\"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "One", 1, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#x20;", 1, 4, 10, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xae;", 1, 10, 16, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xDC;", 1, 16, 22, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 1, 22, 25, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "\"", 1, 25, 26, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 26, 26, null)

                lexer.start("'One&#x20;&#xae;&#xDC;Two'")
                matchToken(lexer, "'", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "One", 2, 1, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#x20;", 2, 4, 10, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xae;", 2, 10, 16, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xDC;", 2, 16, 22, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 2, 22, 25, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "'", 2, 25, 26, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 26, 26, null)

                lexer.start("\"&#x\"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#x", 1, 1, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "\"", 1, 4, 5, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 5, 5, null)

                lexer.start("\"&#x \"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#x", 1, 1, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 1, 4, 5, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "\"", 1, 5, 6, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 6, 6, null)

                lexer.start("\"&#x")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#x", 1, 1, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 1, 4, 4, null)

                lexer.start("\"&#x12")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#x12", 1, 1, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 1, 6, 6, null)

                lexer.start("\"&#x;&#x2G;&#x2g;&#xg2;\"")
                matchToken(lexer, "\"", 0, 0, 1, XPathTokenType.STRING_LITERAL_START)
                matchToken(lexer, "&#x;", 1, 1, 5, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "&#x2", 1, 5, 9, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "G;", 1, 9, 11, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#x2", 1, 11, 15, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g;", 1, 15, 17, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#x", 1, 17, 20, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g2;", 1, 20, 23, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "\"", 1, 23, 24, XPathTokenType.STRING_LITERAL_END)
                matchToken(lexer, "", 0, 24, 24, null)
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (151) Comment ; XQuery 1.0 EBNF (159) CommentContents")
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
    @DisplayName("XQuery 1.0 EBNF (154) QName ; Namespaces in XML 1.0 EBNF (7) QName")
    fun qname() {
        val lexer = createLexer()

        lexer.start("one:two")
        matchToken(lexer, "one", 0, 0, 3, XPathTokenType.NCNAME)
        matchToken(lexer, ":", 0, 3, 4, XPathTokenType.QNAME_SEPARATOR)
        matchToken(lexer, "two", 0, 4, 7, XPathTokenType.NCNAME)
        matchToken(lexer, "", 0, 7, 7, null)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (155) NCName ; Namespaces in XML 1.0 EBNF (4) NCName")
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

    @Test
    @DisplayName("XQuery 1.0 EBNF (156) S")
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
    @DisplayName("XQuery 3.0 EBNF (18) DecimalFormatDecl")
    fun testDecimalFormatDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "decimal-format", XQueryTokenType.K_DECIMAL_FORMAT)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "=", XQueryTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (19) DFPropertyName")
    fun testDFPropertyName() {
        val lexer = createLexer()

        matchSingleToken(lexer, "decimal-separator", XQueryTokenType.K_DECIMAL_SEPARATOR)
        matchSingleToken(lexer, "grouping-separator", XQueryTokenType.K_GROUPING_SEPARATOR)
        matchSingleToken(lexer, "infinity", XQueryTokenType.K_INFINITY)
        matchSingleToken(lexer, "minus-sign", XQueryTokenType.K_MINUS_SIGN)
        matchSingleToken(lexer, "NaN", XQueryTokenType.K_NAN)
        matchSingleToken(lexer, "percent", XQueryTokenType.K_PERCENT)
        matchSingleToken(lexer, "per-mille", XQueryTokenType.K_PER_MILLE)
        matchSingleToken(lexer, "zero-digit", XQueryTokenType.K_ZERO_DIGIT)
        matchSingleToken(lexer, "digit", XQueryTokenType.K_DIGIT)
        matchSingleToken(lexer, "pattern-separator", XQueryTokenType.K_PATTERN_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (26) AnnotatedDecl")
    fun testAnnotatedDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (27) Annotation")
    fun testAnnotation() {
        val lexer = createLexer()

        matchSingleToken(lexer, "%", XQueryTokenType.ANNOTATION_INDICATOR)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)

        matchSingleToken(lexer, "public", XQueryTokenType.K_PUBLIC)
        matchSingleToken(lexer, "private", XQueryTokenType.K_PRIVATE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (31) ContextItemDecl")
    fun testContextItemDecl() {
        val lexer = createLexer()

        matchSingleToken(lexer, "declare", XQueryTokenType.K_DECLARE)
        matchSingleToken(lexer, "context", XQueryTokenType.K_CONTEXT)
        matchSingleToken(lexer, "item", XQueryTokenType.K_ITEM)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
        matchSingleToken(lexer, "external", XQueryTokenType.K_EXTERNAL)
        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (46) AllowingEmpty")
    fun testAllowingEmpty() {
        val lexer = createLexer()

        matchSingleToken(lexer, "allowing", XQueryTokenType.K_ALLOWING)
        matchSingleToken(lexer, "empty", XQueryTokenType.K_EMPTY)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (50) WindowClause")
    fun testWindowClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "for", XQueryTokenType.K_FOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (51) TumblingWindowClause")
    fun testTumblingWindowClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "tumbling", XQueryTokenType.K_TUMBLING)
        matchSingleToken(lexer, "window", XQueryTokenType.K_WINDOW)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "in", XQueryTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (52) SlidingWindowClause")
    fun testSlidingWindowClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "sliding", XQueryTokenType.K_SLIDING)
        matchSingleToken(lexer, "window", XQueryTokenType.K_WINDOW)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "in", XQueryTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (53) WindowStartCondition")
    fun testWindowStartCondition() {
        val lexer = createLexer()

        matchSingleToken(lexer, "start", XQueryTokenType.K_START)
        matchSingleToken(lexer, "when", XQueryTokenType.K_WHEN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (54) WindowEndCondition")
    fun testWindowEndCondition() {
        val lexer = createLexer()

        matchSingleToken(lexer, "only", XQueryTokenType.K_ONLY)
        matchSingleToken(lexer, "end", XQueryTokenType.K_END)
        matchSingleToken(lexer, "when", XQueryTokenType.K_WHEN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (55) WindowVars")
    fun testWindowVars() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
        matchSingleToken(lexer, "previous", XQueryTokenType.K_PREVIOUS)
        matchSingleToken(lexer, "next", XQueryTokenType.K_NEXT)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (59) CountClause")
    fun testCountClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "count", XQueryTokenType.K_COUNT)
        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (61) GroupByClause")
    fun testGroupByClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "group", XQueryTokenType.K_GROUP)
        matchSingleToken(lexer, "by", XQueryTokenType.K_BY)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (62) GroupingSpecList")
    fun testGroupingSpecList() {
        val lexer = createLexer()

        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (63) GroupingSpec")
    fun testGroupingSpec() {
        val lexer = createLexer()

        matchSingleToken(lexer, ":=", XQueryTokenType.ASSIGN_EQUAL)
        matchSingleToken(lexer, "collation", XQueryTokenType.K_COLLATION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (64) GroupingVariable")
    fun testGroupingVariable() {
        val lexer = createLexer()

        matchSingleToken(lexer, "$", XQueryTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (69) ReturnClause")
    fun testReturnClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (71) SwitchExpr")
    fun testSwitchExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "switch", XQueryTokenType.K_SWITCH)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "default", XQueryTokenType.K_DEFAULT)
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (72) SwitchCaseClause")
    fun testSwitchCaseClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "case", XQueryTokenType.K_CASE)
        matchSingleToken(lexer, "return", XQueryTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (76) SequenceTypeUnion")
    fun testSequenceTypeUnion() {
        val lexer = createLexer()

        matchSingleToken(lexer, "|", XQueryTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (79) TryClause")
    fun testTryClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "try", XQueryTokenType.K_TRY)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (81) CatchClause")
    fun testCatchClause() {
        val lexer = createLexer()

        matchSingleToken(lexer, "catch", XQueryTokenType.K_CATCH)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (82) CatchErrorList")
    fun testCatchErrorList() {
        val lexer = createLexer()

        matchSingleToken(lexer, "|", XQueryTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (86) StringConcatExpr")
    fun testStringConcatExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "||", XQueryTokenType.CONCATENATION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (101) ValidateExpr")
    fun testValidateExpr_Type() {
        val lexer = createLexer()

        matchSingleToken(lexer, "validate", XQueryTokenType.K_VALIDATE)
        matchSingleToken(lexer, "type", XQueryTokenType.K_TYPE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (106) SimpleMapExpr")
    fun testSimpleMapExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "!", XQueryTokenType.MAP_OPERATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (121) ArgumentList")
    fun testArgumentList() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (135) ArgumentPlaceholder")
    fun testArgumentPlaceholder() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (156) CompNamespaceConstructor")
    fun testCompNamespaceConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "namespace", XQueryTokenType.K_NAMESPACE)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (164) NamedFunctionRef")
    fun testNamedFunctionRef() {
        val lexer = createLexer()

        matchSingleToken(lexer, "#", XQueryTokenType.FUNCTION_REF_OPERATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (165) InlineFunctionExpr")
    fun testInlineFunctionExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (177) NamespaceNodeTest")
    fun testNamespaceNodeTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "namespace-node", XQueryTokenType.K_NAMESPACE_NODE)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (192) AnyFunctionTest")
    fun testAnyFunctionTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (193) TypedFunctionTest")
    fun testTypedFunctionTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "function", XQueryTokenType.K_FUNCTION)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
        matchSingleToken(lexer, "as", XQueryTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (164) ParenthesizedItemType")
    fun testParenthesizedItemType() {
        val lexer = createLexer()

        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (202) BracedURILiteral")
    internal inner class BracedURILiteral {
        @Test
        @DisplayName("braced uri literal")
        fun testBracedURILiteral() {
            val lexer = createLexer()

            matchSingleToken(lexer, "Q", XPathTokenType.NCNAME)

            lexer.start("Q{")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "", 26, 2, 2, null)

            lexer.start("Q{Hello World}")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "Hello World", 26, 2, 13, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 26, 13, 14, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 0, 14, 14, null)

            // NOTE: "", '', {{ and }} are used as escaped characters in string and attribute literals.
            lexer.start("Q{A\"\"B''C{{D}}E}")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "A\"\"B''C", 26, 2, 9, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "{", 26, 9, 10, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "{", 26, 10, 11, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "D", 26, 11, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 26, 12, 13, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "}", 0, 13, 14, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "E", 0, 14, 15, XPathTokenType.NCNAME)
            matchToken(lexer, "}", 0, 15, 16, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "", 0, 16, 16, null)
        }

        @Test
        @DisplayName("braced uri literal in Pragma")
        fun testBracedURILiteral_Pragma() {
            val lexer = createLexer()

            lexer.start("Q", 0, 1, 8)
            matchToken(lexer, "Q", 8, 0, 1, XPathTokenType.NCNAME)
            matchToken(lexer, "", 9, 1, 1, null)

            lexer.start("Q{", 0, 2, 8)
            matchToken(lexer, "Q{", 8, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "", 31, 2, 2, null)

            lexer.start("Q{Hello World}", 0, 14, 8)
            matchToken(lexer, "Q{", 8, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "Hello World", 31, 2, 13, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 31, 13, 14, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 9, 14, 14, null)

            // NOTE: "", '', {{ and }} are used as escaped characters in string and attribute literals.
            lexer.start("Q{A\"\"B''C{{D}}E}", 0, 16, 8)
            matchToken(lexer, "Q{", 8, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "A\"\"B''C", 31, 2, 9, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "{", 31, 9, 10, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "{", 31, 10, 11, XPathTokenType.BAD_CHARACTER)
            matchToken(lexer, "D", 31, 11, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 31, 12, 13, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "}E}", 9, 13, 16, XQueryTokenType.PRAGMA_CONTENTS)
            matchToken(lexer, "", 6, 16, 16, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            matchToken(lexer, "", 0, 16, 16, null)
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (203) PredefinedEntityRef")
        fun testBracedURILiteral_PredefinedEntityRef() {
            val lexer = createLexer()

            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            lexer.start("Q{One&abc;&aBc;&Abc;&ABC;&a4;&a;Two}")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "One", 26, 2, 5, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "&abc;", 26, 5, 10, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&aBc;", 26, 10, 15, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&Abc;", 26, 15, 20, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&ABC;", 26, 20, 25, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a4;", 26, 25, 29, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "&a;", 26, 29, 32, XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
            matchToken(lexer, "Two", 26, 32, 35, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 26, 35, 36, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 0, 36, 36, null)

            lexer.start("Q{&}")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "&", 26, 2, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "}", 26, 3, 4, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 0, 4, 4, null)

            lexer.start("Q{&abc!}")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "&abc", 26, 2, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "!", 26, 6, 7, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 26, 7, 8, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 0, 8, 8, null)

            lexer.start("Q{& }")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "&", 26, 2, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, " ", 26, 3, 4, XPathTokenType.STRING_LITERAL_CONTENTS)
            matchToken(lexer, "}", 26, 4, 5, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 0, 5, 5, null)

            lexer.start("Q{&")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "&", 26, 2, 3, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 26, 3, 3, null)

            lexer.start("Q{&abc")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "&abc", 26, 2, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            matchToken(lexer, "", 26, 6, 6, null)

            lexer.start("Q{&;}")
            matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
            matchToken(lexer, "&;", 26, 2, 4, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
            matchToken(lexer, "}", 26, 4, 5, XQueryTokenType.BRACED_URI_LITERAL_END)
            matchToken(lexer, "", 0, 5, 5, null)
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class BracedURILiteral_CharRef {
            @Test
            @DisplayName("octal")
            fun testBracedURILiteral_CharRef_Octal() {
                val lexer = createLexer()

                lexer.start("Q{One&#20;Two}")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "One", 26, 2, 5, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#20;", 26, 5, 10, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 26, 10, 13, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "}", 26, 13, 14, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 14, 14, null)

                lexer.start("Q{&#}")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#", 26, 2, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "}", 26, 4, 5, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 5, 5, null)

                lexer.start("Q{&# }")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#", 26, 2, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 26, 4, 5, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "}", 26, 5, 6, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 6, 6, null)

                lexer.start("Q{&#")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#", 26, 2, 4, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 26, 4, 4, null)

                lexer.start("Q{&#12")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#12", 26, 2, 6, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 26, 6, 6, null)

                lexer.start("Q{&#;}")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#;", 26, 2, 5, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "}", 26, 5, 6, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 6, 6, null)
            }

            @Test
            @DisplayName("hexadecimal")
            fun testBracedURILiteral_CharRef_Hexadecimal() {
                val lexer = createLexer()

                lexer.start("Q{One&#x20;&#xae;&#xDC;Two}")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "One", 26, 2, 5, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#x20;", 26, 5, 11, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xae;", 26, 11, 17, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "&#xDC;", 26, 17, 23, XQueryTokenType.CHARACTER_REFERENCE)
                matchToken(lexer, "Two", 26, 23, 26, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "}", 26, 26, 27, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 27, 27, null)

                lexer.start("Q{&#x}")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#x", 26, 2, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "}", 26, 5, 6, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 6, 6, null)

                lexer.start("Q{&#x }")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#x", 26, 2, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, " ", 26, 5, 6, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "}", 26, 6, 7, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 7, 7, null)

                lexer.start("Q{&#x")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#x", 26, 2, 5, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 26, 5, 5, null)

                lexer.start("Q{&#x12")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#x12", 26, 2, 7, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "", 26, 7, 7, null)

                lexer.start("Q{&#x;&#x2G;&#x2g;&#xg2;}")
                matchToken(lexer, "Q{", 0, 0, 2, XQueryTokenType.BRACED_URI_LITERAL_START)
                matchToken(lexer, "&#x;", 26, 2, 6, XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                matchToken(lexer, "&#x2", 26, 6, 10, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "G;", 26, 10, 12, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#x2", 26, 12, 16, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g;", 26, 16, 18, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "&#x", 26, 18, 21, XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                matchToken(lexer, "g2;", 26, 21, 24, XPathTokenType.STRING_LITERAL_CONTENTS)
                matchToken(lexer, "}", 26, 24, 25, XQueryTokenType.BRACED_URI_LITERAL_END)
                matchToken(lexer, "", 0, 25, 25, null)
            }
        }
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (19) DFPropertyName")
    fun testDFPropertyName_XQuery31() {
        val lexer = createLexer()

        matchSingleToken(lexer, "decimal-separator", XQueryTokenType.K_DECIMAL_SEPARATOR)
        matchSingleToken(lexer, "grouping-separator", XQueryTokenType.K_GROUPING_SEPARATOR)
        matchSingleToken(lexer, "infinity", XQueryTokenType.K_INFINITY)
        matchSingleToken(lexer, "minus-sign", XQueryTokenType.K_MINUS_SIGN)
        matchSingleToken(lexer, "NaN", XQueryTokenType.K_NAN)
        matchSingleToken(lexer, "percent", XQueryTokenType.K_PERCENT)
        matchSingleToken(lexer, "per-mille", XQueryTokenType.K_PER_MILLE)
        matchSingleToken(lexer, "zero-digit", XQueryTokenType.K_ZERO_DIGIT)
        matchSingleToken(lexer, "digit", XQueryTokenType.K_DIGIT)
        matchSingleToken(lexer, "pattern-separator", XQueryTokenType.K_PATTERN_SEPARATOR)
        matchSingleToken(lexer, "exponent-separator", XQueryTokenType.K_EXPONENT_SEPARATOR) // New in XQuery 3.1
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr")
    fun testArrowExpr() {
        val lexer = createLexer()

        matchSingleToken(lexer, "=>", XQueryTokenType.ARROW)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (125) Lookup")
    fun testLookup() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier")
    fun testKeySpecifier() {
        val lexer = createLexer()

        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (170) MapConstructor")
    fun testMapConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "map", XQueryTokenType.K_MAP)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (171) MapConstructorEntry")
    fun testMapConstructorEntry() {
        val lexer = createLexer()

        matchSingleToken(lexer, ":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (175) SquareArrayConstructor")
    fun testSquareArrayConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "[", XQueryTokenType.SQUARE_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, "]", XQueryTokenType.SQUARE_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (176) CurlyArrayConstructor")
    fun testCurlyArrayConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array", XQueryTokenType.K_ARRAY)
        matchSingleToken(lexer, "{", XQueryTokenType.BLOCK_OPEN)
        matchSingleToken(lexer, "}", XQueryTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (177) StringConstructor ; XQuery 3.1 EBNF (179) StringConstructorChars")
    fun testStringConstructor() {
        val lexer = createLexer()

        matchSingleToken(lexer, "`", XQueryTokenType.INVALID)
        matchSingleToken(lexer, "``", XQueryTokenType.INVALID)

        matchSingleToken(lexer, "]", XQueryTokenType.SQUARE_CLOSE)
        matchSingleToken(lexer, "]`", XQueryTokenType.INVALID)
        matchSingleToken(lexer, "]``", XQueryTokenType.STRING_CONSTRUCTOR_END)

        lexer.start("``[")
        matchToken(lexer, "``[", 0, 0, 3, XQueryTokenType.STRING_CONSTRUCTOR_START)
        matchToken(lexer, "", 27, 3, 3, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
        matchToken(lexer, "", 6, 3, 3, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
        matchToken(lexer, "", 0, 3, 3, null)

        lexer.start("``[One]Two]`")
        matchToken(lexer, "``[", 0, 0, 3, XQueryTokenType.STRING_CONSTRUCTOR_START)
        matchToken(lexer, "One]Two]`", 27, 3, 12, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
        matchToken(lexer, "", 6, 12, 12, XPathTokenType.UNEXPECTED_END_OF_BLOCK)
        matchToken(lexer, "", 0, 12, 12, null)

        lexer.start("``[One]Two]``")
        matchToken(lexer, "``[", 0, 0, 3, XQueryTokenType.STRING_CONSTRUCTOR_START)
        matchToken(lexer, "One]Two", 27, 3, 10, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
        matchToken(lexer, "]``", 0, 10, 13, XQueryTokenType.STRING_CONSTRUCTOR_END)
        matchToken(lexer, "", 0, 13, 13, null)

        lexer.start("``[`]``")
        matchToken(lexer, "``[", 0, 0, 3, XQueryTokenType.STRING_CONSTRUCTOR_START)
        matchToken(lexer, "`", 27, 3, 4, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
        matchToken(lexer, "]``", 0, 4, 7, XQueryTokenType.STRING_CONSTRUCTOR_END)
        matchToken(lexer, "", 0, 7, 7, null)
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (180) StringConstructorInterpolation")
    internal inner class StringConstructorInterpolation {
        @Test
        @DisplayName("in DirElemContent as element contents")
        fun testStringConstructorInterpolation_InDirElemContent() {
            val lexer = createLexer()

            lexer.start("<a>`{2}`</a>")
            matchToken(lexer, "<", 0x60000000 or 30, 0, 1, XQueryTokenType.OPEN_XML_TAG)
            matchToken(lexer, "a", 0x60000000 or 11, 1, 2, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 0x60000000 or 11, 2, 3, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "`", 17, 3, 4, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "{", 17, 4, 5, XQueryTokenType.BLOCK_OPEN)
            matchToken(lexer, "2", 18, 5, 6, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}", 18, 6, 7, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "`", 17, 7, 8, XQueryTokenType.XML_ELEMENT_CONTENTS)
            matchToken(lexer, "</", 17, 8, 10, XQueryTokenType.CLOSE_XML_TAG)
            matchToken(lexer, "a", 12, 10, 11, XQueryTokenType.XML_TAG_NCNAME)
            matchToken(lexer, ">", 12, 11, 12, XQueryTokenType.END_XML_TAG)
            matchToken(lexer, "", 0, 12, 12, null)
        }

        @Test
        @DisplayName("in Expr as invalid characters")
        fun testStringConstructorInterpolation_InterpolationMarkersOutsideStringConstructor() {
            val lexer = createLexer()

            // String interpolation marker is only valid in string interpolation contexts.
            lexer.start("`{")
            matchToken(lexer, "`", 0, 0, 1, XQueryTokenType.INVALID)
            matchToken(lexer, "{", 0, 1, 2, XQueryTokenType.BLOCK_OPEN)
            matchToken(lexer, "", 0, 2, 2, null)

            // String interpolation marker is only valid in string interpolation contexts.
            lexer.start("}`")
            matchToken(lexer, "}", 0, 0, 1, XQueryTokenType.BLOCK_CLOSE)
            matchToken(lexer, "`", 0, 1, 2, XQueryTokenType.INVALID)
            matchToken(lexer, "", 0, 2, 2, null)
        }

        @Test
        @DisplayName("in StringConstructor")
        fun testStringConstructorInterpolation() {
            val lexer = createLexer()

            lexer.start("``[One`{2}`Three]``")
            matchToken(lexer, "``[", 0, 0, 3, XQueryTokenType.STRING_CONSTRUCTOR_START)
            matchToken(lexer, "One", 27, 3, 6, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            matchToken(lexer, "`{", 27, 6, 8, XQueryTokenType.STRING_INTERPOLATION_OPEN)
            matchToken(lexer, "2", 28, 8, 9, XPathTokenType.INTEGER_LITERAL)
            matchToken(lexer, "}`", 28, 9, 11, XQueryTokenType.STRING_INTERPOLATION_CLOSE)
            matchToken(lexer, "Three", 27, 11, 16, XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            matchToken(lexer, "]``", 0, 16, 19, XQueryTokenType.STRING_CONSTRUCTOR_END)
            matchToken(lexer, "", 0, 19, 19, null)
        }
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (181) UnaryLookup")
    fun testUnaryLookup() {
        val lexer = createLexer()

        matchSingleToken(lexer, "?", XQueryTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (211) AnyMapTest")
    fun testAnyMapTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "map", XQueryTokenType.K_MAP)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (212) TypedMapTest")
    fun testTypedMapTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "map", XQueryTokenType.K_MAP)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ",", XQueryTokenType.COMMA)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (214) AnyArrayTest")
    fun testAnyArrayTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array", XQueryTokenType.K_ARRAY)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, "*", XQueryTokenType.STAR)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (215) TypedArrayTest")
    fun testTypedArrayTest() {
        val lexer = createLexer()

        matchSingleToken(lexer, "array", XQueryTokenType.K_ARRAY)
        matchSingleToken(lexer, "(", XQueryTokenType.PARENTHESIS_OPEN)
        matchSingleToken(lexer, ")", XQueryTokenType.PARENTHESIS_CLOSE)
    }
}
