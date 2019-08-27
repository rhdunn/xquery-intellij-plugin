/*
 * Copyright (C) 2019 Reece H. Dunn
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
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewEx
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsModel
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettingsWithVersionCache
import uk.co.reecedunn.intellij.plugin.processor.query.addToModel
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel

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

    private fun createQueryProcessorUI() {
        val model = QueryProcessorSettingsModel()
        queryProcessor = ComboBox(model)

        queryProcessor!!.renderer = QueryProcessorSettingsCellRenderer()
        queryProcessor!!.addActionListener {
            populateLogFiles()
        }

        QueryProcessors.getInstance().processors.addToModel(model, serversOnly = true)
    }

    // endregion
    // region Filter :: Log File

    private var logFile: JComboBox<String>? = null

    private fun createLogFileUI() {
        logFile = ComboBox()
        logFile!!.addActionListener {
            lines = -1
            populateLogFile()
        }
    }

    private fun populateLogFiles() {
        val settings = queryProcessor?.selectedItem as? QueryProcessorSettings?
        executeOnPooledThread {
            try {
                val logs = (settings?.session as? LogViewProvider)?.logs()
                invokeLater(ModalityState.any()) {
                    logFile?.removeAllItems()
                    logs?.forEach { logFile?.addItem(it) }
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
    private var logView: JComponent? = null
    private var lines: Int = -1

    private fun createConsoleEditor() {
        logConsole = TextConsoleView(project)
        logView = logConsole?.component

        logConsole?.setConsoleBorder(Borders.ConsoleToolbarTop)
        logConsole?.createActionToolbar(ActionPlaces.UNKNOWN)
    }

    private fun populateLogFile() {
        val settings = queryProcessor?.selectedItem as? QueryProcessorSettings?
        val logFile = logFile?.selectedItem as? String
        executeOnPooledThread {
            try {
                val log = logFile?.let { (settings?.session as? LogViewProvider)?.log(logFile) }
                invokeLater(ModalityState.any()) {
                    if (log != null) {
                        val offset = logConsole!!.offset
                        val isAtEnd = offset == logConsole!!.contentSize

                        if (lines == -1) {
                            logConsole?.clear()
                        }
                        log.withIndex().forEach { line ->
                            if (line.index > lines) {
                                logConsole?.print(line.value, ConsoleViewContentType.NORMAL_OUTPUT)
                                logConsole?.print("\n", ConsoleViewContentType.NORMAL_OUTPUT)
                                lines = line.index
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

    var panel: JPanel? = null

    private fun createUIComponents() {
        createQueryProcessorUI()
        createLogFileUI()
        createConsoleEditor()
    }

    // endregion
}
