/*
 * Copyright (C) 2021-2024 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xproc.lang.fileTypes

import com.intellij.ide.FileIconPatcher
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import uk.co.reecedunn.intellij.plugin.xproc.lang.XProc
import uk.co.reecedunn.intellij.plugin.xproc.resources.XProcIcons
import javax.swing.Icon

class XProcFileIconPatcher : FileIconPatcher {
    override fun patchIcon(baseIcon: Icon, file: VirtualFile, flags: Int, project: Project?): Icon = when {
        project == null -> baseIcon
        else -> {
            (PsiManager.getInstance(project).findFile(file) as? XmlFile)?.rootTag?.let {
                if (it.namespace == XProc.NAMESPACE) {
                    XProcIcons.FileType
                } else {
                    null
                }
            } ?: baseIcon
        }
    }
}
