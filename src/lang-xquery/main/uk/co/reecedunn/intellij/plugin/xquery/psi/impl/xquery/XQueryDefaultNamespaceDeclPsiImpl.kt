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
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpm.namespace.XpmDefaultNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xdm.namespaces.XdmNamespaceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*

class XQueryDefaultNamespaceDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XpmDefaultNamespaceDeclaration,
    XQueryDefaultNamespaceDecl {
    // region XdmDefaultNamespaceDeclaration

    override val namespacePrefix: XsNCNameValue? = null

    override val namespaceUri: XsAnyUriValue?
        get() = children().filterIsInstance<XsAnyUriValue>().filterNotNull().firstOrNull()

    @Suppress("Reformat") // Kotlin formatter bug: https://youtrack.jetbrains.com/issue/KT-22518
    override fun accepts(namespaceType: XdmNamespaceType): Boolean {
        return children().map { child ->
            when (child.elementType) {
                XPathTokenType.K_ELEMENT -> namespaceType === XdmNamespaceType.DefaultElementOrType
                XPathTokenType.K_FUNCTION -> {
                    namespaceType === XdmNamespaceType.DefaultFunctionDecl ||
                    namespaceType === XdmNamespaceType.DefaultFunctionRef
                }
                else -> null
            }
        }.filterNotNull().first()
    }

    // endregion
}
