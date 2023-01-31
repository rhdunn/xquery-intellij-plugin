// Copyright (C) 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.application.options.codeStyle.cache

import com.intellij.application.options.codeStyle.cache.CodeStyleCachingServiceImpl
import com.intellij.openapi.project.Project

@Suppress("FunctionName")
fun CodeStyleCachingService(project: Project): CodeStyleCachingServiceImpl {
    return CodeStyleCachingServiceImpl(project)
}
