/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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

import com.intellij.psi.PsiElement
import com.intellij.util.Range
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.context.XstUsageType
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginContextItemFunctionExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginTypeAlias
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginUnionType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.getUsageType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.tests.parser.ParserTestCase
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI) - XPath")
private class PluginPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath (2.1.2.1) Union Type")
    internal inner class UnionType {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (16) UnionType")
        internal inner class UnionType {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() instance of union(test)")[0] as XsQNameValue
                assertThat(qname.element!!.getUsageType(), `is`(XstUsageType.Type))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("empty")
            fun empty() {
                val test = parse<PluginUnionType>("() instance of union ( (::) )")[0]

                val memberTypes = test.memberTypes.toList()
                assertThat(memberTypes.size, `is`(0))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("union()"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("one")
            fun one() {
                val test = parse<PluginUnionType>("() instance of union ( xs:string )")[0]

                val memberTypes = test.memberTypes.toList()
                assertThat(memberTypes.size, `is`(1))
                assertThat(op_qname_presentation(memberTypes[0]), `is`("xs:string"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("union(xs:string)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("many")
            fun many() {
                val test = parse<PluginUnionType>("() instance of union ( xs:string , xs:anyURI )")[0]

                val memberTypes = test.memberTypes.toList()
                assertThat(memberTypes.size, `is`(2))
                assertThat(op_qname_presentation(memberTypes[0]), `is`("xs:string"))
                assertThat(op_qname_presentation(memberTypes[1]), `is`("xs:anyURI"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("union(xs:string, xs:anyURI)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAnyUnionType::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }

        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (17) TypedMapTest")
        internal inner class TypedMapTest {
            @Test
            @DisplayName("union key type")
            fun unionKeyType() {
                val test = parse<XPathTypedMapTest>("() instance of map ( union ( xs:string , xs:float ) , xs:int )")[0]
                assertThat(test.keyType?.typeName, `is`("union(xs:string, xs:float)"))
                assertThat(test.valueType?.typeName, `is`("xs:int"))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("map(union(xs:string, xs:float), xs:int)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmMap::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath (2.1.2.3) Element Test")
    internal inner class ElementTest {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (29) ElementNameOrWildcard")
        internal inner class ElementNameOrWildcard {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val test = parse<XPathElementTest>("() instance of element ( *:test )")[0]
                assertThat(op_qname_presentation(test.nodeName!!), `is`("*:test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("element(*:test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmElementNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath (2.1.2.4) Attribute Test")
    internal inner class AttributeTest {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (30) AttribNameOrWildcard")
        internal inner class AttribNameOrWildcard {
            @Test
            @DisplayName("wildcard")
            fun wildcard() {
                val test = parse<XPathAttributeTest>("() instance of attribute ( *:test )")[0]
                assertThat(op_qname_presentation(test.nodeName!!), `is`("*:test"))
                assertThat(test.nodeType, `is`(nullValue()))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("attribute(*:test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmAttributeNode::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath (2.1.2.5) Type Alias")
    internal inner class TypeAlias {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (34) TypeAlias")
        internal inner class TypeAlias {
            @Test
            @DisplayName("NCName namespace resolution")
            fun ncname() {
                val qname = parse<XPathEQName>("() instance of ~ test")[0] as XsQNameValue
                assertThat(qname.element!!.getUsageType(), `is`(XstUsageType.Type))

                assertThat(qname.isLexicalQName, `is`(true))
                assertThat(qname.namespace, `is`(nullValue()))
                assertThat(qname.prefix, `is`(nullValue()))
                assertThat(qname.localName!!.data, `is`("test"))
                assertThat(qname.element, sameInstance(qname as PsiElement))
            }

            @Test
            @DisplayName("item type; Saxon 9.8")
            fun itemType_saxon9() {
                val test = parse<PluginTypeAlias>("() instance of ~ test")[0]
                assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("type(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }

            @Test
            @DisplayName("item type; Saxon 10.0")
            fun itemType_saxon10() {
                val test = parse<PluginTypeAlias>("() instance of type ( test )")[0]
                assertThat(test.type, `is`(sameInstance(test.children().filterIsInstance<XsQNameValue>().first())))

                val type = test as XdmItemType
                assertThat(type.typeName, `is`("type(test)"))
                assertThat(type.typeClass, `is`(sameInstance(XdmItem::class.java)))

                assertThat(type.itemType, `is`(sameInstance(type)))
                assertThat(type.lowerBound, `is`(1))
                assertThat(type.upperBound, `is`(1))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin XPath (3.4) Logical Expressions")
    internal inner class LogicalExpressions {
        @Test
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (19) OrExpr")
        fun orExpr() {
            val expr = parse<XPathOrExpr>("1 or 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_OR))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }

        @Test
        @DisplayName("XQuery IntelliJ Plugin XPath EBNF (20) AndExpr")
        fun andExpr() {
            val expr = parse<XPathAndExpr>("1 andAlso 2")[0] as XpmExpression

            assertThat(expr.expressionElement.elementType, `is`(XPathTokenType.K_ANDALSO))
            assertThat(expr.expressionElement?.textOffset, `is`(2))
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.6) Primary Expressions")
    internal inner class PrimaryExpressions {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin XPath (3.6.1) Inline Function Expressions")
        internal inner class InlineFunctionExpressions {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (169) InlineFunctionExpr ; XQuery IntelliJ Plugin XPath EBNF (22) ParamList")
            internal inner class InlineFunctionExpr {
                @Test
                @DisplayName("variadic")
                fun variadic() {
                    val decl = parse<XdmFunctionDeclaration>("function (\$one, \$two ...) {}")[0]
                    assertThat(decl.functionName, `is`(nullValue()))
                    assertThat(decl.returnType, `is`(nullValue()))
                    assertThat(decl.arity, `is`(Range(1, Int.MAX_VALUE)))
                    assertThat(decl.isVariadic, `is`(true))

                    assertThat(decl.params.size, `is`(2))
                    assertThat(op_qname_presentation(decl.params[0].variableName!!), `is`("one"))
                    assertThat(op_qname_presentation(decl.params[1].variableName!!), `is`("two"))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (137) FunctionCall ; XQuery IntelliJ Plugin EBNF (22) ParamList")
            internal inner class FunctionCall {
                @Test
                @DisplayName("variadic; no arguments specified for the variadic parameter")
                fun variadicEmpty() {
                    val f = parse<XPathFunctionCall>("concat(2, 4)")[0] as XdmFunctionReference
                    assertThat(f.arity, `is`(2))

                    val qname = f.functionName!!
                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("concat"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val args = (f as XPathFunctionCall).argumentList
                    assertThat(args.arity, `is`(2))
                    assertThat(args.functionReference, `is`(sameInstance(f)))

                    val bindings = args.bindings
                    assertThat(bindings.size, `is`(0))
                }

                @Test
                @DisplayName("variadic; single argument specified for the variadic parameter")
                fun variadicSingle() {
                    val f = parse<XPathFunctionCall>("concat(2, 4, 6)")[0] as XdmFunctionReference
                    assertThat(f.arity, `is`(3))

                    val qname = f.functionName!!
                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("concat"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val args = (f as XPathFunctionCall).argumentList
                    assertThat(args.arity, `is`(3))
                    assertThat(args.functionReference, `is`(sameInstance(f)))

                    val bindings = args.bindings
                    assertThat(bindings.size, `is`(0))
                }

                @Test
                @DisplayName("variadic; multiple arguments specified for the variadic parameter")
                fun variadicMultiple() {
                    val f = parse<XPathFunctionCall>("concat(2, 4, 6, 8)")[0] as XdmFunctionReference
                    assertThat(f.arity, `is`(4))

                    val qname = f.functionName!!
                    assertThat(qname.isLexicalQName, `is`(true))
                    assertThat(qname.namespace, `is`(nullValue()))
                    assertThat(qname.prefix, `is`(nullValue()))
                    assertThat(qname.localName!!.data, `is`("concat"))
                    assertThat(qname.element, sameInstance(qname as PsiElement))

                    val args = (f as XPathFunctionCall).argumentList
                    assertThat(args.arity, `is`(4))
                    assertThat(args.functionReference, `is`(sameInstance(f)))

                    val bindings = args.bindings
                    assertThat(bindings.size, `is`(0))
                }
            }

            @Test
            @DisplayName("XQuery IntelliJ Plugin XPath EBNF (24) ContextItemFunctionExpr")
            fun contextItemFunctionExpr() {
                val expr = parse<PluginContextItemFunctionExpr>(".{ () }")[0] as XpmExpression

                assertThat(expr.expressionElement.elementType, `is`(XPathElementType.CONTEXT_ITEM_FUNCTION_EXPR))
                assertThat(expr.expressionElement?.textOffset, `is`(0))
            }
        }
    }
}
