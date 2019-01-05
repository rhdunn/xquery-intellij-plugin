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
package uk.co.reecedunn.intellij.plugin.processor.query

fun primitiveToItemType(primitive: String): String {
    return if (primitive.endsWith("-order") ||
        primitive.endsWith("-query") ||
        primitive.endsWith("-reference"))
        "cts:$primitive"
    else when (primitive) {
        // CTS types ============================
        "box",
        "circle",
        "complex-polygon",
        "linestring",
        "period",
        "point",
        "polygon",
        "region",
        "unordered" ->
            "cts:$primitive"
        // JSON types ===========================
        // - `json:null()` is equivalent to `()`, so is not returned as a primitive name.
        // - `map` includes other map-like types, such as `sem:binding`.
        "map" ->
            "map:map"
        "array",
        "object" ->
            "json:$primitive"
        // Semantics types ======================
        "store",
        "triple" ->
            "sem:$primitive"
        // XMLSchema types ======================
        // - `xs:anySimpleType` is returned instead of `xs:numeric`.
        // - `xs:anyURI` includes other URI-like types, such as `sem:bnode` and `sem:iri`.
        // - `xs:integer` includes other integer-like types, such as `xs:byte`.
        // - `xs:string` includes other string-like types, such as `xs:language` and `cts:token`.
        // - `xs:untypedAtomic` includes other atomic-like types, such as `sem:invalid` and `sem:unknown`.
        "anySimpleType", "anyURI",
        "base64Binary", "boolean",
        "date", "dateTime", "dayTimeDuration", "decimal", "double", "duration",
        "float",
        "gDay", "gMonth", "gMonthDay", "gYear", "gYearMonth",
        "hexBinary",
        "integer",
        "QName",
        "string",
        "time",
        "untypedAtomic",
        "yearMonthDuration" ->
            "xs:$primitive"
        // other types ==========================
        // - Don't throw an error here, so unknown types will still work
        //   (unlike with the XCC API).
        // - This also handles node types that have the same name as the
        //   primitive.
        else -> primitive
    }
}

fun mimetypeFromXQueryItemType(type: String): String {
    return when (type.substringBefore("(")) {
        "array-node", "boolean-node", "null-node", "number-node", "object-node" -> "application/json"
        "binary" -> "application/octet-stream"
        "document-node", "element" -> "application/xml"
        else -> "text/plain"
    }
}

data class QueryResult(val position: Long, val value: Any, val type: String, val mimetype: String) {
    companion object {
        fun fromItemType(position: Long, value: String, type: String): QueryResult =
            QueryResult(position, value, type, mimetypeFromXQueryItemType(type))
    }
}
