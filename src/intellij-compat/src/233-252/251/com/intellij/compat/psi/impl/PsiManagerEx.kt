// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.psi.impl

import com.intellij.codeInsight.multiverse.CodeInsightContext
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile

@Suppress("UnstableApiUsage")
abstract class PsiManagerEx : com.intellij.psi.impl.PsiManagerEx() {
    @Deprecated("Deprecated in Java")
    override fun findFile(
        file: VirtualFile,
        context: CodeInsightContext
    ): PsiFile? = findFile(file)

    @Deprecated("Deprecated in Java")
    override fun findViewProvider(
        file: VirtualFile,
        context: CodeInsightContext
    ): FileViewProvider? = findViewProvider(file)
}
