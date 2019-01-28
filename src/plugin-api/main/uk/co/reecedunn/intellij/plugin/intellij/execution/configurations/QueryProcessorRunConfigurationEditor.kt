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
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.*
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.fileChooser.FileNameMatcherDescriptor
import uk.co.reecedunn.intellij.plugin.core.lang.LanguageCellRenderer
import uk.co.reecedunn.intellij.plugin.core.lang.findByAssociations
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
        queryProcessor!!.childComponent.addActionListener {
            updateUI(false)
            populateServerUI()
            populateDatabaseUI()
        }
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
    // region Option :: Server

    private var server: JComboBox<String>? = null

    private fun createServerUI() {
        server = ComboBox()
        server!!.isEditable = true

        server!!.addItem(null)
    }

    private fun populateServerUI() {
        val session = (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings?)?.session
        session?.servers?.execute { servers ->
            val server = server!!
            val current = server.selectedItem
            server.removeAllItems()
            server.addItem(null)
            servers.forEach { name -> server.addItem(name) }
            server.selectedItem = current
        }
    }

    // endregion
    // region Option :: Module Path

    private var modulePath: TextFieldWithBrowseButton? = null

    private fun createModulePathUI() {
        val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
        descriptor.title = PluginApiBundle.message("browser.choose.module-path")

        modulePath = TextFieldWithBrowseButton()
        modulePath!!.addBrowseFolderListener(null, null, project, descriptor)
    }

    // endregion
    // region Option :: Database

    private var database: JComboBox<String>? = null

    private fun createDatabaseUI() {
        database = ComboBox()
        database!!.isEditable = true

        database!!.addItem(null)
    }

    private fun populateDatabaseUI() {
        val session = (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings?)?.session
        session?.databases?.execute { databases ->
            val database = database!!
            val current = database.selectedItem
            database.removeAllItems()
            database.addItem(null)
            databases.forEach { name -> database.addItem(name) }
            database.selectedItem = current
        }
    }

    // endregion
    // region Option :: Script File

    private var scriptFile: TextFieldWithBrowseButton? = null

    private fun createScriptFileUI() {
        val descriptor = FileNameMatcherDescriptor(languages.getAssociations())
        descriptor.title = PluginApiBundle.message("browser.choose.script-file")

        scriptFile = TextFieldWithBrowseButton()
        scriptFile!!.addBrowseFolderListener(null, null, project, descriptor)
        scriptFile!!.textField.addActionListener {
            if (languages[0].mimeTypes[0] == "application/sparql-query") {
                updateUI(true)
            }
        }
    }

    // endregion
    // region Form

    private var queryDivider: JPanel? = null
    private var databaseDivider: JPanel? = null

    private var updating: JCheckBox? = null

    private fun createUIComponents() {
        queryDivider = LabelledDivider(PluginApiBundle.message("xquery.configurations.processor.query-group.label"))
        databaseDivider = LabelledDivider(PluginApiBundle.message("xquery.configurations.processor.database-group.label"))

        updating = JCheckBox(PluginApiBundle.message("xquery.configurations.processor.updating.label"))

        createQueryProcessorUI()
        createRdfOutputFormatUI()
        createServerUI()
        createModulePathUI()
        createDatabaseUI()
        createScriptFileUI()
    }

    private fun updateUI(isSparql: Boolean) {
        val processor = queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings
        rdfOutputFormat!!.isEnabled = processor?.api?.canOutputRdf(null) == true
        updating!!.isEnabled = processor?.api?.canUpdate(languages[0]) == true

        if (isSparql) {
            val path = scriptFile!!.textField.text
            val lang = languages.findByAssociations(path) ?: languages[0]
            updating!!.isSelected = lang.mimeTypes[0] != "application/sparql-query"
        }
    }

    // endregion
    // region SettingsUI

    override var panel: JPanel? = null

    override fun isModified(configuration: QueryProcessorRunConfiguration): Boolean {
        if ((queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings?)?.id != configuration.processorId)
            return true
        if ((rdfOutputFormat!!.selectedItem as? Language)?.id != configuration.rdfOutputFormat?.id)
            return true
        if ((server!!.selectedItem as? String) != configuration.server)
            return true
        if ((database!!.selectedItem as? String) != configuration.database)
            return true
        if (modulePath!!.textField.text.nullize() != configuration.modulePath)
            return true
        if (scriptFile!!.textField.text != configuration.scriptFilePath)
            return true
        return false
    }

    override fun reset(configuration: QueryProcessorRunConfiguration) {
        queryProcessor!!.childComponent.selectedItem = configuration.processor
        rdfOutputFormat!!.selectedItem = configuration.rdfOutputFormat
        server!!.selectedItem = configuration.server
        database!!.selectedItem = configuration.database
        modulePath!!.textField.text = configuration.modulePath ?: ""
        scriptFile!!.textField.text = configuration.scriptFilePath ?: ""

        updateUI(languages[0].mimeTypes[0] == "application/sparql-query")
    }

    override fun apply(configuration: QueryProcessorRunConfiguration) {
        configuration.processorId = (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettings?)?.id
        configuration.rdfOutputFormat = rdfOutputFormat!!.selectedItem as? Language
        configuration.server = server!!.selectedItem as? String
        configuration.database = database!!.selectedItem as? String
        configuration.modulePath = modulePath!!.textField.text.nullize()
        configuration.scriptFilePath = scriptFile!!.textField.text.nullize()
    }

    // endregion
}
