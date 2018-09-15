/*
 * Copyright (C) 2016, 2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathDefaultNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDefaultNamespaceDecl
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryUriLiteral
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType

class XQueryDefaultNamespaceDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathDefaultNamespaceDeclaration,
    XQueryDefaultNamespaceDecl {

    override val namespacePrefix: XsNCNameValue? = null

    override val namespaceUri
        get(): XsAnyUriValue? {
            return children().filterIsInstance<XQueryUriLiteral>().map { uri ->
                uri.value as? XsAnyUriValue
            }.filterNotNull().firstOrNull()
        }

    override val namespaceType
        get(): XPathNamespaceType {
            return children().map { child ->
                when (child.node.elementType) {
                    XQueryTokenType.K_ELEMENT -> XPathNamespaceType.DefaultElementOrType
                    XQueryTokenType.K_FUNCTION -> XPathNamespaceType.DefaultFunction
                    else -> null
                }
            }.filterNotNull().first()
        }
}
