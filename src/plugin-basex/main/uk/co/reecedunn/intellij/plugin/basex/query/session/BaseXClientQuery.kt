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
import uk.co.reecedunn.intellij.plugin.basex.query.session.binding.ClientQuery
import uk.co.reecedunn.intellij.plugin.basex.query.session.binding.ClientSession
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.processor.database.DatabaseModule
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery

internal class BaseXClientQuery(
    val session: ClientSession,
    val queryString: String,
    val queryFile: VirtualFile,
    val classes: BaseXClasses
) : RunnableQuery {
    private val query: ClientQuery = session.query(queryString)

    override var rdfOutputFormat: Language? = null

    override var updating: Boolean = false

    override var xpathSubset: XPathSubset = XPathSubset.XPath

    override var server: String = ""

    override var database: String = ""

    override var modulePath: String = ""

    override fun bindVariable(name: String, value: Any?, type: String?): Unit = classes.check(queryFile) {
        // BaseX cannot bind to namespaced variables, so only pass the NCName.
        query.bind(name, value, mapType(type))
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = classes.check(queryFile) {
        when (value) {
            is DatabaseModule -> query.context(value.path, mapType(type))
            is VirtualFile -> query.context(value.decode()!!, mapType(type))
            else -> query.context(value.toString(), mapType(type))
        }
    }

    override fun run(): ExecutableOnPooledThread<Sequence<QueryResult>> = pooled_thread {
        classes.check(queryFile) {
            BaseXQueryResultIterator(query.`object`, queryFile, classes, query.clientQueryClass).asSequence()
        }
    }

    override fun close() {
        session.close()
    }
}
