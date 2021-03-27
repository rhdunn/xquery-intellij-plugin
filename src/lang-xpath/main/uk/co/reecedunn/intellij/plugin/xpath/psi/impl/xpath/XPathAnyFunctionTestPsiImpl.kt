/*
 * Copyright (C) 2019-2021 Reece H. Dunn
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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.psi.ASTWrapperPsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAnnotation
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathAnyFunctionTest
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmFunction
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmAnnotated
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation

class XPathAnyFunctionTestPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathAnyFunctionTest,
    XpmSyntaxValidationElement {
    companion object {
        private val TYPE_NAME = Key.create<String>("TYPE_NAME")
    }
    // region ASTDelegatePsiElement

    override fun subtreeChanged() {
        super.subtreeChanged()
        clearUserData(TYPE_NAME)
    }

    // endregion
    // region XdmSequenceType

    override val typeName: String
        get() = computeUserDataIfAbsent(TYPE_NAME) {
            val annotations = annotations.mapNotNull { (it as ItemPresentation).presentableText }.joinToString(" ")
            when (annotations) {
                "" -> "function(*)"
                else -> "$annotations function(*)"
            }
        }

    override val itemType: XdmItemType
        get() = this

    override val lowerBound: Int = 1

    override val upperBound: Int = 1

    // endregion
    // region XdmItemType

    override val typeClass: Class<*> = XdmFunction::class.java

    // endregion
    // region XpmAnnotated

    override val annotations: Sequence<XdmAnnotation>
        get() = children().filterIsInstance<XdmAnnotation>()

    override val isPublic: Boolean
        get() = annotation(XpmAnnotated.PRIVATE) == null

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
}
