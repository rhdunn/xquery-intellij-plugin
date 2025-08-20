// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.psi.codeStyles

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.codeStyle.IndentHelper

class MockIndentHelper : IndentHelper() {
    override fun getIndent(file: PsiFile, element: ASTNode): Int = getIndent(file, element, true)

    override fun getIndent(file: PsiFile, element: ASTNode, includeNonSpace: Boolean): Int = 3
}
