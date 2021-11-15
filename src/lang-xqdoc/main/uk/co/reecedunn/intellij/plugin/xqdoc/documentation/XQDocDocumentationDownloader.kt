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
package uk.co.reecedunn.intellij.plugin.xqdoc.documentation

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.io.HttpRequests
import com.intellij.util.xmlb.XmlSerializerUtil
import uk.co.reecedunn.intellij.plugin.core.progress.TaskManager
import uk.co.reecedunn.intellij.plugin.core.progress.TaskProgressListener
import uk.co.reecedunn.intellij.plugin.core.progress.waitCancellable
import uk.co.reecedunn.intellij.plugin.xpm.lang.documentation.XpmDocumentationSource
import uk.co.reecedunn.intellij.plugin.xqdoc.resources.XQDocBundle
import java.io.File
import java.io.IOException

@State(name = "XdmDocumentationDownloader", storages = [Storage("xijp_settings.xml")])
class XQDocDocumentationDownloader : PersistentStateComponent<XQDocDocumentationDownloader> {
    var basePath: String? = null
        get() = field ?: "${PathManager.getSystemPath()}/xdm-cache/documentation"

    private val tasks = TaskManager<XpmDocumentationSource>()

    fun addListener(listener: TaskProgressListener<XpmDocumentationSource>): Boolean = tasks.addListener(listener)

    fun removeListener(listener: TaskProgressListener<XpmDocumentationSource>): Boolean {
        return tasks.removeListener(listener)
    }

    fun download(source: XpmDocumentationSource): Boolean {
        return tasks.backgroundable(XQDocBundle.message("documentation-source.download.title"), source) { indicator ->
            val file = File("$basePath/${source.path}")
            try {
                HttpRequests.request(source.href).saveToFile(file, indicator)
            } catch (e: IOException) {
                return@backgroundable
            }
            XQDocDocumentationSourceProvider.invalidate(source)
        }
    }

    fun load(source: XpmDocumentationSource, download: Boolean = false): VirtualFile? {
        val file = file(source)
        if (download) {
            if (!file.exists() || tasks.isActive(source)) {
                if (download(source)) waitCancellable {
                    !tasks.isActive(source)
                }
                waitCancellable {
                    tasks.isActive(source)
                }
            }
        }
        return LocalFileSystem.getInstance().findFileByIoFile(file)
    }

    fun file(source: XpmDocumentationSource): File = File("$basePath/${source.path}")

    fun status(source: XpmDocumentationSource): XQDocDocumentationDownloadStatus = when {
        tasks.isActive(source) -> XQDocDocumentationDownloadStatus.Downloading
        file(source).exists() -> XQDocDocumentationDownloadStatus.Downloaded
        else -> XQDocDocumentationDownloadStatus.NotDownloaded
    }

    // region PersistentStateComponent

    override fun getState(): XQDocDocumentationDownloader = this

    override fun loadState(state: XQDocDocumentationDownloader): Unit = XmlSerializerUtil.copyBean(state, this)

    // endregion

    companion object {
        fun getInstance(): XQDocDocumentationDownloader {
            return ApplicationManager.getApplication().getService(XQDocDocumentationDownloader::class.java)
        }
    }
}
