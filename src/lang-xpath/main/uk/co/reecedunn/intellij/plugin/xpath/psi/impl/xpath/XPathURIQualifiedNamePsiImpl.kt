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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.psi.createElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathURIQualifiedName
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.psi.impl.XmlNCNameImpl

open class XPathURIQualifiedNamePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathURIQualifiedName,
    XsQNameValue {
    // region XsQNameValue

    override val namespace: XsAnyUriValue?
        get() = findChildByType<PsiElement>(XPathElementType.BRACED_URI_LITERAL) as XsAnyUriValue

    override val prefix: XsNCNameValue? = null

    override val localName: XsNCNameValue?
        get() = children().filterIsInstance<XsNCNameValue>().firstOrNull()

    override val isLexicalQName: Boolean = false

    // endregion
    // region PsiElement

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()

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
}
