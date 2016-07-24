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

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MockFileViewProvider implements FileViewProvider {
    private final PsiManager mPsiManager;
    private final Set<Language> mLanguages = new HashSet<>();

    public MockFileViewProvider(PsiManager psiManager) {
        mPsiManager = psiManager;
        mLanguages.add(XQuery.INSTANCE);
    }

    @NotNull
    @Override
    public PsiManager getManager() {
        return mPsiManager;
    }

    @Nullable
    @Override
    public Document getDocument() {
        return null;
    }

    @NotNull
    @Override
    public CharSequence getContents() {
        return null;
    }

    @NotNull
    @Override
    public VirtualFile getVirtualFile() {
        return null;
    }

    @NotNull
    @Override
    public Language getBaseLanguage() {
        return null;
    }

    @NotNull
    @Override
    public Set<Language> getLanguages() {
        return mLanguages;
    }

    @Override
    public PsiFile getPsi(@NotNull Language target) {
        return null;
    }

    @NotNull
    @Override
    public List<PsiFile> getAllFiles() {
        return null;
    }

    @Override
    public boolean isEventSystemEnabled() {
        return false;
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public long getModificationStamp() {
        return 0;
    }

    @Override
    public boolean supportsIncrementalReparse(@NotNull Language rootLanguage) {
        return false;
    }

    @Override
    public void rootChanged(@NotNull PsiFile psiFile) {
    }

    @Override
    public void beforeContentsSynchronized() {
    }

    @Override
    public void contentsSynchronized() {
    }

    @Override
    public FileViewProvider clone() {
        return null;
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @Nullable
    @Override
    public PsiReference findReferenceAt(int offset) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset, @NotNull Language language) {
        return null;
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset, @NotNull Class<? extends Language> lang) {
        return null;
    }

    @Nullable
    @Override
    public PsiReference findReferenceAt(int offsetInElement, @NotNull Language language) {
        return null;
    }

    @NotNull
    @Override
    public FileViewProvider createCopy(@NotNull VirtualFile copy) {
        return null;
    }

    @NotNull
    @Override
    public PsiFile getStubBindingRoot() {
        return null;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return null;
    }

    @Nullable
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
    }
}
