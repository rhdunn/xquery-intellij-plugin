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
package uk.co.reecedunn.intellij.plugin.processor.saxon.s9api

import uk.co.reecedunn.intellij.plugin.core.reflection.getAnyMethod
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import javax.xml.transform.TransformerException

private val ERR_NS = "http://www.w3.org/2005/xqt-errors"

internal class SaxonTransformerQueryError(e: TransformerException, classes: SaxonClasses) : QueryError() {
    override val standardCode: String by lazy {
        val qname = classes.xpathExceptionClass.getMethod("getErrorCodeQName").invoke(e)
        val ns = classes.structuredQNameClass.getAnyMethod("getURI", "getNamespaceURI").invoke(qname)
        val prefix = classes.structuredQNameClass.getMethod("getPrefix").invoke(qname)
        val localname = classes.structuredQNameClass.getAnyMethod("getLocalPart", "getLocalName").invoke(qname)
        if (ns == ERR_NS || prefix == null)
            localname as String
        else
            "$prefix:$localname"
    }

    override val vendorCode: String? = null

    override val description: String? = e.message

    override val module: String? = e.locator?.systemId

    override val lineNumber: Int? = e.locator?.lineNumber

    override val columnNumber: Int? = e.locator?.columnNumber
}
