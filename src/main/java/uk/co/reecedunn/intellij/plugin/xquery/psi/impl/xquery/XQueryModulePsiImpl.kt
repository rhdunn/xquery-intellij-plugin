/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.xquery

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryFile
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModule
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.lang.Specification
import uk.co.reecedunn.intellij.plugin.xquery.lang.XQuery
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

open class XQueryModulePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryModule, XQueryNamespaceResolver, XQueryPrologResolver {
    private val settings: XQueryProjectSettings
    private var staticContext: XQueryNamespaceResolver? = null
    private var dialectId = ""

    init {
        settings = XQueryProjectSettings.getInstance(project)
    }

    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        val xqueryVersion = (containingFile as XQueryFile).XQueryVersion.getVersionOrDefault(project)
        val version = XQuery.versions.find { v -> (v as Specification).label == xqueryVersion.toString() }
        val dialect = settings.getDialectForXQueryVersion(version!!)
        if (dialect.id != dialectId) {
            dialectId = dialect.id
            staticContext = null

            val project = project
            val file = ResourceVirtualFile.resolve(dialect.staticContext, project)
            if (file is XQueryFile) {
                staticContext = (file.module as? XQueryPrologResolver)?.prolog as XQueryNamespaceResolver
            }
        }

        if (staticContext != null) {
            return staticContext?.resolveNamespace(prefix)
        }
        return null
    }

    override val prolog get(): XQueryProlog? =
        children().filterIsInstance<XQueryProlog>().firstOrNull()
}
