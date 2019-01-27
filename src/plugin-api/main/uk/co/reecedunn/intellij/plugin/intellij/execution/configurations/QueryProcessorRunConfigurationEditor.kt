/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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

import com.intellij.lang.Language
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.*
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.fileChooser.FileNameMatcherDescriptor
import uk.co.reecedunn.intellij.plugin.core.lang.LanguageCellRenderer
import uk.co.reecedunn.intellij.plugin.core.lang.getAssociations
import uk.co.reecedunn.intellij.plugin.core.ui.EditableListPanel
import uk.co.reecedunn.intellij.plugin.core.ui.LabelledDivider
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.intellij.lang.RDF_FORMATS
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsDialog
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import java.awt.Dimension
import javax.swing.*

class QueryProcessorRunConfigurationEditor(private val project: Project, private vararg val languages: Language) :
    SettingsEditor<QueryProcessorRunConfiguration>() {

    private var editor: QueryProcessorRunConfigurationEditorUI? = null

    override fun createEditor(): JComponent {
        editor = QueryProcessorRunConfigurationEditorUI(project, *languages)
        return editor?.panel!!
    }

    override fun resetEditorFrom(configuration: QueryProcessorRunConfiguration) {
        editor!!.reset(configuration)
    }

    override fun applyEditorTo(configuration: QueryProcessorRunConfiguration) {
        editor!!.apply(configuration)
    }
}

class QueryProcessorRunConfigurationEditorUI(private val project: Project, private vararg val languages: Language) :
    SettingsUI<QueryProcessorRunConfiguration> {
    // region Option :: Query Processor

    private var queryProcessor: ComponentWithBrowseButton<JComboBox<QueryProcessorSettings>>? = null
    private var queryDivider: JPanel? = null

    private fun createQueryProcessorUI() {
        val model = DefaultComboBoxModel<QueryProcessorSettings>()
        QueryProcessors.getInstance().processors.forEach { processor ->
            model.addElement(processor)
        }

        queryProcessor = ComponentWithBrowseButton(ComboBox(model), null)
        queryProcessor!!.addActionListener {
            val list = object : EditableListPanel<QueryProcessorSettings>(model) {
                override fun add() {
                    val item = QueryProcessorSettings()
                    val dialog = QueryProcessorSettingsDialog(project)
                    if (dialog.create(item)) {
                        queryProcessor!!.childComponent.addItem(item)
                        QueryProcessors.getInstance().addProcessor(item)
                    }
                }

                override fun edit(index: Int) {
                    val item = queryProcessor!!.childComponent.getItemAt(index)
                    val dialog = QueryProcessorSettingsDialog(project)
                    if (dialog.edit(item))
                        QueryProcessors.getInstance().setProcessor(index, item)
                }

                override fun remove(index: Int) {
                    queryProcessor!!.childComponent.removeItemAt(index)
                    QueryProcessors.getInstance().removeProcessor(index)
                }
            }
            list.cellRenderer = QueryProcessorSettingsCellRenderer()
            list.emptyText = PluginApiBundle.message("xquery.configurations.processor.manage-processors-empty")

            val panel = list.createPanel()
            panel.minimumSize = Dimension(300, 200)

            val builder = DialogBuilder()
            builder.setTitle(PluginApiBundle.message("xquery.configurations.processor.manage-processors"))
            builder.setCenterPanel(panel)
            builder.showAndGet()
        }

        queryProcessor!!.childComponent.renderer = QueryProcessorSettingsCellRenderer()
        queryProcessor!!.childComponent.addActionListener { updateUI() }

        queryDivider = LabelledDivider(PluginApiBundle.message("xquery.configurations.processor.query-group.label"))
    }

    // endregion
    // region Option :: RDF Output Format

    private var rdfOutputFormat: JComboBox<Language>? = null

    private fun createRdfOutputFormatUI() {
        rdfOutputFormat = ComboBox()
        rdfOutputFormat!!.renderer = LanguageCellRenderer()

        rdfOutputFormat!!.addItem(null)
        RDF_FORMATS.forEach { rdfOutputFormat!!.addItem(it) }
    }

    // endregion
    // region Option :: Script File

    private var scriptFile: TextFieldWithBrowseButton? = null

    private fun createScriptFileUI() {
        val descriptor = FileNameMatcherDescriptor(languages.getAssociations())
        descriptor.title = PluginApiBundle.message("browser.choose.script-file")

        scriptFile = TextFieldWithBrowseButton()
        scriptFile!!.addBrowseFolderListener(null, null, project, descriptor)
    }

    // endregion
    // region Form

    private var updating: JCheckBox? = null

    private fun createUIComponents() {
        updating = JCheckBox(PluginApiBundle.message("xquery.configurations.processor.updating.label"))

        createQueryProcessorUI()
        createRdfOutputFormatUI()
        createScriptFileUI()
        updateUI()
    }

    private fun updateUI() {
        val processor = queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings
        rdfOutputFormat!!.isEnabled = processor?.api?.canOutputRdf(null) == true
        updating!!.isEnabled = processor?.api?.canUpdate(languages[0]) == true
    }

    // endregion
    // region SettingsUI

    override var panel: JPanel? = null

    override fun isModified(configuration: QueryProcessorRunConfiguration): Boolean {
        if ((queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings?)?.id != configuration.processorId)
            return true
        if ((rdfOutputFormat!!.selectedItem as? Language)?.id != configuration.rdfOutputFormat?.id)
            return true
        if (scriptFile!!.textField.text != configuration.scriptFilePath)
            return true
        return false
    }

    override fun reset(configuration: QueryProcessorRunConfiguration) {
        queryProcessor!!.childComponent.selectedItem = configuration.processor
        rdfOutputFormat!!.selectedItem = configuration.rdfOutputFormat
        scriptFile!!.textField.text = configuration.scriptFilePath ?: ""
    }

    override fun apply(configuration: QueryProcessorRunConfiguration) {
        configuration.processorId = (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings?)?.id
        configuration.rdfOutputFormat = rdfOutputFormat!!.selectedItem as? Language
        configuration.scriptFilePath = scriptFile!!.textField.text.nullize()
    }

    // endregion
}
