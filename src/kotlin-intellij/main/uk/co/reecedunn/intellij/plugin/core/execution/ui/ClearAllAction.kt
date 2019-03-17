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
package uk.co.reecedunn.intellij.plugin.core.execution.ui

import com.intellij.execution.ExecutionBundle
import com.intellij.execution.ui.ConsoleView
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.project.DumbAwareAction

class ClearAllAction internal constructor(private val consoleView: ConsoleView?) : DumbAwareAction(
    ExecutionBundle.message("clear.all.from.console.action.name"),
    "Clear the contents of the console",
    AllIcons.Actions.GC
) {
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
