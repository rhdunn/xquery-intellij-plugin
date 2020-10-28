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

import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.psi.PsiFile

class XQueryElemTagTreeHighlightingPass(val file: PsiFile, val editor: EditorEx) :
    TextEditorHighlightingPass(file.project, editor.document, true) {

    override fun doCollectInformation(progress: ProgressIndicator) {
    }

    override fun doApplyInformationToEditor() {
    }
}
