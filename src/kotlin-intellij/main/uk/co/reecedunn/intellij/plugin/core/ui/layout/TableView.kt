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
package uk.co.reecedunn.intellij.plugin.core.ui.layout

import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import com.intellij.ui.table.TableView as TableViewBase

class TableView<Item> : TableViewBase<Item>() {
    fun add(item: Item) = listTableModel.addRow(item)

    fun updateAll() {
        (0 until rowCount).forEach { row -> update(listTableModel.getItem(row)) }
    }

    fun update(item: Item) {
        val row = listTableModel.indexOf(item)
        (0 until columnCount).forEach { listTableModel.fireTableCellUpdated(row, it) }
    }
}

fun <Item> JBScrollPane.tableView(init: TableView<Item>.() -> Unit): TableView<Item> {
    val view = TableView<Item>()
    view.setEnableAntialiasing(true)
    view.init()
    setViewportView(view)
    return view
}

fun <Item> TableViewBase<Item>.columns(init: ArrayList<ColumnInfo<Item, *>>.() -> Unit) {
    val columns = ArrayList<ColumnInfo<Item, *>>()
    columns.init()
    setModelAndUpdateColumns(ListTableModel(*columns.toTypedArray()))
}
