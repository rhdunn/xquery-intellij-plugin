/*
 * Copyright (C) 2016-2020 Reece H. Dunn
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
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathNCNamePsiImpl
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.reference.XPathFunctionNameReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.reference.XQueryVariableNameReference

open class XQueryNCNamePsiImpl(node: ASTNode) : XPathNCNamePsiImpl(node) {
    // region PsiElement

    override fun getReferences(): Array<PsiReference> {
        return (localName as? PsiElement)?.let {
            when (parent) {
                is XpmFunctionReference -> {
                    val ref = XPathFunctionNameReference(this, it.textRange.shiftRight(-node.startOffset))
                    arrayOf(ref as PsiReference)
                }
                is XpmVariableReference -> {
                    val ref = XQueryVariableNameReference(this, it.textRange.shiftRight(-node.startOffset))
                    arrayOf(ref as PsiReference)
                }
                else -> null
            }
        } ?: PsiReference.EMPTY_ARRAY
    }

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = (parent as NavigatablePsiElement).presentation

    // endregion
}
