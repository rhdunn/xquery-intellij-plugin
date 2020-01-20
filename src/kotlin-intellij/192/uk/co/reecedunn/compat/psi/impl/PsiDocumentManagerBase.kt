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
package uk.co.reecedunn.compat.psi.impl

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.TransactionId
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.DocumentCommitProcessor

private class DocumentCommitProcessorImpl : DocumentCommitProcessor {
    override fun commitAsynchronously(project: Project, document: Document, reason: Any, context: TransactionId?) {
    }

    override fun commitSynchronously(document: Document, project: Project, psiFile: PsiFile) {
    }
}

abstract class PsiDocumentManagerBase(project: Project) :
    com.intellij.psi.impl.PsiDocumentManagerBase(
        project,
        PsiManager.getInstance(project),
        ApplicationManager.getApplication().messageBus,
        DocumentCommitProcessorImpl()
    ) {

    abstract fun commitAllDocumentsUnderProgress(): Boolean
}
