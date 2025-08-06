// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.compat.openapi.vfs.encoding.EncodingManagerImpl
import com.intellij.lang.DefaultASTFactory
import com.intellij.lang.DefaultASTFactoryImpl
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.PsiFileFactoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.editor.MockEditorFactoryEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiDocumentManagerEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

interface LanguageParserTestCase<File : PsiFile> : LanguageTestCase, PlatformTestCase {
    fun registerPsiFileFactory() {
        val psiManager = MockPsiManager(project)
        project.registerService<PsiManager>(psiManager)
        project.registerService<PsiFileFactory>(PsiFileFactoryImpl(psiManager))

        project.registerService<PsiDocumentManager>(MockPsiDocumentManagerEx(project))

        val app = ApplicationManager.getApplication()
        app.registerService<DefaultASTFactory>(DefaultASTFactoryImpl())
        app.registerService<PsiBuilderFactory>(PsiBuilderFactoryImpl())

        app.registerService<EditorFactory>(MockEditorFactoryEx())
        app.registerService<EncodingManager>(EncodingManagerImpl(CoroutineScope(Dispatchers.IO)))
    }
}
