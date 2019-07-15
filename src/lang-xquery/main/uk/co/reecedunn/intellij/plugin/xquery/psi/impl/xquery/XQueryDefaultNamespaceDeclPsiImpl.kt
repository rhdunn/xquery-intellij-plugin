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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathUriLiteral
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.*
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.*
import uk.co.reecedunn.intellij.plugin.xquery.model.XQueryPrologResolver

class XQueryDefaultNamespaceDeclPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathDefaultNamespaceDeclaration,
    XQueryPrologResolver,
    XQueryDefaultNamespaceDecl {
    // region XQueryPrologResolver

    override val prolog: Sequence<XQueryProlog>
        get() {
            val file = namespaceUri?.resolveUri<XQueryModule>(true)
            val library = file?.children()?.filterIsInstance<XQueryLibraryModule>()?.firstOrNull()
            return (library as? XQueryPrologResolver)?.prolog ?: emptySequence()
        }

    // endregion
    // region XPathDefaultNamespaceDeclaration

    override val namespacePrefix: XsNCNameValue? = null

    override val namespaceUri
        get(): XsAnyUriValue? {
            return children().filterIsInstance<XPathUriLiteral>().map { uri ->
                uri.value as? XsAnyUriValue
            }.filterNotNull().firstOrNull()
        }

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
