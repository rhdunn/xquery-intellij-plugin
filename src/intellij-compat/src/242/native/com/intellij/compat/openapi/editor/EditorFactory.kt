// Copyright (C) 2024 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.editor

import com.intellij.openapi.editor.Editor
import org.jetbrains.annotations.TestOnly

@Suppress("NonExtendableApiUsage")
@TestOnly
abstract class EditorFactory: com.intellij.openapi.editor.EditorFactory() {
    override fun getEditorList(): MutableList<Editor> = TODO()
}
