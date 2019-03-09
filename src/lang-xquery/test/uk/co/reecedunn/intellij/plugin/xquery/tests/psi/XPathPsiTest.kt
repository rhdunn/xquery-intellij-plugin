/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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

import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginQuantifiedExprBinding
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XPath 3.1 - IntelliJ Program Structure Interface (PSI)")
private class XPathPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XPath 3.1 (3.1.5) Static Function Calls")
    internal inner class StaticFunctionCalls {
        @Nested
        @DisplayName("XPath 3.1 EBNF (63) FunctionCall")
        internal inner class FunctionCall {
            @Test
            @DisplayName("non-empty ArgumentList")
            fun nonEmptyArguments() {
                val f = parse<XPathFunctionCall>("math:pow(2, 8)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(2))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("pow"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("empty ArgumentList")
            fun emptyArguments() {
                val f = parse<XPathFunctionCall>("fn:true()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("fn"))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("ArgumentPlaceholder")
            fun argumentPlaceholder() {
                val f = parse<XPathFunctionCall>("math:sin(?)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("math"))
                assertThat(qname.localName!!.data, `is`("sin"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathFunctionCall>(":true()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))
                assertThat(f.functionName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (50) ArgumentList")
        internal inner class ArgumentList {
            @Test
            @DisplayName("empty parameters")
            fun empty() {
                val args = parse<XPathArgumentList>("fn:true()")[0]
                assertThat(args.arity, `is`(0))
            }

            @Test
            @DisplayName("multiple ExprSingle parameters")
            fun multiple() {
                val args = parse<XPathArgumentList>("math:pow(2, 8)")[0]
                assertThat(args.arity, `is`(2))
            }

            @Test
            @DisplayName("ArgumentPlaceholder parameter")
            fun argumentPlaceholder() {
                val args = parse<XPathArgumentList>("math:sin(?)")[0]
                assertThat(args.arity, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.1.6) Named Function References")
    internal inner class NamedFunctionReferences {
        @Nested
        @DisplayName("XPath 3.1 EBNF (67) NamedFunctionRef")
        internal inner class NamedFunctionRef {
            @Test
            @DisplayName("named function reference")
            fun namedFunctionRef() {
                val f = parse<XPathNamedFunctionRef>("true#3")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(3))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("missing arity")
            fun missingArity() {
                val f = parse<XPathNamedFunctionRef>("true#")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("true"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathNamedFunctionRef>(":true#0")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(0))
                assertThat(f.functionName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.1.7) Inline Function Expression")
    internal inner class InlineFunctionExpressions {
        @Nested
        @DisplayName("XPath 3.1 EBNF (3) Param")
        internal inner class Param {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<XPathParam>("function (\$x) {}")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<XPathParam>("function (\$a:x) {}")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<XPathParam>("function (\$Q{http://www.example.com}x) {}")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<XPathParam>("function (\$) {}")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.3.2.1) Axes")
    internal inner class Axes {
        @Nested
        @DisplayName("XPath 3.1 EBNF (41) ForwardAxis")
        internal inner class ForwardAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("""
                    child::one, descendant::two, attribute::three, self::four, descendant-or-self::five,
                    following-sibling::six, following::seven, namespace::eight
                """)
                assertThat(steps.size, `is`(8))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // child
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // descendant
                assertThat(steps[2].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Attribute)) // attribute
                assertThat(steps[3].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // self
                assertThat(steps[4].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // descendant-or-self
                assertThat(steps[5].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // following-sibling
                assertThat(steps[6].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // following
                assertThat(steps[7].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Namespace)) // namespace
            }
        }

        @Nested
        @DisplayName("XPath 3.1 EBNF (44) ReverseAxis")
        internal inner class ReverseAxis {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>(
                    "parent::one, ancestor::two, preceding-sibling::three, preceding::four, ancestor-or-self::five"
                )
                assertThat(steps.size, `is`(5))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // parent
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // ancestor
                assertThat(steps[2].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // preceding-sibling
                assertThat(steps[3].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // preceding
                assertThat(steps[4].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element)) // ancestor-or-self
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.3.2.2) Node Tests")
    internal inner class NodeTests {
        @Nested
        @DisplayName("XPath 3.1 EBNF (48) Wildcard")
        internal inner class Wildcard {
            @Test
            @DisplayName("wildcard prefix; wildcard local name")
            fun bothWildcard() {
                val qname = parse<XPathWildcard>("*:*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("wildcard prefix; non-wildcard local name")
            fun wildcardPrefix() {
                val qname = parse<XPathWildcard>("*:test")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(not(instanceOf(XdmWildcardValue::class.java))))
                assertThat(qname.localName!!.data, `is`("test"))
            }

            @Test
            @DisplayName("non-wildcard prefix; wildcard local name")
            fun wildcardLocalName() {
                val qname = parse<XPathWildcard>("test:*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(not(instanceOf(XdmWildcardValue::class.java))))
                assertThat(qname.prefix!!.data, `is`("test"))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("missing local name")
            fun noLocalName() {
                val qname = parse<XPathWildcard>("*:")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.prefix, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.prefix!!.data, `is`("*"))

                assertThat(qname.localName, `is`(nullValue()))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun keyword() {
                val qname = parse<XPathWildcard>("Q{http://www.example.com}*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }

            @Test
            @DisplayName("URIQualifiedName with an empty namespace")
            fun emptyNamespace() {
                val qname = parse<XPathWildcard>("Q{}*")[0] as XsQNameValue
                assertThat(qname.isLexicalQName, `is`(false))
                assertThat(qname.namespace!!.data, `is`(""))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.element, sameInstance(qname as PsiElement))

                assertThat(qname.localName, `is`(instanceOf(XdmWildcardValue::class.java)))
                assertThat(qname.localName!!.data, `is`("*"))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.3.5) Abbreviated Syntax")
    internal inner class AbbreviatedSyntax {
        @Nested
        @DisplayName("XPath 3.1 EBNF (42) AbbrevForwardStep")
        internal inner class AbbrevForwardStep {
            @Test
            @DisplayName("principal node kind")
            fun principalNodeKind() {
                val steps = parse<XPathNodeTest>("one, @two")
                assertThat(steps.size, `is`(2))
                assertThat(steps[0].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Element))
                assertThat(steps[1].getPrincipalNodeKind(), `is`(XPathPrincipalNodeKind.Attribute))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.11.1) Maps")
    internal inner class Maps {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (17) MapConstructorEntry")
        internal inner class MapConstructorEntry {
            @Test
            @DisplayName("key, value")
            fun keyValue() {
                val entry = parse<XPathMapConstructorEntry>("map { \"1\" : \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathTokenType.QNAME_SEPARATOR))
            }

            @Test
            @DisplayName("key, no value")
            fun saxon() {
                val entry = parse<XPathMapConstructorEntry>("map { \$ a }")[0]
                assertThat(entry.separator.node.elementType, `is`(XPathElementType.MAP_KEY_EXPR))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.13) Quantified Expressions")
    internal inner class QuantifiedExpressions {
        @Nested
        @DisplayName("XPath 3.1 EBNF (14) QuantifiedExpr")
        internal inner class QuantifiedExpr {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$x in \$y satisfies \$z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$a:x in \$a:y satisfies \$a:z")[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix!!.data, `is`("a"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val expr = parse<PluginQuantifiedExprBinding>(
                    "some \$Q{http://www.example.com}x in \$Q{http://www.example.com}y satisfies \$Q{http://www.example.com}z"
                )[0] as XPathVariableBinding

                val qname = expr.variableName!!
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.namespace!!.data, `is`("http://www.example.com"))
                assertThat(qname.localName!!.data, `is`("x"))
            }

            @Test
            @DisplayName("missing VarName")
            fun missingVarName() {
                val expr = parse<PluginQuantifiedExprBinding>("some \$")[0] as XPathVariableBinding
                assertThat(expr.variableName, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XPath 3.1 (3.20) Arrow Operator")
    internal inner class ArrowOperator {
        @Nested
        @DisplayName("XPath 3.1 EBNF (127) ArrowFunctionSpecifier")
        internal inner class ArrowFunctionSpecifier {
            @Test
            @DisplayName("EQName specifier, non-empty ArgumentList")
            fun nonEmptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => f(1, 2,  3)")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(4))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("f"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("EQName specifier, empty ArgumentList")
            fun emptyArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case()")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("EQName specifier, missing ArgumentList")
            fun missingArgumentList() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => upper-case")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))

                val qname = f.functionName!!
                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("upper-case"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("invalid EQName")
            fun invalidEQName() {
                val f = parse<XPathArrowFunctionSpecifier>("\$x => :upper-case")[0] as XPathFunctionReference
                assertThat(f.arity, `is`(1))
                assertThat(f.functionName, `is`(nullValue()))
            }
        }
    }
}
