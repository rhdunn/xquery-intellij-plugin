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
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UpdateFacility10ParserTest extends XQuery10ParserTest {
    // region Initialization

    public void initializeSettings(XQueryProjectSettings settings) {
        settings.setImplementation("w3c");
        settings.setXQueryVersion(XQueryVersion.XQUERY_1_0);
        settings.setXQuery10Dialect("w3c/1.0-update");
    }

    // endregion
    // region Update Facility 1.0 :: FunctionDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: RevalidationDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_MissingRevalidationMode() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_PrologBodyStatementsBefore() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: InsertExprTargetChoice

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_Into() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_First() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_Last() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_FirstLast_MissingAsKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_FirstLast_MissingFirstLastKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_FirstLast_MissingIntoKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_Before() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_After() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: InsertExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Node() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Node_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Nodes() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Nodes_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_MissingSourceExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_MissingInsertExprTargetChoice() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_MissingTargetExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: DeleteExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr_Node() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr_Nodes() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr_MissingTargetExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: ReplaceExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_MissingTargetExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_MissingWithKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_MissingReplaceExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_ValueOf() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_ValueOf_MissingNodeKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_ValueOf_MissingOfKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.txt");
        final ASTNode actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
