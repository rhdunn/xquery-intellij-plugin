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
package uk.co.reecedunn.intellij.plugin.core.execution.ui

import com.intellij.compat.execution.ui.setTopLeftToolbar
import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import javax.swing.border.Border

class ConsoleRunnerLayoutUiBuilder(primary: ConsoleView) : ConsoleViewWrapperBase(primary), ConsoleViewEx {
    // region Builder

    private val providers: ArrayList<ContentProvider> = ArrayList()
    private var activeProvider: ContentProvider? = null

    fun contentProvider(provider: ContentProvider, active: Boolean = false): ConsoleRunnerLayoutUiBuilder {
        providers.add(provider)
        if (active) {
            activeProvider = provider
        }
        return this
    }

    fun consoleView(): ConsoleView = this

    // endregion
    // region ExecutionConsoleEx

    override fun buildUi(layoutUi: RunnerLayoutUi?) {
        RunContentBuilder.buildConsoleUiDefault(layoutUi!!, delegate)

        var actions: DefaultActionGroup? = null
        providers.forEach { provider ->
            val content = provider.getContent(layoutUi)
            layoutUi.contentManager.addContent(content)

            val runnerActions = provider.createRunnerLayoutActions()
            if (runnerActions.isNotEmpty()) {
                if (actions == null) {
                    actions = DefaultActionGroup()
                }
                actions!!.addAll(*provider.createRunnerLayoutActions())
            }

            provider.attachToConsole(delegate)
            if (provider === activeProvider) {
                layoutUi.contentManager.setSelectedContent(content)
            }
        }
        if (actions != null) {
            layoutUi.setTopLeftToolbar(actions!!, ActionPlaces.RUNNER_TOOLBAR)
        }
    }

    // endregion
    // region ConsoleView

    override fun dispose() {
        providers.clear()
        super.dispose()
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        super.attachToProcess(processHandler)
        providers.forEach { it.attachToProcess(processHandler) }
    }

    // endregion
    // region ConsoleViewEx

    override val offset: Int get() = (delegate as? ConsoleViewEx)?.offset ?: 0

    override fun scrollToTop(offset: Int) {
        (delegate as? ConsoleViewEx)?.scrollToTop(offset)
    }

    override fun setConsoleBorder(border: Border) {
        (delegate as? ConsoleViewEx)?.setConsoleBorder(border)
    }

    override fun createActionToolbar(place: String) {
        (delegate as? ConsoleViewEx)?.createActionToolbar(place)
    }

    override fun setConsoleText(text: String) {
        (delegate as? ConsoleViewEx)?.setConsoleText(text)
    }

    // endregion
}
