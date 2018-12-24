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
package uk.co.reecedunn.intellij.plugin.saxon.query.s9api

import uk.co.reecedunn.intellij.plugin.processor.query.QueryError

private val ERR_NS = "http://www.w3.org/2005/xqt-errors"

internal class SaxonQueryError(e: Any, classes: SaxonClasses) : QueryError() {
    override val standardCode: String by lazy {
        val qname = classes.saxonApiExceptionClass.getMethod("getErrorCode").invoke(e)
        val ns = classes.qnameClass.getMethod("getNamespaceURI").invoke(qname)
        val prefix = classes.qnameClass.getMethod("getPrefix").invoke(qname)
        val localname = classes.qnameClass.getMethod("getLocalName").invoke(qname)
        if (ns == ERR_NS || prefix == null)
            localname as String
        else
            "$prefix:$localname"
    }

    override val vendorCode: String? = null

    override val description: String? by lazy {
        classes.saxonApiExceptionClass.getMethod("getMessage").invoke(e) as String?
    }

    override val module: String? by lazy {
        classes.saxonApiExceptionClass.getMethod("getSystemId").invoke(e) as String?
    }

    override val lineNumber: Int? by lazy {
        classes.saxonApiExceptionClass.getMethod("getLineNumber").invoke(e) as Int?
    }

    override val columnNumber: Int? = 1
}
