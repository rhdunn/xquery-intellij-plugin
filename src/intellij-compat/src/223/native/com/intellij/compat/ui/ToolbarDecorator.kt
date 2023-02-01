// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.ui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.ui.AnActionButton
import com.intellij.ui.ToolbarDecorator

fun ToolbarDecorator.addExtraActionButton(action: AnActionButton) {
    addExtraAction(action as AnAction)
}
