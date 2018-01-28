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
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

class MockSmartPointerManager : SmartPointerManager() {
    override fun createSmartPsiFileRangePointer(file: PsiFile, range: TextRange): SmartPsiFileRange = TODO()

    override fun <E : PsiElement> createSmartPsiElementPointer(element: E): SmartPsiElementPointer<E> = TODO()

    override fun <E : PsiElement> createSmartPsiElementPointer(element: E, containingFile: PsiFile): SmartPsiElementPointer<E> {
        return MockSmartPsiElementPointer(element, containingFile)
    }

    override fun pointToTheSameElement(pointer1: SmartPsiElementPointer<*>, pointer2: SmartPsiElementPointer<*>): Boolean {
        return false
    }

    override fun removePointer(pointer: SmartPsiElementPointer<*>) {}
}
