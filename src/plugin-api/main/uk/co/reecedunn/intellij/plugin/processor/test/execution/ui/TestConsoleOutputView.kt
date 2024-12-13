// Copyright (C) 2021, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.processor.test.execution.ui

import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.content.Content
import uk.co.reecedunn.intellij.plugin.core.execution.ui.ContentProvider
import uk.co.reecedunn.intellij.plugin.core.execution.ui.TextConsoleView
import uk.co.reecedunn.intellij.plugin.processor.query.QueryResult
import uk.co.reecedunn.intellij.plugin.processor.run.execution.process.QueryProcessHandlerBase
import uk.co.reecedunn.intellij.plugin.processor.run.execution.process.QueryResultListener
import uk.co.reecedunn.intellij.plugin.processor.test.TestFormat
import uk.co.reecedunn.intellij.plugin.xdm.types.XsDurationValue

class TestConsoleOutputView(project: Project, private val outputFormat: TestFormat) :
    TextConsoleView(project),
    ContentProvider,
    QueryResultListener {
    // region ContentProvider

    private var queryProcessHandler: QueryProcessHandlerBase? = null

    override val contentId: String = "TestConsoleOutput"

    override fun getContent(ui: RunnerLayoutUi): Content {
        val consoleTitle = outputFormat.name
        val content = ui.createContent(contentId, component, consoleTitle, AllIcons.Nodes.Console, null)
        content.isCloseable = false
        return content
    }

    override fun createRunnerLayoutActions(): Array<AnAction> = arrayOf()

    override fun attachToProcess(processHandler: ProcessHandler) {
        queryProcessHandler = (processHandler as? QueryProcessHandlerBase)
        queryProcessHandler?.addQueryResultListener(this)
    }

    override fun attachToConsole(consoleView: ConsoleView) {
    }

    // endregion
    // region Disposable

    override fun dispose() {
        queryProcessHandler?.removeQueryResultListener(this)
        super.dispose()
    }

    // endregion
    // region QueryResultListener

    private var psiFile: PsiFile? = null

    override fun onBeginResults() {
        psiFile = null
        clear()
    }

    override fun onEndResults(handler: (PsiFile) -> Unit) {
        val doc = editor?.document ?: return
        val language = outputFormat.language

        language.associatedFileType!!.let {
            val provider = FileTypeEditorHighlighterProviders.INSTANCE.forFileType(it)
            editor!!.highlighter = provider.getEditorHighlighter(project, it, null, editor!!.colorsScheme)
        }

        performWhenNoDeferredOutput {
            PsiFileFactory.getInstance(project).createFileFromText(language, doc.text)?.let { psiFile ->
                psiFile.viewProvider.virtualFile.putUserData(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY, doc)
                handler(psiFile)
            }
        }
    }

    override fun onQueryResult(result: QueryResult) {
        print(result.value as String, ConsoleViewContentType.NORMAL_OUTPUT)
    }

    override fun onException(e: Throwable) {
    }

    override fun onQueryElapsedTime(time: XsDurationValue) {
    }

    override fun onQueryResultsPsiFile(psiFile: PsiFile) {
    }

    // endregion
}
