// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.codeInsight.lookup

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.completion.OffsetMap
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.command.CommandProcessor
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.tests.editor.EditorTestCase
import uk.co.reecedunn.intellij.plugin.core.tests.parser.LanguageParserTestCase

interface LookupElementTestCase<File : PsiFile> : LanguageParserTestCase<File>, EditorTestCase {
    @Suppress("MemberVisibilityCanBePrivate")
    private fun handleInsert(text: String, char: Char, lookups: Array<LookupElement>, tailOffset: Int): InsertionContext {
        val file = parseText(text)
        val editor = getEditor(file)
        editor.caretModel.moveToOffset(tailOffset)

        val context = InsertionContext(OffsetMap(editor.document), char, lookups, file, editor, false)
        CommandProcessor.getInstance().executeCommand(null, {
            lookups.forEach { it.handleInsert(context) }
        }, null, null)
        return context
    }

    fun handleInsert(text: String, char: Char, lookup: LookupElement, tailOffset: Int): InsertionContext {
        return handleInsert(text, char, listOf(lookup).toTypedArray(), tailOffset)
    }
}
