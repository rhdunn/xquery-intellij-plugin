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
import java.io.File

@Suppress("Reformat")
@DisplayName("XQuery processor")
class ProcessorTest {
    val home = System.getProperty("user.home")
    // Modify these for the processor being tested:
    val provider = BaseX(File("$home/xquery/basex/basex-9.0/BaseX.jar"))
    val processor: QueryProcessor = provider.create()

    @Nested
    @DisplayName("return values")
    internal inner class ReturnValues {
        private fun atomic(value: String, type: String, value2: String? = null) {
            val q = processor.createQuery("\"$value\" cast as $type", MimeTypes.XQUERY)
            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, `is`(anyOf(`is`(value), `is`(value2 ?: value))))
            assertThat(items[0].type, `is`(type))
        }

        @Test @DisplayName("empty-sequence()") fun emptySequence() {
            val q = processor.createQuery("()", MimeTypes.XQUERY)
            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
        @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic("bG9yZW0gaXBzdW0=", "xs:base64Binary", "lorem ipsum") }
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
        @Test @DisplayName("xs:hexBinary") fun xsHexBinary() { atomic("6C6F72656D20697073756D", "xs:hexBinary", "lorem ipsum") }
        @Test @DisplayName("xs:integer") fun xsInteger() { atomic("2", "xs:integer") }
        @Test @DisplayName("xs:string") fun xsString() { atomic("lorem ipsum", "xs:string") }
        @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }

        @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
    }

    @Nested
    @DisplayName("bind variable using local-name from string with a specified type")
    internal inner class BindVariableNCNameFromString {
        private fun atomic(value: String, type: String, value2: String? = null) {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable("x", value, type)

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, `is`(anyOf(`is`(value), `is`(value2 ?: value))))
            assertThat(items[0].type, `is`(type))
        }

        @Test @DisplayName("null") fun nullValue() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable("x", null, "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("empty-sequence()") fun emptySequence() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable("x", "()", "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
        @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic("bG9yZW0gaXBzdW0=", "xs:base64Binary", "lorem ipsum") }
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
        @Test @DisplayName("xs:hexBinary") fun xsHexBinary() { atomic("6C6F72656D20697073756D", "xs:hexBinary", "lorem ipsum") }
        @Test @DisplayName("xs:integer") fun xsInteger() { atomic("2", "xs:integer") }
        @Test @DisplayName("xs:string") fun xsString() { atomic("lorem ipsum", "xs:string") }
        @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }

        @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
    }

    @Nested
    @DisplayName("bind variable using Q{}local-name from string with a specified type")
    internal inner class BindVariableURIQualifiedNameFromString {
        private fun atomic(value: String, type: String, value2: String? = null) {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable("Q{}x", value, type)

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, `is`(anyOf(`is`(value), `is`(value2 ?: value))))
            assertThat(items[0].type, `is`(type))
        }

        @Test @DisplayName("null") fun nullValue() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable("Q{}x", null, "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("empty-sequence()") fun emptySequence() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable("Q{}x", "()", "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
        @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic("bG9yZW0gaXBzdW0=", "xs:base64Binary", "lorem ipsum") }
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
        @Test @DisplayName("xs:hexBinary") fun xsHexBinary() { atomic("6C6F72656D20697073756D", "xs:hexBinary", "lorem ipsum") }
        @Test @DisplayName("xs:integer") fun xsInteger() { atomic("2", "xs:integer") }
        @Test @DisplayName("xs:string") fun xsString() { atomic("lorem ipsum", "xs:string") }
        @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }

        @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
    }

    @Nested
    @DisplayName("bind context item from string with a specified type")
    internal inner class BindContextItemFromString {
        private fun atomic(value: String, type: String, value2: String? = null) {
            val q = processor.createQuery(".", MimeTypes.XQUERY)
            q.bindContextItem(value, type)

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, `is`(anyOf(`is`(value), `is`(value2 ?: value))))
            assertThat(items[0].type, `is`(type))
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
        @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic("bG9yZW0gaXBzdW0=", "xs:base64Binary", "lorem ipsum") }
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
        @Test @DisplayName("xs:hexBinary") fun xsHexBinary() { atomic("6C6F72656D20697073756D", "xs:hexBinary", "lorem ipsum") }
        @Test @DisplayName("xs:integer") fun xsInteger() { atomic("2", "xs:integer") }
        @Test @DisplayName("xs:string") fun xsString() { atomic("lorem ipsum", "xs:string") }
        @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }

        @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
    }
}
