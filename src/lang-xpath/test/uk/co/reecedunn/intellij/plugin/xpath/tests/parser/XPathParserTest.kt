/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
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
        return ResourceVirtualFile(XPathParserTest::class.java.classLoader, resource).decode()
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
    @DisplayName("XPath 1.0 EBNF (4) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-1.0/OrExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/OrExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AndExpr")
        fun missingAndExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/OrExpr_MissingAndExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/OrExpr_MissingAndExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/OrExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/OrExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (5) AndExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-1.0/AndExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AndExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ComparisonExpr")
        fun missingComparisonExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/AndExpr_MissingComparisonExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AndExpr_MissingComparisonExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/AndExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AndExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (8) AdditiveExpr")
    internal inner class AdditiveExpr {
        @Test
        @DisplayName("additive expression")
        fun additiveExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/AdditiveExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("additive expression; compact whitespace")
        fun additiveExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/AdditiveExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AdditiveExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing MultiplicativeExpr")
        fun missingMultiplicativeExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/AdditiveExpr_MissingMultiplicativeExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AdditiveExpr_MissingMultiplicativeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/AdditiveExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AdditiveExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (9) MultiplicativeExpr")
    internal inner class MultiplicativeExpr_XPath10 {
        @Test
        @DisplayName("multiplicative expression")
        fun multiplicativeExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/MultiplicativeExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/MultiplicativeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiplicative expression; compact whitespace")
        fun multiplicativeExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/MultiplicativeExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/MultiplicativeExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing UnionExpr")
        fun missingUnionExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/MultiplicativeExpr_MissingUnionExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/MultiplicativeExpr_MissingUnionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/MultiplicativeExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/MultiplicativeExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (10) UnaryExpr")
    internal inner class UnaryExpr_XPath10 {
        @Test
        @DisplayName("minus; single")
        fun minusSingle() {
            val expected = loadResource("tests/parser/xpath-1.0/UnaryExpr_Minus.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnaryExpr_Minus.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("minus; single; compact whitespace")
        fun minusSingle_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/UnaryExpr_Minus_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnaryExpr_Minus_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("minus; multiple")
        fun minusMultiple() {
            val expected = loadResource("tests/parser/xpath-1.0/UnaryExpr_Minus_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnaryExpr_Minus_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("minus; multiple; compact whitespace")
        fun minusMultiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/UnaryExpr_Minus_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnaryExpr_Minus_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: minus; missing ValueExpr")
        fun minus_missingValueExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/UnaryExpr_Minus_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnaryExpr_Minus_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (11) UnionExpr")
    internal inner class UnionExpr_XPath10 {
        @Test
        @DisplayName("union expression")
        fun unionExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/UnionExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("union expression; compact whitespace")
        fun unionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/UnionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnionExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing IntersectExceptExpr")
        fun missingIntersectExceptExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/UnionExpr_MissingIntersectExceptExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnionExpr_MissingIntersectExceptExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/UnionExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/UnionExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (12) PathExpr")
    internal inner class PathExpr {
        @Test
        @DisplayName("leading forward slash")
        fun leadingForwardSlash() {
            val expected = loadResource("tests/parser/xpath-1.0/PathExpr_LeadingForwardSlash.txt")
            val actual = parseResource("tests/parser/xpath-1.0/PathExpr_LeadingForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("leading forward slash; compact whitespace")
        fun leadingForwardSlash_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/PathExpr_LeadingForwardSlash_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("leading double forward slash")
        fun leadingDoubleForwardSlash() {
            val expected = loadResource("tests/parser/xpath-1.0/PathExpr_LeadingDoubleForwardSlash.txt")
            val actual = parseResource("tests/parser/xpath-1.0/PathExpr_LeadingDoubleForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("leading double forward slash; compact whitespace")
        fun leadingDoubleForwardSlash_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/PathExpr_LeadingDoubleForwardSlash_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("lone forward slash")
        fun loneForwardSlash() {
            val expected = loadResource("tests/parser/xpath-1.0/PathExpr_LoneForwardSlash.txt")
            val actual = parseResource("tests/parser/xpath-1.0/PathExpr_LoneForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("lone double forward slash")
        fun loneDoubleForwardSlash() {
            val expected = loadResource("tests/parser/xpath-1.0/PathExpr_LoneDoubleForwardSlash.txt")
            val actual = parseResource("tests/parser/xpath-1.0/PathExpr_LoneDoubleForwardSlash.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (13) RelativePathExpr")
    internal inner class RelativePathExpr {
        @Test
        @DisplayName("direct descendants")
        fun directDescendants() {
            val expected = loadResource("tests/parser/xpath-1.0/RelativePathExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/RelativePathExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("direct descendants; compact whitespace")
        fun directDescendants_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/RelativePathExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/RelativePathExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("all descendants")
        fun allDescendants() {
            val expected = loadResource("tests/parser/xpath-1.0/RelativePathExpr_AllDescendants.txt")
            val actual = parseResource("tests/parser/xpath-1.0/RelativePathExpr_AllDescendants.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("all descendants; compact whitespace")
        fun allDescendants_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/RelativePathExpr_AllDescendants_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing StepExpr")
        fun missingStepExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/RelativePathExpr_MissingStepExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/RelativePathExpr_MissingStepExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/RelativePathExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/RelativePathExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (16) ForwardStep ; XPath 1.0 EBNF (17) ForwardAxis")
    internal inner class ForwardAxis {
        @Nested
        @DisplayName("XPath 1.0 EBNF (22) NodeTest ; XPath 1.0 EBNF (23) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("child")
            fun child() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Child.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Child.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("child; compact whitespace")
            fun child_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Child_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Child_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant")
            fun descendant() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Descendant.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Descendant.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant; compact whitespace")
            fun descendant_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Descendant_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Descendant_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("attribute")
            fun attribute() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Attribute.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Attribute.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("attribute; compact whitespace")
            fun attribute_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Attribute_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Attribute_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("self")
            fun self() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Self.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Self.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("self; compact whitespace")
            fun self_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Self_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Self_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant-or-self")
            fun descendantOrSelf() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_DescendantOrSelf.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_DescendantOrSelf.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("descendant-or-self; compact whitespace")
            fun descendantOrSelf_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_DescendantOrSelf_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_DescendantOrSelf_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following-sibling")
            fun followingSibling() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_FollowingSibling.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_FollowingSibling.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following-sibling; compact whitespace")
            fun followingSibling_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_FollowingSibling_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_FollowingSibling_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following")
            fun following() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Following.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Following.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("following; compact whitespace")
            fun following_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Following_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Following_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("namespace")
            fun namespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Namespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Namespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("namespace; compact whitespace")
            fun namespace_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Namespace_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_NameTest_Namespace_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 1.0 EBNF (22) NodeTest ; XPath 1.0 EBNF (36) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("self")
            fun self() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_KindTest_Self.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_KindTest_Self.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("self; compact whitespace")
            fun self_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_KindTest_Self_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_KindTest_Self_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: missing NodeTest")
        fun missingNodeTest() {
            val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_MissingNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (26) PredicateList ; XPath 2.0 EBNF (27) Predicate")
        internal inner class PredicateList {
            @Test
            @DisplayName("predicate list")
            fun predicateList() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("predicate list; compact whitespace")
            fun predicateList_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_MissingClosingBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_MissingExpr.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_Multiple.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ForwardAxis_PredicateList_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (16) ForwardStep ; XPath 1.0 EBNF (18) AbbrevForwardStep")
    internal inner class AbbrevForwardStep {
        @Test
        @DisplayName("node test only")
        fun nodeTest() {
            val expected = loadResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest.txt")
            val actual = parseResource("tests/parser/xpath-1.0/NodeTest_AnyKindTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("abbreviated attribute")
        fun attribute() {
            val expected = loadResource("tests/parser/xpath-1.0/AbbrevForwardStep.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AbbrevForwardStep.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("abbreviated attribute; compact whitespace")
        fun attribute_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/AbbrevForwardStep_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AbbrevForwardStep_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: abbreviated attribute; missing NodeTest")
        fun attribute_MissingNodeTest() {
            val expected = loadResource("tests/parser/xpath-1.0/AbbrevForwardStep_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xpath-1.0/AbbrevForwardStep_MissingNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (19) ReverseStep ; XPath 1.0 EBNF (20) ReverseAxis")
    internal inner class ReverseAxis {
        @Nested
        @DisplayName("XPath 1.0 EBNF (22) NodeTest ; XPath 1.0 EBNF (23) NameTest")
        internal inner class NameTest {
            @Test
            @DisplayName("parent")
            fun parent() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Parent.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Parent.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("parent; compact whitespace")
            fun parent_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Parent_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Parent_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor")
            fun ancestor() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Ancestor.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Ancestor.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor; compact whitespace")
            fun ancestor_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Ancestor_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Ancestor_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding-sibling")
            fun precedingSibling() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_PrecedingSibling.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_PrecedingSibling.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding-sibling; compact whitespace")
            fun precedingSibling_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_PrecedingSibling_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_PrecedingSibling_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding")
            fun preceding() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Preceding.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Preceding.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("preceding; compact whitespace")
            fun preceding_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Preceding_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_Preceding_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor-or-self")
            fun ancestorOrSelf() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_AncestorOrSelf.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_AncestorOrSelf.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("ancestor-or-self; compact whitespace")
            fun ancestorOrSelf_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_AncestorOrSelf_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_NameTest_AncestorOrSelf_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 1.0 EBNF (22) NodeTest ; XPath 1.0 EBNF (36) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("parent")
            fun parent() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_KindTest_Parent.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_KindTest_Parent.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("parent; compact whitespace")
            fun parent_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_KindTest_Parent_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_KindTest_Parent_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: missing NodeTest")
        fun missingNodeTest() {
            val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_MissingNodeTest.txt")
            val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_MissingNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (26) PredicateList ; XPath 2.0 EBNF (27) Predicate")
        internal inner class PredicateList {
            @Test
            @DisplayName("predicate list")
            fun predicateList() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("predicate list; compact whitespace")
            fun predicateList_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_MissingClosingBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_MissingExpr.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_Multiple.txt")
                val actual = parseResource("tests/parser/xpath-1.0/ReverseAxis_PredicateList_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Test
    @DisplayName("XPath 1.0 EBNF (19) ReverseStep ; XPath 1.0 EBNF (21) AbbrevReverseStep")
    fun abbrevReverseStep() {
        val expected = loadResource("tests/parser/xpath-1.0/AbbrevReverseStep.txt")
        val actual = parseResource("tests/parser/xpath-1.0/AbbrevReverseStep.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
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
    @DisplayName("XPath 1.0 EBNF (25) FilterExpr ; XPath 1.0 EBNF (26) PredicateList ; XPath 1.0 EBNF (27) Predicate")
    internal inner class FilterExpr {
        @Test
        @DisplayName("predicate list")
        fun predicateList() {
            val expected = loadResource("tests/parser/xpath-1.0/FilterExpr_PredicateList.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FilterExpr_PredicateList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("predicate list; compact whitespace")
        fun predicateList_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_MissingExpr.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FilterExpr_PredicateList_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (31) VarRef ; XPath 2.0 EBNF (32) VarName")
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

    @Test
    @DisplayName("XPath 1.0 EBNF (34) ContextItemExpr")
    fun contextItemExpr() {
        val expected = loadResource("tests/parser/xpath-1.0/ContextItemExpr.txt")
        val actual = parseResource("tests/parser/xpath-1.0/ContextItemExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XPath 1.0 EBNF (35) FunctionCall ; XPath 3.0 EBNF (49) ArgumentList")
    internal inner class FunctionCall {
        @Test
        @DisplayName("keyword NCName")
        fun keywordNCName() {
            val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_NCName_Keyword.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_NCName_Keyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty argument list")
        fun argumentList_Empty() {
            val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty argument list; compact whitespace")
        fun argumentList_Empty_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening parenthesis")
        fun missingOpeningParenthesis() {
            val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty_MissingOpeningParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty_MissingOpeningParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_ArgumentList_Empty_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XPath 3.0 EBNF (60) Argument")
        internal inner class Argument {
            @Test
            @DisplayName("single argument")
            fun singleParam() {
                val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_SingleParam.txt")
                val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_SingleParam.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single argument; compact whitespace")
            fun singleParam_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_SingleParam_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_SingleParam_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument")
            fun multipleParam() {
                val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam.txt")
                val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument; compact whitespace")
            fun multipleParam_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple argument; missing ExprSingle")
            fun multipleParam_MissingExpr() {
                val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam_MissingExpr.txt")
                val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple argument; space before next comma")
            fun multipleParam_SpaceBeforeNextComma() {
                val expected = loadResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.txt")
                val actual = parseResource("tests/parser/xpath-1.0/FunctionCall_MultipleParam_SpaceBeforeNextComma.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
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
    @DisplayName("XPath 1.0 EBNF (22) NodeTest ; XPath 1.0 EBNF (23) NameTest")
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
    @DisplayName("XPath 2.0 EBNF (4) ForExpr ; XPath 2.0 EBNF (5) SimpleForClause")
    internal inner class ForExpr {
        @Nested
        @DisplayName("single SimpleForBinding")
        internal inner class SingleForBinding {
            @Test
            @DisplayName("single binding")
            fun single() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single binding; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MissingVarName.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MissingVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' keyword")
            fun missingInKeyword() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MissingInKeyword.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MissingInKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'in' ExprSingle")
            fun missingInExpr() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MissingInExpr.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MissingInExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MissingReturnKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' ExprSingle")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MissingReturnExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("multiple SimpleForBindings")
        internal inner class MultipleForBindings {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MultipleVarName.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MultipleVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MultipleVarName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MultipleVarName_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xpath-2.0/ForExpr_MultipleVarName_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ForExpr_MultipleVarName_MissingVarIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: ReturnClause only")
        fun returnClauseOnly() {
            val expected = loadResource("tests/parser/xpath-2.0/ForExpr_ReturnOnly.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ForExpr_ReturnOnly.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (6) QuantifiedExpr")
    internal inner class QuantifiedExpr {
        @Test
        @DisplayName("some, every")
        fun quantifiedExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("some, every; compact whitespace")
        fun quantifiedExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingVarName.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' keyword")
        fun missingInKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingInKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingInKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'in' ExprSingle")
        fun testQuantifiedExpr_MissingInExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingInExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingInExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'satisfies' keyword")
        fun missingSatisfiesKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingSatisfiesKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingSatisfiesKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'satisfies' ExprSingle")
        fun missingSatisfiesExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingSatisfiesExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MissingSatisfiesExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName")
        fun multipleVarName() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MultipleVarName.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MultipleVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; compact whitespace")
        fun multipleVarName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MultipleVarName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; missing variable indicator")
        fun multipleVarName_MissingVarIndicator() {
            val expected = loadResource("tests/parser/xpath-2.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.txt")
            val actual = parseResource("tests/parser/xpath-2.0/QuantifiedExpr_MultipleVarName_MissingVarIndicator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (7) IfExpr")
    internal inner class IfExpr {
        @Test
        @DisplayName("if expression")
        fun ifExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("if expression; compact whitespace")
        fun ifExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing condition Expr")
        fun missingCondExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr_MissingCondExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr_MissingCondExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'then' keyword")
        fun missingThenKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr_MissingThenKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr_MissingThenKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'then' ExprSingle")
        fun missingThenExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr_MissingThenExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr_MissingThenExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'else' keyword")
        fun missingElseKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr_MissingElseKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr_MissingElseKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'else' ExprSingle")
        fun missingElseExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/IfExpr_MissingElseExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IfExpr_MissingElseExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (10) ComparisonExpr ; XPath 2.0 EBNF (22) GeneralComp")
    internal inner class GeneralComp {
        @Test
        @DisplayName("general comparison")
        fun generalComp() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("general comparison; compact whitespace")
        fun generalComp_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp_MissingRhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing lhs RangeExpr")
        fun missingLhsRangeExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp_MissingLhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_GeneralComp_MissingLhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (10) ComparisonExpr ; XPath 2.0 EBNF (23) ValueComp")
    internal inner class ValueComp {
        @Test
        @DisplayName("value comparison")
        fun valueComp() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_ValueComp.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_ValueComp.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_ValueComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_ValueComp_MissingRhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (10) ComparisonExpr ; XPath 2.0 EBNF (24) NodeComp")
    internal inner class NodeComp {
        @Test
        @DisplayName("node comparison")
        fun nodeComp() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("node comparison; compact whitespace")
        fun nodeComp_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing rhs RangeExpr")
        fun missingRhsRangeExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp_MissingRhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp_MissingRhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing lhs RangeExpr")
        fun missingLhsRangeExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp_MissingLhsRangeExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ComparisonExpr_NodeComp_MissingLhsRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (11) RangeExpr")
    internal inner class RangeExpr {
        @Test
        @DisplayName("range expression")
        fun rangeExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/RangeExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/RangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing AdditiveExpr")
        fun missingAdditiveExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/RangeExpr_MissingAdditiveExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/RangeExpr_MissingAdditiveExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (13) MultiplicativeExpr")
    internal inner class MultiplicativeExpr_XPath20 {
        @Test
        @DisplayName("multiplicative expression")
        fun multiplicativeExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/MultiplicativeExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/MultiplicativeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing UnionExpr")
        fun missingUnionExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/MultiplicativeExpr_MissingUnionExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/MultiplicativeExpr_MissingUnionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (14) UnionExpr")
    internal inner class UnionExpr_XPath20 {
        @Test
        @DisplayName("union expression")
        fun unionExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/UnionExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing IntersectExceptExpr")
        fun missingIntersectExceptExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/UnionExpr_MissingIntersectExceptExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnionExpr_MissingIntersectExceptExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (15) IntersectExceptExpr")
    internal inner class IntersectExceptExpr {
        @Test
        @DisplayName("intersect")
        fun intersect() {
            val expected = loadResource("tests/parser/xpath-2.0/IntersectExceptExpr_Intersect.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IntersectExceptExpr_Intersect.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: intersect; missing InstanceofExpr")
        fun intersect_MissingInstanceofExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IntersectExceptExpr_Intersect_MissingInstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("except")
        fun except() {
            val expected = loadResource("tests/parser/xpath-2.0/IntersectExceptExpr_Except.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IntersectExceptExpr_Except.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: except; missing InstanceofExpr")
        fun except_MissingInstanceofExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/IntersectExceptExpr_Except_MissingInstanceofExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IntersectExceptExpr_Except_MissingInstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-2.0/IntersectExceptExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-2.0/IntersectExceptExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (16) InstanceofExpr")
    internal inner class InstanceofExpr {
        @Test
        @DisplayName("instance of expression")
        fun instanceofExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/InstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'instance' keyword")
        fun missingInstanceKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/InstanceofExpr_MissingInstanceKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/InstanceofExpr_MissingInstanceKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'of' keyword")
        fun missingOfKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/InstanceofExpr_MissingOfKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/InstanceofExpr_MissingOfKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xpath-2.0/InstanceofExpr_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xpath-2.0/InstanceofExpr_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (17) TreatExpr")
    internal inner class TreatExpr {
        @Test
        @DisplayName("treat expression")
        fun treatExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/TreatExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/TreatExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'treat' keyword")
        fun missingTreatKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr_MissingCastKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/TreatExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/TreatExpr_MissingAsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SequenceType")
        fun missingSequenceType() {
            val expected = loadResource("tests/parser/xpath-2.0/TreatExpr_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xpath-2.0/TreatExpr_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (18) CastableExpr ; XPath 2.0 EBNF (49) SingleType")
    internal inner class CastableExpr {
        @Test
        @DisplayName("castable expression")
        fun castableExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/CastableExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastableExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'castable' keyword")
        fun missingCastableKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr_MissingCastKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/CastableExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastableExpr_MissingAsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SingleType")
        fun missingSingleType() {
            val expected = loadResource("tests/parser/xpath-2.0/CastableExpr_MissingSingleType.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastableExpr_MissingSingleType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType")
        fun optionalAtomicType() {
            val expected = loadResource("tests/parser/xpath-2.0/CastableExpr_OptionalAtomicType.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastableExpr_OptionalAtomicType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType; compact whitespace")
        fun optionalAtomicType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/CastableExpr_OptionalAtomicType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastableExpr_OptionalAtomicType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (19) CastExpr ; XPath 2.0 EBNF (49) SingleType")
    internal inner class CastExpr {
        @Test
        @DisplayName("cast expression")
        fun castExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'cast' keyword")
        fun missingCastKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr_MissingCastKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr_MissingCastKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'as' keyword")
        fun missingAsKeyword() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr_MissingAsKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing SingleType")
        fun missingSingleType() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr_MissingSingleType.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr_MissingSingleType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType")
        fun optionalAtomicType() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr_OptionalAtomicType.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr_OptionalAtomicType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional AtomicType; compact whitespace")
        fun optionalAtomicType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/CastExpr_OptionalAtomicType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/CastExpr_OptionalAtomicType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (20) UnaryExpr")
    internal inner class UnaryExpr {
        @Test
        @DisplayName("plus; single")
        fun plusSingle() {
            val expected = loadResource("tests/parser/xpath-2.0/UnaryExpr_Plus.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnaryExpr_Plus.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("plus; single; compact whitespace")
        fun plusSingle_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/UnaryExpr_Plus_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnaryExpr_Plus_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("plus; multiple")
        fun plusMultiple() {
            val expected = loadResource("tests/parser/xpath-2.0/UnaryExpr_Plus_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnaryExpr_Plus_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("plus; multiple; compact whitespace")
        fun plusMultiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/UnaryExpr_Plus_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnaryExpr_Plus_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: plus; missing ValueExpr")
        fun plus_missingValueExpr() {
            val expected = loadResource("tests/parser/xpath-2.0/UnaryExpr_Plus_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnaryExpr_Plus_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed")
        fun mixed() {
            val expected = loadResource("tests/parser/xpath-2.0/UnaryExpr_Mixed.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnaryExpr_Mixed.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; compact whitespace")
        fun mixed_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-2.0/UnaryExpr_Mixed_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-2.0/UnaryExpr_Mixed_CompactWhitespace.xq")
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
            @DisplayName("XPath 2.0 EBNF (139) TypeName")
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
    @DisplayName("XPath 2.0 EBNF (35) NodeTest ; XPath 2.0 EBNF (36) NameTest")
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
    @DisplayName("XPath 2.0 EBNF (50) SequenceType")
    internal inner class SequenceType {
        @Nested
        @DisplayName("empty-sequence()")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun empty() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_Empty.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_Empty.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("empty sequence; compact whitespace")
            fun empty_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_Empty_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_Empty_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun empty_MissingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_Empty_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_Empty_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (52) ItemType")
        fun itemType() {
            val expected = loadResource("tests/parser/xpath-2.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/InstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (52) ItemType ; XPath 2.0 EBNF (51) OccurrenceIndicator")
        internal inner class OccurrenceIndicator {
            @Test
            @DisplayName("zero or one")
            fun zeroOrOne() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_ZeroOrOne.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_ZeroOrOne.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("zero or one; compact whitespace")
            fun zeroOrOne_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_ZeroOrOne_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_ZeroOrOne_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("zero or more")
            fun zeroOrMore() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_ZeroOrMore.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_ZeroOrMore.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("zero or more; compact whitespace")
            fun zeroOrMore_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_ZeroOrMore_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_ZeroOrMore_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("one or more")
            fun oneOrMore() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_OneOrMore.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_OneOrMore.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("one or more; compact whitespace")
            fun oneOrMore_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/SequenceType_OneOrMore_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/SequenceType_OneOrMore_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath 2.0 EBNF (52) ItemType")
    internal inner class ItemType {
        @Nested
        @DisplayName("item()")
        internal inner class Item {
            @Test
            @DisplayName("item")
            fun itemType() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("item; compact whitespace")
            fun itemType_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XPath 2.0 EBNF (53) AtomicType")
        fun atomicType() {
            val expected = loadResource("tests/parser/xpath-2.0/InstanceofExpr.txt")
            val actual = parseResource("tests/parser/xpath-2.0/InstanceofExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XPath 2.0 EBNF (54) KindTest")
        internal inner class KindTest {
            @Test
            @DisplayName("XPath 2.0 EBNF (55) AnyKindTest")
            fun anyKindTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_AnyKindTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_AnyKindTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (56) DocumentTest")
            fun documentTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_DocumentTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_DocumentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (57) TextTest")
            fun textTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_TextTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_TextTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (58) CommentTest")
            fun commentTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_CommentTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_CommentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (59) PITest")
            fun piTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_PITest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_PITest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (60) AttributeTest")
            fun attributeTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_AttributeTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_AttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (62) SchemaAttributeTest")
            fun schemaAttributeTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_SchemaAttributeTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_SchemaAttributeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (64) ElementTest")
            fun elementTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_ElementTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_ElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("XPath 2.0 EBNF (66) SchemaElementTest")
            fun schemaElementTest() {
                val expected = loadResource("tests/parser/xpath-2.0/ItemType_SchemaElementTest.txt")
                val actual = parseResource("tests/parser/xpath-2.0/ItemType_SchemaElementTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
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
    @DisplayName("XPath 3.0 EBNF (11) ForExpr ; XPath 3.0 EBNF (12) SimpleLetClause")
    internal inner class LetExpr {
        @Nested
        @DisplayName("single SimpleLetBinding")
        internal inner class SingleLetBinding {
            @Test
            @DisplayName("single binding")
            fun single() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single binding; compact whitespace")
            fun single_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing VarName")
            fun missingVarName() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MissingVarName.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MissingVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: '=' instead of ':='")
            fun equal() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_Equal.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_Equal.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable assignment operator")
            fun missingVarAssignOperator() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MissingVarAssignOperator.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MissingVarAssignOperator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable assignment ExprSingle")
            fun missingVarAssignExpr() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MissingVarAssignExpr.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MissingVarAssignExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' keyword")
            fun missingReturnKeyword() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MissingReturnKeyword.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MissingReturnKeyword.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing 'return' ExprSingle")
            fun missingReturnExpr() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MissingReturnExpr.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MissingReturnExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("multiple SimpleLetBindings")
        internal inner class MultipleLetBindings {
            @Test
            @DisplayName("multiple")
            fun multiple() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MultipleVarName.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MultipleVarName.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multiple_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MultipleVarName_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MultipleVarName_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing variable indicator")
            fun missingVarIndicator() {
                val expected = loadResource("tests/parser/xpath-3.0/LetExpr_MultipleVarName_MissingVarIndicator.txt")
                val actual = parseResource("tests/parser/xpath-3.0/LetExpr_MultipleVarName_MissingVarIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery: ReturnClause only")
        fun returnClauseOnly() {
            val expected = loadResource("tests/parser/xpath-2.0/ForExpr_ReturnOnly.txt")
            val actual = parseResource("tests/parser/xpath-2.0/ForExpr_ReturnOnly.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (19) StringConcatExpr")
    internal inner class StringConcatExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-3.0/StringConcatExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.0/StringConcatExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/StringConcatExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/StringConcatExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing RangeExpr")
        fun missingRangeExpr() {
            val expected = loadResource("tests/parser/xpath-3.0/StringConcatExpr_MissingRangeExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.0/StringConcatExpr_MissingRangeExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-3.0/StringConcatExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-3.0/StringConcatExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/StringConcatExpr_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/StringConcatExpr_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (34) SimpleMapExpr")
    internal inner class SimpleMapExpr {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-3.0/SimpleMapExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.0/SimpleMapExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/SimpleMapExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/SimpleMapExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing PathExpr")
        fun missingPathExpr() {
            val expected = loadResource("tests/parser/xpath-3.0/SimpleMapExpr_MissingPathExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.0/SimpleMapExpr_MissingPathExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-3.0/SimpleMapExpr_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-3.0/SimpleMapExpr_Multiple.xq")
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
    @DisplayName("XPath 3.0 EBNF (45) NodeTest ; XPath 3.0 EBNF (46) NameTest")
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

    @Nested
    @DisplayName("XPath 3.0 EBNF (48) PostfixExpr; XPath 3.0 EBNF (49) ArgumentList")
    internal inner class PostfixExpr_ArgumentList {
        @Test
        @DisplayName("argument list")
        fun argumentList() {
            val expected = loadResource("tests/parser/xpath-3.0/PostfixExpr_ArgumentList.txt")
            val actual = parseResource("tests/parser/xpath-3.0/PostfixExpr_ArgumentList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("argument list; compact whitespace")
        fun argumentList_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/PostfixExpr_ArgumentList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/PostfixExpr_ArgumentList_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed")
        fun mixed() {
            val expected = loadResource("tests/parser/xpath-3.0/PostfixExpr_Mixed.txt")
            val actual = parseResource("tests/parser/xpath-3.0/PostfixExpr_Mixed.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; compact whitespace")
        fun mixed_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/PostfixExpr_Mixed_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/PostfixExpr_Mixed_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (59) FunctionCall ; XPath 3.0 EBNF (49) ArgumentList")
    internal inner class FunctionCall_XPath30 {
        @Test
        @DisplayName("XPath 3.0 EBNF (61) ArgumentPlaceholder")
        fun argumentPlaceholder() {
            val expected = loadResource("tests/parser/xpath-3.0/ArgumentPlaceholder.txt")
            val actual = parseResource("tests/parser/xpath-3.0/ArgumentPlaceholder.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (63) NamedFunctionRef")
    internal inner class NamedFunctionRef {
        @Test
        @DisplayName("named function reference")
        fun namedFunctionRef() {
            val expected = loadResource("tests/parser/xpath-3.0/NamedFunctionRef.txt")
            val actual = parseResource("tests/parser/xpath-3.0/NamedFunctionRef.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("named function reference; compact whitespace")
        fun namedFunctionRef_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/NamedFunctionRef_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/NamedFunctionRef_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing arity")
        fun missingArity() {
            val expected = loadResource("tests/parser/xpath-3.0/NamedFunctionRef_MissingArity.txt")
            val actual = parseResource("tests/parser/xpath-3.0/NamedFunctionRef_MissingArity.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (64) InlineFunctionExpr")
    internal inner class InlineFunctionExpr {
        @Test
        @DisplayName("inline function expression")
        fun inlineFunctionExpr() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("inline function expression; compact whitespace")
        fun inlineFunctionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening brace")
        fun missingOpeningBrace() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr_MissingOpeningBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing function body")
        fun missingFunctionBody() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr_MissingFunctionBody.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr_MissingFunctionBody.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parameter list")
        fun paramList() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr_ParamList.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr_ParamList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("return type")
        fun returnType() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr_ReturnType.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr_ReturnType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("return type; missing SequenceType")
        fun returnType_MissingSequenceType() {
            val expected = loadResource("tests/parser/xpath-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/xpath-3.0/InlineFunctionExpr_ReturnType_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (90) FunctionTest")
    internal inner class FunctionTest {
        @Nested
        @DisplayName("XPath 3.0 EBNF (91) AnyFunctionTest")
        internal inner class AnyFunctionTest {
            @Test
            @DisplayName("any function test")
            fun anyFunctionTest() {
                val expected = loadResource("tests/parser/xpath-3.0/AnyFunctionTest.txt")
                val actual = parseResource("tests/parser/xpath-3.0/AnyFunctionTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any function test; compact whitespace")
            fun anyFunctionTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-3.0/AnyFunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-3.0/AnyFunctionTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-3.0/AnyFunctionTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-3.0/AnyFunctionTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: unexpected return type")
            fun unexpectedReturnType() {
                val expected = loadResource("tests/parser/xpath-3.0/AnyFunctionTest_UnexpectedReturnType.txt")
                val actual = parseResource("tests/parser/xpath-3.0/AnyFunctionTest_UnexpectedReturnType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XPath 3.0 EBNF (92) TypedFunctionTest")
        internal inner class TypedFunctionTest {
            @Test
            @DisplayName("single parameter")
            fun singleParameter() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("single parameter; compact whitespace")
            fun singleParameter_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters")
            fun multipleParameters() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_Multiple.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters; compact whitespace")
            fun multipleParameters_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_Multiple_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_Multiple_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: multiple parameters; missing SequenceType")
            fun multipleParameters_MissingSequenceType() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_Multiple_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_Multiple_MissingSequenceType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple parameters with OccurrenceIndicator")
            fun multipleParametersWithOccurenceIndicator() {
                // This is testing handling of whitespace before parsing the next comma.
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_MultipleWithOccurrenceIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing return type")
            fun missingReturnType() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_MissingReturnType.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_MissingReturnType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing SequnceType from the return type")
            fun returnType_MissingSequenceType() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_MissingReturnType_MissingSequenceType.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_MissingReturnType_MissingSequenceType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("empty parameter list")
            fun emptyParameterList() {
                val expected = loadResource("tests/parser/xpath-3.0/TypedFunctionTest_EmptyTypeList.txt")
                val actual = parseResource("tests/parser/xpath-3.0/TypedFunctionTest_EmptyTypeList.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.0 EBNF (93) ParenthesizedItemType")
    internal inner class ParenthesizedItemType {
        @Test
        @DisplayName("parenthesized item type")
        fun parenthesizedItemType() {
            val expected = loadResource("tests/parser/xpath-3.0/ParenthesizedItemType.txt")
            val actual = parseResource("tests/parser/xpath-3.0/ParenthesizedItemType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("parenthesized item type; compact whitespace")
        fun parenthesizedItemType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.0/ParenthesizedItemType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.0/ParenthesizedItemType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing item type")
            fun missingItemType() {
                val expected = loadResource("tests/parser/xpath-3.0/ParenthesizedItemType_MissingItemType.txt")
                val actual = parseResource("tests/parser/xpath-3.0/ParenthesizedItemType_MissingItemType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xpath-3.0/ParenthesizedItemType_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xpath-3.0/ParenthesizedItemType_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("error recovery; item type as sequence type")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val expected = loadResource("tests/parser/xpath-3.0/ParenthesizedItemType_EmptySequence.txt")
                val actual = parseResource("tests/parser/xpath-3.0/ParenthesizedItemType_EmptySequence.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("occurrence indicator")
            fun occurrenceIndicator() {
                val expected = loadResource("tests/parser/xpath-3.0/ParenthesizedItemType_OccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xpath-3.0/ParenthesizedItemType_OccurrenceIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (29) ArrowExpr ; XPath 3.1 EBNF (55) ArrowFunctionSpecifier")
    internal inner class ArrowExpr {
        @Test
        @DisplayName("arrow function specifier: EQName")
        fun arrowFunctionSpecifier_EQName() {
            val expected = loadResource("tests/parser/xpath-3.1/ArrowExpr_EQName.txt")
            val actual = parseResource("tests/parser/xpath-3.1/ArrowExpr_EQName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("arrow function specifier: VarRef")
        fun arrowFunctionSpecifier_VarRef() {
            val expected = loadResource("tests/parser/xpath-3.1/ArrowExpr_VarRef.txt")
            val actual = parseResource("tests/parser/xpath-3.1/ArrowExpr_VarRef.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("arrow function specifier: ParenthesizedExpr")
        fun arrowFunctionSpecifier_ParenthesizedExpr() {
            val expected = loadResource("tests/parser/xpath-3.1/ArrowExpr_ParenthesizedExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/ArrowExpr_ParenthesizedExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/ArrowExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/ArrowExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ArgumentList")
        fun missingArgumentList() {
            val expected = loadResource("tests/parser/xpath-3.1/ArrowExpr_MissingArgumentList.txt")
            val actual = parseResource("tests/parser/xpath-3.1/ArrowExpr_MissingArgumentList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing function specifier")
        fun missingFunctionSpecifier() {
            val expected = loadResource("tests/parser/xpath-3.1/ArrowExpr_MissingFunctionSpecifier.txt")
            val actual = parseResource("tests/parser/xpath-3.1/ArrowExpr_MissingFunctionSpecifier.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple arrows")
        fun multipleArrows() {
            val expected = loadResource("tests/parser/xpath-3.1/ArrowExpr_MultipleArrows.txt")
            val actual = parseResource("tests/parser/xpath-3.1/ArrowExpr_MultipleArrows.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (49) PostfixExpr ; XPath 3.1 EBNF (53) Lookup ; XPath 3.1 EBNF (54) KeySpecifier")
    internal inner class PostfixExpr_Lookup {
        @Test
        @DisplayName("key specifier; NCName")
        fun keySpecifier_ncname() {
            val expected = loadResource("tests/parser/xpath-3.1/Lookup_NCName.txt")
            val actual = parseResource("tests/parser/xpath-3.1/Lookup_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; IntegerLiteral")
        fun keySpecifier_IntegerLiteral() {
            val expected = loadResource("tests/parser/xpath-3.1/Lookup_IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xpath-3.1/Lookup_IntegerLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr")
        fun keySpecifier_ParenthesizedExpr() {
            val expected = loadResource("tests/parser/xpath-3.1/Lookup_ParenthesizedExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/Lookup_ParenthesizedExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; wildcard")
        fun keySpecifier_Wildcard() {
            val expected = loadResource("tests/parser/xpath-3.1/Lookup_Wildcard.txt")
            val actual = parseResource("tests/parser/xpath-3.1/Lookup_Wildcard.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/Lookup_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/Lookup_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing KeySpecifier")
        fun missingKeySpecifier() {
            val expected = loadResource("tests/parser/xpath-3.1/Lookup_MissingKeySpecifier.txt")
            val actual = parseResource("tests/parser/xpath-3.1/Lookup_MissingKeySpecifier.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (69) MapConstructor ; XPath 3.1 EBNF (70) MapConstructorEntry")
    internal inner class MapConstructor {
        @Test
        @DisplayName("empty")
        fun empty() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructor.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty; compact whitespace")
        fun empty_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("StringLiteral map key expression")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("StringLiteral map key expression; compact whitespace")
        fun stringLiteral_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing key-value separator (colon)")
        fun missingSeparator() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_MissingSeparator.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_MissingSeparator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing map value expression")
        fun missingValueExpr() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing map constructor entry after comma")
        fun missingEntry() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_Multiple_MissingEntry.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_Multiple_MissingEntry.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression")
        fun ncname() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_NCName.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression; whitespace after colon")
        fun ncname_WhitespaceAfterColon() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_NCName_WhitespaceAfterColon.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression; compact whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_NCName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName map key expression")
        fun qname_KeyExpr() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_QName_KeyExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_QName_KeyExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName map value expression")
        fun qname_ValueExpr() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_QName_ValueExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_QName_ValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName map key expression; compact whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_QName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_QName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("VarRef map key expression")
        fun varRef_NCName() {
            val expected = loadResource("tests/parser/xpath-3.1/MapConstructorEntry_VarRef_NCName.txt")
            val actual = parseResource("tests/parser/xpath-3.1/MapConstructorEntry_VarRef_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (73) SquareArrayConstructor")
    internal inner class SquareArrayConstructor {
        @Test
        @DisplayName("single")
        fun single() {
            val expected = loadResource("tests/parser/xpath-3.1/SquareArrayConstructor.txt")
            val actual = parseResource("tests/parser/xpath-3.1/SquareArrayConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun single_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/SquareArrayConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/SquareArrayConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty")
        fun empty() {
            val expected = loadResource("tests/parser/xpath-3.1/SquareArrayConstructor_MissingExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/SquareArrayConstructor_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/xpath-3.1/SquareArrayConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/SquareArrayConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xpath-3.1/SquareArrayConstructor_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-3.1/SquareArrayConstructor_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/SquareArrayConstructor_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; missing expression after comma")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/xpath-3.1/SquareArrayConstructor_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/SquareArrayConstructor_Multiple_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XPath 3.1 EBNF (76) UnaryLookup ; XPath 3.1 EBNF (54) KeySpecifier")
    internal inner class UnaryLookup {
        @Test
        @DisplayName("key specifier; NCName")
        fun keySpecifier_ncname() {
            val expected = loadResource("tests/parser/xpath-3.1/UnaryLookup_NCName.txt")
            val actual = parseResource("tests/parser/xpath-3.1/UnaryLookup_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; IntegerLiteral")
        fun keySpecifier_IntegerLiteral() {
            val expected = loadResource("tests/parser/xpath-3.1/UnaryLookup_IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xpath-3.1/UnaryLookup_IntegerLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; ParenthesizedExpr")
        fun keySpecifier_ParenthesizedExpr() {
            val expected = loadResource("tests/parser/xpath-3.1/UnaryLookup_ParenthesizedExpr.txt")
            val actual = parseResource("tests/parser/xpath-3.1/UnaryLookup_ParenthesizedExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("key specifier; wildcard")
        fun keySpecifier_Wildcard() {
            val expected = loadResource("tests/parser/xpath-3.1/UnaryLookup_Wildcard.txt")
            val actual = parseResource("tests/parser/xpath-3.1/UnaryLookup_Wildcard.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("compact whitespace")
        fun compactWhitespace() {
            val expected = loadResource("tests/parser/xpath-3.1/UnaryLookup_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-3.1/UnaryLookup_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing KeySpecifier")
        fun missingKeySpecifier() {
            val expected = loadResource("tests/parser/xpath-3.1/UnaryLookup_MissingKeySpecifier.txt")
            val actual = parseResource("tests/parser/xpath-3.1/UnaryLookup_MissingKeySpecifier.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
