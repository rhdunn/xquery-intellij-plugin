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
package uk.co.reecedunn.intellij.plugin.xpm.module.loader.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import org.jetbrains.jps.model.java.JavaSourceRootType
import org.jetbrains.jps.model.module.JpsModuleSourceRootType
import uk.co.reecedunn.intellij.plugin.core.roots.getSourceRootType
import uk.co.reecedunn.intellij.plugin.core.roots.sourceFolders
import uk.co.reecedunn.intellij.plugin.core.vfs.relativePathTo
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmStaticContext
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xpm.module.path.impl.XpmModuleLocationPath

class JspModuleSourceRootLoader(private val rootType: JpsModuleSourceRootType<*>) : XpmModuleLoader {
    // region XpmModuleLoader

    private fun findFileByPath(root: VirtualFile, path: String, moduleTypes: Array<XdmModuleType>?): VirtualFile? {
        moduleTypes?.forEach { type ->
            type.extensions.forEach { extension ->
                val file = root.findFileByRelativePath("$path$extension")
                if (file != null) return file
            }
        }
        return root.findFileByRelativePath(path)
    }

    private fun findFileByPath(project: Project, path: String, moduleTypes: Array<XdmModuleType>?): VirtualFile? {
        return project.sourceFolders(rootType).map { folder ->
            folder.file?.let { findFileByPath(it, path, moduleTypes) }
        }.filterNotNull().firstOrNull()
    }

    override fun resolve(path: XpmModulePath, context: VirtualFile?): PsiElement? = when (path) {
        is XpmModuleLocationPath -> {
            if (rootType === JavaSourceRootType.SOURCE || rootType === context?.getSourceRootType(path.project)) {
                if (path.isResource == null) // BaseX reverse domain name module path
                    findFileByPath(path.project, path.path, path.moduleTypes)?.toPsiFile(path.project)
                else
                    findFileByPath(path.project, path.path, null)?.toPsiFile(path.project)
            } else
                null
        }
        else -> null
    }

    override fun context(path: XpmModulePath, context: VirtualFile?): XpmStaticContext? = when (path) {
        is XpmModuleLocationPath -> resolve(path, context) as? XpmStaticContext
        else -> null
    }

    override fun relativePathTo(file: VirtualFile, project: Project): String? {
        return project.sourceFolders(rootType).map { folder ->
            folder.file?.relativePathTo(file)
        }.filterNotNull().firstOrNull()
    }

    // endregion
    // region XpmModuleLoaderFactory

    companion object : XpmModuleLoaderFactory {
        override fun loader(context: String?): XpmModuleLoader? = when (context) {
            "java:source" -> JspModuleSourceRootLoader(JavaSourceRootType.SOURCE)
            "java:test-source" -> JspModuleSourceRootLoader(JavaSourceRootType.TEST_SOURCE)
            else -> null
        }
    }

    // endregion
}
