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
package uk.co.reecedunn.intellij.plugin.xdm.module.loader

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.vfs.originalFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xdm.context.XdmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleLocationPath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType

class FixedModuleLoader(val root: VirtualFile) : XdmModuleLoader {
    // region XdmModuleLoader

    private fun findFileByPath(path: String, moduleTypes: Array<XdmModuleType>?): VirtualFile? {
        moduleTypes?.forEach { type ->
            type.extensions.forEach { extension ->
                val file = root.findFileByRelativePath("$path$extension")
                if (file != null) return file
            }
        }
        return root.findFileByRelativePath(path)
    }

    override fun resolve(path: XdmModulePath, context: PsiElement): PsiElement? {
        return when (path) {
            is XdmModuleLocationPath -> {
                if (path.isResource == null) // BaseX reverse domain name module path
                    findFileByPath(path.path, path.moduleTypes)?.toPsiFile(path.project)
                else
                    findFileByPath(path.path, null)?.toPsiFile(path.project)
            }
            else -> null
        }
    }

    override fun context(path: XdmModulePath, context: PsiElement): XdmStaticContext? {
        return when (path) {
            is XdmModuleLocationPath -> resolve(path, context) as? XdmStaticContext
            else -> null
        }
    }

    // endregion
    // region XdmModuleLoaderFactory

    companion object : XdmModuleLoaderFactory {
        override fun loader(context: String?): XdmModuleLoader? {
            return context?.let {
                LocalFileSystem.getInstance().findFileByPath(context)?.let { loader(it) }
            }
        }

        fun loader(file: VirtualFile): XdmModuleLoader? = FixedModuleLoader(file.originalFile)
    }

    // endregion
}
