/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.model

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.model.inScopeVariableBindings
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

class XPathStaticContextTest : ParserTestCase() {
    // region In-Scope Variable Bindings
    // region InlineFunctionExpr -> ParamList -> Param

    fun testInlineFunctionExpr_FunctionBody_NoParameters() {
        val element = parse<XPathFunctionCall>("function () { test() }")[0]
        val variables = element.inScopeVariableBindings().toList()
        assertThat(variables.size, `is`(0))
    }

    fun testInlineFunctionExpr_FunctionBody_SingleParameter() {
        val element = parse<XPathFunctionCall>("function (\$x) { test() }")[0]
        val variables = element.inScopeVariableBindings().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.staticValue as String, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    fun testInlineFunctionExpr_FunctionBody_MultipleParameters() {
        val element = parse<XPathFunctionCall>("function (\$x, \$y) { test() }")[0]
        val variables = element.inScopeVariableBindings().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.staticValue as String, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.staticValue as String, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    fun testInlineFunctionExpr_OutsideFunctionBody() {
        val element = parse<XPathFunctionCall>("function (\$x) {}(test())")[0]
        val variables = element.inScopeVariableBindings().toList()
        assertThat(variables.size, `is`(0))
    }

    // endregion
    // endregion
}
