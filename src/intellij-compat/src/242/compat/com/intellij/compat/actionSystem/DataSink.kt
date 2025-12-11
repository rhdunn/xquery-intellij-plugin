// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.actionSystem

import com.intellij.openapi.actionSystem.DataKey

interface DataSink {
    operator fun <T : Any> set(key: DataKey<T>, data: T?)
}
