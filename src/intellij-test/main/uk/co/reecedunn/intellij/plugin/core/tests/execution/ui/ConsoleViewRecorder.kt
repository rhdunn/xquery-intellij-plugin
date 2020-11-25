/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.execution.ui

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import java.lang.UnsupportedOperationException
import javax.swing.JComponent

class ConsoleViewRecorder : ConsoleView {
    companion object {
        val HYPERLINK = ConsoleViewContentType("HYPERLINK", ConsoleViewContentType.NORMAL_OUTPUT_KEY)
    }

    var printed: MutableList<Pair<ConsoleViewContentType, String>> = mutableListOf()

    override fun addMessageFilter(filter: Filter) = throw UnsupportedOperationException()

    override fun allowHeavyFilters() = throw UnsupportedOperationException()

    override fun attachToProcess(processHandler: ProcessHandler) = throw UnsupportedOperationException()

    override fun canPause(): Boolean = throw UnsupportedOperationException()

    override fun clear() = throw UnsupportedOperationException()

    override fun createConsoleActions(): Array<AnAction> = throw UnsupportedOperationException()

    override fun dispose() = throw UnsupportedOperationException()

    override fun getComponent(): JComponent = throw UnsupportedOperationException()

    override fun getContentSize(): Int = throw UnsupportedOperationException()

    override fun getPreferredFocusableComponent(): JComponent = throw UnsupportedOperationException()

    override fun hasDeferredOutput(): Boolean = throw UnsupportedOperationException()

    override fun isOutputPaused(): Boolean = throw UnsupportedOperationException()

    override fun performWhenNoDeferredOutput(runnable: Runnable) = throw UnsupportedOperationException()

    override fun print(text: String, contentType: ConsoleViewContentType) {
        printed.add(contentType to text)
    }

    override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
        printed.add(HYPERLINK to hyperlinkText)
    }

    override fun scrollTo(offset: Int) = throw UnsupportedOperationException()

    override fun setHelpId(helpId: String) = throw UnsupportedOperationException()

    override fun setOutputPaused(value: Boolean) = throw UnsupportedOperationException()
}
