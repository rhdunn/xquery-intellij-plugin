// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton

fun TextFieldWithBrowseButton.addBrowseFolderListenerEx(
    project: Project?,
    fileChooserDescriptor: FileChooserDescriptor
) {
    addBrowseFolderListener(null, null, project, fileChooserDescriptor)
}
