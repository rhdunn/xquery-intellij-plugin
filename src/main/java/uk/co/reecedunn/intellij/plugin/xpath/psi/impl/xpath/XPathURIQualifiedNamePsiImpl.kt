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

import com.intellij.extapi.psi.ASTWrapperPsiElement
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
import uk.co.reecedunn.intellij.plugin.xdm.createQName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathURIQualifiedName
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathNamespaceDeclaration
import uk.co.reecedunn.intellij.plugin.xquery.parser.XQueryElementType
import uk.co.reecedunn.intellij.plugin.xquery.psi.impl.XmlNCNameImpl

class XPathURIQualifiedNamePsiImpl(node: ASTNode):
        ASTWrapperPsiElement(node),
        XPathURIQualifiedName,
        XdmStaticValue,
        PsiNameIdentifierOwner {
    // region XPathEQName

    override val prefix get(): PsiElement? {
        return findChildByType(XQueryElementType.BRACED_URI_LITERAL)
    }

    override val localName get(): PsiElement? {
        return children().filterIsInstance<XmlNCNameImpl>().firstOrNull()
    }

    override fun resolvePrefixNamespace(): Sequence<XPathNamespaceDeclaration> {
        return emptySequence()
    }

    // endregion
    // region XdmStaticValue

    override val cacheable: CachingBehaviour = CachingBehaviour.Cache

    override val staticType get(): XdmSequenceType = staticValue?.let { XsQName } ?: XsUntyped

    override val staticValue get(): Any? = cachedConstantValue.get()

    private val cachedConstantValue = CacheableProperty {
        val namespace: PsiElement? = findChildByType(XQueryElementType.BRACED_URI_LITERAL)
        val localName: PsiElement? = children().filterIsInstance<XmlNCNameImpl>().firstOrNull()
        localName?.let {
            createQName(namespace as XdmStaticValue, localName as XdmStaticValue, this)
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

    override fun getNameIdentifier(): PsiElement? = children().filterIsInstance<XmlNCNameImpl>().firstOrNull()

    // endregion
    // region PsiNamedElement

    override fun getName(): String? = nameIdentifier?.text

    @Throws(IncorrectOperationException::class)
    override fun setName(@NonNls name: String): PsiElement {
        return this
    }

    // endregion
}
