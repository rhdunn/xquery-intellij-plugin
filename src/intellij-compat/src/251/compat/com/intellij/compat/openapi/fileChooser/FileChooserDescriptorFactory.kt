// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.fileChooser

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.*

object FileChooserDescriptorFactory {
    fun singleDir(): FileChooserDescriptor = createSingleFolderDescriptor()
}
