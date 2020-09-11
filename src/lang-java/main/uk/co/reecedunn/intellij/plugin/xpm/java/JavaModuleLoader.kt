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
package uk.co.reecedunn.intellij.plugin.xpm.java

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xpm.context.XpmStaticContext
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoader
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xpm.module.loader.XpmModuleLoaderFactory

object JavaModuleLoader : XpmModuleLoaderFactory, XpmModuleLoader {
    // region XpmModuleLoaderFactory

    override fun loader(context: String?): XpmModuleLoader = this

    // endregion
    // region XpmModuleLoader

    override fun resolve(path: XpmModulePath, context: VirtualFile?): PsiElement? = when (path) {
        is JavaModulePath -> JavaTypePath.getInstance(path.project).findClass(path.classPath)
        else -> null
    }

    override fun context(path: XpmModulePath, context: VirtualFile?): XpmStaticContext? = when (path) {
        is JavaTypePath -> path
        else -> null
    }

    override fun relativePathTo(file: VirtualFile, project: Project): String? = null

    // endregion
}
