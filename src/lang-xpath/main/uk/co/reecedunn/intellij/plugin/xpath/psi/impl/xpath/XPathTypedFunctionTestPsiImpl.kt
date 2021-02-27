/*
 * Copyright (C) 2018-2021 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.reverse
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTypedFunctionTest
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement

class XPathTypedFunctionTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathTypedFunctionTest,
    XpmSyntaxValidationElement {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region XPathTypedFunctionTest

    override val paramTypes: Sequence<XdmSequenceType>
        get() = children().filterIsInstance<XdmSequenceTypeList>().firstOrNull()?.types ?: emptySequence()

    override val returnType: XdmSequenceType?
        get() {
            val type = reverse(children()).filterIsInstance<XdmSequenceType>().firstOrNull() ?: return null
            val asBefore = reverse((type as PsiElement).siblings()).find {
                it.elementType === XPathTokenType.K_AS
            }
            return if (asBefore != null) type else null
        }

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        val returnType = returnType?.typeName ?: "item()*"
        "function(${paramTypes.joinToString { it.typeName }}) as $returnType"
    }

    override val typeName: String
        get() = cachedTypeName.get()!!

    override val itemType: XdmItemType
        get() = this

    override val lowerBound: Int = 1

    override val upperBound: Int = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmFunction::class.java

    // endregion
    // region XpmAnnotated

    override val annotations: Sequence<XdmAnnotation> = emptySequence()

    override val isPublic: Boolean = false

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}
