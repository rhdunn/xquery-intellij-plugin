// Copyright (C) 2021, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.query.execution.ui

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.ui.AnActionButton
import com.intellij.ui.components.JBList
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.ui.layout.dialog
import uk.co.reecedunn.intellij.plugin.core.ui.layout.list
import uk.co.reecedunn.intellij.plugin.core.ui.layout.toolbarPanel
import uk.co.reecedunn.intellij.plugin.processor.query.CachedQueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryServer
import uk.co.reecedunn.intellij.plugin.processor.query.addToModel
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessorSettingsDialog
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessorSettingsModel
import uk.co.reecedunn.intellij.plugin.processor.query.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.resources.PluginApiBundle
import java.awt.Dimension
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JComboBox
import javax.swing.JComponent

class QueryProcessorComboBox(private val project: Project) {
    private lateinit var queryProcessor: ComponentWithBrowseButton<JComboBox<CachedQueryProcessorSettings>>
    private lateinit var model: QueryProcessorSettingsModel
    private lateinit var list: JBList<CachedQueryProcessorSettings>

    private fun addQueryProcessor(@Suppress("UNUSED_PARAMETER") button: AnActionButton) {
        val item = QueryProcessorSettings()
        val dialog = QueryProcessorSettingsDialog(project)
        if (dialog.create(item)) {
            QueryProcessors.getInstance().addProcessor(item)

            val settings = CachedQueryProcessorSettings(item, dialog.presentation)
            queryProcessor.childComponent.addItem(settings)
        }
    }

    private fun editQueryProcessor(@Suppress("UNUSED_PARAMETER") button: AnActionButton) {
        val index = list.selectedIndex
        val item = queryProcessor.childComponent.getItemAt(index)
        val dialog = QueryProcessorSettingsDialog(project)
        if (dialog.edit(item.settings)) {
            QueryProcessors.getInstance().setProcessor(index, item.settings)

            item.presentation = dialog.presentation
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

                    list = list(model) {
                        cellRenderer = QueryProcessorSettingsCellRenderer()
                        setEmptyText(PluginApiBundle.message("xquery.configurations.processor.manage-processors-empty"))
                        selectedIndex = queryProcessor.childComponent.selectedIndex
                    }
                }
            }
            if (dialog.showAndGet()) {
                val index = list.selectedIndex
                if (index > 0 && queryProcessor.childComponent.selectedIndex != index) {
                    queryProcessor.childComponent.selectedIndex = index
                }
            }
        }

    val component: JComponent
        get() {
            model = QueryProcessorSettingsModel()
            QueryProcessors.getInstance().processors.addToModel(model, selectedServer = -1)

            queryProcessor = ComponentWithBrowseButton(ComboBox(model), manageQueryProcessorsAction)
            queryProcessor.childComponent.renderer = QueryProcessorSettingsCellRenderer()
            return queryProcessor
        }

    fun addActionListener(listener: (ActionEvent) -> Unit) {
        queryProcessor.childComponent.addActionListener(listener)
    }

    val settings: QueryProcessorSettings?
        get() = (queryProcessor.childComponent.selectedItem as? CachedQueryProcessorSettings?)?.settings

    var processorId: Int?
        get() = settings?.id
        set(value) {
            queryProcessor.childComponent.let {
                var updatedSelection = false
                (0 until it.itemCount).forEach { i ->
                    if (it.getItemAt(i)?.settings?.id == value) {
                        it.selectedIndex = i
                        updatedSelection = true
                    }
                }
                if (!updatedSelection && it.itemCount > 0) {
                    // Ensure the action listeners get called.
                    it.selectedIndex = 0
                }
            }
        }

    private var servers: List<QueryServer>? = null

    fun servers(handler: (List<QueryServer>) -> Unit) = servers(false, handler)

    @Synchronized
    fun servers(refresh: Boolean, handler: (List<QueryServer>) -> Unit) {
        if (servers != null && !refresh) {
            handler(servers!!)
            return
        }

        val settings = settings ?: return
        executeOnPooledThread {
            servers = settings.session.servers
            try {
                invokeLater(ModalityState.any()) {
                    handler(servers!!)
                }
            } catch (e: Throwable) {
                handler(listOf())
            }
        }
    }
}
