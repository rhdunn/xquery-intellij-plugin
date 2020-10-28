/*
 * Copyright (C) 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting

import com.intellij.application.options.editor.WebEditorOptions
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.xml.breadcrumbs.BreadcrumbsUtilEx
import com.intellij.xml.breadcrumbs.PsiFileBreadcrumbsCollector.getLinePsiElements
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.intellij.codeInsight.highlighting.XQueryElemTagRangesProvider.getElementTagRanges

class XQueryElemTagTreeHighlightingPass(val file: PsiFile, val editor: EditorEx) :
    TextEditorHighlightingPass(file.project, editor.document, true) {

    val provider = BreadcrumbsUtilEx.findProvider(false, file.manager.findViewProvider(file.virtualFile)!!)
    var tagsToHighlight: List<Pair<TextRange?, TextRange?>> = listOf()

    override fun doCollectInformation(progress: ProgressIndicator) {
        if (WebEditorOptions.getInstance().isTagTreeHighlightingEnabled) {
            val offset: Int = editor.caretModel.offset
            val elements = getLinePsiElements(document, offset, file.virtualFile, file.project, provider)
            tagsToHighlight = getElementRanges(elements)
        }
    }

    override fun doApplyInformationToEditor() {
    }

    private fun getElementRanges(elements: Array<out PsiElement?>?): List<Pair<TextRange?, TextRange?>> = when {
        elements.isNullOrEmpty() -> listOf()
        !isTagTreeHighlightingActive(elements.last()!!.containingFile) -> listOf()
        !containsTagsWithSameName(elements) -> listOf()
        else -> elements.reversed().mapNotNull {
            if (it is XQueryDirElemConstructor)
                getElementTagRanges(it)
            else
                null
        }
    }
}
