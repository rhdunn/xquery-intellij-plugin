// Copyright (C) 2016-2021, 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.codeInsight.multiverse.CodeInsightContext
import com.intellij.lang.LanguageUtil
import com.intellij.mock.MockFileManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileFilter
import com.intellij.psi.*
import com.intellij.psi.impl.PsiManagerEx
import com.intellij.psi.impl.PsiModificationTrackerImpl
import com.intellij.psi.impl.PsiTreeChangeEventImpl
import com.intellij.psi.impl.PsiTreeChangePreprocessor
import com.intellij.psi.impl.file.impl.FileManager
import com.intellij.psi.impl.file.impl.FileManagerEx
import com.intellij.psi.util.PsiModificationTracker
import uk.co.reecedunn.intellij.plugin.core.vfs.decode
import java.io.IOException

class MockPsiManager(private val project: Project) : PsiManagerEx() {
    // region PsiManager

    private val myModificationTracker: PsiModificationTracker by lazy { PsiModificationTrackerImpl(project) }

    override fun getProject(): Project = project

    override fun findFile(file: VirtualFile): PsiFile? {
        try {
            val language = LanguageUtil.getLanguageForPsi(project, file) ?: return null
            return PsiFileFactory.getInstance(project).createFileFromText(
                file.name, language, file.decode() ?: return null, true, false, false, file
            )
        } catch (e: IOException) {
            return null
        }
    }

    override fun findFile(
        file: VirtualFile,
        context: CodeInsightContext
    ): PsiFile? = findFile(file)

    override fun findViewProvider(file: VirtualFile): FileViewProvider? = null

    override fun findViewProvider(
        file: VirtualFile,
        context: CodeInsightContext
    ): FileViewProvider? = findViewProvider(file)

    override fun findDirectory(file: VirtualFile): PsiDirectory? = null

    override fun areElementsEquivalent(element1: PsiElement?, element2: PsiElement?): Boolean {
        return Comparing.equal(element1, element2)
    }

    override fun reloadFromDisk(file: PsiFile) {
    }

    @Deprecated("Deprecated in Java")
    override fun addPsiTreeChangeListener(listener: PsiTreeChangeListener) {
    }

    override fun addPsiTreeChangeListener(listener: PsiTreeChangeListener, parentDisposable: Disposable) {
    }

    override fun removePsiTreeChangeListener(listener: PsiTreeChangeListener) {
    }

    override fun getModificationTracker(): PsiModificationTracker = myModificationTracker

    @Deprecated("Deprecated in Java")
    override fun startBatchFilesProcessingMode() {
    }

    @Deprecated("Deprecated in Java")
    override fun finishBatchFilesProcessingMode() {
    }

    override fun <T : Any?> runInBatchFilesMode(runnable: Computable<T>): T {
        return runnable.get()
    }

    override fun isDisposed(): Boolean = false

    override fun dropResolveCaches(): Unit = fileManager.cleanupForNextTest()

    override fun dropPsiCaches(): Unit = dropResolveCaches()

    override fun isInProject(element: PsiElement): Boolean = false

    @Suppress("UnstableApiUsage", "RedundantSuppression")
    override fun findCachedViewProvider(vFile: VirtualFile): FileViewProvider = TODO()

    // endregion
    // region PsiManagerEx

    private val myFileManager: FileManager by lazy { MockFileManager(this) }

    override fun cleanupForNextTest() = TODO()

    override fun dropResolveCacheRegularly(progressIndicator: ProgressIndicator) = TODO()

    override fun isBatchFilesProcessingMode(): Boolean = false

    override fun setAssertOnFileLoadingFilter(filter: VirtualFileFilter, parentDisposable: Disposable) {
    }

    override fun isAssertOnFileLoading(file: VirtualFile): Boolean = false

    override fun getFileManager(): FileManager = myFileManager

    override fun getFileManagerEx(): FileManagerEx = TODO()

    override fun beforeChildAddition(event: PsiTreeChangeEventImpl) {
    }

    override fun beforeChildRemoval(event: PsiTreeChangeEventImpl) {
    }

    override fun beforeChildReplacement(event: PsiTreeChangeEventImpl) {
    }

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

    override fun beforeChange(isPhysical: Boolean) {
        throw UnsupportedOperationException()
    }

    override fun afterChange(isPhysical: Boolean) {
        throw UnsupportedOperationException()
    }

    // endregion
}