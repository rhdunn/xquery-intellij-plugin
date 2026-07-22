// Copyright (C) 2016, 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*

class MockSmartPointerManager : SmartPointerManager() {
    override fun createSmartPsiFileRangePointer(file: PsiFile, range: TextRange): SmartPsiFileRange = TODO()

    override fun <E : PsiElement> createSmartPsiElementPointer(element: E): SmartPsiElementPointer<E> = TODO()

    override fun <E : PsiElement> createSmartPsiElementPointer(
        element: E,
        containingFile: PsiFile
    ): SmartPsiElementPointer<E> {
        return MockSmartPsiElementPointer(element, containingFile)
    }

    override fun pointToTheSameElement(
        pointer1: SmartPsiElementPointer<*>,
        pointer2: SmartPsiElementPointer<*>
    ): Boolean {
        return false
    }

    override fun removePointer(pointer: SmartPsiElementPointer<*>) {}
}
