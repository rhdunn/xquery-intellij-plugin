/*
 * Copyright (C) 2017, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import uk.co.reecedunn.compat.psi.impl.PsiDocumentManagerBase

// NOTE: PSI document modifications rely on PsiDocumentManagerBase, which
// MockPsiDocumentManager does not derive from.
class MockPsiDocumentManagerEx(project: Project) : PsiDocumentManagerBase(project) {

    override fun getPsiFile(document: Document): PsiFile? = TODO()

    override fun getCachedPsiFile(document: Document): PsiFile? = TODO()

    override fun getDocument(file: PsiFile): Document? {
        val vFile = file.viewProvider.virtualFile
        return FileDocumentManager.getInstance().getDocument(vFile)
    }

    override fun getCachedDocument(file: PsiFile): Document? {
        val vFile = file.viewProvider.virtualFile
        return FileDocumentManager.getInstance().getCachedDocument(vFile)
    }

    override fun commitAllDocuments() {}

    override fun commitAllDocumentsUnderProgress(): Boolean = true

    override fun performForCommittedDocument(document: Document, action: Runnable) = action.run()

    override fun commitDocument(document: Document) {}

    override fun getLastCommittedText(document: Document): CharSequence = document.immutableCharSequence

    override fun getLastCommittedStamp(document: Document): Long = document.modificationStamp

    override fun getLastCommittedDocument(file: PsiFile): Document? = null

    override fun getUncommittedDocuments(): Array<Document?> = TODO()

    override fun isUncommited(document: Document): Boolean = TODO()

    override fun isCommitted(document: Document): Boolean = TODO()

    override fun hasUncommitedDocuments(): Boolean = TODO()

    override fun commitAndRunReadAction(runnable: Runnable) = TODO()

    override fun <T> commitAndRunReadAction(computation: Computable<T>): T = TODO()

    override fun addListener(listener: Listener) = TODO()

    override fun removeListener(listener: Listener) = TODO()

    override fun isDocumentBlockedByPsi(doc: Document): Boolean = TODO()

    override fun doPostponedOperationsAndUnblockDocument(doc: Document) = TODO()

    override fun performWhenAllCommitted(action: Runnable): Boolean = TODO()

    override fun reparseFiles(files: Collection<VirtualFile?>, includeOpenFiles: Boolean) = TODO()

    override fun performLaterWhenAllCommitted(runnable: Runnable) = TODO()

    override fun performLaterWhenAllCommitted(runnable: Runnable, modalityState: ModalityState?) = TODO()
}
