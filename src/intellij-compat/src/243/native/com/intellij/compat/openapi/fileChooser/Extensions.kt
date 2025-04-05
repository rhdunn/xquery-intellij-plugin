// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.fileChooser

import com.intellij.openapi.fileChooser.FileChooserDescriptor

fun FileChooserDescriptor.withExtensionFilterEx(extension: String): FileChooserDescriptor {
    return this.withExtensionFilter(extension)
}
