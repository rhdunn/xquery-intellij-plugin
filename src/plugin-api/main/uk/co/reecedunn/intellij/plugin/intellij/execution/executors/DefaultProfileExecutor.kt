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
package uk.co.reecedunn.intellij.plugin.intellij.execution.executors

import com.intellij.execution.Executor
import com.intellij.execution.ExecutorRegistry
import com.intellij.icons.AllIcons
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import javax.swing.Icon

class DefaultProfileExecutor : Executor() {
    companion object {
        @NonNls
        const val EXECUTOR_ID: String = "XIJPProfile"

        fun getInstance(): Executor = ExecutorRegistry.getInstance().getExecutorById(EXECUTOR_ID)
    }

    override fun getToolWindowId(): String = EXECUTOR_ID

    override fun getToolWindowIcon(): Icon = AllIcons.Actions.Profile

    override fun getIcon(): Icon = AllIcons.Actions.Profile

    override fun getDisabledIcon(): Icon = AllIcons.Actions.Profile

    override fun getActionName(): String = PluginApiBundle.message("executor.profile.toolwindow.name")

    override fun getId(): String = EXECUTOR_ID

    override fun getContextActionId(): String = "XIJPProfileClass"

    override fun getStartActionText(): String = PluginApiBundle.message("executor.profile.start.action")

    override fun getDescription(): String = PluginApiBundle.message("executor.profile.description")

    override fun getHelpId(): String? = null
}
