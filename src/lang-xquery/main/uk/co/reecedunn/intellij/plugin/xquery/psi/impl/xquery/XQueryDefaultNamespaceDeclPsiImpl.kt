/*
 * Copyright (C) 2016, 2018-2021 Reece H. Dunn
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
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.namespace.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDefaultNamespaceDecl
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryDefaultNamespaceDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryDefaultNamespaceDecl,
    XpmSyntaxValidationElement {
    // region XdmNamespaceNode

    override val namespacePrefix: XsNCNameValue? = null

    override val namespaceUri: XsAnyUriValue?
        get() = children().filterIsInstance<XsAnyUriValue>().firstOrNull()

    override val parentNode: XdmNode? = null

    // endregion
    // region XpmDefaultNamespaceDeclaration

    override fun accepts(namespaceType: XdmNamespaceType): Boolean = when (conformanceElement.elementType) {
        XPathTokenType.K_ELEMENT -> when (namespaceType) {
            XdmNamespaceType.DefaultElement, XdmNamespaceType.DefaultType -> true
            else -> false
        }
        XPathTokenType.K_FUNCTION -> when (namespaceType) {
            XdmNamespaceType.DefaultFunctionDecl, XdmNamespaceType.DefaultFunctionRef -> true
            else -> false
        }
        else -> false
    }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(XQueryTokenType.DEFAULT_NAMESPACE_TOKENS) ?: firstChild

    // endregion
}
