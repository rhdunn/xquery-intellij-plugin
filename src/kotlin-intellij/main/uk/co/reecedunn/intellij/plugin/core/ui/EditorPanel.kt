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
package uk.co.reecedunn.intellij.plugin.core.ui

import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.project.Project
import com.intellij.ui.components.panels.Wrapper
import java.awt.BorderLayout
import javax.swing.JPanel
import javax.swing.border.Border

class EditorPanel : JPanel(BorderLayout()) {
    var editor: EditorEx? = null

    fun setupConsoleEditor(project: Project, foldingOutlineShown: Boolean, lineMarkerAreaShown: Boolean) {
        editor = ConsoleViewUtil.setupConsoleEditor(project, foldingOutlineShown, lineMarkerAreaShown)
        editor?.contextMenuGroupId = null // disabling default context menu
        add(editor!!.component, BorderLayout.CENTER)
    }

    fun setEditorBorder(border: Border) {
        editor?.setBorder(border)
    }

    fun createActionToolbar(place: String, vararg actions: AnAction) {
        val actionGroup = DefaultActionGroup()
        actionGroup.addAll(*actions)

        val toolbar = ActionManagerEx.getInstanceEx().createActionToolbar(place, actionGroup, false)
        toolbar.setTargetComponent(this)

        // Setting a border on the toolbar removes the standard padding/spacing,
        // so set the border on a panel that wraps the toolbar element.
        val wrapper = Wrapper()
        wrapper.add(toolbar.component)
        wrapper.border = Borders.ConsoleToolbarRight
        add(wrapper, BorderLayout.LINE_START)
    }
}
