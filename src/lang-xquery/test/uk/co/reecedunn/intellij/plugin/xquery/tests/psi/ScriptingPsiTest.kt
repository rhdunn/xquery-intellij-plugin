/*
 * Copyright (C) 2020 Reece H. Dunn
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
import org.hamcrest.CoreMatchers.nullValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.variable.XpmVariableBinding
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName")
@DisplayName("XQuery Scripting Extension 1.0 - IntelliJ Program Structure Interface (PSI)")
private class ScriptingPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.1) Apply Expression")
    internal inner class ApplyExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (32) ApplyExpr")
        fun applyExpr() {
            val expr = parse<ScriptingApplyExpr>("1; 2;")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.APPLY_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.2) Block Expressions")
    internal inner class BlockExpressions {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (153) BlockExpr")
        fun blockExpr() {
            val expr = parse<ScriptingBlockExpr>("block { 1, 2 }")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.BLOCK_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (157) BlockBody")
        fun blockBody() {
            val expr = parse<ScriptingBlockBody>("block { 1, 2 }")[0] as XpmExpression
            assertThat(expr.expressionElement, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.3) Assignment Expression")
    internal inner class AssignmentExpression {
        @Nested
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (158) AssignmentExpr")
        internal inner class AssignmentExpr {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val binding = parse<ScriptingAssignmentExpr>("\$x := \$y")[0] as XpmVariableBinding

                val qname = binding.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val expr = binding as XpmExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
                assertThat(expr.expressionElement?.textOffset, `is`(3))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val binding = parse<ScriptingAssignmentExpr>("\$a:x := \$a:y")[0] as XpmVariableBinding

                val qname = binding.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val expr = binding as XpmExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
                assertThat(expr.expressionElement?.textOffset, `is`(5))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val binding = parse<ScriptingAssignmentExpr>(
                    "\$Q{http://www.example.com}x := \$Q{http://www.example.com}y"
                )[0] as XpmVariableBinding

                val qname = binding.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val expr = binding as XpmExpression
                assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.ASSIGN_EQUAL))
                assertThat(expr.expressionElement?.textOffset, `is`(28))
            }
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.4) While Expression")
    internal inner class WhileExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (160) WhileExpr")
        fun whileExpr() {
            val expr = parse<ScriptingWhileExpr>("while (false()) { 1 }")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.WHILE_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.5) Exit Expression")
    internal inner class ExitExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (159) ExitExpr")
        fun exitExpr() {
            val expr = parse<ScriptingExitExpr>("exit returning 1")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.EXIT_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (6.5) Concatenation Expression")
    internal inner class ConcatenationExpression {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (33) ConcatExpr")
        fun concatExpr() {
            val expr = parse<ScriptingConcatExpr>("1, 2")[0] as XpmExpression
            assertThat(expr.expressionElement, `is`(nullValue()))
        }
    }
}
