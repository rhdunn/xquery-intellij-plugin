/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xpath.model

import com.intellij.psi.PsiFile
import org.jetbrains.jps.model.java.JavaSourceRootType
import uk.co.reecedunn.intellij.plugin.core.roots.getSourceRootType
import uk.co.reecedunn.intellij.plugin.core.vfs.toPsiFile

private val STATIC_IMPORT_RESOLVERS by lazy {
    sequenceOf(
        sequenceOf(EmptyPathImportResolver),
        ImportPathResolver.IMPORT_PATH_RESOLVER_EP.extensions.asSequence()
    ).flatten()
}

fun <T : PsiFile> XsAnyUriValue.resolveUri(httpOnly: Boolean = false): T? {
    val path = data
    val project = element!!.project
    val resolvers =
        if (httpOnly)
            STATIC_IMPORT_RESOLVERS
        else {
            val file = element!!.containingFile.virtualFile
            sequenceOf(
                STATIC_IMPORT_RESOLVERS,
                moduleRootImportResolvers(
                    project,
                    JavaSourceRootType.SOURCE
                ),
                if (file.getSourceRootType(project) === JavaSourceRootType.TEST_SOURCE)
                    moduleRootImportResolvers(
                        project,
                        JavaSourceRootType.TEST_SOURCE
                    )
                else
                    emptySequence(),
                sequenceOf(RelativeFileImportResolver(file))
            ).flatten()
        }
    return resolvers
        .filter { resolver -> resolver.match(path) }
        .map { resolver -> resolver.resolve(path)?.toPsiFile<T>(project) }
        .filterNotNull().firstOrNull()
}
