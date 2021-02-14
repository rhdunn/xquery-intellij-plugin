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

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathIntegerLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNamedFunctionRef
import uk.co.reecedunn.intellij.plugin.xdm.types.XsIntegerValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.toInt
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.lang.validation.XpmSyntaxValidationElement

class XPathNamedFunctionRefPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathNamedFunctionRef,
    XpmSyntaxValidationElement {
    // region XpmSyntaxValidationElement

    override val conformanceElement: PsiElement
        get() = findChildByType(XPathTokenType.FUNCTION_REF_OPERATOR) ?: this

    // endregion
    // region XdmFunctionReference

    override val functionName: XsQNameValue?
        get() = children().filterIsInstance<XsQNameValue>().firstOrNull()

    override val arity: Int
        get() = (children().filterIsInstance<XPathIntegerLiteral>().firstOrNull() as? XsIntegerValue)?.toInt() ?: 0

    // endregion
    // region XpmExpression

    override val expressionElement: PsiElement
        get() = this

    // endregion
}
