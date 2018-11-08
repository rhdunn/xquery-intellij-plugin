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
import org.apache.http.entity.StringEntity
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.cached
import uk.co.reecedunn.intellij.plugin.core.async.getValue
import uk.co.reecedunn.intellij.plugin.core.io.decode
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.intellij.resources.Resources
import uk.co.reecedunn.intellij.plugin.processor.http.HttpConnection
import uk.co.reecedunn.intellij.plugin.processor.query.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.UnsupportedQueryType

private val POST_QUERY = Resources.load("queries/existdb/post-query.xml")!!.decode()

val VERSION_QUERY = Resources.load("queries/existdb/version.xq")!!.decode()

internal class EXistDBQueryProcessor(val baseUri: String, val connection: HttpConnection) : QueryProcessor {
    override val version: ExecutableOnPooledThread<String> by cached {
        eval(VERSION_QUERY, MimeTypes.XQUERY).use { query ->
            query.run().then { results -> results.first().value }
        }
    }

    override val supportedQueryTypes: Array<String> = arrayOf(MimeTypes.XQUERY)

    override fun eval(query: String, mimetype: String): Query {
        return when (mimetype) {
            MimeTypes.XQUERY -> {
                val xml = XmlDocument.parse(POST_QUERY)
                xml.root.children("text").first().appendChild(xml.doc.createCDATASection(query))
                val builder = RequestBuilder.post("$baseUri/db")
                builder.entity = StringEntity(xml.toXmlString())
                EXistDBQuery(builder, connection)
            }
            else -> throw UnsupportedQueryType(mimetype)
        }
    }

    override fun invoke(path: String, mimetype: String): Query {
        return when (mimetype) {
            MimeTypes.XQUERY -> {
                val builder = RequestBuilder.get("$baseUri$path")
                EXistDBHttpRequest(builder, connection)
            }
            else -> throw UnsupportedQueryType(mimetype)
        }
    }

    override fun close() = connection.close()
}
