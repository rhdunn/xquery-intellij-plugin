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
package uk.co.reecedunn.intellij.plugin.xpath.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.io.decode
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - Parser")
@Suppress("ClassName")
private class XPathParserTest : ParserTestCase() {
    fun parseResource(resource: String): XPath {
        val file = ResourceVirtualFile(XPathParserTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun loadResource(resource: String): String? {
        val file = ResourceVirtualFile(XPathParserTest::class.java.classLoader, resource)
        return file.inputStream?.decode()
    }

    @Nested
    @DisplayName("Parser")
    internal inner class Parser {
        @Test
        @DisplayName("empty buffer")
        fun emptyBuffer() {
            val expected = "XPathImpl[FILE(0:0)]\n"

            assertThat(prettyPrintASTNode(parseText("")), `is`(expected))
        }

        @Test
        @DisplayName("bad characters")
        fun badCharacters() {
            val expected =
                "XPathImpl[FILE(0:3)]\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:0)]('XPST0003: Missing expression.')\n" +
                "   PsiErrorElementImpl[ERROR_ELEMENT(0:1)]('XPST0003: Unexpected token.')\n" +
                "      LeafPsiElement[BAD_CHARACTER(0:1)]('~')\n" +
                "   LeafPsiElement[BAD_CHARACTER(1:2)]('\uFFFE')\n" +
                "   LeafPsiElement[BAD_CHARACTER(2:3)]('\uFFFF')\n"

            assertThat(prettyPrintASTNode(parseText("~\uFFFE\uFFFF")), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (1) XPath ; XPath 1.0 EBNF (2) Expr")
    internal inner class XPath_XPath10 {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-1.0/IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xpath-1.0/IntegerLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (22) NodeTest ; XPath 1.0 EBNF (36) KindTest")
    internal inner class NodeTest {
        @Nested
        @DisplayName("XPath 1.0 EBNF (37) AnyKindTest")
        internal inner class AnyKindTest {
            @Test
            @DisplayName("any kind test")
            fun anyKindTest() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any kind test; compact whitespace")
            fun anyKindTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 1.0 EBNF (38) TextTest")
        internal inner class TestTest {
            @Test
            @DisplayName("text test")
            fun textTest() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_TextTest.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_TextTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("text test; compact whitespace")
            fun textTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_TextTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_TextTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_TextTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_TextTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 1.0 EBNF (39) CommentTest")
        internal inner class CommentTest {
            @Test
            @DisplayName("comment test")
            fun commentTest() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_CommentTest.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_CommentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("comment test; compact whitespace")
            fun commentTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_CommentTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_CommentTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_CommentTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_CommentTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 1.0 EBNF (40) PITest")
        internal inner class PITest {
            @Test
            @DisplayName("processing instruction test")
            fun piTest() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_PITest.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_PITest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("processing instruction test; compact whitespace")
            fun piTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_PITest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_PITest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-1.0/NodeTest_PITest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NodeTest_PITest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("StringLiteral")
            internal inner class StringLiteral {
                @Test
                @DisplayName("string literal")
                fun stringLiteral() {
                    val expected = loadResource("tests/parser/xpath-1.0/NodeTest_PITest_StringLiteral.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NodeTest_PITest_StringLiteral.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("string literal; compact whitespace")
                fun stringLiteral_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-1.0/NodeTest_PITest_StringLiteral_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NodeTest_PITest_StringLiteral_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (31) VarRef ; XQuery 1.0 EBNF (32) VarName")
    internal inner class VarRef {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/xpath-1.0/VarRef_NCName.txt")
            val actual = parseResource("tests/parser/xpath-1.0/VarRef_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName; compat whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/VarRef_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/VarRef_NCName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val expected = loadResource("tests/parser/xpath-1.0/VarRef.txt")
            val actual = parseResource("tests/parser/xpath-1.0/VarRef.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName; compat whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/VarRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/VarRef_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xpath-1.0/VarRef_MissingVarName.txt")
            val actual = parseResource("tests/parser/xpath-1.0/VarRef_MissingVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (33) ParenthesizedExpr ; XPath 1.0 EBNF (2) Expr")
    internal inner class ParenthesizedExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Single.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Single.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Single_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Single_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple; missing Expr")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; space before next comma")
        fun multiple_SpaceBeforeNextComma() {
            val expected = loadResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_SpaceBeforeNextComma.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_SpaceBeforeNextComma.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ParenthesizedExpr_Expr_Multiple_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (41) StringLiteral")
    internal inner class StringLiteral_XPath10 {
        @Test
        @DisplayName("string literal")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xpath-1.0/StringLiteral.txt")
            val actual = parseResource("tests/parser/xpath-1.0/StringLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("unclosed string literal")
        fun unclosedString() {
            val expected = loadResource("tests/parser/xpath-1.0/StringLiteral_UnclosedString.txt")
            val actual = parseResource("tests/parser/xpath-1.0/StringLiteral_UnclosedString.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (42) IntegerLiteral")
    fun integerLiteral() {
        val expected = loadResource("tests/parser/xpath-1.0/IntegerLiteral.txt")
        val actual = parseResource("tests/parser/xpath-1.0/IntegerLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (43) DecimalLiteral")
    fun decimalLiteral() {
        val expected = loadResource("tests/parser/xpath-1.0/DecimalLiteral.txt")
        val actual = parseResource("tests/parser/xpath-1.0/DecimalLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (44) S ; XML 1.0 EBNF (3) S")
    fun s() {
        val expected = loadResource("tests/parser/xpath-1.0/S.txt")
        val actual = parseResource("tests/parser/xpath-1.0/S.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (23) NameTest")
    internal inner class NameTest {
        @Nested
        @DisplayName("XPath 1.0 EBNF (45) QName")
        internal inner class QName {
            @Test
            @DisplayName("qname")
            fun qname() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword prefix part")
            fun keywordPrefixPart() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_KeywordPrefixPart.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_KeywordPrefixPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword local part")
            fun keywordLocalPart() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_KeywordLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: spaces between colon")
            internal inner class SpacesBetweenColon {
                @Test
                @DisplayName("space before colon")
                fun spaceBeforeColon() {
                    val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_SpaceBeforeColon.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_SpaceBeforeColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space after colon")
                fun spaceAfterColon() {
                    val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_SpaceAfterColon.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_SpaceAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space before and after colon")
                fun spaceBeforeAndAfterColon() {
                    val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_SpaceBeforeAndAfterColon.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_SpaceBeforeAndAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }

            @Test
            @DisplayName("error recovery: integer literal local name")
            fun integerLiteralLocalPart() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_IntegerLiteralLocalName.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_IntegerLiteralLocalName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: missing part")
            internal inner class MissingPart {
                @Test
                @DisplayName("missing local name")
                fun missingLocalName() {
                    val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_MissingLocalPart.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_MissingLocalPart.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("missing prefix")
                fun missingPrefix() {
                    val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_MissingPrefixPart.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_MissingPrefixPart.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("missing prefix and local name")
                fun missingPrefixAndLocalName() {
                    val expected = loadResource("tests/parser/xpath-1.0/NameTest_QName_MissingPrefixAndLocalPart.txt")
                    val actual = parseResource("tests/parser/xpath-1.0/NameTest_QName_MissingPrefixAndLocalPart.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XPath 1.0 EBNF (46) NCName")
        internal inner class NCName {
            @Test
            @DisplayName("identifier")
            fun identifier() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_NCName.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_NCName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword")
            fun keyword() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_NCName_Keyword.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_NCName_Keyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 1.0 EBNF (24) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("ncname")
            fun ncname() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_Wildcard_NCName.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_Wildcard_NCName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("qname: ncname prefix")
            fun ncnamePrefix() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_Wildcard_NCNamePrefixPart.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_Wildcard_NCNamePrefixPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("qname: keyword prefix")
            fun keywordPrefix() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_Wildcard_KeywordPrefixPart.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_Wildcard_KeywordPrefixPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: prefix and local name wildcard")
            fun prefixAndLocalNameWildcard() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_Wildcard_PrefixAndLocalPart.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_Wildcard_PrefixAndLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing prefix")
            fun missingPrefix() {
                val expected = loadResource("tests/parser/xpath-1.0/NameTest_Wildcard_MissingPrefix.txt")
                val actual = parseResource("tests/parser/xpath-1.0/NameTest_Wildcard_MissingPrefix.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("error recovery: spaces between colon")
            internal inner class SpacesBetweenColon {
                @Test
                @DisplayName("space before colon")
                fun spaceBeforeColon() {
                    val expected = loadResource("tests/parser/xpath-2.0/NameTest_Wildcard_SpaceBeforeColon.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NameTest_Wildcard_SpaceBeforeColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space after colon")
                fun spaceAfterColon() {
                    val expected = loadResource("tests/parser/xpath-2.0/NameTest_Wildcard_SpaceAfterColon.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NameTest_Wildcard_SpaceAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("space before and after colon")
                fun spaceBeforeAndAfterColon() {
                    val expected = loadResource("tests/parser/xpath-2.0/NameTest_Wildcard_SpaceBeforeAndAfterColon.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NameTest_Wildcard_SpaceBeforeAndAfterColon.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (1) XPath")
    internal inner class XPath20_XPath {
        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-2.0/XPath_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-2.0/XPath_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/XPath_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/XPath_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: multiple; missing Expr")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/XPath_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/XPath_Multiple_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; space before next comma")
        fun multiple_SpaceBeforeNextComma() {
            val expected = loadResource("tests/parser/xpath-2.0/XPath_Multiple_SpaceBeforeNextComma.txt")
            val actual = parseResource("tests/parser/xpath-2.0/XPath_Multiple_SpaceBeforeNextComma.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (35) NodeTest ; XPath 2.0 EBNF (54) KindTest")
    internal inner class NodeTest_XPath20 {
        @Nested
        @DisplayName("XPath 2.0 EBNF (56) DocumentTest")
        internal inner class DocumentTest {
            @Test
            @DisplayName("document test")
            fun documentTest() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_DocumentTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_DocumentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test; compact whitespace")
            fun documentTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + element test")
            fun elementTest() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_ElementTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_ElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + element test; compact whitespace")
            fun elementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_ElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_ElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + schema element test")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_SchemaElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("document test + schema element test; compact whitespace")
            fun schemaElementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_SchemaElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_DocumentTest_SchemaElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (59) PITest")
        internal inner class PITest {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_PITest_NCName.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_PITest_NCName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("NCName; compact whitespace")
            fun ncname_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_PITest_NCName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_PITest_NCName_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (60) AttributeTest")
        internal inner class AttributeTest {
            @Test
            @DisplayName("attribute test")
            fun attributeTest() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("attribute test; compact whitespace")
            fun attributeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("XPath 2.0 EBNF (61) AttribNameOrWildcard")
            internal inner class AttribNameOrWildcard {
                @Test
                @DisplayName("ncname")
                fun ncname() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_NCName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_NCName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("ncname: compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_NCName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname")
                fun qname() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_QName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_QName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname: compact whitespace")
                fun qname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_QName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_QName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard")
                fun wildcard() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_Wildcard.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_Wildcard.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard: compact whitespace")
                fun wildcard_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_Wildcard_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_Wildcard_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }

            @Nested
            @DisplayName("XPath 2.0 EBNF (70) TypeName")
            internal inner class TypeName {
                @Test
                @DisplayName("type name")
                fun typeName() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("type name: compact whitespace")
                fun typeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name")
                fun typeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName_MissingTypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing comma")
                fun typeName_MissingComma() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName_MissingComma.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_AttributeTest_TypeName_MissingComma.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (62) SchemaAttributeTest")
        internal inner class SchemaAttributeTest {
            @Test
            @DisplayName("schema attribute test")
            fun schemaAttributeTest() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema attribute test; compact whitespace")
            fun schemaAttributeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing attribute declaration")
            fun missingAttributeDeclaration() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest_MissingAttributeDeclaration.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaAttributeTest_MissingAttributeDeclaration.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (64) ElementTest")
        internal inner class ElementTest {
            @Test
            @DisplayName("element test")
            fun elementTest() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("element test; compact whitespace")
            fun elementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Nested
            @DisplayName("XPath 2.0 EBNF (65) ElementNameOrWildcard")
            internal inner class ElementNameOrWildcard {
                @Test
                @DisplayName("ncname")
                fun ncname() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_NCName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_NCName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("ncname: compact whitespace")
                fun ncname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_NCName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_NCName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname")
                fun qname() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_QName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_QName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("qname: compact whitespace")
                fun qname_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_QName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_QName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard")
                fun wildcard() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_Wildcard.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_Wildcard.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("wildcard: compact whitespace")
                fun wildcard_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_Wildcard_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_Wildcard_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }

            @Nested
            @DisplayName("XQuery 1.0 EBNF (139) TypeName")
            internal inner class TypeName {
                @Test
                @DisplayName("type name")
                fun typeName() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("type name: compact whitespace")
                fun typeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name")
                fun typeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_MissingTypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing comma")
                fun typeName_MissingComma() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_MissingComma.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_MissingComma.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("optional type name")
                fun optionalTypeName() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_Optional.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_Optional.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("optional type name: compact whitespace")
                fun optionalTypeName_CompactWhitespace() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_Optional_CompactWhitespace.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_Optional_CompactWhitespace.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }

                @Test
                @DisplayName("error recovery: missing type name on optional type name")
                fun optionalTypeName_MissingTypeName() {
                    val expected = loadResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_Optional_MissingTypeName.txt")
                    val actual = parseResource("tests/parser/xpath-2.0/NodeTest_ElementTest_TypeName_Optional_MissingTypeName.xq")
                    assertThat(prettyPrintASTNode(actual), `is`(expected))
                }
            }
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (66) SchemaElementTest")
        internal inner class SchemaElementTest {
            @Test
            @DisplayName("schema element test")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema element test; compact whitespace")
            fun schemaElementTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing element declaration")
            fun missingElementDeclaration() {
                val expected = loadResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest_MissingElementDeclaration.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NodeTest_SchemaElementTest_MissingElementDeclaration.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (36) NameTest")
    internal inner class NameTest_XPath20 {
        @Nested
        @DisplayName("XPath 1.0 EBNF (37) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("qname: ncname local name")
            fun ncnameLocalName() {
                val expected = loadResource("tests/parser/xpath-2.0/NameTest_Wildcard_NCNameLocalPart.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NameTest_Wildcard_NCNameLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("qname: keyword local name")
            fun keywordLocalName() {
                val expected = loadResource("tests/parser/xpath-2.0/NameTest_Wildcard_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NameTest_Wildcard_KeywordLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing local name")
            fun missingLocalName() {
                val expected = loadResource("tests/parser/xpath-2.0/NameTest_Wildcard_MissingLocalName.txt")
                val actual = parseResource("tests/parser/xpath-2.0/NameTest_Wildcard_MissingLocalName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (46) ParenthesizedExpr")
    internal inner class ParenthesizedExpr_XPath20 {
        @Test
        @DisplayName("empty expression")
        fun emptyExpression() {
            val expected = loadResource("tests/parser/xpath-2.0/ParenthesizedExpr_EmptyExpression.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ParenthesizedExpr_EmptyExpression.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty expression; compact whitespace")
        fun emptyExpression_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ParenthesizedExpr_EmptyExpression_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun emptyExpression_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-2.0/ParenthesizedExpr_EmptyExpression_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ParenthesizedExpr_EmptyExpression_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (73) DoubleLiteral")
    internal inner class DoubleLiteral {
        @Test
        @DisplayName("double literal")
        fun doubleLiteral() {
            val expected = loadResource("tests/parser/xpath-2.0/DoubleLiteral.txt")
            val actual = parseResource("tests/parser/xpath-2.0/DoubleLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("double literal with incomplete exponent")
        fun incompleteExponent() {
            val expected = loadResource("tests/parser/xpath-2.0/DoubleLiteral_IncompleteExponent.txt")
            val actual = parseResource("tests/parser/xpath-2.0/DoubleLiteral_IncompleteExponent.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (74) StringLiteral")
    internal inner class StringLiteral_XPath20 {
        @Test
        @DisplayName("XPath 1.0 EBNF (75) EscapeQuot")
        fun escapeQuot() {
            val expected = loadResource("tests/parser/xpath-2.0/StringLiteral_EscapeQuot.txt")
            val actual = parseResource("tests/parser/xpath-2.0/StringLiteral_EscapeQuot.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (76) EscapeApos")
        fun escapeApos() {
            val expected = loadResource("tests/parser/xpath-2.0/StringLiteral_EscapeApos.txt")
            val actual = parseResource("tests/parser/xpath-2.0/StringLiteral_EscapeApos.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (77) Comment ; XPath 2.0 EBNF (82) CommentContents")
    internal inner class Comment {
        @Test
        @DisplayName("comment")
        fun comment() {
            val expected = loadResource("tests/parser/xpath-2.0/Comment.txt")
            val actual = parseResource("tests/parser/xpath-2.0/Comment.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("unclosed comment")
        fun unclosedComment() {
            val expected = loadResource("tests/parser/xpath-2.0/Comment_UnclosedComment.txt")
            val actual = parseResource("tests/parser/xpath-2.0/Comment_UnclosedComment.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("comment end tag without comment start tag")
        fun unexpectedCommentEndTag() {
            val expected = loadResource("tests/parser/xpath-2.0/Comment_UnexpectedCommentEndTag.txt")
            val actual = parseResource("tests/parser/xpath-2.0/Comment_UnexpectedCommentEndTag.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (45) NodeTest ; XPath 3.0 EBNF (71) KindTest")
    internal inner class NodeTest_XPath30 {
        @Nested
        @DisplayName("XPath 3.0 EBNF (76) NamespaceNodeTest")
        internal inner class NamespaceNodeTest {
            @Test
            @DisplayName("namespace node test")
            fun namespaceNodeTest() {
                val expected = loadResource("tests/parser/xpath-3.0/NodeTest_NamespaceNodeTest.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NodeTest_NamespaceNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("namespace node test; compact whitespace")
            fun namespaceNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-3.0/NodeTest_NamespaceNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NodeTest_NamespaceNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-3.0/NodeTest_NamespaceNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NodeTest_NamespaceNodeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (46) NameTest")
    internal inner class NameTest_XPath30 {
        @Nested
        @DisplayName("XPath 3.0 EBNF (99) URIQualifiedName ; XPath 3.0 EBNF (100) BracedURILiteral")
        internal inner class URIQualifiedName {
            @Test
            @DisplayName("NCName local name")
            fun ncname() {
                val expected = loadResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_NCNameLocalPart.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_NCNameLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("keyword local name")
            fun keyword() {
                val expected = loadResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_KeywordLocalPart.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_KeywordLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing local name")
            fun missingLocalName() {
                val expected = loadResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_MissingLocalPart.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_MissingLocalPart.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: incomplete braced URI literal")
            fun incompleteBracedURILiteral() {
                val expected = loadResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_IncompleteBracedURILiteral.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NameTest_URIQualifiedName_IncompleteBracedURILiteral.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 3.0 EBNF (47) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("BracedURILiteral prefix")
            fun bracedURILiteralPrefix() {
                val expected = loadResource("tests/parser/xpath-3.0/NameTest_Wildcard_BracedURILiteral.txt")
                val actual = parseResource("tests/parser/xpath-3.0/NameTest_Wildcard_BracedURILiteral.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }
}
