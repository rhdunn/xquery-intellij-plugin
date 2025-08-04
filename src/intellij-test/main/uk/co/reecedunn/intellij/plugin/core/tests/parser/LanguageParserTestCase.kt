// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.parser

import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.sequences.walkTree
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.ProjectTestCase
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile

interface LanguageParserTestCase<File : PsiFile> : LanguageTestCase, ProjectTestCase {
    @Suppress("UNCHECKED_CAST")
    fun parseText(text: String): File = createVirtualFile("testcase.xqy", text).toPsiFile(project) as File
}

inline fun <reified T> LanguageParserTestCase<*>.parse(xquery: String): List<T> {
    return parseText(xquery).walkTree().filterIsInstance<T>().toList()
}

inline fun <reified T> LanguageParserTestCase<*>.parse(vararg xquery: String): List<T> {
    return parseText(xquery.joinToString("\n")).walkTree().filterIsInstance<T>().toList()
}
