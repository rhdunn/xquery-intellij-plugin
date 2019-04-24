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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewImpl
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultTime
import uk.co.reecedunn.intellij.plugin.intellij.execution.ui.results.QueryTextConsoleView
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.xpath.model.XsDurationValue
import java.awt.*
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class QueryConsoleView(val project: Project) : ConsoleViewImpl(), QueryResultListener {
    companion object {
        private const val SPLITTER_KEY = "XQueryIntelliJPlugin.QueryResultView.Splitter"
    }

    private var text: QueryTextConsoleView? = null

    private var summary: JLabel? = null
    private var table: QueryResultTable? = null

    private fun createTextConsoleView(): JComponent {
        text = QueryTextConsoleView(project)
        return text!!.component
    }

    private fun createResultTable(): JComponent {
        table = QueryResultTable(
            QueryResultItemTypeColumn(sortable = false),
            QueryResultMimeTypeColumn(sortable = false)
        )

        table!!.selectionModel.addListSelectionListener {
            table?.selectedObject?.second?.let { range -> text?.scrollToTop(range.from) }
        }

        return JBScrollPane(table)
    }

    private fun createResultPanel(): JComponent {
        summary = JLabel()

        val infoPanel = JPanel(VerticalLayout(0))
        summary!!.border = EmptyBorder(4, 4, 4, 4)
        infoPanel.add(summary)

        val panel = JPanel(GridBagLayout())
        val constraints = GridBagConstraints(
            0, 0, 1, 1, 1.0, 0.0,
            GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,
            Insets(0, 0, 0, 0),
            0, 0
        )
        panel.add(infoPanel, constraints)

        constraints.gridy = 1
        constraints.weighty = 1.0
        constraints.fill = GridBagConstraints.BOTH
        panel.add(createResultTable(), constraints)

        infoPanel.border = Borders.TableHeaderBottom
        return panel
    }

    // region ConsoleView

    override fun clear() {
        text?.clear()

        summary!!.text = "\u00A0"

        table?.removeAll()
        table?.isRunning = false
        table?.hasException = false
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
        this.text?.print(text, contentType)
    }

    override fun getContentSize(): Int = text?.contentSize ?: 0

    override fun attachToProcess(processHandler: ProcessHandler?) {
        (processHandler as? QueryProcessHandlerBase)?.addQueryResultListener(this, this)
    }

    override fun getComponent(): JComponent {
        if (table == null) {
            val splitPane = OnePixelSplitter(false)
            splitPane.firstComponent = createTextConsoleView()
            splitPane.secondComponent = createResultPanel()
            splitPane.secondComponent.minimumSize = Dimension(250, -1)
            splitPane.setHonorComponentsMinimumSize(true)
            splitPane.setAndLoadSplitterProportionKey(SPLITTER_KEY)
            add(splitPane, BorderLayout.CENTER)
        }
        return super.getComponent()
    }

    override fun scrollTo(offset: Int) {
        text?.scrollTo(offset)
    }

    // endregion
    // region QueryResultListener

    override fun onBeginResults() {
        clear()
        table?.isRunning = true
    }

    override fun onEndResults() {
        table?.isRunning = false
        if (table?.isEmpty == true) {
            print("()", ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

    override fun onQueryResult(result: QueryResult) {
        val from = contentSize
        when (result.type) {
            "binary()", "xs:hexBinary", "xs:base64Binary" -> {
                val length = (result.value as? String)?.length ?: 0
                print("Binary data ($length bytes)", ConsoleViewContentType.NORMAL_OUTPUT)
            }
            else -> print(result.value.toString(), ConsoleViewContentType.NORMAL_OUTPUT)
        }
        print("\n", ConsoleViewContentType.NORMAL_OUTPUT)

        table?.addRow(result, Range(from, contentSize))
    }

    override fun onException(e: Throwable) {
        print("${e.message ?: e.javaClass.name}\n", ConsoleViewContentType.ERROR_OUTPUT)
        if (e is QueryError) {
            e.value.withIndex().forEach {
                if (it.index == 0) {
                    print("  with ${it.value}\n", ConsoleViewContentType.ERROR_OUTPUT)
                } else {
                    print("   and ${it.value}\n", ConsoleViewContentType.ERROR_OUTPUT)
                }
            }
            e.frames.forEach {
                print(
                    "    at ${it.module ?: ""}:${it.lineNumber ?: 0}:${it.columnNumber ?: 0}\n",
                    ConsoleViewContentType.ERROR_OUTPUT
                )
            }
        } else {
            val writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            print(writer.buffer.toString(), ConsoleViewContentType.ERROR_OUTPUT)
        }

        table?.hasException = true
    }

    override fun onQueryResultTime(resultTime: QueryResultTime, time: XsDurationValue) {
        when (resultTime) {
            QueryResultTime.Elapsed -> {
                val count = table?.rowCount ?: 0
                summary!!.text = PluginApiBundle.message("console.summary.label", count, formatDuration(time))
            }
        }
    }

    // endregion
}
