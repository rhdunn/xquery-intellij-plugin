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
package uk.co.reecedunn.intellij.plugin.exquery.restxq.endpoints

import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xdm.types.XsStringValue

class RestXqAnnotations(private val annotations: List<XdmAnnotation>) {
    private fun annotation(name: String): XdmAnnotation? = annotations.find { it.name?.localName?.data == name }

    private fun strings(name: String): Sequence<String> {
        return annotation(name)?.values?.filterIsInstance<XsStringValue>()?.map { it.data } ?: sequenceOf()
    }

    // 3.2.1 Path Annotation
    val path: String? get() = strings("path").firstOrNull()

    // 3.2.2 Method Annotation
    val methods: Sequence<String>
        get() = annotations.asSequence().mapNotNull { it.name?.localName?.data }.filter { METHODS.contains(it) }

    // 3.2.3 Consumes Annotation
    val consumes: Sequence<String> get() = strings("consumes")

    // 3.2.4 Produces Annotation
    val produces: Sequence<String> get() = strings("produces")

    // 3.3.1 Query Parameters
    val queryParam: Sequence<String>
        get() = annotations.asSequence().filter { it.name?.localName?.data == "query-param" }.mapNotNull {
            it.values.filterIsInstance<XsStringValue>().firstOrNull()?.data
        }

    companion object {
        // NOTE: RESTXQ only supports HTTP 1.1 methods, excluding TRACE and CONNECT.
        private val METHODS = setOf(
            "OPTIONS", // HTTP 1.1 (9.2)
            "GET", // HTTP 1.1 (9.3)
            "HEAD", // HTTP 1.1 (9.4)
            "POST", // HTTP 1.1 (9.5)
            "PUT", // HTTP 1.1 (9.6)
            "DELETE" // HTTP 1.1 (9.7)
        )
    }
}
