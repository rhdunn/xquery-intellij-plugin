/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration

class RestXqAnnotations(private val annotations: List<XdmAnnotation>) {
    private fun strings(name: String): Sequence<String> {
        return annotations.asSequence().filter { it.name?.localName?.data == name }.flatMap {
            it.values.filterIsInstance<XsStringValue>().map { string -> string.data }
        }
    }

    private fun params(name: String): Sequence<String> {
        return annotations.asSequence().filter { it.name?.localName?.data == name }.mapNotNull {
            it.values.filterIsInstance<XsStringValue>().firstOrNull()?.data
        }
    }

    // 3.2.1 Path Annotation
    val path: String?
        get() = strings("path").firstOrNull()

    // 3.2.2 Method Annotation
    val methods: Sequence<String>
        get() = annotations.asSequence().mapNotNull { it.name?.localName?.data }.filter { METHODS.contains(it) }

    // 3.2.3 Consumes Annotation
    @Suppress("unused")
    val consumes: Sequence<String>
        get() = strings("consumes")

    // 3.2.4 Produces Annotation
    @Suppress("unused")
    val produces: Sequence<String>
        get() = strings("produces")

    // 3.3.1 Query Parameters
    @Suppress("unused")
    val queryParams: Sequence<String>
        get() = params("query-param")

    // 3.3.2 Form Parameters
    @Suppress("unused")
    val formParams: Sequence<String>
        get() = params("form-param")

    // 3.3.3 HTTP Header Parameters
    @Suppress("unused")
    val headerParams: Sequence<String>
        get() = params("header-param")

    // 3.3.4 Cookie Parameters
    @Suppress("unused")
    val cookieParams: Sequence<String>
        get() = params("cookie-param")

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

        private const val RESTXQ_NAMESPACE: String = "http://exquery.org/ns/restxq"

        fun create(function: XpmFunctionDeclaration): RestXqAnnotations? {
            val annotations = function.annotations.filter { annotation ->
                annotation.name?.expand()?.find { it.namespace?.data == RESTXQ_NAMESPACE } != null
            }
            return annotations.takeIf { it.any() }?.let { RestXqAnnotations(it.toList()) }
        }
    }
}
