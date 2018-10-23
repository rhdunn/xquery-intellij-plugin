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
package uk.co.reecedunn.intellij.plugin.processor.tests

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.*
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.processor.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.basex.BaseX
import uk.co.reecedunn.intellij.plugin.xpath.functions.op.op_qname_parse
import java.io.File

@Suppress("Reformat")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("XQuery processor")
class ProcessorTest {
    val home = System.getProperty("user.home")
    // Modify these for the processor being tested:
    val provider = BaseX(File("$home/xquery/basex/basex-9.0/BaseX.jar"))
    val processor: QueryProcessor = provider.create()

    @AfterAll
    fun tearDown() {
        processor.close()
    }

    @Nested
    @DisplayName("return value type display name")
    internal inner class ReturnValues {
        private fun atomic(value: String, type: String, valueMatcher: Matcher<String>, typeMatcher: Matcher<String>) {
            val q = processor.createQuery("\"$value\" cast as $type", MimeTypes.XQUERY)
            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, valueMatcher)
            assertThat(items[0].type, typeMatcher)
        }

        private fun atomic_values(value: String, type: String, valueMatcher: Matcher<String>) {
            atomic(value, type, valueMatcher, `is`(type))
        }

        private fun atomic_types(value: String, type: String, typeMatcher: Matcher<String>) {
            atomic(value, type, `is`(value), typeMatcher)
        }

        private fun atomic(value: String, type: String) {
            atomic(value, type, `is`(value), `is`(type))
        }

