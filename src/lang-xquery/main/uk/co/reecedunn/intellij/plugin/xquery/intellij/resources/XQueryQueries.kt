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
package uk.co.reecedunn.intellij.plugin.xquery.intellij.resources

import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.VirtualFileSystemImpl
import uk.co.reecedunn.intellij.plugin.core.vfs.decode

object XQueryQueries : VirtualFileSystemImpl("res") {
    private val cache: HashMap<String, VirtualFile?> = HashMap()

    fun resourceFile(path: String): VirtualFile? {
        val file = ResourceVirtualFile.createIfValid(this::class.java.classLoader, path, this) ?: return null
        file.charset = CharsetToolkit.UTF8_CHARSET
        return file
    }

    override fun findFileByPath(path: String): VirtualFile? {
        return cache[path] ?: resourceFile(path).let {
            cache[path] = it
            it
        }
    }

    override fun refresh(asynchronous: Boolean) {
    }

    val ColorSettingsDemo: String = findFileByPath("settings/xquery-color-demo.xq")?.decode()!!

    val CatchClauseVariables: VirtualFile = findFileByPath("static-context/www.w3.org/catch-clause-variables.xqy")!!
}
