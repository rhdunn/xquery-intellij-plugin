/*
 * Copyright (C) 2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.xquery.XQST0031

import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xquery.XQST0031.UnsupportedXQueryVersionInspection
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat

class UnsupportedXQueryVersionInspectionTest : InspectionTestCase() {
    // region Inspection Details

    fun testDisplayName() {
        val inspection = UnsupportedXQueryVersionInspection()
        assertThat(inspection.displayName, `is`(notNullValue()))
    }

    fun testDescription() {
        val inspection = UnsupportedXQueryVersionInspection()
        assertThat(inspection.loadDescription(), `is`(notNullValue()))
    }

    // endregion
    // region Invalid XQuery Versions

    fun testNoVersionDecl() {
        val file = parseResource("tests/inspections/xquery/XQST0031/no-versiondecl.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    fun testEmptyVersionDecl() {
        val file = parseResource("tests/inspections/xquery/XQST0031/empty-version.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"\""))
    }

    fun testXQueryVersion_UNSUPPORTED() {
        // XQueryVersion.parse("3.99") returns XQueryVersion.UNSUPPORTED
        val file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.99.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"3.99\""))
    }

    fun testUnsupportedVersion() {
        // XQueryVersion.parse("9.7") returns XQueryVersion.VERSION_9_7, but that is not a valid XQuery version.
        val file = parseResource("tests/inspections/xquery/XQST0031/xquery-9.7.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"9.7\""))
    }

    // endregion
    // region MarkLogic

    fun testSupportedVersion_MarkLogic() {
        settings.implementationVersion = "marklogic/v8"

        val file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    fun testUnsupportedVersion_MarkLogic() {
        settings.implementationVersion = "marklogic/v8"

        val file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.0.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"3.0\""))
    }

    // endregion
    // region W3C

    fun testSupportedVersion_W3C() {
        settings.implementationVersion = "w3c/spec"

        val file = parseResource("tests/inspections/xquery/XQST0031/xquery-3.0.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    fun testUnsupportedVersion_W3C() {
        settings.implementationVersion = "w3c/spec"

        val file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"1.0-ml\""))
    }

    // endregion
    // region MarkLogic Transactions

    fun testTransactions_SameVersion_MarkLogic() {
        settings.implementationVersion = "marklogic/v8"

        val file = parseResource("tests/inspections/xquery/XQST0031/xquery-1.0-ml.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    fun testTransactions_SameVersion_W3C() {
        settings.implementationVersion = "w3c/spec"

        val file = parseResource("tests/inspections/xquery/XQST0031/transaction-same-version.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(2))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"1.0-ml\""))

        assertThat(problems[1].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[1].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[1].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[1].psiElement.text, `is`("\"1.0-ml\""))
    }

    fun testTransactions_UnsupportedOtherVersion() {
        settings.implementationVersion = "marklogic/v8"

        val file = parseResource("tests/inspections/xquery/XQST0031/transaction-unsupported-other-version.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: The implementation does not support this XQuery version."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"0.2\""))
    }

    fun testTransactions_DifferentVersions() {
        settings.implementationVersion = "marklogic/v8"

        val file = parseResource("tests/inspections/xquery/XQST0031/transaction-different-version.xq")

        val problems = inspect(file!!, UnsupportedXQueryVersionInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR))
        assertThat(problems[0].descriptionTemplate, `is`("XQST0031: MarkLogic requires that XQuery versions are the same across different transactions."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryElementType.STRING_LITERAL))
        assertThat(problems[0].psiElement.text, `is`("\"0.9-ml\""))
    }

    // endregion
}
