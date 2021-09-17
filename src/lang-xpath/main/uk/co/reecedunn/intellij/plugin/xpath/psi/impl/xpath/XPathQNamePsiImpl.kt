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
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.search.SearchScope
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathQNamePrefixReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathVariableNameReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference

class XPathQNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathQName {
    // region XsQNameValue

    private val names: Sequence<XsNCNameValue>
        get() = children().filterIsInstance<XsNCNameValue>()

    override val namespace: XsAnyUriValue? = null

    override val prefix: XsNCNameValue
        get() = names.first()

    override val localName: XsNCNameValue?
        get() = names.toList().let { if (it.size == 2) it[1] else null }

    override val isLexicalQName: Boolean = true

    // endregion
    // region PsiElement

    override fun getUseScope(): SearchScope = parent.useScope

    override fun getReference(): PsiReference? {
        val references = references
        return if (references.isEmpty()) null else references[0]
    }

    @Suppress("DuplicatedCode") // Same logic in XPathURIQualifiedNamePsiImpl.
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

        val prefix = prefix as? PsiElement
        if (prefix == null) { // local name only
            if (localNameRef != null) {
                return arrayOf(localNameRef)
            }
            return PsiReference.EMPTY_ARRAY
        } else {
            if (localNameRef != null) {
                return arrayOf(
                    XPathQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)),
                    localNameRef
                )
            }
            return arrayOf(XPathQNamePrefixReference(this, prefix.textRange.shiftRight(-eqnameStart)))
        }
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()

    // endregion
    // region PsiNameIdentifierOwner

    override fun getNameIdentifier(): PsiElement? = localName as? PsiElement

    // endregion
    // region PsiNamedElement

    override fun getName(): String? = nameIdentifier?.text

    override fun setName(@NonNls name: String): PsiElement {
        val renamed = createElement<XPathQName>("${prefix.data}:$name") ?: return this
        return replace(renamed)
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = (parent as NavigationItem).presentation

    // endregion
}
