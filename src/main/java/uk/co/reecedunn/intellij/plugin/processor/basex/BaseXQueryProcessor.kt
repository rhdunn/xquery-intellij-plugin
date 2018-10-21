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
package uk.co.reecedunn.intellij.plugin.processor.basex

import org.intellij.lang.annotations.Language
import uk.co.reecedunn.intellij.plugin.processor.MimeTypes
import uk.co.reecedunn.intellij.plugin.processor.Query
import uk.co.reecedunn.intellij.plugin.processor.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.UnsupportedQueryType

@Language("XQuery")
val VERSION_QUERY = """
declare namespace db = "http://basex.org/modules/db";
db:system()/generalinformation/version/string()
"""

internal class BaseXQueryProcessor(val session: Any, val classes: BaseXClasses) : QueryProcessor {
    override val version: String
        get() = createQuery(VERSION_QUERY, MimeTypes.XQUERY).use { query -> query.run().first().value }

    override val supportedQueryTypes: Array<String> = arrayOf(MimeTypes.XQUERY)

    override fun createQuery(query: String, mimetype: String): Query {
        return when (mimetype) {
            MimeTypes.XQUERY -> {
                val ret = classes.sessionClass.getMethod("query", String::class.java).invoke(session, query)
                BaseXQuery(ret, classes)
            }
            else -> throw UnsupportedQueryType(mimetype)
        }
    }

    override fun close() {
        classes.sessionClass.getMethod("close").invoke(session)
    }
}
