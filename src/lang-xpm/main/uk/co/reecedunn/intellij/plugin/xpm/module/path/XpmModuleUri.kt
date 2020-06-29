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
package uk.co.reecedunn.intellij.plugin.xpm.module.path

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileSystem
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import java.io.InputStream
import java.io.OutputStream

class XpmModuleUri(val contextFile: VirtualFile?, private val path: String) : VirtualFile(), XsAnyUriValue {
    // region VirtualFile

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?): Unit = TODO()

    override fun getLength(): Long = TODO()

    override fun getFileSystem(): VirtualFileSystem = TODO()

    override fun getPath(): String = path

    override fun isDirectory(): Boolean = path.endsWith('/')

    override fun getTimeStamp(): Long = TODO()

    override fun getName(): String = path

    override fun contentsToByteArray(): ByteArray = TODO()

    override fun isValid(): Boolean = false

    override fun getInputStream(): InputStream = TODO()

    override fun getParent(): VirtualFile? = null

    override fun getChildren(): Array<VirtualFile> = TODO()

    override fun isWritable(): Boolean = TODO()

    override fun getOutputStream(
        requestor: Any?,
        newModificationStamp: Long,
        newTimeStamp: Long
    ): OutputStream = TODO()

    // endregion
    // region XsAnyUriValue

    override val data: String = path

    override val context: XdmUriContext = XdmUriContext.Location

    override val moduleTypes: Array<XdmModuleType> = XdmModuleType.MODULE

    // endregion
    // region Object

    override fun toString(): String = path

    override fun equals(other: Any?): Boolean {
        if (other !is XpmModuleUri) return false
        return path == other.path
    }

    override fun hashCode(): Int = path.hashCode()

    // endregion
}
