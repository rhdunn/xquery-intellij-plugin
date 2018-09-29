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

package uk.co.reecedunn.intellij.plugin.xpath.codeInspection.xpst.XPST0003

import com.intellij.codeInspection.ProblemHighlightType
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.core.tests.codeInspection.InspectionTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class FinalStatementSemicolonInspectionTest : InspectionTestCase() {
    // region Inspection Details

    @Test
    fun testDescription() {
        val inspection = FinalStatementSemicolonInspection()
        assertThat(inspection.loadDescription(), `is`(notNullValue()))
    }

    // endregion
    // region MarkLogic 6.0

    @Test
    fun testMarkLogic_Single_NoSemicolon() {
        settings.implementationVersion = "marklogic/v6.0"
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testMarkLogic_Single_Semicolon() {
        settings.implementationVersion = "marklogic/v6.0"
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testMarkLogic_Multiple_SemicolonAtEnd() {
        settings.implementationVersion = "marklogic/v6.0"
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testMarkLogic_Multiple_NoSemicolonAtEnd() {
        settings.implementationVersion = "marklogic/v6.0"
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testMarkLogic_WithProlog() {
        settings.implementationVersion = "marklogic/v6.0"
        settings.XQueryVersion = XQuery.MARKLOGIC_1_0.label
        val file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    // endregion
    // region Scripting Extension 1.0

    @Test
    fun testScripting_Single_NoSemicolon() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testScripting_Single_Semicolon() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testScripting_Multiple_SemicolonAtEnd() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testScripting_Multiple_NoSemicolonAtEnd() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: XQuery Scripting Extension 1.0 requires ';' at the end of each statement."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.INTEGER_LITERAL))
    }

    @Test
    fun testScripting_WithProlog() {
        settings.implementationVersion = "w3c/spec/v1ed"
        settings.XQueryVersion = XQuery.REC_1_0_20070123.label
        val file = parseResource("tests/parser/marklogic-6.0/Transactions_WithVersionDecl.xq")

        val problems = inspect(file, FinalStatementSemicolonInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    // endregion
}
