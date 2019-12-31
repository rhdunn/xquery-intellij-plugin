/*
 * Copyright (C) 2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginEmptySequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType

private val XQUERY10_REC_EMPTY: List<Version> = listOf(
    XQuerySpec.REC_1_0_20070123,
    EXistDB.VERSION_4_0
)

private val XQUERY10_WD_EMPTY: List<Version> = listOf(
    XQuerySpec.WD_1_0_20030502,
    XQuerySpec.MARKLOGIC_0_9,
    until(EXistDB.VERSION_4_0)
)

class PluginEmptySequenceTypePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginEmptySequenceType, XdmSequenceType, VersionConformance {
    // region XdmSequenceType

    override val typeName: String = "empty-sequence()"

    override val itemType get(): XdmItemType? = null

    override val lowerBound: Int? = 0

    override val upperBound: Int? = 0

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() {
            return if (conformanceElement.node.elementType == XPathTokenType.K_EMPTY)
                XQUERY10_WD_EMPTY
            else
                XQUERY10_REC_EMPTY
        }

    override val conformanceElement: PsiElement get() = firstChild

    // endregion
}
