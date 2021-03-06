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
package uk.co.reecedunn.intellij.plugin.marklogic.query.rest

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.mimetypeFromXQueryItemType
import uk.co.reecedunn.intellij.plugin.processor.query.primitiveToItemType

fun MimeResponse.queryResults(queryFile: VirtualFile): Sequence<QueryResult> {
    var position: Long = -1
    val responseContentType: String? = getHeader("Content-type")?.substringBefore("; ")
    return parts.asSequence().mapIndexed { index, part ->
        if (part.getHeader("Content-Length")?.toInt() == 0)
            null
        else {
            val primitive = part.getHeader("X-Primitive") ?: "string"
            val derived = getHeader("X-Derived-${index + 1}")
            if (derived == "err:error")
                throw part.body.toMarkLogicQueryError(queryFile)
            else {
                val itemType = primitiveToItemType(derived ?: primitive)
                val contentType = when (itemType) {
                    "sem:triple" -> "application/xquery"
                    else -> sequenceOf(
                        getHeader("X-Content-Type-${index + 1}"),
                        responseContentType,
                        part.getHeader("Content-Type"),
                        mimetypeFromXQueryItemType(itemType)
                    ).filterNotNull().first()
                }
                QueryResult(
                    ++position,
                    part.body,
                    itemType,
                    if (contentType == "application/x-unknown-content-type")
                        "application/octet-stream"
                    else
                        contentType
                )
            }
        }
    }.filterNotNull()
}
