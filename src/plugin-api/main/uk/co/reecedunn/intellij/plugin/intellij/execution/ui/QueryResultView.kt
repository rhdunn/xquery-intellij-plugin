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
import com.intellij.ui.components.JBScrollPane
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import java.awt.BorderLayout
import java.io.PrintWriter
import java.io.StringWriter
import javax.swing.JComponent

class QueryResultView(project: Project) : TextConsoleView(project), QueryResultListener {
    private var table: QueryResultTable? = null

    // region ConsoleView

    override fun clear() {
        super.clear()
        table?.removeAll()
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        (processHandler as? QueryProcessHandlerBase)?.addQueryResultListener(this, this)
    }

    override fun getComponent(): JComponent {
        if (table == null) {
            table = QueryResultTable(
                QueryResultItemTypeColumn(sortable = false),
                QueryResultMimeTypeColumn(sortable = false)
            )
            add(JBScrollPane(table), BorderLayout.LINE_END)
        }
        return super.getComponent()
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
        when (result.type) {
            "binary()", "xs:hexBinary", "xs:base64Binary" -> {
                val length = (result.value as? String)?.length ?: 0
                print("Binary data ($length bytes)", ConsoleViewContentType.NORMAL_OUTPUT)
            }
            else -> print(result.value.toString(), ConsoleViewContentType.NORMAL_OUTPUT)
        }
        print("\n", ConsoleViewContentType.NORMAL_OUTPUT)

        table?.addRow(result)
    }

    override fun onException(e: Throwable) {
        print("${e.message!!}\n", ConsoleViewContentType.ERROR_OUTPUT)
        if (e is QueryError) {
            print(
                "    at ${e.module ?: ""}:${e.lineNumber ?: 0}:${e.columnNumber ?: 0}\n",
                ConsoleViewContentType.ERROR_OUTPUT
            )
        } else {
            val writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            print(writer.buffer.toString(), ConsoleViewContentType.ERROR_OUTPUT)
        }
    }

    // endregion
}
