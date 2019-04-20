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
package uk.co.reecedunn.intellij.plugin.basex.query.session

import com.intellij.lang.Language
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery

internal class BaseXLocalQuery(
    val session: Any,
    val queryString: String,
    val queryFile: VirtualFile,
    val classes: BaseXClasses
) : RunnableQuery {
    private var basexQuery: Any? = null
    val query: Any
        get() {
            if (basexQuery == null) {
                basexQuery = classes.localSessionClass.getMethod("query", String::class.java).invoke(session, queryString)
            }
            return basexQuery!!
        }

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean = false

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    override fun bindVariable(name: String, value: Any?, type: String?): Unit = classes.check(queryFile) {
        // BaseX cannot bind to namespaced variables, so only pass the NCName.
        classes.localQueryClass
            .getMethod("bind", String::class.java, Any::class.java, String::class.java)
            .invoke(query, name, value, mapType(type))
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = classes.check(queryFile) {
        val bind = classes.localQueryClass.getMethod("context", Any::class.java, String::class.java)
        when (value) {
            is DatabaseModule -> bind.invoke(query, value.path, mapType(type))
            is VirtualFile -> bind.invoke(query, value.decode()!!, mapType(type))
            else -> bind.invoke(query, value.toString(), mapType(type))
        }
    }

    override fun run(): ExecutableOnPooledThread<Sequence<QueryResult>> = pooled_thread {
        classes.check(queryFile) {
            BaseXQueryResultIterator(query, queryFile, classes, classes.localQueryClass).asSequence()
        }
    }

    override fun close() {
        if (basexQuery != null) {
            classes.localQueryClass.getMethod("close").invoke(query)
            basexQuery = null
        }
    }
}
