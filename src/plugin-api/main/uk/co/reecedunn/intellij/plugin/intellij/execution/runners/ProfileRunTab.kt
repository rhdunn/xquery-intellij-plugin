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
package uk.co.reecedunn.intellij.plugin.intellij.execution.runners

import com.intellij.execution.ExecutionResult
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.RunContentBuilder
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ContentProvider

class ProfileRunTab(executionResult: ExecutionResult, environment: ExecutionEnvironment) :
    RunContentBuilder(executionResult, environment) {

    private var runnerLayoutActions: DefaultActionGroup? = null

    var runContentDescriptor: RunContentDescriptor? = null
        private set
        get() {
            runnerLayoutActions?.let { myUi.options.setTopToolbar(it, ActionPlaces.RUNNER_TOOLBAR) }
            return field
        }

    fun addContentProvider(provider: ContentProvider, isActiveProvider: Boolean) {
        if (runnerLayoutActions == null) {
            runnerLayoutActions = DefaultActionGroup()
            runContentDescriptor = showRunContent(myEnvironment.contentToReuse)
        }

        val content = provider.getContent(myUi)
        myUi.contentManager.addContent(content)

        provider.attachToProcess(myRunContentDescriptor.processHandler)
        runnerLayoutActions?.addAll(*provider.createRunnerLayoutActions())

        if (isActiveProvider) {
            myUi.contentManager.setSelectedContent(content, true)
        }
    }
}
