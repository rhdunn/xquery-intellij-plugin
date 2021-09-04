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

import com.intellij.codeInsight.hints.HintInfo
import com.intellij.codeInsight.hints.InlayInfo
import com.intellij.codeInsight.hints.InlayParameterHintsProvider
import com.intellij.psi.PsiElement
import uk.co.reecedunn.intellij.plugin.xdm.functions.op.qname_presentation
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmAttributeNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XdmElementNode
import uk.co.reecedunn.intellij.plugin.xdm.types.XsNCNameValue
import uk.co.reecedunn.intellij.plugin.xdm.types.XsQNameValue
import uk.co.reecedunn.intellij.plugin.xpath.ast.parenthesizedExprTextOffset
import uk.co.reecedunn.intellij.plugin.xpath.ast.xpath.*
import uk.co.reecedunn.intellij.plugin.xpm.context.expand
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmConcatenatingExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.expression.XpmLookupExpression
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmArrowFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.XpmFunctionCall
import uk.co.reecedunn.intellij.plugin.xpm.optree.function.resolve
import uk.co.reecedunn.intellij.plugin.xpm.optree.variable.XpmVariableReference

@Suppress("UnstableApiUsage", "UnstableTypeUsedInSignature")
class XPathInlayParameterHintsProvider : InlayParameterHintsProvider {
    override fun getParameterHints(element: PsiElement): List<InlayInfo> {
        val (_, bindings) = (element as? XpmFunctionCall)?.resolve ?: return emptyList()
        return bindings.mapIndexedNotNull { index, binding ->
            val expr = when (val expr = binding.variableExpression) {
                is PsiElement -> expr
                is XpmConcatenatingExpression -> expr.expressions.firstOrNull() as? PsiElement
                else -> null
            }
            when {
                binding.variableName == null -> null // Parameter with incomplete variable name.
                expr == null -> null // Empty variadic parameter.
                expr.parent is XPathKeywordArgument -> null // keyword argument
                index == 0 && element is XpmArrowFunctionCall -> null // Arrow function call context argument.
                getName(expr)?.data == binding.variableName?.localName?.data -> null
                else -> qname_presentation(binding.variableName!!)?.let { name ->
                    InlayInfo(name, expr.let { it.parenthesizedExprTextOffset ?: it.textOffset }, false)
                }
            }
        }
    }

    override fun getDefaultBlackList(): Set<String> = DEFAULT_BLACKLIST

    override fun getHintInfo(element: PsiElement): HintInfo.MethodInfo? {
        val (decl, bindings) = (element as? XpmFunctionCall)?.resolve ?: return null
        val functionName = decl.functionName?.expand()?.firstOrNull()
        val eqname = functionName?.let { qname_presentation(it, true) } ?: return null
        val params = bindings.mapNotNull { it.variableName?.localName?.data }
        return XPathMethodInfo(eqname, functionName.localName!!.data, params)
    }

    companion object {
        private val DEFAULT_BLACKLIST = setOf(
            "(arg)" // e.g. fn:string#1, xs:QName#1
        )

        private fun getName(element: PsiElement): XsNCNameValue? = when (element) {
            is XPathRelativePathExpr -> when (val step = element.lastChild) {
                is XPathNameTest -> (step.firstChild as? XsQNameValue)?.localName
                is XPathAbbrevForwardStep, is XPathForwardStep, is XPathReverseStep -> when (step.lastChild) {
                    is XPathNameTest -> (step.lastChild.firstChild as? XsQNameValue)?.localName
                    else -> null
                }
                else -> null
            }
            is XPathSimpleMapExpr -> getName(element.lastChild)
            is XdmAttributeNode -> element.nodeName?.localName
            is XdmElementNode -> element.nodeName?.localName
            is XpmVariableReference -> element.variableName?.localName
            is XpmLookupExpression -> element.keyExpression as? XsNCNameValue
            else -> null
        }
    }
}
