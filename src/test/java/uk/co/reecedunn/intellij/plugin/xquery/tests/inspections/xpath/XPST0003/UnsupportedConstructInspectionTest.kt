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
@file:Suppress("PackageName")

package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xpath.XPST0003

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0003.UnsupportedConstructInspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.core.codeInspection.InspectionTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class UnsupportedConstructInspectionTest : InspectionTestCase() {
    // region Inspection Details

    @Test
    fun testDescription() {
        val inspection = UnsupportedConstructInspection()
        assertThat(inspection.loadDescription(), `is`(notNullValue()))
    }

    // endregion
    // region XQuery Conformance

    @Test
    fun testXQuery30VersionDeclInXQuery10() {
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testXQuery30VersionDecl() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-3.0/VersionDecl_EncodingOnly.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    // endregion
    // region Update Facility Conformance

    @Test
    fun testUpdateFacility10_ProductConformsToSpecification() {
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        settings.implementationVersion = "w3c/spec/v1ed"

        val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testUpdateFacility10_ProductDoesNotConformToSpecification() {
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        settings.implementationVersion = "marklogic/v7.0"

        val file = parseResource("tests/parser/xquery-update-1.0/DeleteExpr_Node.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_DELETE))
    }

    @Test
    fun testUpdateFacility30_ProductConformsToSpecification() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        settings.implementationVersion = "w3c/spec/v1ed"

        val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testUpdateFacility30_ProductDoesNotConformToSpecification() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        settings.implementationVersion = "saxon/EE/v9.5" // Supports Update Facility 1.0, not 3.0

        val file = parseResource("tests/parser/xquery-update-3.0/UpdatingFunctionCall.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0 constructs."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_INVOKE))
    }

    @Test
    fun testUpdateFacilityBaseX_ProductConformsToSpecification() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        settings.implementationVersion = "basex/v8.6"

        val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testUpdateFacilityBaseX_ProductDoesNotConformToSpecification() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        settings.implementationVersion = "saxon/EE/v9.5" // Supports Update Facility 1.0, not 3.0

        val file = parseResource("tests/parser/xquery-update-3.0/TransformWithExpr.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Saxon 9.5 does not support XQuery Update Facility 3.0, or BaseX 8.5 constructs."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_TRANSFORM))
    }

    // endregion
    // region Scripting Conformance

    @Test
    fun testScripting10_ProductConformsToSpecification() {
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        settings.implementationVersion = "w3c/spec/v1ed"

        val file = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testScripting10_ProductDoesNotConformToSpecification() {
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        settings.implementationVersion = "marklogic/v7.0"

        val file = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: MarkLogic 7.0 does not support XQuery Scripting Extension 1.0 constructs."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_BLOCK))
    }

    // endregion
    // region BaseX Conformance

    @Test
    fun testBaseX_ProductConformsToSpecification() {
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        settings.implementationVersion = "basex/v8.5"

        val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testBaseX_ProductDoesNotConformToSpecification() {
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        settings.implementationVersion = "marklogic/v7.0"

        val file = parseResource("tests/parser/basex-7.8/UpdateExpr.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(2))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: MarkLogic 7.0 does not support BaseX 7.8 constructs."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_UPDATE))

        assertThat(problems[1].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[1].descriptionTemplate, `is`("XPST0003: MarkLogic 7.0 does not support XQuery Update Facility 1.0 constructs."))
        assertThat(problems[1].psiElement.node.elementType, `is`(XQueryTokenType.K_DELETE))
    }

    // endregion
    // region MarkLogic Conformance

    @Test
    fun testMarkLogic09ml_ProductConformsToSpecification() {
        settings.XQueryVersion = XQuery.MARKLOGIC_0_9.label
        settings.implementationVersion = "marklogic/v7.0"

        val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testMarkLogic09ml_ProductDoesNotConformToSpecification() {
        settings.XQueryVersion = XQuery.MARKLOGIC_0_9.label
        settings.implementationVersion = "saxon/EE/v9.5"

        val file = parseResource("tests/parser/marklogic-6.0/BinaryConstructor.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Saxon 9.5 does not support MarkLogic 4.0, or XQuery 0.9-ml constructs."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_BINARY))
    }

    @Test
    fun testMarkLogic10ml_ProductConformsToSpecification() {
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        settings.implementationVersion = "marklogic/v7.0"

        val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testMarkLogic10ml_ProductDoesNotConformToSpecification() {
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        settings.implementationVersion = "saxon/EE/v9.5"

        val file = parseResource("tests/parser/marklogic-7.0/SchemaRootTest.xq")

        val problems = inspect(file, UnsupportedConstructInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Saxon 9.5 does not support MarkLogic 7.0 constructs."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_SCHEMA_ROOT))
    }

    // endregion
}
