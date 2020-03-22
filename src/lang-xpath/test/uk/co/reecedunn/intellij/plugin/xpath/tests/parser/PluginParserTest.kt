/*
 * Copyright (C) 2017-2020 Reece H. Dunn
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
        val file = ResourceVirtualFile.create(this::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    fun loadResource(resource: String): String? {
        return ResourceVirtualFile.create(this::class.java.classLoader, resource).decode()
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (20) AndExpr")
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
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (16) UnionType ; XPath 3.1 EBNF (52) ItemType")
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
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (16) UnionType ; XPath 3.1 EBNF (77) SingleType")
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
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (16) UnionType ; XPath 3.1 EBNF (107) TypedMapTest")
    fun unionType_typedMapTest() {
        val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InTypedMapTest.txt")
        val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-6/UnionType_InTypedMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (25) TupleType")
    internal inner class TupleType {
        @Test
        @DisplayName("tuple type")
        fun tupleType() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleType.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tuple type; compact whitespace")
        fun tupleType_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleType_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleType_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("extensible tuple")
        fun extensible() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("extensible tuple; compact whitespace")
        fun extensible_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("extensible tuple; not last")
        fun extensible_NotLast() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_NotLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleType_Extensible_NotLast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (25) TupleType ; XQuery IntelliJ Plugin XPath EBNF (26) TupleField")
    internal inner class TupleType_TupleField {
        @Test
        @DisplayName("single")
        fun testTupleField() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun testTupleField_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun testTupleField_Multiple() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun testTupleField_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("occurrence indicator in SequenceType")
        fun testTupleField_MultipleWithOccurrenceIndicator() {
            // This is testing handling of whitespace before parsing the next comma.
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_MultipleWithOccurrenceIndicator.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_MultipleWithOccurrenceIndicator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing colon")
        fun testTupleField_MissingColon() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_MissingColon.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_MissingColon.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing SequenceType")
        fun testTupleField_MissingSequenceType() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("no SequenceType")
        fun testTupleField_NoSequenceType() {
            val expected = loadResource("tests/parser/saxon-9.8-xpath/TupleField_NoSequenceType.txt")
            val actual = parseResource("tests/parser/saxon-9.8-xpath/TupleField_NoSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional field name")
        fun testTupleField_OptionFieldName() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional field name; compact whitespace")
        fun testTupleField_OptionFieldName_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional field name; no sequence type")
        fun testTupleField_OptionFieldName_NoSequenceType() {
            val expected = loadResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_NoSequenceType.txt")
            val actual = parseResource("tests/parser/saxon-9.9-xpath/TupleField_OptionalFieldName_NoSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (19) OrExpr")
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
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (24) ContextItemFunctionExpr")
    internal inner class ContextItemFunctionExpr {
        @Nested
        @DisplayName("simple inline function")
        internal inner class SimpleInlineFunction {
            @Test
            @DisplayName("simple inline function expression")
            fun simpleInlineFunctionExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("simple inline function expression; compact whitespace")
            fun simpleInlineFunctionExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/SimpleInlineFunctionExpr_MissingClosingBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("context inline function")
        internal inner class ContextInlineFunction {
            @Test
            @DisplayName("context item function expression")
            fun contextItemFunctionExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("context item function expression; compact whitespace")
            fun contextItemFunctionExpr_CompactWhitespace() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing Expr")
            fun missingExpr() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_MissingExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_MissingExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing closing brace")
            fun missingClosingBrace() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_MissingClosingBrace.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_MissingClosingBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("space between dot and brace")
            fun spaceBetweenDotAndBrace() {
                val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_SpaceBetweenDotAndBrace.txt")
                val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-5/ContextItemFunctionExpr_SpaceBetweenDotAndBrace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (10) TernaryIfExpr")
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
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (11) ElvisExpr")
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

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (21) IfExpr")
    internal inner class IfExpr {
        @Test
        @DisplayName("if without else")
        fun ifWithoutElse() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-7/IfExpr_WithoutElse.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-7/IfExpr_WithoutElse.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("if without else; compact whitespace")
        fun ifWithoutElse_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-7/IfExpr_WithoutElse_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-7/IfExpr_WithoutElse_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("if without else; nested")
        fun ifWithoutElse_Nested() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-7/IfExpr_WithoutElse_Nested.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-7/IfExpr_WithoutElse_Nested.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (22) ParamList")
    internal inner class ParamList {
        @Test
        @DisplayName("untyped")
        fun untyped() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-1/ParamList_Variadic_Untyped.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-1/ParamList_Variadic_Untyped.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("untyped; compact whitespace")
        fun untyped_compactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-1/ParamList_Variadic_Untyped_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-1/ParamList_Variadic_Untyped_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple Params; on the last parameter")
        fun multipleParams() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-1/ParamList_Variadic_Multiple_LastParam.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-1/ParamList_Variadic_Multiple_LastParam.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (29) ElementNameOrWildcard")
    internal inner class ElementNameOrWildcard {
        @Test
        @DisplayName("wildcard prefix")
        fun prefix() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-13/ElementNameOrWildcard_WildcardPrefix.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-13/ElementNameOrWildcard_WildcardPrefix.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("wildcard local name")
        fun localName() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-13/ElementNameOrWildcard_WildcardLocalName.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-13/ElementNameOrWildcard_WildcardLocalName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("wildcard URIQualifiedName")
        fun uriQualifiedName() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-13/ElementNameOrWildcard_WildcardURIQualifiedName.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-13/ElementNameOrWildcard_WildcardURIQualifiedName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath EBNF (30) AttribNameOrWildcard")
    internal inner class AttribNameOrWildcard {
        @Test
        @DisplayName("wildcard prefix")
        fun prefix() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-13/AttribNameOrWildcard_WildcardPrefix.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-13/AttribNameOrWildcard_WildcardPrefix.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("wildcard local name")
        fun localName() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-13/AttribNameOrWildcard_WildcardLocalName.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-13/AttribNameOrWildcard_WildcardLocalName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("wildcard URIQualifiedName")
        fun uriQualifiedName() {
            val expected = loadResource("tests/parser/xpath-ng/xpath/proposal-13/AttribNameOrWildcard_WildcardURIQualifiedName.txt")
            val actual = parseResource("tests/parser/xpath-ng/xpath/proposal-13/AttribNameOrWildcard_WildcardURIQualifiedName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
