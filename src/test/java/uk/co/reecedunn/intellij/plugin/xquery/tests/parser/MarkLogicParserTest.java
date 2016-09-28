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
    // region MarkLogic 6.0 :: BinaryTest

    public void testBinaryTest() {
        final String expected = loadResource("tests/parser/marklogic-6.0/BinaryTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/BinaryTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-6.0/BinaryTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/BinaryTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic-6.0/BinaryTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-6.0/BinaryTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: CompArrayConstructor

    public void testCompArrayConstructor() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompArrayConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompArrayConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompArrayConstructor_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompArrayConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompArrayConstructor_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompArrayConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompArrayConstructor_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompArrayConstructor_Multiple() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompArrayConstructor_Multiple.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompArrayConstructor_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompArrayConstructor_Multiple_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompArrayConstructor_Multiple_MissingExpr() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompArrayConstructor_Multiple_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompArrayConstructor_Multiple_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: CompBooleanConstructor

    public void testCompBooleanConstructor() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompBooleanConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompBooleanConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompBooleanConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompBooleanConstructor_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompBooleanConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompBooleanConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompBooleanConstructor_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompBooleanConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompBooleanConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompBooleanConstructor_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompBooleanConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: CompNullConstructor

    public void testCompNullConstructor() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompNullConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompNullConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompNullConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompNullConstructor_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompNullConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompNullConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompNullConstructor_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompNullConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: CompNumberConstructor

    public void testCompNumberConstructor() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompNumberConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompNumberConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompNumberConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompNumberConstructor_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompNumberConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompNumberConstructor_MissingExpr() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompNumberConstructor_MissingExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompNumberConstructor_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompNumberConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompNumberConstructor_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompNumberConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: CompObjectConstructor

    public void testCompObjectConstructor() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompObjectConstructor.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompObjectConstructor.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompObjectConstructor_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompObjectConstructor_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompObjectConstructor_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompObjectConstructor_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/CompObjectConstructor_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/CompObjectConstructor_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: ObjectKeyValue + CompObjectConstructor

    public void testObjectKeyValue() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ObjectKeyValue.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ObjectKeyValue.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testObjectKeyValue_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ObjectKeyValue_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ObjectKeyValue_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testObjectKeyValue_MissingSeparator() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ObjectKeyValue_MissingSeparator.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ObjectKeyValue_MissingSeparator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testObjectKeyValue_MissingValueExpr() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ObjectKeyValue_MissingValueExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ObjectKeyValue_MissingValueExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testObjectKeyValue_Multiple() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ObjectKeyValue_Multiple.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ObjectKeyValue_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testObjectKeyValue_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ObjectKeyValue_Multiple_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ObjectKeyValue_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testObjectKeyValue_Multiple_MissingObjectKeyValue() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ObjectKeyValue_Multiple_MissingObjectKeyValue.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ObjectKeyValue_Multiple_MissingObjectKeyValue.xq");
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
    // region MarkLogic 8.0 :: TextTest

    public void testTextTest_KeyName() {
        final String expected = loadResource("tests/parser/marklogic-8.0/TextTest_KeyName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTextTest_KeyName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/TextTest_KeyName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/TextTest_KeyName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTextTest_Wildcard() {
        final String expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testTextTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/TextTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: ArrayTest

    public void testArrayTest() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ArrayTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ArrayTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testArrayTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testArrayTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testArrayTest_KeyName() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_KeyName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_KeyName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testArrayTest_KeyName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_KeyName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_KeyName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testArrayTest_Wildcard() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testArrayTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/ArrayTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: BooleanTest

    public void testBooleanTest() {
        final String expected = loadResource("tests/parser/marklogic-8.0/BooleanTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/BooleanTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBooleanTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/BooleanTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/BooleanTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBooleanTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic-8.0/BooleanTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/BooleanTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBooleanTest_KeyName() {
        final String expected = loadResource("tests/parser/marklogic-8.0/BooleanTest_KeyName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/BooleanTest_KeyName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBooleanTest_KeyName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/BooleanTest_KeyName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/BooleanTest_KeyName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBooleanTest_Wildcard() {
        final String expected = loadResource("tests/parser/marklogic-8.0/BooleanTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/BooleanTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBooleanTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/BooleanTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/BooleanTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: NullTest

    public void testNullTest() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NullTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NullTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNullTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NullTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NullTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNullTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NullTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NullTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNullTest_KeyName() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NullTest_KeyName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NullTest_KeyName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNullTest_KeyName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NullTest_KeyName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NullTest_KeyName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNullTest_Wildcard() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NullTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NullTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNullTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NullTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NullTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 8.0 :: NumberTest

    public void testNumberTest() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NumberTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NumberTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNumberTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NumberTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NumberTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNumberTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NumberTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NumberTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNumberTest_KeyName() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NumberTest_KeyName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NumberTest_KeyName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNumberTest_KeyName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NumberTest_KeyName_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NumberTest_KeyName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNumberTest_Wildcard() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NumberTest_Wildcard.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NumberTest_Wildcard.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testNumberTest_Wildcard_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic-8.0/NumberTest_Wildcard_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic-8.0/NumberTest_Wildcard_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
