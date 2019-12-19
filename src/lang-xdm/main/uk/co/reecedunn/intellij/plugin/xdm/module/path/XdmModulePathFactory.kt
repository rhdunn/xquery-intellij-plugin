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
package uk.co.reecedunn.intellij.plugin.xdm.module.path

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.context.XdmStaticContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.loader.XdmModuleLoaderSettings

interface XdmModulePathFactory {
    companion object {
        val EP_NAME = ExtensionPointName.create<XdmModulePathFactory>("uk.co.reecedunn.intellij.modulePathFactory")
    }

    fun create(project: Project, uri: XsAnyUriValue): XdmModulePath?
}

fun XsAnyUriValue.paths(project: Project): Sequence<XdmModulePath> {
    return XdmModulePathFactory.EP_NAME.extensions.asSequence().map { it.create(project, this) }.filterNotNull()
}

fun XsAnyUriValue.resolve(project: Project): PsiElement? {
    val loaders = XdmModuleLoaderSettings.getInstance(project)
    return paths(project).map { loaders.resolve(it) }.filterNotNull().firstOrNull()
}

fun XsAnyUriValue.context(project: Project): XdmStaticContext? {
    val loaders = XdmModuleLoaderSettings.getInstance(project)
    return paths(project).map { loaders.context(it) }.filterNotNull().firstOrNull()
}
