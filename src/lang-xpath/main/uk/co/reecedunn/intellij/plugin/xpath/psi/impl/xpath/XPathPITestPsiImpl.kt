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
package uk.co.reecedunn.intellij.plugin.xpath.psi.impl.xpath

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xdm.types.impl.psi.XsNCName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPITest
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathStringLiteral

class XPathPITestPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathPITest, XdmItemType {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedNodeName.invalidate()
    }

    // endregion
    // region XPathPITest

    private val cachedNodeName = CacheableProperty {
        children().map {
            when (it) {
                is XsQNameValue -> it.localName
                // TODO: Provide a way of validating that the StringLiteral is an NCName [XPTY0004].
                is XPathStringLiteral -> XsNCName((it as XsStringValue).data.trim(), it)
                else -> null
            }
        }.filterNotNull().firstOrNull()
    }
    override val nodeName get(): XsAnyAtomicType? = cachedNodeName.get()

    // endregion
    // region XdmSequenceType

    override val typeName
        get(): String {
            return when (val name = nodeName) {
                is XsStringValue -> "processing-instruction(${name.data})"
                else -> "processing-instruction()"
            }
        }

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmProcessingInstructionNode::class.java

    // endregion
}
