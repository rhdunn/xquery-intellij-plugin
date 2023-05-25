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
package uk.co.reecedunn.intellij.plugin.xquery.tests.lexer

import com.intellij.lexer.Lexer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.lexer.CombinedLexer
import uk.co.reecedunn.intellij.plugin.core.tests.lexer.LexerTestCase
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryLexer
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

@Suppress("ClassName", "Reformat")
@DisplayName("XQuery 3.1 - Lexer")
class XQueryLexerTest : LexerTestCase() {
    override val lexer: Lexer = run {
        val lexer = CombinedLexer(XQueryLexer())
        lexer.addState(
            XQueryLexer(), 0x50000000, 0,
            XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_MAYBE_OPEN_XML_TAG
        )
        lexer.addState(
            XQueryLexer(), 0x60000000, 0,
            XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR, XQueryTokenType.DIRELEM_OPEN_XML_TAG
        )
        lexer
    }

    @Nested
    @DisplayName("Lexer")
    internal inner class LexerTest {
        @Test
        @DisplayName("invalid state")
        fun invalidState() {
            val e = assertThrows(AssertionError::class.java) { tokenize("123", 0, 3, 4096) }
            assertThat(e.message, `is`("Invalid state: 4096"))
        }

        @Test
        @DisplayName("empty stack when calling advance()")
        fun emptyStackInAdvance() {
            lexer.start("\"Hello World\"")
            lexer.advance()
            assertThat(lexer.state, `is`(1))

            tokenize("} {\"") {
                token("}", XPathTokenType.BLOCK_CLOSE)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("{", XPathTokenType.BLOCK_OPEN)
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
            }
        }

        @Test
        @DisplayName("empty stack when calling popState()")
        fun emptyStackInPopState() = tokenize("} } ") {
            token("}", XPathTokenType.BLOCK_CLOSE)
            token(" ", XPathTokenType.WHITE_SPACE)
            token("}", XPathTokenType.BLOCK_CLOSE)
            token(" ", XPathTokenType.WHITE_SPACE)
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
    @DisplayName("XQuery 1.0 EBNF (2) VersionDecl")
    fun versionDecl() {
        token("xquery", XQueryTokenType.K_XQUERY)
        token("version", XQueryTokenType.K_VERSION)
        token("encoding", XQueryTokenType.K_ENCODING)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (5) ModuleDecl")
    fun moduleDecl() {
        token("module", XQueryTokenType.K_MODULE)
        token("namespace", XPathTokenType.K_NAMESPACE)
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (9) Separator")
    fun separator() {
        token(";", XQueryTokenType.SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (10) NamespaceDecl")
    fun namespaceDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("namespace", XPathTokenType.K_NAMESPACE)
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (11) BoundarySpaceDecl")
    fun boundarySpaceDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("boundary-space", XQueryTokenType.K_BOUNDARY_SPACE)
        token("preserve", XQueryTokenType.K_PRESERVE)
        token("strip", XQueryTokenType.K_STRIP)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (12) DefaultNamespaceDecl")
    fun defaultNamespaceDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("default", XPathTokenType.K_DEFAULT)
        token("element", XPathTokenType.K_ELEMENT)
        token("function", XPathTokenType.K_FUNCTION)
        token("namespace", XPathTokenType.K_NAMESPACE)
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (13) OptionDecl")
    fun optionDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("option", XPathTokenType.K_OPTION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (14) OrderingModeDecl")
    fun orderingModeDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("ordering", XQueryTokenType.K_ORDERING)
        token("ordered", XPathTokenType.K_ORDERED)
        token("unordered", XQueryTokenType.K_UNORDERED)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (15) EmptyOrderDecl")
    fun emptyOrderDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("default", XPathTokenType.K_DEFAULT)
        token("order", XQueryTokenType.K_ORDER)
        token("empty", XPathTokenType.K_EMPTY)
        token("greatest", XQueryTokenType.K_GREATEST)
        token("least", XPathTokenType.K_LEAST)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (16) CopyNamespacesDecl")
    fun copyNamespacesDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("copy-namespaces", XQueryTokenType.K_COPY_NAMESPACES)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (17) PreserveMode")
    fun preserveMode() {
        token("preserve", XQueryTokenType.K_PRESERVE)
        token("no-preserve", XQueryTokenType.K_NO_PRESERVE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (18) InheritMode")
    fun inheritMode() {
        token("inherit", XQueryTokenType.K_INHERIT)
        token("no-inherit", XQueryTokenType.K_NO_INHERIT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (19) DefaultCollationDecl")
    fun defaultCollationDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("default", XPathTokenType.K_DEFAULT)
        token("collation", XQueryTokenType.K_COLLATION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (20) BaseURIDecl")
    fun baseURIDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("base-uri", XQueryTokenType.K_BASE_URI)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (21) SchemaImport")
    fun schemaImport() {
        token("import", XQueryTokenType.K_IMPORT)
        token("schema", XQueryTokenType.K_SCHEMA)
        token("at", XPathTokenType.K_AT)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (22) SchemaPrefix")
    fun schemaPrefix() {
        token("namespace", XPathTokenType.K_NAMESPACE)
        token("=", XPathTokenType.EQUAL)

        token("default", XPathTokenType.K_DEFAULT)
        token("element", XPathTokenType.K_ELEMENT)
        token("namespace", XPathTokenType.K_NAMESPACE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (23) ModuleImport")
    fun moduleImport() {
        token("import", XQueryTokenType.K_IMPORT)
        token("module", XQueryTokenType.K_MODULE)
        token("at", XPathTokenType.K_AT)
        token(",", XPathTokenType.COMMA)

        token("namespace", XPathTokenType.K_NAMESPACE)
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (24) VarDecl")
    fun varDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("variable", XQueryTokenType.K_VARIABLE)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
        token("external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (25) ConstructionDecl")
    fun constructionDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("construction", XQueryTokenType.K_CONSTRUCTION)
        token("strip", XQueryTokenType.K_STRIP)
        token("preserve", XQueryTokenType.K_PRESERVE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (26) FunctionDecl")
    fun functionDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("as", XPathTokenType.K_AS)
        token("external", XQueryTokenType.K_EXTERNAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (27) ParamList")
    fun paramList() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (28) Param")
    fun param() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (29) EnclosedExpr")
    fun enclosedExpr() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (31) Expr")
    fun expr() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (33) FLWORExpr")
    fun flworExpr() {
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (34) ForClause ; XQuery 3.0 EBNF (45) ForBinding")
    fun forClause() {
        token("for", XPathTokenType.K_FOR)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (35) PositionalVar")
    fun positionalVar() {
        token("at", XPathTokenType.K_AT)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (36) LetClause ; XQuery 3.0 EBNF (49) LetBinding")
    fun letClause() {
        token("let", XPathTokenType.K_LET)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (37) WhereClause")
    fun whereClause() {
        token("where", XQueryTokenType.K_WHERE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (38) OrderByClause")
    fun orderByClause() {
        token("stable", XQueryTokenType.K_STABLE)
        token("order", XQueryTokenType.K_ORDER)
        token("by", XQueryTokenType.K_BY)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (39) OrderSpecList")
    fun orderSpecList() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (41) OrderModifier")
    fun orderModifier() {
        token("ascending", XQueryTokenType.K_ASCENDING)
        token("descending", XQueryTokenType.K_DESCENDING)

        token("empty", XPathTokenType.K_EMPTY)
        token("greatest", XQueryTokenType.K_GREATEST)
        token("least", XPathTokenType.K_LEAST)

        token("collation", XQueryTokenType.K_COLLATION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (42) QuantifiedExpr")
    fun quantifiedExpr() {
        token("some", XPathTokenType.K_SOME)
        token("every", XPathTokenType.K_EVERY)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
        token(",", XPathTokenType.COMMA)
        token("satisfies", XPathTokenType.K_SATISFIES)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (43) TypeswitchExpr")
    fun typeswitchExpr() {
        token("typeswitch", XQueryTokenType.K_TYPESWITCH)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("default", XPathTokenType.K_DEFAULT)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (44) CaseClause")
    fun caseClause() {
        token("case", XPathTokenType.K_CASE)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("as", XPathTokenType.K_AS)
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (45) IfExpr")
    fun ifExpr() {
        token("if", XPathTokenType.K_IF)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("then", XPathTokenType.K_THEN)
        token("else", XPathTokenType.K_ELSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (46) OrExpr")
    fun orExpr() {
        token("or", XPathTokenType.K_OR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (47) AndExpr")
    fun andExpr() {
        token("and", XPathTokenType.K_AND)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (49) RangeExpr")
    fun rangeExpr() {
        token("to", XPathTokenType.K_TO)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (50) AdditiveExpr")
    fun additiveExpr() {
        token("+", XPathTokenType.PLUS)
        token("-", XPathTokenType.MINUS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (51) MultiplicativeExpr")
    fun multiplicativeExpr() {
        token("*", XPathTokenType.STAR)
        token("div", XPathTokenType.K_DIV)
        token("idiv", XPathTokenType.K_IDIV)
        token("mod", XPathTokenType.K_MOD)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (52) UnionExpr")
    fun unionExpr() {
        token("union", XPathTokenType.K_UNION)
        token("|", XPathTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (53) IntersectExceptExpr")
    fun intersectExceptExpr() {
        token("intersect", XPathTokenType.K_INTERSECT)
        token("except", XPathTokenType.K_EXCEPT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (54) InstanceofExpr")
    fun instanceofExpr() {
        token("instance", XPathTokenType.K_INSTANCE)
        token("of", XPathTokenType.K_OF)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (55) TreatExpr")
    fun treatExpr() {
        token("treat", XPathTokenType.K_TREAT)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (56) CastableExpr")
    fun castableExpr() {
        token("castable", XPathTokenType.K_CASTABLE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (57) CastExpr")
    fun castExpr() {
        token("cast", XPathTokenType.K_CAST)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (58) UnaryExpr")
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
    @DisplayName("XQuery 1.0 EBNF (60) GeneralComp")
    fun generalComp() {
        token("=", XPathTokenType.EQUAL)
        token("!=", XPathTokenType.NOT_EQUAL)
        token("<", XPathTokenType.LESS_THAN)
        token("<=", XPathTokenType.LESS_THAN_OR_EQUAL)
        token(">", XPathTokenType.GREATER_THAN)
        token(">=", XPathTokenType.GREATER_THAN_OR_EQUAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (61) ValueComp")
    fun valueComp() {
        token("eq", XPathTokenType.K_EQ)
        token("ne", XPathTokenType.K_NE)
        token("lt", XPathTokenType.K_LT)
        token("le", XPathTokenType.K_LE)
        token("gt", XPathTokenType.K_GT)
        token("ge", XPathTokenType.K_GE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (62) NodeComp")
    fun nodeComp() {
        token("is", XPathTokenType.K_IS)
        token("<<", XPathTokenType.NODE_BEFORE)
        token(">>", XPathTokenType.NODE_AFTER)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (63) ValidateExpr")
    fun validateExpr() {
        token("validate", XQueryTokenType.K_VALIDATE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (64) ValidateMode")
    fun validationMode() {
        token("lax", XQueryTokenType.K_LAX)
        token("strict", XQueryTokenType.K_STRICT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (65) ExtensionExpr")
    fun extensionExpr() {
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (66) Pragma ; XQuery 1.0 EBNF (67) PragmaContents")
    fun pragma() {
        token("(#", 8, XPathTokenType.PRAGMA_BEGIN)
        token("#)", 0, XPathTokenType.PRAGMA_END)

        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("#", XPathTokenType.FUNCTION_REF_OPERATOR)

        tokenize("(#  let:for  6^gkgw~*#g#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("  ", XPathTokenType.WHITE_SPACE)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(":", XPathTokenType.QNAME_SEPARATOR)
            token("for", XPathTokenType.NCNAME)
            token("  ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("6^gkgw~*#g", XPathTokenType.PRAGMA_CONTENTS)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#let ##)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("#", XPathTokenType.PRAGMA_CONTENTS)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#let 2") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("2", XPathTokenType.PRAGMA_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }

        tokenize("(#let ") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
        }

        tokenize("(#let~~~#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(10)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#let~~~") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("let", XPathTokenType.NCNAME)
            state(9)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }

        tokenize("(#:let 2#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token(":", XPathTokenType.QNAME_SEPARATOR)
            state(9)
            token("let", XPathTokenType.NCNAME)
            token(" ", XPathTokenType.WHITE_SPACE)
            state(10)
            token("2", XPathTokenType.PRAGMA_CONTENTS)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#~~~#)") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(10)
            token("#)", XPathTokenType.PRAGMA_END)
            state(0)
        }

        tokenize("(#~~~") {
            token("(#", XPathTokenType.PRAGMA_BEGIN)
            state(8)
            token("~~~", XPathTokenType.PRAGMA_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (68) PathExpr")
    fun pathExpr() {
        token("/", XPathTokenType.DIRECT_DESCENDANTS_PATH)
        token("//", XPathTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (69) RelativePathExpr")
    fun relativePathExpr() {
        token("/", XPathTokenType.DIRECT_DESCENDANTS_PATH)
        token("//", XPathTokenType.ALL_DESCENDANTS_PATH)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (73) ForwardAxis")
    fun forwardAxis() {
        // NOTE: XQuery does not support the XPath 'namespace::' forward axis.
        token("attribute", XPathTokenType.K_ATTRIBUTE)
        token("child", XPathTokenType.K_CHILD)
        token("descendant", XPathTokenType.K_DESCENDANT)
        token("descendant-or-self", XPathTokenType.K_DESCENDANT_OR_SELF)
        token("following", XPathTokenType.K_FOLLOWING)
        token("following-sibling", XPathTokenType.K_FOLLOWING_SIBLING)
        token("self", XPathTokenType.K_SELF)
        token("::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (74) AbbrevForwardStep")
    fun abbrevForwardStep() {
        token("@", XPathTokenType.ATTRIBUTE_SELECTOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (76) ReverseAxis")
    fun reverseAxis() {
        token("ancestor", XPathTokenType.K_ANCESTOR)
        token("ancestor-or-self", XPathTokenType.K_ANCESTOR_OR_SELF)
        token("parent", XPathTokenType.K_PARENT)
        token("preceding", XPathTokenType.K_PRECEDING)
        token("preceding-sibling", XPathTokenType.K_PRECEDING_SIBLING)
        token("::", XPathTokenType.AXIS_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (77) AbbrevReverseStep")
    fun abbrevReverseStep() {
        token("..", XPathTokenType.PARENT_SELECTOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (80) Wildcard")
    fun wildcard() {
        token("*", XPathTokenType.STAR)
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (83) Predicate")
    fun predicate() {
        token("[", XPathTokenType.SQUARE_OPEN)
        token("]", XPathTokenType.SQUARE_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (87) VarRef")
    fun varRef() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (89) ParenthesizedExpr")
    fun parenthesizedExpr() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (90) ContextItemExpr")
    fun contextItemExpr() {
        token(".", XPathTokenType.DOT)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (91) OrderedExpr")
    fun orderedExpr() {
        token("ordered", XPathTokenType.K_ORDERED)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (92) UnorderedExpr")
    fun unorderedExpr() {
        token("unordered", XQueryTokenType.K_UNORDERED)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (93) FunctionCall")
    fun functionCall() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (96) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Test
        @DisplayName("maybe direct element constructor state")
        fun maybeDirElem() {
            tokenize("<one:two/>", 0, 10, XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR) {
                token("<", XPathTokenType.LESS_THAN)
                token("one", XPathTokenType.NCNAME)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("two", XPathTokenType.NCNAME)
                token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
            }

            tokenize("<one:two>", 0, 9, XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR) {
                token("<", XPathTokenType.LESS_THAN)
                token("one", XPathTokenType.NCNAME)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("two", XPathTokenType.NCNAME)
                token(">", XPathTokenType.GREATER_THAN)
            }

            tokenize("<  one:two  ", 0, 12, XQueryLexer.STATE_MAYBE_DIR_ELEM_CONSTRUCTOR) {
                token("<", XPathTokenType.LESS_THAN)
                token("  ", XPathTokenType.WHITE_SPACE)
                token("one", XPathTokenType.NCNAME)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("two", XPathTokenType.NCNAME)
                token("  ", XPathTokenType.WHITE_SPACE)
            }
        }

        @Test
        @DisplayName("start direct element constructor state")
        fun startDirElem() {
            tokenize("<one:two/>", 0, 10, XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR) {
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(11)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
                state(0)
            }

            tokenize("<one:two>", 0, 9, XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR) {
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(11)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
            }

            tokenize("<  one:two  ", 0, 12, XQueryLexer.STATE_START_DIR_ELEM_CONSTRUCTOR) {
                token("<", XQueryTokenType.OPEN_XML_TAG)
                token("  ", XQueryTokenType.XML_WHITE_SPACE)
                state(11)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token("  ", XQueryTokenType.XML_WHITE_SPACE)
                state(25)
            }
        }

        @Test
        @DisplayName("as separate tokens using CombinedLexer")
        fun dirElemConstructor() {
            token("<", XPathTokenType.LESS_THAN)
            token(">", XPathTokenType.GREATER_THAN)

            token("</", XQueryTokenType.CLOSE_XML_TAG)
            token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)

            tokenize("<one:two/>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
                state(0)
            }

            tokenize("<one:two></one:two  >") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token("  ", XQueryTokenType.XML_WHITE_SPACE)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<one:two  ></one:two>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token("  ", XQueryTokenType.XML_WHITE_SPACE)
                state(0x60000000 or 25)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("one", XQueryTokenType.XML_TAG_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("two", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<one:two//*/>") {
                state(0x50000000 or 29)
                token("<", XPathTokenType.LESS_THAN)
                token("one", XPathTokenType.NCNAME)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("two", XPathTokenType.NCNAME)
                state(0)
                token("//", XPathTokenType.ALL_DESCENDANTS_PATH)
                token("*", XPathTokenType.STAR)
                token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
            }

            tokenize("1 < fn:abs (") {
                token("1", XPathTokenType.INTEGER_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0x50000000 or 29)
                token("<", XPathTokenType.LESS_THAN)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("fn", XPathTokenType.K_FN)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("abs", XPathTokenType.NCNAME)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0)
                token("(", XPathTokenType.PARENTHESIS_OPEN)
            }

            tokenize("1 <fn:abs (") {
                token("1", XPathTokenType.INTEGER_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0x50000000 or 29)
                token("<", XPathTokenType.LESS_THAN)
                token("fn", XPathTokenType.K_FN)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("abs", XPathTokenType.NCNAME)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0)
                token("(", XPathTokenType.PARENTHESIS_OPEN)
            }

            tokenize("1 < fn:abs #") {
                token("1", XPathTokenType.INTEGER_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0x50000000 or 29)
                token("<", XPathTokenType.LESS_THAN)
                token(" ", XPathTokenType.WHITE_SPACE)
                token("fn", XPathTokenType.K_FN)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("abs", XPathTokenType.NCNAME)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0)
                token("#", XPathTokenType.FUNCTION_REF_OPERATOR)
            }

            tokenize("1 <fn:abs #") {
                token("1", XPathTokenType.INTEGER_LITERAL)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0x50000000 or 29)
                token("<", XPathTokenType.LESS_THAN)
                token("fn", XPathTokenType.K_FN)
                token(":", XPathTokenType.QNAME_SEPARATOR)
                token("abs", XPathTokenType.NCNAME)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0)
                token("#", XPathTokenType.FUNCTION_REF_OPERATOR)
            }
        }

        @Test
        @DisplayName("as separate tokens using CombinedLexer, adding an xml element")
        fun addingXmlElement() {
            tokenize("<<a") {
                token("<<", XPathTokenType.NODE_BEFORE)
                token("a", XPathTokenType.NCNAME)
            }

            tokenize("<<a/>") {
                token("<", XPathTokenType.LESS_THAN)
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
                state(0)
            }

            tokenize("<a<a/>") {
                state(0x50000000 or 29)
                token("<", XPathTokenType.LESS_THAN)
                token("a", XPathTokenType.NCNAME)
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
                state(0)
            }

            tokenize("<a <a/>") {
                state(0x50000000 or 29)
                token("<", XPathTokenType.LESS_THAN)
                token("a", XPathTokenType.NCNAME)
                token(" ", XPathTokenType.WHITE_SPACE)
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
                state(0)
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (97) DirAttributeList ; XQuery 1.0 EBNF (98) DirAttributeValue")
    internal inner class DirAttributeList {
        @Test
        @DisplayName("as separate tokens using CombinedLexer")
        fun dirAttributeList() = tokenize("<one:two  a:b  =  \"One\"  c:d  =  'Two'  />") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("one", XQueryTokenType.XML_TAG_NCNAME)
            token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            token("two", XQueryTokenType.XML_TAG_NCNAME)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            state(25)
            token("a", XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            token(":", XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)
            token("b", XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            token("=", XQueryTokenType.XML_EQUAL)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(13)
            token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(25)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            token("c", XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            token(":", XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)
            token("d", XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            token("=", XQueryTokenType.XML_EQUAL)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(14)
            token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(25)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
            state(0)
        }

        @Test
        @DisplayName("xmlns namespace declaration")
        fun xmlns() = tokenize("<a xmlns=\"test\"/>") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(" ", XQueryTokenType.XML_WHITE_SPACE)
            state(25)
            token("xmlns", XQueryTokenType.XML_ATTRIBUTE_XMLNS)
            token("=", XQueryTokenType.XML_EQUAL)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(13)
            token("test", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(25)
            token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
            state(0)
        }

        @Test
        @DisplayName("xmlns:prefix namespace declaration")
        fun xmlnsPrefix() = tokenize("<a xmlns:b=\"test\"/>") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(" ", XQueryTokenType.XML_WHITE_SPACE)
            state(25)
            token("xmlns", XQueryTokenType.XML_ATTRIBUTE_XMLNS)
            token(":", XQueryTokenType.XML_ATTRIBUTE_QNAME_SEPARATOR)
            token("b", XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            token("=", XQueryTokenType.XML_EQUAL)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(13)
            token("test", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(25)
            token("/>", XQueryTokenType.SELF_CLOSING_XML_TAG)
            state(0)
        }

        @Test
        @DisplayName("incomplete closing tag")
        fun incompleteClosingTag() = tokenize("<a b/") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(" ", XQueryTokenType.XML_WHITE_SPACE)
            state(25)
            token("b", XQueryTokenType.XML_ATTRIBUTE_NCNAME)
            token("/", XQueryTokenType.INVALID)
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (98) DirAttributeValue")
    internal inner class DirAttributeValue {
        @Test
        @DisplayName("XQuery 1.0 EBNF (99) QuotAttrValueContent ; XQuery 1.0 EBNF (149) QuotAttrContentChar")
        fun quotAttrValueContent() = tokenize("\"One {2}<& \u3053\u3093\u3070\u3093\u306F.\"", 0, 18, 11) {
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(13)
            token("One ", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("{", XPathTokenType.BLOCK_OPEN)
            state(15)
            token("2", XPathTokenType.INTEGER_LITERAL)
            token("}", XPathTokenType.BLOCK_CLOSE)
            state(13)
            token("<", XPathTokenType.BAD_CHARACTER)
            token("&", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            token(" \u3053\u3093\u3070\u3093\u306F.", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(11)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (100) AposAttrValueContent ; XQuery 1.0 EBNF (150) AposAttrContentChar")
        fun aposAttrValueContent() = tokenize("'One {2}<& \u3053\u3093\u3070\u3093\u306F.}'", 0, 19, 11) {
            token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(14)
            token("One ", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("{", XPathTokenType.BLOCK_OPEN)
            state(16)
            token("2", XPathTokenType.INTEGER_LITERAL)
            token("}", XPathTokenType.BLOCK_CLOSE)
            state(14)
            token("<", XPathTokenType.BAD_CHARACTER)
            token("&", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            token(" \u3053\u3093\u3070\u3093\u306F.", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("}", XPathTokenType.BLOCK_CLOSE)
            token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(11)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent")
        fun commonContent() {
            tokenize("\"{{}}\"", 0, 6, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("{{", XQueryTokenType.XML_ESCAPED_CHARACTER)
                token("}}", XQueryTokenType.XML_ESCAPED_CHARACTER)
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }

            tokenize("'{{}}'", 0, 6, 11) {
                token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(14)
                token("{{", XQueryTokenType.XML_ESCAPED_CHARACTER)
                token("}}", XQueryTokenType.XML_ESCAPED_CHARACTER)
                token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        fun predefinedEntityRef() {
            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            tokenize("\"One&abc;&aBc;&Abc;&ABC;&a4;&a;Two\"", 0, 35, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("&abc;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&aBc;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&Abc;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&ABC;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&a4;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&a;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }

            tokenize("'One&abc;&aBc;&Abc;&ABC;&a4;&a;Two'", 0, 35, 11) {
                token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(14)
                token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("&abc;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&aBc;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&Abc;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&ABC;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&a4;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("&a;", XQueryTokenType.XML_PREDEFINED_ENTITY_REFERENCE)
                token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }

            tokenize("\"&\"", 0, 3, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("&", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }

            tokenize("\"&abc!\"", 0, 7, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("&abc", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                token("!", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }

            tokenize("\"& \"", 0, 4, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("&", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                token(" ", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }

            tokenize("\"&", 0, 2, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("&", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("\"&abc", 0, 5, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("&abc", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("\"&;\"", 0, 4, 11) {
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                state(13)
                token("&;", XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)
                token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                state(11)
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (146) EscapeQuot")
        fun escapeQuot() = tokenize("\"One\"\"Two\"", 0, 10, 11) {
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(13)
            token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("\"\"", XQueryTokenType.XML_ESCAPED_CHARACTER)
            token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(11)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (147) EscapeApos")
        fun escapeApos() = tokenize("'One''Two'", 0, 10, 11) {
            token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
            state(14)
            token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("''", XQueryTokenType.XML_ESCAPED_CHARACTER)
            token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
            token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
            state(11)
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class DirAttributeValue_CharRef {
            @Test
            @DisplayName("decimal")
            fun decimal() {
                tokenize("\"One&#20;Two\"", 0, 13, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("&#20;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("'One&#20;Two'", 0, 13, 11) {
                    token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(14)
                    token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("&#20;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("\"One&#9;Two\"", 0, 12, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("&#9;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("\"&#\"", 0, 4, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("\"&# \"", 0, 5, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("\"&#", 0, 3, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#12", 0, 5, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#12", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#;\"", 0, 5, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#;", XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }
            }

            @Test
            @DisplayName("hexadecimal")
            fun hexadecimal() {
                tokenize("\"One&#x20;&#xae;&#xDC;Two\"", 0, 26, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("&#x20;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("&#xae;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("&#xDC;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("'One&#x20;&#xae;&#xDC;Two'", 0, 26, 11) {
                    token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(14)
                    token("One", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("&#x20;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("&#xae;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("&#xDC;", XQueryTokenType.XML_CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("'", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("\"&#x\"", 0, 5, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#x", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("\"&#x \"", 0, 6, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#x", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }

                tokenize("\"&#x", 0, 4, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#x", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#x12", 0, 6, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#x12", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#x;&#x2G;&#x2g;&#xg2;\"", 0, 24, 11) {
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_START)
                    state(13)
                    token("&#x;", XQueryTokenType.XML_EMPTY_ENTITY_REFERENCE)
                    token("&#x2", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                    token("G;", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("&#x2", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                    token("g;", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("&#x", XQueryTokenType.XML_PARTIAL_ENTITY_REFERENCE)
                    token("g2;", XQueryTokenType.XML_ATTRIBUTE_VALUE_CONTENTS)
                    token("\"", XQueryTokenType.XML_ATTRIBUTE_VALUE_END)
                    state(11)
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (101) DirElemContent")
    internal inner class DirElemContent {
        @Test
        @DisplayName("XQuery 1.0 EBNF (148) ElementContentChar")
        fun elementContentChar() = tokenize("<a>One {2}<& \u3053\u3093\u3070\u3093\u306F.}</a>") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(17)
            token("One ", XQueryTokenType.XML_ELEMENT_CONTENTS)
            token("{", XPathTokenType.BLOCK_OPEN)
            state(18)
            token("2", XPathTokenType.INTEGER_LITERAL)
            token("}", XPathTokenType.BLOCK_CLOSE)
            state(17)
            token("<", XPathTokenType.BAD_CHARACTER)
            token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            token(" \u3053\u3093\u3070\u3093\u306F.", XQueryTokenType.XML_ELEMENT_CONTENTS)
            token("}", XPathTokenType.BLOCK_CLOSE)
            token("</", XQueryTokenType.CLOSE_XML_TAG)
            state(12)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(0)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (96) DirElemConstructor")
        fun dirElemConstructor() = tokenize("<a>One <b>Two</b> Three</a>") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(17)
            token("One ", XQueryTokenType.XML_ELEMENT_CONTENTS)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(11)
            token("b", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(17)
            token("Two", XQueryTokenType.XML_ELEMENT_CONTENTS)
            token("</", XQueryTokenType.CLOSE_XML_TAG)
            state(12)
            token("b", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(17)
            token(" Three", XQueryTokenType.XML_ELEMENT_CONTENTS)
            token("</", XQueryTokenType.CLOSE_XML_TAG)
            state(12)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(0)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (103) DirCommentConstructor")
        fun dirCommentConstructor() {
            tokenize("<!<!-", 0, 5, 17) {
                token("<!", XQueryTokenType.INVALID)
                token("<!-", XQueryTokenType.INVALID)
            }

            tokenize("<a>One <!-- 2 --> Three</a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("One ", XQueryTokenType.XML_ELEMENT_CONTENTS)
                token("<!--", XQueryTokenType.XML_COMMENT_START_TAG)
                state(19)
                token(" 2 ", XQueryTokenType.XML_COMMENT)
                token("-->", XQueryTokenType.XML_COMMENT_END_TAG)
                state(17)
                token(" Three", XQueryTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (107) CDataSection")
        fun cdataSection() {
            token("<!", XQueryTokenType.INVALID)
            token("<![", XQueryTokenType.INVALID)
            token("<![C", XQueryTokenType.INVALID)
            token("<![CD", XQueryTokenType.INVALID)
            token("<![CDA", XQueryTokenType.INVALID)
            token("<![CDAT", XQueryTokenType.INVALID)
            token("<![CDATA", XQueryTokenType.INVALID)

            tokenize("<a>One <![CDATA[ 2 ]]> Three</a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("One ", XQueryTokenType.XML_ELEMENT_CONTENTS)
                token("<![CDATA[", XQueryTokenType.CDATA_SECTION_START_TAG)
                state(20)
                token(" 2 ", XQueryTokenType.CDATA_SECTION)
                token("]]>", XQueryTokenType.CDATA_SECTION_END_TAG)
                state(17)
                token(" Three", XQueryTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (105) DirPIConstructor")
        fun dirPIConstructor() {
            tokenize("<a><?for  6^gkgw~*?g?></a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
                state(23)
                token("for", XQueryTokenType.XML_PI_TARGET_NCNAME)
                token("  ", XQueryTokenType.XML_WHITE_SPACE)
                state(24)
                token("6^gkgw~*?g", XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
                token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
                state(17)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<a><?for?></a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
                state(23)
                token("for", XQueryTokenType.XML_PI_TARGET_NCNAME)
                token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
                state(17)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<a><?*?$?></a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
                state(23)
                token("*", XPathTokenType.BAD_CHARACTER)
                token("?", XQueryTokenType.INVALID)
                token("$", XPathTokenType.BAD_CHARACTER)
                token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
                state(17)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<a><?lorem:ipsum dolor sed emit?></a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
                state(23)
                token("lorem", XQueryTokenType.XML_PI_TARGET_NCNAME)
                token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
                token("ipsum", XQueryTokenType.XML_PI_TARGET_NCNAME)
                token(" ", XQueryTokenType.XML_WHITE_SPACE)
                state(24)
                token("dolor sed emit", XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
                token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
                state(17)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<?a ?", 0, 5, 17) {
                state(17)
                token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
                state(23)
                token("a", XQueryTokenType.XML_PI_TARGET_NCNAME)
                token(" ", XQueryTokenType.XML_WHITE_SPACE)
                state(24)
                token("?", XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(17)
            }

            tokenize("<?a ", 0, 4, 17) {
                token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
                state(23)
                token("a", XQueryTokenType.XML_PI_TARGET_NCNAME)
                token(" ", XQueryTokenType.XML_WHITE_SPACE)
                state(24)
            }
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (102) CommonContent")
        fun commonContent() = tokenize("<a>{{}}</a>") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(17)
            token("{{", XPathTokenType.ESCAPED_CHARACTER)
            token("}}", XPathTokenType.ESCAPED_CHARACTER)
            token("</", XQueryTokenType.CLOSE_XML_TAG)
            state(12)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(0)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        fun predefinedEntityRef() {
            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            tokenize("<a>One&abc;&aBc;&Abc;&ABC;&a4;&a;Two</a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("One", XQueryTokenType.XML_ELEMENT_CONTENTS)
                token("&abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&aBc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&Abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&ABC;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a4;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("Two", XQueryTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<a>&</a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<a>&abc!</a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("&abc", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token("!", XQueryTokenType.XML_ELEMENT_CONTENTS)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }

            tokenize("<a>&") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("<a>&abc") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("&abc", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("<a>&;</a>") {
                state(0x60000000 or 30)
                token("<", XQueryTokenType.OPEN_XML_TAG)
                state(0x60000000 or 11)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(17)
                token("&;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                token("</", XQueryTokenType.CLOSE_XML_TAG)
                state(12)
                token("a", XQueryTokenType.XML_TAG_NCNAME)
                token(">", XQueryTokenType.END_XML_TAG)
                state(0)
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class DirElemContent_CharRef {
            @Test
            @DisplayName("decimal")
            fun decimal() {
                tokenize("<a>One&#20;Two</a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("One", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("&#20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }

                tokenize("<a>One&#9;Two</a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("One", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("&#9;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }

                tokenize("<a>&#</a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }

                tokenize("<a>&# </a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }

                tokenize("<a>&#") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("<a>&#12") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#12", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("<a>&#;</a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }
            }

            @Test
            @DisplayName("hexadecimal")
            fun hexadecimal() {
                tokenize("<a>One&#x20;&#xae;&#xDC;Two</a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("One", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("&#x20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xae;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xDC;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }

                tokenize("<a>&#x</a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }

                tokenize("<a>&#x </a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }

                tokenize("<a>&#x") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("<a>&#x12") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#x12", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("<a>&#x;&#x2G;&#x2g;&#xg2;</a>") {
                    state(0x60000000 or 30)
                    token("<", XQueryTokenType.OPEN_XML_TAG)
                    state(0x60000000 or 11)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(17)
                    token("&#x;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                    token("&#x2", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("G;", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("&#x2", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g;", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g2;", XQueryTokenType.XML_ELEMENT_CONTENTS)
                    token("</", XQueryTokenType.CLOSE_XML_TAG)
                    state(12)
                    token("a", XQueryTokenType.XML_TAG_NCNAME)
                    token(">", XQueryTokenType.END_XML_TAG)
                    state(0)
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (103) DirCommentConstructor ; XQuery 1.0 EBNF (104) DirCommentContents")
    internal inner class DirCommentConstructor {
        @Test
        @DisplayName("direct comment constructor")
        fun dirCommentConstructor() {
            token("<", 0, XPathTokenType.LESS_THAN)
            token("<!", 0, XQueryTokenType.INVALID)
            token("<!-", 0, XQueryTokenType.INVALID)
            token("<!--", 5, XQueryTokenType.XML_COMMENT_START_TAG)

            // Unary Minus
            tokenize("--") {
                token("-", XPathTokenType.MINUS)
                token("-", XPathTokenType.MINUS)
            }

            token("-->", XQueryTokenType.XML_COMMENT_END_TAG)

            tokenize("<!-- Test") {
                token("<!--", XQueryTokenType.XML_COMMENT_START_TAG)
                state(5)
                token(" Test", XQueryTokenType.XML_COMMENT)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<!-- Test --") {
                token("<!--", XQueryTokenType.XML_COMMENT_START_TAG)
                state(5)
                token(" Test --", XQueryTokenType.XML_COMMENT)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<!-- Test -->") {
                token("<!--", XQueryTokenType.XML_COMMENT_START_TAG)
                state(5)
                token(" Test ", XQueryTokenType.XML_COMMENT)
                token("-->", XQueryTokenType.XML_COMMENT_END_TAG)
                state(0)
            }

            tokenize("<!--\nMultiline\nComment\n-->") {
                token("<!--", XQueryTokenType.XML_COMMENT_START_TAG)
                state(5)
                token("\nMultiline\nComment\n", XQueryTokenType.XML_COMMENT)
                token("-->", XQueryTokenType.XML_COMMENT_END_TAG)
                state(0)
            }

            tokenize("<!---") {
                token("<!--", XQueryTokenType.XML_COMMENT_START_TAG)
                state(5)
                token("-", XQueryTokenType.XML_COMMENT)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<!----") {
                token("<!--", XQueryTokenType.XML_COMMENT_START_TAG)
                state(5)
                token("--", XQueryTokenType.XML_COMMENT)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            tokenize("<!-- Test", 4, 9, 5) {
                token(" Test", XQueryTokenType.XML_COMMENT)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<!-- Test -->", 4, 13, 5) {
                token(" Test ", XQueryTokenType.XML_COMMENT)
                token("-->", XQueryTokenType.XML_COMMENT_END_TAG)
                state(0)
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (105) DirPIConstructor ; XQuery 1.0 EBNF (106) DirPIContents")
    fun dirPIConstructor() {
        token("<?", 21, XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
        token("?>", 0, XQueryTokenType.PROCESSING_INSTRUCTION_END)

        tokenize("<?for  6^gkgw~*?g?>") {
            token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            state(21)
            token("for", XQueryTokenType.XML_PI_TARGET_NCNAME)
            token("  ", XQueryTokenType.XML_WHITE_SPACE)
            state(22)
            token("6^gkgw~*?g", XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
            token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
            state(0)
        }

        tokenize("<?for?>") {
            token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            state(21)
            token("for", XQueryTokenType.XML_PI_TARGET_NCNAME)
            token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
            state(0)
        }

        tokenize("<?*?$?>") {
            token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            state(21)
            token("*", XPathTokenType.BAD_CHARACTER)
            token("?", XQueryTokenType.INVALID)
            token("$", XPathTokenType.BAD_CHARACTER)
            token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
            state(0)
        }

        tokenize("<?a ?") {
            token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            state(21)
            token("a", XQueryTokenType.XML_PI_TARGET_NCNAME)
            token(" ", XQueryTokenType.XML_WHITE_SPACE)
            state(22)
            token("?", XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }

        tokenize("<?a ") {
            token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            state(21)
            token("a", XQueryTokenType.XML_PI_TARGET_NCNAME)
            token(" ", XQueryTokenType.XML_WHITE_SPACE)
            state(22)
        }

        tokenize("<?lorem:ipsum dolor sed emit?>") {
            token("<?", XQueryTokenType.PROCESSING_INSTRUCTION_BEGIN)
            state(21)
            token("lorem", XQueryTokenType.XML_PI_TARGET_NCNAME)
            token(":", XQueryTokenType.XML_TAG_QNAME_SEPARATOR)
            token("ipsum", XQueryTokenType.XML_PI_TARGET_NCNAME)
            token(" ", XQueryTokenType.XML_WHITE_SPACE)
            state(22)
            token("dolor sed emit", XQueryTokenType.PROCESSING_INSTRUCTION_CONTENTS)
            token("?>", XQueryTokenType.PROCESSING_INSTRUCTION_END)
            state(0)
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (107) CDataSection ; XQuery 1.0 EBNF (108) CDataSectionContents")
    internal inner class CDataSection {
        @Test
        @DisplayName("cdata section")
        fun cdataSection() {
            token("<", 0, XPathTokenType.LESS_THAN)
            token("<!", 0, XQueryTokenType.INVALID)
            token("<![", 0, XQueryTokenType.INVALID)
            token("<![C", 0, XQueryTokenType.INVALID)
            token("<![CD", 0, XQueryTokenType.INVALID)
            token("<![CDA", 0, XQueryTokenType.INVALID)
            token("<![CDAT", 0, XQueryTokenType.INVALID)
            token("<![CDATA", 0, XQueryTokenType.INVALID)
            token("<![CDATA[", 7, XQueryTokenType.CDATA_SECTION_START_TAG)

            token("]", XPathTokenType.SQUARE_CLOSE)
            tokenize("]]") {
                token("]", XPathTokenType.SQUARE_CLOSE)
                token("]", XPathTokenType.SQUARE_CLOSE)
            }
            token("]]>", XQueryTokenType.CDATA_SECTION_END_TAG)

            tokenize("<![CDATA[ Test") {
                token("<![CDATA[", XQueryTokenType.CDATA_SECTION_START_TAG)
                state(7)
                token(" Test", XQueryTokenType.CDATA_SECTION)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<![CDATA[ Test ]]") {
                token("<![CDATA[", XQueryTokenType.CDATA_SECTION_START_TAG)
                state(7)
                token(" Test ]]", XQueryTokenType.CDATA_SECTION)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<![CDATA[ Test ]]>") {
                token("<![CDATA[", XQueryTokenType.CDATA_SECTION_START_TAG)
                state(7)
                token(" Test ", XQueryTokenType.CDATA_SECTION)
                token("]]>", XQueryTokenType.CDATA_SECTION_END_TAG)
                state(0)
            }

            tokenize("<![CDATA[\nMultiline\nComment\n]]>") {
                token("<![CDATA[", XQueryTokenType.CDATA_SECTION_START_TAG)
                state(7)
                token("\nMultiline\nComment\n", XQueryTokenType.CDATA_SECTION)
                token("]]>", XQueryTokenType.CDATA_SECTION_END_TAG)
                state(0)
            }

            tokenize("<![CDATA[]") {
                token("<![CDATA[", XQueryTokenType.CDATA_SECTION_START_TAG)
                state(7)
                token("]", XQueryTokenType.CDATA_SECTION)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<![CDATA[]]") {
                token("<![CDATA[", XQueryTokenType.CDATA_SECTION_START_TAG)
                state(7)
                token("]]", XQueryTokenType.CDATA_SECTION)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }
        }

        @Test
        @DisplayName("initial state")
        fun initialState() {
            tokenize("<![CDATA[ Test", 9, 14, 7) {
                token(" Test", XQueryTokenType.CDATA_SECTION)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }

            tokenize("<![CDATA[ Test ]]>", 9, 18, 7) {
                token(" Test ", XQueryTokenType.CDATA_SECTION)
                token("]]>", XQueryTokenType.CDATA_SECTION_END_TAG)
                state(0)
            }
        }
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (110) CompDocConstructor")
    fun compDocConstructor() {
        token("document", XQueryTokenType.K_DOCUMENT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (111) CompElemConstructor")
    fun compElemConstructor() {
        token("element", XPathTokenType.K_ELEMENT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (113) CompAttrConstructor")
    fun compAttrConstructor() {
        token("attribute", XPathTokenType.K_ATTRIBUTE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (114) CompTextConstructor")
    fun compTextConstructor() {
        token("text", XPathTokenType.K_TEXT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (115) CompCommentConstructor")
    fun compCommentConstructor() {
        token("comment", XPathTokenType.K_COMMENT)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (116) CompPIConstructor")
    fun compPIConstructor() {
        token("processing-instruction", XPathTokenType.K_PROCESSING_INSTRUCTION)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (117) SingleType")
    fun singleType() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (118) TypeDeclaration")
    fun typeDeclaration() {
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (119) SequenceType")
    fun sequenceType() {
        token("empty-sequence", XPathTokenType.K_EMPTY_SEQUENCE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (120) OccurrenceIndicator")
    fun occurrenceIndicator() {
        token("?", XPathTokenType.OPTIONAL)
        token("*", XPathTokenType.STAR)
        token("+", XPathTokenType.PLUS)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (121) ItemType")
    fun itemType() {
        token("item", XPathTokenType.K_ITEM)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (124) AnyKindTest")
    fun anyKindTest() {
        token("node", XPathTokenType.K_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (125) DocumentTest")
    fun documentTest() {
        token("document-node", XPathTokenType.K_DOCUMENT_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (126) TextTest")
    fun textTest() {
        token("text", XPathTokenType.K_TEXT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (127) CommentTest")
    fun commentTest() {
        token("comment", XPathTokenType.K_COMMENT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (128) PITest")
    fun piTest() {
        token("processing-instruction", XPathTokenType.K_PROCESSING_INSTRUCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (129) AttributeTest")
    fun attributeTest() {
        token("attribute", XPathTokenType.K_ATTRIBUTE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (130) AttribNameOrWildcard")
    fun attribNameOrWildcard() {
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (131) SchemaAttributeTest")
    fun schemaAttributeTest() {
        token("schema-attribute", XPathTokenType.K_SCHEMA_ATTRIBUTE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (133) ElementTest")
    fun elementTest() {
        token("element", XPathTokenType.K_ELEMENT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (134) ElementNameOrWildcard")
    fun elementNameOrWildcard() {
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (135) SchemaElementTest")
    fun schemaElementTest() {
        token("schema-element", XPathTokenType.K_SCHEMA_ELEMENT)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (141) IntegerLiteral")
    fun integerLiteral() {
        token("1234", XPathTokenType.INTEGER_LITERAL)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (142) DecimalLiteral")
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
    @DisplayName("XQuery 1.0 EBNF (143) DoubleLiteral")
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
    @DisplayName("XQuery 1.0 EBNF (144) StringLiteral")
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
        @DisplayName("XQuery 1.0 EBNF (145) PredefinedEntityRef")
        fun predefinedEntityRef() {
            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            tokenize("\"One&abc;&aBc;&Abc;&ABC;&a4;&a;Two\"") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("&abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&aBc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&Abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&ABC;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a4;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            tokenize("'One&abc;&aBc;&Abc;&ABC;&a4;&a;Two'") {
                token("'", XPathTokenType.STRING_LITERAL_START)
                state(2)
                token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("&abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&aBc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&Abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&ABC;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a4;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("'", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            tokenize("\"&\"") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            tokenize("\"&abc!\"") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("&abc", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token("!", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            tokenize("\"& \"") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token(" ", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            tokenize("\"&") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("\"&abc") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("&abc", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("\"&;\"") {
                token("\"", XPathTokenType.STRING_LITERAL_START)
                state(1)
                token("&;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                token("\"", XPathTokenType.STRING_LITERAL_END)
                state(0)
            }

            token("&", XQueryTokenType.ENTITY_REFERENCE_NOT_IN_STRING)
        }

        @Test
        @DisplayName("XQuery 1.0 EBNF (146) EscapeQuot")
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
        @DisplayName("XQuery 1.0 EBNF (147) EscapeApos")
        fun escapeApos() = tokenize("'One''Two'") {
            token("'", XPathTokenType.STRING_LITERAL_START)
            state(2)
            token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("''", XPathTokenType.ESCAPED_CHARACTER)
            token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
            token("'", XPathTokenType.STRING_LITERAL_END)
            state(0)
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("decimal")
            fun decimal() {
                tokenize("\"One&#20;Two\"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("'One&#20;Two'") {
                    token("'", XPathTokenType.STRING_LITERAL_START)
                    state(2)
                    token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("'", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("\"One&#9;Two\"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#9;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("\"&#\"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("\"&# \"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("\"&#") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#12") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#12", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#;\"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }
            }

            @Test
            @DisplayName("hexadecimal")
            fun hexadecimal() {
                tokenize("\"One&#x20;&#xae;&#xDC;Two\"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#x20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xae;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xDC;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("'One&#x20;&#xae;&#xDC;Two'") {
                    token("'", XPathTokenType.STRING_LITERAL_START)
                    state(2)
                    token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#x20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xae;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xDC;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("'", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("\"&#x\"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("\"&#x \"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }

                tokenize("\"&#x") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#x12") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#x12", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("\"&#x;&#x2G;&#x2g;&#xg2;\"") {
                    token("\"", XPathTokenType.STRING_LITERAL_START)
                    state(1)
                    token("&#x;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                    token("&#x2", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("G;", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#x2", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g;", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g2;", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("\"", XPathTokenType.STRING_LITERAL_END)
                    state(0)
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 1.0 EBNF (151) Comment ; XQuery 1.0 EBNF (159) CommentContents")
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
    @DisplayName("XQuery 1.0 EBNF (154) QName ; Namespaces in XML 1.0 EBNF (7) QName")
    fun qname() = tokenize("one:two") {
        token("one", XPathTokenType.NCNAME)
        token(":", XPathTokenType.QNAME_SEPARATOR)
        token("two", XPathTokenType.NCNAME)
    }

    @Test
    @DisplayName("XQuery 1.0 EBNF (155) NCName ; Namespaces in XML 1.0 EBNF (4) NCName")
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
    @DisplayName("XQuery 1.0 EBNF (156) S")
    fun s() {
        token(" ", XPathTokenType.WHITE_SPACE)
        token("\t", XPathTokenType.WHITE_SPACE)
        token("\r", XPathTokenType.WHITE_SPACE)
        token("\n", XPathTokenType.WHITE_SPACE)

        tokenize("   \t  \r\n ") {
            token("   \t  \r\n ", XPathTokenType.WHITE_SPACE)
        }
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (18) DecimalFormatDecl")
    fun decimalFormatDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("decimal-format", XQueryTokenType.K_DECIMAL_FORMAT)
        token("default", XPathTokenType.K_DEFAULT)
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (19) DFPropertyName")
    fun dfPropertyName() {
        token("decimal-separator", XQueryTokenType.K_DECIMAL_SEPARATOR)
        token("grouping-separator", XQueryTokenType.K_GROUPING_SEPARATOR)
        token("infinity", XQueryTokenType.K_INFINITY)
        token("minus-sign", XQueryTokenType.K_MINUS_SIGN)
        token("NaN", XQueryTokenType.K_NAN)
        token("percent", XQueryTokenType.K_PERCENT)
        token("per-mille", XQueryTokenType.K_PER_MILLE)
        token("zero-digit", XQueryTokenType.K_ZERO_DIGIT)
        token("digit", XQueryTokenType.K_DIGIT)
        token("pattern-separator", XQueryTokenType.K_PATTERN_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (26) AnnotatedDecl")
    fun annotatedDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (27) Annotation")
    fun annotation() {
        token("%", XQueryTokenType.ANNOTATION_INDICATOR)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)

        token("public", XQueryTokenType.K_PUBLIC)
        token("private", XQueryTokenType.K_PRIVATE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (31) ContextItemDecl")
    fun contextItemDecl() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("context", XQueryTokenType.K_CONTEXT)
        token("item", XPathTokenType.K_ITEM)
        token("as", XPathTokenType.K_AS)
        token("external", XQueryTokenType.K_EXTERNAL)
        token(":=", XPathTokenType.ASSIGN_EQUAL)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (46) AllowingEmpty")
    fun allowingEmpty() {
        token("allowing", XQueryTokenType.K_ALLOWING)
        token("empty", XPathTokenType.K_EMPTY)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (50) WindowClause")
    fun windowClause() {
        token("for", XPathTokenType.K_FOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (51) TumblingWindowClause")
    fun tumblingWindowClause() {
        token("tumbling", XQueryTokenType.K_TUMBLING)
        token("window", XPathTokenType.K_WINDOW)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (52) SlidingWindowClause")
    fun slidingWindowClause() {
        token("sliding", XQueryTokenType.K_SLIDING)
        token("window", XPathTokenType.K_WINDOW)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (53) WindowStartCondition")
    fun windowStartCondition() {
        token("start", XPathTokenType.K_START)
        token("when", XQueryTokenType.K_WHEN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (54) WindowEndCondition")
    fun windowEndCondition() {
        token("only", XQueryTokenType.K_ONLY)
        token("end", XPathTokenType.K_END)
        token("when", XQueryTokenType.K_WHEN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (55) WindowVars")
    fun windowVars() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("previous", XQueryTokenType.K_PREVIOUS)
        token("next", XQueryTokenType.K_NEXT)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (59) CountClause")
    fun countClause() {
        token("count", XQueryTokenType.K_COUNT)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (61) GroupByClause")
    fun groupByClause() {
        token("group", XQueryTokenType.K_GROUP)
        token("by", XQueryTokenType.K_BY)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (62) GroupingSpecList")
    fun groupingSpecList() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (63) GroupingSpec")
    fun groupingSpec() {
        token(":=", XPathTokenType.ASSIGN_EQUAL)
        token("collation", XQueryTokenType.K_COLLATION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (64) GroupingVariable")
    fun groupingVariable() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (69) ReturnClause")
    fun returnClause() {
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (71) SwitchExpr")
    fun switchExpr() {
        token("switch", XQueryTokenType.K_SWITCH)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("default", XPathTokenType.K_DEFAULT)
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (72) SwitchCaseClause")
    fun switchCaseClause() {
        token("case", XPathTokenType.K_CASE)
        token("return", XPathTokenType.K_RETURN)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (76) SequenceTypeUnion")
    fun sequenceTypeUnion() {
        token("|", XPathTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (79) TryClause")
    fun tryClause() {
        token("try", XQueryTokenType.K_TRY)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (81) CatchClause")
    fun catchClause() {
        token("catch", XQueryTokenType.K_CATCH)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (82) CatchErrorList")
    fun catchErrorList() {
        token("|", XPathTokenType.UNION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (86) StringConcatExpr")
    fun stringConcatExpr() {
        token("||", XPathTokenType.CONCATENATION)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (101) ValidateExpr")
    fun validateExpr_XQuery30() {
        token("validate", XQueryTokenType.K_VALIDATE)
        token("type", XPathTokenType.K_TYPE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (106) SimpleMapExpr")
    fun simpleMapExpr() {
        token("!", XPathTokenType.MAP_OPERATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (121) ArgumentList")
    fun argumentList() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (135) ArgumentPlaceholder")
    fun argumentPlaceholder() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (156) CompNamespaceConstructor")
    fun compNamespaceConstructor() {
        token("namespace", XPathTokenType.K_NAMESPACE)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (164) NamedFunctionRef")
    fun namedFunctionRef() {
        token("#", XPathTokenType.FUNCTION_REF_OPERATOR)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (165) InlineFunctionExpr")
    fun inlineFunctionExpr() {
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (177) NamespaceNodeTest")
    fun namespaceNodeTest() {
        token("namespace-node", XPathTokenType.K_NAMESPACE_NODE)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (192) AnyFunctionTest")
    fun anyFunctionTest() {
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("*", XPathTokenType.STAR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (193) TypedFunctionTest")
    fun typedFunctionTest() {
        token("function", XPathTokenType.K_FUNCTION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 3.0 EBNF (164) ParenthesizedItemType")
    fun parenthesizedItemType() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Nested
    @DisplayName("XQuery 3.0 EBNF (202) BracedURILiteral")
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

        @Test
        @DisplayName("braced uri literal in Pragma")
        fun inPragma() {
            tokenize("Q", 0, 1, 8) {
                token("Q", XPathTokenType.NCNAME)
                state(9)
            }

            tokenize("Q{", 0, 2, 8) {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(31)
            }

            tokenize("Q{Hello World}", 0, 14, 8) {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(31)
                token("Hello World", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(9)
            }

            // NOTE: "", '', {{ and }} are used as escaped characters in string and attribute literals.
            tokenize("Q{A\"\"B''C{{D}}E}", 0, 16, 8) {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(31)
                token("A\"\"B''C", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("{", XPathTokenType.BAD_CHARACTER)
                token("{", XPathTokenType.BAD_CHARACTER)
                token("D", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(9)
                token("}E}", XPathTokenType.PRAGMA_CONTENTS)
                state(6)
                token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
                state(0)
            }
        }

        @Test
        @DisplayName("XQuery 3.0 EBNF (203) PredefinedEntityRef")
        fun predefinedEntityRef() {
            // NOTE: The predefined entity reference names are not validated by the lexer, as some
            // XQuery processors support HTML predefined entities. Shifting the name validation to
            // the parser allows proper validation errors to be generated.

            tokenize("Q{One&abc;&aBc;&Abc;&ABC;&a4;&a;Two}") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("&abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&aBc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&Abc;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&ABC;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a4;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("&a;", XQueryTokenType.PREDEFINED_ENTITY_REFERENCE)
                token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(0)
            }

            tokenize("Q{&}") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(0)
            }

            tokenize("Q{&abc!}") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("&abc", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token("!", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(0)
            }

            tokenize("Q{& }") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                token(" ", XPathTokenType.STRING_LITERAL_CONTENTS)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(0)
            }

            tokenize("Q{&") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("&", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("Q{&abc") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("&abc", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
            }

            tokenize("Q{&;}") {
                token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                state(26)
                token("&;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                state(0)
            }
        }

        @Nested
        @DisplayName("XQuery 1.0 EBNF (153) CharRef")
        internal inner class CharRef {
            @Test
            @DisplayName("octal")
            fun octal() {
                tokenize("Q{One&#20;Two}") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }

                tokenize("Q{&#}") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }

                tokenize("Q{&# }") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }

                tokenize("Q{&#") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("Q{&#12") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#12", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("Q{&#;}") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }
            }

            @Test
            @DisplayName("hexadecimal")
            fun hexadecimal() {
                tokenize("Q{One&#x20;&#xae;&#xDC;Two}") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("One", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#x20;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xae;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("&#xDC;", XQueryTokenType.CHARACTER_REFERENCE)
                    token("Two", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }

                tokenize("Q{&#x}") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }

                tokenize("Q{&#x }") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token(" ", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }

                tokenize("Q{&#x") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("Q{&#x12") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#x12", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                }

                tokenize("Q{&#x;&#x2G;&#x2g;&#xg2;}") {
                    token("Q{", XPathTokenType.BRACED_URI_LITERAL_START)
                    state(26)
                    token("&#x;", XQueryTokenType.EMPTY_ENTITY_REFERENCE)
                    token("&#x2", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("G;", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#x2", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g;", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("&#x", XQueryTokenType.PARTIAL_ENTITY_REFERENCE)
                    token("g2;", XPathTokenType.STRING_LITERAL_CONTENTS)
                    token("}", XPathTokenType.BRACED_URI_LITERAL_END)
                    state(0)
                }
            }
        }
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (19) DFPropertyName")
    fun dfPropertyName_XQuery31() {
        token("decimal-separator", XQueryTokenType.K_DECIMAL_SEPARATOR)
        token("grouping-separator", XQueryTokenType.K_GROUPING_SEPARATOR)
        token("infinity", XQueryTokenType.K_INFINITY)
        token("minus-sign", XQueryTokenType.K_MINUS_SIGN)
        token("NaN", XQueryTokenType.K_NAN)
        token("percent", XQueryTokenType.K_PERCENT)
        token("per-mille", XQueryTokenType.K_PER_MILLE)
        token("zero-digit", XQueryTokenType.K_ZERO_DIGIT)
        token("digit", XQueryTokenType.K_DIGIT)
        token("pattern-separator", XQueryTokenType.K_PATTERN_SEPARATOR)
        token("exponent-separator", XQueryTokenType.K_EXPONENT_SEPARATOR) // New in XQuery 3.1
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (96) ArrowExpr")
    fun arrowExpr() {
        token("=>", XPathTokenType.ARROW)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (125) Lookup")
    fun lookup() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (126) KeySpecifier")
    fun keySpecifier() {
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (170) MapConstructor")
    fun mapConstructor() {
        token("map", XPathTokenType.K_MAP)
        token("{", XPathTokenType.BLOCK_OPEN)
        token(",", XPathTokenType.COMMA)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (171) MapConstructorEntry")
    fun mapConstructorEntry() {
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (175) SquareArrayConstructor")
    fun squareArrayConstructor() {
        token("[", XPathTokenType.SQUARE_OPEN)
        token(",", XPathTokenType.COMMA)
        token("]", XPathTokenType.SQUARE_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (176) CurlyArrayConstructor")
    fun curlyArrayConstructor() {
        token("array", XPathTokenType.K_ARRAY)
        token("{", XPathTokenType.BLOCK_OPEN)
        token("}", XPathTokenType.BLOCK_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (177) StringConstructor ; XQuery 3.1 EBNF (179) StringConstructorChars")
    fun stringConstructor() {
        token("`", XQueryTokenType.INVALID)
        token("``", XQueryTokenType.INVALID)

        token("]", XPathTokenType.SQUARE_CLOSE)
        token("]`", XQueryTokenType.INVALID)
        token("]``", XQueryTokenType.STRING_CONSTRUCTOR_END)

        tokenize("``[") {
            token("``[", XQueryTokenType.STRING_CONSTRUCTOR_START)
            state(27)
            token("", XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }

        tokenize("``[One]Two]`") {
            token("``[", XQueryTokenType.STRING_CONSTRUCTOR_START)
            state(27)
            token("One]Two]`", XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            state(6)
            token("", XPathTokenType.UNEXPECTED_END_OF_BLOCK)
            state(0)
        }

        tokenize("``[One]Two]``") {
            token("``[", XQueryTokenType.STRING_CONSTRUCTOR_START)
            state(27)
            token("One]Two", XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            state(0)
            token("]``", XQueryTokenType.STRING_CONSTRUCTOR_END)
        }

        tokenize("``[`]``") {
            token("``[", XQueryTokenType.STRING_CONSTRUCTOR_START)
            state(27)
            token("`", XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            state(0)
            token("]``", XQueryTokenType.STRING_CONSTRUCTOR_END)
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (180) StringConstructorInterpolation")
    internal inner class StringConstructorInterpolation {
        @Test
        @DisplayName("in DirElemContent as element contents")
        fun inDirElemContent() = tokenize("<a>`{2}`</a>") {
            state(0x60000000 or 30)
            token("<", XQueryTokenType.OPEN_XML_TAG)
            state(0x60000000 or 11)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(17)
            token("`", XQueryTokenType.XML_ELEMENT_CONTENTS)
            token("{", XPathTokenType.BLOCK_OPEN)
            state(18)
            token("2", XPathTokenType.INTEGER_LITERAL)
            token("}", XPathTokenType.BLOCK_CLOSE)
            state(17)
            token("`", XQueryTokenType.XML_ELEMENT_CONTENTS)
            token("</", XQueryTokenType.CLOSE_XML_TAG)
            state(12)
            token("a", XQueryTokenType.XML_TAG_NCNAME)
            token(">", XQueryTokenType.END_XML_TAG)
            state(0)
        }

        @Test
        @DisplayName("invalid open interpolation marker outside StringConstructor")
        fun openInterpolationMarkerInExpr() = tokenize("`{") {
            token("`", XQueryTokenType.INVALID)
            token("{", XPathTokenType.BLOCK_OPEN)
        }

        @Test
        @DisplayName("invalid close interpolation marker outside StringConstructor")
        fun closeInterpolationMarkerInExpr() = tokenize("}`") {
            token("}", XPathTokenType.BLOCK_CLOSE)
            token("`", XQueryTokenType.INVALID)
        }

        @Test
        @DisplayName("in StringConstructor")
        fun stringConstructorInterpolation() = tokenize("``[One`{2}`Three]``") {
            token("``[", XQueryTokenType.STRING_CONSTRUCTOR_START)
            state(27)
            token("One", XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            token("`{", XQueryTokenType.STRING_INTERPOLATION_OPEN)
            state(28)
            token("2", XPathTokenType.INTEGER_LITERAL)
            token("}`", XQueryTokenType.STRING_INTERPOLATION_CLOSE)
            state(27)
            token("Three", XQueryTokenType.STRING_CONSTRUCTOR_CONTENTS)
            state(0)
            token("]``", XQueryTokenType.STRING_CONSTRUCTOR_END)
        }
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (181) UnaryLookup")
    fun unaryLookup() {
        token("?", XPathTokenType.OPTIONAL)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (211) AnyMapTest")
    fun anyMapTest() {
        token("map", XPathTokenType.K_MAP)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("*", XPathTokenType.STAR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (212) TypedMapTest")
    fun typedMapTest() {
        token("map", XPathTokenType.K_MAP)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (214) AnyArrayTest")
    fun anyArrayTest() {
        token("array", XPathTokenType.K_ARRAY)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token("*", XPathTokenType.STAR)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 3.1 EBNF (215) TypedArrayTest")
    fun typedArrayTest() {
        token("array", XPathTokenType.K_ARRAY)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (25) DefaultNamespaceDecl")
    fun defaultNamespaceDecl_XQuery40() {
        token("declare", XQueryTokenType.K_DECLARE)
        token("default", XPathTokenType.K_DEFAULT)
        token("element", XPathTokenType.K_ELEMENT)
        token("function", XPathTokenType.K_FUNCTION)
        token("type", XPathTokenType.K_TYPE)
        token("namespace", XPathTokenType.K_NAMESPACE)
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (33) FunctionSignature")
    fun functionSignature() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (38) ItemTypeDecl")
    fun itemTypeDecl() {
        token("item-type", XQueryTokenType.K_ITEM_TYPE)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (43) WithExpr")
    fun withExpr() {
        token("with", XPathTokenType.K_WITH)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XPath 4.0 ED EBNF (44) NamespaceDeclaration")
    fun namespaceDeclaration() {
        token("=", XPathTokenType.EQUAL)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (45) TernaryConditionalExpr")
    fun ternaryConditionalExpr() {
        token("??", XPathTokenType.TERNARY_IF)
        token("!!", XPathTokenType.TERNARY_ELSE)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (52) ForMemberClause ; XQuery 4.0 ED EBNF (53) ForMemberBinding")
    fun forMemberClause() {
        token("for", XPathTokenType.K_FOR)
        token("member", XPathTokenType.K_MEMBER)
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (78) QuantifierBinding")
    fun quantifierBinding() {
        token("$", XPathTokenType.VARIABLE_INDICATOR)
        token("in", XPathTokenType.K_IN)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (96) OtherwiseExpr")
    fun otherwiseExpr() {
        token("otherwise", XPathTokenType.K_OTHERWISE)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (106) FatArrowTarget")
    fun fatArrowTarget() {
        token("=>", XPathTokenType.ARROW)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (107) ThinArrowTarget")
    fun thinArrowTarget() {
        token("->", XPathTokenType.THIN_ARROW)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (132) PositionalArgumentList")
    fun positionalArgumentList() {
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (133) PositionalArguments")
    fun positionalArguments() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (134) KeywordArguments")
    fun keywordArguments() {
        token(",", XPathTokenType.COMMA)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (134) KeywordArgument")
    fun keywordArgument() {
        token(":", XPathTokenType.QNAME_SEPARATOR)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (185) InlineFunctionExpr")
    fun inlineFunctionExpr_XQuery40() {
        token("function", XPathTokenType.K_FUNCTION)
        token("->", XPathTokenType.THIN_ARROW)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (228) RecordTest")
    fun recordTest() {
        token("record", XPathTokenType.K_RECORD)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (229) FieldDeclaration")
    fun fieldDeclaration() {
        token("?", XPathTokenType.OPTIONAL)
        token("as", XPathTokenType.K_AS)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (231) SelfReference")
    fun selfReference() {
        token("..", XPathTokenType.PARENT_SELECTOR)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (232) ExtensibleFlag")
    fun extensibleFlag() {
        token(",", XPathTokenType.COMMA)
        token("*", XPathTokenType.STAR)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (233) LocalUnionType")
    fun localUnionType() {
        token("union", XPathTokenType.K_UNION)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }

    @Test
    @DisplayName("XQuery 4.0 ED EBNF (234) EnumerationType")
    fun enumerationType() {
        token("enum", XPathTokenType.K_ENUM)
        token("(", XPathTokenType.PARENTHESIS_OPEN)
        token(",", XPathTokenType.COMMA)
        token(")", XPathTokenType.PARENTHESIS_CLOSE)
    }
}
