/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui

import com.intellij.ui.table.TableView
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columnInfo
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columns
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

private val RESULT_INDEX_COLUMN = columnInfo<Pair<QueryResult, Range<Int>>, Long>(
    heading = PluginApiBundle.message("query.result.table.index.column.label"),
    getter = { (first) -> first.position },
    sortable = false
)

private val RESULT_ITEM_TYPE_COLUMN = columnInfo<Pair<QueryResult, Range<Int>>, String>(
    heading = PluginApiBundle.message("query.result.table.item-type.column.label"),
    getter = { (first) -> first.type },
    sortable = false
)

private val RESULT_MIME_TYPE_COLUMN = columnInfo<Pair<QueryResult, Range<Int>>, String>(
    heading = PluginApiBundle.message("query.result.table.mime-type.column.label"),
    getter = { (first) -> first.mimetype },
    sortable = false
)

class QueryResultTable : TableView<Pair<QueryResult, Range<Int>>>(), QueryTable {
    init {
        columns {
            add(RESULT_INDEX_COLUMN)
            add(RESULT_ITEM_TYPE_COLUMN)
            add(RESULT_MIME_TYPE_COLUMN)
        }
        setEnableAntialiasing(true)

        updateEmptyText(running = false, exception = false)
    }

    private fun updateEmptyText(running: Boolean, exception: Boolean) {
        when {
            exception -> emptyText.text = PluginApiBundle.message("query.result.table.has-exception")
            running -> emptyText.text = runningText
            else -> emptyText.text = PluginApiBundle.message("query.result.table.no-results")
        }
    }

    override var runningText: String = PluginApiBundle.message("query.result.table.results-pending")
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    override var isRunning: Boolean = false
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    override var hasException: Boolean = false
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    override val itemCount: Int get() = rowCount

    fun addRow(entry: QueryResult, range: Range<Int>) = listTableModel.addRow(Pair(entry, range))
}
