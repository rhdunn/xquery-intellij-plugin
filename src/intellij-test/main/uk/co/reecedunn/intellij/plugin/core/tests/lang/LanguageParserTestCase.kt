// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.DefaultASTFactory
import com.intellij.lang.DefaultASTFactoryImpl
import com.intellij.lang.PsiBuilderFactory
import com.intellij.lang.impl.PsiBuilderFactoryImpl
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.PsiFileFactoryImpl
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
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
    }
}

