/*
 * Copyright (C) 2016-2017, 2019 Reece H. Dunn
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
import com.intellij.psi.tree.TokenSet
import uk.co.reecedunn.intellij.plugin.core.sequences.children
import uk.co.reecedunn.intellij.plugin.core.sequences.filterIsElementType
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.plugin.PluginArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathFunctionCall
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPostfixExpr
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionParamBinding
import uk.co.reecedunn.intellij.plugin.xpath.model.XPathFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.model.staticallyKnownFunctions
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType

private val ARGUMENTS = TokenSet.create(
    XPathElementType.ARGUMENT,
    XPathElementType.ARGUMENT_PLACEHOLDER
)

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

class XPathArgumentListPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathArgumentList, VersionConformance {
    // region VersionConformance

    override val requiresConformance
        get(): List<Version> {
            if (parent !is XPathPostfixExpr) {
                return XQUERY10
            }
            return XQUERY30
        }

    override val conformanceElement get(): PsiElement = firstChild

    // endregion
    // region XPathArgumentList

    override val functionReference: XPathFunctionReference?
        get() = when (parent) {
            is XPathFunctionCall -> parent as XPathFunctionReference
            is PluginArrowFunctionCall -> parent.firstChild as XPathFunctionReference
            else -> null
        }

    override val arity get(): Int = children().filterIsElementType(ARGUMENTS).count()

    override val bindings: List<XPathFunctionParamBinding>
        get() {
            val ref = functionReference
            val target = ref?.functionName?.staticallyKnownFunctions()?.firstOrNull { f ->
                f.arity.isWithin(ref.arity)
            } ?: return listOf()

            val args = children().filterIsElementType(ARGUMENTS).iterator()
            val params = target.params
            return params.mapIndexed { index, param ->
                when {
                    index == 0 && parent is PluginArrowFunctionCall -> {
                        // First argument bound to an ArrowExpr evaluation result.
                        val context = parent.siblings().reversed().filter {
                            it is PluginArrowFunctionCall
                        }.firstOrNull() ?: parent.parent.firstChild
                        XPathFunctionParamBinding(param, context)
                    }
                    index == params.size - 1 -> {
                        // Last argument, maybe variadic.
                        XPathFunctionParamBinding(param, args.asSequence().toList())
                    }
                    else -> {
                        // Other argument bound to the relevant parameter.
                        XPathFunctionParamBinding(param, args.next())
                    }
                }
            }
        }

    // endregion
}
