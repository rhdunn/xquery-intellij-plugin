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
package uk.co.reecedunn.intellij.plugin.existdb.query.rest

import com.intellij.lang.Language
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.util.EntityUtils
import uk.co.reecedunn.intellij.plugin.core.http.HttpStatusException
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.query.http.HttpConnection
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResults
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDuration

@Suppress("unused")
internal class EXistDBHttpRequest(private val builder: RequestBuilder, private val connection: HttpConnection) :
    RunnableQuery {

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean = false

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    override fun bindVariable(name: String, value: Any?, type: String?) {
        throw UnsupportedOperationException()
    }

    override fun bindContextItem(value: Any?, type: String?) {
        throw UnsupportedOperationException()
    }

    override fun run(): QueryResults {
        val request = builder.build()

        val start = System.nanoTime()
        val response = connection.execute(request)
        val end = System.nanoTime()

        val body = EntityUtils.toString(response.entity)
        response.close()

        if (response.statusLine.statusCode != 200) {
            throw HttpStatusException(response.statusLine.statusCode, response.statusLine.reasonPhrase)
        }

        val contentType = response.allHeaders.firstOrNull { h -> h.name == "ContentType" }?.value
        return QueryResults(
            listOf(QueryResult(0, body, "xs:string", contentType ?: "text/plain")),
            XsDuration.ns(end - start)
        )
    }

    override fun close() {
    }
}
