// Copyright (C) 2016-2018, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerExtension
import uk.co.reecedunn.intellij.plugin.core.tests.lang.registerFileType
import uk.co.reecedunn.intellij.plugin.core.tests.parser.LanguageParserTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFileSystem
import uk.co.reecedunn.intellij.plugin.intellij.lang.BaseX
import uk.co.reecedunn.intellij.plugin.intellij.lang.UpdateFacilitySpec
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.lang.XPath
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathASTFactory
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginCompatibilityAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryVarDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lang.fileTypes.XQueryFileType
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryASTFactory
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryParserDefinition
import uk.co.reecedunn.intellij.plugin.xquery.project.settings.XQueryProjectSettings

@Suppress("RedundantVisibilityModifier")
@DisplayName("XQuery Update Facility 3.0 - Implementation Conformance Checks")
class UpdateFacilityConformanceTest : IdeaPlatformTestCase(), LanguageParserTestCase<XQueryModule> {
    override val pluginId: PluginId = PluginId.getId("UpdateFacilityConformanceTest")
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

    @Nested
    @DisplayName("XQuery Update Facility 3.0 EBNF (27) CompatibilityAnnotation")
    internal inner class CompatibilityAnnotation {
        @Test
        @DisplayName("function declaration")
        fun testCompatibilityAnnotation_FunctionDecl() {
            val file = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryFunctionDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_UPDATING))
        }

        @Test
        @DisplayName("variable declaration")
        fun testCompatibilityAnnotation_VarDecl() {
            val file = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryVarDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.NOTE_3_0_20170124))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_UPDATING))
        }
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (144) DeleteExpr")
    fun testDeleteExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")

        val deleteExprPsi = file.descendants().filterIsInstance<UpdateFacilityDeleteExpr>().first()
        val conformance = deleteExprPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_DELETE))
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (26) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("updating annotation")
        fun testFunctionDecl_Updating() {
            val file = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryFunctionDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_UPDATING))
        }
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (143) InsertExpr")
    fun testInsertExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq")

        val insertExprPsi = file.descendants().filterIsInstance<UpdateFacilityInsertExpr>().first()
        val conformance = insertExprPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_INSERT))
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (145) ReplaceExpr")
    fun testRenameExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/RenameExpr.xq")

        val renameExprPsi = file.descendants().filterIsInstance<UpdateFacilityRenameExpr>().first()
        val conformance = renameExprPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_RENAME))
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (145) ReplaceExpr")
    fun testReplaceExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq")

        val replaceExprPsi = file.descendants().filterIsInstance<UpdateFacilityReplaceExpr>().first()
        val conformance = replaceExprPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_REPLACE))
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (141) RevalidationDecl")
    fun testRevalidationDecl() {
        val file = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq")

        val revalidationDeclPsi = file.descendants().filterIsInstance<UpdateFacilityRevalidationDecl>().first()
        val conformance = revalidationDeclPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_REVALIDATION))
    }

    @Test
    @DisplayName("XQuery Update Facility 1.0 EBNF (150) TransformExpr ; XQuery Update Facility 3.0 EBNF (208) CopyModifyExpr")
    fun testTransformExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/TransformExpr.xq")

        val transformExprPsi = file.descendants().filterIsInstance<UpdateFacilityCopyModifyExpr>().first()
        val conformance = transformExprPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_COPY))
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (97) TransformWithExpr")
    fun testTransformWithExpr() {
        val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")

        val transformWithExprPsi = file.descendants().filterIsInstance<UpdateFacilityTransformWithExpr>().first()
        val conformance = transformWithExprPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.NOTE_3_0_20170124))
        assertThat(conformance.requiresConformance[1], `is`(BaseX.VERSION_8_5))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_TRANSFORM))
    }

    @Test
    @DisplayName("XQuery Update Facility 3.0 EBNF (207) UpdatingFunctionCall")
    fun testUpdatingFunctionCall() {
        val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

        val updatingFunctionCallPsi = file.descendants().filterIsInstance<UpdateFacilityUpdatingFunctionCall>().first()
        val conformance = updatingFunctionCallPsi as VersionConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.NOTE_3_0_20170124))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.elementType, `is`(XQueryTokenType.K_INVOKE))
    }
}
