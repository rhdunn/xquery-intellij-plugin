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

import com.intellij.CommonBundle
import com.intellij.compat.execution.ui.setTopLeftToolbar
import com.intellij.execution.console.ConsoleViewWrapperBase
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.execution.ui.layout.PlaceInGrid
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.ui.content.Content
import javax.swing.JComponent
import javax.swing.border.Border

class ConsoleRunnerLayoutUiBuilder(primary: ConsoleView) : ConsoleViewWrapperBase(primary), ConsoleViewEx {
    // region Builder

    private val providers: ArrayList<ContentProvider> = ArrayList()
    private var activeProvider: ContentProvider? = null
    private var layout: RunnerLayoutUi? = null

    fun contentProvider(provider: ContentProvider, active: Boolean = false): ConsoleRunnerLayoutUiBuilder {
        providers.add(provider)
        if (active) {
            activeProvider = provider
        }
        return this
    }

    fun asRunnerLayout(
        project: Project,
        runnerId: String,
        runnerTitle: String,
        sessionName: String = "default"
    ): ConsoleRunnerLayoutUiBuilder {
        layout = RunnerLayoutUi.Factory.getInstance(project).create(runnerId, runnerTitle, sessionName, this)
        buildUi(layout)
        return this
    }

    fun consoleView(): ConsoleView = this

    private fun buildConsoleUiDefault(ui: RunnerLayoutUi, console: ExecutionConsole): Content {
        val consoleContent = ui.createContent(
            CONSOLE_CONTENT_ID,
            console.component,
            CommonBundle.message("title.console"),
            AllIcons.Debugger.Console,
            console.preferredFocusableComponent
        )
        consoleContent.isCloseable = false
        ui.addContent(consoleContent, 0, PlaceInGrid.bottom, false)
        return consoleContent
    }

    // endregion
    // region ExecutionConsoleEx

    override fun buildUi(layoutUi: RunnerLayoutUi?) {
        if (layout == null) {
            RunContentBuilder.buildConsoleUiDefault(layoutUi!!, delegate)
        } else {
            // Don't add the console actions when using a custom runner layout,
            // as they will be added as part of the primary UI.
            buildConsoleUiDefault(layoutUi!!, delegate)
        }

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
        providers.forEach { provider -> (provider as? Disposable)?.let { Disposer.dispose(it) } }
        providers.clear()
        super.dispose()
    }

    override fun getComponent(): JComponent = layout?.component ?: super.getComponent()

    override fun attachToProcess(processHandler: ProcessHandler) {
        super.attachToProcess(processHandler)
        providers.forEach { it.attachToProcess(processHandler) }
    }

    // endregion
    // region ConsoleViewEx

    override val offset: Int
        get() = (delegate as? ConsoleViewEx)?.offset ?: 0

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
