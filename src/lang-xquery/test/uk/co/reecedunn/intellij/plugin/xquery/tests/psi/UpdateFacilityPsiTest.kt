/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.util.elementType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xdm.types.element
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.text
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmAssignableVariable
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginCopyModifyExprBinding
import uk.co.reecedunn.intellij.plugin.xquery.ast.update.facility.*
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("ClassName", "RedundantVisibilityModifier")
@DisplayName("XQuery Update Facility 3.0 - IntelliJ Program Structure Interface (PSI)")
class UpdateFacilityPsiTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("UpdateFacilityPsiTest")

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

        @Test
        @DisplayName("XQuery Update Facility 3.0 EBNF (205) TargetExpr")
        fun targetExpr() {
            val expr = parse<UpdateFacilityTargetExpr>("insert node text <a/> after \$x/b")[0] as XpmExpression
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
                "copy \$x := () modify delete node \$y return \$z"
            )[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XQueryElementType.COPY_MODIFY_EXPR))
            assertThat(expr.expressionElement?.textOffset, `is`(0))
        }

        @Nested
        @DisplayName("XQuery Update Facility 3.0 EBNF (208) CopyModifyExpr ; XQuery IntelliJ Plugin XQuery EBNF (153) CopyModifyExprBinding")
        internal inner class CopyModifyExprBinding {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<PluginCopyModifyExprBinding>(
                    "copy \$x := () modify delete node \$y return \$z"
                )[0] as XpmAssignableVariable
                assertThat(expr.variableType?.typeName, `is`(nullValue()))
                assertThat(expr.variableExpression?.text, `is`("()"))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(UpdateFacilityCopyModifyExpr::class.java)))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<PluginCopyModifyExprBinding>(
                    "copy \$a:x := () modify delete node \$a:y return \$a:z"
                )[0] as XpmAssignableVariable
                assertThat(expr.variableType?.typeName, `is`(nullValue()))
                assertThat(expr.variableExpression?.text, `is`("()"))

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(UpdateFacilityCopyModifyExpr::class.java)))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<PluginCopyModifyExprBinding>(
                    """
                    copy ${'$'}Q{http://www.example.com}x := ()
                    modify delete node ${'$'}Q{http://www.example.com}y
                    return ${'$'}Q{http://www.example.com}z
                    """.trimIndent()
                )[0] as XpmAssignableVariable
                assertThat(expr.variableType?.typeName, `is`(nullValue()))
                assertThat(expr.variableExpression?.text, `is`("()"))

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))

                val localScope = expr.variableName?.element?.useScope as LocalSearchScope
                assertThat(localScope.scope.size, `is`(1))
                assertThat(localScope.scope[0], `is`(instanceOf(UpdateFacilityCopyModifyExpr::class.java)))
            }
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
