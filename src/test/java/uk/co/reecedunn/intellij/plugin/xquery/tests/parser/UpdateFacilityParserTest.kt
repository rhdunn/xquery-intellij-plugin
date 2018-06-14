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
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.tests.Specification

class UpdateFacilityParserTest : ParserTestCase() {
    // region Update Facility 1.0 :: FunctionDecl

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Updating() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Updating_EnclosedExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-FunctionDecl")
    fun testFunctionDecl_Updating_MissingFunctionKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 1.0 :: RevalidationDecl

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    fun testRevalidationDecl() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    fun testRevalidationDecl_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    fun testRevalidationDecl_MissingRevalidationMode() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    fun testRevalidationDecl_MissingRevalidationKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    fun testRevalidationDecl_MissingSemicolon() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    fun testRevalidationDecl_PrologBodyStatementsBefore() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RevalidationDecl")
    fun testRevalidationDecl_PrologBodyStatementsAfter() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 1.0 :: InsertExprTargetChoice

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_Into() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_First() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_Last() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_FirstLast_MissingAsKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_FirstLast_MissingFirstLastKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_FirstLast_MissingIntoKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_Before() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExprTargetChoice")
    fun testInsertExprTargetChoice_After() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 1.0 :: InsertExpr

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    fun testInsertExpr_Node() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    fun testInsertExpr_Node_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    fun testInsertExpr_Nodes() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    fun testInsertExpr_Nodes_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    fun testInsertExpr_MissingSourceExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    fun testInsertExpr_MissingInsertExprTargetChoice() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-InsertExpr")
    fun testInsertExpr_MissingTargetExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 1.0 :: DeleteExpr

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    fun testDeleteExpr_Node() {
        val expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    fun testDeleteExpr_Nodes() {
        val expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-DeleteExpr")
    fun testDeleteExpr_MissingTargetExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 1.0 :: ReplaceExpr

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    fun testReplaceExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    fun testReplaceExpr_MissingTargetExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    fun testReplaceExpr_MissingWithKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    fun testReplaceExpr_MissingReplaceExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    fun testReplaceExpr_ValueOf() {
        val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    fun testReplaceExpr_ValueOf_MissingNodeKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-ReplaceExpr")
    fun testReplaceExpr_ValueOf_MissingOfKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 1.0 :: RenameExpr

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    fun testRenameExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    fun testRenameExpr_MissingTargetExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingTargetExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingTargetExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    fun testRenameExpr_MissingAsKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingAsKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingAsKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-RenameExpr")
    fun testRenameExpr_MissingNewNameExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingNewNameExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingNewNameExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 1.0 :: TransformExpr (CopyModifyExpr)

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_Equal() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_Equal.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_Equal.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MissingVarName() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarName.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MissingVarAssignOperator() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignOperator.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignOperator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MissingVarAssignExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MissingModifyKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MissingModifyExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MissingReturnKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MissingReturnExpr() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MultipleVarName() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MultipleVarName_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    fun testTransformExpr_MultipleVarName_MissingVarIndicator() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_MissingVarIndicator.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_MissingVarIndicator.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 1.0", reference = "https://www.w3.org/TR/2011/REC-xquery-update-10-20110317/#prod-xquery-TransformExpr")
    @Specification(name = "XQuery 3.0", reference = "https://www.w3.org/TR/2014/REC-xquery-30-20140408/#prod-xquery30-VarName")
    fun testTransformExpr_EQName() {
        val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_EQName.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_EQName.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 3.0 :: CompatibilityAnnotation

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-CompatibilityAnnotation")
    fun testCompatibilityAnnotation_FunctionDecl() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-CompatibilityAnnotation")
    fun testCompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword() {
        val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-CompatibilityAnnotation")
    fun testCompatibilityAnnotation_VarDecl() {
        val expected = loadResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-CompatibilityAnnotation")
    fun testCompatibilityAnnotation_VarDecl_MissingVariableKeyword() {
        val expected = loadResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 3.0 :: TransformWithExpr

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-TransformWithExpr")
    fun testTransformWithExpr() {
        val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-TransformWithExpr")
    fun testTransformWithExpr_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-TransformWithExpr")
    fun testTransformWithExpr_MissingWithKeyword() {
        val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingWithKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingWithKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-TransformWithExpr")
    fun testTransformWithExpr_MissingOpeningBrace() {
        val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingOpeningBrace.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingOpeningBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-TransformWithExpr")
    fun testTransformWithExpr_MissingExpr() {
        val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-TransformWithExpr")
    fun testTransformWithExpr_MissingClosingBrace() {
        val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingClosingBrace.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingClosingBrace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 3.0 :: UpdatingFunctionCall

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_MissingUpdatingKeyword() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingUpdatingKeyword.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingUpdatingKeyword.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_MissingOpeningParenthesis() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingOpeningParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingOpeningParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_MissingPrimaryExpr() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingPrimaryExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingPrimaryExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_MissingClosingParenthesis() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingClosingParenthesis.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingClosingParenthesis.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_Arguments_Single() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Single.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Single.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_Arguments_Multiple() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-UpdatingFunctionCall")
    fun testUpdatingFunctionCall_Arguments_Multiple_CompactWhitespace() {
        val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple_CompactWhitespace.txt")
        val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple_CompactWhitespace.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
    // region Update Facility 3.1 :: TransformWithExpr

    @Specification(name = "XQuery Update Facility 3.0", reference = "https://www.w3.org/TR/2017/NOTE-xquery-update-30-20170124/#prod-xquery30-TransformWithExpr")
    fun TransformWithExpr_ArrowExpr() {
        val expected = loadResource("tests/parser/xquery-update-3.1/TransformWithExpr_ArrowExpr.txt")
        val actual = parseResource("tests/parser/xquery-update-3.1/TransformWithExpr_ArrowExpr.xq")
        assertThat(prettyPrintASTNode(actual), `is`(expected))
    }

    // endregion
}
