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

import com.google.gson.JsonObject
import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import org.apache.http.client.methods.RequestBuilder
import uk.co.reecedunn.intellij.plugin.core.async.*
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.*
import uk.co.reecedunn.intellij.plugin.processor.query.http.HttpConnection

internal class MarkLogicQueryProcessor(val baseUri: String, val connection: HttpConnection) : QueryProcessor {
    override val version: ExecutableOnPooledThread<String> by cached {
        createRunnableQuery(MarkLogicQueries.Version, XQuery).use { query ->
            query.run().then { results -> results.first().value as String }
        }
    }

    override val servers: ExecutableOnPooledThread<List<String>>
        get() {
            return createRunnableQuery(MarkLogicQueries.Servers, XQuery).use { query ->
                query.run().then { results -> results.map { it.value as String }.toList() }
            }
        }

    override val databases: ExecutableOnPooledThread<List<String>>
        get() {
            return createRunnableQuery(MarkLogicQueries.Databases, XQuery).use { query ->
                query.run().then { results -> results.map { it.value as String }.toList() }
            }
        }

    private fun buildParameters(query: VirtualFile, language: Language, mode: String): JsonObject {
        val queryParams = JsonObject()
        queryParams.addProperty("mode", mode)
        queryParams.addProperty("mimetype", language.mimeTypes[0])

        queryParams.addProperty("module-path", "")
        queryParams.addProperty("query", query.decode()!!)
        return queryParams
    }

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery {
        return when (language) {
            ServerSideJavaScript, SPARQLQuery, SPARQLUpdate, SQL, XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", MarkLogicQueries.Run)
                MarkLogicRunQuery(builder, buildParameters(query, language, "run"), connection)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun createProfileableQuery(query: VirtualFile, language: Language): ProfileableQuery {
        return when (language) {
            XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", MarkLogicQueries.Run)
                MarkLogicProfileQuery(builder, buildParameters(query, language, "profile"), connection)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    override fun close() = connection.close()
}
