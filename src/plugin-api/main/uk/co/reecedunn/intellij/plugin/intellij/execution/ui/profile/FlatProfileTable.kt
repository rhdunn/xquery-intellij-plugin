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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui.profile

import com.intellij.ide.util.PropertiesComponent
import com.intellij.ui.JBColor
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import uk.co.reecedunn.intellij.plugin.core.awt.scope
import uk.co.reecedunn.intellij.plugin.core.ui.layout.ColumnInfo
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.QueryTable
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry
import uk.co.reecedunn.intellij.plugin.xdm.model.XsDurationValue
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.DefaultRowSorter
import javax.swing.JTable
import javax.swing.RowSorter
import javax.swing.SortOrder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer

@Suppress("ClassName")
private object TIME_CELL_RENDERER : DefaultTableCellRenderer() {
    const val PERCENTAGE_BAR_PADDING = 2
    val PERCENTAGE_BAR_COLOR = JBColor(Color(225, 225, 255), Color(51, 51, 255))

    var percentage: Double = 0.0
    var percentageBarColor: Color = PERCENTAGE_BAR_COLOR

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        if (value !is XsDurationValue || table !is FlatProfileTable) return this

        val seconds = value.seconds.data
        super.getTableCellRendererComponent(table, seconds.toPlainString(), isSelected, hasFocus, row, column)
        isOpaque = false // Don't render the component's background.
        percentage = (seconds.toDouble() / (table.elapsed?.seconds?.data?.toDouble() ?: 1.0))
        percentageBarColor = if (isSelected) background else PERCENTAGE_BAR_COLOR
        return this
    }

    private fun paintBackground(g: Graphics) {
        g.color = background
        g.fillRect(0, 0, width, height)

        val width = (width.toDouble() * percentage).toInt() - (2 * PERCENTAGE_BAR_PADDING)
        if (width > 0) {
            g.color = percentageBarColor
            g.fillRect(PERCENTAGE_BAR_PADDING, PERCENTAGE_BAR_PADDING, width, height - (2 * PERCENTAGE_BAR_PADDING))
        }
    }

    override fun paintComponent(g: Graphics?) {
        g?.scope { paintBackground(it) }
        super.paintComponent(g)
    }
}

@Suppress("ClassName")
private object MODULE_PATH_COLUMN : ColumnInfo<FlatProfileEntry, String?>(
    PluginApiBundle.message("profile.entry.table.module.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry): String? = item.frame.module?.name

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return (o1?.frame?.module?.path ?: "").compareTo(o2?.frame?.module?.path ?: "")
    }
}

@Suppress("ClassName")
private object LINE_NUMBER_COLUMN : ColumnInfo<FlatProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.line-number.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry): Int = item.frame.lineNumber

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1?.frame?.lineNumber!!.compareTo(o2?.frame?.lineNumber!!)
    }
}

@Suppress("ClassName")
private object COLUMN_NUMBER_COLUMN : ColumnInfo<FlatProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.column-number.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry): Int = item.frame.columnNumber

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1?.frame?.columnNumber!!.compareTo(o2?.frame?.columnNumber!!)
    }
}

@Suppress("ClassName")
private object COUNT_COLUMN : ColumnInfo<FlatProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.count.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry): Int = item.count

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1!!.count.compareTo(o2!!.count)
    }
}

@Suppress("ClassName")
private object SELF_TIME_COLUMN : ColumnInfo<FlatProfileEntry, XsDurationValue>(
    PluginApiBundle.message("profile.entry.table.self-time.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry): XsDurationValue = item.selfTime

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1!!.selfTime.compareTo(o2!!.selfTime)
    }

    override fun getRenderer(item: FlatProfileEntry?): TableCellRenderer? = TIME_CELL_RENDERER
}

@Suppress("ClassName")
private object TOTAL_TIME_COLUMN : ColumnInfo<FlatProfileEntry, XsDurationValue>(
    PluginApiBundle.message("profile.entry.table.total-time.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry): XsDurationValue = item.totalTime

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1!!.totalTime.compareTo(o2!!.totalTime)
    }

    override fun getRenderer(item: FlatProfileEntry?): TableCellRenderer? = TIME_CELL_RENDERER
}

@Suppress("ClassName")
private object CONTEXT_COLUMN : ColumnInfo<FlatProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.context.column.label")
), Comparator<FlatProfileEntry> {
    override fun valueOf(item: FlatProfileEntry): String = item.context

    override fun getComparator(): Comparator<FlatProfileEntry>? = this

    override fun compare(o1: FlatProfileEntry?, o2: FlatProfileEntry?): Int {
        return o1!!.context.compareTo(o2!!.context)
    }

    override fun getRenderer(item: FlatProfileEntry?): TableCellRenderer? {
        val renderer = DefaultTableCellRenderer()
        renderer.putClientProperty("html.disable", true)
        return renderer
    }
}

private val COLUMNS: Array<ColumnInfo<*, *>> = arrayOf(
    MODULE_PATH_COLUMN,
    LINE_NUMBER_COLUMN,
    COLUMN_NUMBER_COLUMN,
    COUNT_COLUMN,
    SELF_TIME_COLUMN,
    TOTAL_TIME_COLUMN,
    CONTEXT_COLUMN
)

class FlatProfileTable : TableView<FlatProfileEntry>(), QueryTable {
    companion object {
        private const val PROPERTY_SORT_COLUMN = "XQueryIntelliJPlugin.FlatProfileTable.SortKeys.Column"
        private const val PROPERTY_SORT_ORDER = "XQueryIntelliJPlugin.FlatProfileTable.SortKeys.SortOrder"
    }

    init {
        setModelAndUpdateColumns(ListTableModel<FlatProfileEntry>(*COLUMNS))
        setEnableAntialiasing(true)

        updateEmptyText(running = false, exception = false)

        val sorter = rowSorter as DefaultRowSorter<*, *>
        sorter.sortKeys = listOf(defaultSortKey)
        sorter.sort()

        sorter.addRowSorterListener { defaultSortKey = rowSorter.sortKeys[0] }
    }

    private var defaultSortKey: RowSorter.SortKey
        get() {
            val properties = PropertiesComponent.getInstance()
            val column = properties.getInt(PROPERTY_SORT_COLUMN, 5)
            val order = properties.getValue(PROPERTY_SORT_ORDER, SortOrder.DESCENDING.name)
            return RowSorter.SortKey(column, SortOrder.valueOf(order))
        }
        set(value) {
            val properties = PropertiesComponent.getInstance()
            properties.setValue(PROPERTY_SORT_COLUMN, value.column, 5)
            properties.setValue(PROPERTY_SORT_ORDER, value.sortOrder.name, SortOrder.DESCENDING.name)
        }

    private fun updateEmptyText(running: Boolean, exception: Boolean) {
        when {
            exception -> emptyText.text = PluginApiBundle.message("profile.entry.table.has-exception")
            running -> emptyText.text = runningText
            else -> emptyText.text = PluginApiBundle.message("profile.entry.table.no-results")
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

    override val itemCount: Int = 0

    var elapsed: XsDurationValue? = null

    fun addRow(entry: FlatProfileEntry) = listTableModel.addRow(entry)
}
