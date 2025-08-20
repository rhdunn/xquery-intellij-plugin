// Copyright (C) 2023, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.lang.injection

import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement

abstract class InjectedLanguageManager : com.intellij.lang.injection.InjectedLanguageManager() {
    abstract fun isInjectedViewProvider(viewProvider: FileViewProvider): Boolean

    abstract fun shouldInspectionsBeLenient(injectedContext: PsiElement): Boolean

    abstract fun isFrankensteinInjection(injectedContext: PsiElement): Boolean
}
