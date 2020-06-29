/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmStaticContext
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory
import java.io.File
import java.net.URLClassLoader

class JarModuleLoader(val classLoader: ClassLoader) : VirtualFileSystemImpl("res"), XpmModuleLoader {
    // region VirtualFileSystem

    private val cache: HashMap<String, VirtualFile?> = HashMap()

    private fun findCacheableFile(path: String, types: Array<XdmModuleType>): VirtualFile? {
        types.forEach { type ->
            type.extensions.forEach { extension ->
                val file = ResourceVirtualFile.createIfValid(classLoader, "$path$extension", this)
                if (file != null) return file
            }
        }
        return ResourceVirtualFile.createIfValid(classLoader, path, this)
    }

    private fun findFileByPath(path: String, types: Array<XdmModuleType>): VirtualFile? {
        return cache[path] ?: findCacheableFile(path, types)?.let {
            cache[path] = it
            it
        }
    }

    override fun findFileByPath(path: String): VirtualFile? = findFileByPath(path, arrayOf())

    override fun refresh(asynchronous: Boolean): Unit = cache.clear()

    // endregion
    // region XpmModuleLoader

    override fun resolve(path: XpmModulePath, context: VirtualFile?): PsiElement? {
        return when (path) {
            is XpmModuleLocationPath -> findFileByPath(path.path, path.moduleTypes)?.toPsiFile(path.project)
            else -> null
        }
    }

    override fun context(path: XpmModulePath, context: VirtualFile?): XpmStaticContext? {
        return when (path) {
            is XpmModuleLocationPath -> resolve(path, context) as? XpmStaticContext
            else -> null
        }
    }

    override fun relativePathTo(file: VirtualFile, project: Project): String? = null

    // endregion
    // region XpmModuleLoaderFactory

    companion object : XpmModuleLoaderFactory {
        override fun loader(context: String?): XpmModuleLoader? {
            try {
                val path = context?.let { File(context) } ?: return null
                val classLoader = URLClassLoader(arrayOf(path.toURI().toURL()))
                return JarModuleLoader(classLoader)
            } catch (_: Throwable) {
                return null
            }
        }
    }

    // endregion
}
