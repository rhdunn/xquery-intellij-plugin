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
import uk.co.reecedunn.intellij.plugin.basex.query.session.binding.Query
import uk.co.reecedunn.intellij.plugin.basex.query.session.binding.Session
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResults
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDuration
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue

internal class BaseXRunnableQuery(
    val session: Session,
    val queryString: String,
    val queryFile: VirtualFile,
    val classLoader: ClassLoader
) : RunnableQuery {
    override var rdfOutputFormat: Language? = null

    override var updating: Boolean = false

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    private val variables: HashMap<String, Pair<Any?, String?>> = HashMap()
    private var contextItem: Pair<Any?, String?>? = null

    override fun bindVariable(name: String, value: Any?, type: String?) {
        variables[name] = Pair(value, mapType(type))
    }

    override fun bindContextItem(value: Any?, type: String?) {
        contextItem = when (value) {
            is DatabaseModule -> Pair(value.path, mapType(type))
            is VirtualFile -> Pair(value.decode()!!, mapType(type))
            else -> Pair(value.toString(), mapType(type))
        }
    }

    override fun run(): ExecutableOnPooledThread<QueryResults> = pooled_thread {
        check(classLoader, queryFile) {
            session.execute("set queryinfo off")
            session.execute("set xmlplan off")
            val query: Query = session.query(queryString)

            contextItem?.let { query.context(it.first, it.second) }
            variables.forEach { query.bind(it.key, it.value.first, it.value.second) }

            val results = BaseXQueryResultIterator(query, queryFile, classLoader).asSequence().toList()
            val info = query.info()!!.toBaseXInfo()
            QueryResults(results, info["Total Time"] as XsDurationValue)
        }
    }

    override fun close() {
    }
}
