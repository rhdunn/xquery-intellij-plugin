// Copyright (C) 2019-2020, 2025 Reece H. Dunn. SPDX-License-Identifier: Apache-2.0
package uk.co.reecedunn.intellij.plugin.xpm.java

import com.intellij.openapi.project.Project
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.core.text.pascalCase
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePath
import uk.co.reecedunn.intellij.plugin.xpm.module.path.XpmModulePathFactory

/**
 * BaseX, eXist-db, and Saxon allow declaring a namespace to a Java class.
 */
class JavaModulePath internal constructor(
    val project: Project,
    val classPath: String,
    val voidThis: Boolean
) : XpmModulePath {
    override val moduleTypes: Array<XdmModuleType> = XdmModuleType.JAVA

    companion object : XpmModulePathFactory {
        private val SPECIAL_CHARACTERS = "[^\\w.-/]".toRegex()

        private fun createJava(project: Project, path: String): JavaModulePath = when {
            // Saxon 9.7
            path.endsWith("?void=this") -> JavaModulePath(project, path.substring(5, path.length - 10), true)
            // BaseX, eXist-db, Saxon
            else -> JavaModulePath(project, path.substring(5), false)
        }

        private fun createJava(project: Project, path: String, modulePaths: List<String>): JavaModulePath {
            val module = modulePaths.last().pascalCase()
            return when (modulePaths.size) {
                1 -> JavaModulePath(project, "$path.$module", false)
                else -> JavaModulePath(project, "$path.${modulePaths.dropLast(1).joinToString(".")}.$module", false)
            }
        }

        private fun createUri(project: Project, path: String): JavaModulePath? {
            val parts = path.substringAfter("://").nullize()?.split('/') ?: return null
            val rdn = parts[0].split('.').reversed()
            val rest = parts.drop(1)
            return when {
                rest.isEmpty() -> createRelative(project, listOf(rdn, listOf("")).flatten())
                else -> createRelative(project, listOf(rdn, rest).flatten())
            }
        }

        private fun createUrn(project: Project, path: String): JavaModulePath? {
            return createRelative(project, path.split(':').map { it.replace('/', '.') })
        }

        private fun createRelative(project: Project, paths: List<String>): JavaModulePath? {
            val path = paths.map { it.replace(SPECIAL_CHARACTERS, "-") }
            return when {
                path.size == 1 && path.last().isEmpty() -> null
                path.last().isEmpty() -> JavaModulePath(project, "${path.joinToString(".")}Index", false)
                else -> createJava(project, path.dropLast(1).joinToString("."), path.last().split('.'))
            }
        }

        override fun create(project: Project, uri: XsAnyUriValue): JavaModulePath? = when (uri.context) {
            XdmUriContext.Namespace, XdmUriContext.TargetNamespace, XdmUriContext.NamespaceDeclaration -> {
                val path = uri.data
                when {
                    path.isEmpty() -> null
                    path.startsWith("java:") -> createJava(project, path) // BaseX, eXist-db, Saxon
                    path.startsWith("xmldb:exist://") -> null // Ignore eXist-db database paths.
                    path.startsWith("file://") -> null // Don't map file URLs to Java namespaces.
                    path.contains("://") -> createUri(project, path) // BaseX
                    path.contains(":") -> createUrn(project, path) // BaseX
                    path.startsWith("/") -> createRelative(project, path.substring(1).split('/')) // BaseX
                    path.contains("/") -> createRelative(project, path.split('/')) // BaseX
                    else -> JavaModulePath(project, path, false) // BaseX, Saxon
                }
            }
            else -> null
        }
    }
}
