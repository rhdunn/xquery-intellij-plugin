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

import com.intellij.ui.table.TableView
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import uk.co.reecedunn.intellij.plugin.xdm.documentation.XdmDocumentationSource

private val COLUMNS: Array<ColumnInfo<*, *>> = arrayOf(
)

class XdmDocumentationSourcesTable : TableView<XdmDocumentationSource>() {
    init {
        setModelAndUpdateColumns(ListTableModel<XdmDocumentationSource>(*COLUMNS))
        setEnableAntialiasing(true)
    }

    fun add(source: XdmDocumentationSource) {
        listTableModel.addRow(source)
    }
}
