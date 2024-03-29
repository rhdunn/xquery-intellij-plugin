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
package uk.co.reecedunn.intellij.plugin.xdm.tests.functions.op

import com.intellij.openapi.extensions.PluginId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_equal
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.context.expand

@Suppress("RedundantVisibilityModifier")
@DisplayName("XPath and XQuery Functions and Operators 3.1")
class FunctionsAndOperatorsTest : ParserTestCase() {
    override val pluginId: PluginId = PluginId.getId("FunctionsAndOperatorsTest")

    @Nested
    @DisplayName("XPath and XQuery Functions and Operators 3.1 (10.2.1) op:QName-equal")
    internal inner class OpQNameEqual {
        @Test
        @DisplayName("local-name == local-name")
        fun ncname() {
            val qnames = parse<XsQNameValue>("test test testing")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(true))
            assertThat(qname_equal(qnames[1], qnames[0]), `is`(true))

            // local name differs
            assertThat(qname_equal(qnames[0], qnames[2]), `is`(false))
            assertThat(qname_equal(qnames[2], qnames[0]), `is`(false))
        }

        @Test
        @DisplayName("local-name == prefix:local-name")
        fun ncnameAndQname() {
            val qnames = parse<XsQNameValue>("string xs:string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("local-name == Q{namespace}local-name")
        fun ncnameAndUriQualifiedName() {
            val qnames = parse<XsQNameValue>("string Q{http://www.w3.org/2001/XMLSchema}string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("prefix:local-name == prefix:local-name")
        fun qname() {
            val qnames = parse<XsQNameValue>("xs:string xs:string xs:integer fn:string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(true))
            assertThat(qname_equal(qnames[1], qnames[0]), `is`(true))

            // local name differs
            assertThat(qname_equal(qnames[0], qnames[2]), `is`(false))
            assertThat(qname_equal(qnames[2], qnames[0]), `is`(false))

            // prefix differs
            assertThat(qname_equal(qnames[0], qnames[3]), `is`(false))
            assertThat(qname_equal(qnames[3], qnames[0]), `is`(false))
        }

        @Test
        @DisplayName("prefix:local-name == local-name")
        fun qnameAndNcname() {
            val qnames = parse<XsQNameValue>("xs:string string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("prefix:local-name == Q{namespace}local-name")
        fun qnameAndUriQualifiedName() {
            val qnames = parse<XsQNameValue>("xs:string Q{http://www.w3.org/2001/XMLSchema}string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("Q{namespace}local-name == Q{namespace}local-name")
        fun uriQualifiedName() {
            val qnames = parse<XsQNameValue>(
                """
                Q{http://www.w3.org/2001/XMLSchema}string
                Q{http://www.w3.org/2001/XMLSchema}string
                Q{http://www.w3.org/2001/XMLSchema}integer
                Q{http://www.w3.org/2005/xpath-functions}string
                """
            )

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(true))
            assertThat(qname_equal(qnames[1], qnames[0]), `is`(true))

            // local name differs
            assertThat(qname_equal(qnames[0], qnames[2]), `is`(false))
            assertThat(qname_equal(qnames[2], qnames[0]), `is`(false))

            // namespace differs
            assertThat(qname_equal(qnames[0], qnames[3]), `is`(false))
            assertThat(qname_equal(qnames[3], qnames[0]), `is`(false))
        }

        @Test
        @DisplayName("Q{namespace}local-name == prefix:local-name")
        fun uriQualifiedNameAndQName() {
            val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string xs:string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Test
        @DisplayName("Q{namespace}local-name == local-name")
        fun uriQualifiedNameAndNcname() {
            val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string string")

            assertThat(qname_equal(qnames[0], qnames[1]), `is`(false))
        }

        @Nested
        @DisplayName("expanded")
        internal inner class Expanded {
            @Test
            @DisplayName("local-name == local-name")
            fun ncname() {
                val qnames = parse<XsQNameValue>("test test testing")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[0]), `is`(true))

                // local name differs
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(false))
            }

            @Test
            @DisplayName("local-name == prefix:local-name")
            fun ncnameAndQname() {
                val qnames = parse<XsQNameValue>(
                    """
                    declare default element namespace "http://www.w3.org/2005/xpath-functions";
                    string
                    xs:string
                    fn:string
                    """
                )
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(false))
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(true))
            }

            @Test
            @DisplayName("local-name == Q{namespace}local-name")
            fun ncnameAndUriQualifiedName() {
                val qnames = parse<XsQNameValue>("string Q{http://www.w3.org/2001/XMLSchema}string Q{}string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(false))
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(true))
            }

            @Test
            @DisplayName("prefix:local-name == prefix:local-name")
            fun qname() {
                val qnames = parse<XsQNameValue>("xs:string xs:string xs:integer fn:string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[0]), `is`(true))

                // local name differs
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(false))

                // prefix differs
                assertThat(qname_equal(expanded[0], expanded[3]), `is`(false))
                assertThat(qname_equal(expanded[3], expanded[0]), `is`(false))
            }

            @Test
            @DisplayName("prefix:local-name == local-name")
            fun qnameAndNcname() {
                val qnames = parse<XsQNameValue>(
                    """
                    declare default element namespace "http://www.w3.org/2005/xpath-functions";
                    string
                    xs:string
                    fn:string
                    """
                )
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[1], expanded[0]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(true))
            }

            @Test
            @DisplayName("prefix:local-name == Q{namespace}local-name")
            fun qnameAndUriQualifiedName() {
                val qnames = parse<XsQNameValue>("xs:string Q{http://www.w3.org/2001/XMLSchema}string Q{}string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
            }

            @Test
            @DisplayName("Q{namespace}local-name == Q{namespace}local-name")
            fun uriQualifiedName() {
                val qnames = parse<XsQNameValue>(
                    """
                    Q{http://www.w3.org/2001/XMLSchema}string
                    Q{http://www.w3.org/2001/XMLSchema}string
                    Q{http://www.w3.org/2001/XMLSchema}integer
                    Q{http://www.w3.org/2005/xpath-functions}string
                    """
                )
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[1]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[0]), `is`(true))

                // local name differs
                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[2], expanded[0]), `is`(false))

                // namespace differs
                assertThat(qname_equal(expanded[0], expanded[3]), `is`(false))
                assertThat(qname_equal(expanded[3], expanded[0]), `is`(false))
            }

            @Test
            @DisplayName("Q{namespace}local-name == prefix:local-name")
            fun uriQualifiedNameAndQName() {
                val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string Q{}string xs:string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[2]), `is`(true))
                assertThat(qname_equal(expanded[1], expanded[2]), `is`(false))
            }

            @Test
            @DisplayName("Q{namespace}local-name == local-name")
            fun uriQualifiedNameAndNcname() {
                val qnames = parse<XsQNameValue>("Q{http://www.w3.org/2001/XMLSchema}string Q{}string string")
                val expanded = qnames.map { qname -> qname.expand().first() }

                assertThat(qname_equal(expanded[0], expanded[2]), `is`(false))
                assertThat(qname_equal(expanded[1], expanded[2]), `is`(true))
            }
        }
    }
}
