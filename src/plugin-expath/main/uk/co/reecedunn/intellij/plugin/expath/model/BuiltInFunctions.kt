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
package uk.co.reecedunn.intellij.plugin.expath.model

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.xpath.model.ImportPathResolver

private object EXPathBuiltInFunctionFileSystem : VirtualFileSystemImpl("res") {
    override fun findCacheableFile(path: String): VirtualFile? {
        return ResourceVirtualFile(this::class.java.classLoader, path, this)
    }
}

object BuiltInFunctions : ImportPathResolver {
    override fun match(path: String): Boolean = MODULES.containsKey(path)

    override fun resolve(path: String): VirtualFile? {
        return MODULES[path]?.let { EXPathBuiltInFunctionFileSystem.findFileByPath(it) }
    }

    private val MODULES = mapOf(
        "http://expath.org/ns/binary" to "builtin/expath.org/ns/binary.xqy",
        "http://expath.org/ns/crypto" to "builtin/expath.org/ns/crypto.xqy",
        "http://expath.org/ns/file" to "builtin/expath.org/ns/file.xqy",
        "http://expath.org/ns/geo" to "builtin/expath.org/ns/geo.xqy",
        "http://expath.org/ns/http-client" to "builtin/expath.org/ns/http-client.xqy",
        "http://expath.org/ns/webapp" to "builtin/expath.org/ns/webapp.xqy",
        "http://expath.org/ns/zip" to "builtin/expath.org/ns/zip.xqy"
    )
}
