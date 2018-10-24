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

import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.CloseableHttpClient
import uk.co.reecedunn.intellij.plugin.processor.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.Query
import uk.co.reecedunn.intellij.plugin.processor.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.UnsupportedQueryType

fun String.toXQueryString(): String {
    return "\"${this.replace("\"".toRegex(), "\"\"").replace("&".toRegex(), "&amp;")}\""
}

fun String.toRunQuery(): String {
    // Use try/catch to report any errors back from MarkLogic, otherwise "500 Internal Error" is returned.
    return "try { xdmp:eval(${this.toXQueryString()}, (), ()) } catch(\$e) { \$e }"
}

internal class MarkLogicQueryProcessor(val baseUri: String, val client: CloseableHttpClient) : QueryProcessor {
    override val version: String get() = TODO("not implemented")

    override val supportedQueryTypes: Array<String> = arrayOf(MimeTypes.XQUERY)

    override fun createQuery(query: String, mimetype: String): Query {
        return when (mimetype) {
            MimeTypes.XQUERY -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", query.toRunQuery())
                MarkLogicQuery(builder, client)
            }
            else -> throw UnsupportedQueryType(mimetype)
        }
    }

    override fun close() = client.close()
}
