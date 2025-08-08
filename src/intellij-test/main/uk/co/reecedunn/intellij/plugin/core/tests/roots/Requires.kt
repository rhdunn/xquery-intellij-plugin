// Copyright (C) 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.roots

import com.intellij.openapi.roots.ProjectRootManager
import uk.co.reecedunn.intellij.plugin.core.extensions.registerService
import uk.co.reecedunn.intellij.plugin.core.tests.testFramework.PlatformTestCase

fun PlatformTestCase.requiresProjectSourceFolders() {
    project.registerService<ProjectRootManager>(MockProjectRootsManager())
}
