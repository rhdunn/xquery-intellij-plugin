/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.vfs.VirtualFile
import org.apache.http.client.methods.RequestBuilder
import uk.co.reecedunn.intellij.plugin.core.lang.getLanguageMimeTypes
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.lang.SPARQLQuery
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.lang.SPARQLUpdate
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.lang.SQL
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.lang.ServerSideJavaScript
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.marklogic.query.rest.debugger.MarkLogicDebugQuery
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQuery
import uk.co.reecedunn.intellij.plugin.processor.debug.DebuggableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQueryProvider
import uk.co.reecedunn.intellij.plugin.processor.query.*
import uk.co.reecedunn.intellij.plugin.processor.query.http.HttpConnection
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQuery
import uk.co.reecedunn.intellij.plugin.processor.validation.ValidatableQueryProvider
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri
import uk.co.reecedunn.intellij.plugin.xquery.intellij.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xslt.intellij.lang.XSLT

internal class MarkLogicQueryProcessor(
    private val baseUri: String,
    private val connection: HttpConnection
) : ProfileableQueryProvider,
    RunnableQueryProvider,
    DebuggableQueryProvider,
    ValidatableQueryProvider,
    LogViewProvider {
    // region QueryProcessor

    override val presentation: ItemPresentation = MarkLogicRest.presentation

    override val version: String
        get() = createRunnableQuery(MarkLogicQueries.Version, XQuery).run().results.first().value as String

    override val servers: List<String>
        get() = createRunnableQuery(MarkLogicQueries.Servers, XQuery).run().results.map { it.value as String }

    override val databases: List<String>
        get() = createRunnableQuery(MarkLogicQueries.Databases, XQuery).run().results.map { it.value as String }

    private fun buildParameters(query: VirtualFile, language: Language, mode: String): JsonObject {
        val queryParams = JsonObject()
        queryParams.addProperty("mode", mode)
        queryParams.addProperty("mimetype", language.getLanguageMimeTypes()[0])

        if (query is XpmModuleUri) {
            queryParams.addProperty("module-path", query.path)
            queryParams.addProperty("query", "")
        } else {
            queryParams.addProperty("module-path", "")
            queryParams.addProperty("query", query.decode()!!)
        }
        return queryParams
    }

    // endregion
    // region ProfileableQueryProvider

    override fun createProfileableQuery(query: VirtualFile, language: Language): ProfileableQuery {
        return when (language) {
            XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                val queryId = "(: xijp:queryId=${builder.hashCode()}:${System.nanoTime()} :)"
                val params = buildParameters(query, language, "profile")
                builder.addParameter("xquery", "$queryId${MarkLogicQueries.Run}")
                MarkLogicProfileQuery(builder, params, query, connection, this, queryId)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    // endregion
    // region RunnableQueryProvider

    override fun createRunnableQuery(query: VirtualFile, language: Language): RunnableQuery {
        return when (language) {
            ServerSideJavaScript, SPARQLQuery, SPARQLUpdate, SQL, XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                val queryId = "(: xijp:queryId=${builder.hashCode()}:${System.nanoTime()} :)"
                val params = buildParameters(query, language, "run")
                builder.addParameter("xquery", "$queryId${MarkLogicQueries.Run}")
                MarkLogicRunQuery(builder, params, query, connection, this, queryId)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    // endregion
    // region DebuggableQueryProvider

    override fun createDebuggableQuery(query: VirtualFile, language: Language): DebuggableQuery {
        return when (language) {
            XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", MarkLogicQueries.Run)
                MarkLogicDebugQuery(builder, buildParameters(query, language, "debug"), query, connection, this)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    // endregion
    // region ValidatableQueryProvider

    override fun createValidatableQuery(query: VirtualFile, language: Language): ValidatableQuery {
        return when (language) {
            XQuery, XSLT -> {
                val builder = RequestBuilder.post("$baseUri/v1/eval")
                builder.addParameter("xquery", MarkLogicQueries.Run)
                MarkLogicRunQuery(builder, buildParameters(query, language, "validate"), query, connection, this, null)
            }
            else -> throw UnsupportedQueryType(language)
        }
    }

    // endregion
    // region LogViewProvider

    override fun logs(): List<String> {
        return createRunnableQuery(MarkLogicQueries.Log.Logs, XQuery).run().results.map { it.value as String }
    }

    override fun log(name: String): List<String> {
        return createRunnableQuery(MarkLogicQueries.Log.Log, XQuery).use { query ->
            query.bindVariable("name", name, "xs:string")
            query.run().results.map { it.value as String }
        }
    }

    override fun defaultLogFile(logs: List<String>): String? {
        return if (logs.contains("ErrorLog.txt"))
            "ErrorLog.txt" // The log has not rolled over.
        else
            "ErrorLog_1.txt" // The log has rolled over, and no new log entries have been added.
    }

    // endregion
    // region Closeable

    override fun close() = connection.close()

    // endregion
}
