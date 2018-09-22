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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.model.inScopeVariablesForFile
import uk.co.reecedunn.intellij.plugin.xpath.model.staticallyKnownFunctions
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - Static Context")
private class XPathStaticContextTest : ParserTestCase() {
    @Nested
    @DisplayName("XPath 3.1 (2.1.1) Statically known function signatures")
    internal inner class StaticallyKnownFunctionSignatures {
        @Nested
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("fn:true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("fn:data()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("fn:false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("Q{http://www.w3.org/2005/xpath-functions}false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
        internal inner class NamedFunctionRef {
            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("fn:true#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("fn:data#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("fn:false#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("true#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("Q{http://www.w3.org/2005/xpath-functions}false#0")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (55) ArrowFunctionSpecifier")
        internal inner class ArrowFunctionSpecifier {
            @Nested
            @DisplayName("arity")
            internal inner class Arity {
                @Test
                @DisplayName("single match")
                fun singleMatch() {
                    val qname = parse<XPathEQName>("() => fn:true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }

                @Test
                @DisplayName("multiple matches")
                fun multipleMatches() {
                    val qname = parse<XPathEQName>("() => fn:data()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(2))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:data"))

                    assertThat(decls[1].arity, `is`(1))
                    assertThat(decls[1].functionName!!.element!!.text, `is`("fn:data"))
                }
            }

            @Nested
            @DisplayName("QName")
            internal inner class QName {
                @Test
                @DisplayName("built-in namespaces")
                fun builtInNamespaces() {
                    val qname = parse<XPathEQName>("() => fn:false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }

            @Nested
            @DisplayName("NCName")
            internal inner class NCName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("() => true()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:true"))
                }
            }

            @Nested
            @DisplayName("URIQualifiedName")
            internal inner class URIQualifiedName {
                @Test
                @DisplayName("built-in function")
                fun builtInFunction() {
                    val qname = parse<XPathEQName>("() => Q{http://www.w3.org/2005/xpath-functions}false()")[0]

                    val decls = qname.staticallyKnownFunctions().toList()
                    assertThat(decls.size, `is`(1))

                    assertThat(decls[0].arity, `is`(0))
                    assertThat(decls[0].functionName!!.element!!.text, `is`("fn:false"))
                }
            }
        }
    }

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

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testInlineFunctionExpr_FunctionBody_MultipleParameters() {
        val element = parse<XPathFunctionCall>("function (\$x, \$y) { test() }")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("y"))
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

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
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

        assertThat(variables[0].variableName?.localName?.data, `is`("x"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))
    }

    @Test
    fun testQuantifiedExpr_MultipleBindings_SatisfiesExpr() {
        val element = parse<XPathFunctionCall>("some \$x in 1, \$y in 2 satisfies test()")[0]
        val variables = element.inScopeVariablesForFile().toList()
        assertThat(variables.size, `is`(2))

        assertThat(variables[0].variableName?.localName?.data, `is`("y"))
        assertThat(variables[0].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[0].variableName?.namespace, `is`(nullValue()))

        assertThat(variables[1].variableName?.localName?.data, `is`("x"))
        assertThat(variables[1].variableName?.prefix, `is`(nullValue()))
        assertThat(variables[1].variableName?.namespace, `is`(nullValue()))
    }

    // endregion
    // endregion
}
