// Copyright (C) 2024-2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.vfs.encoding

import com.intellij.openapi.vfs.encoding.EncodingManager
import com.intellij.openapi.vfs.encoding.EncodingManagerImpl
import kotlinx.coroutines.CoroutineScope

@Suppress("FunctionName")
fun EncodingManagerImpl(@Suppress("UNUSED_PARAMETER", "unused") coroutineScope: CoroutineScope): EncodingManager {
    return EncodingManagerImpl()
}
