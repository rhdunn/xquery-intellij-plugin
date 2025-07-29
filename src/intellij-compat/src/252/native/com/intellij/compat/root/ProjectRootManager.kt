// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package com.intellij.compat.openapi.fileChooser

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager

abstract class ProjectRootManager : com.intellij.openapi.roots.ProjectRootManager() {
    override fun getModuleRootManager(module: Module): ModuleRootManager = TODO()
}
