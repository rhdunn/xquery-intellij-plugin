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
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.xdm.functions.XdmFunctionReference
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathEQName

class PluginArrowFunctionCallPsiImpl(node: ASTNode) :
    ASTWrapperPsiElement(node), PluginArrowFunctionCall, XdmFunctionReference {
    // region XpmExpression

    override val expressionElement: PsiElement
        get() = when (firstChild.firstChild) {
            is XPathEQName -> this
            else -> children().filterIsInstance<XPathArgumentList>().first()
        }

    // endregion
    // region XdmFunctionReference

    override val arity: Int
        get() {
            val args: XPathArgumentList? = children().filterIsInstance<XPathArgumentList>().firstOrNull()
            return args?.arity?.plus(1) ?: 1
        }

    override val functionName: XsQNameValue?
        get() = firstChild.firstChild as? XsQNameValue

    // endregion
}
