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
package uk.co.reecedunn.intellij.plugin.intellij.resources

import com.intellij.openapi.vfs.CharsetToolkit
import com.intellij.openapi.vfs.VirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.core.vfs.decode

object MarkLogicQueries {
    private fun resourceFile(path: String): VirtualFile {
        val file = ResourceVirtualFile(MarkLogicQueries::class.java.classLoader, path)
        file.charset = CharsetToolkit.UTF8_CHARSET
        return file
    }

    val Run = resourceFile("queries/marklogic/run.xq").decode()!!

    val Version = resourceFile("queries/marklogic/version.xq")
    val Servers = resourceFile("queries/marklogic/servers.xq")
    val Databases = resourceFile("queries/marklogic/databases.xq")
}
