/*
 * Copyright (C) 2016-2017, 2019 Reece H. Dunn
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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQuerySequenceTypeUnion
import uk.co.reecedunn.intellij.plugin.xquery.ast.xquery.XQueryCaseClause
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmItemType
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmSequenceTypeUnion
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmSingleItemType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

private val SEMANTICS: List<Version> = listOf(XQueryIntelliJPlugin.VERSION_1_3)
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XQuerySequenceTypeUnionPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XQuerySequenceTypeUnion, XdmSequenceTypeUnion, VersionConformance {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region PluginSequenceTypeList

    override val isParenthesized: Boolean
        get() {
            var element = prevSibling
            while (
                element.node.elementType === XPathTokenType.WHITE_SPACE ||
                element.node.elementType === XPathElementType.COMMENT
            ) {
                element = element.prevSibling
            }
            return element.node.elementType === XPathTokenType.PARENTHESIS_OPEN
        }

    // endregion
    // region XdmSequenceTypeUnion

    override val types: Sequence<XdmSequenceType> get() = children().filterIsInstance<XdmSequenceType>()

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        val name = types.joinToString(" | ") { it.typeName }
        if (isParenthesized)
            "($name)"
        else
            name
    }
    override val typeName get(): String = cachedTypeName.get()!!

    override val itemType get(): XdmItemType? = XdmSingleItemType

    override val lowerBound: Int? = 0

    override val upperBound: Int? = Int.MAX_VALUE

    // endregion
    // region VersionConformance

    override val requiresConformance
        get(): List<Version> {
            return if (parent is XQueryCaseClause)
                XQUERY30
            else
                SEMANTICS
        }

    override val conformanceElement get(): PsiElement = findChildByType(XPathTokenType.UNION)!!

    // endregion
}
