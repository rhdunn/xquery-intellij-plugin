/*
 * Copyright (C) 2021 Reece H. Dunn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.reecedunn.intellij.plugin.marklogic.xray.test

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.data.ModificationTrackedProperty
import uk.co.reecedunn.intellij.plugin.core.vfs.relativePathTo
import uk.co.reecedunn.intellij.plugin.core.vfs.replace
import uk.co.reecedunn.intellij.plugin.marklogic.intellij.resources.MarkLogicQueries
import uk.co.reecedunn.intellij.plugin.xpm.project.configuration.XpmProjectConfigurations

class XRayTestService(private val project: Project) {
    private val cachedXrayRootDir = ModificationTrackedProperty<ProjectRootManager, VirtualFile> {
        var rootDir: VirtualFile? = null
        it.fileIndex.iterateContent { vf ->
            if (!vf.isDirectory) return@iterateContent true

            val isXRayDir = vf.name == XRAY_DIR
            val hasIndex = vf.findChild(INDEX_XQY) != null
            val hasSrcXRay = vf.findChild(SRC_DIR)?.findChild(XRAY_XQY) != null
            if (isXRayDir && hasIndex && hasSrcXRay) {
                rootDir = vf
            }
            rootDir == null
        }
        rootDir
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val xrayRootDir: VirtualFile?
        get() = cachedXrayRootDir.get(ProjectRootManager.getInstance(project))

    @Suppress("MemberVisibilityCanBePrivate")
    val xrayModuleRoot: String?
        get() = xrayRootDir?.let { rootDir ->
            val path = XpmProjectConfigurations.getInstance(project).configurations.mapNotNull { configuration ->
                configuration.modulePaths.mapNotNull { it.relativePathTo(rootDir) }.firstOrNull()
            }.firstOrNull()
            path?.let { "/$it" }
        }

    val runTestsQuery: VirtualFile?
        get() = xrayModuleRoot?.let { root ->
            MarkLogicQueries.XRay.RunTests.replace("%XRAY_XQY_PATH%", "$root/src/xray.xqy")
        }

    companion object {
        fun getInstance(project: Project): XRayTestService {
            return ServiceManager.getService(project, XRayTestService::class.java)
        }

        private const val XRAY_DIR = "xray"
        private const val SRC_DIR = "src"

        private const val INDEX_XQY = "index.xqy"
        private const val XRAY_XQY = "xray.xqy"
    }
}
