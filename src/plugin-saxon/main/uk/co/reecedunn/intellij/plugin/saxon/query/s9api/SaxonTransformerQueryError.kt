/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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

import uk.co.reecedunn.intellij.plugin.core.reflection.getAnyMethod
import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryErrorImpl
import javax.xml.transform.TransformerException

private const val ERR_NS = "http://www.w3.org/2005/xqt-errors"

internal fun TransformerException.toSaxonError(classes: SaxonClasses): QueryError {
    val qname = classes.xpathExceptionClass.getMethod("getErrorCodeQName").invoke(this)
    val ns = classes.structuredQNameClass.getAnyMethod("getURI", "getNamespaceURI").invoke(qname)
    val prefix = classes.structuredQNameClass.getMethod("getPrefix").invoke(qname)
    val localname = classes.structuredQNameClass.getAnyMethod("getLocalPart", "getLocalName").invoke(qname)
    return QueryErrorImpl(
        standardCode = if (ns == ERR_NS || prefix == null) localname as String else "$prefix:$localname",
        vendorCode = null,
        description = message,
        value = listOf(),
        frames = listOf(StackFrame(locator?.systemId, locator?.lineNumber, locator?.columnNumber))
    )
}
