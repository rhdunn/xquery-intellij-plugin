/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.project.configuration

import com.intellij.compat.extensions.extensionPoint
import com.intellij.compat.extensions.ExtensionPointListener
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.vfs.relativePathTo

class XpmProjectConfigurations(private val project: Project) :
    ExtensionPointListener<XpmProjectConfigurationFactoryBean>(),
    Disposable {
    // region ExtensionPointListener

    override fun extensionAdded(extension: XpmProjectConfigurationFactoryBean) {
        cachedConfigurations.invalidate()
    }

    override fun extensionRemoved(extension: XpmProjectConfigurationFactoryBean) {
        cachedConfigurations.invalidate()
    }

    // endregion
    // region Disposable

    override fun dispose() {
    }

    // endregion

    private val cachedConfigurations = CacheableProperty {
        val configurations = ArrayList<XpmProjectConfiguration>()
        ProjectRootManager.getInstance(project).fileIndex.iterateContent { vf ->
            if (!vf.isDirectory) return@iterateContent true
            XpmProjectConfigurationFactory.EP_NAME.extensionList.forEach {
                it.getInstance().create(project, vf)?.let { configuration -> configurations.add(configuration) }
            }
            true
        }
        configurations
    }

    val configurations: Sequence<XpmProjectConfiguration>
        get() = cachedConfigurations.get()!!.asSequence()

    val applicationName: String?
        get() = configurations.mapNotNull { it.applicationName }.firstOrNull()

    val modulePaths: Sequence<VirtualFile>
        get() = configurations.flatMap { it.modulePaths }

    val processorId: Int?
        get() = configurations.mapNotNull { it.processorId }.firstOrNull()

    fun toModulePath(path: VirtualFile): String {
        val modulePath = modulePaths.mapNotNull { it.relativePathTo(path) }.firstOrNull()
        return modulePath?.let { "/$it" } ?: path.path
    }

    fun toModulePath(path: String): String {
        val file = LocalFileSystem.getInstance().findFileByPath(path)
        return file?.let { toModulePath(it) } ?: path
    }

    init {
        XpmProjectConfigurationFactory.EP_NAME.extensionPoint.addExtensionPointListener(this, false, this)
    }

    companion object {
        fun getInstance(project: Project): XpmProjectConfigurations {
            return ServiceManager.getService(project, XpmProjectConfigurations::class.java)
        }
    }
}
