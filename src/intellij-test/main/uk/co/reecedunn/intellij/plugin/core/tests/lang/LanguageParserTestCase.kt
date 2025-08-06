// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

interface LanguageParserTestCase<File : PsiFile> : LanguageTestCase, PlatformTestCase {
}
