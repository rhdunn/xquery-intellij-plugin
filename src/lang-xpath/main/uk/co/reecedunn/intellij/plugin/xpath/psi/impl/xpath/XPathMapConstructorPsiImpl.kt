/*
 * Copyright (C) 2016-2017, 2020-2021 Reece H. Dunn
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
import com.intellij.psi.util.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.Saxon
import uk.co.reecedunn.intellij.plugin.intellij.lang.Version
import uk.co.reecedunn.intellij.plugin.intellij.lang.VersionConformance
import uk.co.reecedunn.intellij.plugin.intellij.lang.XQuerySpec
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmMap
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmObjectNode
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathMapConstructor
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathMapConstructorEntry
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.item.XpmMapEntry

private val XQUERY31: List<Version> = listOf(XQuerySpec.REC_3_1_20170321, Saxon.VERSION_9_4)
private val MARKLOGIC80: List<Version> = listOf()

class XPathMapConstructorPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathMapConstructor, VersionConformance, XpmSyntaxValidationElement {
    // region XpmConstructableItemExpression

    override val itemTypeClass: Class<*>
        get() = when (conformanceElement.elementType) {
            XPathTokenType.K_OBJECT_NODE -> XdmObjectNode::class.java
            else -> XdmMap::class.java
        }

    override val itemExpression: XpmExpression
        get() = this

    // endregion
    // region XpmMapExpression

    override val expressionElement: PsiElement
        get() = children().firstOrNull { it is XPathMapConstructorEntry } ?: this

    override val entries: Sequence<XpmMapEntry>
        get() = children().filterIsInstance<XpmMapEntry>()

    // endregion
    // region VersionConformance/XpmSyntaxValidationElement

    override val requiresConformance: List<Version>
        get() {
            if (conformanceElement.elementType === XPathTokenType.K_OBJECT_NODE) {
                return MARKLOGIC80
            }
            return XQUERY31
        }

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}
