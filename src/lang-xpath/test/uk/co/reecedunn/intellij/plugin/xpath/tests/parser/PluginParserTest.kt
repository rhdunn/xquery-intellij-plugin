/*
 * Copyright (C) 2017-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPath

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName", "Reformat")
@DisplayName("XQuery IntelliJ Plugin - XPath Parser")
private class PluginParserTest : ParserTestCase() {
    fun parseResource(resource: String): XPath {
        val file = ResourceVirtualFile(PluginParserTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile(PluginParserTest::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (11) AndExpr")
    internal inner class AndExpr {
        @Test
        @DisplayName("single")
        fun singleAndAlso() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_SingleAndAlso.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_SingleAndAlso.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing ComparisonExpr")
        fun missingUpdateExpr() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_MissingComparisonExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_MissingComparisonExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multipleAndAlso() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_MultipleAndAlso.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_MultipleAndAlso.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; andAlso is first")
        fun mixedAndAlsoFirst() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoFirst.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoFirst.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; andAlso is last")
        fun mixedAndAlsoLast() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/AndExpr_Mixed_AndAlsoLast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType ; XPath 3.1 EBNF (52) ItemType")
    internal inner class UnionType_ItemType {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_NCName.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName; compact whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_NCName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName; compact whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_URIQualifiedName.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_URIQualifiedName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("URIQualifiedName; compact whitespace")
        fun uriQualifiedName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_URIQualifiedName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_URIQualifiedName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun testUnionType_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing first type")
        fun testUnionType_MissingFirstType() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_MissingFirstType.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_MissingFirstType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing next type")
        fun testUnionType_MissingNextType() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_MissingNextType.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_MissingNextType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple types")
        fun testUnionType_Multiple() {
            // This is testing handling of whitespace before parsing the next comma.
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType ; XPath 3.1 EBNF (77) SingleType")
    internal inner class UnionType_SingleType {
        @Test
        @DisplayName("single type")
        fun singleType() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InSingleType.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InSingleType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional")
        fun optional() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InSingleType_Optional.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InSingleType_Optional.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Test
    @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType ; XPath 3.1 EBNF (107) TypedMapTest")
    fun unionType_typedMapTest() {
        val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InTypedMapTest.txt")
        val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InTypedMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (79) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("single")
        fun singleOrElse() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_SingleOrElse.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_SingleOrElse.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing AndExpr")
        fun missingAndExpr() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_MissingAndExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_MissingAndExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multipleOrElse() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_MultipleOrElse.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_MultipleOrElse.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; orElse is first")
        fun mixedOrElseFirst() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseFirst.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseFirst.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; orElse is last")
        fun mixedOrElseLast() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/OrExpr_Mixed_OrElseLast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (92) TernaryIfExpr")
    internal inner class TernaryIfExpr {
        @Test
        @DisplayName("ternary if")
        fun ternaryIf() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("ternary if; compact whitespace")
        fun ternaryIf_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing then Expr")
            fun missingThenExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingThenExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingThenExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing else operator")
            fun missingElseOperator() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseOperator.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseOperator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing else Expr")
            fun missingElseExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/TernaryIfExpr_MissingElseExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (93) ElvisExpr")
    internal inner class ElvisExpr {
        @Test
        @DisplayName("elvis")
        fun elvis() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("elvis; compact whitespace")
        fun elvis_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing else Expr")
            fun missingElseExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_MissingElseExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-2/ElvisExpr_MissingElseExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }
}
