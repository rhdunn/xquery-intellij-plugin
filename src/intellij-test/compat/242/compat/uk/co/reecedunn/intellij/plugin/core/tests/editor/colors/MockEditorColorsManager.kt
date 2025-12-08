// Copyright (C) 2021-2022, 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.editor.colors

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColorsScheme

class MockEditorColorsManager : EditorColorsManager() {
    override fun addColorScheme(scheme: EditorColorsScheme): Unit = TODO()

    override fun getAllSchemes(): Array<EditorColorsScheme> = TODO()

    override fun setGlobalScheme(scheme: EditorColorsScheme?): Unit = TODO()

    override fun getGlobalScheme(): EditorColorsScheme = TODO()

    override fun getScheme(schemeName: String): EditorColorsScheme = TODO()

    override fun isDefaultScheme(scheme: EditorColorsScheme?): Boolean = TODO()

    override fun isUseOnlyMonospacedFonts(): Boolean = TODO()

    override fun setUseOnlyMonospacedFonts(b: Boolean): Unit = TODO()

    override fun isDarkEditor(): Boolean = false

    @Suppress("UnstableApiUsage", "RedundantSuppression")
    override fun setCurrentSchemeOnLafChange(scheme: EditorColorsScheme) = TODO()

    @Suppress("UnstableApiUsage", "RedundantSuppression")
    override fun getSchemeModificationCounter(): Long = TODO()
}
