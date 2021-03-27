/*
 * Copyright (C) 2016-2021 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.util.containers.orNull
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.resources.XPathIcons
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathParam
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpm.optree.expr.XpmExpression
import java.util.*
import javax.swing.Icon

class XPathParamPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathParam, ItemPresentation {
    companion object {
        private val PRESENTABLE_TEXT = Key.create<Optional<String>>("PRESENTABLE_TEXT")
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(PRESENTABLE_TEXT)
    }

    // endregion
    // region XpmVariableBinding

    override val variableName: XsQNameValue?
        get() = children().filterIsInstance<XPathEQName>().firstOrNull()

    // endregion
    // region XpmParameter

    override val variableType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val defaultExpression: XpmExpression? = null

    // endregion
    // region NavigationItem

    override fun getPresentation(): ItemPresentation = this

    // endregion
    // region ItemPresentation

    override fun getIcon(unused: Boolean): Icon = XPathIcons.Nodes.Param

    override fun getLocationString(): String? = null

    override fun getPresentableText(): String? = computeUserDataIfAbsent(PRESENTABLE_TEXT) {
        variableName?.let { name ->
            val type = variableType
            if (type == null)
                Optional.of("\$${op_qname_presentation(name)}")
            else
                Optional.of("\$${op_qname_presentation(name)} as ${type.typeName}")
        } ?: Optional.empty()
    }.orNull()

    // endregion
}
