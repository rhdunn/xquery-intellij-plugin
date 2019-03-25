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
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.util.EntityUtils
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.http.HttpStatusException
import uk.co.reecedunn.intellij.plugin.core.http.mime.MimeResponse
import uk.co.reecedunn.intellij.plugin.core.lang.getLanguageMimeTypes
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileQueryResult
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileableQuery
import uk.co.reecedunn.intellij.plugin.processor.query.http.HttpConnection

internal class MarkLogicProfileQuery(
    val builder: RequestBuilder,
    val queryParams: JsonObject,
    val queryPath: String,
    val connection: HttpConnection
) :
    ProfileableQuery,
    BuildableQuery {

    private var variables: JsonObject = JsonObject()
    private var types: JsonObject = JsonObject()

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean = false

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    private var contextValue: String = ""
    private var contextPath: String = ""

    override fun bindVariable(name: String, value: Any?, type: String?) {
        variables.addProperty(name, value as String? ?: "")
        types.addProperty(name, type)
    }

    override fun bindContextItem(value: Any?, type: String?) {
        // NOTE: Only supported for XSLT files.
        when (value) {
            is DatabaseModule -> contextPath = value.path
            is VirtualFile -> contextValue = value.decode()!!
            else -> contextValue = value.toString()
        }
    }

    override fun request(): HttpUriRequest {
        val params = queryParams.deepCopy()
        params.addProperty("vars", variables.toString())
        params.addProperty("types", types.toString())
        params.addProperty("rdf-output-format", rdfOutputFormat?.getLanguageMimeTypes()?.get(0) ?: "")
        params.addProperty("updating", updating.toString())
        params.addProperty("server", server)
        params.addProperty("database", database)
        params.addProperty("module-root", modulePath)
        params.addProperty("context-value", contextValue)
        params.addProperty("context-path", contextPath)

        builder.addParameter("vars", params.toString())
        builder.charset = Charsets.UTF_8
        return builder.build()
    }

    override fun profile(): ExecutableOnPooledThread<ProfileQueryResult> = pooled_thread {
        val response = connection.execute(request())
        val body = EntityUtils.toString(response.entity)
        response.close()

        if (response.statusLine.statusCode != 200) {
            throw HttpStatusException(response.statusLine.statusCode, response.statusLine.reasonPhrase)
        }

        val results = MimeResponse(response.allHeaders, body, Charsets.UTF_8).queryResults(queryPath).iterator()
        MarkLogicProfileQueryResults(results)
    }

    override fun close() {
    }
}
