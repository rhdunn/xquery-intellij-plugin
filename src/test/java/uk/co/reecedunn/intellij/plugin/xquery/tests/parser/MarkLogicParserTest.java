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
    // region MarkLogic 6.0 :: CompatibilityAnnotation

    public void testCompatibilityAnnotation_FunctionDecl() {
        final String expected = loadResource("tests/parser/marklogic/CompatibilityAnnotation_FunctionDecl.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/CompatibilityAnnotation_FunctionDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/marklogic/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/CompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompatibilityAnnotation_VarDecl() {
        final String expected = loadResource("tests/parser/marklogic/CompatibilityAnnotation_VarDecl.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/CompatibilityAnnotation_VarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testCompatibilityAnnotation_VarDecl_MissingVariableKeyword() {
        final String expected = loadResource("tests/parser/marklogic/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: ValidateExpr

    public void testValidateExpr_ValidateAs() {
        final String expected = loadResource("tests/parser/marklogic/ValidateExpr_ValidateAs.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ValidateExpr_ValidateAs.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/ValidateExpr_ValidateAs_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ValidateExpr_ValidateAs_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_MissingTypeName() {
        final String expected = loadResource("tests/parser/marklogic/ValidateExpr_ValidateAs_MissingTypeName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ValidateExpr_ValidateAs_MissingTypeName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/marklogic/ValidateExpr_ValidateAs_MissingOpeningBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ValidateExpr_ValidateAs_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testValidateExpr_ValidateAs_EQName() {
        final String expected = loadResource("tests/parser/marklogic/ValidateExpr_ValidateAs_EQName.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ValidateExpr_ValidateAs_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: ForwardAxis

    public void testForwardAxis_Namespace() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Namespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Namespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Namespace_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Namespace_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Namespace_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Property() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Property.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Property.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testForwardAxis_Property_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/ForwardAxis_Property_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/ForwardAxis_Property_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryExpr

    public void testBinaryExpr() {
        final String expected = loadResource("tests/parser/marklogic/BinaryExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/BinaryExpr_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryExpr_NoExpr() {
        final String expected = loadResource("tests/parser/marklogic/BinaryExpr_NoExpr.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryExpr_NoExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/marklogic/BinaryExpr_MissingClosingBrace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region MarkLogic 6.0 :: BinaryKindTest

    public void testBinaryKindTest() {
        final String expected = loadResource("tests/parser/marklogic/BinaryKindTest.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryKindTest.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryKindTest_CompactWhitespace() {
        final String expected = loadResource("tests/parser/marklogic/BinaryKindTest_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryKindTest_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    public void testBinaryKindTest_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/marklogic/BinaryKindTest_MissingClosingParenthesis.txt");
        final ASTNode actual = parseResource("tests/parser/marklogic/BinaryKindTest_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
