// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.root

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager

abstract class ProjectRootManager : ProjectRootManager() {
    abstract fun getModuleRootManager(module: Module): ModuleRootManager
}
