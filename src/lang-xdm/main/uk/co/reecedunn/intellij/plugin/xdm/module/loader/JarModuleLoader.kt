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
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl

class JarModuleLoader(val classLoader: ClassLoader) : VirtualFileSystemImpl("res") {
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
}
