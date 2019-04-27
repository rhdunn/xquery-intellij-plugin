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
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

class QueryResultItemTypeColumn(val sortable: Boolean = true) : ColumnInfo<Pair<QueryResult, Range<Int>>, String>(
    PluginApiBundle.message("query.result.table.item-type.column.label")
), Comparator<Pair<QueryResult, Range<Int>>> {
    override fun valueOf(item: Pair<QueryResult, Range<Int>>?): String? = item?.first?.type

    override fun getComparator(): Comparator<Pair<QueryResult, Range<Int>>>? = if (sortable) this else null

    override fun compare(o1: Pair<QueryResult, Range<Int>>?, o2: Pair<QueryResult, Range<Int>>?): Int {
        return (o1?.first?.type ?: "").compareTo(o2?.first?.type ?: "")
    }
}

class QueryResultMimeTypeColumn(val sortable: Boolean = true) : ColumnInfo<Pair<QueryResult, Range<Int>>, String>(
    PluginApiBundle.message("query.result.table.mime-type.column.label")
), Comparator<Pair<QueryResult, Range<Int>>> {
    override fun valueOf(item: Pair<QueryResult, Range<Int>>?): String? = item?.first?.mimetype

    override fun getComparator(): Comparator<Pair<QueryResult, Range<Int>>>? = if (sortable) this else null

    override fun compare(o1: Pair<QueryResult, Range<Int>>?, o2: Pair<QueryResult, Range<Int>>?): Int {
        return (o1?.first?.mimetype ?: "").compareTo(o2?.first?.mimetype ?: "")
    }
}

class QueryResultValueColumn(val sortable: Boolean = true) : ColumnInfo<Pair<QueryResult, Range<Int>>, String>(
    PluginApiBundle.message("query.result.table.value.column.label")
), Comparator<Pair<QueryResult, Range<Int>>> {
    override fun valueOf(item: Pair<QueryResult, Range<Int>>?): String? {
        val value = item?.first?.value
        return when (value) {
            is QueryError -> value.message
            is Throwable -> value.localizedMessage
            else -> value?.toString()
        }
    }

    override fun getComparator(): Comparator<Pair<QueryResult, Range<Int>>>? = if (sortable) this else null

    override fun compare(o1: Pair<QueryResult, Range<Int>>?, o2: Pair<QueryResult, Range<Int>>?): Int {
        return o1!!.first.position.compareTo(o2!!.first.position)
    }
}

class QueryResultTable(vararg columns: ColumnInfo<*, *>) : TableView<Pair<QueryResult, Range<Int>>>(), QueryTable {
    init {
        setModelAndUpdateColumns(ListTableModel<Pair<QueryResult, Range<Int>>>(*columns))
        setEnableAntialiasing(true)

        updateEmptyText(false, false)
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

    fun addRow(entry: QueryResult, range: Range<Int>) = listTableModel.addRow(Pair(entry, range))
}
