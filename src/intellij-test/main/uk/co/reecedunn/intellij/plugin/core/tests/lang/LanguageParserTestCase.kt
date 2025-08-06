// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.PsiFileFactoryImpl
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.psi.MockPsiManager
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

interface LanguageParserTestCase<File : PsiFile> : LanguageTestCase, PlatformTestCase {
    fun registerPsiFileFactory() {
        val psiManager = MockPsiManager(project)
        project.registerService<PsiManager>(psiManager)
        project.registerService<PsiFileFactory>(PsiFileFactoryImpl(psiManager))
    }
}
