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
package uk.co.reecedunn.intellij.plugin.xpm.module.path.impl

import com.intellij.openapi.project.Project
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue

object XpmReverseDomainNameModulePath : XpmModulePathFactory {
    private val SPECIAL_CHARACTERS = "[^\\w.-/]".toRegex()

    private fun createUri(project: Project, path: String, uri: XsAnyUriValue): XpmModuleLocationPath? {
        val parts = path.substringAfter("://").nullize()?.split('/') ?: return null
        val rdn = parts[0].split('.').reversed()
        val rest = parts.drop(1).map { it.replace('.', '/') }
        return when {
            rest.isEmpty() -> createRelative(project, "${rdn.joinToString("/")}/", uri)
            else -> createRelative(project, listOf(rdn, rest).flatten().joinToString("/"), uri)
        }
    }

    private fun createUrn(project: Project, path: String, uri: XsAnyUriValue): XpmModuleLocationPath? {
        return createRelative(project, path.replace(':', '/'), uri)
    }

    private fun createRelative(project: Project, path: String, uri: XsAnyUriValue): XpmModuleLocationPath? = when {
        path.isEmpty() -> null
        path.endsWith('/') -> {
            XpmModuleLocationPath(project, "${path.replace(SPECIAL_CHARACTERS, "-")}index", uri.moduleTypes, null)
        }
        else -> XpmModuleLocationPath(project, path.replace(SPECIAL_CHARACTERS, "-"), uri.moduleTypes, null)
    }

    override fun create(project: Project, uri: XsAnyUriValue): XpmModuleLocationPath? = when (uri.context) {
        XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
            val path = uri.data
            when {
                path.startsWith("java:") /* Java paths are not converted by BaseX. */ -> null
                path.startsWith("xmldb:exist://") /* Ignore eXist-db database paths. */ -> null
                path.startsWith("file://") /* Keep file URLs intact. */ -> {
                    XpmModuleLocationPath(project, path, uri.moduleTypes, null)
                }
                path.contains("://") /* BaseX */ -> createUri(project, path, uri)
                path.contains(":") /* BaseX */ -> createUrn(project, path, uri)
                else /* BaseX */ -> createRelative(project, path, uri)
            }
        }
        else -> null
    }
}
