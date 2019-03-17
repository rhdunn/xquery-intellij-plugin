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
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult

class QueryResultItemTypeColumn(val sortable: Boolean = true) : ColumnInfo<QueryResult, String>(
    PluginApiBundle.message("query.result.table.item-type.column.label")
), Comparator<QueryResult> {
    override fun valueOf(item: QueryResult?): String? = item?.type

    override fun getComparator(): Comparator<QueryResult>? = if (sortable) this else null

    override fun compare(o1: QueryResult?, o2: QueryResult?): Int {
        return (o1?.type ?: "").compareTo(o2?.type ?: "")
    }
}

class QueryResultMimeTypeColumn(val sortable: Boolean = true) : ColumnInfo<QueryResult, String>(
    PluginApiBundle.message("query.result.table.mime-type.column.label")
), Comparator<QueryResult> {
    override fun valueOf(item: QueryResult?): String? = item?.mimetype

    override fun getComparator(): Comparator<QueryResult>? = if (sortable) this else null

    override fun compare(o1: QueryResult?, o2: QueryResult?): Int {
        return (o1?.mimetype ?: "").compareTo(o2?.mimetype ?: "")
    }
}

class QueryResultValueColumn(val sortable: Boolean = true) : ColumnInfo<QueryResult, String>(
    PluginApiBundle.message("query.result.table.value.column.label")
), Comparator<QueryResult> {
    override fun valueOf(item: QueryResult?): String? {
        val value = item?.value
        return when (value) {
            is QueryError -> value.message
            is Throwable -> value.localizedMessage
            else -> value?.toString()
        }
    }

    override fun getComparator(): Comparator<QueryResult>? = if (sortable) this else null

    override fun compare(o1: QueryResult?, o2: QueryResult?): Int {
        return o1!!.position.compareTo(o2!!.position)
    }
}

class QueryResultTable(vararg columns: ColumnInfo<*, *>) : TableView<QueryResult>() {
    init {
        setModelAndUpdateColumns(ListTableModel<QueryResult>(*columns))
        setEnableAntialiasing(true)

        updateEmptyText(false, false)
    }

    private fun updateEmptyText(running: Boolean, exception: Boolean) {
        if (exception)
            emptyText.text = PluginApiBundle.message("query.result.table.has-exception")
        else if (running)
            emptyText.text = PluginApiBundle.message("query.result.table.results-pending")
        else
            emptyText.text = PluginApiBundle.message("query.result.table.no-results")
    }

    var isRunning: Boolean = false
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    var hasException: Boolean = false
        set(value) {
            field = value
            updateEmptyText(isRunning, hasException)
        }

    fun addRow(entry: QueryResult) = listTableModel.addRow(entry)
}
