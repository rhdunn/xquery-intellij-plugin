// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.lang.injection

import com.intellij.psi.FileViewProvider

abstract class InjectedLanguageManager : com.intellij.lang.injection.InjectedLanguageManager() {
    abstract fun isInjectedViewProvider(viewProvider: FileViewProvider): Boolean
}
