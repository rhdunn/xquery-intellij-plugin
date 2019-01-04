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
object MODULE_PATH_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.module.column.label")
) {
    override fun valueOf(item: ProfileEntry?): String? = item?.module
}

@Suppress("ClassName")
object LINE_NUMBER_COLUMN : ColumnInfo<ProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.line-number.column.label")
) {
    override fun valueOf(item: ProfileEntry?): Int? = item?.lineNumber
}

@Suppress("ClassName")
object COLUMN_NUMBER_COLUMN : ColumnInfo<ProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.column-number.column.label")
) {
    override fun valueOf(item: ProfileEntry?): Int? = item?.columnNumber
}

@Suppress("ClassName")
object HITS_COLUMN : ColumnInfo<ProfileEntry, Int>(
    PluginApiBundle.message("profile.entry.table.hits.column.label")
) {
    override fun valueOf(item: ProfileEntry?): Int? = item?.hits
}

@Suppress("ClassName")
object SHALLOW_TIME_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.shallow-time.column.label")
) {
    override fun valueOf(item: ProfileEntry?): String? = item?.shallowTime?.seconds?.data?.toPlainString()
}

@Suppress("ClassName")
object DEEP_TIME_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.deep-time.column.label")
) {
    override fun valueOf(item: ProfileEntry?): String? = item?.deepTime?.seconds?.data?.toPlainString()
}

@Suppress("ClassName")
object EXPRESSION_COLUMN : ColumnInfo<ProfileEntry, String>(
    PluginApiBundle.message("profile.entry.table.expression.column.label")
) {
    override fun valueOf(item: ProfileEntry?): String? = item?.expression
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

        emptyText.text = PluginApiBundle.message("profile.entry.table.no-results")

        tableHeader = createDefaultTableHeader()
        tableHeader.columnModel
    }

    fun addRow(entry: ProfileEntry) = listTableModel.addRow(entry)
}
