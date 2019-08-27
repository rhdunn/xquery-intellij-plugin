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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import uk.co.reecedunn.intellij.plugin.core.async.pooled_thread
import uk.co.reecedunn.intellij.plugin.processor.query.*
import javax.swing.DefaultComboBoxModel
import javax.swing.JList

class QueryProcessorSettingsModel : DefaultComboBoxModel<QueryProcessorSettingsWithVersionCache>() {
    override fun addElement(item: QueryProcessorSettingsWithVersionCache?) {
        super.addElement(item)
        item?.let { updateElement(it) }
    }

    fun updateElement(item: QueryProcessorSettingsWithVersionCache) {
        pooled_thread { item.settings.session.version }
            .execute { version ->
                val index = getIndexOf(item)
                item.version = version
                fireContentsChanged(item, index, index)
            }
            .onException { e ->
                val index = getIndexOf(item)
                item.version = e
                fireContentsChanged(item, index, index)
            }
    }
}
