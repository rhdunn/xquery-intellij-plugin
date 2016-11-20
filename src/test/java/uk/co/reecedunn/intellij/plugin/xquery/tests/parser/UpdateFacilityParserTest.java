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

import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQueryVersion;
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings;
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UpdateFacilityParserTest extends ParserTestCase {
    // region Initialization

    public void initializeSettings(XQueryProjectSettings settings) {
        settings.setImplementation("w3c");
        settings.setXQueryVersion(XQueryVersion.VERSION_1_0);
        settings.setXQuery10Dialect("w3c/1.0-update");
    }

    // endregion
    // region Update Facility 1.0 :: FunctionDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    public void testFunctionDecl_Updating_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: RevalidationDecl

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_MissingRevalidationMode() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_MissingRevalidationKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_MissingSemicolon() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_PrologBodyStatementsBefore() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    public void testRevalidationDecl_PrologBodyStatementsAfter() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: InsertExprTargetChoice

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_Into() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_First() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_Last() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_FirstLast_MissingAsKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_FirstLast_MissingFirstLastKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_FirstLast_MissingIntoKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_Before() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    public void testInsertExprTargetChoice_After() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: InsertExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Node() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Node_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Nodes() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_Nodes_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_MissingSourceExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_MissingInsertExprTargetChoice() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    public void testInsertExpr_MissingTargetExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: DeleteExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr_Node() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr_Nodes() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    public void testDeleteExpr_MissingTargetExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: ReplaceExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_MissingTargetExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_MissingWithKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_MissingReplaceExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_ValueOf() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_ValueOf_MissingNodeKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    public void testReplaceExpr_ValueOf_MissingOfKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: RenameExpr

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    public void testRenameExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    public void testRenameExpr_MissingTargetExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingTargetExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingTargetExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    public void testRenameExpr_MissingAsKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingAsKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingAsKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    public void testRenameExpr_MissingNewNameExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingNewNameExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingNewNameExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 1.0 :: TransformExpr (CopyModifyExpr)

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MissingVarName() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarName.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MissingVarAssignOperator() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignOperator.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignOperator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MissingVarAssignExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MissingModifyKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MissingModifyExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MissingReturnKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MissingReturnExpr() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MultipleVarName() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MultipleVarName_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    public void testTransformExpr_MultipleVarName_MissingVarIndicator() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_MissingVarIndicator.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_MissingVarIndicator.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 1.0", reference="https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    @Specification(name="XQuery 3.0", reference="https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VarName")
    public void testTransformExpr_EQName() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_EQName.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_EQName.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 3.0 :: CompatibilityAnnotation

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-CompatibilityAnnotation")
    public void testCompatibilityAnnotation_FunctionDecl() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-CompatibilityAnnotation")
    public void testCompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-CompatibilityAnnotation")
    public void testCompatibilityAnnotation_VarDecl() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-CompatibilityAnnotation")
    public void testCompatibilityAnnotation_VarDecl_MissingVariableKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 3.0 :: TransformWithExpr

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-TransformWithExpr")
    public void testTransformWithExpr() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-TransformWithExpr")
    public void testTransformWithExpr_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-TransformWithExpr")
    public void testTransformWithExpr_MissingWithKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingWithKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingWithKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-TransformWithExpr")
    public void testTransformWithExpr_MissingOpeningBrace() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingOpeningBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingOpeningBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-TransformWithExpr")
    public void testTransformWithExpr_MissingExpr() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-TransformWithExpr")
    public void testTransformWithExpr_MissingClosingBrace() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingClosingBrace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingClosingBrace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
    // region Update Facility 3.0 :: UpdatingFunctionCall

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_MissingUpdatingKeyword() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingUpdatingKeyword.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingUpdatingKeyword.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_MissingOpeningParenthesis() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingOpeningParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingOpeningParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_MissingPrimaryExpr() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingPrimaryExpr.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingPrimaryExpr.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_MissingClosingParenthesis() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingClosingParenthesis.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingClosingParenthesis.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_Arguments_Single() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Single.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Single.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_Arguments_Multiple() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    @Specification(name="XQuery Update Facility 3.0", reference="https://www.w3.org/TR/2015/WD-xquery-update-30-20150219/#prod-xquery30-UpdatingFunctionCall")
    public void testUpdatingFunctionCall_Arguments_Multiple_CompactWhitespace() {
        final String expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple_CompactWhitespace.txt");
        final XQueryFile actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple_CompactWhitespace.xq");
        assertThat(prettyPrintASTNode(actual), is(expected));
    }

    // endregion
}
