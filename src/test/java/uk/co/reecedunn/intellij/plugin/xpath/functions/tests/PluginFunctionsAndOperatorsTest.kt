/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.functions.tests

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xpath.functions.op.UndeclaredNamespacePrefixException
import uk.co.reecedunn.intellij.plugin.xpath.functions.op.op_qname_parse

@DisplayName("XQuery IntelliJ Plugin Functions and Operators")
class PluginFunctionsAndOperatorsTest {
    @Nested
    @DisplayName("XQuery IntelliJ Plugin Functions and Operators (3.1) op:QName-parse")
    internal inner class OpQNameParse {
        @Test
        @DisplayName("URIQualifiedName")
        fun uriQualifiedName() {
            val qname = op_qname_parse("Q{http://www.example.co.uk}test", mapOf())
            assertThat(qname.namespace!!.data, `is`("http://www.example.co.uk"))
            assertThat(qname.prefix, `is`(nullValue()))
            assertThat(qname.localName!!.data, `is`("test"))
            assertThat(qname.isLexicalQName, `is`(false))
        }

        @Test
        @DisplayName("Clark Notation")
        fun clarkNotation() {
            val qname = op_qname_parse("{http://www.example.co.uk}test", mapOf())
            assertThat(qname.namespace!!.data, `is`("http://www.example.co.uk"))
            assertThat(qname.prefix, `is`(nullValue()))
            assertThat(qname.localName!!.data, `is`("test"))
            assertThat(qname.isLexicalQName, `is`(false))
        }

        @Test
        @DisplayName("QName")
        fun qname() {
            val qname = op_qname_parse("xs:string", mapOf("xs" to "http://www.w3.org/2001/XMLSchema"))
            assertThat(qname.namespace!!.data, `is`("http://www.w3.org/2001/XMLSchema"))
            assertThat(qname.prefix!!.data, `is`("xs"))
            assertThat(qname.localName!!.data, `is`("string"))
            assertThat(qname.isLexicalQName, `is`(true))
        }

        @Test
        @DisplayName("QName; undeclared namespace")
        fun qname_undeclaredNamespace() {
            val e = assertThrows(UndeclaredNamespacePrefixException::class.java) {
                op_qname_parse("xs:string", mapOf())
            }
            assertThat(e.message, `is`("XPST0081: Undeclared namespace prefix: xs"))
        }

        @Test
        @DisplayName("NCName")
        fun ncname() {
            val qname = op_qname_parse("string", mapOf("xs" to "http://www.w3.org/2001/XMLSchema"))
            assertThat(qname.namespace!!.data, `is`(""))
            assertThat(qname.prefix, `is`(nullValue()))
            assertThat(qname.localName!!.data, `is`("string"))
            assertThat(qname.isLexicalQName, `is`(true))
        }
    }
}
