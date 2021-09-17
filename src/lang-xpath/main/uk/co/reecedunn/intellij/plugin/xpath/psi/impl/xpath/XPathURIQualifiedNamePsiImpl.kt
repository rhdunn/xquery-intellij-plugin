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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.SearchScope
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathBracedURILiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathURIQualifiedName
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathBracedURILiteralReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathVariableNameReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference

class XPathURIQualifiedNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathURIQualifiedName {
    // region XsQNameValue

    override val namespace: XsAnyUriValue
        get() = children().find { it is XPathBracedURILiteral } as XsAnyUriValue

    override val prefix: XsNCNameValue? = null

    override val localName: XsNCNameValue?
        get() = children().filterIsInstance<XsNCNameValue>().firstOrNull()

    override val isLexicalQName: Boolean = false

    // endregion
    // region PsiElement

    override fun getUseScope(): SearchScope = parent.useScope

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()

    @Suppress("DuplicatedCode") // Same logic in XPathQNamePsiImpl.
    override fun getReferences(): Array<PsiReference> {
        val eqnameStart = node.startOffset
        val localName = localName as? PsiElement
        val localNameRef: PsiReference? =
            if (localName != null) when (parent) {
                is XpmFunctionReference ->
                    XPathFunctionNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                is XpmVariableReference ->
                    XPathVariableNameReference(this, localName.textRange.shiftRight(-eqnameStart))
                else -> null
            } else {
                null
            }

        val namespace = namespace as XPathBracedURILiteral
        if (localNameRef != null) {
            return arrayOf(
                XPathBracedURILiteralReference(this, TextRange(2, namespace.textRange.length - 1)),
                localNameRef
            )
        }
        return arrayOf(XPathBracedURILiteralReference(this, TextRange(2, namespace.textRange.length - 1)))
    }

    // endregion
    // region PsiNameIdentifierOwner

    override fun getNameIdentifier(): PsiElement? = children().filterIsInstance<XmlNCNameImpl>().firstOrNull()

    // endregion
    // region PsiNamedElement

    override fun getName(): String? = nameIdentifier?.text

    override fun setName(@NonNls name: String): PsiElement {
        val renamed = createElement<XPathURIQualifiedName>("${(namespace as PsiElement).text}$name") ?: return this
        return replace(renamed)
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = (parent as NavigationItem).presentation

    // endregion
}
