/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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

import com.intellij.psi.tree.IElementType
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.core.extensions.descendants
import uk.co.reecedunn.intellij.plugin.xquery.lang.BaseX
import uk.co.reecedunn.intellij.plugin.xquery.lang.UpdateFacility
import uk.co.reecedunn.intellij.plugin.xquery.lang.Version
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance

class UpdateFacilityPsiTest : ParserTestCase() {
    // region XQueryConformance
    // region CompatibilityAnnotation

    fun testCompatibilityAnnotation_FunctionDecl() {
        val file = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val compatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<UpdateFacilityCompatibilityAnnotation>().first()
        val conformance = compatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_UPDATING))
    }

    fun testCompatibilityAnnotation_VarDecl() {
        val file = parseResource("tests/parser/xquery-update-3.0/CompatibilityAnnotation_VarDecl.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val compatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<UpdateFacilityCompatibilityAnnotation>().first()
        val conformance = compatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.NOTE_3_0_20170124))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_UPDATING))
    }

    // endregion
    // region DeleteExpr

    fun testDeleteExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")!!

        val deleteExprPsi = file.descendants().filterIsInstance<UpdateFacilityDeleteExpr>().first()
        val conformance = deleteExprPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_DELETE))
    }

    // endregion
    // region FunctionDecl

    fun testFunctionDecl_Updating() {
        val file = parseResource("tests/parser/xquery-update-1.0/FunctionDecl_Updating.xq")!!

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val compatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<UpdateFacilityCompatibilityAnnotation>().first()
        val conformance = compatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_UPDATING))
    }

    // endregion
    // region InsertExpr

    fun testInsertExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/InsertExpr_Node.xq")!!

        val insertExprPsi = file.descendants().filterIsInstance<UpdateFacilityInsertExpr>().first()
        val conformance = insertExprPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_INSERT))
    }

    // endregion
    // region RenameExpr

    fun testRenameExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/RenameExpr.xq")!!

        val renameExprPsi = file.descendants().filterIsInstance<UpdateFacilityRenameExpr>().first()
        val conformance = renameExprPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_RENAME))
    }

    // endregion
    // region ReplaceExpr

    fun testReplaceExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/ReplaceExpr.xq")!!

        val replaceExprPsi = file.descendants().filterIsInstance<UpdateFacilityReplaceExpr>().first()
        val conformance = replaceExprPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_REPLACE))
    }

    // endregion
    // region RevalidationDecl

    fun testRevalidationDecl() {
        val file = parseResource("tests/parser/xquery-update-1.0/RevalidationDecl.xq")!!

        val revalidationDeclPsi = file.descendants().filterIsInstance<UpdateFacilityRevalidationDecl>().first()
        val conformance = revalidationDeclPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_REVALIDATION))
    }

    // endregion
    // region TransformExpr (CopyModifyExpr)

    fun testTransformExpr() {
        val file = parseResource("tests/parser/xquery-update-1.0/TransformExpr.xq")!!

        val transformExprPsi = file.descendants().filterIsInstance<UpdateFacilityCopyModifyExpr>().first()
        val conformance = transformExprPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.REC_1_0_20110317))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_COPY))
    }

    // endregion
    // region TransformWithExpr

    fun testTransformWithExpr() {
        val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")!!

        val transformWithExprPsi = file.descendants().filterIsInstance<UpdateFacilityTransformWithExpr>().first()
        val conformance = transformWithExprPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(2))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.NOTE_3_0_20170124))
        assertThat(conformance.requiresConformance[1], `is`<Version>(BaseX.VERSION_8_5))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_TRANSFORM))
    }

    // endregion
    // region UpdatingFunctionCall

    fun testUpdatingFunctionCall() {
        val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")!!

        val updatingFunctionCallPsi = file.descendants().filterIsInstance<UpdateFacilityUpdatingFunctionCall>().first()
        val conformance = updatingFunctionCallPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`<Version>(UpdateFacility.NOTE_3_0_20170124))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`<IElementType>(XQueryTokenType.K_INVOKE))
    }

    // endregion
    // endregion
}
