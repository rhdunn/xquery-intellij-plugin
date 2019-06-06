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

import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.execution.impl.EditorHyperlinkSupport
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessorSettingsCellRenderer
import uk.co.reecedunn.intellij.plugin.intellij.settings.QueryProcessors
import uk.co.reecedunn.intellij.plugin.processor.log.LogViewProvider
import uk.co.reecedunn.intellij.plugin.processor.query.QueryProcessorSettings
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

    private var queryProcessor: JComboBox<QueryProcessorSettings>? = null

    private fun createQueryProcessorUI() {
        val model = DefaultComboBoxModel<QueryProcessorSettings>()
        QueryProcessors.getInstance().processors.forEach { processor ->
            processor.connection?.let { model.addElement(processor) }
        }

        queryProcessor = ComboBox(model)

        queryProcessor!!.renderer = QueryProcessorSettingsCellRenderer()
        queryProcessor!!.addActionListener {
            populateLogFiles()
        }
    }

    // endregion
    // region Filter :: Log File

    private var logFile: JComboBox<String>? = null

    private fun createLogFileUI() {
        logFile = ComboBox()
        logFile!!.addActionListener {
            populateLogFile()
        }
    }

    private fun populateLogFiles() {
        val session = (queryProcessor?.selectedItem as? QueryProcessorSettings?)?.session
        if (session is LogViewProvider) {
            session.logs().execute { logs ->
                logs.forEach { logFile?.addItem(it) }
            }.onException { logFile?.removeAllItems() }
        } else {
            logFile?.removeAllItems()
        }
    }

    // endregion
    // region Log View

    private var logViewEditor: EditorEx? = null

    private var logView: JComponent? = null

    private fun createConsoleEditor() {
        logViewEditor = ConsoleViewUtil.setupConsoleEditor(project, true, false)
        logViewEditor?.contextMenuGroupId = null // disabling default context menu
        logView = logViewEditor!!.component
    }

    private fun populateLogFile() {
        val session = (queryProcessor?.selectedItem as? QueryProcessorSettings?)?.session
        if (session is LogViewProvider) {
            val logFile = logFile?.selectedItem as? String
            if (logFile != null) {
                session.log(logFile).execute { log ->
                    logViewEditor!!.document.setText(log ?: "")
                }.onException { logViewEditor!!.document.setText("") }
            }
        } else {
            logViewEditor!!.document.setText("")
        }
    }

    // endregion

    var panel: JPanel? = null

    private fun createUIComponents() {
        createQueryProcessorUI()
        createLogFileUI()
        createConsoleEditor()
    }
}
