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
import uk.co.reecedunn.intellij.plugin.xquery.inspections.xpath.XPST0003.ReservedFunctionNameInspection
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.tests.inspections.InspectionTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class ReservedFunctionNameInspectionTest : InspectionTestCase() {
    // region Inspection Details

    @Test
    fun testDescription() {
        val inspection = ReservedFunctionNameInspection()
        assertThat(inspection.loadDescription(), `is`(notNullValue()))
    }

    // endregion
    // region FunctionCall
    // region MarkLogic 8.0 Reserved Function Names

    @Test
    fun testFunctionCall_MarkLogic80ReservedFunctionName_XQuery10() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic70() {
        settings.implementationVersion = "marklogic/v7"
        val file = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testFunctionCall_MarkLogic80ReservedFunctionName_MarkLogic80() {
        settings.implementationVersion = "marklogic/v8"
        val file = parseResource("tests/parser/marklogic-8.0/NodeTest_AnyArrayNodeTest_FunctionCallLike.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region Scripting 1.0 Reserved Function Names

    @Test
    fun testFunctionCall_Scripting10ReservedFunctionName_XQuery10() {
        settings.implementationVersion = "saxon/HE/v9.5"
        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testFunctionCall_Scripting10ReservedFunctionName_W3C() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionCall_WhileKeyword_NoParams.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
    }

    // endregion
    // endregion
    // region FunctionDecl
    // region MarkLogic 8.0 Reserved Function Names

    @Test
    fun testFunctionDecl_MarkLogic80ReservedFunctionName_XQuery10() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic70() {
        settings.implementationVersion = "marklogic/v7"
        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testFunctionDecl_MarkLogic80ReservedFunctionName_MarkLogic80() {
        settings.implementationVersion = "marklogic/v8"
        val file = parseResource("tests/psi/marklogic-8.0/FunctionDecl_ReservedKeyword_ArrayNode.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Reserved MarkLogic 8.0 keyword used as a function name."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_ARRAY_NODE))
    }

    // endregion
    // region Scripting 1.0 Reserved Function Names

    @Test
    fun testFunctionDecl_Scripting10ReservedFunctionName_XQuery10() {
        settings.implementationVersion = "saxon/HE/v9.5"
        val file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testFunctionDecl_Scripting10ReservedFunctionName_W3C() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/psi/xquery-sx-1.0/FunctionDecl_ReservedKeyword_While.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
    }

    // endregion
    // endregion
    // region NamedFunctionRef
    // region XQuery 1.0 Reserved Function Names

    @Test
    fun testNamedFunctionRef_XQuery10ReservedFunctionName() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Reserved XQuery 1.0 keyword used as a function name."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_IF))
    }

    // endregion
    // region XQuery 3.0 Reserved Function Names

    @Test
    fun testNamedFunctionRef_XQuery30ReservedFunctionName() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/psi/xquery-3.0/NamedFunctionRef_ReservedKeyword_Function.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Reserved XQuery 3.0 keyword used as a function name."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_FUNCTION))
    }

    // endregion
    // region Scripting 1.0 Reserved Function Names

    @Test
    fun testNamedFunctionRef_Scripting10ReservedFunctionName_XQuery10() {
        settings.implementationVersion = "saxon/HE/v9.5"
        val file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(0))
    }

    @Test
    fun testNamedFunctionRef_Scripting10ReservedFunctionName_W3C() {
        settings.implementationVersion = "w3c/spec/v1ed"
        val file = parseResource("tests/psi/xquery-sx-1.0/NamedFunctionRef_ReservedKeyword_While.xq")

        val problems = inspect(file, ReservedFunctionNameInspection())
        assertThat(problems, `is`(notNullValue()))
        assertThat(problems!!.size, `is`(1))

        assertThat(problems[0].highlightType, `is`(ProblemHighlightType.GENERIC_ERROR_OR_WARNING))
        assertThat(problems[0].descriptionTemplate, `is`("XPST0003: Reserved XQuery Scripting Extension 1.0 keyword used as a function name."))
        assertThat(problems[0].psiElement.node.elementType, `is`(XQueryTokenType.K_WHILE))
    }

    // endregion
    // endregion
}
