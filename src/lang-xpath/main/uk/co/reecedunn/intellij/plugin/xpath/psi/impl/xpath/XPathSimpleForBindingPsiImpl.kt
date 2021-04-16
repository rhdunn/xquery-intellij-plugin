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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmSequenceType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathSimpleForBinding
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.flwor.XpmBindingCollectionType
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmCollectionBinding

class XPathSimpleForBindingPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathSimpleForBinding,
    XpmCollectionBinding,
    XpmSyntaxValidationElement {
    // region XpmVariableBinding

    override val variableName: XsQNameValue?
        get() = children().filterIsInstance<XPathEQName>().firstOrNull()

    // endregion
    // region XpmCollectionBinding

    override val variableType: XdmSequenceType?
        get() = children().filterIsInstance<XdmSequenceType>().firstOrNull()

    override val bindingExpression: XpmExpression?
        get() = children().filterIsInstance<XpmExpression>().firstOrNull()

    // endregion
    // region XpmForBinding

    override val bindingCollectionType: XpmBindingCollectionType
        get() = when (parent.children().filterIsElementType(XPathTokenType.K_MEMBER).firstOrNull()) {
            null -> XpmBindingCollectionType.SequenceItem
            else -> XpmBindingCollectionType.ArrayMember
        }

    // endregion
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(XPathTokenType.K_MEMBER) ?: this

    // endregion
}
