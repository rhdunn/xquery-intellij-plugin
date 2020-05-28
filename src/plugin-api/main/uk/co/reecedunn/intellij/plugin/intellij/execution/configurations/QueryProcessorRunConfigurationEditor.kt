/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.*
import com.intellij.ui.AnActionButton
import com.intellij.ui.components.JBList
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.fileChooser.FileNameMatcherDescriptor
import uk.co.reecedunn.intellij.plugin.core.lang.*
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.QueryProcessorDataSource
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.queryProcessorDataSource
import uk.co.reecedunn.intellij.plugin.intellij.lang.RDF_FORMATS
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorSettingsDialog
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorSettingsModel
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettingsWithVersionCache
import uk.co.reecedunn.intellij.plugin.processor.query.addToModel
import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.*

class QueryProcessorRunConfigurationEditor(private val project: Project, private vararg val languages: Language) :
    SettingsEditor<QueryProcessorRunConfiguration>() {
    // region Manage Query Processor Dialog

    private lateinit var queryProcessor: ComponentWithBrowseButton<JComboBox<QueryProcessorSettingsWithVersionCache>>
    private lateinit var model: QueryProcessorSettingsModel
    private lateinit var list: JBList<QueryProcessorSettingsWithVersionCache>

    private fun addQueryProcessor(@Suppress("UNUSED_PARAMETER") button: AnActionButton) {
        val item = QueryProcessorSettings()
        val dialog = QueryProcessorSettingsDialog(project)
        if (dialog.create(item)) {
            val settings = QueryProcessorSettingsWithVersionCache(item)
            queryProcessor.childComponent.addItem(settings)
            QueryProcessors.getInstance().addProcessor(item)
        }
    }

    private fun editQueryProcessor(@Suppress("UNUSED_PARAMETER") button: AnActionButton) {
        val index = list.selectedIndex
        val item = queryProcessor.childComponent.getItemAt(index)
        val dialog = QueryProcessorSettingsDialog(project)
        if (dialog.edit(item.settings)) {
            QueryProcessors.getInstance().setProcessor(index, item.settings)
            model.updateElement(item)
        }
    }

    private fun removeQueryProcessor(@Suppress("UNUSED_PARAMETER") button: AnActionButton) {
        val index = list.selectedIndex
        queryProcessor.childComponent.removeItemAt(index)
        QueryProcessors.getInstance().removeProcessor(index)
    }

    private val manageQueryProcessorsAction: ActionListener
        get() = ActionListener {
            val dialog = dialog(PluginApiBundle.message("xquery.configurations.processor.manage-processors")) {
                toolbarPanel(minimumSize = Dimension(300, 200)) {
                    addAction(::addQueryProcessor)
                    editAction(::editQueryProcessor)
                    removeAction(::removeQueryProcessor)

                    list = JBList(model)
                    list.cellRenderer = QueryProcessorSettingsCellRenderer()
                    list.setEmptyText(PluginApiBundle.message("xquery.configurations.processor.manage-processors-empty"))
                    list
                }
            }
            dialog.showAndGet()
        }

    // endregion
    // region "Query" Page

    private lateinit var rdfOutputFormat: JComboBox<Language>
    private lateinit var scriptFile: QueryProcessorDataSource
    private lateinit var xpathSubset: JComboBox<XPathSubset>
    private lateinit var updating: JCheckBox

    private lateinit var xpathSubsetLabel: JLabel

    private val queryPanel: JPanel = panel {
        model = QueryProcessorSettingsModel()
        QueryProcessors.getInstance().processors.addToModel(model)

        row {
            label(PluginApiBundle.message("xquery.configurations.processor.query-processor.label"), column.vgap())
            queryProcessor = componentWithBrowseButton(column.horizontal().hgap().vgap(), manageQueryProcessorsAction) {
                comboBox<QueryProcessorSettingsWithVersionCache>(model) {
                    renderer = QueryProcessorSettingsCellRenderer()
                    addActionListener {
                        updateUI(false)
                        populateServerUI()
                        populateDatabaseUI()
                    }
                }
            }
        }
        row {
            label(
                PluginApiBundle.message("xquery.configurations.processor.run-query-from.label"),
                column.spanCols().vgap(6, LayoutPosition.Both)
            )
        }
        scriptFile = queryProcessorDataSource {
            val descriptor = FileNameMatcherDescriptor(languages.getAssociations())
            descriptor.title = PluginApiBundle.message("browser.choose.script-file")
            addBrowseFolderListener(null, null, project, descriptor)
            addActionListener {
                if (languages[0].getLanguageMimeTypes()[0] == "application/sparql-query") {
                    updateUI(true)
                }
            }
        }
        row {
            xpathSubsetLabel = label(
                PluginApiBundle.message("xquery.configurations.processor.xpath-subset.label"), column.vgap()
            )
            xpathSubset = comboBox(column.horizontal().hgap().vgap()) {
                renderer = coloredListCellRenderer { _, value, _, _, _ ->
                    clear()
                    value?.let { append(it.displayName) }
                }
                addItem(XPathSubset.XPath)
                addItem(XPathSubset.XsltPattern)
            }
        }
        row {
            updating = checkBox(column.spanCols()) {
                text = PluginApiBundle.message("xquery.configurations.processor.updating.label")
            }
        }
        row {
            spacer(column.vertical())
        }
    }

    // endregion
    // region "Database" Page

    private lateinit var server: JComboBox<String>

    private lateinit var modulePath: TextFieldWithBrowseButton

    private lateinit var database: JComboBox<String>

    private fun populateServerUI() {
        val settings =
            (queryProcessor.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings
                ?: return
        executeOnPooledThread {
            try {
                val servers = settings.session.servers
                invokeLater(ModalityState.any()) {
                    val current = server.selectedItem
                    server.removeAllItems()
                    server.addItem(null)
                    servers.forEach { name -> server.addItem(name) }
                    server.selectedItem = current
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.any()) {
                    server.removeAllItems()
                    server.addItem(null)
                }
            }
        }
    }

    private fun populateDatabaseUI() {
        val settings =
            (queryProcessor.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings
                ?: return
        executeOnPooledThread {
            try {
                val databases = settings.session.databases
                invokeLater(ModalityState.any()) {
                    val current = database.selectedItem
                    database.removeAllItems()
                    database.addItem(null)
                    databases.forEach { name -> database.addItem(name) }
                    database.selectedItem = current
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.any()) {
                    database.removeAllItems()
                    database.addItem(null)
                }
            }
        }
    }

    private val databasePanel: JPanel = panel {
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.server.label"), column.vgap())
            server = comboBox(column.horizontal().hgap().vgap()) {
                isEditable = true
                addItem(null)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.module-root.label"), column.vgap())
            modulePath = textFieldWithBrowseButton(column.horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                descriptor.title = PluginApiBundle.message("browser.choose.module-path")
                addBrowseFolderListener(null, null, project, descriptor)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.content-database.label"), column)
            database = comboBox(column.horizontal().hgap()) {
                isEditable = true
                addItem(null)
            }
        }
        row {
            spacer(column.vertical())
            spacer(column.horizontal())
        }
    }

    // endregion
    // region "Context Item"/"Input" Page

    private lateinit var contextItem: QueryProcessorDataSource

    private val inputLabel: String?
        get() {
            return when {
                languages.findByMimeType { it == "application/xslt+xml" } != null -> {
                    // Use "Input" instead of "Context Item" for XSLT queries.
                    PluginApiBundle.message("xquery.configurations.processor.group.input.label")
                }
                languages.findByMimeType { it == "application/xquery" || it == "application/vnd+xpath" } == null -> {
                    // Server-side JS, SPARQL, and SQL queries don't support an input/context item;
                    // XSLT, XQuery, and XPath do.
                    null
                }
                else -> PluginApiBundle.message("xquery.configurations.processor.group.context-item.label")
            }
        }

    private val inputPanel: JPanel = panel {
        contextItem = queryProcessorDataSource(allowUnspecified = true) {
            val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
            descriptor.title = PluginApiBundle.message("browser.choose.context-item")
            addBrowseFolderListener(null, null, project, descriptor)
        }
        row {
            spacer(column.vertical())
        }
    }

    // endregion
    // region "Output" Page

    private lateinit var reformatResults: JCheckBox

    private val outputPanel: JPanel = panel {
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.rdf-format.label"), column.vgap())
            rdfOutputFormat = comboBox(column.horizontal().hgap().vgap()) {
                renderer = LanguageCellRenderer()
                addItem(null)
                RDF_FORMATS.forEach { addItem(it) }
            }
        }
        row {
            reformatResults = checkBox(column.horizontal().spanCols()) {
                text = PluginApiBundle.message("xquery.configurations.processor.reformat-results.label")
            }
        }
        row {
            spacer(column.vertical())
            spacer(column.horizontal())
        }
    }

    // endregion
    // region Form

    private fun configureUI() {
        if (languages.findByMimeType { it == "application/vnd+xpath" } == null) {
            xpathSubsetLabel.isVisible = false
            xpathSubset.isVisible = false
        }
    }

    private fun updateUI(isSparql: Boolean) {
        val processor =
            (queryProcessor.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache)?.settings
        rdfOutputFormat.isEnabled = processor?.api?.canOutputRdf(null) == true
        updating.isEnabled = processor?.api?.canUpdate(languages[0]) == true

        if (isSparql) {
            val path = scriptFile.path ?: ""
            val lang = languages.findByAssociations(path) ?: languages[0]
            updating.isSelected = !lang.getLanguageMimeTypes().contains("application/sparql-query")
        }
    }

    private val panel: JPanel = tabbedPanel {
        tab(PluginApiBundle.message("xquery.configurations.processor.group.query.label"), queryPanel)
        tab(PluginApiBundle.message("xquery.configurations.processor.group.database.label"), databasePanel)
        inputLabel?.let { tab(it, inputPanel) }
        tab(PluginApiBundle.message("xquery.configurations.processor.group.output.label"), outputPanel)
    }

    // endregion
    // region SettingsEditor

    override fun createEditor(): JComponent = panel

    override fun resetEditorFrom(configuration: QueryProcessorRunConfiguration) {
        queryProcessor.childComponent.let {
            (0 until it.itemCount).forEach { i ->
                if (it.getItemAt(i)?.settings?.id == configuration.processorId) {
                    it.selectedIndex = i
                }
            }
        }

        rdfOutputFormat.selectedItem = configuration.rdfOutputFormat
        server.selectedItem = configuration.server
        database.selectedItem = configuration.database
        modulePath.textField.text = configuration.modulePath ?: ""
        scriptFile.type = configuration.scriptSource
        scriptFile.path = configuration.scriptFilePath
        updating.isSelected = configuration.updating
        xpathSubset.selectedItem = configuration.xpathSubset
        contextItem.type = configuration.contextItemSource
        contextItem.path = configuration.contextItemValue
        reformatResults.isSelected = configuration.reformatResults

        configureUI()
        updateUI(languages.findByMimeType { it == "application/sparql-query" } != null)
    }

    override fun applyEditorTo(configuration: QueryProcessorRunConfiguration) {
        configuration.processorId =
            (queryProcessor.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings?.id
        configuration.rdfOutputFormat = rdfOutputFormat.selectedItem as? Language
        configuration.server = server.selectedItem as? String
        configuration.database = database.selectedItem as? String
        configuration.modulePath = modulePath.textField.text.nullize()
        configuration.scriptSource = scriptFile.type!!
        configuration.scriptFilePath = scriptFile.path
        configuration.updating = updating.isSelected
        configuration.xpathSubset = xpathSubset.selectedItem as XPathSubset
        configuration.contextItemSource = contextItem.type
        configuration.contextItemValue = contextItem.path
        configuration.reformatResults = reformatResults.isSelected
    }

    // endregion
}
