/*
 * Copyright (C) 2016-2018, 2020-2021 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.psi.*
import uk.co.reecedunn.intellij.plugin.xdm.module.path.XdmModuleType
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsAnyUri
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathComment
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathExpr
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.psi.enclosedExpressionBlocks
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCompNamespaceConstructor
import java.util.*

class XQueryCompNamespaceConstructorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryCompNamespaceConstructor,
    XpmSyntaxValidationElement {
    companion object {
        private val NAMESPACE_PREFIX = Key.create<Optional<XsNCNameValue>>("NAMESPACE_PREFIX")
        private val NAMESPACE_URI = Key.create<Optional<XsAnyUriValue>>("NAMESPACE_URI")
    }
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(NAMESPACE_PREFIX)
        clearUserData(NAMESPACE_URI)
    }

    // endregion
    // region XdmNamespaceNode

    override val namespacePrefix: XsNCNameValue?
        get() = computeUserDataIfAbsent(NAMESPACE_PREFIX) {
            var prefix = firstChild
            prefix = prefix.nextSiblingIfSelf { it.elementType === XPathTokenType.K_NAMESPACE }
            prefix = prefix.nextSiblingWhileSelf { it.elementType === XPathTokenType.WHITE_SPACE || it is XPathComment }
            if (prefix is XPathNCName) return@computeUserDataIfAbsent Optional.ofNullable(prefix.localName)

            prefix = prefix.nextSiblingIfSelf { it.elementType === XPathTokenType.BLOCK_OPEN }
            prefix = prefix.nextSiblingWhileSelf { it.elementType === XPathTokenType.WHITE_SPACE || it is XPathComment }
            when (prefix) {
                is XPathNCName -> Optional.ofNullable(prefix.localName)
                is XsStringValue -> Optional.of(XsNCName(prefix.data.trim(), prefix.element))
                is XpmExpression -> Optional.empty() // Can't compute the expression statically.
                else -> Optional.of(XsNCName("", this)) // Empty EnclosedPrefixExpr.
            }
        }.orElse(null)

    override val namespaceUri: XsAnyUriValue?
        get() = computeUserDataIfAbsent(NAMESPACE_URI) {
            var uri = lastChild
            uri = uri.prevSiblingIfSelf { it.elementType === XPathTokenType.BLOCK_CLOSE }
            uri = uri.prevSiblingWhileSelf { it.elementType === XPathTokenType.WHITE_SPACE || it is XPathComment }
            when (uri) {
                is XsStringValue -> Optional.of(
                    XsAnyUri(uri.data, XdmUriContext.Namespace, XdmModuleType.NONE, this)
                )
                is XpmExpression -> Optional.empty() // Can't compute the expression statically.
                else -> Optional.of( // Empty EnclosedURIExpr.
                    XsAnyUri("", XdmUriContext.Namespace, XdmModuleType.NONE, this)
                )
            }
        }.orElse(null)

    override val parentNode: XdmNode?
        get() = when (val parent = parent) {
            is XdmElementNode -> parent
            is XPathExpr -> parent.parent as? XdmElementNode
            else -> null
        }

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement? = null

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = when (val expr = enclosedExpressionBlocks.find { it.isEmpty }) {
            null -> firstChild
            else -> expr.open
        }

    // endregion
}
