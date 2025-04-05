// Copyright (C) 2018-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.query.settings

import com.intellij.compat.openapi.fileChooser.withExtensionFilterEx
import com.intellij.compat.openapi.ui.addBrowseFolderListenerEx
import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.ui.Dialog
import uk.co.reecedunn.intellij.plugin.core.ui.Insets
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorApi
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.connection.AWSConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.connection.ConnectionSettings
import uk.co.reecedunn.intellij.plugin.processor.query.toQueryUserMessage
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.*

class QueryProcessorSettingsDialog(private val project: Project) : Dialog<QueryProcessorSettings>() {
    // region Dialog

    var presentation: ItemPresentation? = null

    override val resizable: Boolean = true
    override val createTitle: String = PluginApiBundle.message("xquery.settings.dialog.query-processor.create")
    override val editTitle: String = PluginApiBundle.message("xquery.settings.dialog.query-processor.edit")

    override fun validate(onvalidate: (Boolean) -> Unit) {
        oninfo(PluginApiBundle.message("xquery.settings.dialog.query-processor.validating-processor"))

        val settings = QueryProcessorSettings()
        apply(settings)
        executeOnPooledThread {
            try {
                presentation = settings.session.presentation
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
                standaloneInstance.isEnabled = false
                serverInstance.isEnabled = false
                awsInstance.isEnabled = false

                standaloneInstance.isSelected = true
            } else if (!selection.canCreate && selection.canConnect) { // connect only
                standaloneInstance.isEnabled = false
                serverInstance.isEnabled = true
                awsInstance.isEnabled = true

                serverInstance.isSelected = true
            } else { // create and connect
                standaloneInstance.isEnabled = true
                serverInstance.isEnabled = true
                awsInstance.isEnabled = true
            }

            updateInstanceSelection()
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

    private lateinit var standaloneInstance: JRadioButton

    private lateinit var serverInstance: JRadioButton
    private lateinit var hostname: JTextField

    private lateinit var awsInstance: JRadioButton
    private lateinit var awsApplication: TextFieldWithBrowseButton
    private lateinit var awsProfile: JTextField
    private lateinit var awsRegion: JTextField
    private lateinit var awsInstanceName: JTextField

    private lateinit var databasePort: JTextField
    private lateinit var username: JTextField
    private lateinit var password: JPasswordField

    private lateinit var errorMessage: JLabel

    private fun updateInstanceSelection() {
        val serverEnabled = serverInstance.isSelected
        hostname.isEnabled = serverEnabled

        val awsEnabled = awsInstance.isSelected
        awsApplication.isEnabled = awsEnabled
        awsProfile.isEnabled = awsEnabled
        awsRegion.isEnabled = awsEnabled
        awsInstanceName.isEnabled = awsEnabled

        databasePort.isEnabled = serverEnabled || awsEnabled
        username.isEnabled = serverEnabled || awsEnabled
        password.isEnabled = serverEnabled || awsEnabled
    }

    override val panel: JPanel = panel {
        row {
            label(PluginApiBundle.message("xquery.settings.dialog.query-processor.name.label"), column.vgap())
            description = textField(column.horizontal().hgap().vgap()) { minimumSize = Dimension(250, -1) }
        }
        row {
            label(PluginApiBundle.message("xquery.settings.dialog.query-processor.implementation.label"), column.vgap())
            api = comboBox(column.horizontal().hgap().vgap()) {
                renderer = coloredListCellRenderer { _, value, index, _, _ ->
                    icon = value?.presentation?.getIcon(false)
                    ipad = Insets.listCellRenderer(index)

                    value?.presentation?.presentableText?.let { append(it) }
                }

                QueryProcessorApi.apis.forEach { value -> addItem(value) }
                addActionListener(selectQueryProcessor)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.settings.dialog.query-processor.api-jar.label"), column.vgap())
            jar = textFieldWithBrowseButton(column.horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptor(true, false, false, true, false, false)
                    .withExtensionFilterEx("jar")
                descriptor.title = PluginApiBundle.message("browser.choose.implementation-api-jar")
                addBrowseFolderListenerEx(project, descriptor)
            }
        }
        row {
            label(PluginApiBundle.message("xquery.settings.dialog.query-processor.config-path.label"), column.vgap())
            configuration = textFieldWithBrowseButton(column.horizontal().hgap().vgap()) {
                val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
                descriptor.title = PluginApiBundle.message("browser.choose.configuration")
                addBrowseFolderListenerEx(project, descriptor)
            }
        }
        buttonGroup {
            // Standalone instance
            row {
                standaloneInstance = radio(column.vgap()) {
                    text = PluginApiBundle.message("xquery.settings.dialog.query-processor.instance.standalone.label")
                }
                standaloneInstance.addActionListener { updateInstanceSelection() }
            }
            // Server instance
            row {
                serverInstance = radio(column.vgap()) {
                    text = PluginApiBundle.message("xquery.settings.dialog.query-processor.instance.server.label")
                }
                serverInstance.addActionListener { updateInstanceSelection() }
            }
            row {
                label(
                    PluginApiBundle.message("xquery.settings.dialog.query-processor.hostname.label"),
                    column.surrogate().vgap()
                )
                hostname = textField(column.horizontal().hgap().vgap())
            }
            // AWS server instance
            row {
                awsInstance = radio(column.vgap()) {
                    text = PluginApiBundle.message("xquery.settings.dialog.query-processor.instance.aws.label")
                }
                awsInstance.addActionListener { updateInstanceSelection() }
            }
            row {
                label(
                    PluginApiBundle.message("xquery.settings.dialog.query-processor.aws.application.label"),
                    column.surrogate().vgap()
                )
                awsApplication = textFieldWithBrowseButton(column.horizontal().hgap().vgap()) {
                    val descriptor = object : FileChooserDescriptor(true, false, false, false, false, false) {
                        override fun isFileSelectable(file: VirtualFile?): Boolean {
                            return super.isFileSelectable(file) || isMacExecutable(file)
                        }

                        fun isMacExecutable(file: VirtualFile?): Boolean {
                            return SystemInfo.isMac && file?.isDirectory == true && "app" == file.extension
                        }
                    }
                    descriptor.title = PluginApiBundle.message("browser.choose.aws-application")
                    descriptor.withFileFilter { it.name == "aws" || it.name == "aws.exe" }
                    addBrowseFolderListenerEx(project, descriptor)
                }
            }
            row {
                label(
                    PluginApiBundle.message("xquery.settings.dialog.query-processor.aws.profile.label"),
                    column.surrogate().vgap()
                )
                awsProfile = textField(column.horizontal().hgap().vgap())
            }
            row {
                label(
                    PluginApiBundle.message("xquery.settings.dialog.query-processor.aws.region.label"),
                    column.surrogate().vgap()
                )
                awsRegion = textField(column.horizontal().hgap().vgap())
            }
            row {
                label(
                    PluginApiBundle.message("xquery.settings.dialog.query-processor.aws.instance-name.label"),
                    column.surrogate().vgap()
                )
                awsInstanceName = textField(column.horizontal().hgap().vgap())
            }
        }
        row {
            label(PluginApiBundle.message("xquery.settings.dialog.query-processor.database-port.label"), column.vgap())
            databasePort = textField(column.horizontal().hgap().vgap())
        }
        row {
            label(PluginApiBundle.message("xquery.settings.dialog.query-processor.username.label"), column.vgap())
            username = textField(column.horizontal().hgap().vgap())
        }
        row {
            label(PluginApiBundle.message("xquery.settings.dialog.query-processor.password.label"), column.vgap())
            password = passwordField(column.horizontal().hgap().vgap())
        }
        row {
            errorMessage = label(column.horizontal().spanCols()) { isVisible = false }
        }
    }

    override fun isModified(configuration: QueryProcessorSettings): Boolean = false

    override fun reset(configuration: QueryProcessorSettings) {
        description.text = configuration.name
        api.selectedItem = configuration.api
        jar.textField.text = configuration.jar
        this.configuration.textField.text = configuration.configurationPath

        when {
            configuration.connection != null -> {
                serverInstance.isSelected = true

                hostname.text = configuration.connection!!.hostname
                awsApplication.text = ""
                awsProfile.text = ""
                awsRegion.text = ""
                awsInstanceName.text = ""
                databasePort.text = configuration.connection!!.databasePort.toString()
                username.text = configuration.connection!!.username
                password.text = configuration.connection!!.password
            }
            configuration.awsConnection != null -> {
                awsInstance.isSelected = true

                hostname.text = ""
                awsApplication.text = configuration.awsConnection!!.application
                awsProfile.text = configuration.awsConnection!!.profile
                awsRegion.text = configuration.awsConnection!!.region
                awsInstanceName.text = configuration.awsConnection!!.instanceName
                databasePort.text = configuration.awsConnection!!.databasePort.toString()
                username.text = configuration.awsConnection!!.username
                password.text = configuration.awsConnection!!.password
            }
            else -> {
                standaloneInstance.isSelected = true

                hostname.text = ""
                awsApplication.text = ""
                awsProfile.text = ""
                awsRegion.text = ""
                awsInstanceName.text = ""
                databasePort.text = "0"
                username.text = ""
                password.text = ""
            }
        }

        updateInstanceSelection()
    }

    override fun apply(configuration: QueryProcessorSettings) {
        configuration.name = description.text.nullize()
        configuration.api = api.selectedItem as QueryProcessorApi
        configuration.jar = jar.textField.text.nullize()
        configuration.configurationPath = this.configuration.textField.text.nullize()
        when {
            serverInstance.isSelected -> {
                configuration.awsConnection = null
                configuration.connection = ConnectionSettings(
                    hostname.text,
                    databasePort.text.toInt(),
                    username.text.nullize()
                )
                configuration.connection!!.setPassword(password.password?.nullize())
            }
            awsInstance.isSelected -> {
                configuration.connection = null
                configuration.awsConnection = AWSConnectionSettings(
                    awsApplication.text,
                    awsProfile.text,
                    awsRegion.text,
                    awsInstanceName.text,
                    databasePort.text.toInt(),
                    username.text.nullize()
                )
                configuration.awsConnection!!.setPassword(password.password?.nullize())
            }
            else -> {
                configuration.awsConnection = null
                configuration.connection = null
            }
        }
    }

    // endregion
}
