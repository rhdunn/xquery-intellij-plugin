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
package uk.co.reecedunn.intellij.plugin.intellij.execution.ui.results

import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.project.Project
import com.intellij.ui.components.panels.Wrapper
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import uk.co.reecedunn.intellij.plugin.intellij.resources.PluginApiBundle
import java.awt.BorderLayout
import javax.swing.JComponent

class QueryTextConsoleView(project: Project) : TextConsoleView(project) {
    // region ConsoleView

    override fun getComponent(): JComponent {
        val component = super.getComponent() // Ensure the text view is initialized.

        // Add the text console's action toolbar to the text console itself,
        // not the result view console. This ensures that the text view editor
        // does not grab the table's keyboard navigation events.

        val actions = DefaultActionGroup()
        actions.addAll(*createConsoleActions())

        val toolbar = ActionManagerEx.getInstanceEx().createActionToolbar(ActionPlaces.RUNNER_TOOLBAR, actions, false)
        toolbar.setTargetComponent(this)

        // Setting a border on the toolbar removes the standard padding/spacing,
        // so set the border on a panel that wraps the toolbar element.
        val wrapper = Wrapper()
        wrapper.add(toolbar.component)
        wrapper.border = Borders.ConsoleToolbarRight
        add(wrapper, BorderLayout.LINE_START)

        return component
    }

    // endregion
    // region ConsoleViewEx

    override val consoleTitle: String = PluginApiBundle.message("console.tab.results.label")

    // endregion
}
