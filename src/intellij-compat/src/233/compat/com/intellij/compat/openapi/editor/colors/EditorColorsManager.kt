// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.editor.colors

import com.intellij.openapi.editor.colors.EditorColorsScheme

abstract class EditorColorsManager : com.intellij.openapi.editor.colors.EditorColorsManager() {
    abstract fun addColorScheme(scheme: EditorColorsScheme)

    abstract fun setCurrentSchemeOnLafChange(scheme: EditorColorsScheme)

    abstract fun getSchemeModificationCounter(): Long

    override fun addColorsScheme(scheme: EditorColorsScheme) = addColorScheme(scheme)
}
