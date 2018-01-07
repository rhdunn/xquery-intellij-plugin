/*
 * Copyright (C) 2016-2017 Reece H. Dunn
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
import com.intellij.psi.PsiNamedElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import uk.co.reecedunn.intellij.plugin.core.data.CachingBehaviour
import uk.co.reecedunn.intellij.plugin.xdm.XsQName
import uk.co.reecedunn.intellij.plugin.xdm.createLexicalQName
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmStaticValue
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNCName

open class XPathNCNamePsiImpl(node: ASTNode) : XPathEQNamePsiImpl(node), XPathNCName, XdmStaticValue, PsiNamedElement {
    override val cacheable: CachingBehaviour = CachingBehaviour.DoNotCache // Bound to the static context

    override val staticType: XdmSequenceType = XsQName

    override val staticValue get(): Any? = createLexicalQName(null, firstChild as XdmStaticValue, this)

    override fun getName(): String? = firstChild.text

    @Throws(IncorrectOperationException::class)
    override fun setName(@NonNls name: String): PsiElement {
        return this
    }
}
