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

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

open class TextConsoleView(val project: Project) : JPanel(BorderLayout()), ConsoleView, DataProvider {
    var editor: EditorEx? = null
        private set

    private var helpId: String? = null

    var emulateCarriageReturn: Boolean = false
        set(value) {
            field = value
            (editor?.document as? DocumentImpl)?.setAcceptSlashR(value)
        }

    // region ConsoleView

    override fun hasDeferredOutput(): Boolean = false

    override fun clear() {
        editor!!.document.setText("")
    }

    override fun setHelpId(helpId: String) {
        this.helpId = helpId
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
        val newText = StringUtil.convertLineSeparators(text, emulateCarriageReturn)

        val doc = editor!!.document
        doc.insertString(doc.textLength, newText)
    }

    override fun getContentSize(): Int = editor?.document?.textLength ?: 0

    override fun setOutputPaused(value: Boolean) {
    }

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
            editor = ConsoleViewUtil.setupConsoleEditor(project, true, false)
            editor!!.contextMenuGroupId = null // disabling default context menu
            (editor?.document as? DocumentImpl)?.setAcceptSlashR(emulateCarriageReturn)
            add(editor!!.component, BorderLayout.CENTER)
        }
        return this
    }

    override fun performWhenNoDeferredOutput(runnable: Runnable) {
    }

    override fun attachToProcess(processHandler: ProcessHandler?) {
    }

    override fun getPreferredFocusableComponent(): JComponent = this

    override fun isOutputPaused(): Boolean = false

    override fun addMessageFilter(filter: Filter) {
    }

    override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
    }

    override fun canPause(): Boolean = false

    override fun allowHeavyFilters() {
    }

    override fun dispose() {
    }

    override fun scrollTo(offset: Int) {
    }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? {
        return when (dataId) {
            PlatformDataKeys.HELP_ID.name -> helpId
            CommonDataKeys.EDITOR.name -> editor
            LangDataKeys.CONSOLE_VIEW.name -> this
            else -> null
        }
    }

    // endregion
}
