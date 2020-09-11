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
import com.intellij.navigation.ItemPresentation
import uk.co.reecedunn.intellij.plugin.core.data.CacheableProperty
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.intellij.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableBinding
import uk.co.reecedunn.intellij.plugin.xdm.variables.XdmVariableType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import javax.swing.Icon

class XPathParamPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathParam,
    XdmVariableBinding,
    XdmVariableType,
    ItemPresentation {
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        cachedPresentableText.invalidate()
    }

    // endregion
    // region XPathVariableBinding

    override val variableName: XsQNameValue?
        get() = children().filterIsInstance<XPathEQName>().firstOrNull() as? XsQNameValue

    // endregion
    // region XPathVariableType

    override val variableType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation? = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon? = XPathIcons.Nodes.Param

    override fun getLocationString(): String? = null

    private val cachedPresentableText = CacheableProperty {
        variableName?.let { name ->
            val type = variableType
            if (type == null)
                "\$${op_qname_presentation(name)}"
            else
                "\$${op_qname_presentation(name)} as ${type.typeName}"
        }
    }

    override fun getPresentableText(): String? = cachedPresentableText.get()

    // endregion
}
