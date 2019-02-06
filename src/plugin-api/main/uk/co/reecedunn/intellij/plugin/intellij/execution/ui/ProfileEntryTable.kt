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
import uk.co.reecedunn.intellij.plugin.processor.profile.ProfileEntry

@Suppress("ClassName")
private object MODULE_PATH_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.module.column.label")
), Comparator<ProfileEntry> {
    override fun valueOf(item: ProfileEntry?): String? = item?.module

    override fun getComparator(): Comparator<ProfileEntry>? = this

    override fun compare(o1: ProfileEntry?, o2: ProfileEntry?): Int {
        return (o1?.module ?: "").compareTo(o2?.module ?: "")
    }
}

@Suppress("ClassName")
private object LINE_NUMBER_COLUMN : ColumnInfo<ProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.line-number.column.label")
), Comparator<ProfileEntry> {
    override fun valueOf(item: ProfileEntry?): Int? = item?.lineNumber

    override fun getComparator(): Comparator<ProfileEntry>? = this

    override fun compare(o1: ProfileEntry?, o2: ProfileEntry?): Int {
        return o1?.lineNumber!!.compareTo(o2?.lineNumber!!)
    }
}

@Suppress("ClassName")
private object COLUMN_NUMBER_COLUMN : ColumnInfo<ProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.column-number.column.label")
), Comparator<ProfileEntry> {
    override fun valueOf(item: ProfileEntry?): Int? = item?.columnNumber

    override fun getComparator(): Comparator<ProfileEntry>? = this

    override fun compare(o1: ProfileEntry?, o2: ProfileEntry?): Int {
        return o1?.columnNumber!!.compareTo(o2?.columnNumber!!)
    }
}

@Suppress("ClassName")
private object HITS_COLUMN : ColumnInfo<ProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.hits.column.label")
), Comparator<ProfileEntry> {
    override fun valueOf(item: ProfileEntry?): Int? = item?.hits

    override fun getComparator(): Comparator<ProfileEntry>? = this

    override fun compare(o1: ProfileEntry?, o2: ProfileEntry?): Int {
        return o1!!.hits.compareTo(o2!!.hits)
    }
}

@Suppress("ClassName")
private object SHALLOW_TIME_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.shallow-time.column.label")
), Comparator<ProfileEntry> {
    override fun valueOf(item: ProfileEntry?): String? = item?.shallowTime?.seconds?.data?.toPlainString()

    override fun getComparator(): Comparator<ProfileEntry>? = this

    override fun compare(o1: ProfileEntry?, o2: ProfileEntry?): Int {
        val compared = o1!!.shallowTime.months.data.compareTo(o2!!.shallowTime.months.data)
        return if (compared == 0)
            o1.shallowTime.seconds.data.compareTo(o2.shallowTime.seconds.data)
        else
            compared
    }
}

@Suppress("ClassName")
private object DEEP_TIME_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.deep-time.column.label")
), Comparator<ProfileEntry> {
    override fun valueOf(item: ProfileEntry?): String? = item?.deepTime?.seconds?.data?.toPlainString()

    override fun getComparator(): Comparator<ProfileEntry>? = this

    override fun compare(o1: ProfileEntry?, o2: ProfileEntry?): Int {
        val compared = o1!!.deepTime.months.data.compareTo(o2!!.deepTime.months.data)
        return if (compared == 0)
            o1.deepTime.seconds.data.compareTo(o2.deepTime.seconds.data)
        else
            compared
    }
}

@Suppress("ClassName")
private object EXPRESSION_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.expression.column.label")
), Comparator<ProfileEntry> {
    override fun valueOf(item: ProfileEntry?): String? = item?.expression

    override fun getComparator(): Comparator<ProfileEntry>? = this

    override fun compare(o1: ProfileEntry?, o2: ProfileEntry?): Int {
        return o1!!.expression.compareTo(o2!!.expression)
    }
}

private val COLUMNS: Array<ColumnInfo<*, *>> = arrayOf(
    MODULE_PATH_COLUMN,
    LINE_NUMBER_COLUMN,
    COLUMN_NUMBER_COLUMN,
    HITS_COLUMN,
    SHALLOW_TIME_COLUMN,
    DEEP_TIME_COLUMN,
    EXPRESSION_COLUMN
)

class ProfileEntryTable : TableView<ProfileEntry>() {
    init {
        setModelAndUpdateColumns(ListTableModel<ProfileEntry>(*COLUMNS))
        setEnableAntialiasing(true)

        updateEmptyText(false, false)
    }

    private fun updateEmptyText(running: Boolean, exception: Boolean) {
        if (exception)
            emptyText.text = PluginApiBundle.message("profile.entry.table.has-exception")
        else if (running)
            emptyText.text = PluginApiBundle.message("profile.entry.table.results-pending")
        else
            emptyText.text = PluginApiBundle.message("profile.entry.table.no-results")
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

    fun addRow(entry: ProfileEntry) = listTableModel.addRow(entry)
}
