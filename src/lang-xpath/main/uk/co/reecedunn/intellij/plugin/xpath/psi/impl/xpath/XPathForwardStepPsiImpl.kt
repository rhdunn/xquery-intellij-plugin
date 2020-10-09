/*
 * Copyright (C) 2016, 2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.psi.elementType
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmItemType
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathForwardStep
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNameTest
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNodeTest
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmAxisType
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmPredicate
import java.lang.UnsupportedOperationException

class XPathForwardStepPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), XPathForwardStep, XpmSyntaxValidationElement {

    override val axisType: XpmAxisType
        get() = when (firstChild.elementType) {
            XPathTokenType.K_ATTRIBUTE -> XpmAxisType.Attribute
            XPathTokenType.K_CHILD -> XpmAxisType.Child
            XPathTokenType.K_DESCENDANT -> XpmAxisType.Descendant
            XPathTokenType.K_DESCENDANT_OR_SELF -> XpmAxisType.DescendantOrSelf
            XPathTokenType.K_FOLLOWING -> XpmAxisType.Following
            XPathTokenType.K_FOLLOWING_SIBLING -> XpmAxisType.FollowingSibling
            XPathTokenType.K_NAMESPACE -> XpmAxisType.Namespace
            XPathTokenType.K_PROPERTY -> XpmAxisType.Property
            XPathTokenType.K_SELF -> XpmAxisType.Self
            else -> throw UnsupportedOperationException()
        }

    override val nodeName: XsQNameValue?
        get() = (lastChild as? XPathNameTest)?.nodeName

    override val nodeType: XdmItemType
        get() = (lastChild as? XPathNodeTest)?.nodeType ?: axisType.principalNodeKind

    override val predicate: XpmPredicate? = null

    override val conformanceElement: PsiElement
        get() = firstChild
}
