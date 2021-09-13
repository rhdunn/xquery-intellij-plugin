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

import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.ExtensionPointListener
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import uk.co.reecedunn.intellij.plugin.core.util.UserDataHolderBase
import uk.co.reecedunn.intellij.plugin.core.vfs.isAncestorOf
import uk.co.reecedunn.intellij.plugin.core.vfs.relativePathTo

class XpmProjectConfigurations(private val project: Project) :
    UserDataHolderBase(),
    ExtensionPointListener<XpmProjectConfigurationFactoryBean>,
    Disposable {
    companion object {
        private val CONFIGURATIONS = Key.create<List<XpmProjectConfiguration>>("CONFIGURATIONS")

        fun getInstance(project: Project): XpmProjectConfigurations {
            return project.getService(XpmProjectConfigurations::class.java)
        }
    }
    // region ExtensionPointListener

    override fun extensionAdded(extension: XpmProjectConfigurationFactoryBean, pluginDescriptor: PluginDescriptor) {
        clearUserData(CONFIGURATIONS)
    }

    override fun extensionRemoved(extension: XpmProjectConfigurationFactoryBean, pluginDescriptor: PluginDescriptor) {
        clearUserData(CONFIGURATIONS)
    }

    // endregion
    // region Disposable

    override fun dispose() {
    }

    // endregion

    val configurations: Sequence<XpmProjectConfiguration>
        get() = computeUserDataIfAbsent(CONFIGURATIONS) {
            val configurations = ArrayList<XpmProjectConfiguration>()
            ProjectRootManager.getInstance(project).fileIndex.iterateContent { vf ->
                if (!vf.isDirectory) return@iterateContent true
                XpmProjectConfigurationFactory.EP_NAME.extensionList.forEach {
                    it.getInstance().create(project, vf)?.let { configuration -> configurations.add(configuration) }
                }
                true
            }
            configurations
        }.asSequence()

    val applicationName: String?
        get() = configurations.mapNotNull { it.applicationName }.firstOrNull()

    val modulePaths: Sequence<VirtualFile>
        get() = configurations.flatMap { it.modulePaths }

    val processorId: Int?
        get() = configurations.mapNotNull { it.processorId }.firstOrNull()

    val databaseName: String?
        get() = configurations.mapNotNull { it.databaseName }.firstOrNull()

    fun toModulePath(path: VirtualFile): String {
        val modulePath = modulePaths.mapNotNull { it.relativePathTo(path) }.firstOrNull()
        return modulePath?.let { "/$it" } ?: path.path
    }

    fun toModulePath(path: String): String {
        val file = LocalFileSystem.getInstance().findFileByPath(path)
        return file?.let { toModulePath(it) } ?: path
    }

    fun isModulesDirectory(directory: PsiDirectory): Boolean {
        val vf = directory.virtualFile
        return modulePaths.find { it == vf || it.isAncestorOf(vf) } != null
    }

    init {
        XpmProjectConfigurationFactory.EP_NAME.point.addExtensionPointListener(this, false, this)
    }
}
