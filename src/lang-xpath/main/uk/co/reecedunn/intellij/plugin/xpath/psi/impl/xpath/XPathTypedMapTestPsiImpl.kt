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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTypedMapTest
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmMap
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType

class XPathTypedMapTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathTypedMapTest, XdmItemType, VersionConformance {
    // region XPathTypedMapTest

    override val keyType: XdmItemType?
        get() {
            val type = children().filterIsInstance<XdmItemType>().filterNotNull().firstOrNull()
            val commaBefore = (type as? PsiElement)?.siblings()?.reversed()?.find {
                it.elementType == XPathTokenType.COMMA
            }
            return if (commaBefore != null) null else type
        }

    override val valueType: XdmSequenceType?
        get() {
            val type = children().reversed().filterIsInstance<XdmItemType>().filterNotNull().firstOrNull()
            val commaAfter = (type as? PsiElement)?.siblings()?.find {
                it.elementType == XPathTokenType.COMMA
            }
            return if (commaAfter != null) null else type
        }

    // endregion
    // region XdmSequenceType

    override val typeName: String
        get() {
            val key = keyType
            val value = valueType
            return when {
                key == null && value == null -> "map(*)"
                key == null -> "map(xs:anyAtomicType, ${value!!.typeName})"
                value == null -> "map(${key.typeName}, item()*)"
                else -> "map(${key.typeName}, ${value.typeName})"
            }
        }

    override val itemType: XdmItemType get() = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmMap::class.java

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version> get() = listOf(XQuerySpec.REC_3_1_20170321, Saxon.VERSION_9_4)

    override val conformanceElement: PsiElement get() = firstChild

    // endregion
}
