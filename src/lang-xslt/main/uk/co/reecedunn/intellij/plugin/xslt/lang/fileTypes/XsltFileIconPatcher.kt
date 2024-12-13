/*
 * Copyright (C) 2020-2024 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xslt.lang.fileTypes

import com.intellij.ide.FileIconPatcher
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import uk.co.reecedunn.intellij.plugin.xslt.lang.XSLT
import uk.co.reecedunn.intellij.plugin.xslt.lang.isIntellijXPathPluginEnabled
import uk.co.reecedunn.intellij.plugin.xslt.resources.XsltIcons
import javax.swing.Icon

class XsltFileIconPatcher : FileIconPatcher {
    override fun patchIcon(baseIcon: Icon, file: VirtualFile, flags: Int, project: Project?): Icon = when {
        isIntellijXPathPluginEnabled() -> baseIcon
        project == null -> baseIcon
        else -> {
            (PsiManager.getInstance(project).findFile(file) as? XmlFile)?.rootTag?.let {
                if (it.namespace == XSLT.NAMESPACE) {
                    XsltIcons.FileType
                } else {
                    null
                }
            } ?: baseIcon
        }
    }
}
