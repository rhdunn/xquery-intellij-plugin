/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.run.execution.ui

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.ui.table.TableView
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columnInfo
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columns
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import javax.swing.JScrollPane

data class QueryResultReference(private val textOffset: Int, internal var element: PsiElement? = null) {
    val offset: Int
        get() = element?.textOffset ?: textOffset
}

private val RESULT_INDEX_COLUMN = columnInfo<Pair<QueryResult, QueryResultReference>, Long>(
    heading = PluginApiBundle.message("query.result.table.index.column.label"),
    getter = { (first) -> first.position },
    sortable = false
)

private val RESULT_ITEM_TYPE_COLUMN = columnInfo<Pair<QueryResult, QueryResultReference>, String>(
    heading = PluginApiBundle.message("query.result.table.item-type.column.label"),
    getter = { (first) -> first.type },
    sortable = false
)

private val RESULT_MIME_TYPE_COLUMN = columnInfo<Pair<QueryResult, QueryResultReference>, String>(
    heading = PluginApiBundle.message("query.result.table.mime-type.column.label"),
    getter = { (first) -> first.mimetype },
    sortable = false
)

class QueryResultTable : TableView<Pair<QueryResult, QueryResultReference>>(), QueryTable {
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

    override val itemCount: Int
        get() = rowCount

    fun addRow(entry: QueryResult, offset: Int) {
        listTableModel.addRow(Pair(entry, QueryResultReference(offset)))
    }

    fun updateQueryReferences(psiFile: PsiFile) {
        (0 until itemCount).forEach {
            val item = listTableModel.getItem(it)
            item.second.element = psiFile.findElementAt(item.second.offset)
        }
    }
}

fun JScrollPane.queryResultTable(init: QueryResultTable.() -> Unit): QueryResultTable {
    val view = QueryResultTable()
    view.init()
    setViewportView(view)
    return view
}
