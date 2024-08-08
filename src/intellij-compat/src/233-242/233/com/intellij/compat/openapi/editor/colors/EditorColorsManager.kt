// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.editor.colors

import com.intellij.openapi.editor.colors.EditorColorsScheme
import org.jetbrains.annotations.TestOnly

@Suppress("UnstableApiUsage")
@TestOnly
abstract class EditorColorsManager : com.intellij.openapi.editor.colors.EditorColorsManager()
