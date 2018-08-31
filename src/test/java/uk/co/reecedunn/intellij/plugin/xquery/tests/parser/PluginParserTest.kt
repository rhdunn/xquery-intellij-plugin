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
    // region MarkLogic 6.0 :: BinaryConstructor

    @Test
    fun testBinaryConstructor() {
        val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBinaryConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBinaryConstructor_NoExpr() {
        val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor_NoExpr.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor_NoExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBinaryConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-6.0/BinaryConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/BinaryConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryTest

    @Test
    fun testBinaryTest() {
        val expected = loadResource("tests/parser/marklogic-6.0/BinaryTest.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBinaryTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/BinaryTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/BinaryTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBinaryTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-6.0/BinaryTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/BinaryTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 6.0 :: CatchClause + TryCatchExpr

    @Test
    fun testCatchClause() {
        val expected = loadResource("tests/parser/marklogic-6.0/CatchClause.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CatchClause.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_MissingVariableIndicator() {
        val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingVariableIndicator.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingVariableIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_MissingVarName() {
        val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingVarName.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_EmptyExpr() {
        val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCatchClause_Multiple() {
        val expected = loadResource("tests/parser/marklogic-6.0/CatchClause_Multiple.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CatchClause_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 6.0 :: CompatibilityAnnotation

    @Test
    fun testCompatibilityAnnotation_FunctionDecl() {
        val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword() {
        val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompatibilityAnnotation_VarDecl() {
        val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testCompatibilityAnnotation_VarDecl_MissingVariableKeyword() {
        val expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 6.0 :: ForwardAxis

    @Test
    fun testForwardAxis_Namespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Namespace_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Property() {
        val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Property.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testForwardAxis_Property_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Property_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 6.0 :: StylesheetImport

    @Test
    fun testStylesheetImport() {
        val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStylesheetImport_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStylesheetImport_MissingAtKeyword() {
        val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_MissingAtKeyword.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_MissingAtKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testStylesheetImport_MissingUriLiteral() {
        val expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_MissingUriLiteral.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_MissingUriLiteral.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 6.0 :: Transactions + TransactionSeparator

    @Test
    fun testTransactions_WithVersionDecl() {
        val expected = loadResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTransactions_LibraryModule() {
        val expected = loadResource("tests/parser/marklogic-6.0/Transactions_LibraryModule.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/Transactions_LibraryModule.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTransactions_Prolog_ImportStatement() {
        val expected = loadResource("tests/parser/marklogic-6.0/Transactions_Prolog_ImportStatement.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/Transactions_Prolog_ImportStatement.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTransactions_Prolog_DeclareStatement() {
        val expected = loadResource("tests/parser/marklogic-6.0/Transactions_Prolog_DeclareStatement.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/Transactions_Prolog_DeclareStatement.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTransactions_AfterApplyExpr() {
        val expected = loadResource("tests/parser/marklogic-6.0/Transactions_AfterApplyExpr.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/Transactions_AfterApplyExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 6.0 :: ValidateExpr

    @Test
    fun testValidateExpr_ValidateAs() {
        val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_ValidateAs_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_ValidateAs_MissingTypeName() {
        val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingTypeName.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingTypeName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_ValidateAs_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testValidateExpr_ValidateAs_EQName() {
        val expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_EQName.txt")
        val actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 7.0 :: AttributeDeclTest

    @Test
    fun testAttributeDeclTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/AttributeDeclTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeDeclTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/AttributeDeclTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAttributeDeclTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/AttributeDeclTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/AttributeDeclTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 7.0 :: ComplexTypeTest

    @Test
    fun testComplexTypeTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/ComplexTypeTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComplexTypeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/ComplexTypeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testComplexTypeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/ComplexTypeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/ComplexTypeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 7.0 :: ElementDeclTest

    @Test
    fun testElementDeclTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/ElementDeclTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/ElementDeclTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementDeclTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/ElementDeclTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/ElementDeclTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testElementDeclTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/ElementDeclTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/ElementDeclTest_MissingClosingParenthesis.xq")
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

    @Test
    fun testTupleField_NoSequenceType() {
        val expected = loadResource("tests/parser/saxon-9.8/TupleField_NoSequenceType.txt")
        val actual = parseResource("tests/parser/saxon-9.8/TupleField_NoSequenceType.xq")
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
