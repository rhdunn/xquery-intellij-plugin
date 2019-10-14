/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.tests.psi

import com.intellij.util.Range
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI) - XPath")
private class PluginPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.7.3) Inline Function Expressions")
    internal inner class InlineFunctionExpressions {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr ; XQuery IntelliJ Plugin EBNF (95) ParamList")
        internal inner class InlineFunctionExpr {
            @Test
            @DisplayName("variadic")
            fun variadic() {
                val decl = parse<XPathFunctionDeclaration>("function (\$one, \$two ...) {}")[0]
                assertThat(decl.functionName, CoreMatchers.`is`(CoreMatchers.nullValue()))
                assertThat(decl.returnType, CoreMatchers.`is`(CoreMatchers.nullValue()))
                assertThat(decl.arity, CoreMatchers.`is`(Range(1, Int.MAX_VALUE)))
                assertThat(decl.isVariadic, CoreMatchers.`is`(true))

                assertThat(decl.params.size, CoreMatchers.`is`(2))
                assertThat(op_qname_presentation(decl.params[0].variableName!!), CoreMatchers.`is`("one"))
                assertThat(op_qname_presentation(decl.params[1].variableName!!), CoreMatchers.`is`("two"))
            }
        }
    }
}
