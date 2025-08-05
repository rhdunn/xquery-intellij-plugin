// Copyright (C) 2016-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
// Copyright 2000-2019 JetBrains s.r.o. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.compat.openapi.vfs.encoding.EncodingManagerImpl
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import com.intellij.lang.*
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.mock.MockFileDocumentManagerImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.ProgressManagerImpl
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.psi.*
import com.intellij.psi.impl.PsiCachedValuesFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.CachedValuesManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uk.co.reecedunn.intellij.plugin.core.tests.editor.EditorTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.editor.MockEditorFactoryEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiDocumentManagerEx
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.IdeaPlatformTestCase

// NOTE: The IntelliJ ParsingTextCase implementation does not make it easy to
// customise the mock implementation, making it difficult to implement some tests.
@Suppress("SameParameterValue", "ReplaceNotNullAssertionWithElvisReturn")
abstract class ParsingTestCase<File : PsiFile>(
    override val language: Language
) : IdeaPlatformTestCase(), LanguageParserTestCase<File>, EditorTestCase {
    override fun registerServicesAndExtensions() {
        Registry.markAsLoaded()

        // IntelliJ ParsingTestCase setUp
        val app = ApplicationManager.getApplication()
        app.registerService<ProgressManager>(ProgressManagerImpl())

        val psiManager = MockPsiManager(project)
        app.registerService(app.messageBus)
        val editorFactory = MockEditorFactoryEx()
        app.registerService<EditorFactory>(editorFactory)
        app.registerService<EncodingManager>(EncodingManagerImpl(CoroutineScope(Dispatchers.IO)))
        app.registerService<FileDocumentManager>(MockFileDocumentManagerImpl(FileDocumentManagerImpl.HARD_REF_TO_DOCUMENT_KEY) {
            editorFactory.createDocument(it)
        })

        app.registerService<PsiBuilderFactory>(PsiBuilderFactoryImpl())
        app.registerService<DefaultASTFactory>(DefaultASTFactoryImpl())

        project.registerService<PsiManager>(psiManager)
        project.registerService<CachedValuesManager>(CachedValuesManagerImpl(project, PsiCachedValuesFactory(project)))
        project.registerService<PsiDocumentManager>(MockPsiDocumentManagerEx(project))
        project.registerService<PsiFileFactory>(PsiFileFactoryImpl(psiManager))
    }
}
