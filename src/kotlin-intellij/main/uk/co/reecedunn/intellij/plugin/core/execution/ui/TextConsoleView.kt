/*
 * Copyright (C) 2019-2021 Reece H. Dunn
 * Copyright 2000-2020 JetBrains s.r.o.
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

import com.intellij.compat.execution.ui.addRangeHighlighterAndChangeAttributes
import com.intellij.execution.filters.FileHyperlinkInfo
import com.intellij.execution.filters.HyperlinkInfo
import com.intellij.execution.impl.ConsoleViewUtil
import com.intellij.execution.impl.EditorHyperlinkSupport
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.MarkupModelEx
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.editor.impl.DocumentMarkupModel
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.components.panels.Wrapper
import com.intellij.util.Alarm
import com.intellij.util.SystemProperties
import com.intellij.util.ui.UIUtil
import uk.co.reecedunn.intellij.plugin.core.execution.ui.impl.ConsoleViewToken
import uk.co.reecedunn.intellij.plugin.core.execution.ui.impl.ConsoleViewTokenBuffer
import uk.co.reecedunn.intellij.plugin.core.ui.Borders
import java.awt.BorderLayout
import java.util.concurrent.atomic.AtomicBoolean
import javax.swing.JComponent
import javax.swing.border.Border
import kotlin.math.min

// The IntelliJ ConsoleViewImpl should be used when the following issues are fixed:
// 1. IDEA-256363 : Support custom TokenBuffer sizes for ConsoleViewImpl instances.
// 2. IDEA-256612 : ConsoleViewImpl overrides syntax highlighting on the output text.
open class TextConsoleView(val project: Project) : ConsoleViewImpl(), ConsoleViewEx {
    companion object {
        private val CONTENT_TYPE = Key.create<ConsoleViewContentType>("ConsoleViewContentType")

        private val FLUSH_DELAY = SystemProperties.getIntProperty("console.flush.delay.ms", 200).toLong()
        private const val FLUSH_IMMEDIATELY = 0.toLong()
    }

    init {
        @Suppress("LeakingThis")
        ApplicationManager.getApplication().messageBus.connect(this).subscribe(
            EditorColorsManager.TOPIC,
            EditorColorsListener {
                ApplicationManager.getApplication().assertIsDispatchThread()
                if (project.isDisposed || editor == null) return@EditorColorsListener

                val model = DocumentMarkupModel.forDocument(editor!!.document, project, false)
                model.allHighlighters.asSequence().filterIsInstance<RangeHighlighterEx>().forEach { tokenMarker ->
                    val contentType = tokenMarker.getUserData(CONTENT_TYPE)
                    if (contentType != null && contentType.attributesKey == null) {
                        tokenMarker.setTextAttributes(contentType.attributes)
                    }
                }
            }
        )
    }

    var editor: EditorEx? = null
        private set

    private var hyperlinks: EditorHyperlinkSupport? = null

    private var emulateCarriageReturn: Boolean = false
        set(value) {
            field = value
            (editor?.document as? DocumentImpl)?.setAcceptSlashR(value)
        }

    @Suppress("LeakingThis")
    private val alarm = Alarm(this)

    private val lock = Any()
    private val tokens = ConsoleViewTokenBuffer()

    private fun createConsoleEditor(): JComponent {
        if (editor != null) return editor!!.component

        editor = ConsoleViewUtil.setupConsoleEditor(project, true, false)
        editor?.contextMenuGroupId = null // disabling default context menu
        (editor?.document as? DocumentImpl)?.setAcceptSlashR(emulateCarriageReturn)

        hyperlinks = EditorHyperlinkSupport(editor!!, project)
        return editor!!.component
    }

    private fun flushDeferredText() {
        lateinit var deferred: Array<ConsoleViewToken>
        synchronized(lock) {
            if (tokens.isEmpty()) return
            try {
                deferred = tokens.toTypedArray()
            } finally {
                tokens.clear()
            }
        }

        val document = editor!!.document
        var startLength = document.textLength
        document.insertString(startLength, deferred.joinToString("") { it.text })

        var contentTypeStart = startLength
        var previousContentType: ConsoleViewContentType? = null
        deferred.forEach {
            val endLength = startLength + it.text.length

            when {
                it.contentType == null || previousContentType === it.contentType -> {
                }
                previousContentType == null -> {
                    previousContentType = it.contentType
                }
                else -> {
                    createTokenRangeHighlighter(previousContentType!!, contentTypeStart, startLength)
                    contentTypeStart = startLength
                    previousContentType = it.contentType
                }
            }

            if (it.hyperlinkInfo != null) {
                hyperlinks!!.createHyperlink(startLength, endLength, null, it.hyperlinkInfo)
            }

            startLength = endLength
        }

        if (previousContentType != null) {
            createTokenRangeHighlighter(previousContentType!!, contentTypeStart, document.textLength)
        }
    }

    private fun createTokenRangeHighlighter(contentType: ConsoleViewContentType, startOffset: Int, endOffset: Int) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        val model = DocumentMarkupModel.forDocument(editor!!.document, project, true) as MarkupModelEx
        val layer = HighlighterLayer.SYNTAX - 1
        contentType.addRangeHighlighterAndChangeAttributes(
            model, startOffset, endOffset, layer, HighlighterTargetArea.EXACT_RANGE, false, CONTENT_TYPE
        )
    }

    private val flush = object : Runnable {
        private val requested = AtomicBoolean()

        fun queue(delay: Long) {
            if (alarm.isDisposed) return
            if (delay == FLUSH_IMMEDIATELY || requested.compareAndSet(false, true)) {
                alarm.addRequest(this, delay, null)
            }
        }

        override fun run() {
            requested.set(false)
            flushDeferredText()
        }
    }

    // region ConsoleViewEx

    override val offset: Int
        get() = editor!!.caretModel.offset

    override fun scrollToTop(offset: Int) {
        ApplicationManager.getApplication().invokeLater {
            flush.queue(FLUSH_IMMEDIATELY)

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
        val newText = StringUtil.convertLineSeparators(text, emulateCarriageReturn)
        editor!!.document.setText(newText)
        synchronized(lock) {
            tokens.clear()
        }
    }

    // endregion
    // region ConsoleView

    override fun hasDeferredOutput(): Boolean = synchronized(lock) {
        return tokens.isNotEmpty()
    }

    override fun performWhenNoDeferredOutput(runnable: Runnable) {
        flushDeferredText()
        runnable.run()
    }

    override fun clear() {
        editor!!.document.setText("")
        synchronized(lock) {
            tokens.clear()
        }
    }

    override fun print(text: String, contentType: ConsoleViewContentType) {
        val newText = StringUtil.convertLineSeparators(text, emulateCarriageReturn)
        synchronized(lock) {
            tokens.add(ConsoleViewToken(newText, contentType))
        }
        flush.queue(FLUSH_DELAY)
    }

    override fun printHyperlink(hyperlinkText: String, info: HyperlinkInfo?) {
        val newText = StringUtil.convertLineSeparators(hyperlinkText, emulateCarriageReturn)
        synchronized(lock) {
            tokens.add(ConsoleViewToken(newText, info))
        }
        flush.queue(FLUSH_DELAY)
    }

    override fun getContentSize(): Int {
        val document = editor?.document ?: return 0
        return document.textLength + synchronized(lock) { tokens.length }
    }

    override fun createConsoleActions(): Array<AnAction> {
        val actions = arrayOf(
            object : ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE) {
                override fun getEditor(e: AnActionEvent): Editor? = editor
            },
            ScrollToTheEndToolbarAction(editor!!),
            ActionManager.getInstance().getAction("Print"),
            ClearAllAction(this)
        )

        val additional = createAdditionalConsoleActions()
        if (additional.isNotEmpty()) {
            val primary = DefaultActionGroup()
            primary.addAll(*actions)

            val secondary = DefaultActionGroup()
            secondary.addAll(*additional)

            return arrayOf(primary, Separator(), secondary)
        }
        return actions
    }

    open fun createAdditionalConsoleActions(): Array<AnAction> = arrayOf()

    override fun getComponent(): JComponent {
        if (editor == null) {
            add(createConsoleEditor(), BorderLayout.CENTER)
        }
        return this
    }

    override fun scrollTo(offset: Int) {
        ApplicationManager.getApplication().invokeLater {
            flush.queue(FLUSH_IMMEDIATELY)

            val moveOffset = min(offset, contentSize)
            editor!!.caretModel.moveToOffset(moveOffset)
            editor!!.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
    }

    override fun dispose() {
        hyperlinks = null
        if (editor != null) {
            UIUtil.invokeAndWaitIfNeeded(Runnable {
                if (!editor!!.isDisposed) {
                    EditorFactory.getInstance().releaseEditor(editor!!)
                }
            })
            synchronized(lock) {
                tokens.clear()
            }
            editor = null
        }
    }

    // endregion
    // region DataProvider

    override fun getData(dataId: String): Any? = when (dataId) {
        CommonDataKeys.EDITOR.name -> editor
        CommonDataKeys.NAVIGATABLE.name -> {
            val pos = editor!!.caretModel.logicalPosition
            val info = hyperlinks!!.getHyperlinkInfoByLineAndCol(pos.line, pos.column)
            val openFileDescriptor = (info as? FileHyperlinkInfo)?.descriptor
            if (openFileDescriptor?.file?.isValid == true)
                openFileDescriptor
            else
                null
        }
        else -> super.getData(dataId)
    }

    // endregion
}
