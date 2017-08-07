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
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

class XQueryModuleImportPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryModuleImport, XQueryNamespaceResolver, XQueryPrologResolver {
    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        val name = findChildByType<XQueryNCName>(XQueryElementType.NCNAME) ?: return null
        val localName = name.localName
        if (localName?.text == prefix) {
            val element = findChildByType<PsiElement>(XQueryElementType.URI_LITERAL)
            return XQueryNamespace(localName, element, this)
        }
        return null
    }

    override fun resolveProlog(): XQueryProlog? {
        return children().filterIsInstance<XQueryUriLiteral>().map { uri ->
            val file = uri.resolveUri()
            if (file is XQueryFile) {
                val module = file.children.filterIsInstance<XQueryModule>().firstOrNull()
                module?.children()?.filterIsInstance<XQueryProlog>()?.firstOrNull()
            } else {
                null
            }
        }.filterNotNull().firstOrNull()
    }
}
