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

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.debug.StackFrame
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trans.XPathException
import uk.co.reecedunn.intellij.plugin.saxon.query.s9api.binding.trans.toXPathException
import java.lang.reflect.InvocationTargetException

private const val ERR_NS = "http://www.w3.org/2005/xqt-errors"

fun <T> check(queryFile: VirtualFile, classLoader: ClassLoader, f: () -> T): T {
    return try {
        f()
    } catch (e: InvocationTargetException) {
        throw e.targetException.run { toXPathException(classLoader)?.toSaxonQueryError(queryFile) ?: this }
    }
}

internal fun XPathException.toSaxonQueryError(queryFile: VirtualFile): QueryError {
    val qname = getErrorCodeQName()
    val ns = qname?.getNamespaceURI()
    val prefix = qname?.getPrefix().nullize()
    val localname = qname?.getLocalName()?.nullize() ?: "FOER0000"
    return QueryError(
        standardCode = if (ns == ERR_NS || prefix == null) localname else "$prefix:$localname",
        vendorCode = null,
        description = message,
        value = listOf(),
        frames = listOf(
            StackFrame(
                locator?.systemId?.let { DatabaseModule(it) } ?: queryFile,
                locator?.lineNumber,
                locator?.columnNumber
            )
        )
    )
}
