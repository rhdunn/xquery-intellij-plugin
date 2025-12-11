// Copyright (C) 2019, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.execution.ui

import com.intellij.compat.actionSystem.DataSink
import com.intellij.compat.actionSystem.UiDataProvider
import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

open class ConsoleViewImpl : JPanel(BorderLayout()), ConsoleView, UiDataProvider, DataProvider {
    private var helpId: String? = null

    // region ConsoleView

    override fun hasDeferredOutput(): Boolean = false

    override fun clear() {
    }

    override fun setHelpId(helpId: String) {
        this.helpId = helpId
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
    }

    override fun getContentSize(): Int = 0

    override fun setOutputPaused(value: Boolean) {
    }

    override fun createConsoleActions(): Array<AnAction> = AnAction.EMPTY_ARRAY

    override fun getComponent(): JComponent = this

    override fun performWhenNoDeferredOutput(runnable: Runnable) {
        runnable.run()
    }

    override fun attachToProcess(processHandler: ProcessHandler) {
    }

    override fun getPreferredFocusableComponent(): JComponent = this

    override fun isOutputPaused(): Boolean = false

    override fun addMessageFilter(filter: Filter) {
    }

    override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
        print(hyperlinkText, ConsoleViewContentType.NORMAL_OUTPUT)
    }

    override fun canPause(): Boolean = false

    override fun allowHeavyFilters() {
    }

    override fun dispose() {
    }

    override fun scrollTo(offset: Int) {
    }

    // endregion
    // region UiDataProvider

    override fun uiDataSnapshot(sink: DataSink) {
        sink[PlatformDataKeys.HELP_ID] = helpId
        sink[LangDataKeys.CONSOLE_VIEW] = this
    }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        PlatformDataKeys.HELP_ID.name -> helpId
        LangDataKeys.CONSOLE_VIEW.name -> this
        else -> null
    }

    // endregion
}
