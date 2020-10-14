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
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingApplyExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.scripting.ScriptingBlockBody
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
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
        fun transformWithExpr() {
            val expr = parse<ScriptingApplyExpr>("1; 2;")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.APPLY_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Scripting Extension 1.0 (5.2) Block Expressions")
    internal inner class BlockExpressions {
        @Test
        @DisplayName("XQuery Scripting Extension 1.0 EBNF (157) BlockBody")
        fun blockBody() {
            val expr = parse<ScriptingBlockBody>("block { 1, 2 }")[0] as XpmExpression
            assertThat(expr.expressionElement, `is`(nullValue()))
        }
    }
}