        @Test @DisplayName("empty-sequence()") fun emptySequence() {
            val q = processor.createQuery("()", MimeTypes.XQUERY)
            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
        @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic_values("bG9yZW0gaXBzdW0=", "xs:base64Binary", anyOf(`is`("bG9yZW0gaXBzdW0="), `is`("lorem ipsum"))) }
        @Test @DisplayName("xs:boolean") fun xsBoolean() { atomic("true", "xs:boolean") }
        @Test @DisplayName("xs:date") fun xsDate() { atomic("1995-10-12", "xs:date") }
        @Test @DisplayName("xs:dateTime") fun xsDateTime() { atomic("1995-10-12T11:22:33.444", "xs:dateTime") }
        @Test @DisplayName("xs:decimal") fun xsDecimal() { atomic("2.5", "xs:decimal") }
        @Test @DisplayName("xs:double") fun xsDouble() { atomic("2.5", "xs:double") }
        @Test @DisplayName("xs:duration") fun xsDuration() { atomic("P5D", "xs:duration") }
        @Test @DisplayName("xs:float") fun xsFloat() { atomic("2.5", "xs:float") }
        @Test @DisplayName("xs:gDay") fun xsGDay() { atomic("---12", "xs:gDay") }
        @Test @DisplayName("xs:gMonth") fun xsGMonth() { atomic("--10", "xs:gMonth") }
        @Test @DisplayName("xs:gMonthDay") fun xsGMonthDay() { atomic("--10-12", "xs:gMonthDay") }
        @Test @DisplayName("xs:gYear") fun xsGYear() { atomic("1995", "xs:gYear") }
        @Test @DisplayName("xs:gYearMonth") fun xsGYearMonth() { atomic("1995-10", "xs:gYearMonth") }
        @Test @DisplayName("xs:hexBinary") fun xsHexBinary() { atomic_values("6C6F72656D20697073756D", "xs:hexBinary", anyOf(`is`("6C6F72656D20697073756D"), `is`("lorem ipsum"))) }
        @Test @DisplayName("xs:integer") fun xsInteger() { atomic("2", "xs:integer") }
        @Test @DisplayName("xs:string") fun xsString() { atomic("lorem ipsum", "xs:string") }
        @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }

        @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
    }

    @Nested
    @DisplayName("bind variable using NCName from string with a specified type")
    internal inner class BindVariableNCNameFromString {
        private fun atomic(value: String, type: String, valueMatcher: Matcher<String>, typeMatcher: Matcher<String>) {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable(op_qname_parse("x", mapOf()), value, type)

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, valueMatcher)
            assertThat(items[0].type, typeMatcher)
        }

        private fun atomic_values(value: String, type: String, valueMatcher: Matcher<String>) {
            atomic(value, type, valueMatcher, `is`(type))
        }

        private fun atomic_types(value: String, type: String, typeMatcher: Matcher<String>) {
            atomic(value, type, `is`(value), typeMatcher)
        }

        private fun atomic(value: String, type: String) {
            atomic(value, type, `is`(value), `is`(type))
        }

        @Test @DisplayName("null") fun nullValue() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable(op_qname_parse("x", mapOf()), null, "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("empty-sequence()") fun emptySequence() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable(op_qname_parse("x", mapOf()), "()", "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
        @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic_values("bG9yZW0gaXBzdW0=", "xs:base64Binary", anyOf(`is`("bG9yZW0gaXBzdW0="), `is`("lorem ipsum"))) }
        @Test @DisplayName("xs:boolean") fun xsBoolean() { atomic("true", "xs:boolean") }
        @Test @DisplayName("xs:date") fun xsDate() { atomic("1995-10-12", "xs:date") }
        @Test @DisplayName("xs:dateTime") fun xsDateTime() { atomic("1995-10-12T11:22:33.444", "xs:dateTime") }
        @Test @DisplayName("xs:decimal") fun xsDecimal() { atomic("2.5", "xs:decimal") }
        @Test @DisplayName("xs:double") fun xsDouble() { atomic("2.5", "xs:double") }
        @Test @DisplayName("xs:duration") fun xsDuration() { atomic("P5D", "xs:duration") }
        @Test @DisplayName("xs:float") fun xsFloat() { atomic("2.5", "xs:float") }
        @Test @DisplayName("xs:gDay") fun xsGDay() { atomic("---12", "xs:gDay") }
        @Test @DisplayName("xs:gMonth") fun xsGMonth() { atomic("--10", "xs:gMonth") }
        @Test @DisplayName("xs:gMonthDay") fun xsGMonthDay() { atomic("--10-12", "xs:gMonthDay") }
        @Test @DisplayName("xs:gYear") fun xsGYear() { atomic("1995", "xs:gYear") }
        @Test @DisplayName("xs:gYearMonth") fun xsGYearMonth() { atomic("1995-10", "xs:gYearMonth") }
        @Test @DisplayName("xs:hexBinary") fun xsHexBinary() { atomic_values("6C6F72656D20697073756D", "xs:hexBinary", anyOf(`is`("6C6F72656D20697073756D"), `is`("lorem ipsum"))) }
        @Test @DisplayName("xs:integer") fun xsInteger() { atomic("2", "xs:integer") }
        @Test @DisplayName("xs:string") fun xsString() { atomic("lorem ipsum", "xs:string") }
        @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }

        @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
    }

    @Nested
    @DisplayName("bind context item from string with a specified type")
    internal inner class BindContextItemFromString {
        private fun atomic(value: String, type: String, valueMatcher: Matcher<String>, typeMatcher: Matcher<String>) {
            val q = processor.createQuery(".", MimeTypes.XQUERY)
            q.bindContextItem(value, type)

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, valueMatcher)
            assertThat(items[0].type, typeMatcher)
        }

        private fun atomic_values(value: String, type: String, valueMatcher: Matcher<String>) {
            atomic(value, type, valueMatcher, `is`(type))
        }

        private fun atomic_types(value: String, type: String, typeMatcher: Matcher<String>) {
            atomic(value, type, `is`(value), typeMatcher)
        }

        private fun atomic(value: String, type: String) {
            atomic(value, type, `is`(value), `is`(type))
        }

        @Test @DisplayName("null") fun nullValue() {
            val q = processor.createQuery(".", MimeTypes.XQUERY)
            q.bindContextItem(null, "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("empty-sequence()") fun emptySequence() {
            val q = processor.createQuery(".", MimeTypes.XQUERY)
            q.bindContextItem("()", "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
        @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic_values("bG9yZW0gaXBzdW0=", "xs:base64Binary", anyOf(`is`("bG9yZW0gaXBzdW0="), `is`("lorem ipsum"))) }
        @Test @DisplayName("xs:boolean") fun xsBoolean() { atomic("true", "xs:boolean") }
        @Test @DisplayName("xs:date") fun xsDate() { atomic("1995-10-12", "xs:date") }
        @Test @DisplayName("xs:dateTime") fun xsDateTime() { atomic("1995-10-12T11:22:33.444", "xs:dateTime") }
        @Test @DisplayName("xs:decimal") fun xsDecimal() { atomic("2.5", "xs:decimal") }
        @Test @DisplayName("xs:double") fun xsDouble() { atomic("2.5", "xs:double") }
        @Test @DisplayName("xs:duration") fun xsDuration() { atomic("P5D", "xs:duration") }
        @Test @DisplayName("xs:float") fun xsFloat() { atomic("2.5", "xs:float") }
        @Test @DisplayName("xs:gDay") fun xsGDay() { atomic("---12", "xs:gDay") }
        @Test @DisplayName("xs:gMonth") fun xsGMonth() { atomic("--10", "xs:gMonth") }
        @Test @DisplayName("xs:gMonthDay") fun xsGMonthDay() { atomic("--10-12", "xs:gMonthDay") }
        @Test @DisplayName("xs:gYear") fun xsGYear() { atomic("1995", "xs:gYear") }
        @Test @DisplayName("xs:gYearMonth") fun xsGYearMonth() { atomic("1995-10", "xs:gYearMonth") }
        @Test @DisplayName("xs:hexBinary") fun xsHexBinary() { atomic_values("6C6F72656D20697073756D", "xs:hexBinary", anyOf(`is`("6C6F72656D20697073756D"), `is`("lorem ipsum"))) }
        @Test @DisplayName("xs:integer") fun xsInteger() { atomic("2", "xs:integer") }
        @Test @DisplayName("xs:string") fun xsString() { atomic("lorem ipsum", "xs:string") }
        @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }

        @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
    }
}
