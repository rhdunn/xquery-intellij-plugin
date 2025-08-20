// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.lang.injection

import com.intellij.psi.PsiElement

abstract class InjectedLanguageManager : com.intellij.lang.injection.InjectedLanguageManager() {
    abstract fun shouldInspectionsBeLenient(injectedContext: PsiElement): Boolean

    abstract fun isFrankensteinInjection(injectedContext: PsiElement): Boolean
}
