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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui.profile

import com.intellij.ide.util.PropertiesComponent
import com.intellij.ui.JBColor
import com.intellij.ui.table.TableView
import uk.co.reecedunn.intellij.plugin.core.awt.scope
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columnInfo
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columns
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.QueryTable
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.xdebugger.QuerySourcePosition
import uk.co.reecedunn.intellij.plugin.processor.profile.FlatProfileEntry
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import javax.swing.DefaultRowSorter
import javax.swing.JTable
import javax.swing.RowSorter
import javax.swing.SortOrder
import javax.swing.table.DefaultTableCellRenderer

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

private val MODULE_PATH_COLUMN = columnInfo<FlatProfileEntry, String>(
    heading = PluginApiBundle.message("profile.entry.table.module.column.label"),
    getter = { item -> item.frame.sourcePosition?.file?.name ?: "" }
)

private val LINE_NUMBER_COLUMN = columnInfo<FlatProfileEntry, Int>(
    heading = PluginApiBundle.message("profile.entry.table.line-number.column.label"),
    getter = { item -> (item.frame.sourcePosition?.line ?: 0) + 1 }
)

private val COLUMN_NUMBER_COLUMN = columnInfo<FlatProfileEntry, Int>(
    heading = PluginApiBundle.message("profile.entry.table.column-number.column.label"),
    getter = { item -> ((item.frame.sourcePosition as? QuerySourcePosition)?.column ?: 0) + 1 }
)

private val COUNT_COLUMN = columnInfo(
    heading = PluginApiBundle.message("profile.entry.table.count.column.label"),
    getter = FlatProfileEntry::count
)

private val SELF_TIME_COLUMN = columnInfo(
    heading = PluginApiBundle.message("profile.entry.table.self-time.column.label"),
    getter = FlatProfileEntry::selfTime,
    renderer = { TIME_CELL_RENDERER }
)

private val TOTAL_TIME_COLUMN = columnInfo(
    heading = PluginApiBundle.message("profile.entry.table.total-time.column.label"),
    getter = FlatProfileEntry::totalTime,
    renderer = { TIME_CELL_RENDERER }
)

private val CONTEXT_COLUMN = columnInfo(
    heading = PluginApiBundle.message("profile.entry.table.context.column.label"),
    getter = FlatProfileEntry::context,
    renderer = {
        val renderer = DefaultTableCellRenderer()
        renderer.putClientProperty("html.disable", true)
        renderer
    }
)

class FlatProfileTable : TableView<FlatProfileEntry>(), QueryTable {
    companion object {
        private const val PROPERTY_SORT_COLUMN = "XQueryIntelliJPlugin.FlatProfileTable.SortKeys.Column"
        private const val PROPERTY_SORT_ORDER = "XQueryIntelliJPlugin.FlatProfileTable.SortKeys.SortOrder"
    }

    init {
        columns {
            add(MODULE_PATH_COLUMN)
            add(LINE_NUMBER_COLUMN)
            add(COLUMN_NUMBER_COLUMN)
            add(COUNT_COLUMN)
            add(SELF_TIME_COLUMN)
            add(TOTAL_TIME_COLUMN)
            add(CONTEXT_COLUMN)
        }
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
