/*
 * Copyright (C) 2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xdm.java

import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.context.XdmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.module.loader.XdmModuleLoader
import uk.co.reecedunn.intellij.plugin.xdm.module.loader.XdmModuleLoaderFactory
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath

object JavaModuleLoader : XdmModuleLoaderFactory, XdmModuleLoader {
    // region XdmModuleLoaderFactory

    override fun loader(context: String?): XdmModuleLoader = this

    // endregion
    // region XdmModuleLoader

    override fun resolve(path: XdmModulePath, context: PsiElement): PsiElement? {
        return when (path) {
            is JavaModulePath -> JavaTypePath.getInstance(path.project).findClass(path.classPath)
            else -> null
        }
    }

    override fun context(path: XdmModulePath, context: PsiElement): XdmStaticContext? {
        return when (path) {
            is JavaTypePath -> path
            else -> null
        }
    }

    // endregion
}
