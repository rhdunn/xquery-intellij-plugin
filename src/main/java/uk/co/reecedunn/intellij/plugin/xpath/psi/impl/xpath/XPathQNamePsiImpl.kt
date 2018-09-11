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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.XsQName
import uk.co.reecedunn.intellij.plugin.xdm.XsUntyped
import uk.co.reecedunn.intellij.plugin.xdm.createLexicalQName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathQName
import uk.co.reecedunn.intellij.plugin.xpath.model.XsAnyUriValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XmlNCNameImpl

class XPathQNamePsiImpl(node: ASTNode) :
    XPathEQNamePsiImpl(node),
    XPathQName,
    XdmStaticValue,
    XsQNameValue,
    PsiNameIdentifierOwner {
    // region XsQNameValue

    private val names get(): Sequence<XsNCNameValue> = children().filterIsInstance<XsNCNameValue>()

    override val namespace: XsAnyUriValue? = null

    override val prefix get(): XsNCNameValue? = names.first()

    override val localName get(): XsNCNameValue? = names.toList().let { if (it.size == 2) it[1] else null }

    override val isLexicalQName: Boolean = true

    // endregion
    // region XdmStaticValue

    override val cacheable get(): CachingBehaviour = cachedConstantValue.cachingBehaviour

    override val staticType get(): XdmSequenceType = staticValue?.let { XsQName } ?: XsUntyped

    override val staticValue get(): Any? = cachedConstantValue.get()

    private val cachedConstantValue = CacheableProperty {
        val names: List<PsiElement> = children().filterIsInstance<XmlNCNameImpl>().toList()
        when (names.size) {
            2 -> createLexicalQName(names[0] as XdmStaticValue, names[1] as XdmStaticValue, this)
            else -> null
        } `is` Cacheable
    }

    // endregion
    // region PsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedConstantValue.invalidate()
    }

    override fun getTextOffset(): Int = nameIdentifier?.textOffset ?: super.getTextOffset()

    // endregion
    // region PsiNameIdentifierOwner

    override fun getNameIdentifier(): PsiElement? = localName as? PsiElement

    // endregion
    // region PsiNamedElement

    override fun getName(): String? = nameIdentifier?.text

    @Throws(IncorrectOperationException::class)
    override fun setName(@NonNls name: String): PsiElement {
        return this
    }

    // endregion
}
