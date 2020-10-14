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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.UpdateFacilityCopyModifyExpr
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("ClassName")
@DisplayName("XQuery Update Facility 3.0 - IntelliJ Program Structure Interface (PSI)")
private class UpdateFacilityPsiTest : ParserTestCase() {
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
}
