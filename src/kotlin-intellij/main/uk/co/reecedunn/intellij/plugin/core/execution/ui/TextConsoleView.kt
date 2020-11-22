/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.panels.Wrapper
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import java.awt.BorderLayout
import javax.swing.border.Border
import kotlin.math.min

open class TextConsoleView(project: Project) : ConsoleViewImpl(project, true), ConsoleViewEx {
    // region ConsoleViewEx

    override val offset: Int
        get() = editor!!.caretModel.offset

    @Suppress("DuplicatedCode")
    override fun scrollToTop(offset: Int) {
        ApplicationManager.getApplication().invokeLater {
            val moveOffset = min(offset, contentSize)
            val scrolling = editor!!.scrollingModel
            val caret = editor!!.caretModel

            caret.moveToOffset(moveOffset)
            scrolling.scrollVertically(editor!!.visualPositionToXY(caret.visualPosition).y)
        }
    }

    override fun setConsoleBorder(border: Border) {
        editor?.setBorder(border)
    }

    @Suppress("DuplicatedCode")
    override fun createActionToolbar(place: String) {
        val actions = DefaultActionGroup()
        actions.addAll(*createConsoleActions())

        val toolbar = ActionManagerEx.getInstanceEx().createActionToolbar(place, actions, false)
        toolbar.setTargetComponent(this)

        // Setting a border on the toolbar removes the standard padding/spacing,
        // so set the border on a panel that wraps the toolbar element.
        val wrapper = Wrapper()
        wrapper.add(toolbar.component)
        wrapper.border = Borders.ConsoleToolbarRight
        add(wrapper, BorderLayout.LINE_START)
    }

    override fun setConsoleText(text: String) {
        clear()
        print(text)
    }

    // endregion
}
