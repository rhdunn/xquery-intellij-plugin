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

import com.intellij.compat.testFramework.registerExtensionPointBean
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
import uk.co.reecedunn.intellij.plugin.xdm.xml.XmlAccessorsProvider
import uk.co.reecedunn.intellij.plugin.xdm.xml.impl.XmlPsiAccessorsProvider

class XmlPsiAccessorsProviderTest : ParsingTestCase<XmlFile>(null, XMLParserDefinition()) {
    override val pluginId: PluginId = PluginId.getId("XmlPsiAccessorsProviderTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())

        val app = ApplicationManager.getApplication()
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
        @DisplayName("providers")
        fun providers() {
            val node = parse<XmlTag>("<a test='value'/>")[0]
            val (matched, accessors) = XmlAccessorsProvider.element(node)!!

            assertThat(matched, `is`(instanceOf(XmlTag::class.java)))
            assertThat((matched as XmlTag).name, `is`("a"))
            assertThat(matched, `is`(sameInstance(node)))

            assertThat(accessors, `is`(sameInstance(XmlPsiAccessorsProvider)))
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
}
