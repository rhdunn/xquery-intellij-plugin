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
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.parameterInfo.*
import com.intellij.psi.NavigatablePsiElement
import uk.co.reecedunn.intellij.plugin.core.sequences.ancestors
import uk.co.reecedunn.intellij.plugin.xpath.ast.isArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionDeclaration
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpath.lexer.XPathTokenType
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.functionReference
import uk.co.reecedunn.intellij.plugin.xpm.staticallyKnownFunctions

class XPathParameterInfoHandler : ParameterInfoHandler<XPathArgumentList, XpmFunctionDeclaration> {
    override fun couldShowInLookup(): Boolean = true

    override fun getParametersForLookup(item: LookupElement?, context: ParameterInfoContext?): Array<Any>? = null

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): XPathArgumentList? {
        val e = context.file.findElementAt(context.offset)
        val args = e?.ancestors()?.filterIsInstance<XPathArgumentList>()?.firstOrNull()
        context.itemsToShow = functionCandidates(args).filter {
            if (it.arity.from == 0 && it.arity.to == 0)
                args?.parent?.isArrowFunctionCall == false
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
        context.setCurrentParameter(if (parameterOwner.parent.isArrowFunctionCall) index + 1 else index)
    }

    override fun updateUI(p: XpmFunctionDeclaration?, context: ParameterInfoUIContext) {
        if (p == null) return

        val params = p.parameters.map { (it as NavigatablePsiElement).presentation?.presentableText!! }
        if (params.isNotEmpty()) {
            val isVariadic = p.isVariadic
            var start = -1
            var end = -1
            if (context.currentParameterIndex < p.arity.to) {
                params.withIndex().forEach { (i, param) ->
                    if (i <= context.currentParameterIndex) {
                        start = if (i == 0) 0 else end + PARAM_SEPARATOR.length
                        end = start + param.length
                        if (i == params.size - 1 && isVariadic) {
                            end += VARIADIC_MARKER.length
                        }
                    }
                }
            }

            context.setupUIComponentPresentation(
                if (isVariadic)
                    "${params.joinToString(PARAM_SEPARATOR)}$VARIADIC_MARKER"
                else
                    params.joinToString(", "),
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
        return functionName?.staticallyKnownFunctions()?.sortedBy { it.arity.from }?.distinct() ?: emptySequence()
    }

    companion object {
        private const val PARAM_SEPARATOR = ", "
        private const val VARIADIC_MARKER = " ..."
    }
}
