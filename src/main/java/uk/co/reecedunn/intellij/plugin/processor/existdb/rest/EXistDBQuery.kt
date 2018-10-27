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
package uk.co.reecedunn.intellij.plugin.processor.existdb.rest

import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import uk.co.reecedunn.intellij.plugin.core.http.HttpStatusException
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

private val EXIST_NS = "http://exist.sourceforge.net/NS/exist"

internal class EXistDBQuery(val builder: RequestBuilder, val client: CloseableHttpClient) : Query {
    override fun bindVariable(name: String, value: Any?, type: String?) {
        throw UnsupportedOperationException()
    }

    override fun bindContextItem(value: Any?, type: String?) {
        throw UnsupportedOperationException()
    }

    override fun run(): Sequence<QueryResult> {
        val request = builder.build()

        val response = client.execute(request)
        val body = EntityUtils.toString(response.entity)
        response.close()

        if (response.statusLine.statusCode != 200) when (response.statusLine.statusCode) {
            400 -> throw EXistDBQueryError(body)
            else -> throw HttpStatusException(response.statusLine.statusCode, response.statusLine.reasonPhrase)
        }

        val result = XmlDocument.parse(body)
        return result.root.children(EXIST_NS, "value").map { value ->
            val type = value.getAttributeNS(EXIST_NS, "type")
            QueryResult.fromItemType(value.firstChild?.nodeValue ?: "", type)
        }
    }

    override fun close() {
    }
}
