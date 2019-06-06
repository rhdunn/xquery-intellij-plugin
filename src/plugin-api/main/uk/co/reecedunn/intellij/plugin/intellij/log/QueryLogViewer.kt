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
import javax.swing.JPanel

class QueryLogViewer : ToolWindowFactory, DumbAware {
    private var logView: QueryLogViewerUI? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        if (logView == null) {
            logView = QueryLogViewerUI()
        }

        val content = ContentFactory.SERVICE.getInstance().createContent(logView?.panel, null, false)
        val contentManager = toolWindow.contentManager
        contentManager.removeAllContents(true)
        contentManager.addContent(content)
        contentManager.setSelectedContent(content)
        toolWindow.show(null)
    }
}

class QueryLogViewerUI {
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

    var panel: JPanel? = null

    private fun createUIComponents() {
        createQueryProcessorUI()
    }
}
