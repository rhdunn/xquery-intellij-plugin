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

import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XMLParserDefinition
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.openapi.extensions.PluginId
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlFile
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

        XmlAccessorsProvider.register(this, XmlPsiAccessorsProvider)
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
