// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.mock.MockFileDocumentManagerImpl
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.psi.PsiDocumentManager
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.editor.requiresEditorFactory
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresPsiFileGetChildren() {
    requiresFileTreeGetNode()
    requiresLazyParsableElementSetChildren()
}

private fun PlatformTestCase.requiresLazyParsableElementSetChildren() {
    app.registerService<ProgressManager>(ProgressManagerImpl())
}

private fun PlatformTestCase.requiresFileTreeGetNode() {
    app.registerService<FileDocumentManager>(
        MockFileDocumentManagerImpl(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY) {
            EditorFactory.getInstance().createDocument(it)
        }
    )
}

fun PlatformTestCase.requiresPsiFileGetDocument() {
    project.registerService<PsiDocumentManager>(MockPsiDocumentManagerEx(project))
}
