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
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.*
import com.intellij.ui.AnActionButtonRunnable
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.fileChooser.FileNameMatcherDescriptor
import uk.co.reecedunn.intellij.plugin.core.lang.*
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.core.ui.layout.dialog
import uk.co.reecedunn.intellij.plugin.core.ui.layout.toolbar
import uk.co.reecedunn.intellij.plugin.intellij.lang.RDF_FORMATS
import uk.co.reecedunn.intellij.plugin.intellij.lang.XPathSubset
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsDialog
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsModel
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettingsWithVersionCache
import uk.co.reecedunn.intellij.plugin.processor.query.addToModel
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

    private var queryProcessor: ComponentWithBrowseButton<JComboBox<QueryProcessorSettingsWithVersionCache>>? = null

    private fun createQueryProcessorUI() {
        val model = QueryProcessorSettingsModel()
        QueryProcessors.getInstance().processors.addToModel(model)

        queryProcessor = ComponentWithBrowseButton(ComboBox(model), null)
        queryProcessor!!.addActionListener {
            val dialog = dialog(PluginApiBundle.message("xquery.configurations.processor.manage-processors")) {
                lateinit var list: JBList<QueryProcessorSettingsWithVersionCache>
                val panel = toolbar {
                    addAction {
                        val item = QueryProcessorSettings()
                        val dialog = QueryProcessorSettingsDialog(project)
                        if (dialog.create(item)) {
                            val settings = QueryProcessorSettingsWithVersionCache(item)
                            queryProcessor!!.childComponent.addItem(settings)
                            QueryProcessors.getInstance().addProcessor(item)
                        }
                    }

                    editAction {
                        val index = list.selectedIndex
                        val item = queryProcessor!!.childComponent.getItemAt(index)
                        val dialog = QueryProcessorSettingsDialog(project)
                        if (dialog.edit(item.settings)) {
                            QueryProcessors.getInstance().setProcessor(index, item.settings)
                            model.updateElement(item)
                        }
                    }

                    removeAction {
                        val index = list.selectedIndex
                        queryProcessor!!.childComponent.removeItemAt(index)
                        QueryProcessors.getInstance().removeProcessor(index)
                    }

                    list = JBList<QueryProcessorSettingsWithVersionCache>(model)
                    list.cellRenderer = QueryProcessorSettingsCellRenderer()
                    list.setEmptyText(PluginApiBundle.message("xquery.configurations.processor.manage-processors-empty"))
                    list
                }
                panel.minimumSize = Dimension(300, 200)
                setCenterPanel(panel)
            }
            dialog.showAndGet()
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
        val settings =
            (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings
                ?: return
        executeOnPooledThread {
            val server = server!!
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
        val settings =
            (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings
                ?: return
        executeOnPooledThread {
            val database = database!!
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

    // endregion
    // region Option :: Script File

    private var scriptFile: QueryProcessorDataSource? = null
    private var scriptFileLayout: JComponent? = null

    private fun createScriptFileUI() {
        val descriptor = FileNameMatcherDescriptor(languages.getAssociations())
        descriptor.title = PluginApiBundle.message("browser.choose.script-file")

        scriptFile = QueryProcessorDataSource()
        scriptFile!!.addBrowseFolderListener(null, null, project, descriptor)
        scriptFile!!.addActionListener {
            if (languages[0].getLanguageMimeTypes()[0] == "application/sparql-query") {
                updateUI(true)
            }
        }

        scriptFileLayout = scriptFile!!.create()
    }

    // endregion
    // region Option :: Context Item

    private var contextItem: QueryProcessorDataSource? = null
    private var contextItemLayout: JComponent? = null

    private fun createContextItemUI() {
        val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        descriptor.title = PluginApiBundle.message("browser.choose.context-item")

        contextItem = QueryProcessorDataSource(allowUnspecified = true)
        contextItem!!.addBrowseFolderListener(null, null, project, descriptor)

        contextItemLayout = contextItem!!.create()
    }

    // endregion
    // region Option :: XPath Subset

    private var xpathSubset: JComboBox<XPathSubset>? = null
    private var xpathSubsetLabel: JLabel? = null

    private fun createXPathSubsetUI() {
        xpathSubset = ComboBox()
        xpathSubset!!.renderer = object : ColoredListCellRenderer<XPathSubset>() {
            override fun customizeCellRenderer(
                list: JList<out XPathSubset>, value: XPathSubset?, index: Int, selected: Boolean, hasFocus: Boolean
            ) {
                clear()
                value?.let { append(it.displayName) }
            }
        }

        xpathSubset!!.addItem(XPathSubset.XPath)
        xpathSubset!!.addItem(XPathSubset.XsltPattern)
    }

    // endregion
    // region Form

    private var tabbedPane: JTabbedPane? = null
    private var contextItemTab: JPanel? = null

    private var updating: JCheckBox? = null

    private fun createUIComponents() {
        updating = JCheckBox(PluginApiBundle.message("xquery.configurations.processor.updating.label"))

        createQueryProcessorUI()
        createRdfOutputFormatUI()
        createServerUI()
        createModulePathUI()
        createDatabaseUI()
        createScriptFileUI()
        createContextItemUI()
        createXPathSubsetUI()
    }

    private fun configureUI() {
        if (languages.findByMimeType { it == "application/xslt+xml" } != null) {
            // Use "Input" instead of "Context Item" for XSLT queries.
            val title = PluginApiBundle.message("xquery.configurations.processor.group.input.label")
            tabbedPane!!.setTitleAt(tabbedPane!!.indexOfComponent(contextItemTab), title)
        } else if (languages.findByMimeType { it == "application/xquery" || it == "application/vnd+xpath" } == null) {
            // Server-side JS, SPARQL, and SQL queries don't support an input/context item; XSLT, XQuery, and XPath do.
            tabbedPane!!.removeTabAt(tabbedPane!!.indexOfComponent(contextItemTab))
        }

        if (languages.findByMimeType { it == "application/vnd+xpath" } == null) {
            xpathSubsetLabel!!.isVisible = false
            xpathSubset!!.isVisible = false
        }
    }

    private fun updateUI(isSparql: Boolean) {
        val processor =
            (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache)?.settings
        rdfOutputFormat!!.isEnabled = processor?.api?.canOutputRdf(null) == true
        updating!!.isEnabled = processor?.api?.canUpdate(languages[0]) == true

        if (isSparql) {
            val path = scriptFile!!.path ?: ""
            val lang = languages.findByAssociations(path) ?: languages[0]
            updating!!.isSelected = !lang.getLanguageMimeTypes().contains("application/sparql-query")
        }
    }

    // endregion
    // region SettingsUI

    override var panel: JPanel? = null

    override fun isModified(configuration: QueryProcessorRunConfiguration): Boolean {
        if ((queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings?.id != configuration.processorId)
            return true
        if ((rdfOutputFormat!!.selectedItem as? Language)?.id != configuration.rdfOutputFormat?.id)
            return true
        if ((server!!.selectedItem as? String) != configuration.server)
            return true
        if ((database!!.selectedItem as? String) != configuration.database)
            return true
        if (modulePath!!.textField.text.nullize() != configuration.modulePath)
            return true
        if (scriptFile!!.type != configuration.scriptSource)
            return true
        if (scriptFile!!.path != configuration.scriptFilePath)
            return true
        if (updating!!.isSelected != configuration.updating)
            return true
        if (xpathSubset!!.selectedItem != configuration.xpathSubset)
            return true
        if (contextItem!!.type != configuration.contextItemSource)
            return true
        if (contextItem!!.path != configuration.contextItemValue)
            return true
        return false
    }

    override fun reset(configuration: QueryProcessorRunConfiguration) {
        queryProcessor?.childComponent?.let {
            (0 until it.itemCount).forEach { i ->
                if (it.getItemAt(i)?.settings?.id == configuration.processorId) {
                    it.selectedIndex = i
                }
            }
        }

        rdfOutputFormat!!.selectedItem = configuration.rdfOutputFormat
        server!!.selectedItem = configuration.server
        database!!.selectedItem = configuration.database
        modulePath!!.textField.text = configuration.modulePath ?: ""
        scriptFile!!.type = configuration.scriptSource
        scriptFile!!.path = configuration.scriptFilePath
        updating!!.isSelected = configuration.updating
        xpathSubset!!.selectedItem = configuration.xpathSubset
        contextItem!!.type = configuration.contextItemSource
        contextItem!!.path = configuration.contextItemValue

        configureUI()
        updateUI(languages.findByMimeType { it == "application/sparql-query" } != null)
    }

    override fun apply(configuration: QueryProcessorRunConfiguration) {
        configuration.processorId =
            (queryProcessor!!.childComponent.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings?.id
        configuration.rdfOutputFormat = rdfOutputFormat!!.selectedItem as? Language
        configuration.server = server!!.selectedItem as? String
        configuration.database = database!!.selectedItem as? String
        configuration.modulePath = modulePath!!.textField.text.nullize()
        configuration.scriptSource = scriptFile?.type!!
        configuration.scriptFilePath = scriptFile!!.path
        configuration.updating = updating!!.isSelected
        configuration.xpathSubset = xpathSubset!!.selectedItem as XPathSubset
        configuration.contextItemSource = contextItem?.type
        configuration.contextItemValue = contextItem?.path
    }

    // endregion
}
