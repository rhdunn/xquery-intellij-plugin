/*
 * Copyright (C) 2016, 2019-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.intellij.resources.XPathBundle
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathElementTest
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTypeName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathWildcard

class XPathElementTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), VersionConformance, VersionConformanceName, XPathElementTest, XdmItemType {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedTypeName.invalidate()
    }

    // endregion
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() = if (conformanceElement is XPathWildcard) listOf(Saxon.VERSION_10_0) else listOf()

    override val conformanceElement: PsiElement
        get() = children().filterIsInstance<XPathWildcard>().firstOrNull() ?: firstChild

    override val conformanceName: String?
        get() = XPathBundle.message("construct.wildcard-element-test")

    // endregion
    // region XPathElementTest

    override val nodeName get(): XsQNameValue? = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val nodeType: XdmSequenceType?
        get() = when (val type = children().filterIsInstance<XdmSequenceType>().firstOrNull()) {
            is XPathTypeName -> if (type.type.localName == null) null else type
            else -> type
        }

    // endregion
    // region XdmSequenceType

    private val cachedTypeName = CacheableProperty {
        val name = nodeName
        val type = nodeType
        when {
            name == null -> {
                type?.let { "element(*,${type.typeName})" } ?: "element()"
            }
            type == null -> "element(${op_qname_presentation(name)})"
            else -> "element(${op_qname_presentation(name)},${type.typeName})"
        }
    }
    override val typeName get(): String = cachedTypeName.get()!!

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmElementNode::class.java

    // endregion
}
