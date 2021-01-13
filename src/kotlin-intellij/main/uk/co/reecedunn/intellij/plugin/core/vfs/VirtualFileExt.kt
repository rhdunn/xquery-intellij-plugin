/*
 * Copyright (C) 2018, 2020 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.core.vfs

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightVirtualFileBase
import uk.co.reecedunn.intellij.plugin.core.io.decode

fun VirtualFile.toPsiFile(project: Project): PsiFile? = PsiManager.getInstance(project).findFile(this)

fun VirtualFile.decode(): String? = when (this) {
    is EditedVirtualFile -> contents // Avoid String => InputStream => String round-trips.
    else -> inputStream?.decode(charset)
}

val VirtualFile.originalFile: VirtualFile
    get() = (this as? LightVirtualFileBase)?.originalFile ?: this

fun VirtualFile.isAncestorOf(file: VirtualFile): Boolean {
    val fileParent = file.parent ?: return false
    return this == fileParent || isAncestorOf(fileParent)
}

fun VirtualFile.relativePathTo(file: VirtualFile): String? = when (isAncestorOf(file)) {
    true -> relativePathTo(file.parent, file.name)
    else -> null
}

private fun VirtualFile.relativePathTo(file: VirtualFile?, path: String): String {
    if (file == null || this == file) return path
    return relativePathTo(file.parent, "${file.name}/$path")
}
