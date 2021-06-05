/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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

import com.intellij.navigation.ItemPresentation
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import uk.co.reecedunn.intellij.plugin.core.ui.Insets
import uk.co.reecedunn.intellij.plugin.processor.query.CachedQueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.toQueryUserMessage
import javax.swing.JList

class QueryProcessorSettingsCellRenderer : ColoredListCellRenderer<CachedQueryProcessorSettings>() {
    private fun render(value: QueryProcessorSettings, presentation: ItemPresentation, index: Int) {
        clear()

        icon = presentation.getIcon(false)
        ipad = Insets.listCellRenderer(index)

        append(presentation.presentableText!!)
        value.name?.let { append(" ($it)", SimpleTextAttributes.GRAY_ATTRIBUTES) }
    }

    private fun renderError(value: QueryProcessorSettings, message: String, index: Int) {
        clear()

        icon = value.api.presentation.getIcon(false)
        ipad = Insets.listCellRenderer(index)

        append(value.api.presentation.presentableText!!, SimpleTextAttributes.ERROR_ATTRIBUTES)
        value.name?.let { append(" ($it)", SimpleTextAttributes.ERROR_ATTRIBUTES) }
        append(" [$message]", SimpleTextAttributes.ERROR_ATTRIBUTES)
    }

    override fun customizeCellRenderer(
        list: JList<out CachedQueryProcessorSettings>,
        value: CachedQueryProcessorSettings?,
        index: Int, selected: Boolean, hasFocus: Boolean
    ) {
        if (value != null) when (val presentation = value.presentation) {
            null -> render(value.settings, value.settings.api.presentation, index)
            is ItemPresentation -> render(value.settings, presentation, index)
            is Throwable -> renderError(value.settings, presentation.toQueryUserMessage(), index)
        }
    }
}
