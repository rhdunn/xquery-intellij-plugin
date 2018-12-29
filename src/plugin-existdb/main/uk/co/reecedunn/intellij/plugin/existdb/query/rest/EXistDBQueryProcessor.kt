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
package uk.co.reecedunn.intellij.plugin.existdb.query.rest

import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.cached
import uk.co.reecedunn.intellij.plugin.core.async.getValue
import uk.co.reecedunn.intellij.plugin.core.xml.XmlDocument
import uk.co.reecedunn.intellij.plugin.core.xml.children
import uk.co.reecedunn.intellij.plugin.existdb.resources.EXistDBQueries
import uk.co.reecedunn.intellij.plugin.processor.query.http.HttpConnection
import uk.co.reecedunn.intellij.plugin.processor.query.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.UnsupportedQueryType

internal class EXistDBQueryProcessor(val baseUri: String, val connection: HttpConnection) :
    QueryProcessor {
    override val version: ExecutableOnPooledThread<String> by cached {
        eval(EXistDBQueries.Version, MimeTypes.XQUERY).use { query ->
            query.run().then { results -> results.first().value }
        }
    }

    override fun eval(query: String, mimetype: String): Query {
        return when (mimetype) {
            MimeTypes.XQUERY -> {
                val xml = XmlDocument.parse(EXistDBQueries.PostQueryTemplate)
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
