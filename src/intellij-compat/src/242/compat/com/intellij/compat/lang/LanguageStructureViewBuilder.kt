// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.lang

import com.intellij.lang.LanguageStructureViewBuilder

object LanguageStructureViewBuilder {
    fun getInstance(): LanguageStructureViewBuilder = LanguageStructureViewBuilder.INSTANCE
}
