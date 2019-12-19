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

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.text.nullize
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmUriContext
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePath
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModulePathFactory

/**
 * BaseX, eXist-db, and Saxon allow declaring a namespace to a Java class.
 */
class JavaModulePath private constructor(
    val project: Project,
    val classPath: String,
    val voidThis: Boolean
) : XdmModulePath {
    override fun resolve(): PsiElement? = JavaTypePath.getInstance(project).findClass(classPath)

    companion object : XdmModulePathFactory {
        private val SPECIAL_CHARACTERS = "[^\\w.-/]".toRegex()

        private fun createJava(project: Project, path: String): JavaModulePath? {
            return if (path.endsWith("?void=this")) // Saxon 9.7
                JavaModulePath(project, path.substring(5, path.length - 10), true)
            else // BaseX, eXist-db, Saxon
                JavaModulePath(project, path.substring(5), false)
        }

        private fun createJava(project: Project, path: String, modulePaths: List<String>): JavaModulePath? {
            val module = modulePaths.last().split('-').joinToString("") { it.capitalize() }
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

        override fun create(project: Project, uri: XsAnyUriValue): JavaModulePath? {
            return when (uri.context) {
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
}
