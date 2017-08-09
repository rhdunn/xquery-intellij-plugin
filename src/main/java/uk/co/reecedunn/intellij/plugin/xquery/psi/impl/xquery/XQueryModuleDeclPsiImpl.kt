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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.extensions.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModuleDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryNCName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

class XQueryModuleDeclPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryModuleDecl, XQueryNamespaceResolver, XQueryPrologResolver {
    val namespace get(): XQueryNamespace? {
        return children().filterIsInstance<XQueryNCName>().map { name -> name.localName }.map { localName ->
            val element = findChildByType<PsiElement>(XQueryElementType.URI_LITERAL)
            XQueryNamespace(localName, element, this)
        }.firstOrNull()
    }

    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        val ns = namespace
        return if (ns?.prefix?.text == prefix) ns else null
    }

    override val prolog get(): XQueryProlog? =
        children().filterIsInstance<XQueryProlog>().firstOrNull()
}
