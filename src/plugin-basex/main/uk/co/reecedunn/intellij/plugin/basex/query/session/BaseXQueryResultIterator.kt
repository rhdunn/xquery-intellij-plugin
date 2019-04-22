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

import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.basex.query.session.binding.Query
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

internal class BaseXQueryResultIterator(
    val query: Query,
    val queryFile: VirtualFile,
    val classes: BaseXClasses
) : Iterator<QueryResult> {
    private var position: Long = -1

    override fun hasNext(): Boolean = classes.check(queryFile) {
        query.more()
    }

    override fun next(): QueryResult {
        val next = query.next()
        val type = query.type()
        return QueryResult.fromItemType(++position, next!!, type?.toString() ?: "item()")
    }
}
