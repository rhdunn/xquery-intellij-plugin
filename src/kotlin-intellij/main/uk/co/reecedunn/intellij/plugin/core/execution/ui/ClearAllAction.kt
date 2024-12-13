// Copyright (C) 2019-2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.execution.ui

import com.intellij.execution.ExecutionBundle
import com.intellij.execution.ui.ConsoleView
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction

class ClearAllAction internal constructor(private val consoleView: ConsoleView?) : DumbAwareAction(
    ExecutionBundle.message("clear.all.from.console.action.name"),
    ExecutionBundle.message("clear.all.from.console.action.description"),
    AllIcons.Actions.GC
) {
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    override fun update(e: AnActionEvent) {
        var enabled = consoleView != null && consoleView.contentSize > 0
        if (!enabled) {
            enabled = e.getData(LangDataKeys.CONSOLE_VIEW) != null
            val editor = e.getData(CommonDataKeys.EDITOR)
            if (editor != null && editor.document.textLength == 0) {
                enabled = false
            }
        }
        e.presentation.isEnabled = enabled
    }

    override fun actionPerformed(e: AnActionEvent) {
        val consoleView = consoleView ?: e.getData(LangDataKeys.CONSOLE_VIEW)
        consoleView?.clear()
    }
}
