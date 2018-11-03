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
package uk.co.reecedunn.intellij.plugin.processor.marklogic.rest

import com.google.gson.JsonObject
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.http.HttpStatusException
import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.processor.http.HttpConnection
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.mimetypeFromXQueryItemType
import uk.co.reecedunn.intellij.plugin.processor.query.primitiveToItemType

internal class MarkLogicQuery(
    val builder: RequestBuilder, val queryParams: JsonObject, val connection: HttpConnection
) :
    Query {
    private var variables: JsonObject = JsonObject()
    private var types: JsonObject = JsonObject()

    override fun bindVariable(name: String, value: Any?, type: String?) {
        variables.addProperty(name, value as String? ?: "")
        types.addProperty(name, type)
    }

    override fun bindContextItem(value: Any?, type: String?) {
        // MarkLogic fixes the context item to the content of the MarkLogic database.
        throw UnsupportedOperationException()
    }

    override fun run(): ExecutableOnPooledThread<Sequence<QueryResult>> = pooled_thread {
        val params = queryParams.deepCopy()
        params.addProperty("vars", variables.toString())
        params.addProperty("types", types.toString())

        builder.addParameter("vars", params.toString())
        val request = builder.build()

        val response = connection.execute(request)
        val body = EntityUtils.toString(response.entity)
        response.close()

        if (response.statusLine.statusCode != 200) {
            throw HttpStatusException(response.statusLine.statusCode, response.statusLine.reasonPhrase)
        }

        val mime = MimeResponse(response.allHeaders, body)
        mime.parts.asSequence().mapIndexed { index, part ->
            if (part.getHeader("Content-Length")?.toInt() == 0)
                null
            else {
                val primitive = part.getHeader("X-Primitive") ?: "string"
                val derived = mime.getHeader("X-Derived-${index + 1}")
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

    override fun close() {
    }
}
