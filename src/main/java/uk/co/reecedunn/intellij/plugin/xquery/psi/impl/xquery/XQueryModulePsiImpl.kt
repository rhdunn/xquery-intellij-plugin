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
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.descendants
import uk.co.reecedunn.intellij.plugin.core.vfs.ResourceVirtualFile
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.lang.*
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver
import uk.co.reecedunn.intellij.plugin.xquery.settings.XQueryProjectSettings

open class XQueryModulePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryModule, XQueryNamespaceResolver, XQueryPrologResolver {
    private val settings: XQueryProjectSettings
    init {
        settings = XQueryProjectSettings.getInstance(project)
    }

    private var product: Product? = null
    private var productVersion: Version? = null
    private var xquery: Specification? = null

    private var staticContextCache: XQueryProlog? = null
    private val staticContext get(): XQueryProlog? {
        val version: Specification = (containingFile as XQueryFile).XQueryVersion.getVersionOrDefault(project)
        if (product !== settings.product || productVersion !== settings.productVersion || xquery !== version) {
            product = settings.product
            productVersion = settings.productVersion
            xquery = version

            var context = product?.implementation?.staticContext(product, productVersion, xquery)
            if (context == null) context = defaultStaticContext(xquery)

            val file = ResourceVirtualFile.resolve(context, project)
            staticContextCache = ((file as? XQueryFile)?.modules?.firstOrNull() as? XQueryPrologResolver)?.prolog
        }
        return staticContextCache
    }

    override val XQueryVersion get(): XQueryVersionRef {
        val versionDecl = descendants().filterIsInstance<XQueryVersionDecl>().firstOrNull()
        val version: XPathStringLiteral? = versionDecl?.version
        val xquery: Specification? = XQuery.versionsForXQuery((version as? XdmLexicalValue)?.lexicalRepresentation).firstOrNull()
        return XQueryVersionRef(version, xquery)
    }

    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? =
        (staticContext as? XQueryNamespaceResolver)?.resolveNamespace(prefix)

    override val prolog get(): XQueryProlog? =
        children().filterIsInstance<XQueryProlog>().firstOrNull()
}
