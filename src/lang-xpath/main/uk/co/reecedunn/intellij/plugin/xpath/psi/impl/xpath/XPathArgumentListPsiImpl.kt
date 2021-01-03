/*
 * Copyright (C) 2016-2017, 2019-2020 Reece H. Dunn
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
import uk.co.reecedunn.intellij.plugin.core.sequences.siblings
import uk.co.reecedunn.intellij.plugin.intellij.lang.*
import uk.co.reecedunn.intellij.plugin.xpath.ast.isArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionReference
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentPlaceholder
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathPostfixExpr
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionParamBinding
import uk.co.reecedunn.intellij.plugin.xpath.parser.XPathElementType
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmDynamicFunctionReference
import uk.co.reecedunn.intellij.plugin.xpm.optree.XpmExpression
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownFunctions

private val XQUERY10: List<Version> = listOf()
private val XQUERY30: List<Version> = listOf(XQuerySpec.REC_3_0_20140408, MarkLogic.VERSION_6_0)

open class XPathArgumentListPsiImpl(node: ASTNode) : ASTWrapperPsiElement(node), XPathArgumentList, VersionConformance {
    // region VersionConformance

    override val requiresConformance: List<Version>
        get() {
            if (parent !is XPathPostfixExpr) {
                return XQUERY10
            }
            return XQUERY30
        }

    override val conformanceElement: PsiElement
        get() = firstChild

    // endregion
    // region XPathArgumentList

    private val arguments: Sequence<PsiElement>
        get() = children().filter { it is XpmExpression || it is XPathArgumentPlaceholder }

    override val functionReference: XpmFunctionReference?
        get() = when (val parent = parent) {
            is XpmFunctionReference -> parent
            is XpmDynamicFunctionReference -> parent.functionReference
            else -> null
        }

    override val arity: Int
        get() = arguments.count()

    override val isPartialFunctionApplication: Boolean
        get() = findChildByType<PsiElement>(XPathElementType.ARGUMENT_PLACEHOLDER) != null

    override val bindings: List<XpmFunctionParamBinding>
        get() {
            val ref = functionReference
            val target = ref?.functionName?.staticallyKnownFunctions()?.firstOrNull { f ->
                f.arity.isWithin(ref.arity)
            } ?: return listOf()

            val args = arguments.iterator()
            val params = target.params
            return params.mapIndexed { index, param ->
                when {
                    index == 0 && parent.isArrowFunctionCall -> {
                        // First argument bound to an ArrowExpr evaluation result.
                        val context = parent.siblings().reversed().filter {
                            it.isArrowFunctionCall
                        }.firstOrNull() ?: parent.parent.firstChild
                        XpmFunctionParamBinding(param, context)
                    }
                    index == params.size - 1 -> {
                        // Last argument, maybe variadic.
                        XpmFunctionParamBinding(param, args.asSequence().toList())
                    }
                    else -> {
                        // Other argument bound to the relevant parameter.
                        XpmFunctionParamBinding(param, args.next())
                    }
                }
            }
        }

    // endregion
}
