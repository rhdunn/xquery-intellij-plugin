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
package uk.co.reecedunn.intellij.plugin.xquery.tests.xdm

import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.PsiElement
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.xml.NodeKind
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirTextConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

@Suppress("Reformat", "ClassName", "RedundantVisibilityModifier")
@DisplayName("XQuery 3.1 - Data Model (4) - Infoset Mapping")
class XQueryInfosetMappingTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryInfosetMappingTest")

    @Nested
    @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
    internal inner class DirElemConstructor {
        @Nested
        @DisplayName("Accessors (5.1) attributes")
        internal inner class Attributes {
            @Test
            @DisplayName("empty")
            fun empty() {
                val node = parse<XQueryDirElemConstructor>("<a/>")[0] as XdmElementNode

                assertThat(node.attributes.count(), `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (2) DirAttribute")
            fun dirAttribute() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test one='1' n:two='2' xmlns:n='urn:number' xmlns='urn:test'/>"
                )[0]
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("n:two"))

                assertThat((attributes[0].typedValue as? XsUntypedAtomicValue)?.data, `is`("1"))
                assertThat((attributes[1].typedValue as? XsUntypedAtomicValue)?.data, `is`("2"))

                assertThat(attributes.size, `is`(2))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor ; single attribute")
            fun compAttrConstructor_singleAttribute() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test xmlns:n='urn:number' xmlns='urn:test'>{ attribute one { 1 } }</test>"
                )[0] as XdmElementNode
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(attributes[0].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(1))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor ; multiple attributes")
            fun compAttrConstructor_multipleAttributes() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test xmlns:n='urn:number' xmlns='urn:test'>{ attribute one { 1 } , attribute n:two { 2 } }</test>"
                )[0] as XdmElementNode
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("n:two"))

                assertThat(attributes[0].typedValue, `is`(nullValue()))
                assertThat(attributes[1].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(2))
            }

            @Test
            @DisplayName("with direct and constructed attributes")
            fun withDirectAndConstructedAttributes() {
                val node = parse<XQueryDirElemConstructor>(
                    "<a one='1' two='2'>{attribute three{'3'}, attribute four{'4'}}</a>"
                )[0] as XdmElementNode
                val attributes = node.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("two"))
                assertThat(qname_presentation(attributes[2].nodeName!!), `is`("three"))
                assertThat(qname_presentation(attributes[3].nodeName!!), `is`("four"))

                assertThat((attributes[0].typedValue as? XsUntypedAtomicValue)?.data, `is`("1"))
                assertThat((attributes[1].typedValue as? XsUntypedAtomicValue)?.data, `is`("2"))
                assertThat(attributes[2].typedValue, `is`(nullValue()))
                assertThat(attributes[3].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(4))
            }
        }

        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val element = parse<XQueryDirElemConstructor>("<a/>")[0] as XdmElementNode
            assertThat(element.nodeKind, `is`(NodeKind.Element))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryDirElemConstructor>("<test/>")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryDirElemConstructor>("<t:test xmlns:t='urn:test'/>")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }
        }

        @Nested
        @DisplayName("Accessors (5.11) parent")
        inner class Parent {
            @Test
            @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
            fun dirElemConstructor() {
                val node = parse<XQueryDirElemConstructor>("<lorem><ipsum/></lorem>")[1]

                val parent = node.parentNode
                assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("lorem"))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryDirElemConstructor>("<lorem><ipsum/></lorem>")[0]
                assertThat(node.parentNode, `is`(nullValue()))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (156) CompDocConstructor")
            inner class CompDocConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryDirElemConstructor>("document { <ipsum/> }")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryDirElemConstructor>("document { <ipsum/> , <dolor/> }")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryDirElemConstructor>(
                        "document { if (true()) then <ipsum/> else () }"
                    )[0]

                    assertThat(node.parentNode, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            inner class CompElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryDirElemConstructor>("element lorem { <ipsum/> }")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryDirElemConstructor>("element lorem { <ipsum/> , <dolor/> }")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryDirElemConstructor>(
                        "element lorem { if (true()) then <ipsum/> else () }"
                    )[0]

                    assertThat(node.parentNode, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("text only")
            fun textOnly() {
                val node = parse<XQueryDirElemConstructor>("<test>Lorem ipsum</test>")[0] as XdmElementNode
                assertThat(node.stringValue, `is`("Lorem ipsum"))
            }

            @Test
            @DisplayName("empty inner element")
            fun emptyInnerElement() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test>Lorem <empty/> ipsum</test>"
                )[0] as XdmElementNode
                assertThat(node.stringValue, `is`("Lorem  ipsum"))
            }

            @Test
            @DisplayName("inner element")
            fun innerElement() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test>Lorem <i>ipsum</i> dolor</test>"
                )[0] as XdmElementNode
                assertThat(node.stringValue, `is`("Lorem ipsum dolor"))
            }

            @Test
            @DisplayName("expression content")
            fun expressionContent() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test>Lorem { 2 } dolor</test>"
                )[0] as XdmElementNode
                assertThat(node.stringValue, `is`(nullValue()))
            }

            @Test
            @DisplayName("inner expression content")
            fun innerExpressionContent() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test>Lorem <i>{ 2 }</i> dolor</test>"
                )[0] as XdmElementNode
                assertThat(node.stringValue, `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (2) DirAttribute")
    inner class DirAttribute {
        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<PluginDirAttribute>("<a test='value'/>")[0] as XdmAttributeNode
            assertThat(node.nodeKind, `is`(NodeKind.Attribute))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<PluginDirAttribute>("<a test='value'/>")[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<PluginDirAttribute>("<a xmlns:t='urn:test' t:test='value'/>")[1] as XdmAttributeNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }
        }

        @Test
        @DisplayName("Accessors (5.11) parent")
        fun parent() {
            val node = parse<PluginDirAttribute>("<a test='value'/>")[0]

            val parent = node.parentNode
            assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
            assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("a"))
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValue() {
                val node = parse<PluginDirAttribute>(
                    "<a b=\"http://www.example.com\uFFFF\"/>"
                )[0] as XdmAttributeNode
                assertThat(node.stringValue, `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValue() {
                val node = parse<PluginDirAttribute>("<a b=\"http://www.example.com")[0] as XdmAttributeNode
                assertThat(node.stringValue, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val node = parse<PluginDirAttribute>("<a b='''\"\"{{}}'/>")[0] as XdmAttributeNode
                assertThat(node.stringValue, `is`("'\"\"{}"))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val node = parse<PluginDirAttribute>("<a b=\"''\"\"{{}}\"/>")[0] as XdmAttributeNode
                assertThat(node.stringValue, `is`("''\"{}"))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val node = parse<PluginDirAttribute>(
                    "<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"/>"
                )[0] as XdmAttributeNode
                assertThat(node.stringValue, `is`("<áā\uD835\uDD04≪\u0338"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val node = parse<PluginDirAttribute>("<a b=\"&#xA0;&#160;&#x20;&#x1D520;\"/>")[0] as XdmAttributeNode
                assertThat(node.stringValue, `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
            }

            @Test
            @DisplayName("EnclosedExpr tokens")
            fun enclosedExpr() {
                val node = parse<PluginDirAttribute>("<a b=\"x{\$y}z\"/>")[0] as XdmAttributeNode
                assertThat(node.stringValue, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("Accessors (5.14) typed-value")
        internal inner class TypedValue {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val node = parse<PluginDirAttribute>("<a xmlns:b='http://www.example.com'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsAnyUriValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(value.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(value.element, `is`(node as PsiElement))
            }

            @Test
            @DisplayName("default element/type namespace")
            fun defaultElementTypeNamespace() {
                val node = parse<PluginDirAttribute>("<a xmlns='http://www.example.com'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsAnyUriValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.context, `is`(XdmUriContext.NamespaceDeclaration))
                assertThat(value.moduleTypes, `is`(sameInstance(XdmModuleType.MODULE_OR_SCHEMA)))
                assertThat(value.element, `is`(node as PsiElement))
            }

            @Test
            @DisplayName("xml:id")
            fun id() {
                val node = parse<PluginDirAttribute>("<a xml:id='lorem-ipsum'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsIDValue
                assertThat(value.data, `is`("lorem-ipsum"))
                assertThat(value.element, `is`(node as PsiElement))
            }

            @Test
            @DisplayName("non-namespace declaration attribute")
            fun attribute() {
                val node = parse<PluginDirAttribute>("<a b='http://www.example.com'/>")[0] as XdmAttributeNode

                val value = node.typedValue as XsUntypedAtomicValue
                assertThat(value.data, `is`("http://www.example.com"))
                assertThat(value.element, `is`(node as PsiElement))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (147) DirElemContent ; XQuery IntelliJ Plugin EBNF (123) DirTextConstructor")
    inner class DirTextConstructor {
        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<PluginDirTextConstructor>("<test>Lorem ipsum</test>")[0] as XdmTextNode
            assertThat(node.nodeKind, `is`(NodeKind.Text))
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("text value content")
            fun textValue() {
                val node = parse<PluginDirTextConstructor>("<test>Lorem ipsum\uFFFF</test>")[0] as XdmTextNode
                assertThat(node.stringValue, `is`("Lorem ipsum\uFFFF")) // U+FFFF = BAD_CHARACTER token.
            }

            @Test
            @DisplayName("CommonContent tokens")
            fun commonContent() {
                val node = parse<PluginDirTextConstructor>("<test>{{}}</test>")[0] as XdmTextNode
                assertThat(node.stringValue, `is`("{}"))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val node = parse<PluginDirTextConstructor>(
                    "<test>&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt</test>"
                )[0] as XdmTextNode
                assertThat(node.stringValue, `is`("<áā\uD835\uDD04≪\u0338"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val node = parse<PluginDirTextConstructor>(
                    "<test>&#xA0;&#160;&#x20;&#x1D520;</test>"
                )[0] as XdmTextNode
                assertThat(node.stringValue, `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (153) CDataSection")
            fun cdataSection() {
                val node = parse<PluginDirTextConstructor>("<test>One <![CDATA[Two]]> Three</test>")[0] as XdmTextNode
                assertThat(node.stringValue, `is`("One Two Three"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
    internal inner class CompElemConstructor {
        @Nested
        @DisplayName("Accessors (5.1) attributes")
        internal inner class Attributes {
            @Test
            @DisplayName("without attributes")
            fun withoutAttributes() {
                val element = parse<XQueryCompElemConstructor>("element a {}")[0] as XdmElementNode
                assertThat(element.attributes.count(), `is`(0))
            }

            @Test
            @DisplayName("with single constructed attribute")
            fun withConstructedAttribute() {
                val element = parse<XQueryCompElemConstructor>("element a { attribute one{'1'} }")[0] as XdmElementNode
                val attributes = element.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(attributes[0].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(1))
            }

            @Test
            @DisplayName("with multiple constructed attributes")
            fun withConstructedAttributes() {
                val element = parse<XQueryCompElemConstructor>(
                    "element a { attribute one{'1'}, attribute two{'2'} }"
                )[0] as XdmElementNode
                val attributes = element.attributes.toList()

                assertThat(qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(attributes[0].typedValue, `is`(nullValue()))

                assertThat(qname_presentation(attributes[1].nodeName!!), `is`("two"))
                assertThat(attributes[1].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(2))
            }
        }

        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val element = parse<XQueryCompElemConstructor>("element a {}")[0] as XdmElementNode
            assertThat(element.nodeKind, `is`(NodeKind.Element))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryCompElemConstructor>("element test {}")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryCompElemConstructor>(
                    "declare namespace t = 'urn:test'; element t:test {}"
                )[0] as XdmElementNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val node = parse<XQueryCompElemConstructor>("element Q{urn:test}test {}")[0] as XdmElementNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace?.data, `is`("urn:test"))
                assertThat(node.nodeName?.isLexicalQName, `is`(false))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompElemConstructor>(
                    "element { if (true()) 'test' else () } {}"
                )[0] as XdmElementNode

                assertThat(node.nodeName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("Accessors (5.11) parent")
        inner class Parent {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
            inner class DirElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompElemConstructor>("<lorem>{ element ipsum {} }</lorem>")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompElemConstructor>(
                        "<lorem>{ element ipsum {} , element dolor {} }</lorem>"
                    )[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompElemConstructor>(
                        "<lorem>{ if (true()) then element ipsum {} else () }</lorem>"
                    )[0]

                    assertThat(node.parentNode, `is`(nullValue()))
                }
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompElemConstructor>("element lorem { element ipsum {} }")[0]
                assertThat(node.parentNode, `is`(nullValue()))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (156) CompDocConstructor")
            inner class CompDocConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompElemConstructor>("document { element ipsum {} }")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompElemConstructor>(
                        "document { element ipsum {} , element dolor {} }"
                    )[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompElemConstructor>(
                        "document { if (true()) then element ipsum {} else () }"
                    )[0]

                    assertThat(node.parentNode, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            inner class CompElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompElemConstructor>("element lorem { element ipsum {} }")[1]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompElemConstructor>(
                        "element lorem { element ipsum {} , element dolor {} }"
                    )[1]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompElemConstructor>(
                        "element lorem { if (true()) then element ipsum {} else () }"
                    )[1]

                    assertThat(node.parentNode, `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
    inner class CompAttrConstructor {
        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<PluginDirAttribute>("<a test='value'/>")[0] as XdmAttributeNode
            assertThat(node.nodeKind, `is`(NodeKind.Attribute))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        internal inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute test { 'valid' } }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryCompAttrConstructor>(
                    "declare namespace t = 'urn:test'; element a { attribute t:test { 'value' } }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix?.data, `is`("t"))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace, `is`(nullValue()))
                assertThat(node.nodeName?.isLexicalQName, `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute Q{urn:test}test { 'value' } }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName?.prefix, `is`(nullValue()))
                assertThat(node.nodeName?.localName?.data, `is`("test"))
                assertThat(node.nodeName?.namespace?.data, `is`("urn:test"))
                assertThat(node.nodeName?.isLexicalQName, `is`(false))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute { if (true()) 'test' else () } {} }"
                )[0] as XdmAttributeNode

                assertThat(node.nodeName, `is`(nullValue()))
            }
        }

        @Nested
        @DisplayName("Accessors (5.11) parent")
        inner class Parent {
            @Nested
            @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
            inner class DirElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompAttrConstructor>("<a>{ attribute test { 'value' } }</a>")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "<a>{ attribute test { 'value' } , attribute attr { 'value' } }</a>"
                    )[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "<a>{ if (true()) then attribute test { 'value' } else () }</a>"
                    )[0]

                    assertThat(node.parentNode, `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            inner class CompElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompAttrConstructor>("element a { attribute test { 'value' } }")[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "element a { attribute test { 'value' } , attribute attr { 'value' } }"
                    )[0]

                    val parent = node.parentNode
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "element a { if (true()) then attribute test { 'value' } else () }"
                    )[0]

                    assertThat(node.parentNode, `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor")
    inner class CompTextConstructor {
        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<XQueryCompTextConstructor>("text { 'value' }")[0] as XdmTextNode
            assertThat(node.nodeKind, `is`(NodeKind.Text))
        }
    }
}
