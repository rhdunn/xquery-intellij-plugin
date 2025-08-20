// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.fileTypes

import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders

object FileTypeEditorHighlighterProviders {
    fun getInstance(): FileTypeEditorHighlighterProviders = FileTypeEditorHighlighterProviders.INSTANCE
}
