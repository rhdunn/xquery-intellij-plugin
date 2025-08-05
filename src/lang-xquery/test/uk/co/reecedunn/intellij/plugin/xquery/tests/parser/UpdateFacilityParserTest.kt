// Copyright (C) 2016-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.psi.toPsiTreeString
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier", "Reformat")
@DisplayName("XQuery Update Facility 3.0 - Parser")
class UpdateFacilityParserTest : ParsingTestCase<XQueryModule>(XQuery) {
    override val pluginId: PluginId = PluginId.getId("UpdateFacilityParserTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XPathASTFactory().registerExtension(project, XPath)
        XPathParserDefinition().registerExtension(project)

        XQueryASTFactory().registerExtension(project, XQuery)
        XQueryParserDefinition().registerExtension(project)
        XQueryFileType.registerFileType()

        project.registerService(XQueryProjectSettings())
    }

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    fun loadResource(resource: String): String? = res.findFileByPath(resource)!!.decode()

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (26) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("updating annotation; external")
        fun testFunctionDecl_Updating() {
            val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("updating annotation; function body")
        fun testFunctionDecl_Updating_EnclosedExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("updating annotation; missing function keyword")
        fun testFunctionDecl_Updating_MissingFunctionKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (141) RevalidationDecl")
    internal inner class RevalidationDecl {
        @Test
        @DisplayName("revalidation declaration")
        fun testRevalidationDecl() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("revalidation declaration; compact whitespace")
        fun testRevalidationDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing revalidation mode")
        fun testRevalidationDecl_MissingRevalidationMode() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationMode.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing revalidation keyword")
        fun testRevalidationDecl_MissingRevalidationKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingRevalidationKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing semicolon")
        fun testRevalidationDecl_MissingSemicolon() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("prolog body statements before")
        fun testRevalidationDecl_PrologBodyStatementsBefore() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsBefore.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("prolog body statements after")
        fun testRevalidationDecl_PrologBodyStatementsAfter() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl_PrologBodyStatementsAfter.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (142) InsertExprTargetChoice")
    internal inner class InsertExprTargetChoice {
        @Test
        @DisplayName("into")
        fun testInsertExprTargetChoice_Into() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("first")
        fun testInsertExprTargetChoice_First() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_First.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("last")
        fun testInsertExprTargetChoice_Last() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Last.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("first/last; missing 'as' keyword")
        fun testInsertExprTargetChoice_FirstLast_MissingAsKeyword() {
            val expected =
                loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.txt")
            val actual =
                parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingAsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("first/last; missing 'first'/'last' keyword")
        fun testInsertExprTargetChoice_FirstLast_MissingFirstLastKeyword() {
            val expected =
                loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.txt")
            val actual =
                parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingFirstLastKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("first/last; missing 'into' keyword")
        fun testInsertExprTargetChoice_FirstLast_MissingIntoKeyword() {
            val expected =
                loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.txt")
            val actual =
                parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_FirstLast_MissingIntoKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("before")
        fun testInsertExprTargetChoice_Before() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_Before.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("after")
        fun testInsertExprTargetChoice_After() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExprTargetChoice_After.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (143) InsertExpr")
    internal inner class InsertExpr {
        @Test
        @DisplayName("node")
        fun testInsertExpr_Node() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("node; compact whitespace")
        fun testInsertExpr_Node_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nodes")
        fun testInsertExpr_Nodes() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nodes; compact whitespace")
        fun testInsertExpr_Nodes_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Nodes_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing source SourceExpr")
        fun testInsertExpr_MissingSourceExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingSourceExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing InsertExprTargetChoice")
        fun testInsertExpr_MissingInsertExprTargetChoice() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingInsertExprTargetChoice.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing TargetExpr")
        fun testInsertExpr_MissingTargetExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/InsertExpr_MissingTargetExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (144) DeleteExpr")
    internal inner class DeleteExpr {
        @Test
        @DisplayName("node")
        fun testDeleteExpr_Node() {
            val expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("nodes")
        fun testDeleteExpr_Nodes() {
            val expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Nodes.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing TargetExpr")
        fun testDeleteExpr_MissingTargetExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_MissingTargetExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (145) ReplaceExpr")
    internal inner class ReplaceExpr {
        @Test
        @DisplayName("replace expression")
        fun testReplaceExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing TargetExpr")
        fun testReplaceExpr_MissingTargetExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingTargetExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing 'with' keyword")
        fun testReplaceExpr_MissingWithKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingWithKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing ReplaceExpr")
        fun testReplaceExpr_MissingReplaceExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_MissingReplaceExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("value of")
        fun testReplaceExpr_ValueOf() {
            val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("value of; missing 'node' keyword")
        fun testReplaceExpr_ValueOf_MissingNodeKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingNodeKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("value of; missing 'of' keyword")
        fun testReplaceExpr_ValueOf_MissingOfKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr_ValueOf_MissingOfKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (145) ReplaceExpr")
    internal inner class RenameExpr {
        @Test
        @DisplayName("rename expression")
        fun testRenameExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing TargetExpr")
        fun testRenameExpr_MissingTargetExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingTargetExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingTargetExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing 'as' keyword")
        fun testRenameExpr_MissingAsKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingAsKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingAsKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing NewNameExpr")
        fun testRenameExpr_MissingNewNameExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/RenameExpr_MissingNewNameExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/RenameExpr_MissingNewNameExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (150) TransformExpr ; XQuery Update Facility 3.0 EBNF (208) CopyModifyExpr")
    internal inner class TransformExpr {
        @Test
        @DisplayName("copy modify expression")
        fun testTransformExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("copy modify expression; compact whitespace")
        fun testTransformExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("copy modify expression; recover using '=' instead of ':='")
        fun testTransformExpr_Equal() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_Equal.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_Equal.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing VarName")
        fun testTransformExpr_MissingVarName() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing ':='")
        fun testTransformExpr_MissingVarAssignOperator() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignOperator.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignOperator.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing variable assignment Expr")
        fun testTransformExpr_MissingVarAssignExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingVarAssignExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing 'modify' keyword")
        fun testTransformExpr_MissingModifyKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing modify Expr")
        fun testTransformExpr_MissingModifyExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingModifyExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing 'return' keyword")
        fun testTransformExpr_MissingReturnKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing return Expr")
        fun testTransformExpr_MissingReturnExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MissingReturnExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName")
        fun testTransformExpr_MultipleVarName() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; compact whitespace")
        fun testTransformExpr_MultipleVarName_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple VarName; missing '$' variable indicator")
        fun testTransformExpr_MultipleVarName_MissingVarIndicator() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_MissingVarIndicator.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_MultipleVarName_MissingVarIndicator.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("VarName as XQuery 3.0 EQName")
        fun testTransformExpr_EQName() {
            val expected = loadResource("tests/parser/xquery-update-1.0/TransformExpr_EQName.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/TransformExpr_EQName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 EBNF (27) CompatibilityAnnotation")
    internal inner class CompatibilityAnnotation {
        @Test
        @DisplayName("function declaration")
        fun testCompatibilityAnnotation_FunctionDecl() {
            val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("function declaration; missing 'function' keyword")
        fun testCompatibilityAnnotation_FunctionDecl_MissingFunctionKeyword() {
            val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_MissingFunctionKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("variable declaration")
        fun testCompatibilityAnnotation_VarDecl() {
            val expected = loadResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("variable declaration; missing 'variable' keyword")
        fun testCompatibilityAnnotation_VarDecl_MissingVariableKeyword() {
            val expected = loadResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl_MissingVariableKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 EBNF (97) TransformWithExpr")
    internal inner class TransformWithExpr30 {
        @Test
        @DisplayName("transform with expression")
        fun testTransformWithExpr() {
            val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("transform with expression; compact whitespace")
        fun testTransformWithExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing 'with' keyword")
        fun testTransformWithExpr_MissingWithKeyword() {
            val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingWithKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingWithKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing opening brace")
        fun testTransformWithExpr_MissingOpeningBrace() {
            val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingOpeningBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing Expr")
        fun testTransformWithExpr_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing brace")
        fun testTransformWithExpr_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 EBNF (207) UpdatingFunctionCall")
    internal inner class UpdatingFunctionCall {
        @Test
        @DisplayName("updating function call")
        fun testUpdatingFunctionCall() {
            val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("updating function call; compact whitespace")
        fun testUpdatingFunctionCall_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing 'updating' keyword")
        fun testUpdatingFunctionCall_MissingUpdatingKeyword() {
            val expected =
                loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingUpdatingKeyword.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingUpdatingKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing opening parenthesis")
        fun testUpdatingFunctionCall_MissingOpeningParenthesis() {
            val expected =
                loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingOpeningParenthesis.txt")
            val actual =
                parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingOpeningParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing PrimaryExpr")
        fun testUpdatingFunctionCall_MissingPrimaryExpr() {
            val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingPrimaryExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingPrimaryExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun testUpdatingFunctionCall_MissingClosingParenthesis() {
            val expected =
                loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingClosingParenthesis.txt")
            val actual =
                parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("arguments: single")
        fun testUpdatingFunctionCall_Arguments_Single() {
            val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Single.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Single.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("arguments: multiple")
        fun testUpdatingFunctionCall_Arguments_Multiple() {
            val expected = loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("arguments: multiple; compact whitespace")
        fun testUpdatingFunctionCall_Arguments_Multiple_CompactWhitespace() {
            val expected =
                loadResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple_CompactWhitespace.txt")
            val actual =
                parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall_Arguments_Multiple_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.1 EBNF (97) TransformWithExpr")
    internal inner class TransformWithExpr31 {
        @Test
        @DisplayName("arrow expression")
        fun testTransformWithExpr_ArrowExpr() {
            val expected = loadResource("tests/parser/xquery-update-3.1/TransformWithExpr_ArrowExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-3.1/TransformWithExpr_ArrowExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
