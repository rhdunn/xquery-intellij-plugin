// Copyright (C) 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.execution.ui

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.LangDataKeys

val CONSOLE_VIEW_DATA_KEY: DataKey<ConsoleView>
    get() = LangDataKeys.CONSOLE_VIEW
