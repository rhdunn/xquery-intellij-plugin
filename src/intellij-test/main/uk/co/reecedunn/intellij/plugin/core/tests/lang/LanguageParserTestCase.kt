// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.compat.openapi.vfs.encoding.EncodingManagerImpl
import com.intellij.lang.DefaultASTFactory
import com.intellij.lang.DefaultASTFactoryImpl
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.mock.MockFileDocumentManagerImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.impl.source.tree.LeafPsiElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.editor.MockEditorFactoryEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiDocumentManagerEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile

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

    fun registerPsiTreeWalker() {
        val app = ApplicationManager.getApplication()
        app.registerService<ProgressManager>(ProgressManagerImpl())
        app.registerService<FileDocumentManager>(MockFileDocumentManagerImpl(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY) {
            EditorFactory.getInstance().createDocument(it)
        })
    }

    @Suppress("UNCHECKED_CAST")
    fun parseText(text: String): File = createVirtualFile("testcase.xqy", text).toPsiFile(project) as File

    fun completion(text: String, completionPoint: String = "completion-point"): PsiElement {
        return parse<LeafPsiElement>(text).find { it.text == completionPoint }!!
    }
}

inline fun <reified T> LanguageParserTestCase<*>.parse(xquery: String): List<T> {
    return parseText(xquery).walkTree().filterIsInstance<T>().toList()
}

inline fun <reified T> LanguageParserTestCase<*>.parse(vararg xquery: String): List<T> {
    return parseText(xquery.joinToString("\n")).walkTree().filterIsInstance<T>().toList()
}
