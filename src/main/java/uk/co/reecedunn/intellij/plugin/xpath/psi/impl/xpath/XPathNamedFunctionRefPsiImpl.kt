/*
 * Copyright (C) 2016-2018 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathIntegerLiteral
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathNamedFunctionRef
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.XsIntegerValue
import uk.co.reecedunn.intellij.plugin.xpath.model.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.model.toInt
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType

class XPathNamedFunctionRefPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node),
    XPathFunctionReference,
    XPathNamedFunctionRef,
    VersionConformance {
    // region VersionConformance

    override val requiresConformance get(): List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

    override val conformanceElement get(): PsiElement = findChildByType(XPathTokenType.FUNCTION_REF_OPERATOR) ?: this

    // endregion
    // region XPathFunctionReference

    override val functionName: XsQNameValue? = findChildByClass(XPathEQName::class.java) as? XsQNameValue

    override val arity
        get(): Int =
            (children().filterIsInstance<XPathIntegerLiteral>().firstOrNull() as? XsIntegerValue)?.toInt() ?: 0

    // endregion
}
