// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.ASTFactory
import com.intellij.lang.Language
import com.intellij.lang.LanguageASTFactory
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExplicitExtension

@TestOnly
fun ASTFactory.registerExtension(project: Project, language: Language) {
    project.registerExplicitExtension(LanguageASTFactory.INSTANCE, language, this)
}
