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

import com.google.gson.JsonObject
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.CloseableHttpClient
import uk.co.reecedunn.intellij.plugin.intellij.resources.Resources
import uk.co.reecedunn.intellij.plugin.intellij.resources.decode
import uk.co.reecedunn.intellij.plugin.processor.query.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.UnsupportedQueryType

internal class EXistDBQueryProcessor(val baseUri: String, val client: CloseableHttpClient) : QueryProcessor {
    override val version: String get() = TODO()

    override val supportedQueryTypes: Array<String> = arrayOf(MimeTypes.XQUERY)

    override fun createQuery(query: String, mimetype: String): Query {
        return when (mimetype) {
            MimeTypes.XQUERY -> {
                TODO()
            }
            else -> throw UnsupportedQueryType(mimetype)
        }
    }

    override fun close() = client.close()
}
