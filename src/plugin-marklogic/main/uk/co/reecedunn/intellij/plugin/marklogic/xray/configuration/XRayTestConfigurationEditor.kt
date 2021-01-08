/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.marklogic.xray.configuration

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorComboBox
import javax.swing.JComboBox
import javax.swing.JComponent

class XRayTestConfigurationEditor(private val project: Project) : SettingsEditor<XRayTestConfiguration>() {
    // region Form

    private lateinit var queryProcessor: QueryProcessorComboBox
    private lateinit var server: JComboBox<String>
    private lateinit var database: JComboBox<String>

    @Suppress("DuplicatedCode")
    private val panel = panel {
        row {
            label(PluginApiBundle.message("xquery.configurations.processor.query-processor.label"), column.vgap())
            queryProcessor = QueryProcessorComboBox(project)
            add(queryProcessor.component, column.horizontal().hgap().vgap())
            queryProcessor.addActionListener {
                populateDatabaseUI()
                populateServerUI()
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
            label(PluginApiBundle.message("xquery.configurations.processor.server.label"), column.vgap())
            server = comboBox(column.horizontal().hgap().vgap()) {
                isEditable = true
                addItem(null)
            }
        }
        row {
            spacer(column.vertical())
            spacer(column.horizontal())
        }
    }

    @Suppress("DuplicatedCode")
    private fun populateDatabaseUI() {
        val settings = queryProcessor.settings ?: return
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
                    val current = database.selectedItem
                    database.removeAllItems()
                    database.addItem(null)
                    database.selectedItem = current
                }
            }
        }
    }

    @Suppress("DuplicatedCode")
    private fun populateServerUI() {
        val settings = queryProcessor.settings ?: return
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
                    val current = server.selectedItem
                    server.removeAllItems()
                    server.addItem(null)
                    server.selectedItem = current
                }
            }
        }
    }

    // endregion
    // region SettingsEditor

    override fun createEditor(): JComponent = panel

    override fun resetEditorFrom(settings: XRayTestConfiguration) {
        queryProcessor.processorId = settings.processorId
        database.selectedItem = settings.database
        server.selectedItem = settings.server
    }

    override fun applyEditorTo(settings: XRayTestConfiguration) {
        settings.processorId = queryProcessor.processorId
        settings.database = database.selectedItem as? String
        settings.server = server.selectedItem as? String
    }

    // endregion
}
