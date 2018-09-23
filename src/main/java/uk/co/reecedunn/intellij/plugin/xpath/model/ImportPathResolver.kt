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

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFileBase
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
        val resource = "builtin/${path.substringAfter("http://")}.xqy"
        val file = ResourceVirtualFile.create(ResourceVirtualFile::class.java, resource)
        return if (file.isValid) file else null
    }
}

class RelativeFileImportResolver(private val file: VirtualFile) : ImportPathResolver {
    override fun match(path: String): Boolean = !path.contains("://")

    override fun resolve(path: String): VirtualFile? {
        var file = file
        if (file is LightVirtualFileBase) {
            file = file.originalFile
        }

        return resolve(file, path)
    }

    private fun resolve(parent: VirtualFile?, path: String): VirtualFile? {
        if (parent == null) return null
        return parent.findFileByRelativePath(path) ?: resolve(parent.parent, path)
    }
}
