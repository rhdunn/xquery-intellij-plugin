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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - Parser")
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
    // region MarkLogic 7.0 :: SchemaComponentTest

    @Test
    fun testSchemaComponentTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaComponentTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaComponentTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaComponentTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaComponentTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaComponentTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaComponentTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 7.0 :: SchemaParticleTest

    @Test
    fun testSchemaParticleTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaParticleTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaParticleTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaParticleTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaParticleTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaParticleTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaParticleTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 7.0 :: SchemaRootTest

    @Test
    fun testSchemaRootTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaRootTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaRootTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaRootTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaRootTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaRootTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaRootTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaRootTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 7.0 :: SchemaTypeTest

    @Test
    fun testSchemaTypeTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaTypeTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaTypeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaTypeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaTypeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/SchemaTypeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SchemaTypeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 7.0 :: SimpleTypeTest

    @Test
    fun testSimpleTypeTest() {
        val expected = loadResource("tests/parser/marklogic-7.0/SimpleTypeTest.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSimpleTypeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-7.0/SimpleTypeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSimpleTypeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-7.0/SimpleTypeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-7.0/SimpleTypeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: AnyArrayNodeTest

    @Test
    fun testAnyArrayNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyArrayNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyArrayNodeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyArrayNodeTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyArrayNodeTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyArrayNodeTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyArrayNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyArrayNodeTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: AnyBooleanNodeTest

    @Test
    fun testAnyBooleanNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyBooleanNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyBooleanNodeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyBooleanNodeTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyBooleanNodeTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyBooleanNodeTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyBooleanNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyBooleanNodeTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyBooleanNodeTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: AnyKindTest

    @Test
    fun testAnyKindTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyKindTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: AnyMapNodeTest

    @Test
    fun testAnyMapNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyMapNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyMapNodeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyMapNodeTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyMapNodeTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyMapNodeTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyMapNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyMapNodeTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyMapNodeTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: AnyNullNodeTest

    @Test
    fun testAnyNullNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNullNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNullNodeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNullNodeTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNullNodeTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNullNodeTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNodeTest_NullNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNodeTest_NullNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNodeTest_NullNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNodeTest_NullNodeTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNodeTest_NullNodeTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNodeTest_NullNodeTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: AnyNumberNodeTest

    @Test
    fun testAnyNumberNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNumberNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNumberNodeTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNumberNodeTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testAnyNumberNodeTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/AnyNumberNodeTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyNumberNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_AnyNumberNodeTest_FunctionCallLike() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest_FunctionCallLike.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyNumberNodeTest_FunctionCallLike.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: ArrayConstructor

    @Test
    fun testArrayConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_Multiple() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testArrayConstructor_Multiple_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/ArrayConstructor_Multiple_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: BooleanConstructor

    @Test
    fun testBooleanConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBooleanConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBooleanConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testBooleanConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/BooleanConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: DocumentTest

    @Test
    fun testDocumentTest_AnyArrayTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_AnyArrayTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_AnyArrayTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_NamedArrayTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_NamedArrayTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_NamedArrayTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_AnyMapTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyMapTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_AnyMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_AnyMapTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_AnyMapTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_MapTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testDocumentTest_NamedMapTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/DocumentTest_NamedMapTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/DocumentTest_NamedMapTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: MapConstructor

    @Test
    fun testMapConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMapConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: MapConstructorEntry + MapConstructor

    @Test
    fun testMarkLogic_MapConstructorEntry() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_MissingSeparator() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingSeparator.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingSeparator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_MissingValueExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_Multiple() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_Multiple_MissingEntry() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_MissingEntry.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_Multiple_MissingEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_NCName() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_NCName_WhitespaceAfterColon() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_WhitespaceAfterColon.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_WhitespaceAfterColon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_NCName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_NCName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_QName_KeyExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_KeyExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_KeyExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_QName_ValueExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_ValueExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_ValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_QName_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_QName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testMarkLogic_MapConstructorEntry_VarRef_NCName() {
        val expected = loadResource("tests/parser/marklogic-8.0/MapConstructorEntry_VarRef_NCName.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry_VarRef_NCName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NamedArrayNodeTest

    @Test
    fun testNamedArrayNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedArrayNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedArrayNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedArrayNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedArrayNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedArrayNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_NamedArrayNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedArrayNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedArrayNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NamedBooleanNodeTest

    @Test
    fun testNamedBooleanNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedBooleanNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedBooleanNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_NamedBooleanNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedBooleanNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedBooleanNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NamedKindTest

    @Test
    fun testNamedKindTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedKindTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedKindTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedKindTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedKindTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedKindTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NamedMapNodeTest

    @Test
    fun testNamedMapNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedMapNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedMapNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedMapNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedMapNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedMapNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_NamedMapNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedMapNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedMapNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NamedNullNodeTest

    @Test
    fun testNamedNullNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedNullNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedNullNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedNullNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedNullNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedNullNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_NamedNullNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedNullNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedNullNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NamedNumberNodeTest

    @Test
    fun testNamedNumberNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedNumberNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedNumberNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedNumberNodeTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedNumberNodeTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedNumberNodeTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNodeTest_NamedNumberNodeTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NodeTest_NamedNumberNodeTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NodeTest_NamedNumberNodeTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NamedTextTest

    @Test
    fun testNamedTextTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedTextTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedTextTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNamedTextTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NamedTextTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NamedTextTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NullConstructor

    @Test
    fun testNullConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNullConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NullConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NullConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: NumberConstructor

    @Test
    fun testNumberConstructor() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberConstructor_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberConstructor_MissingExpr() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_MissingExpr.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testNumberConstructor_MissingClosingBrace() {
        val expected = loadResource("tests/parser/marklogic-8.0/NumberConstructor_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/NumberConstructor_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: SchemaFacetTest

    @Test
    fun testSchemaFacetTest() {
        val expected = loadResource("tests/parser/marklogic-8.0/SchemaFacetTest.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaFacetTest_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/SchemaFacetTest_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSchemaFacetTest_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/marklogic-8.0/SchemaFacetTest_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/SchemaFacetTest_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region MarkLogic 8.0 :: TextTest

    @Test
    fun testTextTest_Wildcard() {
        val expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testTextTest_Wildcard_CompactWhitespace() {
        val expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Saxon 9.4 :: MapConstructorEntry + MapConstructor

    @Test
    fun testSaxon_MapConstructorEntry() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSaxon_MapConstructorEntry_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSaxon_MapConstructorEntry_MissingSeparator() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingSeparator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSaxon_MapConstructorEntry_MissingValueExpr() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_MissingValueExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSaxon_MapConstructorEntry_Multiple() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSaxon_MapConstructorEntry_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Test
    fun testSaxon_MapConstructorEntry_Multiple_MissingEntry() {
        val expected = loadResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.txt")
        val actual = parseResource("tests/parser/saxon-9.4/MapConstructorEntry_Multiple_MissingEntry.xq")
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (22) UnionType")
    internal inner class UnionType {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_NCName.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_NCName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("NCName; compact whitespace")
        fun ncname_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_NCName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_NCName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("QName; compact whitespace")
        fun qname_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_URIQualifiedName.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_URIQualifiedName.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("URIQualifiedName; compact whitespace")
        fun uriQualifiedName_CompactWhitespace() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_URIQualifiedName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_URIQualifiedName_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun testUnionType_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingClosingParenthesis.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing first type")
        fun testUnionType_MissingFirstType() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingFirstType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("missing next type")
        fun testUnionType_MissingNextType() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_MissingNextType.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_MissingNextType.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple types")
        fun testUnionType_Multiple() {
            // This is testing handling of whitespace before parsing the next comma.
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_Multiple.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("union in TypedMapTest")
        fun testUnionType_InTypedMapTest() {
            val expected = loadResource("tests/parser/saxon-9.8/UnionType_InTypedMapTest.txt")
            val actual = parseResource("tests/parser/saxon-9.8/UnionType_InTypedMapTest.xq")
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (86) ItemTypeUnion")
    internal inner class ItemTypeUnion {
        @Test
        @DisplayName("item type union")
        fun itemTypeUnion() {
            val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("item type union; compact whitespace")
        fun itemTypeUnion_CompactWhitespace() {
            val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple")
        fun multiple() {
            val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_Multiple.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_Multiple.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Test
        @DisplayName("multiple; compact whitespace")
        fun multiple_CompactWhitespace() {
            val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_Multiple_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_Multiple_CompactWhitespace.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("error recovery; missing token")
        internal inner class MissingToken {
            @Test
            @DisplayName("missing item type")
            fun missingItemType() {
                val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_MissingItemType.txt")
                val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_MissingItemType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("missing closing parenthesis")
            fun missingClosingParenthesis() {
                val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_MissingClosingParenthesis.txt")
                val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_MissingClosingParenthesis.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Test
        @DisplayName("error recovery; mixed with sequence type list")
        fun mixedWithSequenceTypeList() {
            val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_MixedWithSequenceTypeList.txt")
            val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_MixedWithSequenceTypeList.xq")
            assertThat(prettyPrintASTNode(actual), `is`(expected))
        }

        @Nested
        @DisplayName("sequence type")
        internal inner class ItemTypeAsSequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_EmptySequence.txt")
                val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_EmptySequence.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("occurrence indicator")
            fun occurrenceIndicator() {
                val expected = loadResource("tests/parser/intellij-plugin/ItemTypeUnion_OccurrenceIndicator.txt")
                val actual = parseResource("tests/parser/intellij-plugin/ItemTypeUnion_OccurrenceIndicator.xq")
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
        @DisplayName("error recovery; mixed with item type union")
        fun mixedWithItemTypeUnion() {
            val expected = loadResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MixedWithItemTypeUnion.txt")
            val actual = parseResource("tests/parser/xquery-semantics-1.0/SequenceTypeList_MixedWithItemTypeUnion.xq")
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
    @DisplayName("XQuery IntelliJ Plugin EBNF (90) AnnotatedSequenceType")
    internal inner class AnnotatedSequenceType {
        @Nested
        @DisplayName("annotations on kind tests")
        internal inner class KindTest {
            @Test
            @DisplayName("kind test")
            fun kindTest() {
                val expected = loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_KindTest.txt")
                val actual = parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_KindTest.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("kind test; compact whitespace")
            fun kindTest_CompactWhitespace() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_KindTest_CompactWhitespace.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_KindTest_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("annotations on function tests")
        internal inner class FunctionTest {
            @Test
            @DisplayName("function without annotations")
            fun functionWithoutAnnotations() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithoutAnnotations.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithoutAnnotations.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("function without annotations; compact whitespace")
            fun functionWithoutAnnotations_CompactWhitespace() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithoutAnnotations_CompactWhitespace.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithoutAnnotations_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("function with annotations")
            fun functionWithAnnotations() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithAnnotations.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithAnnotations.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("function with annotations; compact whitespace")
            fun functionWithAnnotations_CompactWhitespace() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithAnnotations_CompactWhitespace.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_FunctionTest_FunctionWithAnnotations_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("annotations on atomic or union types")
        internal inner class AtomicOrUnionType {
            @Test
            @DisplayName("atomic or union type")
            fun atomicOrUnionType() {
                val expected = loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_AtomicOrUnionType.txt")
                val actual = parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_AtomicOrUnionType.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("atomic or union type; compact whitespace")
            fun atomicOrUnionType_CompactWhitespace() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_AtomicOrUnionType_CompactWhitespace.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_AtomicOrUnionType_CompactWhitespace.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("annotations on sequence types")
        internal inner class SequenceType {
            @Test
            @DisplayName("empty sequence")
            fun emptySequence() {
                val expected = loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_EmptySequence.txt")
                val actual = parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_EmptySequence.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("occurrence indicator")
            fun occurrenceIndicator() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_OccurrenceIndicator.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_OccurrenceIndicator.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }
        }

        @Nested
        @DisplayName("multiple annotations")
        internal inner class Multiple {
            @Test
            @DisplayName("multiple")
            fun multipleAnnotations() {
                val expected = loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_Multiple.txt")
                val actual = parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_Multiple.xq")
                assertThat(prettyPrintASTNode(actual), `is`(expected))
            }

            @Test
            @DisplayName("multiple; compact whitespace")
            fun multipleAnnotations_CompactWhitespace() {
                val expected =
                    loadResource("tests/parser/intellij-plugin/AnnotatedSequenceType_Multiple_CompactWhitespace.txt")
                val actual =
                    parseResource("tests/parser/intellij-plugin/AnnotatedSequenceType_Multiple_CompactWhitespace.xq")
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
}
