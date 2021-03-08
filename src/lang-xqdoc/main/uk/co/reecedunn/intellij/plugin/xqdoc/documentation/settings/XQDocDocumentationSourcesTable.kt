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
package uk.co.reecedunn.intellij.plugin.xqdoc.documentation.settings

import com.intellij.util.ui.ColumnInfo
import uk.co.reecedunn.intellij.plugin.core.ui.layout.columnInfo
import uk.co.reecedunn.intellij.plugin.xqdoc.resources.XQDocBundle
import uk.co.reecedunn.intellij.plugin.xqdoc.documentation.XQDocDocumentationDownloader
import uk.co.reecedunn.intellij.plugin.xpm.lang.documentation.XpmDocumentationSource
import javax.swing.table.DefaultTableCellRenderer

fun ArrayList<ColumnInfo<XpmDocumentationSource, *>>.nameColumn() {
    val column = columnInfo<XpmDocumentationSource, String>(
        heading = XQDocBundle.message("documentation-source-table.column.name.title"),
        getter = { item -> item.presentation.presentableText!! },
        renderer = { value ->
            val renderer = DefaultTableCellRenderer()
            renderer.icon = value.presentation.getIcon(false)
            renderer
        }
    )
    add(column)
}

fun ArrayList<ColumnInfo<XpmDocumentationSource, *>>.versionColumn() {
    val column = columnInfo<XpmDocumentationSource, String>(
        heading = XQDocBundle.message("documentation-source-table.column.version.title"),
        getter = { item -> item.version }
    )
    add(column)
}

fun ArrayList<ColumnInfo<XpmDocumentationSource, *>>.statusColumn() {
    val column = columnInfo<XpmDocumentationSource, String>(
        heading = XQDocBundle.message("documentation-source-table.column.status.title"),
        getter = { item -> XQDocDocumentationDownloader.getInstance().status(item).label }
    )
    add(column)
}
