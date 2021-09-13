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
package uk.co.reecedunn.intellij.plugin.xdm.tests.xml

import uk.co.reecedunn.intellij.plugin.core.extensions.registerExtensionPointBean
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.xml.*
import com.intellij.xml.XmlExtension
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.tests.parser.ParsingTestCase
import uk.co.reecedunn.intellij.plugin.xdm.xml.NodeKind
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xdm.xml.impl.XmlPsiAccessorsProvider

class XmlPsiAccessorsProviderTest : ParsingTestCase<XmlFile>(null, XMLParserDefinition()) {
    override val pluginId: PluginId = PluginId.getId("XmlPsiAccessorsProviderTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())

        val app = ApplicationManager.getApplication()
        app.registerExtensionPointBean(
            StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java, pluginDisposable
        )
        app.registerExtensionPointBean(XmlExtension.EP_NAME, XmlExtension::class.java, pluginDisposable)
        app.registerExtensionPointBean(
            XmlFileNSInfoProvider.EP_NAME, XmlFileNSInfoProvider::class.java, pluginDisposable
        )

        XmlAccessorsProvider.register(this, XmlPsiAccessorsProvider)
    }

    @Nested
    @DisplayName("XmlTag")
    inner class Element {
        @Test
        @DisplayName("Accessors (5.9) node-kind")
        fun nodeKind() {
            val node = parse<XmlTag>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.element(node)!!

            assertThat(accessors.nodeKind(matched), `is`(NodeKind.Element))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XmlTag>("<test/>")[0]
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
                val node = parse<XmlTag>("<t:test xmlns:t='urn:test'/>")[0]
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
            @DisplayName("element node")
            fun element() {
                val node = parse<XmlTag>("<lorem><ipsum/></lorem>")[1]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val parent = accessors.parent(matched)
                assertThat(parent, `is`(instanceOf(XmlTag::class.java)))
                assertThat((parent as XmlTag).name, `is`("lorem"))
            }

            @Test
            @DisplayName("document node")
            fun document() {
                val node = parse<XmlTag>("<lorem><ipsum/></lorem>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                val parent = accessors.parent(matched)
                assertThat(parent, `is`(instanceOf(XmlDocument::class.java)))
                assertThat((parent as XmlDocument).rootTag?.name, `is`("lorem"))
            }
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("text only")
            fun textOnly() {
                val node = parse<XmlTag>("<test>Lorem ipsum</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem ipsum"))
            }

            @Test
            @DisplayName("empty inner element")
            fun emptyInnerElement() {
                val node = parse<XmlTag>("<test>Lorem <empty/> ipsum</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem  ipsum"))
            }

            @Test
            @DisplayName("inner element")
            fun innerElement() {
                val node = parse<XmlTag>("<test>Lorem <i>ipsum</i> dolor</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.element(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem ipsum dolor"))
            }
        }
    }

    @Nested
    @DisplayName("XmlAttribute")
    inner class Attribute {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<XmlAttribute>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            assertThat(matched, `is`(instanceOf(XmlAttribute::class.java)))
            assertThat((matched as XmlAttribute).nameElement.text, `is`("test"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XmlPsiAccessorsProvider)))
        }

        @Nested
        @DisplayName("Accessors (5.10) node-name")
        inner class NodeName {
            @Test
            @DisplayName("NCName")
            fun ncname() {
                val node = parse<XmlAttribute>("<a test='value'/>")[0]
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
                val node = parse<XmlAttribute>("<a xmlns:t='urn:test' t:test='value'/>")[1]
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
            val node = parse<XmlAttribute>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            val parent = accessors.parent(matched)
            assertThat(parent, `is`(instanceOf(XmlTag::class.java)))
            assertThat((parent as XmlTag).name, `is`("a"))
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("attribute value content")
            fun attributeValue() {
                val node = parse<XmlAttribute>(
                    "<a b=\"http://www.example.com\"/>"
                )[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                val node = parse<XmlAttribute>("<a b=\"&lt;&gt;\"/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("<>"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                // IDEA-277995 -- CharRefs > U+FFFF ignore the higher part, e.g. &#x1D520; -> U+D520
                val node = parse<XmlAttribute>("<a b=\"&#xA0;&#160;&#x20;&#xD520;\"/>")[0]
                val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

                assertThat(accessors.stringValue(matched), `is`("\u00A0\u00A0\u0020\uD520"))
            }
        }
    }

    @Nested
    @DisplayName("XmlAttributeValue")
    inner class AttributeValue {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<XmlAttributeValue>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.attribute(node)!!

            assertThat(matched, `is`(instanceOf(XmlAttribute::class.java)))
            assertThat((matched as XmlAttribute).nameElement.text, `is`("test"))

            assertThat(accessors, `is`(sameInstance(XmlPsiAccessorsProvider)))
        }
    }

    @Nested
    @DisplayName("XmlText")
    inner class Text {
        @Test
        @DisplayName("providers")
        fun providers() {
            val node = parse<XmlText>("<test>Lorem ipsum</test>")[0]
            val (matched, accessors) = XmlAccessorsProvider.text(node)!!

            assertThat(matched, `is`(instanceOf(XmlText::class.java)))
            assertThat((matched as XmlText).text, `is`("Lorem ipsum"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XmlPsiAccessorsProvider)))
        }

        @Nested
        @DisplayName("Accessors (5.12) string-value")
        internal inner class StringValue {
            @Test
            @DisplayName("text value content")
            fun textValue() {
                val node = parse<XmlText>("<test>Lorem ipsum</text>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("Lorem ipsum"))
            }

            @Test
            @DisplayName("PredefinedEntityRef tokens")
            fun predefinedEntityRef() {
                val node = parse<XmlText>("<test>&lt;&gt;</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("<>"))
            }

            @Test
            @DisplayName("CharRef tokens")
            fun charRef() {
                // IDEA-277995 -- CharRefs > U+FFFF ignore the higher part, e.g. &#x1D520; -> U+D520
                val node = parse<XmlText>("<test>&#xA0;&#160;&#x20;&#xD520;</test>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("\u00A0\u00A0\u0020\uD520"))
            }

            @Test
            @DisplayName("CDataSection")
            fun cdataSection() {
                val node = parse<XmlText>("<test>One <![CDATA[Two]]> Three</text>")[0]
                val (matched, accessors) = XmlAccessorsProvider.text(node)!!

                assertThat(accessors.stringValue(matched), `is`("One Two Three"))
            }
        }
    }
}
