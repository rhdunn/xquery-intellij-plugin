// Copyright (C) 2016, 2018-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.parser

import com.intellij.lang.Language
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
import uk.co.reecedunn.intellij.plugin.core.tests.parser.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
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

@Suppress("ClassName", "RedundantVisibilityModifier", "Reformat")
@DisplayName("XQuery Scripting Extensions 1.0 - Parser")
class ScriptingParserTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("ScriptingParserTest")
    override val language: Language = XQuery

    override fun registerServicesAndExtensions() {
        registerPsiFileFactory()
        registerPsiTreeWalker()

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
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (32) ApplyExpr")
    internal inner class ApplyExpr {
        @Test
        @DisplayName("single expression; semicolon at end")
        fun testApplyExpr_Single_SemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single expression; semicolon at end; compact whitespace")
        fun testApplyExpr_Single_SemicolonAtEnd_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single expression; no semicolon at end")
        fun testApplyExpr_Single_NoSemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("two expressions; semicolon at end")
        fun testApplyExpr_TwoExpr_SemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("two expressions; semicolon at end; compact whitespace")
        fun testApplyExpr_TwoExpr_SemicolonAtEnd_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_SemicolonAtEnd_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("two expressions; no semicolon at end")
        fun testApplyExpr_TwoExpr_NoSemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_NoSemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_TwoExpr_NoSemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple expressions; semicolon at end")
        fun testApplyExpr_Multiple_SemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple expressions; semicolon at end; compact whitespace")
        fun testApplyExpr_Multiple_SemicolonAtEnd_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple expressions; no semicolon at end")
        fun testApplyExpr_Multiple_NoSemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_NoSemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_NoSemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no semicolon between expressions at the start")
        fun testApplyExpr_NoSemicolonBetweenExpr_First() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_First.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_First.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no semicolon between expressions at the end")
        fun testApplyExpr_NoSemicolonBetweenExpr_Last() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_Last.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_NoSemicolonBetweenExpr_Last.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty statement at the start")
        fun testApplyExpr_EmptyStatement() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyStatement.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyStatement.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty statement in the middle")
        fun testApplyExpr_EmptyMiddleStatement() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyMiddleStatement.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_EmptyMiddleStatement.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (30) QueryBody ; XQuery Scripting Extensions 1.0 EBNF (32) ApplyExpr")
    internal inner class QueryBody {
        @Test
        @DisplayName("single expression; semicolon at end")
        fun testQueryBody_Single_SemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single expression; semicolon at end; compact whitespace")
        fun testQueryBody_Single_SemicolonAtEnd_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single expression; no semicolon at end")
        fun testQueryBody_Single_NoSemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-1.0/IntegerLiteral.txt")
            val actual = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("two expressions; semicolon at end")
        fun testQueryBody_TwoExpr_SemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("two expressions; semicolon at end; compact whitespace")
        fun testQueryBody_TwoExpr_SemicolonAtEnd_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("two expressions; no semicolon at end")
        fun testQueryBody_TwoExpr_NoSemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple expressions; semicolon at end")
        fun testQueryBody_Multiple_SemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple expressions; semicolon at end; compact whitespace")
        fun testQueryBody_Multiple_SemicolonAtEnd_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_SemicolonAtEnd_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple expressions; no semicolon at end")
        fun testQueryBody_Multiple_NoSemicolonAtEnd() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_NoSemicolonAtEnd.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Multiple_NoSemicolonAtEnd.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no semicolon between expressions at the start")
        fun testQueryBody_NoSemicolonBetweenExpr_First() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_First.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_First.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no semicolon between expressions at the end")
        fun testQueryBody_NoSemicolonBetweenExpr_Last() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_Last.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_NoSemicolonBetweenExpr_Last.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty statement at the start")
        fun testQueryBody_EmptyStatement() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyStatement.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyStatement.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("empty statement in the middle")
        fun testQueryBody_EmptyMiddleStatement() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyMiddleStatement.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/QueryBody_EmptyMiddleStatement.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (24) VarDecl")
    internal inner class VarDecl {
        @Test
        @DisplayName("assignable annotation")
        fun testVarDecl_Assignable() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("unassignable annotation")
        fun testVarDecl_Unassignable() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (26) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("no annotation; external")
        fun testFunctionDecl() {
            val expected = loadResource("tests/parser/xquery-1.0/FunctionDecl.txt")
            val actual = parseResource("tests/parser/xquery-1.0/FunctionDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("no annotation; function body")
        fun testFunctionDecl_EnclosedExpr() {
            val expected = loadResource("tests/parser/xquery-1.0/EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-1.0/EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("simple annotation; external")
        fun testFunctionDecl_Simple() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("simple annotation; function body")
        fun testFunctionDecl_Simple_EnclosedExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple_EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple_EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("updating annotation; external (from XQuery Update Facility 1.0)")
        fun testFunctionDecl_Updating() {
            val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("updating annotation; function body (from XQuery Update Facility 1.0)")
        fun testFunctionDecl_Updating_EnclosedExpr() {
            val expected = loadResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.txt")
            val actual = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating_EnclosedExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("sequential annotation; external")
        fun testFunctionDecl_Sequential() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("sequential annotation; function body")
        fun testFunctionDecl_Sequential_Block() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (153) BlockExpr")
    internal inner class BlockExpr {
        @Test
        @DisplayName("block expression")
        fun testBlockExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("block expression; compact whitespace")
        fun testBlockExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing opening brace")
        fun testBlockExpr_MissingOpeningBrace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingOpeningBrace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingOpeningBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing block body")
        fun testBlockExpr_MissingBlockBody() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingBlockBody.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingBlockBody.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing brace")
        fun testBlockExpr_MissingClosingBrace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingClosingBrace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockExpr_MissingClosingBrace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (155) BlockDecls ; XQuery Scripting Extensions 1.0 EBNF (154) Block")
    internal inner class BlockDecls_Block {
        @Test
        @DisplayName("no declarations")
        fun testBlockDecls_Empty() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential_Block.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single declaration")
        fun testBlockDecls_Single() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple declarations")
        fun testBlockDecls_Multiple() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockDecls_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockDecls_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (155) BlockDecls ; XQuery Scripting Extensions 1.0 EBNF (161) WhileBody")
    internal inner class BlockDecls_WhileBody {
        @Test
        @DisplayName("no declarations")
        fun testBlockDecls_WhileBody_Empty() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("single declaration")
        fun testBlockDecls_WhileBody_Single() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple declarations")
        fun testBlockDecls_WhileBody_Multiple() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl_Multiple.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/WhileBody_BlockVarDecl_Multiple.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (156) BlockVarDecl")
    internal inner class BlockVarDecl {
        @Test
        @DisplayName("block variable declarations")
        fun testBlockVarDecl() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("block variable declarations; compact whitespace")
        fun testBlockVarDecl_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing variable indicator")
        fun testBlockVarDecl_MissingVarIndicator() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarIndicator.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarIndicator.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing VarName")
        fun testBlockVarDecl_MissingVarName() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing semicolon")
        fun testBlockVarDecl_MissingSemicolon() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("declare keyword as NCName function call")
        fun testBlockVarDecl_DeclareAsFunctionCall_NCName() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_NCName.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_NCName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("declare keyword as QName prefix")
        fun testBlockVarDecl_DeclareAsFunctionCall_QNamePrefix() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_QNamePrefix.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_DeclareAsFunctionCall_QNamePrefix.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("type declaration")
        fun testBlockVarDecl_TypeDeclaration() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("type declaration; missing semicolon")
        fun testBlockVarDecl_TypeDeclaration_MissingSemicolon() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration_MissingSemicolon.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_TypeDeclaration_MissingSemicolon.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("variable value")
        fun testBlockVarDecl_Assignment() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("variable value; recover using '=' instead of ':='")
        fun testBlockVarDecl_Assignment_Equals() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_Equals.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_Equals.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("variable value; missing value Expr")
        fun testBlockVarDecl_Assignment_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_Assignment_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple variables in a declaration")
        fun testBlockVarDecl_MultipleVars() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("multiple variables in a declaration; trailing comma")
        fun testBlockVarDecl_MultipleVars_NothingAfterComma() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars_NothingAfterComma.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl_MultipleVars_NothingAfterComma.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (158) AssignmentExpr")
    internal inner class AssignmentExpr {
        @Test
        @DisplayName("assignment expression")
        fun testAssignmentExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("assignment expression; compact whitespace")
        fun testAssignmentExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing VarName")
        fun testAssignmentExpr_MissingVarName() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingVarName.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingVarName.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing value Expr")
        fun testAssignmentExpr_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("assignment expression; nested")
        fun testAssignmentExpr_Nested() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/AssignmentExpr_Nested.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr_Nested.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (159) ExitExpr")
    internal inner class ExitExpr {
        @Test
        @DisplayName("exit expression")
        fun testExitExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("exit expression; compact whitespace")
        fun testExitExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing return keyword")
        fun testExitExpr_MissingReturningKeyword() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingReturningKeyword.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingReturningKeyword.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing Expr")
        fun testExitExpr_MissingExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/ExitExpr_MissingExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (160) WhileExpr")
    internal inner class WhileExpr {
        @Test
        @DisplayName("while expression")
        fun testWhileExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("while expression; compact whitespace")
        fun testWhileExpr_CompactWhitespace() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_CompactWhitespace.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_CompactWhitespace.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing conditional Expr")
        fun testWhileExpr_MissingConditionExpr() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingConditionExpr.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingConditionExpr.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("missing closing parenthesis")
        fun testWhileExpr_MissingClosingParenthesis() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingClosingParenthesis.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/WhileExpr_MissingClosingParenthesis.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("while keyword as NCName function call; no arguments")
        fun testFunctionCall_WhileKeyword_NoParams() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("while keyword as NCName function call; single argument")
        fun testFunctionCall_WhileKeyword_SingleParam() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_SingleParam.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_SingleParam.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }

        @Test
        @DisplayName("while keyword as NCName function call; multiple arguments")
        fun testFunctionCall_WhileKeyword_MultipleParams() {
            val expected = loadResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_MultipleParams.txt")
            val actual = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_MultipleParams.xq")
            assertThat(actual.toPsiTreeString(), `is`(expected))
        }
    }
}
