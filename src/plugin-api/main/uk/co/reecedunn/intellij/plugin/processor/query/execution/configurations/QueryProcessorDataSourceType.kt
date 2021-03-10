/*
 * Copyright (C) 2018-2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.processor.query.execution.configurations

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModuleUri
import java.io.File

enum class QueryProcessorDataSourceType {
    LocalFile, DatabaseModule, ActiveEditorFile;

    fun find(path: String?, project: Project): VirtualFile? = when (this) {
        LocalFile -> path?.let {
            val url = VfsUtil.pathToUrl(path.replace(File.separatorChar, '/'))
            url.let { VirtualFileManager.getInstance().findFileByUrl(url) }
        }
        DatabaseModule -> path?.let { XpmModuleUri(null, path) }
        ActiveEditorFile -> FileEditorManager.getInstance(project).selectedFiles.firstOrNull()
    }
}
