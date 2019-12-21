/*
 * Copyright (C) 2016, 2018-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

class XQueryDefaultNamespaceDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathDefaultNamespaceDeclaration,
    XQueryDefaultNamespaceDecl {
    // region XPathDefaultNamespaceDeclaration

    override val namespacePrefix: XsNCNameValue? = null

    override val namespaceUri
        get(): XsAnyUriValue? = children().filterIsInstance<XsAnyUriValue>().filterNotNull().firstOrNull()

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun accepts(namespaceType: XPathNamespaceType): Boolean {
        return children().map { child ->
            when (child.node.elementType) {
                XPathTokenType.K_ELEMENT -> namespaceType === XPathNamespaceType.DefaultElementOrType
                XPathTokenType.K_FUNCTION -> {
                    namespaceType === XPathNamespaceType.DefaultFunctionDecl ||
                    namespaceType === XPathNamespaceType.DefaultFunctionRef
                }
                else -> null
            }
        }.filterNotNull().first()
    }

    // endregion
}
