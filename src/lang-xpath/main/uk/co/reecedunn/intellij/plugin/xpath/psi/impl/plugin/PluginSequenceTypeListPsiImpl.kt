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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQueryIntelliJPlugin
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginSequenceTypeList
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceTypeList
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSingleItemType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.parser.filterNotWhitespace

class PluginSequenceTypeListPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginSequenceTypeList, XdmSequenceTypeList, VersionConformance {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region PluginSequenceTypeList

    override val isParenthesized: Boolean
        get() {
            val element = siblings().reversed().filterNotWhitespace().first()
            return element.elementType === XPathTokenType.PARENTHESIS_OPEN
        }

    // endregion
    // region XdmSequenceTypeList

    override val types: Sequence<XdmSequenceType>
        get() = children().filterIsInstance<XdmSequenceType>()

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        val name = types.joinToString { it.typeName }
        if (isParenthesized)
            "($name)"
        else
            name
    }

    override val typeName: String
        get() = cachedTypeName.get()!!

    override val itemType: XdmItemType?
        get() = XdmSingleItemType

    override val lowerBound: Int? = 0

    override val upperBound: Int? = Int.MAX_VALUE

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() = when (parent.elementType) {
            XPathElementType.TYPED_FUNCTION_TEST -> listOf()
            else -> listOf(XQueryIntelliJPlugin.VERSION_1_3)
        }

    override val conformanceElement: PsiElement
        get() = findChildByType(XPathTokenType.COMMA) ?: firstChild

    // endregion
}
