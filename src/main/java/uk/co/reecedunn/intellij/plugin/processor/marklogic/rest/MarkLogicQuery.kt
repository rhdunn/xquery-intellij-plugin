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
import uk.co.reecedunn.intellij.plugin.core.http.HttpStatusException
import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.processor.Query
import uk.co.reecedunn.intellij.plugin.processor.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.primitiveToItemType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue

private fun op_qname_clark_notation(qname: XsQNameValue): String {
    return if (qname.namespace != null)
        "{${qname.namespace!!.data}}${qname.localName!!.data}"
    else
        qname.localName!!.data
}

internal class MarkLogicQuery(val builder: RequestBuilder, val queryParams: JsonObject, val client: CloseableHttpClient) : Query {
    private var variables: JsonObject = JsonObject()

    override fun bindVariable(name: XsQNameValue, value: Any?, type: String?) {
        variables.addProperty(op_qname_clark_notation(name), value as String? ?: "")
    }

    override fun bindContextItem(value: Any?, type: String?) {
        //TODO("not implemented")
    }

    override fun run(): Sequence<QueryResult> {
        val params = queryParams.deepCopy()
        params.addProperty("vars", variables.toString())

        builder.addParameter("vars", params.toString())
        val request = builder.build()

        val response = client.execute(request)
        val body = EntityUtils.toString(response.entity)
        response.close()

        if (response.statusLine.statusCode != 200) {
            throw HttpStatusException(response.statusLine.statusCode, response.statusLine.reasonPhrase)
        }

        val mime = MimeResponse(response.allHeaders, body)
        return mime.parts.asSequence().map { part ->
            if (part.getHeader("Content-Length")?.toInt() == 0)
                null
            else {
                val primitive = part.getHeader("X-Primitive") ?: "string"
                QueryResult(part.body, primitiveToItemType(primitive))
            }
        }.filterNotNull()
    }

    override fun close() {
    }
}
