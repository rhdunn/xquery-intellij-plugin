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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileTypeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.ui.ColoredListCellRenderer
import uk.co.reecedunn.intellij.plugin.core.ui.Dialog
import uk.co.reecedunn.intellij.plugin.core.ui.SettingsUI
import uk.co.reecedunn.intellij.plugin.intellij.resources.XQueryBundle
import uk.co.reecedunn.intellij.plugin.processor.query.*
import java.awt.event.ActionEvent
import javax.swing.*

class QueryProcessorSettingsDialog(private val project: Project) : Dialog<QueryProcessorSettings>() {
    override val resizable: Boolean = true
    override val createTitle: String = XQueryBundle.message("xquery.settings.dialog.query-processor.create")
    override val editTitle: String = XQueryBundle.message("xquery.settings.dialog.query-processor.edit")

    override fun validate(editor: SettingsUI<QueryProcessorSettings>, onvalidate: (Boolean) -> Unit) {
        (editor as QueryProcessorSettingsDialogUI).oninfo(
            XQueryBundle.message("xquery.settings.dialog.query-processor.validating-processor")
        )

        val settings = QueryProcessorSettings()
        editor.apply(settings)
        try {
            settings.session.version
                .execute(ModalityState.any()) { _ -> onvalidate(true) }
                .onException { e ->
                    editor.onerror(e.toQueryUserMessage())
                    onvalidate(false)
                }
        } catch (e: Throwable) {
            editor.onerror(e.toQueryUserMessage())
            onvalidate(false)
        }
    }

    override fun createSettingsUI(): SettingsUI<QueryProcessorSettings> {
        return QueryProcessorSettingsDialogUI(project)
    }
}

private fun JTextField.textOrNull(): String? {
    return text?.let { if (it.isEmpty()) null else it }
}

class QueryProcessorSettingsDialogUI(private val project: Project) : SettingsUI<QueryProcessorSettings> {
    // region Processor APIs

    private var api: JComboBox<QueryProcessorApi>? = null

    private fun createQueryProcessorApiUI() {
        api = ComboBox()
        api!!.renderer = object : ColoredListCellRenderer<QueryProcessorApi>() {
            override fun customizeCellRenderer(
                list: JList<out QueryProcessorApi>,
                value: QueryProcessorApi?,
                index: Int, selected: Boolean, hasFocus: Boolean
            ) {
                if (value != null) {
                    append(value.displayName)
                }
            }
        }

        QUERY_PROCESSOR_APIS.forEach { value -> api!!.addItem(value) }

        api!!.addActionListener { _ ->
            val selection = api!!.selectedItem as QueryProcessorApi
            jar!!.isEnabled = selection.requireJar
            configuration!!.isEnabled = selection.hasConfiguration

            if (selection.canCreate && !selection.canConnect) { // create only
                standalone!!.isEnabled = false
                standalone!!.isSelected = true
            } else if (!selection.canCreate && selection.canConnect) { // connect only
                standalone!!.isEnabled = false
                standalone!!.isSelected = false
            } else { // create and connect
                standalone!!.isEnabled = true
            }

            val e = ActionEvent(standalone, 0, "api", 0)
            standalone!!.actionListeners.forEach { listener -> listener.actionPerformed(e) }
        }
    }

    // endregion
    // region Processor JAR

    private var jar: ComponentWithBrowseButton<JTextField>? = null

    private fun createJarUI() {
        jar = ComponentWithBrowseButton(JTextField(), null)
        jar!!.addBrowseFolderListener(
            XQueryBundle.message("browser.choose.implementation-api-jar"), null,
            project,
            FileTypeDescriptor(XQueryBundle.message("browser.choose.implementation-api-jar"), "jar"),
            TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
    }

    // endregion
    // region Configuration File

    private var configuration: ComponentWithBrowseButton<JTextField>? = null

    private fun createConfigurationUI() {
        configuration = ComponentWithBrowseButton(JTextField(), null)
        configuration!!.addBrowseFolderListener(
            XQueryBundle.message("browser.choose.configuration"), null,
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor(),
            TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        )
    }

    // endregion
    // region Standalone

    private var standalone: JCheckBox? = null

    private fun createStandaloneUI() {
        standalone = JCheckBox()
        standalone!!.addActionListener { action ->
            val serverEnabled = !standalone!!.isSelected
            hostname!!.isEnabled = serverEnabled
            databasePort!!.isEnabled = serverEnabled
            adminPort!!.isEnabled = serverEnabled && (api!!.selectedItem as QueryProcessorApi).hasAdminPort
            username!!.isEnabled = serverEnabled
            password!!.isEnabled = serverEnabled
        }
    }

    // endregion
    // region Form

    private var name: JTextField? = null
    private var hostname: JTextField? = null
    private var databasePort: JTextField? = null
    private var adminPort: JTextField? = null
    private var username: JTextField? = null
    private var password: JPasswordField? = null
    private var errorMessage: JLabel? = null

    private fun createUIComponents() {
        name = JTextField()
        hostname = JTextField()
        databasePort = JTextField()
        adminPort = JTextField()
        username = JTextField()
        password = JPasswordField()

        errorMessage = JLabel()
        errorMessage!!.isVisible = false

        createQueryProcessorApiUI()
        createJarUI()
        createConfigurationUI()
        createStandaloneUI()
    }

    fun oninfo(message: String) {
        errorMessage!!.icon = AllIcons.General.Information
        errorMessage!!.text = message
        errorMessage!!.isVisible = true
    }

    fun onerror(message: String) {
        errorMessage!!.icon = AllIcons.General.Error
        errorMessage!!.text = message
        errorMessage!!.isVisible = true
    }

    // endregion
    // region SettingsUI

    override var panel: JPanel? = null

    override fun isModified(configuration: QueryProcessorSettings): Boolean {
        return false
    }

    override fun reset(configuration: QueryProcessorSettings) {
        name!!.text = configuration.name
        api!!.selectedItem = configuration.api
        jar!!.childComponent.text = configuration.jar
        this.configuration!!.childComponent.text = configuration.configurationPath
        if (configuration.connection != null) {
            standalone!!.isSelected = true
            hostname!!.text = configuration.connection!!.hostname
            databasePort!!.text = configuration.connection!!.databasePort.toString()
            adminPort!!.text = configuration.connection!!.adminPort.toString()
            username!!.text = configuration.connection!!.username
            password!!.text = configuration.connection!!.password
        } else {
            standalone!!.isSelected = false
            hostname!!.text = ""
            databasePort!!.text = "0"
            adminPort!!.text = "0"
            username!!.text = ""
            password!!.text = ""
        }
    }

    override fun apply(configuration: QueryProcessorSettings) {
        configuration.name = name!!.textOrNull()
        configuration.api = api!!.selectedItem as QueryProcessorApi
        configuration.jar = jar!!.childComponent.textOrNull()
        configuration.configurationPath = this.configuration!!.childComponent.textOrNull()
        if (!standalone!!.isSelected) {
            val dbPort = databasePort!!.text.toInt()
            val amPort = adminPort!!.text.toInt()
            val user = username!!.textOrNull()
            val pass = password!!.password?.let { if (it.isEmpty()) null else String(it) }
            configuration.connection = ConnectionSettings(hostname!!.text, dbPort, amPort, user, pass?.toString())
        } else {
            configuration.connection = null
        }
    }

    // endregion
}
