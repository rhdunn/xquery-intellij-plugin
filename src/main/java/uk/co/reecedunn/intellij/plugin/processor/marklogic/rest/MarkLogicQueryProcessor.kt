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
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.cached
import uk.co.reecedunn.intellij.plugin.core.async.getValue
import uk.co.reecedunn.intellij.plugin.core.io.decode
import uk.co.reecedunn.intellij.plugin.intellij.resources.Resources
import uk.co.reecedunn.intellij.plugin.processor.query.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.UnsupportedQueryType

val VERSION_QUERY = Resources.load("queries/marklogic/version.xq")!!.decode()
val RUN_QUERY = Resources.load("queries/marklogic/run.xq")!!.decode()

internal class MarkLogicQueryProcessor(val baseUri: String, val client: CloseableHttpClient) :
    QueryProcessor {
    override val version: ExecutableOnPooledThread<String> by cached {
        eval(VERSION_QUERY, MimeTypes.XQUERY).use { query ->
            query.run().then { results -> results.first().value }
        }
    }

    override val supportedQueryTypes: Array<String> = arrayOf(MimeTypes.XQUERY)

    override fun eval(query: String, mimetype: String): Query {
        return when (mimetype) {
            MimeTypes.XQUERY -> {
                val queryParams = JsonObject()
                queryParams.addProperty("query", query)

                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", RUN_QUERY)
                MarkLogicQuery(builder, queryParams, client)
            }
            else -> throw UnsupportedQueryType(mimetype)
        }
    }

    override fun invoke(path: String, mimetype: String): Query {
        throw UnsupportedOperationException()
    }

    override fun close() = client.close()
}
