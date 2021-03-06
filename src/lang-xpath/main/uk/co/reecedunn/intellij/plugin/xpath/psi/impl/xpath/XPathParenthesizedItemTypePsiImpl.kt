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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmEmptySequence
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParenthesizedItemType
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathBundle

private val XQUERY3 = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)
private val SEMANTICS = listOf(FormalSemanticsSpec.REC_1_0_20070123)

private val SEQUENCE_TYPE = TokenSet.create(
    XPathElementType.SEQUENCE_TYPE,
    XPathElementType.EMPTY_SEQUENCE_TYPE
)

class XPathParenthesizedItemTypePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathParenthesizedItemType,
    VersionConformance,
    VersionConformanceName {
    // region XdmSequenceType

    // NOTE: The wrapped type may be a SequenceType, so locate and forward that type.
    private val sequenceType: XdmSequenceType
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull() ?: XdmEmptySequence

    override val typeName: String
        get() = "(${sequenceType.typeName})"

    override val itemType: XdmItemType?
        get() = sequenceType.itemType

    override val lowerBound: Int?
        get() = sequenceType.lowerBound

    override val upperBound: Int?
        get() = sequenceType.upperBound

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() = if (findChildByType<PsiElement>(SEQUENCE_TYPE) != null) SEMANTICS else XQUERY3

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
    // region VersionConformanceName

    override val conformanceName: String?
        get() = when {
            findChildByType<PsiElement>(SEQUENCE_TYPE) != null -> XPathBundle.message("construct.parenthesized-sequence-type")
            else -> null
        }

    // endregion
}
