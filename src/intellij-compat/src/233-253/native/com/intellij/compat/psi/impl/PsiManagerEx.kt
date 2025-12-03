// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.psi.impl

import com.intellij.codeInsight.multiverse.CodeInsightContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiTreeChangeListener
import com.intellij.psi.impl.PsiTreeChangeEventImpl
import com.intellij.psi.impl.PsiTreeChangePreprocessor
import com.intellij.psi.impl.file.impl.FileManagerEx

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

    override fun cleanupForNextTest() = TODO()

    override fun dropResolveCacheRegularly(indicator: ProgressIndicator) = TODO()

    override fun getFileManagerEx(): FileManagerEx = TODO()

    override fun beforeChildrenChange(event: PsiTreeChangeEventImpl) {
    }

    override fun beforeChildMovement(event: PsiTreeChangeEventImpl) {
    }

    override fun beforePropertyChange(event: PsiTreeChangeEventImpl) {
    }

    override fun childAdded(event: PsiTreeChangeEventImpl) {
    }

    override fun childRemoved(event: PsiTreeChangeEventImpl) {
    }

    override fun childReplaced(event: PsiTreeChangeEventImpl) {
    }

    override fun childMoved(event: PsiTreeChangeEventImpl) {
    }

    override fun childrenChanged(event: PsiTreeChangeEventImpl) {
    }

    override fun propertyChanged(event: PsiTreeChangeEventImpl) {
    }

    override fun addTreeChangePreprocessor(preprocessor: PsiTreeChangePreprocessor) {
    }

    override fun removeTreeChangePreprocessor(preprocessor: PsiTreeChangePreprocessor) {
    }

    override fun addPsiTreeChangeListenerBackgroundable(
        listener: PsiTreeChangeListener,
        parentDisposable: Disposable
    ) {
    }

    override fun addTreeChangePreprocessor(
        preprocessor: PsiTreeChangePreprocessor,
        parentDisposable: Disposable
    ) {
    }

    override fun addTreeChangePreprocessorBackgroundable(
        preprocessor: PsiTreeChangePreprocessor,
        parentDisposable: Disposable
    ) {
    }
}
