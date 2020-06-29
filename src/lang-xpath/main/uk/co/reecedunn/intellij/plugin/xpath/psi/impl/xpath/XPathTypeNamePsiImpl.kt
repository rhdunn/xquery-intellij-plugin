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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathTypeName
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.op_qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnyType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

open class XPathTypeNamePsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathTypeName, XdmItemType {
    // region XPathTypeName

    // TODO: Provide a way of validating that the type is in the in-scope schema types [XPST0008].
    override val type: XsQNameValue get() = firstChild as XsQNameValue

    // endregion
    // region XdmSequenceType

    override val typeName: String get() = op_qname_presentation(type) ?: ""

    override val itemType: XdmItemType get() = this

    override val lowerBound: Int? = 1

    // NOTE: type may be a list type (e.g. xs:IDREFS), so the upper bound cannot be restricted
    // without expanding the QName.
    override val upperBound: Int? = Int.MAX_VALUE

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XsAnyType::class.java

    // endregion
}
