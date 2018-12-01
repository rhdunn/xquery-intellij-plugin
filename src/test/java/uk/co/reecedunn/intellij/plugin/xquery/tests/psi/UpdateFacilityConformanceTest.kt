/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginCompatibilityAnnotation
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import uk.co.reecedunn.intellij.plugin.intellij.lang.BaseX
import uk.co.reecedunn.intellij.plugin.intellij.lang.UpdateFacilitySpec
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery Update Facility 3.0 - Implementation Conformance Checks")
private class UpdateFacilityConformanceTest : ParserTestCase() {
    fun parseResource(resource: String): XQueryModule {
        val file = ResourceVirtualFile(UpdateFacilityConformanceTest::class.java.classLoader, resource)
        return file.toPsiFile(myProject)!!
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 EBNF (27) CompatibilityAnnotation")
    internal inner class CompatibilityAnnotation {
        @Test
        @DisplayName("function declaration")
        fun testCompatibilityAnnotation_FunctionDecl() {
            val file = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(
                conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UPDATING)
            )
        }

        @Test
        @DisplayName("variable declaration")
        fun testCompatibilityAnnotation_VarDecl() {
            val file = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.NOTE_3_0_20170124))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(
                conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UPDATING)
            )
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_DELETE))
    }

    @Nested
    @DisplayName("XQuery Update Facility 1.0 EBNF (26) FunctionDecl")
    internal inner class FunctionDecl {
        @Test
        @DisplayName("updating annotation")
        fun testFunctionDecl_Updating() {
            val file = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")

            val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
            val compatibilityAnnotationPsi =
                annotatedDeclPsi.children().filterIsInstance<PluginCompatibilityAnnotation>().first()
            val conformance = compatibilityAnnotationPsi as VersionConformance

            assertThat(conformance.requiresConformance.size, `is`(1))
            assertThat(conformance.requiresConformance[0], `is`(UpdateFacilitySpec.REC_1_0_20110317))

            assertThat(conformance.conformanceElement, `is`(notNullValue()))
            assertThat(
                conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UPDATING)
            )
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_INSERT))
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_RENAME))
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_REPLACE))
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_REVALIDATION))
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_COPY))
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_TRANSFORM))
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
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_INVOKE))
    }
}
