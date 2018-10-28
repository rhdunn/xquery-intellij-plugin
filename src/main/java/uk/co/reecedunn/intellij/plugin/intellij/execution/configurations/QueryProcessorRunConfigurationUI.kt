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
package uk.co.reecedunn.intellij.plugin.intellij.execution.configurations

import com.intellij.openapi.ui.ComboBox
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsDialog
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import javax.swing.Action
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JPanel

class QueryProcessorRunConfigurationUI : SettingsUI<QueryProcessorRunConfiguration> {
    // region Query Processor

    private var processor: QueryProcessorSettings? = null

    private var queryProcessor: JComboBox<String>? = null
    private var createQueryProcessor: JButton? = null

    private fun createQueryProcessorUI() {
        queryProcessor = ComboBox()
        createQueryProcessor = JButton()

        createQueryProcessor!!.addActionListener {
            val settings = QueryProcessorSettings(null)
            val dialog = QueryProcessorSettingsDialog()
            if (dialog.create(settings)) {
                processor = settings
            }
        }
    }

    // endregion
    // region Form

    private fun createUIComponents() {
        createQueryProcessorUI()
    }

    // endregion
    // region SettingsUI

    override var panel: JPanel? = null

    override fun isModified(configuration: QueryProcessorRunConfiguration): Boolean {
        return false
    }

    override fun reset(configuration: QueryProcessorRunConfiguration) {
    }

    override fun apply(configuration: QueryProcessorRunConfiguration) {
    }

    // endregion
}
