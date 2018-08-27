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

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.model.inScopeVariablesForFile
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
private class XPathStaticContextTest : ParserTestCase() {
    // region In-Scope Variable Bindings
    // region InlineFunctionExpr -> ParamList -> Param

    @Test
    fun testInlineFunctionExpr_FunctionBody_NoParameters() {
        val element = parse<XPathFunctionCall>("function () { test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testInlineFunctionExpr_FunctionBody_SingleParameter() {
        val element = parse<XPathFunctionCall>("function (\$x) { test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.staticValue as String, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInlineFunctionExpr_FunctionBody_MultipleParameters() {
        val element = parse<XPathFunctionCall>("function (\$x, \$y) { test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.staticValue as String, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.staticValue as String, `is`("y"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInlineFunctionExpr_OutsideFunctionBody() {
        val element = parse<XPathFunctionCall>("function (\$x) {}(test())")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    // endregion
    // region QuantifiedExpr

    @Test
    fun testQuantifiedExpr_SingleBinding_InExpr() {
        val element = parse<XPathFunctionCall>("some \$x in test() satisfies 1")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testQuantifiedExpr_SingleBinding_SatisfiesExpr() {
        val element = parse<XPathFunctionCall>("some \$x in 1 satisfies test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.staticValue as String, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testQuantifiedExpr_MultipleBindings_FirstInExpr() {
        val element = parse<XPathFunctionCall>("some \$x in test(), \$y in 1 satisfies 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(0))
    }

    @Test
    fun testQuantifiedExpr_MultipleBindings_LastInExpr() {
        val element = parse<XPathFunctionCall>("some \$x in 1, \$y in test() satisfies 2")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(1))

        assertThat(variables[0].variableName?.localName?.staticValue as String, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testQuantifiedExpr_MultipleBindings_SatisfiesExpr() {
        val element = parse<XPathFunctionCall>("some \$x in 1, \$y in 2 satisfies test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.staticValue as String, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.staticValue as String, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
}
