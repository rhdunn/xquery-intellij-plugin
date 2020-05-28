/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.intellij.log

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewEx
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorSettingsModel
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettingsWithVersionCache
import uk.co.reecedunn.intellij.plugin.processor.query.addToModel
import java.awt.Dimension
import javax.swing.JComboBox

class QueryLogViewer : ToolWindowFactory, DumbAware {
    private var logView: QueryLogViewerUI? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (logView == null) {
            logView = QueryLogViewerUI(project)
        }

        val content = ContentFactory.SERVICE.getInstance().createContent(logView?.panel, null, false)
        val contentManager = toolWindow.contentManager
        contentManager.removeAllContents(true)
        contentManager.addContent(content)
        contentManager.setSelectedContent(content)
        toolWindow.show(null)
    }
}

class QueryLogViewerUI(val project: Project) {
    // region Filter :: Server

    private var queryProcessor: JComboBox<QueryProcessorSettingsWithVersionCache>? = null

    // endregion
    // region Filter :: Log File

    private var logFile: JComboBox<String>? = null

    private fun populateLogFiles() {
        val settings = (queryProcessor?.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings
        executeOnPooledThread {
            try {
                val logViewProvider = (settings?.session as? LogViewProvider)
                val logs = logViewProvider?.logs()
                val defaultLog = logs?.let { logViewProvider.defaultLogFile(logs) }
                invokeLater(ModalityState.any()) {
                    updatingLogList = true
                    logFile?.removeAllItems()
                    logs?.forEach {
                        logFile?.addItem(it)
                        if (it == defaultLog) {
                            logFile?.selectedItem = it
                        }
                    }
                    updatingLogList = false
                    populateLogFile(reloadLogFile = true)
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.any()) {
                    logFile?.removeAllItems()
                }
            }
        }
    }

    // endregion
    // region Log View

    private var logConsole: ConsoleViewEx? = null
    private var lines: Int = -1
    private var updatingLogList: Boolean = false

    private fun populateLogFile(reloadLogFile: Boolean) {
        if (updatingLogList) return

        val settings = (queryProcessor?.selectedItem as? QueryProcessorSettingsWithVersionCache?)?.settings
        val logFile = logFile?.selectedItem as? String
        executeOnPooledThread {
            try {
                val log = logFile?.let { (settings?.session as? LogViewProvider)?.log(logFile) }
                invokeLater(ModalityState.any()) {
                    if (log != null) {
                        val offset = logConsole!!.offset
                        val isAtEnd = offset == logConsole!!.contentSize

                        if (reloadLogFile) {
                            lines = -1
                            logConsole?.clear()
                        }

                        log.withIndex().forEach { (index, value) ->
                            if (index > lines) {
                                logConsole?.print(value, ConsoleViewContentType.NORMAL_OUTPUT)
                                logConsole?.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
                                lines = index
                            }
                        }

                        if (isAtEnd) {
                            logConsole?.scrollTo(logConsole!!.contentSize)
                        }
                    } else {
                        logConsole?.clear()
                    }
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.any()) {
                    logConsole?.clear()
                }
            }
        }
    }

    // endregion
    // region Form

    val panel = panel {
        row {
            panel(column.horizontal().padding(4, 2)) {
                row {
                    label(PluginApiBundle.message("logviewer.filter.query-processor"), column)
                    queryProcessor = comboBox(column.hgap(LayoutPosition.Both)) {
                        preferredSize = Dimension(200, preferredSize.height)

                        val model = QueryProcessorSettingsModel()
                        this.model = model

                        renderer = QueryProcessorSettingsCellRenderer()
                        addActionListener {
                            populateLogFiles()
                        }

                        QueryProcessors.getInstance().processors.addToModel(model, serversOnly = true)
                    }

                    label(PluginApiBundle.message("logviewer.filter.log-file"), column)
                    logFile = comboBox(column.hgap(LayoutPosition.Both)) {
                        preferredSize = Dimension(200, preferredSize.height)

                        addActionListener {
                            populateLogFile(reloadLogFile = true)
                        }
                    }

                    spacer(column.horizontal())
                }
            }
        }
        row {
            logConsole = textConsole(project, column.fill()) {
                setConsoleBorder(Borders.ConsoleToolbarTop)
                createActionToolbar(ActionPlaces.UNKNOWN)
            }
        }

        populateLogFiles()
    }

    // endregion
}
