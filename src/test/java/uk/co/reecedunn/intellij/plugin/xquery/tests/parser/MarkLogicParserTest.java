/*
 * Copyright (C) 2016 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser;

import com.intellij.lang.ASTNode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MarkLogicParserTest extends ParserTestCase {
    // region MarkLogic 6.0 :: Transactions + TransactionSeparator

    public void testTransactions() {
        final String expected = loadResource("tests/parser/marklogic-6.0/Transactions.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/Transactions.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTransactions_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/Transactions_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/Transactions_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTransactions_WithVersionDecl() {
        final String expected = loadResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTransactions_MissingMainModule() {
        final String expected = loadResource("tests/parser/marklogic-6.0/Transactions_MissingMainModule.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/Transactions_MissingMainModule.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTransactions_LibraryModule() {
        final String expected = loadResource("tests/parser/marklogic-6.0/Transactions_LibraryModule.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/Transactions_LibraryModule.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: CompatibilityAnnotation

    public void testCompatibilityAnnotation_FunctionDecl() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompatibilityAnnotation_VarDecl() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompatibilityAnnotation_VarDecl_MissingVariableKeyword() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: StylesheetImport

    public void testStylesheetImport() {
        final String expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStylesheetImport_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStylesheetImport_MissingAtKeyword() {
        final String expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_MissingAtKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_MissingAtKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testStylesheetImport_MissingUriLiteral() {
        final String expected = loadResource("tests/parser/marklogic-6.0/StylesheetImport_MissingUriLiteral.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/StylesheetImport_MissingUriLiteral.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: CatchClause + TryCatchExpr

    public void testCatchClause() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CatchClause.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CatchClause.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCatchClause_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CatchClause_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CatchClause_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCatchClause_MissingVariableIndicator() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingVariableIndicator.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingVariableIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCatchClause_MissingVarName() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingVarName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCatchClause_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CatchClause_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CatchClause_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCatchClause_EmptyExpr() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CatchClause_EmptyExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCatchClause_Multiple() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CatchClause_Multiple.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CatchClause_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: ValidateExpr

    public void testValidateExpr_ValidateAs() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_MissingTypeName() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingTypeName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingOpeningBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_EQName() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_EQName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ValidateExpr_ValidateAs_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: ForwardAxis

    public void testForwardAxis_Namespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Namespace_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Namespace_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Property() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Property.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Property_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/ForwardAxis_Property_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/ForwardAxis_Property_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: CompBinaryConstructor

    public void testCompBinaryConstructor() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompBinaryConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompBinaryConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompBinaryConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompBinaryConstructor_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompBinaryConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompBinaryConstructor_NoExpr() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompBinaryConstructor_NoExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompBinaryConstructor_NoExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompBinaryConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/CompBinaryConstructor_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/CompBinaryConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryKindTest

    public void testBinaryKindTest() {
        final String expected = loadResource("tests/parser/marklogic-6.0/BinaryKindTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/BinaryKindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryKindTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/BinaryKindTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/BinaryKindTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryKindTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic-6.0/BinaryKindTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/BinaryKindTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: AnyKindTest

    public void testAnyKindTest_KeyName() {
        final String expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyKindTest_KeyName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_KeyName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyKindTest_Wildcard() {
        final String expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testAnyKindTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/AnyKindTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
