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
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName")
@DisplayName("XQuery Update Facility 3.0 - IntelliJ Program Structure Interface (PSI)")
private class UpdateFacilityPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery Update Facility 3.0 (5.1) Insert")
    internal inner class Insert {
        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (200) InsertExpr")
        fun insertExpr() {
            val expr = parse<UpdateFacilityInsertExpr>("insert node text <a/> after \$x/b")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.INSERT_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (204) SourceExpr")
        fun sourceExpr() {
            val expr = parse<UpdateFacilitySourceExpr>("insert node text <a/> after \$x/b")[0] as XpmExpression
            assertThat(expr.expressionElement, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 (5.2) Delete")
    internal inner class Delete {
        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (201) DeleteExpr")
        fun deleteExpr() {
            val expr = parse<UpdateFacilityDeleteExpr>("delete node \$x/test")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.DELETE_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 (5.3) Replace")
    internal inner class Replace {
        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (202) ReplaceExpr")
        fun replaceExpr() {
            val expr = parse<UpdateFacilityReplaceExpr>("replace node <a/> with <b/>")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.REPLACE_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 (5.4) Rename")
    internal inner class Rename {
        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (203) RenameExpr")
        fun replaceExpr() {
            val expr = parse<UpdateFacilityRenameExpr>("rename node <a/> as \"b\"")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.RENAME_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (206) NewNameExpr")
        fun newNameExpr() {
            val expr = parse<UpdateFacilityNewNameExpr>("rename node <a/> as \"b\"")[0] as XpmExpression
            assertThat(expr.expressionElement, `is`(nullValue()))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 (5.5) Dynamic Updating Function Invocation")
    internal inner class DynamicUpdatingFunctionInvocation {
        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (207) UpdatingFunctionCall")
        fun updatingFunctionCall() {
            val expr = parse<UpdateFacilityUpdatingFunctionCall>("invoke updating \$f(1, 2)")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.UPDATING_FUNCTION_CALL))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 (5.6) Copy Modify")
    internal inner class CopyModify {
        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (208) CopyModifyExpr")
        fun copyModifyExpr() {
            val expr = parse<UpdateFacilityCopyModifyExpr>(
                "copy \$x := () modify delete node \$x/test return \$x"
            )[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COPY_MODIFY_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }
    }

    @Nested
    @DisplayName("XQuery Update Facility 3.0 (5.7) Transform With")
    internal inner class TransformWith {
        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (97) TransformWithExpr")
        fun transformWithExpr() {
            val expr = parse<UpdateFacilityTransformWithExpr>("<a/> transform with {}")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryTokenType.K_TRANSFORM))
            assertThat(expr.expressionElement?.textOffset, `is`(5))
        }
    }
}
