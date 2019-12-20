/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.module.loader

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xdm.context.XdmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath
import java.io.File
import java.net.URLClassLoader

class JarModuleLoader(val classLoader: ClassLoader) : VirtualFileSystemImpl("res"), XdmModuleLoader {
    // region VirtualFileSystem

    private val cache: HashMap<String, VirtualFile?> = HashMap()

    private fun findCacheableFile(path: String): VirtualFile? = ResourceVirtualFile(classLoader, path, this)

    override fun findFileByPath(path: String): VirtualFile? {
        return cache[path] ?: findCacheableFile(path)?.let {
            cache[path] = it
            it
        }
    }

    override fun refresh(asynchronous: Boolean) {
        cache.clear()
    }

    // endregion
    // region XdmModuleLoader

    override fun resolve(path: XdmModulePath): PsiElement? {
        return when (path) {
            is XdmModuleLocationPath -> findFileByPath(path.path)?.toPsiFile(path.project)
            else -> null
        }
    }

    override fun context(path: XdmModulePath): XdmStaticContext? {
        return when (path) {
            is XdmModuleLocationPath -> resolve(path) as? XdmStaticContext
            else -> null
        }
    }

    // endregion
    // region XdmModuleLoaderFactory

    companion object : XdmModuleLoaderFactory {
        override val id: String = "jar"

        override fun loader(context: String?): XdmModuleLoader? {
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
