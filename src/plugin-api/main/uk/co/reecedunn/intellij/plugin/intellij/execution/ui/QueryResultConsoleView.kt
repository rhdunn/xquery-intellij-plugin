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

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.intellij.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.query.QueryError
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable

class QueryResultConsoleView : ConsoleView, QueryResultListener {
    // region UI

    private var panel: JPanel? = null
    private var results: JTable? = null

    private fun createUIComponents() {
        results = QueryResultTable()
    }

    // endregion
    // region ConsoleView

    override fun hasDeferredOutput(): Boolean = false

    override fun clear() {
        results!!.removeAll()
    }

    override fun setHelpId(helpId: String) {
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
    }

    override fun getContentSize(): Int = 0

    override fun setOutputPaused(value: Boolean) {
    }

    override fun createConsoleActions(): Array<AnAction> {
        return AnAction.EMPTY_ARRAY
    }

    override fun getComponent(): JComponent = panel!!

    override fun performWhenNoDeferredOutput(runnable: Runnable) {
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        (processHandler as? QueryProcessHandlerBase)?.addQueryResultListener(this, this)
    }

    override fun getPreferredFocusableComponent(): JComponent = component

    override fun isOutputPaused(): Boolean = false

    override fun addMessageFilter(filter: Filter) {
    }

    override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
    }

    override fun canPause(): Boolean = false

    override fun allowHeavyFilters() {
    }

    override fun dispose() {
    }

    override fun scrollTo(offset: Int) {
    }

    // endregion
    // region QueryResultListener

    override fun onBeginResults() {
        (results as QueryResultTable).let {
            it.removeAll()
            it.isRunning = true
        }
    }

    override fun onEndResults() {
        (results as QueryResultTable).isRunning = false
    }

    override fun onQueryResult(result: QueryResult) {
        (results as QueryResultTable).addRow(result)
    }

    override fun onException(e: Throwable) {
        val result =
            if (e is QueryError)
                QueryResult(0, e, "fn:error", "text/plain")
            else
                QueryResult(0, e, e.javaClass.name, "text/plain")
        onQueryResult(result)
    }

    // endregion
}
