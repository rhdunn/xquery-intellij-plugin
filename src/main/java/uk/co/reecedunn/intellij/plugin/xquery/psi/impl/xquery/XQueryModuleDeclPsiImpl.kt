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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmLexicalValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryModuleDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryProlog
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryUriLiteral
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryPrologResolver

class XQueryModuleDeclPsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XQueryModuleDecl,
        XQueryNamespaceResolver,
        XQueryPrologResolver,
        XdmNamespaceDeclaration {
    // region XdmNamespaceDeclaration

    override val namespacePrefix get(): XdmLexicalValue? =
        children().filterIsInstance<XPathNCName>().firstOrNull()?.localName as? XdmLexicalValue

    override val namespaceUri get(): XdmLexicalValue? =
        children().filterIsInstance<XQueryUriLiteral>().firstOrNull() as? XdmLexicalValue

    // endregion
    // region XQueryNamespaceResolver

    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        val ns = children().filterIsInstance<XPathNCName>().map { name -> name.localName }.map { localName ->
            val element = findChildByType<PsiElement>(XQueryElementType.URI_LITERAL)
            XQueryNamespace(localName, element, this)
        }.firstOrNull()
        return if (ns?.prefix?.text == prefix) ns else null
    }

    // endregion
    // region XQueryPrologResolver

    override val prolog get(): XQueryProlog? =
        siblings().filterIsInstance<XQueryProlog>().firstOrNull()

    // endregion
}
