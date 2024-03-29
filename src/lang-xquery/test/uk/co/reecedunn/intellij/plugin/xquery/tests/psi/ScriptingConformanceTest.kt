/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.intellij.lang.ScriptingSpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginCompatibilityAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("RedundantVisibilityModifier")
@DisplayName("XQuery Scripting Extensions 1.0 - Implementation Conformance Checks")
class ScriptingConformanceTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("ScriptingConformanceTest")

    private val res = ResourceVirtualFileSystem(this::class.java.classLoader)

    fun parseResource(resource: String): XQueryModule = res.toPsiFile(resource, project)

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (32) ApplyExpr")
    internal inner class ApplyExpr {
        @Test
        @DisplayName("single expression; semicolon at end")
        fun testApplyExpr_Single_Semicolon() {
            val file = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd.xq")

            val applyExpr = file.walkTree().filterIsInstance<ScriptingApplyExpr>().first()
            val conformance = applyExpr as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.SEPARATOR))
        }

        @Test
        @DisplayName("multiple expressions; semicolon at end")
        fun testApplyExpr_Multiple() {
            val file = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd.xq")

            val applyExpr = file.walkTree().filterIsInstance<ScriptingApplyExpr>().first()
            val conformance = applyExpr as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.SEPARATOR))
        }

        @Test
        @DisplayName("multiple expressions; no semicolon at end")
        fun testApplyExpr_Multiple_NoSemicolonAtEnd() {
            val file = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_NoSemicolonAtEnd.xq")

            val applyExpr = file.walkTree().filterIsInstance<ScriptingApplyExpr>().first()
            val conformance = applyExpr as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.SEPARATOR))
        }
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (158) AssignmentExpr")
    fun testAssignmentExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr.xq")

        val assignmentExpr = file.descendants().filterIsInstance<ScriptingAssignmentExpr>().first()
        val conformance = assignmentExpr as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (153) BlockExpr")
    fun testBlockExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq")

        val blockExpr = file.descendants().filterIsInstance<ScriptingBlockExpr>().first()
        val conformance = blockExpr as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_BLOCK))
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (156) BlockVarDecl")
    fun testBlockVarDecl() {
        val file = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq")

        val blockVarDeclPsi = file.walkTree().filterIsInstance<ScriptingBlockVarDecl>().first()
        val conformance = blockVarDeclPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_DECLARE))
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (159) ExitExpr")
    fun testExitExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/ExitExpr.xq")

        val exitExpr = file.descendants().filterIsInstance<ScriptingExitExpr>().first()
        val conformance = exitExpr as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_EXIT))
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (26) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("simple annotation")
        fun testFunctionDecl_Simple() {
            val file = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryFunctionDecl>().first()
            val scriptingCompatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = scriptingCompatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SIMPLE))
        }

        @Test
        @DisplayName("sequential annotation")
        fun testFunctionDecl_Sequential() {
            val file = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryFunctionDecl>().first()
            val scriptingCompatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = scriptingCompatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_SEQUENTIAL))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (30) QueryBody ; XQuery Scripting Extensions 1.0 EBNF (32) ApplyExpr")
    internal inner class QueryBody {
        @Test
        @DisplayName("single expression; semicolon at end")
        fun testQueryBody_Single_Semicolon() {
            val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

            val applyExpr = file.walkTree().filterIsInstance<ScriptingApplyExpr>().first()
            val conformance = applyExpr as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryElementType.TRANSACTION_SEPARATOR))
        }

        @Test
        @DisplayName("multiple expressions; semicolon at end")
        fun testQueryBody_Multiple() {
            val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

            val applyExpr = file.walkTree().filterIsInstance<ScriptingApplyExpr>().first()
            val conformance = applyExpr as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryElementType.TRANSACTION_SEPARATOR))
        }

        @Test
        @DisplayName("multiple expressions; no semicolon at end")
        fun testQueryBody_Multiple_NoSemicolonAtEnd() {
            val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

            val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().last()
            val conformance = applyExpr as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(0))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryElementType.TRANSACTION_SEPARATOR))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (24) VarDecl")
    internal inner class VarDecl {
        @Test
        @DisplayName("assignable annotation")
        fun testVarDecl_Assignable() {
            val file = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryVarDecl>().first()
            val scriptingCompatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = scriptingCompatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_ASSIGNABLE))
        }

        @Test
        @DisplayName("unassignable annotation")
        fun testVarDecl_Unassignable() {
            val file = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryVarDecl>().first()
            val scriptingCompatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = scriptingCompatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_UNASSIGNABLE))
        }
    }

    @Test
    @DisplayName("XQuery Scripting Extensions 1.0 EBNF (160) WhileExpr")
    fun testWhileExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")

        val whileExpr = file.descendants().filterIsInstance<ScriptingWhileExpr>().first()
        val conformance = whileExpr as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(ScriptingSpec.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_WHILE))
    }
}
