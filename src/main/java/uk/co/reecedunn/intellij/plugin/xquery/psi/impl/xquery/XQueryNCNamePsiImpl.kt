/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue

open class XQueryNCNamePsiImpl(node: ASTNode) :
    XQueryEQNamePsiImpl(node),
    XPathNCName,
    XsQNameValue,
    PsiNameIdentifierOwner {
    // region XsQNameValue

    override val namespace: XsAnyUriValue? = null

    override val prefix: XsNCNameValue? = null

    override val localName get(): XsNCNameValue? = children().filterIsInstance<XsNCNameValue>().firstOrNull()

    override val isLexicalQName: Boolean = true

    override val element get(): PsiElement? = this

    // endregion
    // region PsiNameIdentifierOwner

    override fun getNameIdentifier(): PsiElement? = firstChild

    // endregion
    // region PsiNamedElement

    override fun getName(): String? = nameIdentifier?.text

    @Throws(IncorrectOperationException::class)
    override fun setName(@NonNls name: String): PsiElement {
        return this
    }

    // endregion
}
