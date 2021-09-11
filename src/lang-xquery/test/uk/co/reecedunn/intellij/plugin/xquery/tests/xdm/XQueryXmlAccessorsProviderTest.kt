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
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XpmNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginEnclosedAttrValueExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompAttrConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.optree.XQueryNamespaceProvider
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import uk.co.reecedunn.intellij.plugin.xquery.xdm.XQueryXmlAccessorsProvider

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
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<XQueryDirElemConstructor>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.element(node)!!

            assertThat(matched, `is`(instanceOf(XQueryDirElemConstructor::class.java)))
            assertThat(qname_presentation((matched as XQueryDirElemConstructor).nodeName!!), `is`("a"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryDirElemConstructor>("<test/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

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
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin EBNF (2) DirAttribute")
    inner class DirAttribute {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<PluginDirAttribute>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            assertThat(matched, `is`(instanceOf(PluginDirAttribute::class.java)))
            assertThat(qname_presentation((matched as PluginDirAttribute).nodeName!!), `is`("test"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<PluginDirAttribute>("<a test='value'/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

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
                val node = parse<PluginDirAttribute>("<a xmlns:t='urn:test' t:test='value'/>")[1]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

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
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (144) DirAttributeValue")
    inner class DirAttributeValue {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<XQueryDirAttributeValue>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            assertThat(matched, `is`(instanceOf(PluginDirAttribute::class.java)))
            assertThat(qname_presentation((matched as PluginDirAttribute).nodeName!!), `is`("test"))

            assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (157) CompElemConstructor")
    inner class CompElemConstructor {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<XQueryCompElemConstructor>("element a { attribute test { 'value' } }")[0]
            val (matched, accessors) = XmlAccessorsProvider.element(node)!!

            assertThat(matched, `is`(instanceOf(XQueryCompElemConstructor::class.java)))
            assertThat(qname_presentation((matched as XQueryCompElemConstructor).nodeName!!), `is`("a"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
    inner class CompAttrConstructor {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<XQueryCompAttrConstructor>("element a { attribute test { 'value' } }")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            assertThat(matched, `is`(instanceOf(XQueryCompAttrConstructor::class.java)))
            assertThat(qname_presentation((matched as XQueryCompAttrConstructor).nodeName!!), `is`("test"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XQueryCompAttrConstructor>("element a { attribute test { 'value' } }")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

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
    @DisplayName("XQuery IntelliJ Plugin EBNF (2) EnclosedAttrValueExpr")
    inner class EnclosedAttrValueExpr {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<PluginEnclosedAttrValueExpr>("element a { attribute test { 'value' } }")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            assertThat(matched, `is`(instanceOf(XQueryCompAttrConstructor::class.java)))
            assertThat(qname_presentation((matched as XQueryCompAttrConstructor).nodeName!!), `is`("test"))

            assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
        }
    }

    @Nested
    @DisplayName("XQuery 3.1 EBNF (222) StringLiteral")
    inner class StringLiteral {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (159) CompAttrConstructor")
        inner class CompAttrConstructor {
            @Test
            @DisplayName("providers ; single StringLiteral")
            fun providersForSingleStringLiteral() {
                val node = parse<XPathStringLiteral>("element a { attribute test { 'value' } }")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(matched, `is`(instanceOf(XQueryCompAttrConstructor::class.java)))
                assertThat(qname_presentation((matched as XQueryCompAttrConstructor).nodeName!!), `is`("test"))

                assertThat(accessors, `is`(sameInstance(XQueryXmlAccessorsProvider)))
            }

            @Test
            @DisplayName("providers ; multiple StringLiterals")
            fun providersForMultipleStringLiterals() {
                val node = parse<XPathStringLiteral>("element a { attribute test { 'one', 'two' } }")[0]
                val provider = XmlAccessorsProvider.attribute(node)
                assertThat(provider, `is`(nullValue()))
            }

            @Test
            @DisplayName("providers ; complex expression")
            fun providersForComplexExpression() {
                val node = parse<XPathStringLiteral>("element a { attribute test { 'one' || 'two' } }")[0]
                val provider = XmlAccessorsProvider.attribute(node)
                assertThat(provider, `is`(nullValue()))
            }
        }
    }
}