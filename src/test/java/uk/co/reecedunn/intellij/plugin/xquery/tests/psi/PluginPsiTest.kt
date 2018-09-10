/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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

import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathMapConstructorEntry
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginDirAttribute
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@DisplayName("XQuery IntelliJ Plugin - IntelliJ Program Structure Interface (PSI)")
private class PluginPsiTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.1) Node Constructors")
    internal inner class NodeConstructors {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (2) DirAttribute")
        internal inner class DirAttribute {
            @Test
            @DisplayName("namespace prefix")
            fun namespacePrefix() {
                val expr = parse<PluginDirAttribute>("<a xmlns:b='http://www.example.com'/>")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("b"))
                assertThat(expr.namespaceUri!!.data, `is`("http://www.example.com"))
            }

            @Test
            @DisplayName("namespace prefix, missing DirAttributeValue")
            fun namespacePrefixMissingDirAttributeValue() {
                val expr = parse<PluginDirAttribute>("<a xmlns:b=>")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix!!.data, `is`("b"))
                assertThat(expr.namespaceUri, `is`(CoreMatchers.nullValue()))
            }

            @Test
            @DisplayName("non-namespace declaration attribute")
            fun attribute() {
                val expr = parse<PluginDirAttribute>("<a b='http://www.example.com'/>")[0] as XPathNamespaceDeclaration
                assertThat(expr.namespacePrefix, `is`(CoreMatchers.nullValue()))
                assertThat(expr.namespaceUri, `is`(CoreMatchers.nullValue()))
            }
        }
    }

    @Nested
    @DisplayName("XQuery IntelliJ Plugin (3.8.1) Maps")
    internal inner class Maps {
        @Nested
        @DisplayName("XQuery IntelliJ Plugin EBNF (17) MapConstructorEntry")
        internal inner class MapConstructorEntry {
            @Test
            @DisplayName("MarkLogic")
            fun markLogic() {
                val entry = parse<XPathMapConstructorEntry>("object-node { \"1\" : \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XQueryTokenType.QNAME_SEPARATOR))
            }

            @Test
            @DisplayName("Saxon")
            fun saxon() {
                val entry = parse<XPathMapConstructorEntry>("map { \"1\" := \"one\" }")[0]
                assertThat(entry.separator.node.elementType, `is`(XQueryTokenType.ASSIGN_EQUAL))
            }
        }
    }
}
