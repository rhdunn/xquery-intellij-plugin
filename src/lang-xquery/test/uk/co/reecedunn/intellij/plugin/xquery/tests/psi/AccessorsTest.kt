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
package uk.co.reecedunn.intellij.plugin.xquery.tests.psi

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xquery.tests.parser.ParserTestCase

// NOTE: This class is private so the JUnit 4 test runner does not run the tests contained in it.
@Suppress("Reformat", "ClassName")
@DisplayName("XQuery 3.1 - XDM Accessors")
private class AccessorsTest : ParserTestCase() {
    @Nested
    @DisplayName("XQuery and XPath Data Model 3.1 (5.1) attributes Accessor")
    internal inner class AttributesAccessor {
        @Nested
        @DisplayName("XQuery 3.1 EBNF (142) DirElemConstructor")
        internal inner class DirElemConstructor {
            @Test
            @DisplayName("without attributes")
            fun withoutAttributes() {
                val element = parse<XQueryDirElemConstructor>("<a/>")[0] as XdmElementNode
                assertThat(element.attributes.count(), `is`(0))
            }

            @Test
            @DisplayName("with direct attributes")
            fun withDirectAttributes() {
                val element = parse<XQueryDirElemConstructor>("<a one='1' two='2'/>")[0] as XdmElementNode
                val attributes = element.attributes.toList()

                assertThat(op_qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat((attributes[0].typedValue as? XsUntypedAtomicValue)?.data, `is`("1"))

                assertThat(op_qname_presentation(attributes[1].nodeName!!), `is`("two"))
                assertThat((attributes[1].typedValue as? XsUntypedAtomicValue)?.data, `is`("2"))

                assertThat(attributes.size, `is`(2))
            }

            @Test
            @DisplayName("with single constructed attribute")
            fun withConstructedAttribute() {
                val element = parse<XQueryDirElemConstructor>("<a>{attribute one{'1'}}</a>")[0] as XdmElementNode
                val attributes = element.attributes.toList()

                assertThat(op_qname_presentation(attributes[0].nodeName!!), `is`("one"))
                assertThat(attributes[0].typedValue, `is`(nullValue()))

                assertThat(attributes.size, `is`(1))
            }
        }
    }
}
