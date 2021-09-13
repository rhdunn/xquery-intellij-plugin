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
package uk.co.reecedunn.intellij.plugin.xpath.lang.editor.parameters

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.lang.parameterInfo.*
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.XPathArgumentList
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.optree.annotation.XpmVariadic
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmConcatenatingExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.*
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownFunctions

class XPathParameterInfoHandler : ParameterInfoHandler<XPathArgumentList, XpmFunctionDeclaration> {
    override fun findElementForParameterInfo(context: CreateParameterInfoContext): XPathArgumentList? {
        val e = context.file.findElementAt(context.offset)
        val args = e?.ancestors()?.filterIsInstance<XPathArgumentList>()?.firstOrNull()
        context.itemsToShow = functionCandidates(args).filter {
            if (it.declaredArity == 0)
                args?.parent !is XpmArrowFunctionCall
            else
                true
        }.toList().toTypedArray()
        return args
    }

    override fun showParameterInfo(element: XPathArgumentList, context: CreateParameterInfoContext) {
        context.showHint(element, element.textOffset, this)
    }

    override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): XPathArgumentList? {
        val e = context.file.findElementAt(context.offset)
        return e?.ancestors()?.filterIsInstance<XPathArgumentList>()?.firstOrNull()
    }

    override fun updateParameterInfo(parameterOwner: XPathArgumentList, context: UpdateParameterInfoContext) {
        val index =
            ParameterInfoUtils.getCurrentParameterIndex(parameterOwner.node, context.offset, XPathTokenType.COMMA)
        context.setCurrentParameter(if (parameterOwner.parent is XpmArrowFunctionCall) index + 1 else index)
    }

    override fun updateUI(p: XpmFunctionDeclaration?, context: ParameterInfoUIContext) {
        if (p == null) return

        val parameters = p.parameters
        if (parameters.isNotEmpty()) {
            val functionCall = context.parameterOwner.parent as XpmFunctionCall
            val argument = functionCall.argumentAt(context.currentParameterIndex)

            val text = StringBuffer()
            val variadicType = p.variadicType
            var start = -1
            var end = -1
            functionCall.bindTo(parameters, variadicType).withIndex().forEach { (i, binding) ->
                val match = when (val expr = binding.variableExpression) {
                    is XpmConcatenatingExpression -> expr === argument || expr.expressions.contains(argument)
                    else -> expr === argument
                }

                if (match) {
                    start = text.length
                }
                text.append(binding.toString())
                if (i == parameters.size - 1 && variadicType === XpmVariadic.Ellipsis) {
                    text.append(ELLIPSIS_VARIADIC_MARKER)
                }
                if (match) {
                    end = text.length
                }

                if (i < parameters.size - 1) {
                    text.append(PARAM_SEPARATOR)
                }
            }

            context.setupUIComponentPresentation(
                text.toString(),
                start, end, false, false, false, context.defaultParameterColor
            )
        } else {
            context.setupUIComponentPresentation(
                CodeInsightBundle.message("parameter.info.no.parameters"),
                -1, -1, false, false, false, context.defaultParameterColor
            )
        }
    }

    private fun functionCandidates(args: XPathArgumentList?): Sequence<XpmFunctionDeclaration> {
        val functionName = when (val parent = args?.parent) {
            is XpmFunctionCall -> parent.functionReference?.functionName
            else -> null
        }
        return functionName?.staticallyKnownFunctions()?.sortedBy { it.declaredArity }?.distinct() ?: emptySequence()
    }

    companion object {
        private const val PARAM_SEPARATOR = ", "
        private const val ELLIPSIS_VARIADIC_MARKER = " ..."
    }
}
