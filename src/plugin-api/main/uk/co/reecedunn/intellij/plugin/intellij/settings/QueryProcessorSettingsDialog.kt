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
package uk.co.reecedunn.intellij.plugin.intellij.settings

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.fileChooser.FileTypeDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.ui.Dialog
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.*
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.*

private fun JTextField.textOrNull(): String? = text?.let { if (it.isEmpty()) null else it }

class QueryProcessorSettingsDialog(private val project: Project) : Dialog<QueryProcessorSettings>() {
    // region Dialog

    override val resizable: Boolean = true
    override val createTitle: String = PluginApiBundle.message("xquery.settings.dialog.query-processor.create")
    override val editTitle: String = PluginApiBundle.message("xquery.settings.dialog.query-processor.edit")

    override fun validate(onvalidate: (Boolean) -> Unit) {
        oninfo(PluginApiBundle.message("xquery.settings.dialog.query-processor.validating-processor"))

        val settings = QueryProcessorSettings()
        apply(settings)
        executeOnPooledThread {
            try {
                settings.session.version
                invokeLater(ModalityState.any()) {
                    onvalidate(true)
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.any()) {
                    onerror(e.toQueryUserMessage())
                    onvalidate(false)
                }
            }
        }
    }

    // endregion
    // region Processor APIs

    private val selectQueryProcessor: ActionListener
        get() = ActionListener {
            val selection = api.selectedItem as QueryProcessorApi
            jar.isEnabled = selection.requireJar
            configuration.isEnabled = selection.hasConfiguration

            if (selection.canCreate && !selection.canConnect) { // create only
                standalone.isEnabled = false
                standalone.isSelected = true
            } else if (!selection.canCreate && selection.canConnect) { // connect only
                standalone.isEnabled = false
                standalone.isSelected = false
            } else { // create and connect
                standalone.isEnabled = true
            }

            val e = ActionEvent(standalone, 0, "api", 0)
            standalone.actionListeners.forEach { listener -> listener.actionPerformed(e) }
        }

    // endregion
    // region Error Message

    private fun oninfo(message: String) {
        errorMessage.icon = AllIcons.General.Information
        errorMessage.text = message
        errorMessage.isVisible = true
    }

    private fun onerror(message: String) {
        errorMessage.icon = AllIcons.General.Error
        errorMessage.text = message
        errorMessage.isVisible = true
    }

    // endregion
    // region SettingsUI

    private lateinit var description: JTextField
    private lateinit var api: JComboBox<QueryProcessorApi>
    private lateinit var jar: TextFieldWithBrowseButton
    private lateinit var configuration: TextFieldWithBrowseButton
    private lateinit var standalone: JCheckBox
    private lateinit var hostname: JTextField
    private lateinit var databasePort: JTextField
    private lateinit var username: JTextField
    private lateinit var password: JPasswordField
    private lateinit var errorMessage: JLabel

    override val panel: JPanel = panel {
        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.name.label"), grid(0, 0))
        description = textField(grid(1, 0).horizontal().hgap().vgap()) { minimumSize = Dimension(250, -1) }

        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.implementation.label"), grid(0, 1))
        api = comboBox(grid(1, 1).horizontal().hgap().vgap()) {
            renderer = coloredListCellRenderer { value ->
                if (value != null) {
                    append(value.displayName)
                }
            }

            QueryProcessorApis.forEach { value -> addItem(value) }
            addActionListener(selectQueryProcessor)
        }

        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.api-jar.label"), grid(0, 2))
        jar = textFieldWithBrowseButton(grid(1, 2).horizontal().hgap().vgap()) {
            addBrowseFolderListener(
                PluginApiBundle.message("browser.choose.implementation-api-jar"), null,
                project,
                FileTypeDescriptor(PluginApiBundle.message("browser.choose.implementation-api-jar"), "jar")
            )
        }

        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.config-path.label"), grid(0, 3))
        configuration = textFieldWithBrowseButton(grid(1, 3).horizontal().hgap().vgap()) {
            addBrowseFolderListener(
                PluginApiBundle.message("browser.choose.configuration"), null,
                project,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        standalone = checkBox(grid(0, 4).horizontal().spanCols().vgap()) {
            text = PluginApiBundle.message("xquery.settings.dialog.query-processor.standalone.label")
            addActionListener {
                val serverEnabled = !isSelected
                hostname.isEnabled = serverEnabled
                databasePort.isEnabled = serverEnabled
                username.isEnabled = serverEnabled
                password.isEnabled = serverEnabled
            }
        }

        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.hostname.label"), grid(0, 5))
        hostname = textField(grid(1, 5).horizontal().hgap().vgap())

        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.database-port.label"), grid(0, 6))
        databasePort = textField(grid(1, 6).horizontal().hgap().vgap())

        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.username.label"), grid(0, 7))
        username = textField(grid(1, 7).horizontal().hgap().vgap())

        label(PluginApiBundle.message("xquery.settings.dialog.query-processor.password.label"), grid(0, 8))
        password = passwordField(grid(1, 8).horizontal().hgap().vgap())

        errorMessage = label(grid(0, 9).horizontal().spanCols()) {
            isVisible = false
        }
    }

    override fun isModified(configuration: QueryProcessorSettings): Boolean = false

    override fun reset(configuration: QueryProcessorSettings) {
        description.text = configuration.name
        api.selectedItem = configuration.api
        jar.textField.text = configuration.jar
        this.configuration.textField.text = configuration.configurationPath
        if (configuration.connection != null) {
            standalone.isSelected = false
            hostname.text = configuration.connection!!.hostname
            databasePort.text = configuration.connection!!.databasePort.toString()
            username.text = configuration.connection!!.username
            password.text = configuration.connection!!.password
        } else {
            standalone.isSelected = true
            hostname.text = ""
            databasePort.text = "0"
            username.text = ""
            password.text = ""
        }

        val e = ActionEvent(standalone, 0, "reset", 0)
        standalone.actionListeners.forEach { listener -> listener.actionPerformed(e) }
    }

    override fun apply(configuration: QueryProcessorSettings) {
        configuration.name = description.textOrNull()
        configuration.api = api.selectedItem as QueryProcessorApi
        configuration.jar = jar.textField.textOrNull()
        configuration.configurationPath = this.configuration.textField.textOrNull()
        if (!standalone.isSelected) {
            val dbPort = databasePort.text.toInt()
            val user = username.textOrNull()
            configuration.connection = ConnectionSettings(hostname.text, dbPort, user)
            configuration.connection!!.setPassword(password.password?.let { if (it.isEmpty()) null else it })
        } else {
            configuration.connection = null
        }
    }

    // endregion
}
