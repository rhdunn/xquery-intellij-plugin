/*
 * Copyright (C) 2020-2021 Reece H. Dunn
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

import com.intellij.lang.ASTNode
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginTypeAlias
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement

class PluginTypeAliasPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginTypeAlias, XpmSyntaxValidationElement {
    companion object {
        private val TYPE_NAME = Key.create<String>("TYPE_NAME")
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(TYPE_NAME)
    }

    // endregion
    // region PluginTypeAlias

    override val type: XsQNameValue
        get() = children().filterIsInstance<XsQNameValue>().first()

    // endregion
    // region XdmSequenceType

    override val typeName: String
        get() = computeUserDataIfAbsent(TYPE_NAME) {
            "type(${op_qname_presentation(type)})"
        }

    override val itemType: XdmItemType
        get() = this

    override val lowerBound: Int = 1

    override val upperBound: Int = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmItem::class.java

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}
