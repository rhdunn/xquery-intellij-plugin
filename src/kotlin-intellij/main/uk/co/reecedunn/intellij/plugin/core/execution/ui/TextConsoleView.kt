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

import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import java.awt.BorderLayout
import javax.swing.JComponent

abstract class TextConsoleView(val project: Project) : ConsoleViewImpl(), ConsoleViewEx {
    var editor: EditorEx? = null
        private set

    var emulateCarriageReturn: Boolean = false
        set(value) {
            field = value
            (editor?.document as? DocumentImpl)?.setAcceptSlashR(value)
        }

    private fun createConsoleEditor(): JComponent {
        editor = ConsoleViewUtil.setupConsoleEditor(project, true, false)
        editor?.contextMenuGroupId = null // disabling default context menu
        (editor?.document as? DocumentImpl)?.setAcceptSlashR(emulateCarriageReturn)
        return editor!!.component
    }

    // region ConsoleViewEx

    override fun scrollToTop(offset: Int) {
        ApplicationManager.getApplication().invokeLater {
            val moveOffset = Math.min(offset, contentSize)
            val scrolling = editor!!.scrollingModel
            val caret = editor!!.caretModel

            caret.moveToOffset(moveOffset)
            scrolling.scrollVertically(editor!!.visualPositionToXY(caret.visualPosition).y)
        }
    }

    // endregion
    // region ConsoleView

    override fun clear() {
        editor!!.document.setText("")
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
        val newText = StringUtil.convertLineSeparators(text, emulateCarriageReturn)

        val doc = editor!!.document
        doc.insertString(doc.textLength, newText)
    }

    override fun getContentSize(): Int = editor?.document?.textLength ?: 0

    override fun createConsoleActions(): Array<AnAction> {
        return arrayOf(
            object : ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE) {
                override fun getEditor(e: AnActionEvent): Editor? = editor
            },
            ScrollToTheEndToolbarAction(editor!!),
            ActionManager.getInstance().getAction("Print"),
            ClearAllAction(this)
        )
    }

    override fun getComponent(): JComponent {
        if (editor == null) {
            add(createConsoleEditor(), BorderLayout.CENTER)
        }
        return this
    }

    override fun scrollTo(offset: Int) {
        ApplicationManager.getApplication().invokeLater {
            val moveOffset = Math.min(offset, contentSize)
            editor!!.caretModel.moveToOffset(moveOffset)
            editor!!.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? {
        return when (dataId) {
            CommonDataKeys.EDITOR.name -> editor
            else -> super.getData(dataId)
        }
    }

    // endregion
}
