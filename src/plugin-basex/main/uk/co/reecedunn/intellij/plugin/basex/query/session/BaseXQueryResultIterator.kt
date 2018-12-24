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
package uk.co.reecedunn.intellij.plugin.basex.query.session

import uk.co.reecedunn.intellij.plugin.core.reflection.getMethodOrNull
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

internal class BaseXQueryResultIterator(val query: Any, val classes: BaseXClasses, val queryClass: Class<*>) : Iterator<QueryResult> {
    override fun hasNext(): Boolean = classes.check {
        queryClass.getMethod("more").invoke(query) as Boolean
    }

    override fun next(): QueryResult {
        val next = queryClass.getMethod("next").invoke(query) as String?
        val type = queryClass.getMethodOrNull("type")?.invoke(query)
        return QueryResult.fromItemType(next!!, type?.toString() ?: "item()")
    }
}
