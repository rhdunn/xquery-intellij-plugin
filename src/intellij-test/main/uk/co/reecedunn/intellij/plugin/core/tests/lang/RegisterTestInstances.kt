// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.lang

import com.intellij.lang.ASTFactory
import com.intellij.lang.Language
import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.ParserDefinition
import com.intellij.mock.MockFileTypeManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.TestOnly
import uk.co.reecedunn.intellij.plugin.core.extensions.registerExplicitExtension
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService

@TestOnly
fun ASTFactory.registerExtension(project: Project, language: Language) {
    project.registerExplicitExtension(LanguageASTFactory.INSTANCE, language, this)
}

@TestOnly
fun FileType.registerFileType() {
    val app = ApplicationManager.getApplication()
    app.registerService(FileTypeManager::class.java, MockFileTypeManager(this))
}

@TestOnly
fun ParserDefinition.registerExtension(project: Project) {
    project.registerExplicitExtension(LanguageParserDefinitions.INSTANCE, fileNodeType.language, this)
}
