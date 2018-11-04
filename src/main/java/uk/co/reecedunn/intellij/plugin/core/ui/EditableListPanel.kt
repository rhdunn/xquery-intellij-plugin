/*
 * Copyright (C) 2018 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.ui

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import javax.swing.ListModel

abstract class EditableListPanel<T>(val model: ListModel<T>) {
    private val list = JBList<T>(model)
    private val decorator = ToolbarDecorator.createDecorator(list)

    init {
        decorator.setAddAction { add() }
        decorator.setEditAction { edit(list.selectedIndex) }
        decorator.setRemoveAction { remove(list.selectedIndex) }
        decorator.disableUpDownActions()
    }

    abstract fun add()

    abstract fun edit(index: Int)

    abstract fun remove(index: Int)

    var cellRenderer: ListCellRenderer<in T>
        get() = list.cellRenderer
        set(value) {
            list.cellRenderer = value
        }

    var emptyText: String
        get() = list.emptyText.text
        set(value) {
            list.setEmptyText(value)
        }

    fun createPanel(): JPanel = decorator.createPanel()
}
