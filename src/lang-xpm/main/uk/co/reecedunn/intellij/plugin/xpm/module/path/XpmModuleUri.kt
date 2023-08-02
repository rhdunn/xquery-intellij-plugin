// Copyright (C) 2019-2020, 2023 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
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

    override fun getLength(): Long = 0

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
