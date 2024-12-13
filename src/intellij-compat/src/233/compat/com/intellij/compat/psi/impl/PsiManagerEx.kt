// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.psi.impl

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.FileViewProvider

abstract class PsiManagerEx : com.intellij.psi.impl.PsiManagerEx() {
    abstract fun findCachedViewProvider(vFile: VirtualFile): FileViewProvider
}
