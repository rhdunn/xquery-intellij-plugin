/*
 * Copyright (C) 2016 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.xquery.tests.mocks;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockPsiManager extends PsiManagerEx {
    @NotNull
    @Override
    public Project getProject() {
        return null;
    }

    @Nullable
    @Override
    public PsiFile findFile(@NotNull VirtualFile file) {
        return null;
    }

    @Nullable
    @Override
    public FileViewProvider findViewProvider(@NotNull VirtualFile file) {
        return null;
    }

    @Nullable
    @Override
    public PsiDirectory findDirectory(@NotNull VirtualFile file) {
        return null;
    }

    @Override
    public boolean areElementsEquivalent(@Nullable PsiElement element1, @Nullable PsiElement element2) {
        return false;
    }

    @Override
    public void reloadFromDisk(@NotNull PsiFile file) {
    }

    @Override
    public void addPsiTreeChangeListener(@NotNull PsiTreeChangeListener listener) {
    }

    @Override
    public void addPsiTreeChangeListener(@NotNull PsiTreeChangeListener listener, @NotNull Disposable parentDisposable) {
    }

    @Override
    public void removePsiTreeChangeListener(@NotNull PsiTreeChangeListener listener) {
    }

    @NotNull
    @Override
    public PsiModificationTracker getModificationTracker() {
        return null;
    }

    @Override
    public void startBatchFilesProcessingMode() {
    }

    @Override
    public void finishBatchFilesProcessingMode() {
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void dropResolveCaches() {
    }

    @Override
    public boolean isInProject(@NotNull PsiElement element) {
        return false;
    }

    @Override
    public boolean isBatchFilesProcessingMode() {
        return false;
    }

    @Override
    public void setAssertOnFileLoadingFilter(@NotNull VirtualFileFilter filter, @NotNull Disposable parentDisposable) {
    }

    @Override
    public boolean isAssertOnFileLoading(@NotNull VirtualFile file) {
        return false;
    }

    @Override
    public void registerRunnableToRunOnChange(@NotNull Runnable runnable) {
    }

    @Override
    public void registerRunnableToRunOnAnyChange(@NotNull Runnable runnable) {
    }

    @Override
    public void registerRunnableToRunAfterAnyChange(@NotNull Runnable runnable) {
    }

    @NotNull
    @Override
    public FileManager getFileManager() {
        return null;
    }

    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEventImpl event) {
    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEventImpl event) {
    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEventImpl event) {
    }

    @Override
    public void beforeChange(boolean isPhysical) {
    }

    @Override
    public void afterChange(boolean isPhysical) {
    }
}
