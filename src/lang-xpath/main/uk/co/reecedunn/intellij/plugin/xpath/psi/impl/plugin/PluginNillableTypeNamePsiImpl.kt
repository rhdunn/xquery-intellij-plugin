/*
 * Copyright (C) 2019-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginNillableTypeName

class PluginNillableTypeNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), PluginNillableTypeName {
    // region XdmSequenceType

    override val typeName: String
        get() = "${itemType.typeName}?"

    override val itemType: XdmItemType
        get() = firstChild as XdmItemType

    override val lowerBound: Int = 0

    override val upperBound: Int?
        get() = itemType.upperBound

    // endregion
}
