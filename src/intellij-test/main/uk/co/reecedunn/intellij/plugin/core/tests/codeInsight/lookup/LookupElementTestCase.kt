// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.codeInsight.lookup

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.impl.CoreCommandProcessor
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase

interface LookupElementTestCase<File : PsiFile> : LanguageTestCase {
    fun registerDocumentEditing() {
        val app = ApplicationManager.getApplication()
        app.registerService<CommandProcessor>(CoreCommandProcessor())
    }
}
