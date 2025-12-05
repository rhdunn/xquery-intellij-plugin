// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.codeInsight.lookup

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.OffsetMap
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.command.CommandProcessor
import com.intellij.psi.PsiFile
import com.intellij.testFramework.runInEdtAndWait
import uk.co.reecedunn.intellij.plugin.core.editor.editor
import uk.co.reecedunn.intellij.plugin.core.tests.lang.LanguageTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.lang.parseText

fun <File : PsiFile> LanguageTestCase.handleInsert(
    text: String,
    char: Char,
    lookup: LookupElement,
    tailOffset: Int
): InsertionContext {
    return handleInsert<File>(text, char, listOf(lookup).toTypedArray(), tailOffset)
}

private fun <File : PsiFile> LanguageTestCase.handleInsert(
    text: String,
    char: Char,
    lookups: Array<LookupElement>,
    tailOffset: Int
): InsertionContext {
    val file = parseText<File>(text)
    val editor = file.editor
    editor.caretModel.moveToOffset(tailOffset)

    val context = InsertionContext(OffsetMap(editor.document), char, lookups, file, editor, false)
    runInEdtAndWait {
        CommandProcessor.getInstance().executeCommand(null, {
            lookups.forEach { it.handleInsert(context) }
        }, null, null)
    }
    return context
}
