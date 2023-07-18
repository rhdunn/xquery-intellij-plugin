// Copyright (C) 2022 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
@file:Suppress("DEPRECATION") // Computable is deprecated by IntelliJ.

package com.intellij.compat.psi.impl

import com.intellij.openapi.util.Computable

@Suppress("UnstableApiUsage")
abstract class PsiManagerEx : com.intellij.psi.impl.PsiManagerEx() {
    override fun <T : Any?> runInBatchFilesMode(runnable: Computable<T>): T {
        return runnable.get()
    }
}
