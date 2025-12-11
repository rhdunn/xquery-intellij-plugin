// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.actionSystem

interface UiDataProvider {
    fun uiDataSnapshot(sink: DataSink)
}
