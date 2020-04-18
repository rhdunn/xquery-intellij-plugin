/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.tests.debugger

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.MarkLogicVariable

@DisplayName("IntelliJ - Base Platform - Run Configuration - Query Debugger - Variables")
class MarkLogicVariableTest {
    companion object {
        val DEBUG_XML_NAMESPACES = mapOf("dbg" to "http://marklogic.com/xdmp/debug")
    }

    @Nested
    @DisplayName("variable name")
    internal inner class VariableName {
        @Test
        @DisplayName("NCName")
        fun ncname() {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="">ipsum</name>
                    <prefix/>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)
            assertThat(v.name, `is`("ipsum"))

            val qname = v.variableName
            assertThat(qname.prefix, `is`(nullValue()))
            assertThat(qname.namespace, `is`(nullValue()))
            assertThat(qname.localName!!.data, `is`("ipsum"))
            assertThat(qname.isLexicalQName, `is`(true))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="http://www.example.co.uk/test">ipsum</name>
                    <prefix>lorem</prefix>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)
            assertThat(v.name, `is`("lorem:ipsum"))

            val qname = v.variableName
            assertThat(qname.prefix!!.data, `is`("lorem"))
            assertThat(qname.namespace!!.data, `is`("http://www.example.co.uk/test"))
            assertThat(qname.localName!!.data, `is`("ipsum"))
            assertThat(qname.isLexicalQName, `is`(true))
        }

        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            @Language("xml")
            val xml = """
                <variable xmlns="http://marklogic.com/xdmp/debug">
                    <name xmlns="http://www.example.co.uk/test">ipsum</name>
                    <prefix/>
                </variable>
            """

            val v = MarkLogicVariable.create(XmlDocument.parse(xml, DEBUG_XML_NAMESPACES).root)
            assertThat(v.name, `is`("Q{http://www.example.co.uk/test}ipsum"))

            val qname = v.variableName
            assertThat(qname.prefix, `is`(nullValue()))
            assertThat(qname.namespace!!.data, `is`("http://www.example.co.uk/test"))
            assertThat(qname.localName!!.data, `is`("ipsum"))
            assertThat(qname.isLexicalQName, `is`(false))
        }
    }
}
