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

import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.mimetypeFromXQueryItemType
import uk.co.reecedunn.intellij.plugin.processor.query.primitiveToItemType

fun MimeResponse.queryResults(): Sequence<QueryResult> {
    return parts.asSequence().mapIndexed { index, part ->
        if (part.getHeader("Content-Length")?.toInt() == 0)
            null
        else {
            val primitive = part.getHeader("X-Primitive") ?: "string"
            val derived = getHeader("X-Derived-${index + 1}")
            if (derived == "err:error")
                throw MarkLogicQueryError(part.body)
            else {
                val itemType = primitiveToItemType(derived ?: primitive)
                val contentType = part.getHeader("Content-Type") ?: mimetypeFromXQueryItemType(itemType)
                QueryResult(
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
