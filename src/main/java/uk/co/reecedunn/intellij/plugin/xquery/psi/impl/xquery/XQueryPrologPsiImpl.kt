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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariable
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryVariableResolver

class XQueryPrologPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryProlog, XQueryNamespaceResolver, XQueryVariableResolver {
    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        return children().reversed().filterIsInstance<XQueryNamespaceResolver>().map { resolver ->
            resolver.resolveNamespace(prefix)
        }.filterNotNull().firstOrNull()
    }

    override fun resolveVariable(name: XPathEQName?): XQueryVariable? {
        return children().reversed().filterIsInstance<XQueryVariableResolver>().map { resolver ->
            resolver.resolveVariable(name)
        }.filterNotNull().firstOrNull()
    }
}
