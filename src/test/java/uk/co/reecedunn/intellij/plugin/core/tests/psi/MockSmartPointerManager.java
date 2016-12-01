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
package uk.co.reecedunn.intellij.plugin.core.tests.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class MockSmartPointerManager extends SmartPointerManager {
    @NotNull
    @Override
    public SmartPsiFileRange createSmartPsiFileRangePointer(@NotNull PsiFile file, @NotNull TextRange range) {
        return null;
    }

    @NotNull
    @Override
    public <E extends PsiElement> SmartPsiElementPointer<E> createSmartPsiElementPointer(@NotNull E element) {
        return null;
    }

    @NotNull
    @Override
    public <E extends PsiElement> SmartPsiElementPointer<E> createSmartPsiElementPointer(@NotNull E element, PsiFile containingFile) {
        return new MockSmartPsiElementPointer<E>(element, containingFile);
    }

    @Override
    public boolean pointToTheSameElement(@NotNull SmartPsiElementPointer pointer1, @NotNull SmartPsiElementPointer pointer2) {
        return false;
    }

    @Override
    public void removePointer(@NotNull SmartPsiElementPointer pointer) {

    }
}
