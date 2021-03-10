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
package uk.co.reecedunn.intellij.plugin.processor.query.settings

import com.intellij.openapi.application.ModalityState
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.processor.query.CachedQueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import javax.swing.DefaultComboBoxModel

class QueryProcessorSettingsModel : DefaultComboBoxModel<CachedQueryProcessorSettings>(), QueryProcessorsListener {
    // region QueryProcessorsListener

    override fun onAddProcessor(processor: QueryProcessorSettings) {
        addElement(CachedQueryProcessorSettings(processor))
    }

    override fun onEditProcessor(index: Int, processor: QueryProcessorSettings) {
        getElementAt(index)?.let {
            it.settings = processor
            it.presentation = null
        }
    }

    override fun onRemoveProcessor(index: Int) {
        removeElementAt(index)
    }

    // endregion

    override fun addElement(item: CachedQueryProcessorSettings?) {
        super.addElement(item)
        item?.let { updateElement(it) }
    }

    fun updateElement(item: CachedQueryProcessorSettings) {
        if (item.presentation != null) return
        executeOnPooledThread {
            try {
                val presentation = item.settings.session.presentation
                invokeLater(ModalityState.any()) {
                    val index = getIndexOf(item)
                    item.presentation = presentation
                    fireContentsChanged(item, index, index)
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.any()) {
                    val index = getIndexOf(item)
                    item.presentation = e
                    fireContentsChanged(item, index, index)
                }
            }
        }
    }
}
