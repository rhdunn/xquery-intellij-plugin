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

import com.intellij.openapi.application.ModalityState
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import javax.swing.AbstractListModel
import javax.swing.ComboBoxModel

class ServerComboBoxModel : AbstractListModel<String>(), ComboBoxModel<String> {
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
            servers.isEmpty() -> selected
            servers.contains(selected) -> selected
            else -> null
        }
    }

    // endregion
    // region ListModel

    private var servers = listOf<String>()

    override fun getSize(): Int = servers.size

    override fun getElementAt(index: Int): String = servers[index]

    fun update(settings: QueryProcessorSettings?, database: String) {
        if (settings == null) return
        executeOnPooledThread {
            val items = settings.session.servers(database)
            try {
                invokeLater(ModalityState.any()) {
                    servers = items
                    fireContentsChanged(this, -1, -1)
                }
            } catch (e: Throwable) {
                servers = listOf()
                fireContentsChanged(this, -1, -1)
            }
        }
    }

    // endregion
}
