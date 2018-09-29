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

package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xpath.XPST0081

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0081.UnboundQNamePrefixInspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.core.codeInspection.InspectionTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class UnboundQNamePrefixInspectionTest : InspectionTestCase() {
    // region Inspection Details

    @Test
    fun testDescription() {
        val inspection = UnboundQNamePrefixInspection()
        assertThat(inspection.loadDescription(), `is`(notNullValue()))
    }

    // endregion
    // region Predefined Namespaces
    // region xmlns

    @Test
    fun testXmlns() {
        val file = parseResource("tests/inspections/xpath/XPST0081/xmlns.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    // endregion
    // region XQuery

    @Test
    fun testBuiltinXQuery() {
        val file = parseResource("tests/inspections/xpath/XPST0081/builtin-xquery.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testBuiltinXQuery31() {
        val file = parseResource("tests/inspections/xpath/XPST0081/builtin-xquery-3.1.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    // endregion
    // region MarkLogic

    @Test
    fun testBuiltinMarkLogic() {
        settings.implementationVersion = "marklogic/v8"
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        val file = parseResource("tests/inspections/xpath/XPST0081/builtin-marklogic.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testBuiltinMarkLogicNotTargettingMarkLogic() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/inspections/xpath/XPST0081/builtin-marklogic.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0081: Cannot resolve namespace prefix."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(problems[0].psiElement.text, `is`("xdmp"))
    }

    // endregion
    // endregion
    // region Inspection Sources

    @Test
    fun testQName() {
        val file = parseResource("tests/inspections/xpath/XPST0081/ModuleDecl_QName_UnboundPrefix.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0081: Cannot resolve namespace prefix."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(problems[0].psiElement.text, `is`("x"))
    }

    // endregion
    // region Namespace Providers
    // region ModuleDecl

    @Test
    fun testModuleDecl_BoundPrefix() {
        val file = parseResource("tests/inspections/xpath/XPST0081/ModuleDecl_QName.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testModuleDecl_UnboundPrefix() {
        val file = parseResource("tests/inspections/xpath/XPST0081/ModuleDecl_QName_UnboundPrefix.xq")

        val problems = inspect(file, UnboundQNamePrefixInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0081: Cannot resolve namespace prefix."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.NCNAME))
        assertThat(problems[0].psiElement.text, `is`("x"))
    }

    // endregion
    // endregion
}
