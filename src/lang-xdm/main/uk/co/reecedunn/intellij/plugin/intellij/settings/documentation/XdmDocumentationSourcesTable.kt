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
package uk.co.reecedunn.intellij.plugin.intellij.settings.documentation

import com.intellij.util.ui.ListTableModel
import uk.co.reecedunn.intellij.plugin.core.ui.layout.ColumnInfo
import uk.co.reecedunn.intellij.plugin.core.ui.layout.TableView
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columnInfo
import uk.co.reecedunn.intellij.plugin.intellij.resources.XdmBundle
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationDownloader
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSource

private val NAME_COLUMN = columnInfo<XdmDocumentationSource, String>(
    heading = XdmBundle.message("documentation-source-table.column.name.title"),
    getter = { item -> item.name }
)

private val VERSION_COLUMN = columnInfo<XdmDocumentationSource, String>(
    heading = XdmBundle.message("documentation-source-table.column.version.title"),
    getter = { item -> item.version }
)

private val STATUS_COLUMN = columnInfo<XdmDocumentationSource, String>(
    heading = XdmBundle.message("documentation-source-table.column.status.title"),
    getter = { item -> XdmDocumentationDownloader.getInstance().status(item).label }
)

private val COLUMNS: Array<ColumnInfo<*, *>> = arrayOf(
    NAME_COLUMN,
    VERSION_COLUMN,
    STATUS_COLUMN
)

class XdmDocumentationSourcesTable : TableView<XdmDocumentationSource>() {
    init {
        setModelAndUpdateColumns(ListTableModel<XdmDocumentationSource>(*COLUMNS))
        setEnableAntialiasing(true)
    }
}
