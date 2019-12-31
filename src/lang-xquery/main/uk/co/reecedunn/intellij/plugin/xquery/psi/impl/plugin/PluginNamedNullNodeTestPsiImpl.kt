/*
 * Copyright (C) 2016-2019 Reece H. Dunn
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
package uk.co.reecedunn.intellij.plugin.xquery.psi.impl.plugin

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xquery.ast.plugin.PluginNamedNullNodeTest
import uk.co.reecedunn.intellij.plugin.intellij.lang.MarkLogic
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmNullNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsStringValue

class PluginNamedNullNodeTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginNamedNullNodeTest, XdmItemType, VersionConformance {
    // region PluginNamedArrayNodeTest

    override val keyName: XsStringValue
        get() = children().filterIsInstance<XPathStringLiteral>().first() as XsStringValue

    // endregion
    // region XdmSequenceType

    override val typeName get(): String = "null-node(\"${keyName.data}\")"

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmNullNode::class.java

    // endregion
    // region VersionConformance

    override val requiresConformance get(): List<Version> = listOf(MarkLogic.VERSION_8_0)

    override val conformanceElement get(): PsiElement = firstChild

    // endregion
}
