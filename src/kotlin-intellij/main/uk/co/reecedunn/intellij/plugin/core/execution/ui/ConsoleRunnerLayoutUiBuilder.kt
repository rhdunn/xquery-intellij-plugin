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

    private val builders: ArrayList<ContentProviderBuilder> = ArrayList()

    fun contentProvider(provider: ContentProvider): ContentProviderBuilder {
        return ContentProviderBuilder(provider)
    }

    fun consoleView(): ConsoleView = this

    inner class ContentProviderBuilder(internal val provider: ContentProvider) {
        internal var active: Boolean = false

        fun active(): ContentProviderBuilder {
            active = true
            return this
        }

        fun add(): ConsoleRunnerLayoutUiBuilder {
            builders.add(this)
            return this@ConsoleRunnerLayoutUiBuilder
        }
    }

    // endregion
    // region ExecutionConsoleEx

    override fun buildUi(layoutUi: RunnerLayoutUi?) {
        RunContentBuilder.buildConsoleUiDefault(layoutUi!!, delegate)

        var actions: DefaultActionGroup? = null
        builders.forEach { builder ->
            val content = builder.provider.getContent(layoutUi)
            layoutUi.contentManager.addContent(content)

            val runnerActions = builder.provider.createRunnerLayoutActions()
            if (runnerActions.isNotEmpty()) {
                if (actions == null) {
                    actions = DefaultActionGroup()
                }
                actions!!.addAll(*builder.provider.createRunnerLayoutActions())
            }

            builder.provider.attachToConsole(delegate)
            if (builder.active) {
                layoutUi.contentManager.setSelectedContent(content)
            }
        }
        if (actions != null) {
            layoutUi.options.setTopToolbar(actions!!, ActionPlaces.RUNNER_TOOLBAR)
        }
    }

    // endregion
    // region ConsoleView

    override fun dispose() {
        builders.clear()
        super.dispose()
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
        super.attachToProcess(processHandler)
        builders.forEach { it.provider.attachToProcess(processHandler) }
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
