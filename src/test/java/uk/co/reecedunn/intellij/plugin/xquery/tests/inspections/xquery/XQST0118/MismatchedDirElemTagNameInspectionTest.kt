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

package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xquery.XQST0118

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0118.MismatchedDirElemTagNameInspection
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class MismatchedDirElemTagNameInspectionTest : InspectionTestCase() {
    // region Inspection Details

    @Test
    fun testDescription() {
        val inspection = MismatchedDirElemTagNameInspection()
        assertThat(inspection.loadDescription(), `is`(notNullValue()))
    }

    // endregion
    // region DirElemConstructor :: NCName

    @Test
    fun testNCName_MatchedTags() {
        val file = parseResource("tests/inspections/xquery/XQST0118/NCName_MatchedTags.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testNCName_SelfClosing() {
        val file = parseResource("tests/inspections/xquery/XQST0118/NCName_SelfClosing.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testNCName_MismatchedTags() {
        val file = parseResource("tests/inspections/xquery/XQST0118/NCName_MismatchedTags.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0118: The closing tag 'b' does not match the open tag 'a'."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.NCNAME))
        assertThat(problems[0].psiElement.text, `is`("b"))
    }

    // endregion
    // region DirElemConstructor :: QName

    @Test
    fun testQName_MatchedPrefixAndLocalName() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_CompactWhitespace.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testQName_SelfClosing() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_SelfClosing.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testQName_MissingClosingTag() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_MissingClosingTag.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testQName_InvalidOpeningTag() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteOpenTagQName.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testQName_InvalidClosingTag() {
        val file = parseResource("tests/parser/xquery-1.0/DirElemConstructor_IncompleteCloseTagQName.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testQName_MismatchedPrefix() {
        val file = parseResource("tests/inspections/xquery/XQST0118/QName_MismatchedPrefix.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0118: The closing tag 'c:b' does not match the open tag 'a:b'."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.QNAME))
        assertThat(problems[0].psiElement.text, `is`("c:b"))
    }

    @Test
    fun testQName_MismatchedLocalName() {
        val file = parseResource("tests/inspections/xquery/XQST0118/QName_MismatchedLocalName.xq")

        val problems = inspect(file, MismatchedDirElemTagNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0118: The closing tag 'a:c' does not match the open tag 'a:b'."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.QNAME))
        assertThat(problems[0].psiElement.text, `is`("a:c"))
    }

    // endregion
}
