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
package uk.co.reecedunn.intellij.plugin.processor.tests

import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.co.reecedunn.intellij.plugin.core.tests.assertion.assertThat
import uk.co.reecedunn.intellij.plugin.processor.primitiveToItemType

@DisplayName("IntelliJ - Base Platform - Run Configuration - XQuery Processor - primitiveToItemType")
class PrimitiveToItemTypeTest {
    @Test
    @DisplayName("node types")
    fun nodeTypes() {
        assertThat(primitiveToItemType("array-node()"), `is`("array-node()"))
        assertThat(primitiveToItemType("attribute()"), `is`("attribute()"))
        assertThat(primitiveToItemType("binary()"), `is`("binary()"))
        assertThat(primitiveToItemType("boolean-node()"), `is`("boolean-node()"))
        assertThat(primitiveToItemType("comment()"), `is`("comment()"))
        assertThat(primitiveToItemType("document-node()"), `is`("document-node()"))
        assertThat(primitiveToItemType("element()"), `is`("element()"))
        assertThat(primitiveToItemType("node()"), `is`("node()"))
        assertThat(primitiveToItemType("null-node()"), `is`("null-node()"))
        assertThat(primitiveToItemType("number-node()"), `is`("number-node()"))
        assertThat(primitiveToItemType("object-node()"), `is`("object-node()"))
        assertThat(primitiveToItemType("processing-instruction()"), `is`("processing-instruction()"))
        assertThat(primitiveToItemType("text()"), `is`("text()"))
    }

    @Test
    @DisplayName("XML Schema types")
    fun xmlSchemaTypes() {
        assertThat(primitiveToItemType("anyURI"), `is`("xs:anyURI"))
        assertThat(primitiveToItemType("base64Binary"), `is`("xs:base64Binary"))
        assertThat(primitiveToItemType("boolean"), `is`("xs:boolean"))
        assertThat(primitiveToItemType("date"), `is`("xs:date"))
        assertThat(primitiveToItemType("dateTime"), `is`("xs:dateTime"))
        assertThat(primitiveToItemType("dayTimeDuration"), `is`("xs:dayTimeDuration"))
        assertThat(primitiveToItemType("decimal"), `is`("xs:decimal"))
        assertThat(primitiveToItemType("double"), `is`("xs:double"))
        assertThat(primitiveToItemType("duration"), `is`("xs:duration"))
        assertThat(primitiveToItemType("float"), `is`("xs:float"))
        assertThat(primitiveToItemType("gDay"), `is`("xs:gDay"))
        assertThat(primitiveToItemType("gMonth"), `is`("xs:gMonth"))
        assertThat(primitiveToItemType("gMonthDay"), `is`("xs:gMonthDay"))
        assertThat(primitiveToItemType("gYear"), `is`("xs:gYear"))
        assertThat(primitiveToItemType("gYearMonth"), `is`("xs:gYearMonth"))
        assertThat(primitiveToItemType("hexBinary"), `is`("xs:hexBinary"))
        assertThat(primitiveToItemType("integer"), `is`("xs:integer"))
        assertThat(primitiveToItemType("QName"), `is`("xs:QName"))
        assertThat(primitiveToItemType("string"), `is`("xs:string"))
        assertThat(primitiveToItemType("time"), `is`("xs:time"))
        assertThat(primitiveToItemType("untypedAtomic"), `is`("xs:untypedAtomic"))
        assertThat(primitiveToItemType("yearMonthDuration"), `is`("xs:yearMonthDuration"))
    }

    @Test
    @DisplayName("MarkLogic; basic types")
    fun markLogic_basicTypes() {
        assertThat(primitiveToItemType("array"), `is`("json:array"))
        assertThat(primitiveToItemType("box"), `is`("cts:box"))
        assertThat(primitiveToItemType("circle"), `is`("cts:circle"))
        assertThat(primitiveToItemType("complex-polygon"), `is`("cts:complex-polygon"))
        assertThat(primitiveToItemType("linestring"), `is`("cts:linestring"))
        assertThat(primitiveToItemType("map"), `is`("map:map"))
        assertThat(primitiveToItemType("object"), `is`("json:object"))
        assertThat(primitiveToItemType("period"), `is`("cts:period"))
        assertThat(primitiveToItemType("point"), `is`("cts:point"))
        assertThat(primitiveToItemType("polygon"), `is`("cts:polygon"))
        assertThat(primitiveToItemType("region"), `is`("cts:region"))
        assertThat(primitiveToItemType("store"), `is`("sem:store"))
        assertThat(primitiveToItemType("triple"), `is`("sem:triple"))
    }

    @Test
    @DisplayName("MarkLogic; CTS order types")
    fun markLogic_ctsOrderTypes() {
        assertThat(primitiveToItemType("confidence-order"), `is`("cts:confidence-order"))
        assertThat(primitiveToItemType("document-order"), `is`("cts:document-order"))
        assertThat(primitiveToItemType("fitness-order"), `is`("cts:fitness-order"))
        assertThat(primitiveToItemType("index-order"), `is`("cts:index-order"))
        assertThat(primitiveToItemType("quality-order"), `is`("cts:quality-order"))
        assertThat(primitiveToItemType("score-order"), `is`("cts:score-order"))
        assertThat(primitiveToItemType("unordered"), `is`("cts:unordered"))
    }

    @Test
    @DisplayName("MarkLogic; CTS query types")
    fun markLogic_ctsQueryTypes() {
        assertThat(primitiveToItemType("collection-query"), `is`("cts:collection-query"))
        assertThat(primitiveToItemType("element-query"), `is`("cts:element-query"))
        assertThat(primitiveToItemType("field-range-query"), `is`("cts:field-range-query"))
    }

    @Test
    @DisplayName("MarkLogic; CTS reference types")
    fun markLogic_ctsReferenceTypes() {
        assertThat(primitiveToItemType("collection-reference"), `is`("cts:collection-reference"))
        assertThat(primitiveToItemType("element-reference"), `is`("cts:element-reference"))
        assertThat(primitiveToItemType("field-reference"), `is`("cts:field-reference"))
    }
}
