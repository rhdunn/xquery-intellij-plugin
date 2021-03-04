/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.intellij.execution.ui.model

import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel

open class QueryServerComboBoxModel : AbstractListModel<String>(), ComboBoxModel<String> {
    // region ComboBoxModel

    var defaultSelection: String? = null
        set(value) {
            field = value
            fireContentsChanged(this, -1, -1)
        }

    private var selected: Any? = null

    override fun setSelectedItem(anItem: Any?) {
        if (anItem?.let { selected == it } ?: selected == null) return
        selected = anItem
        fireContentsChanged(this, -1, -1)
    }

    override fun getSelectedItem(): Any? {
        val selected = selected ?: defaultSelection
        return when {
            items.isEmpty() -> selected
            items.contains(selected) -> selected
            else -> items.first()
        }
    }

    // endregion
    // region ListModel

    private var items = listOf<String>()

    override fun getSize(): Int = items.size

    override fun getElementAt(index: Int): String = items[index]

    fun update(items: List<String>) {
        this.items = items
        fireContentsChanged(this, -1, -1)
    }

    // endregion
}
