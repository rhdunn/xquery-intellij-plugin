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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.reference.XPathVariableNameReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference

class XPathNCNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathNCName {
    // region XsQNameValue

    override val namespace: XsAnyUriValue? = null

    override val prefix: XsNCNameValue? = null

    override val localName: XsNCNameValue?
        get() = children().filterIsInstance<XsNCNameValue>().firstOrNull()

    override val isLexicalQName: Boolean = true

    // endregion
    // region PsiElement

    override fun getUseScope(): SearchScope = parent.useScope

    override fun getReference(): PsiReference? {
        val references = references
        return if (references.isEmpty()) null else references[0]
    }

    override fun getReferences(): Array<PsiReference> {
        return (localName as? PsiElement)?.let {
            when (parent) {
                is XpmFunctionReference -> {
                    val ref = XPathFunctionNameReference(this, it.textRange.shiftRight(-node.startOffset))
                    arrayOf(ref as PsiReference)
                }
                is XpmVariableReference -> {
                    val ref = XPathVariableNameReference(this, it.textRange.shiftRight(-node.startOffset))
                    arrayOf(ref as PsiReference)
                }
                else -> null
            }
        } ?: PsiReference.EMPTY_ARRAY
    }

    // endregion
    // region PsiNameIdentifierOwner

    override fun getNameIdentifier(): PsiElement? = firstChild

    // endregion
    // region PsiNamedElement

    override fun getName(): String? = nameIdentifier?.text

    override fun setName(@NonNls name: String): PsiElement {
        val renamed = createElement<XPathNCName>(name) ?: return this
        return replace(renamed)
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = (parent as NavigationItem).presentation

    // endregion
}
