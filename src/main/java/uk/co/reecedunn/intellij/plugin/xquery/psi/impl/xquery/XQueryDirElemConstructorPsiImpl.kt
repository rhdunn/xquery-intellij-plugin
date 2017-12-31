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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespace
import uk.co.reecedunn.intellij.plugin.xquery.psi.XQueryNamespaceResolver

class XQueryDirElemConstructorPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XQueryDirElemConstructor, XQueryNamespaceResolver {
    override val openTag get(): XPathEQName? =
        findChildByClass(XPathEQName::class.java)

    override val closeTag get(): XPathEQName? {
        val tag = findChildrenByClass(XPathEQName::class.java)
        return if (tag.size == 2) tag[1] else null
    }

    override val isSelfClosing get(): Boolean =
        lastChild.node.elementType === XQueryTokenType.SELF_CLOSING_XML_TAG

    override fun resolveNamespace(prefix: CharSequence?): XQueryNamespace? {
        val element = findChildByType<PsiElement>(XQueryElementType.DIR_ATTRIBUTE_LIST)
        return (element as? XQueryNamespaceResolver)?.resolveNamespace(prefix)
    }
}
