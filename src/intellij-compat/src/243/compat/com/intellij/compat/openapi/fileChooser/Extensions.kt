// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.fileChooser

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileTypeDescriptor

fun FileChooserDescriptor.withExtensionFilterEx(extension: String): FileChooserDescriptor {
    val descriptor = FileTypeDescriptor(this.title, extension)
    return this.withDescription(descriptor.description)
}
