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
package uk.co.reecedunn.intellij.plugin.processor.intellij.log

import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import uk.co.reecedunn.intellij.plugin.core.async.executeOnPooledThread
import uk.co.reecedunn.intellij.plugin.core.async.invokeLater
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewEx
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.core.ui.layout.*
import uk.co.reecedunn.intellij.plugin.processor.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorSettingsModel
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.intellij.settings.QueryProcessorsListener
import uk.co.reecedunn.intellij.plugin.processor.log.LogLine
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.query.CachedQueryProcessorSettings
import uk.co.reecedunn.intellij.plugin.processor.query.addToModel
import java.awt.Dimension
import java.lang.UnsupportedOperationException
import javax.swing.JComboBox
import javax.swing.JPanel

class QueryLogViewerUI(val project: Project) : Disposable {
    companion object {
        private const val PROPERTY_LOG_FILE = "XQueryIntelliJPlugin.QueryLogViewer.LogFile"

        private var selectedLogFile: String?
            get() = PropertiesComponent.getInstance().getValue(PROPERTY_LOG_FILE)
            set(value) {
                PropertiesComponent.getInstance().setValue(PROPERTY_LOG_FILE, value)
            }
    }

    // region Filter :: Server

    private var queryProcessor: JComboBox<CachedQueryProcessorSettings>? = null

    // endregion
    // region Filter :: Log File

    private var logFile: JComboBox<String>? = null

    private fun populateLogFiles() {
        val settings = (queryProcessor?.selectedItem as? CachedQueryProcessorSettings?)?.settings
        executeOnPooledThread {
            try {
                val logViewProvider = (settings?.session as? LogViewProvider)
                val logs = logViewProvider?.logs()
                val defaultLog = logs?.let { logViewProvider.defaultLogFile(logs) }
                val selectedLog = selectedLogFile
                invokeLater(ModalityState.any()) {
                    updatingLogList = true

                    logFile?.removeAllItems()
                    logs?.forEach {
                        logFile?.addItem(it)
                    }

                    if (logs?.contains(selectedLog) == true) {
                        logFile?.selectedItem = selectedLog
                    } else if (defaultLog != null) {
                        logFile?.selectedItem = defaultLog
                    }

                    updatingLogList = false
                    populateLogFile(reloadLogFile = true)
                }
            } catch (e: Throwable) {
                invokeLater(ModalityState.any()) {
                    val newSettings = (queryProcessor?.selectedItem as? CachedQueryProcessorSettings?)?.settings
                    if (settings === newSettings) {
                        // Only clear the log file if the selected query processor hasn't changed.
                        logFile?.removeAllItems()
                    }
                }
            }
        }
    }

    // endregion
    // region Log View

    private var logConsole: ConsoleViewEx? = null
    private var lines: Int = -1
    private var updatingLogList: Boolean = false

    @Suppress("SameParameterValue")
    private fun populateLogFile(reloadLogFile: Boolean) {
        if (updatingLogList) return

        val settings = (queryProcessor?.selectedItem as? CachedQueryProcessorSettings?)?.settings
        val logFile = logFile?.selectedItem as? String
        executeOnPooledThread {
            try {
                val log = logFile?.let {
                    selectedLogFile = logFile
                    (settings?.session as? LogViewProvider)?.log(logFile)
                }
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
                                when (value) {
                                    is String -> logConsole?.print(value, ConsoleViewContentType.NORMAL_OUTPUT)
                                    is LogLine -> logConsole?.let { value.print(it) }
                                    else -> throw UnsupportedOperationException()
                                }
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

    val panel: JPanel = panel {
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

                        val queryProcessors = QueryProcessors.getInstance()
                        queryProcessors.processors.addToModel(model, serversOnly = true)
                        queryProcessors.addQueryProcessorsListener(model)
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
    // region Disposable

    override fun dispose() {
        queryProcessor?.model?.let {
            QueryProcessors.getInstance().removeQueryResultListener(it as QueryProcessorsListener)
        }

        logConsole?.let { Disposer.dispose(it) }
        logConsole = null
    }

    // endregion
}
