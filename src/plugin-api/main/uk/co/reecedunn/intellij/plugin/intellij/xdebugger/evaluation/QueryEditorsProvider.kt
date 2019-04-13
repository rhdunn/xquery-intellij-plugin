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
package uk.co.reecedunn.intellij.plugin.intellij.xdebugger.evaluation

import com.intellij.lang.Language
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.intellij.util.LocalTimeCounter
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.evaluation.EvaluationMode
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider

class QueryEditorsProvider(private val language: Language) : XDebuggerEditorsProvider() {
    override fun getFileType(): FileType = language.associatedFileType!!

    @Suppress("OverridingDeprecatedMember")
    override fun createDocument(
        project: Project,
        expression: String,
        sourcePosition: XSourcePosition?,
        mode: EvaluationMode
    ): Document {
        val psiFile = PsiFileFactory.getInstance(project).createFileFromText(
            "expression." + fileType.defaultExtension, fileType, expression,
            LocalTimeCounter.currentTime(), true
        )
        return PsiDocumentManager.getInstance(project).getDocument(psiFile)!!
    }
}
