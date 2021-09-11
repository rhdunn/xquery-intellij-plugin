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
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginEnclosedAttrValueExpr
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompAttrConstructor
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirAttributeValue
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase
import uk.co.reecedunn.intellij.plugin.xquery.xdm.XQueryXmlAccessorsProvider

class XQueryXmlAccessorsProviderTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("XQueryXmlAccessorsProviderTest")

    override fun registerServicesAndExtensions() {
        super.registerServicesAndExtensions()

        XmlAccessorsProvider.register(this, XQueryXmlAccessorsProvider)
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
