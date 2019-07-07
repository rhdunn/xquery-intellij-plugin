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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - XQuery Parser")
private class PluginParserTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
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
            val expected = loadResource("tests/parser/saxon-9.9/AndExpr_SingleAndAlso.txt")
            val actual = parseResource("tests/parser/saxon-9.9/AndExpr_SingleAndAlso.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing UpdateExpr")
        fun missingUpdateExpr() {
            val expected = loadResource("tests/parser/saxon-9.9/AndExpr_MissingUpdateExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.9/AndExpr_MissingUpdateExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multipleAndAlso() {
            val expected = loadResource("tests/parser/saxon-9.9/AndExpr_MultipleAndAlso.txt")
            val actual = parseResource("tests/parser/saxon-9.9/AndExpr_MultipleAndAlso.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; andAlso is first")
        fun mixedAndAlsoFirst() {
            val expected = loadResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoFirst.txt")
            val actual = parseResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoFirst.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; andAlso is last")
        fun mixedAndAlsoLast() {
            val expected = loadResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9/AndExpr_Mixed_AndAlsoLast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (12) UpdateExpr")
    internal inner class UpdateExpr {
        @Test
        @DisplayName("update expression")
        fun updateExpr() {
            val expected = loadResource("tests/parser/basex-7.8/UpdateExpr.txt")
            val actual = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/basex-7.8/UpdateExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/basex-7.8/UpdateExpr_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/basex-7.8/UpdateExpr_Multiple.txt")
            val actual = parseResource("tests/parser/basex-7.8/UpdateExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("block")
        fun block() {
            val expected = loadResource("tests/parser/basex-8.5/UpdateExpr.txt")
            val actual = parseResource("tests/parser/basex-8.5/UpdateExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("block; error recovery: missing Expr")
        fun block_MissingExpr() {
            val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("block; error recovery: missing closing brace")
        fun block_MissingClosingBrace() {
            val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("block; multiple")
        fun block_Multiple() {
            val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_Multiple.txt")
            val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (14) FTFuzzyOption")
    internal inner class FTFuzzyOption {
        @Test
        @DisplayName("fuzzy")
        fun fuzzyOption() {
            val expected = loadResource("tests/parser/basex-6.1/FTFuzzyOption.txt")
            val actual = parseResource("tests/parser/basex-6.1/FTFuzzyOption.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'using' keyword")
        fun fuzzyOption_MissingUsingKeyword() {
            val expected = loadResource("tests/parser/basex-6.1/FTFuzzyOption_MissingUsingKeyword.txt")
            val actual = parseResource("tests/parser/basex-6.1/FTFuzzyOption_MissingUsingKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (16) NonDeterministicFunctionCall")
    internal inner class NonDeterministicFunctionCall {
        @Test
        @DisplayName("non-deterministic function call")
        fun nonDeterministicFunctionCall() {
            val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall.txt")
            val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("non-deterministic function call; compact whitespace")
        fun nonDeterministicFunctionCall_CompactWhitespace() {
            val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing variable indicator")
        fun missingVariableIndicator() {
            val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingVariableIndicator.txt")
            val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingVariableIndicator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing argument list")
        fun missingArgumentList() {
            val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingArgumentList.txt")
            val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingArgumentList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (17) MapConstructorEntry")
    internal inner class MapConstructorEntry {
        @Test
        @DisplayName("saxon ':=' entry")
        fun mapConstructorEntry() {
            val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry.txt")
            val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("saxon ':=' entry; compact whitespace")
        fun mapConstructorEntry_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ':='")
        fun mapConstructorEntry_MissingSeparator() {
            val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.txt")
            val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ValueExpr")
        fun mapConstructorEntry_MissingValueExpr() {
            val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("saxon ':=' entry; multiple")
        fun mapConstructorEntry_Multiple() {
            val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.txt")
            val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("saxon ':=' entry; multiple; compact whitespace")
        fun mapConstructorEntry_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing entry")
        fun mapConstructorEntry_Multiple_MissingEntry() {
            val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.txt")
            val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (19) TypeDecl")
    internal inner class TypeDecl {
        @Test
        @DisplayName("type declaration")
        fun typeDecl() {
            val expected = loadResource("tests/parser/saxon-9.8/TypeDecl.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TypeDecl.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("type declaration; compact whitespace")
        fun typeDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing QName")
        fun missingQName() {
            val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_MissingQName.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_MissingQName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing equals")
        fun missingEquals() {
            val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_MissingEquals.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_MissingEquals.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing ItemType")
        fun missingItemType() {
            val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_MissingItemType.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_MissingItemType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: ':=' instead of '='")
        fun assignEquals() {
            val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_AssignEquals.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_AssignEquals.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType")
    internal inner class UnionType {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_NCName.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName; compact whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_NCName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName; compact whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_URIQualifiedName.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_URIQualifiedName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("URIQualifiedName; compact whitespace")
        fun uriQualifiedName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_URIQualifiedName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_URIQualifiedName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun testUnionType_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing first type")
        fun testUnionType_MissingFirstType() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_MissingFirstType.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_MissingFirstType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing next type")
        fun testUnionType_MissingNextType() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_MissingNextType.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_MissingNextType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple types")
        fun testUnionType_Multiple() {
            // This is testing handling of whitespace before parsing the next comma.
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_Multiple.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("union in TypedMapTest")
        fun testUnionType_InTypedMapTest() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-6/UnionType_InTypedMapTest.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-6/UnionType_InTypedMapTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (23) TupleType")
    internal inner class TupleType {
        @Test
        @DisplayName("tuple type")
        fun tupleType() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleType.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tuple type; compact whitespace")
        fun tupleType_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleType_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleType_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("extensible tuple")
        fun extensible() {
            val expected = loadResource("tests/parser/saxon-9.9/TupleType_Extensible.txt")
            val actual = parseResource("tests/parser/saxon-9.9/TupleType_Extensible.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("extensible tuple; compact whitespace")
        fun extensible_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.9/TupleType_Extensible_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.9/TupleType_Extensible_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("extensible tuple; not last")
        fun extensible_NotLast() {
            val expected = loadResource("tests/parser/saxon-9.9/TupleType_Extensible_NotLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9/TupleType_Extensible_NotLast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (23) TupleType ; XQuery IntelliJ Plugin EBNF (24) TupleField")
    internal inner class TupleType_TupleField {
        @Test
        @DisplayName("single")
        fun testTupleField() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleField.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("single; compact whitespace")
        fun testTupleField_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleField_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun testTupleField_Multiple() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleField_Multiple.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun testTupleField_Multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleField_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("occurrence indicator in SequenceType")
        fun testTupleField_MultipleWithOccurrenceIndicator() {
            // This is testing handling of whitespace before parsing the next comma.
            val expected = loadResource("tests/parser/saxon-9.8/TupleField_MultipleWithOccurrenceIndicator.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField_MultipleWithOccurrenceIndicator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing colon")
        fun testTupleField_MissingColon() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleField_MissingColon.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField_MissingColon.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing SequenceType")
        fun testTupleField_MissingSequenceType() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleField_MissingSequenceType.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField_MissingSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("no SequenceType")
        fun testTupleField_NoSequenceType() {
            val expected = loadResource("tests/parser/saxon-9.8/TupleField_NoSequenceType.txt")
            val actual = parseResource("tests/parser/saxon-9.8/TupleField_NoSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional field name")
        fun testTupleField_OptionFieldName() {
            val expected = loadResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName.txt")
            val actual = parseResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional field name; compact whitespace")
        fun testTupleField_OptionFieldName_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("optional field name; no sequence type")
        fun testTupleField_OptionFieldName_NoSequenceType() {
            val expected = loadResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName_NoSequenceType.txt")
            val actual = parseResource("tests/parser/saxon-9.9/TupleField_OptionalFieldName_NoSequenceType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (25) ForwardAxis")
    internal inner class ForwardAxis {
        @Test
        @DisplayName("namespace")
        fun namespace() {
            val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("namespace; compact whitespace")
        fun namespace_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("property")
        fun property() {
            val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Property.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("property; compact whitespace")
        fun property_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Property_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (26) CompatibilityAnnotation")
    internal inner class CompatibilityAnnotation {
        @Test
        @DisplayName("FunctionDecl")
        fun functionDecl() {
            val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'function' keyword from FunctionDecl")
        fun functionDecl_MissingFunctionKeyword() {
            val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("VarDecl")
        fun varDecl() {
            val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'variable' keyword from VarDecl")
        fun varDecl_MissingVariableKeyword() {
            val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (27) ValidateExpr")
    internal inner class ValidateExpr {
        @Test
        @DisplayName("validate as")
        fun validateAs() {
            val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("validate as; compact whitespace")
        fun validateAs_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing TypeName")
        fun missingTypeName() {
            val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingTypeName.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingTypeName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing opening brace")
        fun missingOpeningBrace() {
            val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingOpeningBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("EQName")
        fun eqname() {
            val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_EQName.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_EQName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (30) BinaryConstructor")
    internal inner class BinaryConstructor {
        @Test
        @DisplayName("binary constructor")
        fun binaryConstructor() {
            val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("binary constructor; compact whitespace")
        fun binaryConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("no Expr")
        fun noExpr() {
            val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor_NoExpr.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor_NoExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (31) CatchClause")
    internal inner class CatchClause {
        @Test
        @DisplayName("catch clause")
        fun catchClause() {
            val expected = loadResource("tests/parser/marklogic-6.0/CatchClause.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("catch clause; compact whitespace")
        fun catchClause_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing variable indicator")
        fun missingVariableIndicator() {
            val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingVariableIndicator.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingVariableIndicator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing VarName")
        fun missingVarName() {
            val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingVarName.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingVarName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing parenthesis")
        fun missingClosingParenthesis() {
            val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty Expr")
        fun emptyExpr() {
            val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_Multiple.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (32) Import ; XQuery IntelliJ Plugin EBNF (33) StylesheetImport")
    internal inner class StylesheetImport {
        @Test
        @DisplayName("stylesheet import")
        fun stylesheetImport() {
            val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("stylesheet import; compact whitespace")
        fun stylesheetImport_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing 'at' keyword")
        fun missingAtKeyword() {
            val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_MissingAtKeyword.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_MissingAtKeyword.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing URILiteral")
        fun missingUriLiteral() {
            val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_MissingUriLiteral.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_MissingUriLiteral.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (34) Module ; XQuery IntelliJ Plugin EBNF (35) TransactionSeparator")
    internal inner class TransactionSeparator {
        @Test
        @DisplayName("with VersionDecl")
        fun withVersionDecl() {
            val expected = loadResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("library module")
        fun libraryModule() {
            val expected = loadResource("tests/parser/marklogic-6.0/Transactions_LibraryModule.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/Transactions_LibraryModule.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("prolog; import statement")
        fun prolog_ImportStatement() {
            val expected = loadResource("tests/parser/marklogic-6.0/Transactions_Prolog_ImportStatement.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/Transactions_Prolog_ImportStatement.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("prolog; declare statement")
        fun prolog_DeclareStatement() {
            val expected = loadResource("tests/parser/marklogic-6.0/Transactions_Prolog_DeclareStatement.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/Transactions_Prolog_DeclareStatement.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("after ApplyExpr")
        fun afterApplyExpr() {
            val expected = loadResource("tests/parser/marklogic-6.0/Transactions_AfterApplyExpr.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/Transactions_AfterApplyExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (118) NodeTest ; XQuery IntelliJ Plugin EBNF (28) KindTest")
    internal inner class NodeTest_KindTest {
        @Test
        @DisplayName("binary test")
        fun binaryTest() {
            val expected = loadResource("tests/parser/marklogic-6.0/NodeTest_BinaryTest.txt")
            val actual = parseResource("tests/parser/marklogic-6.0/NodeTest_BinaryTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
        internal inner class AnyBooleanNodeTest {
            @Test
            @DisplayName("any boolean-node test")
            fun anyBooleanNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("FunctionCall like")
            fun functionCallLike() {
                val expected =
                    loadResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest_FunctionCallLike.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest_FunctionCallLike.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
        fun namedBooleanNodeTest() {
            val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedBooleanNodeTest.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedBooleanNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
        internal inner class AnyNumberNodeTest {
            @Test
            @DisplayName("any number-node test")
            fun anyNumberNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("FunctionCall like")
            fun functionCallLike() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest_FunctionCallLike.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest_FunctionCallLike.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
        fun namedNumberNodeTest() {
            val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedNumberNodeTest.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedNumberNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
        internal inner class AnyNullNodeTest {
            @Test
            @DisplayName("any null-node test")
            fun anyNullNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyNullNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyNullNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("FunctionCall like")
            fun functionCallLike() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyNullNodeTest_FunctionCallLike.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyNullNodeTest_FunctionCallLike.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
        fun namedNullNodeTest() {
            val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedNullNodeTest.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedNullNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
        internal inner class AnyArrayNodeTest {
            @Test
            @DisplayName("any array-node test")
            fun anyArrayNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("FunctionCall like")
            fun functionCallLike() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
        fun namedArrayNodeTest() {
            val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedArrayNodeTest.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedArrayNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
        internal inner class AnyMapNodeTest {
            @Test
            @DisplayName("any object-node test")
            fun anyMapNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("FunctionCall like")
            fun functionCallLike() {
                val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest_FunctionCallLike.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest_FunctionCallLike.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
        fun namedMapNodeTest() {
            val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedMapNodeTest.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedMapNodeTest.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (186) ItemType ; XQuery IntelliJ Plugin EBNF (28) KindTest")
    internal inner class ItemType_KindTest {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (29) BinaryTest")
        internal inner class BinaryTest {
            @Test
            @DisplayName("binary test")
            fun binaryTest() {
                val expected = loadResource("tests/parser/marklogic-6.0/BinaryTest.txt")
                val actual = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("binary test; compact whitespace")
            fun binaryTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-6.0/BinaryTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-6.0/BinaryTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-6.0/BinaryTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-6.0/BinaryTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (37) AttributeDeclTest")
        internal inner class AttributeDeclTest {
            @Test
            @DisplayName("attribute declaration test")
            fun attributeDeclTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/AttributeDeclTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("attribute declaration test; compact whitespace")
            fun attributeDeclTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/AttributeDeclTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-7.0/AttributeDeclTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (38) ComplexTypeTest")
        internal inner class ComplexTypeTest {
            @Test
            @DisplayName("complex type test")
            fun complexTypeTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/ComplexTypeTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("complex type test; compact whitespace")
            fun complexTypeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/ComplexTypeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-7.0/ComplexTypeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (39) ElementDeclTest")
        internal inner class ElementDeclTest {
            @Test
            @DisplayName("element declaration test")
            fun elementDeclTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/ElementDeclTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/ElementDeclTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("element declaration test; compact whitespace")
            fun elementDeclTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/ElementDeclTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/ElementDeclTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-7.0/ElementDeclTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/ElementDeclTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (40) SchemaComponentTest")
        internal inner class SchemaComponentTest {
            @Test
            @DisplayName("schema component test")
            fun schemaComponentTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaComponentTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema component test; compact whitespace")
            fun schemaComponentTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaComponentTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected =
                    loadResource("tests/parser/marklogic-7.0/SchemaComponentTest_MissingClosingParenthesis.txt")
                val actual =
                    parseResource("tests/parser/marklogic-7.0/SchemaComponentTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (41) SchemaParticleTest")
        internal inner class SchemaParticleTest {
            @Test
            @DisplayName("schema particle test")
            fun schemaParticleTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaParticleTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema particle test; compact whitespace")
            fun schemaParticleTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaParticleTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected =
                    loadResource("tests/parser/marklogic-7.0/SchemaParticleTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (42) SchemaRootTest")
        internal inner class SchemaRootTest {
            @Test
            @DisplayName("schema root test")
            fun schemaRootTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaRootTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema root test; compact whitespace")
            fun schemaRootTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaRootTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaRootTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaRootTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaRootTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (43) SchemaTypeTest")
        internal inner class SchemaTypeTest {
            @Test
            @DisplayName("schema type test")
            fun schemaTypeTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaTypeTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema type test; compact whitespace")
            fun schemaTypeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaTypeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-7.0/SchemaTypeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (44) SimpleTypeTest")
        internal inner class SimpleTypeTest {
            @Test
            @DisplayName("simple type test")
            fun simpleTypeTest() {
                val expected = loadResource("tests/parser/marklogic-7.0/SimpleTypeTest.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("simple type test; compact whitespace")
            fun simpleTypeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-7.0/SimpleTypeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-7.0/SimpleTypeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (45) SchemaFacetTest")
        internal inner class SchemaFacetTest {
            @Test
            @DisplayName("schema facet test")
            fun schemaFacetTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/SchemaFacetTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("schema facet test; compact whitespace")
            fun schemaFacetTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/SchemaFacetTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-8.0/SchemaFacetTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (48) AnyBooleanNodeTest")
        internal inner class AnyBooleanNodeTest {
            @Test
            @DisplayName("any boolean-node test")
            fun anyBooleanNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any boolean-node test; compact whitespace")
            fun anyBooleanNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected =
                    loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard; compact whitespace")
            fun wildcard_CompactWhitespace() {
                val expected =
                    loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard_CompactWhitespace.txt")
                val actual =
                    parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (49) NamedBooleanNodeTest")
        internal inner class NamedBooleanNodeTest {
            @Test
            @DisplayName("named boolean-node test")
            fun namedBooleanNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("named boolean-node test; compact whitespace")
            fun namedBooleanNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (52) AnyNumberNodeTest")
        internal inner class AnyNumberNodeTest {
            @Test
            @DisplayName("any number-node test")
            fun anyNumberNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any number-node test; compact whitespace")
            fun anyNumberNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected =
                    loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard; compact whitespace")
            fun wildcard_CompactWhitespace() {
                val expected =
                    loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (53) NamedNumberNodeTest")
        internal inner class NamedNumberNodeTest {
            @Test
            @DisplayName("named number-node test")
            fun namedNumberNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedNumberNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedNumberNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("named number-node test; compact whitespace")
            fun namedNumberNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedNumberNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedNumberNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (56) AnyNullNodeTest")
        internal inner class AnyNullNodeTest {
            @Test
            @DisplayName("any null-node test")
            fun anyNullNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any null-node test; compact whitespace")
            fun anyNullNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard; compact whitespace")
            fun wildcard_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (57) NamedNullNodeTest")
        internal inner class NamedNullNodeTest {
            @Test
            @DisplayName("named null-node test")
            fun namedNullNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedNullNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedNullNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("named null-node test; compact whitespace")
            fun namedNullNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedNullNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedNullNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (60) AnyArrayNodeTest")
        internal inner class AnyArrayNodeTest {
            @Test
            @DisplayName("any array-node test")
            fun anyArrayNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any array-node test; compact whitespace")
            fun anyArrayNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard; compact whitespace")
            fun wildcard_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (61) NamedArrayNodeTest")
        internal inner class NamedArrayNodeTest {
            @Test
            @DisplayName("named array-node test")
            fun namedArrayNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedArrayNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedArrayNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("named array-node test; compact whitespace")
            fun namedArrayNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedArrayNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedArrayNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (64) AnyMapNodeTest")
        internal inner class AnyMapNodeTest {
            @Test
            @DisplayName("any object-node test")
            fun anyMapNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("any object-node test; compact whitespace")
            fun anyMapNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard; compact whitespace")
            fun wildcard_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (65) NamedMapNodeTest")
        internal inner class NamedMapNodeTest {
            @Test
            @DisplayName("named object-node test")
            fun namedMapNodeTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedMapNodeTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedMapNodeTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("named object-node test; compact whitespace")
            fun namedMapNodeTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedMapNodeTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedMapNodeTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (67) AnyKindTest")
        internal inner class AnyKindTest {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard; compact whitespace")
            fun wildcard_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (68) NamedKindTest")
        internal inner class NamedKindTest {
            @Test
            @DisplayName("named kind test")
            fun namedKindTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedKindTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedKindTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("named kind test; compact whitespace")
            fun namedKindTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedKindTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedKindTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (70) AnyTextTest")
        internal inner class AnyTextTest {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("wildcard; compact whitespace")
            fun wildcard_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (71) NamedTextTest")
        internal inner class NamedTextTest {
            @Test
            @DisplayName("named text test")
            fun namedTextTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedTextTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedTextTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("named text test")
            fun namedTextTest_CompactWhitespace() {
                val expected = loadResource("tests/parser/marklogic-8.0/NamedTextTest_CompactWhitespace.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/NamedTextTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (72) DocumentTest")
        internal inner class DocumentTest {
            @Test
            @DisplayName("array-node test")
            fun anyArrayTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: wildcard in array-node test")
            fun anyArrayTest_Wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: named array-node test")
            fun namedArrayTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_NamedArrayTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_NamedArrayTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("object-node test")
            fun anyMapTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyMapTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_AnyMapTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: wildcard in object-node test")
            fun anyMapTest_Wildcard() {
                val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyMapTest_Wildcard.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_MapTest_Wildcard.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("error recovery: named object-node test")
            fun namedMapTest() {
                val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_NamedMapTest.txt")
                val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_NamedMapTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (50) BooleanConstructor")
    internal inner class BooleanConstructor {
        @Test
        @DisplayName("boolean constructor")
        fun booleanConstructor() {
            val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("boolean constructor; compact whitespace")
        fun booleanConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingExpr.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (54) NumberConstructor")
    internal inner class NumberConstructor {
        @Test
        @DisplayName("number constructor")
        fun numberConstructor() {
            val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("number constructor; compact whitespace")
        fun numberConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_MissingExpr.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (58) NullConstructor")
    internal inner class NullConstructor {
        @Test
        @DisplayName("null constructor")
        fun nullConstructor() {
            val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("null constructor; compact whitespace")
        fun nullConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (62) CurlyArrayConstructor")
    internal inner class CurlyArrayConstructor {
        @Test
        @DisplayName("array constructor")
        fun arrayConstructor() {
            val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("array constructor; compact whitespace")
        fun arrayConstructor_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingExpr.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple values")
        fun multiple() {
            val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple values; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing Expr; multiple values")
        fun multiple_MissingExpr() {
            val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_MissingExpr.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (66) MapConstructor ; XQuery 3.1 EBNF (171) MapConstructorEntry")
    internal inner class MapConstructor {
        @Test
        @DisplayName("empty")
        fun empty() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty; compact whitespace")
        fun empty_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("StringLiteral map key expression")
        fun stringLiteral() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("StringLiteral map key expression; compact whitespace")
        fun stringLiteral_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing key-value separator (colon)")
        fun missingSeparator() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingSeparator.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingSeparator.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing map value expression")
        fun missingValueExpr() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingValueExpr.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("error recovery: missing map constructor entry after comma")
        fun missingEntry() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_MissingEntry.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_MissingEntry.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression")
        fun ncname() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression; whitespace after colon")
        fun ncname_WhitespaceAfterColon() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_WhitespaceAfterColon.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_WhitespaceAfterColon.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName map key expression; compact whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName map key expression")
        fun qname_KeyExpr() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_KeyExpr.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_KeyExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName map value expression")
        fun qname_ValueExpr() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_ValueExpr.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_ValueExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName map key expression; compact whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("VarRef map key expression")
        fun varRef_NCName() {
            val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_VarRef_NCName.txt")
            val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_VarRef_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (78) SequenceType")
    internal inner class SequenceType {
        @Test
        @DisplayName("empty sequence")
        fun testSequenceType_Empty() {
            val expected = loadResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.txt")
            val actual = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty sequence; compact whitespace")
        fun testSequenceType_Empty_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("empty sequence; missing closing parenthesis")
        fun testSequenceType_Empty_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-1.0-20030502/SequenceType_Empty_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (79) OrExpr")
    internal inner class OrExpr {
        @Test
        @DisplayName("single")
        fun singleOrElse() {
            val expected = loadResource("tests/parser/saxon-9.9/OrExpr_SingleOrElse.txt")
            val actual = parseResource("tests/parser/saxon-9.9/OrExpr_SingleOrElse.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing AndExpr")
        fun missingAndExpr() {
            val expected = loadResource("tests/parser/saxon-9.9/OrExpr_MissingAndExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.9/OrExpr_MissingAndExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multipleOrElse() {
            val expected = loadResource("tests/parser/saxon-9.9/OrExpr_MultipleOrElse.txt")
            val actual = parseResource("tests/parser/saxon-9.9/OrExpr_MultipleOrElse.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; orElse is first")
        fun mixedOrElseFirst() {
            val expected = loadResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseFirst.txt")
            val actual = parseResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseFirst.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("mixed; orElse is last")
        fun mixedOrElseLast() {
            val expected = loadResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseLast.txt")
            val actual = parseResource("tests/parser/saxon-9.9/OrExpr_Mixed_OrElseLast.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (81) SimpleInlineFunctionExpr")
    internal inner class SimpleInlineFunctionExpr {
        @Test
        @DisplayName("simple inline function expression")
        fun simpleInlineFunctionExpr() {
            val expected = loadResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("simple inline function expression; compact whitespace")
        fun simpleInlineFunctionExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing Expr")
        fun missingExpr() {
            val expected = loadResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr_MissingExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing closing brace")
        fun missingClosingBrace() {
            val expected = loadResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/SimpleInlineFunctionExpr_MissingClosingBrace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) SequenceTypeUnion")
    internal inner class SequenceTypeUnion {
        @Test
        @DisplayName("sequence type union")
        fun sequenceTypeUnion() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("item type union; compact whitespace")
        fun sequenceTypeUnion_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing item type")
            fun missingItemType() {
                val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_MissingItemType.txt")
                val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_MissingItemType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("mixed with sequence type list")
        fun mixedWithSequenceTypeList() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_MixedWithSequenceTypeList.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_MixedWithSequenceTypeList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("sequence type")
        internal inner class ItemTypeAsSequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_EmptySequence.txt")
                val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_EmptySequence.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("occurrence indicator")
            fun occurrenceIndicator() {
                val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_OccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeUnion_OccurrenceIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (87) SequenceTypeList")
    internal inner class SequenceTypeList {
        @Test
        @DisplayName("tuple sequence type")
        fun tupleSequenceType() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("tuple sequence type; compact whitespace")
        fun tupleSequenceType_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing item type")
            fun missingItemType() {
                val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MissingItemType.txt")
                val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MissingItemType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected =
                    loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MissingClosingParenthesis.txt")
                val actual =
                    parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("mixed with sequence type union")
        fun mixedWithSequenceTypeUnion() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MixedWithSequenceTypeUnion.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MixedWithSequenceTypeUnion.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("sequence type")
        internal inner class ItemTypeAsSequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_EmptySequence.txt")
                val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_EmptySequence.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("occurrence indicator")
            fun occurrenceIndicator() {
                val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_OccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_OccurrenceIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (92) TernaryIfExpr")
    internal inner class TernaryIfExpr {
        @Test
        @DisplayName("ternary if")
        fun ternaryIf() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("ternary if; compact whitespace")
        fun ternaryIf_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing then Expr")
            fun missingThenExpr() {
                val expected = loadResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_MissingThenExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_MissingThenExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing else operator")
            fun missingElseOperator() {
                val expected = loadResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_MissingElseOperator.txt")
                val actual = parseResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_MissingElseOperator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing else Expr")
            fun missingElseExpr() {
                val expected = loadResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_MissingElseExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/proposal-2/TernaryIfExpr_MissingElseExpr.xq")
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
            val expected = loadResource("tests/parser/xpath-ng/proposal-2/ElvisExpr.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-2/ElvisExpr.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("elvis; compact whitespace")
        fun elvis_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-2/ElvisExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-2/ElvisExpr_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing else Expr")
            fun missingElseExpr() {
                val expected = loadResource("tests/parser/xpath-ng/proposal-2/ElvisExpr_MissingElseExpr.txt")
                val actual = parseResource("tests/parser/xpath-ng/proposal-2/ElvisExpr_MissingElseExpr.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (94) IfExpr")
    internal inner class IfExpr {
        @Test
        @DisplayName("if without else")
        fun ifWithoutElse() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-7/IfExpr_WithoutElse.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-7/IfExpr_WithoutElse.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("if without else; compact whitespace")
        fun ifWithoutElse_CompactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-7/IfExpr_WithoutElse_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-7/IfExpr_WithoutElse_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("if without else; nested")
        fun ifWithoutElse_Nested() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-7/IfExpr_WithoutElse_Nested.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-7/IfExpr_WithoutElse_Nested.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (95) ParamList")
    internal inner class ParamList {
        @Test
        @DisplayName("untyped")
        fun untyped() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Untyped.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Untyped.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("untyped; compact whitespace")
        fun untyped_compactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Untyped_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Untyped_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("typed")
        fun typed() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Typed.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Typed.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("typed; compact whitespace")
        fun typed_compactWhitespace() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Typed_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Typed_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple Params; on the last parameter")
        fun multipleParams() {
            val expected = loadResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Multiple_LastParam.txt")
            val actual = parseResource("tests/parser/xpath-ng/proposal-1/ParamList_Variadic_Multiple_LastParam.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }
    }
}
