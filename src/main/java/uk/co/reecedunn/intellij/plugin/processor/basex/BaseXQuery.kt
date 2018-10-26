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

import uk.co.reecedunn.intellij.plugin.core.reflection.getMethodOrNull
import uk.co.reecedunn.intellij.plugin.processor.Query
import uk.co.reecedunn.intellij.plugin.processor.QueryResult
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue

private class BaseXQueryResultIterator(val query: Any, val classes: BaseXClasses) : Iterator<QueryResult> {
    override fun hasNext(): Boolean = classes.check {
        classes.queryClass.getMethod("more").invoke(query) as Boolean
    }

    override fun next(): QueryResult {
        val next = classes.queryClass.getMethod("next").invoke(query) as String?
        val type = classes.queryClass.getMethodOrNull("type")?.invoke(query)
        return QueryResult(next!!, type?.toString() ?: "item()")
    }
}

internal class BaseXQuery(val query: Any, val classes: BaseXClasses) : Query {
    override fun bindVariable(name: XsQNameValue, value: Any?, type: String?) {
        // BaseX cannot bind to namespaced variables, so only pass the NCName.
        classes.queryClass
            .getMethod("bind", String::class.java, Any::class.java, String::class.java)
            .invoke(query, name.localName!!.data, value, type)
    }

    override fun bindContextItem(value: Any?, type: String?) {
        classes.queryClass
            .getMethod("context", Any::class.java, String::class.java)
            .invoke(query, value, type)
    }

    override fun run(): Sequence<QueryResult> {
        return BaseXQueryResultIterator(query, classes).asSequence()
    }

    override fun close() {
        classes.queryClass.getMethod("close").invoke(query)
    }
}
