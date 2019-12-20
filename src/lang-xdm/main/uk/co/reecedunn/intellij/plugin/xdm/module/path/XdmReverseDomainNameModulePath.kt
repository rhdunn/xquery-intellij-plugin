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

import com.intellij.openapi.project.Project
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue

object XdmReverseDomainNameModulePath : XdmModulePathFactory {
    private val SPECIAL_CHARACTERS = "[^\\w.-/]".toRegex()

    private fun createUri(project: Project, path: String): XdmModuleLocationPath? {
        val parts = path.substringAfter("://").nullize()?.split('/') ?: return null
        val rdn = parts[0].split('.').reversed()
        val rest = parts.drop(1).map { it.replace('.', '/') }
        return when {
            rest.isEmpty() -> createRelative(project, "${rdn.joinToString("/")}/")
            else -> createRelative(project, listOf(rdn, rest).flatten().joinToString("/"))
        }
    }

    private fun createUrn(project: Project, path: String): XdmModuleLocationPath? {
        return createRelative(project, path.replace(':', '/'))
    }

    private fun createRelative(project: Project, path: String): XdmModuleLocationPath? {
        return when {
            path.isEmpty() -> null
            path.endsWith('/') -> XdmModuleLocationPath(project, "${path.replace(SPECIAL_CHARACTERS, "-")}index", null)
            else -> XdmModuleLocationPath(project, path.replace(SPECIAL_CHARACTERS, "-"), null)
        }
    }

    override fun create(project: Project, uri: XsAnyUriValue): XdmModuleLocationPath? {
        return when (uri.context) {
            XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                val path = uri.data
                when {
                    path.startsWith("java:") -> null // Java paths are not converted by BaseX.
                    path.startsWith("xmldb:exist://") -> null // Ignore eXist-db database paths.
                    path.startsWith("file://") -> XdmModuleLocationPath(project, path, null) // Keep file URLs intact.
                    path.contains("://") -> createUri(project, path) // BaseX
                    path.contains(":") -> createUrn(project, path) // BaseX
                    else -> createRelative(project, path) // BaseX
                }
            }
            else -> null
        }
    }
}
