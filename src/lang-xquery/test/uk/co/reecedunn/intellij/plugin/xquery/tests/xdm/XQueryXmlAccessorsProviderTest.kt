/*
 * Copyright (C) 2021 Reece H. Dunn
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
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.xml.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirTextConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import uk.co.reecedunn.intellij.plugin.xquery.xdm.XQueryXmlAccessorsProvider

@DisplayName("XQuery 3.1 - Data Model (5) Accessors - XQuery Direct and Constructed Nodes")
class XQueryXmlAccessorsProviderTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryXmlAccessorsProviderTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XpmNamespaceProvider.register(this, XQueryNamespaceProvider)
        XmlAccessorsProvider.register(this, XQueryXmlAccessorsProvider)
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
    inner class DirElemConstructor {
        @Nested
        @DisplayName("Accessors (5.1) attributes")
        inner class Attributes {
            @Test
            @DisplayName("empty")
            fun empty() {
                val node = parse<XQueryDirElemConstructor>("<test/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.attributes(matched).count(), `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (2) DirAttribute")
            fun dirAttribute() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test one='1' n:two='2' xmlns:n='urn:number' xmlns='urn:test'/>"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val attributes = accessors.attributes(matched).toList()
                assertThat(attributes.size, `is`(2))

                assertThat(accessors.nodeKind(attributes[0]), `is`(NodeKind.Attribute))
                assertThat(accessors.nodeKind(attributes[1]), `is`(NodeKind.Attribute))

                assertThat(accessors.hasNodeName(attributes[0], "", "one"), `is`(true))
                assertThat(accessors.hasNodeName(attributes[1], "urn:number", "two"), `is`(true))

                val one = accessors.attribute(matched, "", "one")!!
                assertThat(accessors.nodeKind(one), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(one, "", "one"), `is`(true))
                assertThat(accessors.attributeStringValue(matched, "", "one"), `is`("1"))

                val two = accessors.attribute(matched, "urn:number", "two")!!
                assertThat(accessors.nodeKind(two), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(two, "urn:number", "two"), `is`(true))
                assertThat(accessors.attributeStringValue(matched, "urn:number", "two"), `is`("2"))

                assertThat(accessors.attribute(matched, "urn:numbers", "one"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "urn:numbers", "two"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "", "three"), `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor ; single attribute")
            fun compAttrConstructor_singleAttribute() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test xmlns:n='urn:number' xmlns='urn:test'>{ attribute one { 1 } }</test>"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val attributes = accessors.attributes(matched).toList()
                assertThat(attributes.size, `is`(1))

                assertThat(accessors.nodeKind(attributes[0]), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(attributes[0], "", "one"), `is`(true))

                val one = accessors.attribute(matched, "", "one")!!
                assertThat(accessors.nodeKind(one), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(one, "", "one"), `is`(true))
                assertThat(accessors.attributeStringValue(matched, "", "one"), `is`(nullValue()))

                assertThat(accessors.attribute(matched, "urn:numbers", "one"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "urn:numbers", "two"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "", "three"), `is`(nullValue()))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor ; multiple attributes")
            fun compAttrConstructor_multipleAttributes() {
                val node = parse<XQueryDirElemConstructor>(
                    "<test xmlns:n='urn:number' xmlns='urn:test'>{ attribute one { 1 } , attribute n:two { 2 } }</test>"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val attributes = accessors.attributes(matched).toList()
                assertThat(attributes.size, `is`(2))

                assertThat(accessors.nodeKind(attributes[0]), `is`(NodeKind.Attribute))
                assertThat(accessors.nodeKind(attributes[1]), `is`(NodeKind.Attribute))

                assertThat(accessors.hasNodeName(attributes[0], "", "one"), `is`(true))
                assertThat(accessors.hasNodeName(attributes[1], "urn:number", "two"), `is`(true))

                val one = accessors.attribute(matched, "", "one")!!
                assertThat(accessors.nodeKind(one), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(one, "", "one"), `is`(true))
                assertThat(accessors.attributeStringValue(matched, "", "one"), `is`(nullValue()))

                val two = accessors.attribute(matched, "urn:number", "two")!!
                assertThat(accessors.nodeKind(two), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(two, "urn:number", "two"), `is`(true))
                assertThat(accessors.attributeStringValue(matched, "urn:number", "two"), `is`(nullValue()))

                assertThat(accessors.attribute(matched, "urn:numbers", "one"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "urn:numbers", "two"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "", "three"), `is`(nullValue()))
            }

            @Test
            @DisplayName("with direct and constructed attributes")
            fun withDirectAndConstructedAttributes() {
                val node = parse<XQueryDirElemConstructor>(
                    "<a one='1' two='2'>{attribute three{'3'}, attribute four{'4'}}</a>"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val attributes = accessors.attributes(matched).toList()
                assertThat(attributes.size, `is`(4))

                assertThat(accessors.hasNodeName(attributes[0], "", "one"), `is`(true))
                assertThat(accessors.hasNodeName(attributes[1], "", "two"), `is`(true))
                assertThat(accessors.hasNodeName(attributes[2], "", "three"), `is`(true))
                assertThat(accessors.hasNodeName(attributes[3], "", "four"), `is`(true))

                assertThat(accessors.attributeStringValue(matched, "", "one"), `is`("1"))
                assertThat(accessors.attributeStringValue(matched, "", "two"), `is`("2"))
                assertThat(accessors.attributeStringValue(matched, "", "three"), `is`(nullValue()))
                assertThat(accessors.attributeStringValue(matched, "", "four"), `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<XQueryDirElemConstructor>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.element(node)!!

            assertThat(accessors.nodeKind(matched), `is`(NodeKind.Element))
            assertThat(matched, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryDirElemConstructor>("<test/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.namespaceUri(matched), `is`(""))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(true))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(false))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryDirElemConstructor>("<t:test xmlns:t='urn:test'/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.namespaceUri(matched), `is`("urn:test"))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(true))
            }
        }

        @Nested
        @DisplayName("Accessors (5.11) parent")
        inner class Parent {
            @Test
            @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
            fun dirElemConstructor() {
                val node = parse<XQueryDirElemConstructor>("<lorem><ipsum/></lorem>")[1]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val parent = accessors.parent(matched)
                assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("lorem"))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryDirElemConstructor>("<lorem><ipsum/></lorem>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.parent(matched), `is`(nullValue()))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (156) CompDocConstructor")
            inner class CompDocConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryDirElemConstructor>("document { <ipsum/> }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryDirElemConstructor>("document { <ipsum/> , <dolor/> }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryDirElemConstructor>(
                        "document { if (true()) then <ipsum/> else () }"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    assertThat(accessors.parent(matched), `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            inner class CompElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryDirElemConstructor>("element lorem { <ipsum/> }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryDirElemConstructor>("element lorem { <ipsum/> , <dolor/> }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryDirElemConstructor>(
                        "element lorem { if (true()) then <ipsum/> else () }"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    assertThat(accessors.parent(matched), `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("text only")
            fun textOnly() {
                val node = parse<XQueryDirElemConstructor>("<test>Lorem ipsum</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem ipsum"))
            }

            @Test
            @DisplayName("empty inner element")
            fun emptyInnerElement() {
                val node = parse<XQueryDirElemConstructor>("<test>Lorem <empty/> ipsum</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem  ipsum"))
            }

            @Test
            @DisplayName("inner element")
            fun innerElement() {
                val node = parse<XQueryDirElemConstructor>("<test>Lorem <i>ipsum</i> dolor</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem ipsum dolor"))
            }

            @Test
            @DisplayName("expression content")
            fun expressionContent() {
                val node = parse<XQueryDirElemConstructor>("<test>Lorem { 2 } dolor</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`(nullValue()))
            }

            @Test
            @DisplayName("inner expression content")
            fun innerExpressionContent() {
                val node = parse<XQueryDirElemConstructor>("<test>Lorem <i>{ 2 }</i> dolor</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (2) DirAttribute")
    inner class DirAttribute {
        @Nested
        @DisplayName("Accessors (5.9) node-kind")
        inner class NodeKindTest {
            @Test
            @DisplayName("XQuery 3.1 EBNF (143) DirAttributeList ; XQuery IntelliJ Plugin EBNF (2) DirAttribute")
            fun dirAttribute() {
                val node = parse<PluginDirAttribute>("<a test='value'/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.nodeKind(matched), `is`(NodeKind.Attribute))
                assertThat(matched, `is`(instanceOf(PluginDirAttribute::class.java)))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
            fun dirAttributeValue() {
                val node = parse<XQueryDirAttributeValue>("<a test='value'/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.nodeKind(matched), `is`(NodeKind.Attribute))
                assertThat(matched, `is`(instanceOf(PluginDirAttribute::class.java)))
            }
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<PluginDirAttribute>("<a test='value'/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.namespaceUri(matched), `is`(""))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(true))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(false))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<PluginDirAttribute>("<a xmlns:t='urn:test' t:test='value'/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.namespaceUri(matched), `is`("urn:test"))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(true))
            }
        }

        @Test
        @DisplayName("Accessors (5.11) parent")
        fun parent() {
            val node = parse<PluginDirAttribute>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            val parent = accessors.parent(matched)
            assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
            assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("a"))
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValue() {
                val node = parse<PluginDirAttribute>("<a b=\"http://www.example.com\uFFFF\"/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("http://www.example.com\uFFFF")) // U+FFFF = BAD_CHARACTER token.
            }

            @Test
            @DisplayName("unclosed attribute value content")
            fun unclosedAttributeValue() {
                val node = parse<PluginDirAttribute>("<a b=\"http://www.example.com")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("EscapeApos tokens")
            fun escapeApos() {
                val node = parse<PluginDirAttribute>("<a b='''\"\"{{}}'/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("'\"\"{}"))
            }

            @Test
            @DisplayName("EscapeQuot tokens")
            fun escapeQuot() {
                val node = parse<PluginDirAttribute>("<a b=\"''\"\"{{}}\"/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("''\"{}"))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val node = parse<PluginDirAttribute>("<a b=\"&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt\"/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("<áā\uD835\uDD04≪\u0338"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val node = parse<PluginDirAttribute>("<a b=\"&#xA0;&#160;&#x20;&#x1D520;\"/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
            }

            @Test
            @DisplayName("EnclosedExpr tokens")
            fun enclosedExpr() {
                val node = parse<PluginDirAttribute>("<a b=\"x{\$y}z\"/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`(nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (147) DirElemContent ; XQuery IntelliJ Plugin EBNF (123) DirTextConstructor")
    inner class DirTextConstructor {
        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<PluginDirTextConstructor>("<test>Lorem ipsum</test>")[0]
            val (matched, accessors) = XmlAccessorsProvider.text(node)!!

            assertThat(matched, `is`(instanceOf(PluginDirTextConstructor::class.java)))
            assertThat((matched as PluginDirTextConstructor).text, `is`("Lorem ipsum"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("text value content")
            fun textValue() {
                val node = parse<PluginDirTextConstructor>("<test>Lorem ipsum\uFFFF</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem ipsum\uFFFF")) // U+FFFF = BAD_CHARACTER token.
            }

            @Test
            @DisplayName("CommonContent tokens")
            fun commonContent() {
                val node = parse<PluginDirTextConstructor>("<test>{{}}</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("{}"))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                // entity reference types: XQuery, HTML4, HTML5, UTF-16 surrogate pair, multi-character entity, empty, partial
                val node = parse<PluginDirTextConstructor>(
                    "<test>&lt;&aacute;&amacr;&Afr;&NotLessLess;&;&gt</test>"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("<áā\uD835\uDD04≪\u0338"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                val node = parse<PluginDirTextConstructor>("<test>&#xA0;&#160;&#x20;&#x1D520;</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("\u00A0\u00A0\u0020\uD835\uDD20"))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (153) CDataSection")
            fun cdataSection() {
                val node = parse<PluginDirTextConstructor>("<test>One <![CDATA[Two]]> Three</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("One Two Three"))
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
    inner class CompElemConstructor {
        @Nested
        @DisplayName("Accessors (5.1) attributes")
        inner class Attributes {
            @Test
            @DisplayName("empty")
            fun empty() {
                val node = parse<XQueryCompElemConstructor>("element test {}")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.attributes(matched).count(), `is`(0))
            }

            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
            fun compAttrConstructor() {
                val node = parse<XQueryCompElemConstructor>(
                    "declare namespace n = 'urn:number'; element test { attribute one { 1 } , attribute n:two { 2 } }"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val attributes = accessors.attributes(matched).toList()
                assertThat(attributes.size, `is`(2))

                assertThat(accessors.nodeKind(attributes[0]), `is`(NodeKind.Attribute))
                assertThat(accessors.nodeKind(attributes[1]), `is`(NodeKind.Attribute))

                assertThat(accessors.hasNodeName(attributes[0], "", "one"), `is`(true))
                assertThat(accessors.hasNodeName(attributes[1], "urn:number", "two"), `is`(true))

                val one = accessors.attribute(matched, "", "one")!!
                assertThat(accessors.nodeKind(one), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(one, "", "one"), `is`(true))
                assertThat(accessors.attributeStringValue(matched, "", "one"), `is`(nullValue()))

                val two = accessors.attribute(matched, "urn:number", "two")!!
                assertThat(accessors.nodeKind(two), `is`(NodeKind.Attribute))
                assertThat(accessors.hasNodeName(two, "urn:number", "two"), `is`(true))
                assertThat(accessors.attributeStringValue(matched, "urn:number", "two"), `is`(nullValue()))

                assertThat(accessors.attribute(matched, "urn:numbers", "one"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "urn:numbers", "two"), `is`(nullValue()))
                assertThat(accessors.attribute(matched, "", "three"), `is`(nullValue()))
            }
        }

        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<XQueryCompElemConstructor>("element a { attribute test { 'value' } }")[0]
            val (matched, accessors) = XmlAccessorsProvider.element(node)!!

            assertThat(accessors.nodeKind(matched), `is`(NodeKind.Element))
            assertThat(matched, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryCompElemConstructor>("element test {}")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.namespaceUri(matched), `is`(""))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(true))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(false))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryCompElemConstructor>(
                    "declare namespace t = 'urn:test'; element t:test {}"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.namespaceUri(matched), `is`("urn:test"))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val node = parse<XQueryCompElemConstructor>("element Q{urn:test}test {}")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.namespaceUri(matched), `is`("urn:test"))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(true))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompElemConstructor>(
                    "element { if (true()) 'test' else () } {}"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.namespaceUri(matched), `is`(nullValue()))
                assertThat(accessors.localName(matched), `is`(nullValue()))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(false))
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
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompElemConstructor>(
                        "<lorem>{ element ipsum {} , element dolor {} }</lorem>"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompElemConstructor>(
                        "<lorem>{ if (true()) then element ipsum {} else () }</lorem>"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    assertThat(accessors.parent(matched), `is`(nullValue()))
                }
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompElemConstructor>("element lorem { element ipsum {} }")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.parent(matched), `is`(nullValue()))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (156) CompDocConstructor")
            inner class CompDocConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompElemConstructor>("document { element ipsum {} }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompElemConstructor>(
                        "document { element ipsum {} , element dolor {} }"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompDocConstructor::class.java)))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompElemConstructor>(
                        "document { if (true()) then element ipsum {} else () }"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    assertThat(accessors.parent(matched), `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            inner class CompElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompElemConstructor>("element lorem { element ipsum {} }")[1]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompElemConstructor>(
                        "element lorem { element ipsum {} , element dolor {} }"
                    )[1]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("lorem"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompElemConstructor>(
                        "element lorem { if (true()) then element ipsum {} else () }"
                    )[1]
                    val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                    assertThat(accessors.parent(matched), `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
    inner class CompAttrConstructor {
        @Nested
        @DisplayName("Accessors (5.9) node-kind")
        inner class NodeKindTest {
            @Test
            @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
            fun compAttrConstructor() {
                val node = parse<XQueryCompAttrConstructor>("element a { attribute test { 'value' } }")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.nodeKind(matched), `is`(NodeKind.Attribute))
                assertThat(matched, `is`(instanceOf(XQueryCompAttrConstructor::class.java)))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
            inner class StringLiteral {
                @Test
                @DisplayName("single StringLiteral")
                fun singleStringLiteral() {
                    val node = parse<XPathStringLiteral>("element a { attribute test { 'value' } }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                    assertThat(accessors.nodeKind(matched), `is`(NodeKind.Attribute))
                    assertThat(matched, `is`(instanceOf(XQueryCompAttrConstructor::class.java)))
                }

                @Test
                @DisplayName("multiple StringLiterals")
                fun multipleStringLiterals() {
                    val node = parse<XPathStringLiteral>("element a { attribute test { 'one', 'two' } }")[0]
                    val provider = XmlAccessorsProvider.attribute(node)

                    assertThat(provider, `is`(nullValue()))
                }

                @Test
                @DisplayName("complex expression")
                fun complexExpression() {
                    val node = parse<XPathStringLiteral>("element a { attribute test { 'one' || 'two' } }")[0]
                    val provider = XmlAccessorsProvider.attribute(node)

                    assertThat(provider, `is`(nullValue()))
                }
            }
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryCompAttrConstructor>("element a { attribute test { 'value' } }")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.namespaceUri(matched), `is`(""))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(true))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(false))
            }

            @Test
            @DisplayName("QName")
            fun qname() {
                val node = parse<XQueryCompAttrConstructor>(
                    "declare namespace t = 'urn:test'; element a { attribute t:test { 'value' } }"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.namespaceUri(matched), `is`("urn:test"))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(true))
            }

            @Test
            @DisplayName("URIQualifiedName")
            fun uriQualifiedName() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute Q{urn:test}test { 'value' } }"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.namespaceUri(matched), `is`("urn:test"))
                assertThat(accessors.localName(matched), `is`("test"))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(true))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(true))
            }

            @Test
            @DisplayName("expression")
            fun expression() {
                val node = parse<XQueryCompAttrConstructor>(
                    "element a { attribute { if (true()) 'test' else () } {} }"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.namespaceUri(matched), `is`(nullValue()))
                assertThat(accessors.localName(matched), `is`(nullValue()))

                assertThat(accessors.hasNodeName(matched, "", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests", "test")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "", "tests"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "", setOf("tests")), `is`(false))

                assertThat(accessors.hasNodeName(matched, "urn:test", "test"), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("test")), `is`(false))
                assertThat(accessors.hasNodeName(matched, "urn:test", setOf("tests", "test")), `is`(false))
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
                    val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "<a>{ attribute test { 'value' } , attribute attr { 'value' } }</a>"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryDirElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "<a>{ if (true()) then attribute test { 'value' } else () }</a>"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                    assertThat(accessors.parent(matched), `is`(nullValue()))
                }
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
            inner class CompElemConstructor {
                @Test
                @DisplayName("single item expression")
                fun single() {
                    val node = parse<XQueryCompAttrConstructor>("element a { attribute test { 'value' } }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("multiple item expression")
                fun multiple() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "element a { attribute test { 'value' } , attribute attr { 'value' } }"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                    val parent = accessors.parent(matched)
                    assertThat(parent, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
                    assertThat(qname_presentation((parent as XQueryCompElemConstructor).nodeName!!), `is`("a"))
                }

                @Test
                @DisplayName("complex expression")
                fun complex() {
                    val node = parse<XQueryCompAttrConstructor>(
                        "element a { if (true()) then attribute test { 'value' } else () }"
                    )[0]
                    val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                    assertThat(accessors.parent(matched), `is`(nullValue()))
                }
            }
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor")
    inner class CompTextConstructor {
        @Nested
        @DisplayName("Accessors (5.9) node-kind")
        inner class NodeKindTest {
            @Test
            @DisplayName("XQuery 3.1 EBNF (164) CompTextConstructor")
            fun compTextConstructor() {
                val node = parse<XQueryCompTextConstructor>("element a { text { 'value' } }")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.nodeKind(matched), `is`(NodeKind.Text))
                assertThat(matched, `is`(instanceOf(XQueryCompTextConstructor::class.java)))
            }

            @Nested
            @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
            inner class StringLiteral {
                @Test
                @DisplayName("single StringLiteral")
                fun singleStringLiteral() {
                    val node = parse<XPathStringLiteral>("element a { text { 'value' } }")[0]
                    val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                    assertThat(accessors.nodeKind(matched), `is`(NodeKind.Text))
                    assertThat(matched, `is`(instanceOf(XQueryCompTextConstructor::class.java)))
                }

                @Test
                @DisplayName("multiple StringLiterals")
                fun multipleStringLiterals() {
                    val node = parse<XPathStringLiteral>("element a { text { 'one', 'two' } }")[0]
                    val provider = XmlAccessorsProvider.text(node)

                    assertThat(provider, `is`(nullValue()))
                }

                @Test
                @DisplayName("complex expression")
                fun complexExpression() {
                    val node = parse<XPathStringLiteral>("element a { text { 'one' || 'two' } }")[0]
                    val provider = XmlAccessorsProvider.text(node)

                    assertThat(provider, `is`(nullValue()))
                }
            }
        }
    }
}
