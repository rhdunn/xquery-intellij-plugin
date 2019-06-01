/*
 * Copyright (C) 2018-2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginSequenceTypeList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTypedFunctionTest
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.model.*

private val XQUERY30 = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathTypedFunctionTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathTypedFunctionTest, XdmItemType, VersionConformance {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region XPathTypedFunctionTest

    override val paramTypes: Sequence<XdmSequenceType>
        get() = children().filterIsInstance<PluginSequenceTypeList>().firstOrNull()?.types ?: emptySequence()

    override val returnType: XdmSequenceType?
        get() {
            val type = children().reversed().filterIsInstance<XdmSequenceType>().firstOrNull()
            val asBefore = (type as? PsiElement)?.siblings()?.reversed()?.find {
                it.node.elementType === XPathTokenType.K_AS
            }
            return if (asBefore != null) type else null
        }

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        val returnType = returnType?.typeName ?: "item()*"
        "function(${paramTypes.joinToString { it.typeName }}) as $returnType" `is` Cacheable
    }
    override val typeName get(): String = cachedTypeName.get()!!

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmFunction::class.java

    // endregion
    // region VersionConformance

    override val requiresConformance get(): List<Version> = XQUERY30

    override val conformanceElement get(): PsiElement = firstChild

    // endregion
}
