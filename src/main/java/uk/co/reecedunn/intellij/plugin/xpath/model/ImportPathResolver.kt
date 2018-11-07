/*
 * Copyright (C) 2018 Reece H. Dunn
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

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFileBase
import org.jetbrains.jps.model.module.JpsModuleSourceRootType
import uk.co.reecedunn.intellij.plugin.core.roots.sourceFolders
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile

interface ImportPathResolver {
    fun match(path: String): Boolean

    fun resolve(path: String): VirtualFile?
}

object EmptyPathImportResolver : ImportPathResolver {
    override fun match(path: String): Boolean = path.isEmpty()

    override fun resolve(path: String): VirtualFile? = null
}

object ResProtocolImportResolver : ImportPathResolver {
    override fun match(path: String): Boolean = path.startsWith("res://")

    override fun resolve(path: String): VirtualFile? {
        val resource = path.replaceFirst("res://".toRegex(), "builtin/")
        val file = ResourceVirtualFile.create(ResourceVirtualFile::class.java, resource)
        return if (file.isValid) file else null
    }
}

object HttpProtocolImportResolver : ImportPathResolver {
    override fun match(path: String): Boolean = path.startsWith("http://")

    override fun resolve(path: String): VirtualFile? {
        return when {
            path.endsWith("#") -> resolvePath("builtin/${path.substring(7, path.length - 1)}.xqy")
            path.endsWith("/") -> resolvePath("builtin/${path.substring(7)}default.xqy")
            else -> resolvePath("builtin/${path.substring(7)}.xqy")
        }
    }

    private fun resolvePath(path: String): VirtualFile? {
        val file = ResourceVirtualFile.create(ResourceVirtualFile::class.java, path)
        return if (file.isValid) file else null
    }
}

class RelativeFileImportResolver(private val file: VirtualFile) : ImportPathResolver {
    override fun match(path: String): Boolean = !path.isEmpty() && !path.contains("://") && !path.startsWith("/")

    override fun resolve(path: String): VirtualFile? {
        var file = file
        if (file is LightVirtualFileBase) {
            file = file.originalFile
        }

        return file.parent.findFileByRelativePath(path)
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
