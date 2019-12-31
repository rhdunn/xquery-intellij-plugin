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
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAtomicOrUnionType
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAtomicType
import uk.co.reecedunn.intellij.plugin.xdm.model.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsAnySimpleType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue

class XPathAtomicOrUnionTypePsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathAtomicOrUnionType, XPathAtomicType, XdmItemType {
    // region XPathAtomicOrUnionType

    // TODO: Provide a way of validating that the type is a generalized atomic type [XPST0051].
    override val type get(): XsQNameValue = firstChild as XsQNameValue

    // endregion
    // region XdmSequenceType

    override val typeName get(): String = text

    override val itemType get(): XdmItemType = this

    override val lowerBound: Int? = 1

    override val upperBound: Int? = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XsAnySimpleType::class.java

    // endregion
}
