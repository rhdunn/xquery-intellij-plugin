/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathVarName
import uk.co.reecedunn.intellij.plugin.xdm.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath.XPathURIQualifiedNamePsiImpl

class XQueryURIQualifiedNamePsiImpl(node: ASTNode) : XPathURIQualifiedNamePsiImpl(node) {
    // region XsQNameValue

    override val namespace: XsAnyUriValue?
        get() = findChildByType<PsiElement>(XQueryElementType.BRACED_URI_LITERAL) as XsAnyUriValue

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? {
        return when (parent) {
            is XPathVarName -> (parent.parent as NavigatablePsiElement).presentation
            else -> (parent as NavigatablePsiElement).presentation
        }
    }

    // endregion
}
