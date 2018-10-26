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
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertThrows
import uk.co.reecedunn.intellij.plugin.processor.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.QueryError
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
    val processorVersion = "9.0"
    val provider = BaseX(File("$home/xquery/basex/basex-9.0/BaseX.jar"))
    val processor: QueryProcessor = provider.create()

    @AfterAll
    fun tearDown() {
        processor.close()
    }

    @Test @DisplayName("version") fun version() {
        assertThat(processor.version, `is`(processorVersion))
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

        @Nested
        @DisplayName("sequence type")
        internal inner class SequenceType {
            @Test @DisplayName("empty-sequence()") fun emptySequence() {
                val q = processor.createQuery("()", MimeTypes.XQUERY)
                val items = q.run().toList()
                q.close()

                assertThat(items.size, `is`(0))
            }

            @Test @DisplayName("sequence (same type values)") fun sequenceSameTypeValues() {
                val q = processor.createQuery("(1, 2, 3)", MimeTypes.XQUERY)
                val items = q.run().toList()
                q.close()

                assertThat(items.size, `is`(3))
                assertThat(items[0].value, `is`("1"))
                assertThat(items[0].type, `is`("xs:integer"))
                assertThat(items[1].value, `is`("2"))
                assertThat(items[1].type, `is`("xs:integer"))
                assertThat(items[2].value, `is`("3"))
                assertThat(items[2].type, `is`("xs:integer"))
            }

            @Test @DisplayName("sequence (different type values)") fun sequenceDifferentTypeValues() {
                val q = processor.createQuery("(1 cast as xs:int, 2 cast as xs:byte, 3 cast as xs:decimal)", MimeTypes.XQUERY)
                val items = q.run().toList()
                q.close()

                assertThat(items.size, `is`(3))
                assertThat(items[0].value, `is`("1"))
                assertThat(items[0].type, `is`("xs:int"))
                assertThat(items[1].value, `is`("2"))
                assertThat(items[1].type, `is`("xs:byte"))
                assertThat(items[2].value, `is`("3"))
                assertThat(items[2].type, `is`("xs:decimal"))
            }
        }

        @Nested
        @DisplayName("atomic type (primitive)")
        internal inner class AtomicTypePrimitive {
            @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
            @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic_values("bG9yZW0gaXBzdW0=", "xs:base64Binary", anyOf(`is`("bG9yZW0gaXBzdW0="), `is`("lorem ipsum"))) }
            @Test @DisplayName("xs:boolean") fun xsBoolean() { atomic("true", "xs:boolean") }
            @Test @DisplayName("xs:date") fun xsDate() { atomic("1995-10-12", "xs:date") }
            @Test @DisplayName("xs:dateTime") fun xsDateTime() { atomic("1995-10-12T11:22:33.444", "xs:dateTime") }
            @Test @DisplayName("xs:dayTimeDuration") fun xsDayTimeDuration() { atomic("P5D", "xs:dayTimeDuration") }
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
            @Test @DisplayName("xs:string") fun xsString() { atomic_values("lorem &amp; ipsum", "xs:string", `is`("lorem & ipsum")) }
            @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }
            @Test @DisplayName("xs:untypedAtomic") fun xsUntypedAtomic() { atomic("2", "xs:untypedAtomic") }
            @Test @DisplayName("xs:yearMonthDuration") fun xsYearMonthDuration() { atomic("P5M", "xs:yearMonthDuration") }

            @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }

            @Test @DisplayName("xs:string (empty)") fun xsStringEmpty() { atomic("", "xs:string") }
        }

        @Nested
        @DisplayName("atomic type (derived)")
        internal inner class AtomicTypeDerived {
            @Test @DisplayName("xs:byte") fun xsByte() { atomic("2", "xs:byte") }
            @Test @DisplayName("xs:int") fun xsInt() { atomic("2", "xs:int") }
            @Test @DisplayName("xs:language") fun xsLanguage() { atomic("fr-FR", "xs:language") }
            @Test @DisplayName("xs:long") fun xsLong() { atomic("2", "xs:long") }
            @Test @DisplayName("xs:negativeInteger") fun xsNegativeInteger() { atomic("-2", "xs:negativeInteger") }
            @Test @DisplayName("xs:nonNegativeInteger") fun xsNonNegativeInteger() { atomic("2", "xs:nonNegativeInteger") }
            @Test @DisplayName("xs:nonPositiveInteger") fun xsNonPositiveInteger() { atomic("-2", "xs:nonPositiveInteger") }
            @Test @DisplayName("xs:normalizedString") fun xsNormalizedString() { atomic("lorem ipsum", "xs:normalizedString") }
            @Test @DisplayName("xs:positiveInteger") fun xsPositiveInteger() { atomic("2", "xs:positiveInteger") }
            @Test @DisplayName("xs:short") fun xsShort() { atomic("2", "xs:short") }
            @Test @DisplayName("xs:token") fun xsToken() { atomic("2", "xs:token") }
            @Test @DisplayName("xs:unsignedByte") fun xsUnsignedByte() { atomic("2", "xs:unsignedByte") }
            @Test @DisplayName("xs:unsignedInt") fun xsUnsignedInt() { atomic("2", "xs:unsignedInt") }
            @Test @DisplayName("xs:unsignedLong") fun xsUnsignedLong() { atomic("2", "xs:unsignedLong") }
            @Test @DisplayName("xs:unsignedShort") fun xsUnsignedShort() { atomic("2", "xs:unsignedShort") }

            @Test @DisplayName("xs:ENTITY") fun xsENTITY() { atomic("lorem-ipsum", "xs:ENTITY") }
            @Test @DisplayName("xs:ID") fun xsID() { atomic("lorem-ipsum", "xs:ID") }
            @Test @DisplayName("xs:IDREF") fun xsIDREF() { atomic("lorem-ipsum", "xs:IDREF") }
            @Test @DisplayName("xs:Name") fun xsName() { atomic("lorem-ipsum", "xs:Name") }
            @Test @DisplayName("xs:NCName") fun xsNCName() { atomic("lorem-ipsum", "xs:NCName") }
            @Test @DisplayName("xs:NMTOKEN") fun xsNMTOKEN() { atomic("lorem-ipsum", "xs:NMTOKEN") }
        }

        @Nested
        @DisplayName("atomic type (derived; XML Schema 1.1 Part 2)")
        internal inner class AtomicTypeDerivedXSD11 {
            @Test @DisplayName("xs:dateTimeStamp") fun xsDateTimeStamp() { atomic("1995-10-12T11:22:33.444Z", "xs:dateTimeStamp") }
        }

        @Nested
        @DisplayName("union type")
        internal inner class UnionType {
            @Test @DisplayName("xs:numeric") fun xsNumeric() { atomic_types("2", "xs:numeric", anyOf(`is`("xs:double"), `is`("xs:anySimpleType"))) }
        }
    }

    @Nested
    @DisplayName("bind variable")
    internal inner class BindVariableName {
        @Test @DisplayName("by NCName") fun ncname() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable(op_qname_parse("x", mapOf()), "2", "xs:integer")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, `is`("2"))
            assertThat(items[0].type, `is`("xs:integer"))
        }
        @Test @DisplayName("by URIQualifiedName") fun uriQualifiedName() {
            val q = processor.createQuery("declare variable \$Q{http://www.example.co.uk}x external; \$x", MimeTypes.XQUERY)
            q.bindVariable(op_qname_parse("Q{http://www.example.co.uk}x", mapOf()), "2", "xs:integer")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, `is`("2"))
            assertThat(items[0].type, `is`("xs:integer"))
        }
        @Test @DisplayName("by QName") fun qname() {
            val q = processor.createQuery("declare variable \$local:x external; \$x", MimeTypes.XQUERY)
            q.bindVariable(op_qname_parse("local:x", mapOf("local" to "http://www.w3.org/2005/xquery-local-functions")), "2", "xs:integer")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(1))
            assertThat(items[0].value, `is`("2"))
            assertThat(items[0].type, `is`("xs:integer"))
        }

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

        @Test @DisplayName("as null") fun nullValue() {
            val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
            q.bindVariable(op_qname_parse("x", mapOf()), null, "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Nested
        @DisplayName("as sequence type")
        internal inner class SequenceType {
            @Test @DisplayName("empty-sequence()") fun emptySequence() {
                val q = processor.createQuery("declare variable \$x external; \$x", MimeTypes.XQUERY)
                q.bindVariable(op_qname_parse("x", mapOf()), "()", "empty-sequence()")

                val items = q.run().toList()
                q.close()

                assertThat(items.size, `is`(0))
            }
        }

        @Nested
        @DisplayName("as atomic type (primitive)")
        internal inner class AtomicTypePrimitive {
            @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
            @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic_values("bG9yZW0gaXBzdW0=", "xs:base64Binary", anyOf(`is`("bG9yZW0gaXBzdW0="), `is`("lorem ipsum"))) }
            @Test @DisplayName("xs:boolean") fun xsBoolean() { atomic("true", "xs:boolean") }
            @Test @DisplayName("xs:date") fun xsDate() { atomic("1995-10-12", "xs:date") }
            @Test @DisplayName("xs:dateTime") fun xsDateTime() { atomic("1995-10-12T11:22:33.444", "xs:dateTime") }
            @Test @DisplayName("xs:dayTimeDuration") fun xsDayTimeDuration() { atomic("P5D", "xs:dayTimeDuration") }
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
            @Test @DisplayName("xs:string") fun xsString() { atomic("lorem & ipsum", "xs:string") }
            @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }
            @Test @DisplayName("xs:untypedAtomic") fun xsUntypedAtomic() { atomic("2", "xs:untypedAtomic") }
            @Test @DisplayName("xs:yearMonthDuration") fun xsYearMonthDuration() { atomic("P5M", "xs:yearMonthDuration") }

            @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
        }

        @Nested
        @DisplayName("as atomic type (derived)")
        internal inner class AtomicTypeDerived {
            @Test @DisplayName("xs:byte") fun xsByte() { atomic("2", "xs:byte") }
            @Test @DisplayName("xs:int") fun xsInt() { atomic("2", "xs:int") }
            @Test @DisplayName("xs:language") fun xsLanguage() { atomic("fr-FR", "xs:language") }
            @Test @DisplayName("xs:long") fun xsLong() { atomic("2", "xs:long") }
            @Test @DisplayName("xs:negativeInteger") fun xsNegativeInteger() { atomic("-2", "xs:negativeInteger") }
            @Test @DisplayName("xs:nonNegativeInteger") fun xsNonNegativeInteger() { atomic("2", "xs:nonNegativeInteger") }
            @Test @DisplayName("xs:nonPositiveInteger") fun xsNonPositiveInteger() { atomic("-2", "xs:nonPositiveInteger") }
            @Test @DisplayName("xs:normalizedString") fun xsNormalizedString() { atomic("lorem ipsum", "xs:normalizedString") }
            @Test @DisplayName("xs:positiveInteger") fun xsPositiveInteger() { atomic("2", "xs:positiveInteger") }
            @Test @DisplayName("xs:short") fun xsShort() { atomic("2", "xs:short") }
            @Test @DisplayName("xs:token") fun xsToken() { atomic("2", "xs:token") }
            @Test @DisplayName("xs:unsignedByte") fun xsUnsignedByte() { atomic("2", "xs:unsignedByte") }
            @Test @DisplayName("xs:unsignedInt") fun xsUnsignedInt() { atomic("2", "xs:unsignedInt") }
            @Test @DisplayName("xs:unsignedLong") fun xsUnsignedLong() { atomic("2", "xs:unsignedLong") }
            @Test @DisplayName("xs:unsignedShort") fun xsUnsignedShort() { atomic("2", "xs:unsignedShort") }

            @Test @DisplayName("xs:ENTITY") fun xsENTITY() { atomic("lorem-ipsum", "xs:ENTITY") }
            @Test @DisplayName("xs:ID") fun xsID() { atomic("lorem-ipsum", "xs:ID") }
            @Test @DisplayName("xs:IDREF") fun xsIDREF() { atomic("lorem-ipsum", "xs:IDREF") }
            @Test @DisplayName("xs:Name") fun xsName() { atomic("lorem-ipsum", "xs:Name") }
            @Test @DisplayName("xs:NCName") fun xsNCName() { atomic("lorem-ipsum", "xs:NCName") }
            @Test @DisplayName("xs:NMTOKEN") fun xsNMTOKEN() { atomic("lorem-ipsum", "xs:NMTOKEN") }
        }

        @Nested
        @DisplayName("as atomic type (derived; XML Schema 1.1 Part 2)")
        internal inner class AtomicTypeDerivedXSD11 {
            @Test @DisplayName("xs:dateTimeStamp") fun xsDateTimeStamp() { atomic("1995-10-12T11:22:33.444Z", "xs:dateTimeStamp") }
        }

        @Nested
        @DisplayName("as union type")
        internal inner class UnionType {
            @Test @DisplayName("xs:numeric") fun xsNumeric() { atomic_types("2", "xs:numeric", `is`("xs:double")) }
        }
    }

    @Nested
    @DisplayName("bind context item")
    internal inner class BindContextItem {
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

        @Test @DisplayName("as null") fun nullValue() {
            val q = processor.createQuery(".", MimeTypes.XQUERY)
            q.bindContextItem(null, "empty-sequence()")

            val items = q.run().toList()
            q.close()

            assertThat(items.size, `is`(0))
        }

        @Nested
        @DisplayName("as sequence type")
        internal inner class SequenceType {
            @Test @DisplayName("empty-sequence()") fun emptySequence() {
                val q = processor.createQuery(".", MimeTypes.XQUERY)
                q.bindContextItem("()", "empty-sequence()")

                val items = q.run().toList()
                q.close()

                assertThat(items.size, `is`(0))
            }
        }

        @Nested
        @DisplayName("as atomic type (primitive)")
        internal inner class AtomicTypePrimitive {
            @Test @DisplayName("xs:anyURI") fun xsAnyURI() { atomic("http://www.example.co.uk", "xs:anyURI") }
            @Test @DisplayName("xs:base64Binary") fun xsBase64Binary() { atomic_values("bG9yZW0gaXBzdW0=", "xs:base64Binary", anyOf(`is`("bG9yZW0gaXBzdW0="), `is`("lorem ipsum"))) }
            @Test @DisplayName("xs:boolean") fun xsBoolean() { atomic("true", "xs:boolean") }
            @Test @DisplayName("xs:date") fun xsDate() { atomic("1995-10-12", "xs:date") }
            @Test @DisplayName("xs:dateTime") fun xsDateTime() { atomic("1995-10-12T11:22:33.444", "xs:dateTime") }
            @Test @DisplayName("xs:dayTimeDuration") fun xsDayTimeDuration() { atomic("P5D", "xs:dayTimeDuration") }
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
            @Test @DisplayName("xs:string") fun xsString() { atomic("lorem & ipsum", "xs:string") }
            @Test @DisplayName("xs:time") fun xsTime() { atomic("11:22:33.444", "xs:time") }
            @Test @DisplayName("xs:untypedAtomic") fun xsUntypedAtomic() { atomic("2", "xs:untypedAtomic") }
            @Test @DisplayName("xs:yearMonthDuration") fun xsYearMonthDuration() { atomic("P5M", "xs:yearMonthDuration") }

            @Test @DisplayName("xs:QName") fun xsQName() { atomic("xs:string", "xs:QName") }
        }

        @Nested
        @DisplayName("as atomic type (derived)")
        internal inner class AtomicTypeDerived {
            @Test @DisplayName("xs:byte") fun xsByte() { atomic("2", "xs:byte") }
            @Test @DisplayName("xs:int") fun xsInt() { atomic("2", "xs:int") }
            @Test @DisplayName("xs:language") fun xsLanguage() { atomic("fr-FR", "xs:language") }
            @Test @DisplayName("xs:long") fun xsLong() { atomic("2", "xs:long") }
            @Test @DisplayName("xs:negativeInteger") fun xsNegativeInteger() { atomic("-2", "xs:negativeInteger") }
            @Test @DisplayName("xs:nonNegativeInteger") fun xsNonNegativeInteger() { atomic("2", "xs:nonNegativeInteger") }
            @Test @DisplayName("xs:nonPositiveInteger") fun xsNonPositiveInteger() { atomic("-2", "xs:nonPositiveInteger") }
            @Test @DisplayName("xs:normalizedString") fun xsNormalizedString() { atomic("lorem ipsum", "xs:normalizedString") }
            @Test @DisplayName("xs:positiveInteger") fun xsPositiveInteger() { atomic("2", "xs:positiveInteger") }
            @Test @DisplayName("xs:short") fun xsShort() { atomic("2", "xs:short") }
            @Test @DisplayName("xs:token") fun xsToken() { atomic("2", "xs:token") }
            @Test @DisplayName("xs:unsignedByte") fun xsUnsignedByte() { atomic("2", "xs:unsignedByte") }
            @Test @DisplayName("xs:unsignedInt") fun xsUnsignedInt() { atomic("2", "xs:unsignedInt") }
            @Test @DisplayName("xs:unsignedLong") fun xsUnsignedLong() { atomic("2", "xs:unsignedLong") }
            @Test @DisplayName("xs:unsignedShort") fun xsUnsignedShort() { atomic("2", "xs:unsignedShort") }

            @Test @DisplayName("xs:ENTITY") fun xsENTITY() { atomic("lorem-ipsum", "xs:ENTITY") }
            @Test @DisplayName("xs:ID") fun xsID() { atomic("lorem-ipsum", "xs:ID") }
            @Test @DisplayName("xs:IDREF") fun xsIDREF() { atomic("lorem-ipsum", "xs:IDREF") }
            @Test @DisplayName("xs:Name") fun xsName() { atomic("lorem-ipsum", "xs:Name") }
            @Test @DisplayName("xs:NCName") fun xsNCName() { atomic("lorem-ipsum", "xs:NCName") }
            @Test @DisplayName("xs:NMTOKEN") fun xsNMTOKEN() { atomic("lorem-ipsum", "xs:NMTOKEN") }
        }

        @Nested
        @DisplayName("as atomic type (derived; XML Schema 1.1 Part 2)")
        internal inner class AtomicTypeDerivedXSD11 {
            @Test @DisplayName("xs:dateTimeStamp") fun xsDateTimeStamp() { atomic("1995-10-12T11:22:33.444Z", "xs:dateTimeStamp") }
        }

        @Nested
        @DisplayName("as union type")
        internal inner class UnionType {
            @Test @DisplayName("xs:numeric") fun xsNumeric() { atomic_types("2", "xs:numeric", `is`("xs:double")) }
        }
    }

    @Nested
    @DisplayName("error")
    internal inner class Error {
        fun parse(query: String): QueryError {
            return processor.createQuery(query, MimeTypes.XQUERY).use {
                assertThrows(QueryError::class.java) { it.run().toList() }
            }
        }

        @Test
        @DisplayName("err:code")
        fun code() {
            assertThat(parse("(1, 2,").code, `is`("err:XPST0003"))
        }

        @Test
        @DisplayName("err:description")
        fun description() {
            assertThat(
                parse("(1, 2,").description,
                anyOf(
                    // BaseX:
                    `is`("Incomplete expression."),
                    // MarkLogic:
                    `is`("XDMP-UNEXPECTED: (err:XPST0003) Unexpected token syntax error, unexpected \$end, expecting Function30_ or Percent_")
                )
            )
        }

        @Test
        @DisplayName("err:module")
        fun module() {
            assertThat(parse("(1, 2,").module, `is`(nullValue()))
        }

        @Test
        @DisplayName("err:line-number")
        fun lineNumber() {
            assertThat(parse("(1, 2,").lineNumber, `is`(1))
        }

        @Test
        @DisplayName("err:column-number")
        fun columnNumber() {
            assertThat(parse("(1, 2,").columnNumber, `is`(5))
        }
    }
}
