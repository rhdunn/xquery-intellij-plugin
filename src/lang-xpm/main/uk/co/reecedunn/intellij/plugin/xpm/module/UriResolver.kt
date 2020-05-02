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
package uk.co.reecedunn.intellij.plugin.xpm.module

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.element

private val STATIC_IMPORT_RESOLVERS get() = ImportPathResolver.EP_NAME.extensions.asSequence()

fun <T : PsiFile> XsAnyUriValue.resolveUri(): T? = this.resolveUri(element!!.project)

fun <T : PsiFile> XsAnyUriValue.resolveUri(project: Project): T? {
    val path = data
    return STATIC_IMPORT_RESOLVERS
        .filter { resolver -> resolver.match(path) }
        .map { resolver ->
            @Suppress("UNCHECKED_CAST")
            resolver.resolve(path)?.toPsiFile(project) as? T?
        }
        .filterNotNull().firstOrNull()
}
