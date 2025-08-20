// Copyright (C) 2019, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.editor

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.psi.document
import uk.co.reecedunn.intellij.plugin.core.text.commonPrefixLength

fun Document.completeString(offset: Int, text: String) {
    val common = charsSequence.commonPrefixLength(offset, text)
    if (common != text.length) {
        val rest = text.subSequence(common, text.length)
        insertString(offset + common, rest)
    }
}

val PsiFile.editor: Editor
    get() = EditorFactory.getInstance().createEditor(document!!)
