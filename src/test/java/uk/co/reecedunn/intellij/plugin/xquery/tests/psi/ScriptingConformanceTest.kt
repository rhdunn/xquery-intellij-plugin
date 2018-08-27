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
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.scripting.ScriptingApplyExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParenthesizedExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryAnnotatedDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFunctionDecl
import uk.co.reecedunn.intellij.plugin.xquery.lang.Scripting
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryConformance
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class ScriptingConformanceTest : ParserTestCase() {
    // region ApplyExpr

    @Test
    fun testApplyExpr_Single_NoSemicolon() {
        val file = parseResource("tests/parser/xquery-1.0/ParenthesizedExpr.xq")

        val parenthesizedExpr = file.descendants().filterIsInstance<XPathParenthesizedExpr>().first()
        val applyExpr = parenthesizedExpr.children().filterIsInstance<ScriptingApplyExpr>().first()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryElementType.CONCAT_EXPR))
    }

    @Test
    fun testApplyExpr_Single_Semicolon() {
        val file = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Single_SemicolonAtEnd.xq")

        val parenthesizedExpr = file.descendants().filterIsInstance<XPathParenthesizedExpr>().first()
        val applyExpr = parenthesizedExpr.children().filterIsInstance<ScriptingApplyExpr>().first()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.SEPARATOR))
    }

    @Test
    fun testApplyExpr_Multiple() {
        val file = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_SemicolonAtEnd.xq")

        val parenthesizedExpr = file.descendants().filterIsInstance<XPathParenthesizedExpr>().first()
        val applyExpr = parenthesizedExpr.children().filterIsInstance<ScriptingApplyExpr>().first()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.SEPARATOR))
    }

    @Test
    fun testApplyExpr_Multiple_NoSemicolonAtEnd() {
        val file = parseResource("tests/parser/xquery-sx-1.0/ApplyExpr_Multiple_NoSemicolonAtEnd.xq")

        val parenthesizedExpr = file.descendants().filterIsInstance<XPathParenthesizedExpr>().first()
        val applyExpr = parenthesizedExpr.children().filterIsInstance<ScriptingApplyExpr>().last()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.SEPARATOR))
    }

    // endregion
    // region AssignmentExpr

    @Test
    fun testAssignmentExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/AssignmentExpr.xq")

        val assignmentExpr = file.descendants().filterIsInstance<ScriptingAssignmentExpr>().first()
        val conformance = assignmentExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.ASSIGN_EQUAL))
    }

    // endregion
    // region BlockExpr

    @Test
    fun testBlockExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/BlockExpr.xq")

        val blockExpr = file.descendants().filterIsInstance<ScriptingBlockExpr>().first()
        val conformance = blockExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_BLOCK))
    }

    // endregion
    // region BlockVarDecl

    @Test
    fun testBlockVarDecl() {
        val file = parseResource("tests/parser/xquery-sx-1.0/BlockVarDecl.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val functionDeclPsi = annotatedDeclPsi.children().filterIsInstance<XQueryFunctionDecl>().first()
        val blockPsi = functionDeclPsi.children().filterIsInstance<ScriptingBlock>().first()
        val blockDeclsPsi = blockPsi.children().filterIsInstance<ScriptingBlockDecls>().first()
        val blockVarDeclPsi = blockDeclsPsi.children().filterIsInstance<ScriptingBlockVarDecl>().first()
        val conformance = blockVarDeclPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_DECLARE))
    }

    // endregion
    // region ExitExpr

    @Test
    fun testExitExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/ExitExpr.xq")

        val exitExpr = file.descendants().filterIsInstance<ScriptingExitExpr>().first()
        val conformance = exitExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_EXIT))
    }

    // endregion
    // region FunctionDecl

    @Test
    fun testFunctionDecl_Simple() {
        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Simple.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val scriptingCompatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<ScriptingCompatibilityAnnotation>().first()
        val conformance = scriptingCompatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SIMPLE))
    }

    @Test
    fun testFunctionDecl_Sequential() {
        val file = parseResource("tests/parser/xquery-sx-1.0/FunctionDecl_Sequential.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val scriptingCompatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<ScriptingCompatibilityAnnotation>().first()
        val conformance = scriptingCompatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_SEQUENTIAL))
    }

    // endregion
    // region QueryBody (ApplyExpr)

    @Test
    fun testQueryBody_Single_NoSemicolon() {
        val file = parseResource("tests/parser/xquery-1.0/IntegerLiteral.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryElementType.CONCAT_EXPR))
    }

    @Test
    fun testQueryBody_Single_Semicolon() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_Single_SemicolonAtEnd.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryElementType.TRANSACTION_SEPARATOR))
    }

    @Test
    fun testQueryBody_Multiple() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_SemicolonAtEnd.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().first()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryElementType.TRANSACTION_SEPARATOR))
    }

    @Test
    fun testQueryBody_Multiple_NoSemicolonAtEnd() {
        val file = parseResource("tests/parser/xquery-sx-1.0/QueryBody_TwoExpr_NoSemicolonAtEnd.xq")

        val applyExpr = file.descendants().filterIsInstance<ScriptingApplyExpr>().last()
        val conformance = applyExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(0))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryElementType.TRANSACTION_SEPARATOR))
    }

    // endregion
    // region VarDecl

    @Test
    fun testVarDecl_Assignable() {
        val file = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Assignable.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val scriptingCompatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<ScriptingCompatibilityAnnotation>().first()
        val conformance = scriptingCompatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_ASSIGNABLE))
    }

    @Test
    fun testVarDecl_Unassignable() {
        val file = parseResource("tests/parser/xquery-sx-1.0/VarDecl_Unassignable.xq")

        val annotatedDeclPsi = file.descendants().filterIsInstance<XQueryAnnotatedDecl>().first()
        val scriptingCompatibilityAnnotationPsi = annotatedDeclPsi.children().filterIsInstance<ScriptingCompatibilityAnnotation>().first()
        val conformance = scriptingCompatibilityAnnotationPsi as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_UNASSIGNABLE))
    }

    // endregion
    // region WhileExpr

    @Test
    fun testWhileExpr() {
        val file = parseResource("tests/parser/xquery-sx-1.0/WhileExpr.xq")

        val whileExpr = file.descendants().filterIsInstance<ScriptingWhileExpr>().first()
        val conformance = whileExpr as XQueryConformance

        assertThat(conformance.requiresConformance.size, `is`(1))
        assertThat(conformance.requiresConformance[0], `is`(Scripting.NOTE_1_0_20140918))

        assertThat(conformance.conformanceElement, `is`(notNullValue()))
        assertThat(conformance.conformanceElement.node.elementType,
                `is`(XQueryTokenType.K_WHILE))
    }

    // endregion
}
