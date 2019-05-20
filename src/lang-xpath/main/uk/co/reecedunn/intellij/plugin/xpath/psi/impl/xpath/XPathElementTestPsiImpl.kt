/*
 * Copyright (C) 2016, 2019 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.Cacheable
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.data.`is`
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathElementTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTypeName
import uk.co.reecedunn.intellij.plugin.xpath.functions.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmElement
import uk.co.reecedunn.intellij.plugin.xpath.model.XdmItemType
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue

class XPathElementTestPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathElementTest, XdmItemType {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region XPathElementTest

    override val nodeName get(): XsQNameValue? = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val nodeType get(): XsQNameValue? = children().filterIsInstance<XPathTypeName>().firstOrNull()?.type

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        val name = nodeName
        val type = nodeType
        when {
            name == null -> {
                type?.let {
                    "element(*,${op_qname_presentation(type)})" `is` Cacheable
                } ?: "element()" `is` Cacheable
            }
            type == null -> "element(${op_qname_presentation(name)})" `is` Cacheable
            else -> "element(${op_qname_presentation(name)},${op_qname_presentation(type)})" `is` Cacheable
        }
    }
    override val typeName get(): String = cachedTypeName.get()!!

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmElement::class.java

    // endregion
}
