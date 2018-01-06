/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.model.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDefaultNamespaceDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDefaultNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver

class XQueryPrologPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryProlog,
        XQueryVariableResolver,
        XdmStaticContext {

    override fun subtreeChanged() {
        super.subtreeChanged()
        defaultElementOrTypeNamespaceDecl.invalidate()
        defaultFunctionNamespaceDecl.invalidate()
    }

    override fun resolveVariable(name: XPathEQName?): XQueryVariable? {
        return children().reversed().filterIsInstance<XQueryVariableResolver>().map { resolver ->
            resolver.resolveVariable(name)
        }.filterNotNull().firstOrNull()
    }

    override fun defaultNamespace(context: QNameContext): Sequence<XdmLexicalValue> {
        return when (context) {
            QNameContext.Element,
            QNameContext.Type ->
                defaultElementOrTypeNamespaceDecl.get()!!.map { decl -> decl.defaultValue!! }
            QNameContext.Function ->
                defaultFunctionNamespaceDecl.get()!!.map { decl -> decl.defaultValue!! }
        }
    }

    private val defaultElementOrTypeNamespaceDecl = CacheableProperty {
        children().reversed()
                .filterIsInstance<XQueryDefaultNamespaceDecl>()
                .filter { decl -> decl.type == XQueryDefaultNamespaceType.ElementOrType && decl.defaultValue != null }
                .filterNotNull() `is` Cacheable
    }

    private val defaultFunctionNamespaceDecl = CacheableProperty {
        children().reversed()
                .filterIsInstance<XQueryDefaultNamespaceDecl>()
                .filter { decl -> decl.type == XQueryDefaultNamespaceType.Function && decl.defaultValue != null }
                .filterNotNull() `is` Cacheable
    }
}
