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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Segment;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockSmartPsiElementPointer<E extends PsiElement> implements SmartPsiElementPointer<E> {
    private E mElement;
    private PsiFile mContainingFile;

    public MockSmartPsiElementPointer(E element, PsiFile containingFile) {
        mElement = element;
        mContainingFile = containingFile;
    }

    @Nullable
    @Override
    public E getElement() {
        return mElement;
    }

    @Nullable
    @Override
    public PsiFile getContainingFile() {
        return mContainingFile;
    }

    @NotNull
    @Override
    public Project getProject() {
        return mContainingFile.getProject();
    }

    @Override
    public VirtualFile getVirtualFile() {
        return null;
    }

    @Nullable
    @Override
    public Segment getRange() {
        return null;
    }

    @Nullable
    @Override
    public Segment getPsiRange() {
        return null;
    }
}
