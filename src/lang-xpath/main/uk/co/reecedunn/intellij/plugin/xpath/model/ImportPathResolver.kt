/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFileBase
import org.jetbrains.jps.model.module.JpsModuleSourceRootType
import uk.co.reecedunn.intellij.plugin.core.roots.sourceFolders

interface ImportPathResolver {
    companion object {
        val IMPORT_PATH_RESOLVER_EP = ExtensionPointName.create<ImportPathResolver>(
            "uk.co.reecedunn.intellij.importPathResolver"
        )
    }

    fun match(path: String): Boolean

    fun resolve(path: String): VirtualFile?
}

object EmptyPathImportResolver : ImportPathResolver {
    override fun match(path: String): Boolean = path.isEmpty()

    override fun resolve(path: String): VirtualFile? = null
}

class RelativeFileImportResolver(private val file: VirtualFile) : ImportPathResolver {
    override fun match(path: String): Boolean = !path.isEmpty() && !path.contains("://") && !path.startsWith("/")

    override fun resolve(path: String): VirtualFile? {
        var file: VirtualFile? = file
        if (file is LightVirtualFileBase) {
            file = file.originalFile
        }

        return file?.parent?.findFileByRelativePath(path)
    }
}

class ModuleFileImportResolver(private val root: VirtualFile) : ImportPathResolver {
    override fun match(path: String): Boolean = path.startsWith("/")

    override fun resolve(path: String): VirtualFile? = root.findFileByRelativePath(path)
}

fun moduleRootImportResolvers(project: Project, rootType: JpsModuleSourceRootType<*>): Sequence<ImportPathResolver> {
    return project.sourceFolders()
        .filter { folder -> folder.file != null && folder.rootType === rootType }
        .map { folder -> ModuleFileImportResolver(folder.file!!) }
}
