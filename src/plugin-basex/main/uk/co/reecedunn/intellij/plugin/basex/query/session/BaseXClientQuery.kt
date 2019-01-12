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
import uk.co.reecedunn.intellij.plugin.core.async.ExecutableOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.query.RunnableQuery

internal class BaseXClientQuery(val session: Any, val queryString: String, val classes: BaseXClasses) : RunnableQuery {
    private var basexQuery: Any? = null
    val query: Any
        get() {
            if (basexQuery == null) {
                basexQuery = classes.clientSessionClass.getMethod("query", String::class.java).invoke(session, queryString)
            }
            return basexQuery!!
        }

    override fun setRdfOutputFormat(language: Language?) {
    }

    override fun bindVariable(name: String, value: Any?, type: String?): Unit = classes.check {
        // BaseX cannot bind to namespaced variables, so only pass the NCName.
        classes.clientQueryClass
            .getMethod("bind", String::class.java, Any::class.java, String::class.java)
            .invoke(query, name, value, mapType(type))
    }

    override fun bindContextItem(value: Any?, type: String?): Unit = classes.check {
        classes.clientQueryClass
            .getMethod("context", Any::class.java, String::class.java)
            .invoke(query, value, mapType(type))
    }

    override fun run(): ExecutableOnPooledThread<Sequence<QueryResult>> = pooled_thread {
        classes.check {
            BaseXQueryResultIterator(query, classes, classes.clientQueryClass).asSequence()
        }
    }

    override fun close() {
        if (basexQuery != null) {
            classes.clientQueryClass.getMethod("close").invoke(query)
            basexQuery = null
        }
    }
}
