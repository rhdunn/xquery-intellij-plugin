/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class PluginParserTest : ParserTestCase() {
    // region BaseX 6.1 :: FTFuzzyOption

    @Test
    fun testFTFuzzyOption() {
        val expected = loadResource("tests/parser/basex-6.1/FTFuzzyOption.txt")
        val actual = parseResource("tests/parser/basex-6.1/FTFuzzyOption.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testFTFuzzyOption_MissingUsingKeyword() {
        val expected = loadResource("tests/parser/basex-6.1/FTFuzzyOption_MissingUsingKeyword.txt")
        val actual = parseResource("tests/parser/basex-6.1/FTFuzzyOption_MissingUsingKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region BaseX 7.8 :: UpdateExpr

    @Test
    fun testUpdateExpr() {
        val expected = loadResource("tests/parser/basex-7.8/UpdateExpr.txt")
        val actual = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_MissingExpr() {
        val expected = loadResource("tests/parser/basex-7.8/UpdateExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/basex-7.8/UpdateExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Multiple() {
        val expected = loadResource("tests/parser/basex-7.8/UpdateExpr_Multiple.txt")
        val actual = parseResource("tests/parser/basex-7.8/UpdateExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region BaseX 8.4 :: NonDeterministicFunctionCall

    @Test
    fun testNonDeterministicFunctionCall() {
        val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall.txt")
        val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNonDeterministicFunctionCall_CompactWhitespace() {
        val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNonDeterministicFunctionCall_MissingVariableIndicator() {
        val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingVariableIndicator.txt")
        val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingVariableIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNonDeterministicFunctionCall_MissingArgumentList() {
        val expected = loadResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingArgumentList.txt")
        val actual = parseResource("tests/parser/basex-8.4/NonDeterministicFunctionCall_MissingArgumentList.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region BaseX 8.5 :: UpdateExpr

    @Test
    fun testUpdateExpr_Block() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Block_MissingExpr() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Block_MissingClosingBrace() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUpdateExpr_Block_Multiple() {
        val expected = loadResource("tests/parser/basex-8.5/UpdateExpr_Multiple.txt")
        val actual = parseResource("tests/parser/basex-8.5/UpdateExpr_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.4 :: MapConstructorEntry + MapConstructor

    @Test
    fun testMapConstructorEntry() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_MissingSeparator() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_MissingValueExpr() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructorEntry_Multiple_MissingEntry() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.8 :: TupleType

    @Test
    fun testTupleType() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleType_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleType_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleType_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleType_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleType_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleType_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.8 :: TupleType :: TupleField

    @Test
    fun testTupleField() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_Multiple() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_Multiple.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_MultipleWithOccurrenceIndicator() {
        // This is testing handling of whitespace before parsing the next comma.
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_MultipleWithOccurrenceIndicator.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_MultipleWithOccurrenceIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_MissingColon() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_MissingColon.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_MissingColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTupleField_MissingSequenceType() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_MissingSequenceType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_MissingSequenceType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.8 :: TypeDecl

    @Test
    fun testTypeDecl() {
        val expected = loadResource("tests/parser/saxon-9.8/TypeDecl.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TypeDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeDecl_MissingQName() {
        val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_MissingQName.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_MissingQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeDecl_MissingEquals() {
        val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_MissingEquals.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_MissingEquals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeDecl_MissingItemType() {
        val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_MissingItemType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_MissingItemType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTypeDecl_AssignEquals() {
        val expected = loadResource("tests/parser/saxon-9.8/TypeDecl_AssignEquals.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TypeDecl_AssignEquals.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.8 :: UnionType

    @Test
    fun testUnionType() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_MissingFirstType() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_MissingNextType() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingNextType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingNextType.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_Multiple() {
        // This is testing handling of whitespace before parsing the next comma.
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_Multiple.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testUnionType_InTypedMapTest() {
        val expected = loadResource("tests/parser/saxon-9.8/UnionType_InTypedMapTest.txt")
        val actual = parseResource("tests/parser/saxon-9.8/UnionType_InTypedMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
}
