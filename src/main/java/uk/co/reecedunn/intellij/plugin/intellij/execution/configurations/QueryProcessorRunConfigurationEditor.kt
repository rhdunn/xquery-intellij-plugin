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

import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogBuilder
import uk.co.reecedunn.intellij.plugin.core.ui.EditableListPanel
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsDialog
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessor
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import java.awt.Dimension
import javax.swing.*

class QueryProcessorRunConfigurationEditor(private val project: Project) :
    SettingsEditor<QueryProcessorRunConfiguration>() {

    private var editor: QueryProcessorRunConfigurationEditorUI? = null

    override fun createEditor(): JComponent {
        editor = QueryProcessorRunConfigurationEditorUI(project)
        return editor?.panel!!
    }

    override fun resetEditorFrom(configuration: QueryProcessorRunConfiguration) {
        editor!!.reset(configuration)
    }

    override fun applyEditorTo(configuration: QueryProcessorRunConfiguration) {
        editor!!.apply(configuration)
    }
}

class QueryProcessorRunConfigurationEditorUI(private val project: Project) :
    SettingsUI<QueryProcessorRunConfiguration> {
    // region Query Processor

    private var queryProcessor: JComboBox<QueryProcessorSettings>? = null
    private var manageQueryProcessors: JButton? = null

    private fun createQueryProcessorUI() {
        val model = DefaultComboBoxModel<QueryProcessorSettings>()
        QueryProcessors.getInstance().processors.forEach { processor ->
            model.addElement(processor)
        }

        queryProcessor = ComboBox(model)
        queryProcessor!!.renderer = QueryProcessorSettingsCellRenderer()

        manageQueryProcessors = JButton()
        manageQueryProcessors!!.addActionListener {
            val list = object : EditableListPanel<QueryProcessorSettings>(model) {
                override fun add() {
                    val item = QueryProcessorSettings()
                    val dialog = QueryProcessorSettingsDialog(project)
                    if (dialog.create(item)) {
                        queryProcessor!!.addItem(item)
                        QueryProcessors.getInstance().addProcessor(item)
                    }
                }

                override fun edit(index: Int) {
                    val item = queryProcessor!!.getItemAt(index)
                    val dialog = QueryProcessorSettingsDialog(project)
                    if (dialog.edit(item))
                        QueryProcessors.getInstance().setProcessor(index, item)
                }

                override fun remove(index: Int) {
                    queryProcessor!!.removeItemAt(index)
                    QueryProcessors.getInstance().removeProcessor(index)
                }
            }
            list.cellRenderer = QueryProcessorSettingsCellRenderer()
            list.emptyText = XQueryBundle.message("xquery.configurations.processor.manage-processors-empty")

            val panel = list.createPanel()
            panel.minimumSize = Dimension(300, 200)

            val builder = DialogBuilder()
            builder.setTitle(XQueryBundle.message("xquery.configurations.processor.manage-processors"))
            builder.setCenterPanel(panel)
            builder.showAndGet()
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
        if ((queryProcessor!!.selectedItem as? QueryProcessorSettings?)?.id != configuration.processorId)
            return true
        return false
    }

    override fun reset(configuration: QueryProcessorRunConfiguration) {
        queryProcessor!!.selectedItem = configuration.processor
    }

    override fun apply(configuration: QueryProcessorRunConfiguration) {
        configuration.processorId = (queryProcessor!!.selectedItem as? QueryProcessorSettings?)?.id
    }

    // endregion
}
