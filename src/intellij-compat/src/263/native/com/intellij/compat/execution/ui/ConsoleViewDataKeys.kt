// Copyright (C) 2026 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.execution.ui

import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.actionSystem.DataKey

val CONSOLE_VIEW_DATA_KEY: DataKey<ConsoleView>
    get() = ConsoleView.DATA_KEY
