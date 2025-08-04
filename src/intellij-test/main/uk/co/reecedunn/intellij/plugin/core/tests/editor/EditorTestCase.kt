// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.editor

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.psi.document

interface EditorTestCase {
    @Suppress("MemberVisibilityCanBePrivate")
    fun getEditor(file: PsiFile): Editor = EditorFactory.getInstance().createEditor(file.document!!)
}
