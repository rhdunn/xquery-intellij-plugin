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
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl
import com.intellij.openapi.keymap.ex.KeymapManagerEx
import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.Wrapper
import com.intellij.util.Range
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ConsoleViewImpl
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import java.awt.BorderLayout
import java.awt.Dimension
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JComponent

class QueryResultView(val project: Project) : ConsoleViewImpl(), QueryResultListener {
    companion object {
        private const val SPLITTER_KEY = "XQueryIntelliJPlugin.QueryResultView.Splitter"
    }

    // The TextConsoleView editor provided by the DataProvider interface (needed
    // to support printing) causes the table to not get the arrow key navigation
    // events when used as the base for the QueryResultView console. As such, the
    // text console is created as a child of the result view.
    private var text: TextConsoleView? = null

    private var table: QueryResultTable? = null

    private fun createTextConsoleView() {
        text = TextConsoleView(project)
        text?.component // Ensure the text view is initialized.

        // Add the text console's action toolbar to the text console itself,
        // not the result view console. This ensures that the text view editor
        // does not grab the table's keyboard navigation events.

        val actions = DefaultActionGroup()
        actions.addAll(*text!!.createConsoleActions())

        val toolbar = ActionManagerEx.getInstanceEx().createActionToolbar(ActionPlaces.RUNNER_TOOLBAR, actions, false)
        toolbar.setTargetComponent(text!!)

        // Setting a border on the toolbar removes the standard padding/spacing,
        // so set the border on a panel that wraps the toolbar element.
        val wrapper = Wrapper()
        wrapper.add(toolbar.component)
        wrapper.border = Borders.ConsoleToolbarRight
        text?.add(wrapper, BorderLayout.LINE_START)
    }

    // region ConsoleView

    override fun clear() {
        text?.clear()

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
            createTextConsoleView()

            table = QueryResultTable(
                QueryResultItemTypeColumn(sortable = false),
                QueryResultMimeTypeColumn(sortable = false)
            )

            table!!.selectionModel.addListSelectionListener {
                table?.selectedObject?.second?.let { range -> text?.scrollToTop(range.from) }
            }

            val splitPane = OnePixelSplitter(false)
            splitPane.firstComponent = text?.component
            splitPane.secondComponent = JBScrollPane(table)
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
        print("${e.message!!}\n", ConsoleViewContentType.ERROR_OUTPUT)
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

    // endregion
}
