/*
 * Copyright (C) 2017-2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.tests.query

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - QueryResult")
class QueryResultTest {
    @Nested
    @DisplayName("from ItemType")
    internal inner class FromItemType {
        private fun test(value: String, type: String, mimetype: String) {
            val result = QueryResult.fromItemType(value, type)
            assertThat(result.value, `is`(value))
            assertThat(result.type, `is`(type))
            assertThat(result.mimetype, `is`(mimetype))
        }

        @Test
        @DisplayName("node types")
        fun nodeTypes() {
            test("[]", "array-node()", "application/json") // MarkLogic
            test("lorem ipsum", "attribute()", "text/plain")
            test("6C6F72656D20697073756D", "binary()", "application/octet-stream") // MarkLogic
            test("true", "boolean-node()", "application/json") // MarkLogic
            test("lorem ipsum", "comment()", "text/plain")
            test("<test/>", "document-node()", "application/xml")
            test("<test/>", "element()", "application/xml")
            test("<test/>", "element(test)", "application/xml")
            test("fn:true#0", "function()", "text/plain")
            test("null", "null-node()", "application/json") // MarkLogic
            test("2", "number-node()", "application/json") // MarkLogic
            test("{}", "object-node()", "application/json") // MarkLogic
            test("<?xml?>", "processing-instruction()", "text/plain")
            test("lorem ipsum", "text()", "text/plain")
        }

        @Test
        @DisplayName("primitive types")
        fun primitiveTypes() {
            test("http://www.example.co.uk", "xs:anyURI", "text/plain")
            test("bG9yZW0gaXBzdW0=", "xs:base64Binary", "text/plain")
            test("true", "xs:boolean", "text/plain")
            test("2000-01-02", "xs:date", "text/plain")
            test("2000-01-02T10:11:12", "xs:dateTime", "text/plain")
            test("P5D", "xs:dayTimeDuration", "text/plain")
            test("2", "xs:decimal", "text/plain")
            test("2", "xs:double", "text/plain")
            test("P5D", "xs:duration", "text/plain")
            test("2", "xs:float", "text/plain")
            test("---12", "gDay", "text/plain")
            test("--10", "gMonth", "text/plain")
            test("--10-12", "gMonthDay", "text/plain")
            test("2000", "xs:gYear", "text/plain")
            test("2000-01", "xs:gYearMonth", "text/plain")
            test("6C6F72656D20697073756D", "xs:hexBinary", "text/plain")
            test("2", "xs:integer", "text/plain")
            test("lorem:ipsum", "xs:QName", "text/plain")
            test("lorem ipsum", "xs:string", "text/plain")
            test("10:11:12", "xs:time", "text/plain")
            test("P2Y", "xs:yearMonthDuration", "text/plain")
        }
    }
}
