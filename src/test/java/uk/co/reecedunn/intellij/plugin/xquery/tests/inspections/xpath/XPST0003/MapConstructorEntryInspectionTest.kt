/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0003.MapConstructorEntryInspection
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class MapConstructorEntryInspectionTest : InspectionTestCase() {
    // region Inspection Details

    @Test
    fun testDescription() {
        val inspection = MapConstructorEntryInspection()
        assertThat(inspection.loadDescription(), `is`(notNullValue()))
    }

    // endregion
    // region XQuery 3.1

    @Test
    fun testXQuery31_Map_XQuerySeparator() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_3_1_20170321.label
        val file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testXQuery31_Map_SaxonSeparator() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_3_1_20170321.label
        val file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6)."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.ASSIGN_EQUAL))
    }

    @Test
    fun testXQuery31_Map_NoValueAssignmentOperator() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_3_1_20170321.label
        val file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    // endregion
    // region Saxon 9.4

    @Test
    fun testSaxon94_Map_SaxonSeparator() {
        settings.implementationVersion = "saxon/EE/v9.5"
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/saxon-9.4/MapConstructorEntry.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testSaxon94_Map_XQuerySeparator() {
        settings.implementationVersion = "saxon/EE/v9.5"
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/parser/xquery-3.1/MapConstructorEntry.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6)."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.QNAME_SEPARATOR))
    }

    @Test
    fun testSaxon94_Map_NoValueAssignmentOperator() {
        settings.implementationVersion = "saxon/EE/v9.5"
        settings.XQueryVersion = XQuery.REC_3_0_20140408.label
        val file = parseResource("tests/psi/xquery-3.1/MapConstructorEntry_NoValueAssignmentOperator.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    // endregion
    // region MarkLogic 8.0

    @Test
    fun testMarkLogic80_ObjectNode_MarkLogicSeparator() {
        settings.implementationVersion = "marklogic/v8"
        val file = parseResource("tests/parser/marklogic-8.0/MapConstructorEntry.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testMarkLogic80_ObjectNode_SaxonSeparator() {
        settings.implementationVersion = "marklogic/v8"
        val file = parseResource("tests/psi/marklogic-8.0/MapConstructorEntry_SaxonSeparator.xq")

        val problems = inspect(file, MapConstructorEntryInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Expected ':' (XQuery 3.1/MarkLogic) or ':=' (Saxon 9.4-9.6)."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.ASSIGN_EQUAL))
    }

    // endregion
}
