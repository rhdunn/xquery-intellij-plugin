// Copyright (C) 2018, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.core.tests.module

import com.intellij.mock.MockModule
import com.intellij.mock.MockProject
import com.intellij.openapi.Disposable
import com.intellij.openapi.vfs.VirtualFile

class MockModule(project: MockProject, private val moduleFile: VirtualFile) : MockModule(project, Disposable {}) {
    override fun getModuleFile(): VirtualFile = moduleFile
}
