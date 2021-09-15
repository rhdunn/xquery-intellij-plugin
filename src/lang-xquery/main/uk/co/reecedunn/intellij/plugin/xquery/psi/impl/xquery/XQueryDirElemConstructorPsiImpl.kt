/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceService
import com.intellij.psi.TokenType
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.nextSiblingIfSelf
import uk.co.reecedunn.intellij.plugin.core.psi.prevSiblingIfSelf
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.filterExpressions
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathExpr
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.enclosedExpressionBlocks
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryDirElemConstructor
import uk.co.reecedunn.intellij.plugin.xquery.lexer.XQueryTokenType
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import java.util.*

class XQueryDirElemConstructorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XQueryDirElemConstructor,
    XpmSyntaxValidationElement {
    companion object {
        private val STRING_VALUE = Key.create<Optional<String>>("STRING_VALUE")

        private fun isNamespaceDeclaration(nodeName: XsQNameValue?): Boolean = when {
            nodeName?.prefix?.data == "xmlns" -> true // xmlns:*
            nodeName?.localName?.data == "xmlns" && nodeName.prefix == null -> true // xmlns
            else -> false
        }
    }
    // region HintedReferenceHost

    override fun getReference(): PsiReference? {
        val references = references
        return if (references.isEmpty()) null else references[0]
    }

    override fun getReferences(): Array<PsiReference> = getReferences(PsiReferenceService.Hints.NO_HINTS)

    override fun getReferences(hints: PsiReferenceService.Hints): Array<PsiReference> {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this, hints)
    }

    override fun shouldAskParentForReferences(hints: PsiReferenceService.Hints): Boolean = false

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement
        get() = this

    // endregion
    // region XdmElementNode

    override val attributes: Sequence<XdmAttributeNode>
        get() = filterExpressions<XdmAttributeNode>().filter { !isNamespaceDeclaration(it.nodeName) }

    override val nodeName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val parentNode: XdmNode?
        get() = when (val parent = parent) {
            is XdmNode -> parent
            is XPathExpr -> parent.parent as? XdmNode
            else -> null
        }

    override val stringValue: String?
        get() = computeUserDataIfAbsent(STRING_VALUE) {
            val value = StringBuilder()
            children().forEach { child ->
                when (child) {
                    is XdmTextNode -> value.append(child.stringValue ?: "")
                    is XdmElementNode -> {
                        val stringValue = child.stringValue ?: return@computeUserDataIfAbsent Optional.empty()
                        value.append(stringValue)
                    }
                    is XpmExpression -> return@computeUserDataIfAbsent Optional.empty()
                    else -> {
                    }
                }
            }
            Optional.of(value.toString())
        }.orElse(null)

    override val namespaceAttributes: Sequence<XdmAttributeNode>
        get() = filterExpressions<XdmAttributeNode>().filter { isNamespaceDeclaration(it.nodeName) }

    // endregion
    // region XQueryDirElemConstructor

    override val closingTag: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().lastOrNull()

    override val dirAttributeListStartElement: PsiElement
        get() {
            var start: PsiElement = firstChild
            start = start.nextSiblingIfSelf { it.elementType === XQueryTokenType.OPEN_XML_TAG }
            start = start.nextSiblingIfSelf { it.elementType === TokenType.ERROR_ELEMENT } // whitespace
            start = start.nextSiblingIfSelf { it.elementType in XQueryElementType.XML_NAME }
            start = start.nextSiblingIfSelf { it.elementType === XQueryTokenType.XML_WHITE_SPACE }
            return start
        }

    override val dirElemContentStartElement: PsiElement
        get() {
            var start: PsiElement = dirAttributeListStartElement
            while (start.elementType === XQueryElementType.DIR_ATTRIBUTE) {
                start = start.nextSibling ?: return start
                start = start.nextSiblingIfSelf { it.elementType === XQueryTokenType.XML_WHITE_SPACE }
            }
            start = start.nextSiblingIfSelf { it.elementType === XQueryTokenType.END_XML_TAG }
            return start
        }

    override val dirElemContentEndElement: PsiElement
        get() {
            var end: PsiElement = lastChild
            end = end.prevSiblingIfSelf { it.elementType === XQueryTokenType.END_XML_TAG }
            end = end.prevSiblingIfSelf { it.elementType === XQueryTokenType.XML_WHITE_SPACE }
            end = end.prevSiblingIfSelf { it.elementType in XQueryElementType.XML_NAME }
            end = end.prevSiblingIfSelf { it.elementType === TokenType.ERROR_ELEMENT } // whitespace
            return end
        }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = when (val expr = enclosedExpressionBlocks.find { it.isEmpty }) {
            null -> firstChild
            else -> expr.open
        }

    // endregion
}
