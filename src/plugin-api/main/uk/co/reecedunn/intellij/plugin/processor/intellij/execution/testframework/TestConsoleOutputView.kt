/*
 * Copyright (C) 2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.intellij.execution.testframework

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project
import com.intellij.ui.content.Content
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ContentProvider
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat

class TestConsoleOutputView(project: Project, private val outputFormat: TestFormat) :
    TextConsoleView(project),
    ContentProvider {
    // region ContentProvider

    override val contentId: String = "TestConsoleOutput"

    override fun getContent(ui: RunnerLayoutUi): Content {
        val consoleTitle = outputFormat.name
        val content = ui.createContent(contentId, component, consoleTitle, AllIcons.Modules.Output, null)
        content.isCloseable = false
        return content
    }

    override fun createRunnerLayoutActions(): Array<AnAction> = arrayOf()

    override fun attachToProcess(processHandler: ProcessHandler) {
    }

    override fun attachToConsole(consoleView: ConsoleView) {
    }

    // endregion
}
